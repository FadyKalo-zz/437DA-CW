import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class FailureDetector is the superclass of the FailureDetectors implemented in this assignment contains methods and fields used by all FailureDetectors
 * 
 * @author isabel
 * 
 */
class FailureDetector implements IFailureDetector {

	Process p;
	int sizeOfNetwork;
	LinkedList<Integer> suspects;
	List<Double> timestamps;
	Timer t;
	int leader = -1;

	static final int Delta = 1000;

	/**
	 * timertask to send off heartbeats periodically
	 * 
	 * @author isabel
	 * 
	 */
	class PeriodicTask extends TimerTask {

		public void run() {

			p.broadcast("heartbeat", "null");
		}
	}

	/**
	 * Constructor for FailureDetector
	 * 
	 * @param expects a process p to be passed
	 */
	public FailureDetector(Process p) {

		this.p = p;
		this.sizeOfNetwork = p.getNo();
		t = new Timer();
		suspects = new LinkedList<Integer>();
		timestamps = new ArrayList<Double>();
		initTimeStamps(sizeOfNetwork);
	}

	/**
	 * initialises the timestamps for the failuredetector
	 * 
	 * @param size
	 */
	private void initTimeStamps(int size) {

		for (int i = 0; i < size; i++) {
			timestamps.add(i, -1.0);
		}
	}

	@Override
	public void begin() {

	}

	/**
	 * checks if a process is suspected
	 * 
	 * @param Processes' pid
	 * @return boolean
	 */
	public boolean isSuspect(Integer pid) {

		return suspects.contains(pid);
	}

	/**
	 * returns the leader
	 * 
	 * @return leader pid
	 */
	public int getLeader() {

		return leader;
	}

	/**
	 * adds a suspect to the list of suspected processes
	 * 
	 * @param pid of suspected process
	 */
	public void isSuspected(Integer process) {

		suspects.add(process);
	}

	/**
	 * removes a suspected process from the list of suspects
	 * 
	 * @param pid
	 */
	public void removeSuspect(Integer pid) {

		suspects.remove(pid);
	}

	/**
	 * prints suspected processes
	 */
	public void printSuspected() {

		if (suspects.size() != 0) {
			System.out.print("Suspect List: ");
			for (int i = 0; i < suspects.size(); i++) {
				System.out.print((suspects.get(i) + 1) + " ");
			}
			System.out.println("");
		}

	}

	@Override
	public void receive(Message m) {

	}

	/**
	 * initialises an array of integer with -1 values
	 * 
	 * @param array
	 * @return the array initialised
	 */
	protected int[] initialiseIntArray(int[] array) {

		for (int i = 0; i < array.length; i++) {
			array[i] = -1;
		}
		return array;
	}

	/**
	 * initialises a messageContainer array
	 * 
	 * @param msgContainer
	 * @param size
	 * @param pid
	 * @return initialised array
	 */
	protected MessageContainer[] initialiseMessageContainer(MessageContainer[] msgContainer, int size, int pid) {

		for (int i = 0; i < msgContainer.length; i++) {
			msgContainer[i] = new MessageContainer(size, i, pid);
		}
		return msgContainer;
	}

}
