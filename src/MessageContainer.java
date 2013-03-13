/**
 * class MessageContainer stores the consens messages received by a process from the other processes in the network each messageContainer class is specific to one round, i.e. for each round in the ConsensusAlgorithm there exists a MessageContainer for all incoming messages
 * 
 * @author isabel
 * 
 */
public class MessageContainer {

	private int size;
	private int round;
	private int pid;
	ConsensMessage[] msgs;

	/**
	 * constructor
	 * 
	 * @param size of the messageContainer
	 * @param round for messageContainer
	 * @param pid own pid
	 */
	public MessageContainer(int size, int round, int pid) {

		super();
		this.size = size;
		this.round = round;
		this.pid = pid;
		msgs = new ConsensMessage[size];
		initialise();
	}

	/**
	 * adds a message to the messageContainer
	 * 
	 * @param message
	 */
	public void addMessage(ConsensMessage m) {

		msgs[m.pid - 1] = m;

	}

	/**
	 * initialiases the array of consensMessages
	 */
	private void initialise() {

		for (int i = 0; i < msgs.length; i++) {
			msgs[i] = null;
		}
	}

	/**
	 * get a particular consensMessage of processId
	 * 
	 * @param processId
	 * @return the ConsensMessage
	 */
	public ConsensMessage getConsensMessage(int processId) {

		return msgs[processId - 1];
	}

	/**
	 * check the process' delta vectors for a particular value and update own one.
	 * 
	 * @param the value to be checked
	 * @return -1 if no process did upate that value or the updated value
	 */
	public int findEstimate(int k) {

		for (int i = 0; i < msgs.length; i++) {
			if (msgs[i] != null) {
				if (msgs[i].getDeltaP()[k] != -1) {
					return msgs[i].getDeltaP()[k];
				}
			}
		}
		return -1;
	}

	/**
	 * method checks whether for a particular process any process did not receive a message i.e. if process 2 crashes in the last round and process 1 still receives process 2's message but process 3 didnt - no consens will be established for process 2
	 * 
	 * @param the process to check
	 * @return if one process suspects process process k to have failed
	 */
	public boolean findZeroEstimate(int k) {

		for (int i = 0; i < msgs.length; i++) {
			if (i != (pid - 1) && msgs[i] != null) {
				if (msgs[i].getDeltaP()[k] == -1) {
					return true;
				}
			}
		}
		return false;
	}

	public void printMessageContainer() {

		System.out.println("MessageContainer for round " + round);
		for (int i = 0; i < msgs.length; i++) {
			if (msgs[i] == null) {
			}
			else if (i != (pid - 1)) {
				System.out.println("Pid: " + msgs[i].pid + " Round: " + msgs[i].round);
				msgs[i].printMessage();
			}
		}
	}
}
