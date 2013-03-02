import java.util.*;

class FailureDetector implements IFailureDetector{

 Process p;
 LinkedList<Integer> suspects;
 List<Double> timestamps;
 Timer t;

 static final int Delta = 100;
 
 
 
 class PeriodicTask extends TimerTask{
	public void run(){
		p.broadcast("heartbeat", "null");
	}
 }

 class CheckIfProcessFailed implements Runnable{

	@Override
	public void run() {
		
		// adjust to perfectFailure or EventuallyPerfectFailure
		int timeout = Delta + perfectFailure();
		
		while(true){
			
			try {
				Thread.sleep(Delta);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// go through the list of timeStamps
			Double[] ts = timestamps.toArray(new Double[timestamps.size()]);
			for(int i = 0; i< ts.length; i++){
				// if last timestamp exceeds given delta/ delay add to suspected list
				if(System.currentTimeMillis()- ts[i] > timeout && !isSuspect(i)){
					// add to suspect list
					suspects.add(i);
					
				}
			}
		}
		
	}
	 
 }
 
 
 public FailureDetector(Process p){

	this.p = p;
	t = new Timer();
	suspects = new LinkedList<Integer>();
	timestamps = Arrays.asList(new Double[p.getNo()]);
 }

 public void begin(){

	t.schedule(new PeriodicTask(), 0, Delta);
	(new Thread(new CheckIfProcessFailed())).start();
 }

 public void receive(Message m ){

	//Utils.out(p.pid, m.toString());
	
	int source;
	double now;
	Utils.out(p.pid, m.toString());
	source = m.getSource()-1;
	now = System.currentTimeMillis();
	timestamps.set(source,now);
	Utils.out(p.pid, m.toString());

 }


 
public int perfectFailure(){
	
	return Utils.DELAY;
	
}

public int eventuallyPerfectFailure(){
	
	return -1;
	

}


 public boolean isSuspect(Integer pid){

	return suspects.contains(pid);
 }


 public int getLeader(){

	return -1;
 }

 public void isSuspected(Integer process){

 	return;
 }

}
