import java.util.*;

class FF implements IFailureDetector{

Process p;
LinkedList<Integer> suspects;
Timer t;

double d,
		 previous;

List<Double> timeStampList;

static final int Delta = 100;

class PeriodicTask extends TimerTask{
	public void run(){
		p.broadcast("heartbeat", "null");
	}
}

public FF(Process p){
	this.p = p;
	t = new Timer();
	suspects = new LinkedList<Integer>();

	d=Utils.DELAY;
	int size = p.getNo();

	timeStampList = Arrays.asList(new Double[size]);
	System.out.println("Created the Detector");
}

public void begin(){

	t.schedule(new PeriodicTask(), 0, Delta);
}

public void receive(Message m ){
	int source;
	double now;
	Utils.out(p.pid, m.toString());
	source = m.getSource()-1;
	now = System.currentTimeMillis();
	timeStampList.set(source,now);

//	Utils.out(timeStampList.toString());
	previous=now;
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
