import java.util.*;

class FailureDetector implements IFailureDetector{

 Process p;
 int sizeOfNetwork;
 LinkedList<Integer> suspects;
 List<Double> timestamps;
 Timer t;
 int leader = -1 ;

 static final int Delta = 10000;
 
 
 
 class PeriodicTask extends TimerTask{
	public void run(){
		System.out.println("Sending heartbeat "+p.pid);
		p.broadcast("heartbeat", "null");
	}
 }
 
 
 public FailureDetector(Process p){

	this.p = p;
	this.sizeOfNetwork = p.getNo();
	t = new Timer();
	suspects = new LinkedList<Integer>();
	timestamps = new ArrayList<Double>();
	initTimeStamps(sizeOfNetwork);
 }
 
 
 private void initTimeStamps(int size){
	 
	 for(int i = 0; i< size; i++){
		 timestamps.add(i, -1.0);
	 }
 }
 @Override
 public void begin() {
	
 }
 
 public boolean isSuspect(Integer pid){

	return suspects.contains(pid);
 }


 public int getLeader(){

	return leader;
 }

 public void isSuspected(Integer process){

 	suspects.add(process);
 }
 
 public void removeSuspect(Integer pid){
	 
	 suspects.remove(pid);
 }
 
 public void printSuspected(){
	 
	 System.out.print("Suspect List: ");
	 for(int i = 0; i<suspects.size();i++){
		 System.out.print(suspects.get(i)+" ");
	 }
	 System.out.println("");
	 
	 
 }


 @Override
 public void receive(Message m) {
	
 }




}
