public class PerfectFailureDetector extends FailureDetector implements IFailureDetector {

	public PerfectFailureDetector(Process p) {

		super(p);
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
				printSuspected();

				// go through the list of timeStamps
				if (timestamps.size() != 0) {
					Double[] ts = timestamps.toArray(new Double[timestamps.size()]);
					for (int i = 0; i < ts.length; i++) {
						// if last timestamp exceeds given delta/ delay add to suspected list
						if (ts[i] != -1) {
							if (System.currentTimeMillis() - ts[i] > timeout && !isSuspect(i)) {
								// add to suspect list
								isSuspected(i);

							}
						}
					}
				}
			}

		}

	}

	public void begin() {

		t.schedule(new PeriodicTask(), 0, Delta);
		(new Thread(new CheckIfProcessFailed())).start();
	}

	public void receive(Message m) {

		// Utils.out(p.pid, m.toString());

		int source = m.getSource() - 1;
		double now = System.currentTimeMillis();
		timestamps.set(source, now);

	}

	public int perfectFailure() {

		return Utils.DELAY;

	}
}
