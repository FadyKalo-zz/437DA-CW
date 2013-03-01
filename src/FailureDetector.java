import java.utils.*;

class FailureDetector implements IFailureDetector{

 Process P;
 LinkedList<Integer> suspects;
 Timer t;

 static final int Delat = 100;

 class PeriodicTask extends TimerTask{
	public void run(){
		p.broadcast("heartbeat", "null");
	}
 }

 public FailureDetector(Process p){

	this.p = p;
	t = new Timer();
	suspects = new LinkedList<Integer>();
 }

 public void begin(){

	t.schedule(new PeriodicTask(), 0, Delta);
 }

 public void receive(Message m ){

	Utils.out(p.pid, m.toString());
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
