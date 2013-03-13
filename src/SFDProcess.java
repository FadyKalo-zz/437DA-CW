public class SFDProcess extends Process {

	private IFailureDetector detector;

	public SFDProcess(String name, int id, int size, int consensValue) {

		super(name, id, size);
		detector = new StrongFailureDetector(this, consensValue);
	}

	public void begin() {

		detector.begin();
	}

	public synchronized void receive(Message m) {

		String type = m.getType();
		if (type.equals("heartbeat") || type.equals("consens")) {
			detector.receive(m);
		}

	}

	public static void main(String[] args) {

		String name = args[0];
		int id = Integer.parseInt(args[1]);
		int size = Integer.parseInt(args[2]);
		int consens = Integer.parseInt(args[3]);
		SFDProcess p = new SFDProcess(name, id, size, consens);
		p.registeR();
		p.begin();
	}
}
