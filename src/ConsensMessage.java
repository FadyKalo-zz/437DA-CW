/**
 * class representing a consens message from other processes contains getters and setters, thus very self explaining
 * 
 * @author isabel
 * 
 */
public class ConsensMessage {

	int round;
	int pid;
	int[] deltaP;

	public ConsensMessage(int round, int pid, int[] deltaP) {

		super();
		this.round = round;
		this.pid = pid;
		this.deltaP = deltaP;
	}

	public int getRound() {

		return round;
	}

	public void setRound(int round) {

		this.round = round;
	}

	public int getPid() {

		return pid;
	}

	public void setPid(int pid) {

		this.pid = pid;
	}

	public int[] getDeltaP() {

		return deltaP;
	}

	public void setDeltaP(int[] deltaP) {

		this.deltaP = deltaP;
	}

	public void printMessage() {

		System.out.print("Message in round: " + round + " of process " + pid + " Delta: ");
		for (int i = 0; i < deltaP.length; i++) {
			System.out.print(i + ", ");
		}
		System.out.println();
	}
}
