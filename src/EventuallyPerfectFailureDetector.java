import java.util.ArrayList;
import java.util.List;

public class EventuallyPerfectFailureDetector extends FailureDetector implements IFailureDetector{

	List<Integer> delayForProcess = new ArrayList<Integer>();
	
	
	public EventuallyPerfectFailureDetector(Process p) {
		super(p);
		
	}
	
	
 class CheckIfProcessFailedEventually implements Runnable {

			@Override
			public void run() {
				
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
						if(System.currentTimeMillis()- ts[i] > delayForProcess.get(i) && !isSuspect(i)){
							// add to suspect list
							isSuspected(i);
							calculateLeader();
							
						}
					}
				}
				
			}
			
 }
	 

 public void begin(){

	 	initiateTimeouts();
		t.schedule(new PeriodicTask(), 0, Delta);
		(new Thread(new CheckIfProcessFailedEventually())).start();
 }
 
 
 public void initiateTimeouts(){
	 
	 // every process hasinitiateTimeouts the same delay initially
	 for(int i = 0; i< sizeOfNetwork;i++){
		 delayForProcess.add(i,  Utils.DELAY);		 
	 }
 }
 
 public void receive(Message m ){

	int source = m.getSource()-1;
	double now = System.currentTimeMillis();
	timestamps.set(source,now);

	if(isSuspect(source)){
		// calculate new delay
		int newDelay = eventuallyPerfectFailure(m.getPayload());
		delayForProcess.set(source,newDelay);
		// remove suspect from list
		removeSuspect(source);
		
	}
 }
	 
	 
 public void calculateLeader(){
	 
	 for(int i = 1; i <= sizeOfNetwork; i++){
		 if(!isSuspect(i)){
			 if(i>leader){
				 leader = i;
			 }
		 }
	 } 
 }

 public int eventuallyPerfectFailure(String payload){
	
	return (int) (System.currentTimeMillis() - Long.parseLong(payload));
	

 }

}
