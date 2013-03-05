import com.sun.corba.se.impl.logging.ORBUtilSystemException;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class EventuallyPerfectFailureDetector extends FailureDetector implements IFailureDetector {

List<Double> currentDelay = new ArrayList<Double>(sizeOfNetwork);
List<Double> previousDelay = new ArrayList<Double>(sizeOfNetwork);
List<Double> previousTs = new ArrayList<Double>(sizeOfNetwork);
List<Double> threDelay = new ArrayList<Double>(sizeOfNetwork);
List<Double> currentT = new ArrayList<Double>(sizeOfNetwork);
PFDProcess pfd;


public EventuallyPerfectFailureDetector(Process p) {
	super(p);
}

class PFDProcess extends TimerTask {
	//class PFDProcess implements Runnable {
//	 List<Double> timeout = new ArrayList<Double>(sizeOfNetwork);
	@Override
	public void run() {
//		while(true){
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		System.out.println("----------------------------------------------");
		printSuspected();

		// go through the list of timeStamps
		Double[] ts = timestamps.toArray(new Double[timestamps.size()]);

		for (int i = 0; i < timestamps.size(); i++) {
			//not checking its self
			if (timestamps.get(i) != -1) {
				System.out.println("[" + i + "]: " +
						  " current:" + currentDelay.get(i) + "\tprevious:" + previousDelay.get(i) +
						  "\tcurrentT:" + currentT.get(i) + "\tThre:" + threDelay.get(i));
				//is there a new update from the process?
				if (previousTs.get(i) == timestamps.get(i)) {
					System.out.println("Something stopped or Delayed: " + "[" + i + "]");
					if (!isSuspect(i)) {
						System.out.println("Something stopped or Delayed: " + "[" + i + "]");
						System.out.println("ADD: " + "[" + i + "]");
						isSuspected(i);
						printSuspected();
					}

				}
				//there is a new update
				else {
					//if it's late and not already suspected
//					if ((currentDelay.get(i) + Delta) > threDelay.get(i) && !isSuspect(i)) {
					if (currentT.get(i) > threDelay.get(i) && !isSuspect(i)) {
						System.out.println("ADD: " + "[" + i + "]");
						isSuspected(i);
						printSuspected();
					}
					//if it's already suspected but not late anymore
//					if ((currentDelay.get(i) + Delta) <= threDelay.get(i) && isSuspect(i)) {
					if (currentT.get(i) <= threDelay.get(i) && isSuspect(i)) {
						System.out.println("RMV: " + "[" + i + "]");
						removeSuspect(i);
						printSuspected();
					}

				}

//					System.out.println("["+i+"]: "+"Ts: "+ts[i]+"\tGAP: "+(System.currentTimeMillis() - ts[i])+
//							  "\tDel:"+ currentDelay.get(i)+ "\tThre:"+ threDelay.get(i)+ "\tThreLast:"+ currentT.get(i));

//					if((currentDelay.get(i)+Delta) > threDelay.get(i) && !isSuspect(i)){
//						System.out.println("ADD: "+"["+i+"]");
//						isSuspected(i);
//						printSuspected();
//					}
//					if((currentDelay.get(i)+Delta) <= threDelay.get(i) && isSuspect(i)){
//						System.out.println("RMV: "+"["+i+"]");
//						removeSuspect(i);
//						printSuspected();
//					}
//					if(System.currentTimeMillis()- ts[i] > currentDelay.get(i) && !isSuspect(i)){
//						// add to suspect list
//						isSuspected(i);
//						calculateLeader();
//					}

			}
			previousTs.set(i, timestamps.get(i));
		}
//			System.out.println("Suspects AFTER:");
		printSuspected();
		System.out.println("----------------------------------------------");
//		}
	}

	public void alert(int source, double ts) {
		Utils.out("notification received");
	}
}

public void begin() {

	initiateTimeouts();
	t.schedule(new PeriodicTask(), 0, Delta);
	tim.schedule(new PFDProcess(), 0, 500);
	pfd = new PFDProcess();
//	 	(new Thread(pfd)).start();
}


public void initiateTimeouts() {

	// every process has initiate Timeouts the same delay initially
	for (int i = 0; i < sizeOfNetwork; i++) {
		currentDelay.add(i, (double) Utils.DELAY);
		threDelay.add(i, (double) (Utils.DELAY + Delta));
		currentT.add(i, (double) (Utils.DELAY * 2));
		previousDelay.add(i, (double) Utils.DELAY);
		previousTs.add(i, -1.0);
	}

}

public synchronized void receive(Message m) {
	Utils.out(p.pid, m.toString());

	int source = m.getSource() - 1;
	double now = System.currentTimeMillis();
	double nowDelay = eventuallyPerfectDelay(m.getPayload());
	double T = Delta + nowDelay;
//	pfd.alert(source, now);
//	 previousTs.set(source,timestamps.get(source));

	timestamps.set(source, now);
	previousDelay.set(source, currentDelay.get(source));
	currentDelay.set(source, nowDelay);
	currentT.set(source, T);
	threDelay.set(source, calculateT(T, source));
	System.out.println(nowDelay);

//	 System.out.println("" + timestamps.get(source) + "\t" + currentDelay.get(source)+ "\t" + threDelay.get(source));

//	 if(isSuspect(source)){
//		 System.out.println("I SUSPECTED SOMEONE: "+"["+source+"]");
//
//		// calculate new delay
//		// remove suspect from list
//		removeSuspect(source);
//	}

}

public double calculateT(double t, int source) {
	double newT = (threDelay.get(source) + t) / 2;
	return newT;
}

public void calculateLeader() {

	for (int i = 1; i <= sizeOfNetwork; i++) {
		if (!isSuspect(i)) {
			if (i > leader) {
				leader = i;
			}
		}
	}
}

public double eventuallyPerfectDelay(String payload) {
	return (System.currentTimeMillis() - Long.parseLong(payload));
}

}
