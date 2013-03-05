import java.util.*;

class FailureDetector implements IFailureDetector{

static final int Delta = 500;

Timer t;
Timer tim;
Process p;
LinkedList<Integer> suspects;

int sizeOfNetwork;
List<Double> timestamps;
int leader = -1 ;
String payload="";

 class PeriodicTask extends TimerTask{
	public void run(){
		System.out.println("Sending heartbeat");
		setPayload();
//		System.out.println("payload: "+payload);
		p.broadcast("heartbeat", payload);
	}
 }

 public FailureDetector(Process p){

	this.p = p;
	this.sizeOfNetwork = p.getNo();
	t = new Timer();
	tim = new Timer();
	suspects = new LinkedList<Integer>();
	timestamps = new ArrayList<Double>();
	initTimeStamps(sizeOfNetwork);
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
	int index=suspects.indexOf(pid);
	 suspects.remove(index);
//	 printSuspected();
 }

 public void printSuspected(){

	 System.out.print("Suspect List: [");
	 for(int i = 0; i<suspects.size();i++){
		 if(i==suspects.size()-1)
			 System.out.print(suspects.get(i));
		 else {
			 System.out.print(suspects.get(i)+",");
		 }
	 }
	 System.out.println("]");


 }

 @Override
 public void receive(Message m) {

 }


 public void setPayload(){
	payload=String.format("%d",System.currentTimeMillis());
 }


private void initTimeStamps(int size){

	for(int i = 0; i< size; i++){
		timestamps.add(i, -1.0);
	}
}

}
