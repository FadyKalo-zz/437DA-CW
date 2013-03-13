/**
 * Implementation of the Rotating Coordinators algorithm that solves consensus using a strong failure detector the number of faulty processes is bound by F < N The class runs two threads concurrently - the first one checks whether a process has failed and the second one implements the rotating
 * coordinator algorithm We adapted this algorithm as described by T. D. Chandra and S. Toueg, Unreliable failure detectors for reliable distributed systems, Journal of the ACM, 43(2), 1996, 225–267
 * 
 * @author Isabel and Fady
 * 
 */

public class StrongFailureDetector extends FailureDetector {

	int[] psEstimate; // The estimated values by this process
	MessageContainer[] messageContainer; // Message container array to store consens messages for every round

	/**
	 * Constructor for the StrongFailureDetector initialises Process and used arrays
	 * 
	 * @param Process P
	 * @param xValue - the consens value proposed by the process
	 */
	public StrongFailureDetector(Process p, int xValue) {

		super(p);
		psEstimate = initialiseIntArray(new int[sizeOfNetwork]);
		psEstimate[p.pid - 1] = xValue; // set proposed consens value in vector
		messageContainer = initialiseMessageContainer(new MessageContainer[sizeOfNetwork], sizeOfNetwork, p.pid);

	}

	class CheckIfProcessFailed implements Runnable {

		@Override
		public void run() {

			// adjust to perfectFailure or EventuallyPerfectFailure
			int timeout = Delta + perfectFailure();

			while (true) {

				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}

				// **Test
				// printSuspected();

				// go through the list of timeStamps
				if (timestamps.size() != 0) {
					Double[] ts = timestamps.toArray(new Double[timestamps.size()]);
					for (int i = 0; i < ts.length; i++) {
						// if last timestamp exceeds given delta/ delay add to suspected list
						if (ts[i] != -1) {
							if (System.currentTimeMillis() - ts[i] > timeout && !isSuspect(i)) {
								// add to suspect list
								System.out.println("Add " + (i + 1) + " to suspected");
								isSuspected(i);

							}
						}
					}
				}
			}

		}

	}

	/**
	 * Thread that runs the Consens algorithm concurrently to the failure detector The run method performs n-1 rounds, receiving a vector in every round from every process to update its own vector of proposed values before awaiting incoming messages from every process it broadcasts its own vector to
	 * the processes in the final round each process sends out its final estimate of proposed values the consens value is the first value in the vector that is NOT '-1' (-1 represents an empty value, i.e. the process has crashed)
	 * 
	 * @author isabel
	 * 
	 */
	class Consensus implements Runnable {

		// vector that is sent to
		int[] delta = initialiseIntArray(new int[sizeOfNetwork]);

		@Override
		public void run() {

			delta = psEstimate;

			// sleep a little before starting the consensus; gives some time to detect failed processes
			try {
				Thread.sleep(10000);
			}
			catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			System.out.println("\n*******\n1 - Start Consensus\n*******\n");

			// perform size-of-network rounds - in the last round the estimate of proposed value is broadcasted to all other processes
			for (int r = 0; r < sizeOfNetwork; r++) {

				System.out.println("2 - Round: " + r);
				System.out.print("Current Delta: ");
				printIntArray(delta);

				// broadcast delta to all processes in G
				p.broadcast("consens", payloadArrayToString(delta, r));

				// wait for each process to send a message in round r
				for (int j = 1; j < sizeOfNetwork + 1; j++) {
					if (j == p.pid) { // ignore if j is own process id
						continue;
					}
					while (!receivedConsensusValues(r, j) && !isSuspect(j - 1)) { // while not receivedConsensusValue from process j in round r and j is not a suspect - WAIT
						// sleep for a couple of seconds then check again whether message arrived or is suspect
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}

					}

				}
				System.out.println("4 - Received all Messages in round:  " + r + "\n");

				// update own estimate of proposed values and delta
				if (r != sizeOfNetwork - 1) {
					for (int k = 0; k < psEstimate.length; k++) {
						if (psEstimate[k] == -1) {
							int newEstimate = messageContainer[r].findEstimate(k);
							if (newEstimate != -1) {
								psEstimate[k] = newEstimate;
								delta[k] = newEstimate;
							}
						}
					}

				}
				else { // in the last round find the consensus value
					System.out.println("5 - FINAL: find consens for round " + r);
					System.out.print("PSEstimate: ");
					printIntArray(psEstimate);

					// first update own estimate vector - if any process did not receive a value from a process no consens can be made on that value
					for (int k = 0; k < psEstimate.length; k++) {
						if (messageContainer[r].findZeroEstimate(k)) {
							// check if any other process has a null value
							psEstimate[k] = -1;
						}
					}
					System.out.print("2 -PSEstimate: ");
					printIntArray(psEstimate);
					messageContainer[r].printMessageContainer();

					// if all active processes sent their final estimate decide on the consensus value (the first one that is not -1)
					for (int i = 0; i < psEstimate.length; i++) {

						if (psEstimate[i] != -1) {
							System.out.println("CONSENSUS VALUE = " + psEstimate[i]);
							System.out.println("\n*******\n1 - END Consensus\n*******\n");
							break;
						}
					}

				}
			}

		}
	}

	/**
	 * synchronised method that checks whether the messageContainer received a consensus message from a certain process in a specific round synchronised method ensuring that the messageContainer is not accessed concurrently
	 * 
	 * @param round
	 * @param processId
	 * @return boolean true when message did arrive; false if message didnt arrive
	 */
	public synchronized boolean receivedConsensusValues(int round, int processId) {

		if (messageContainer[round].getConsensMessage(processId) != null) {
			return true;
		}
		return false;
	}

	/**
	 * adds a consensus vector from a process with processId and for round r to the messageContainer synchronised method ensuring that the messageContainer is not accessed concurrently
	 * 
	 * @param round
	 * @param processId
	 * @param vector from process with id processId
	 */
	public synchronized void addConsensusVector(int round, int processId, int[] vector) {

		System.out.println("3c - AddConsensusVector to Message-Container for round" + round);
		ConsensMessage consens = new ConsensMessage(round, processId, vector);
		messageContainer[round].addMessage(consens);
	}

	/**
	 * start the threads and schedule heartbeats
	 */
	public void begin() {

		t.schedule(new PeriodicTask(), 0, Delta);
		(new Thread(new CheckIfProcessFailed())).start();
		(new Thread(new Consensus())).start();

	}

	/**
	 * receive message m check whether message is heartbeat or consens message and perform actions accordingly
	 * 
	 * @param Message m received by the process
	 */
	public void receive(Message m) {

		int source = m.getSource();

		if (m.getType().equals("heartbeat")) {
			// System.out.println("\t\t\t\t**H from " + source + " **");
			double now = System.currentTimeMillis();
			timestamps.set(source - 1, now);
		}
		else if (m.getType().equals("consens")) { // else its consensus
			System.out.println("R - Received a message of type: <" + m.getType() + "> from: <" + source + "> payload: " + m.getPayload());
			String payload = m.getPayload();
			int round = Character.getNumericValue(m.getPayload().charAt(0));
			ConsensMessage message = new ConsensMessage(round, source, payloadStringToArray(payload.substring(1)));
			messageContainer[round].addMessage(message);
		}

	}

	/**
	 * maps a string (payload) to the delta vector from a process a delta vector is of form round_value,value,... for example 0_1,2,3 means in a network of 3 processes a specific process's estimate in round 0 is 1,2,3
	 * 
	 * @param payload
	 * @return the vector with estimated values as an array of integer values
	 */
	private int[] payloadStringToArray(String payload) {

		int[] deltaValues = new int[sizeOfNetwork];
		int soNcounter = 0;
		int i = 0;
		while (i < payload.length()) {
			if (payload.charAt(i) == '_' || payload.charAt(i) == ',' || payload.charAt(i) == ' ') {
				i++;
				continue;
			}
			else if (payload.charAt(i) == '-') {
				deltaValues[soNcounter] = -1;
				i++;
			}
			else {
				deltaValues[soNcounter] = Character.getNumericValue(payload.charAt(i));
			}
			soNcounter++;
			i++;
		}

		return deltaValues;
	}

	/**
	 * maps the process's delta of round r to a string for the payload
	 * 
	 * @param delta vector
	 * @param round
	 * @return String
	 */
	private String payloadArrayToString(int[] delta, int round) {

		String payload = round + "_";

		for (int i = 0; i < delta.length; i++) {
			if (i != delta.length - 1) {
				payload += delta[i] + ",";
			}
			else {
				payload += delta[i];
			}
		}

		return payload;
	}

	/**
	 * prints an array of integers
	 * 
	 * @param array
	 */
	private void printIntArray(int[] array) {

		System.out.print("<");
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.print(">\n");
	}

	public int perfectFailure() {

		return Utils.DELAY;

	}

}
