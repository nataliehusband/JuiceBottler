package juicebottler;

public class Worker implements Runnable {

	// How long do we want to run the juice processing
	public static final long PROCESSING_TIME = 5 * 1000;
	private AssemblyLine out;
	private AssemblyLine in;
	private static final int NUM_WORKERS = 5;
	public int ID;

	/**
	 * alrighty, so I created my Assembly Line with an in and an out so i could give
	 * the workers a job and then basically pass the job along to the next worker
	 */
	public Worker(AssemblyLine in, AssemblyLine out) {
		this();
		this.in = in;
		this.out = out;
	}

	private static void delay(long time, String errMsg) {
		long sleepTime = Math.max(1, time);
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			System.err.println(errMsg);
		}
	}

	private Thread thread = new Thread();

	private boolean timeToWork;

	Worker() {
		thread = new Thread(this, "Slaves");
	}

	public void startWorker() {
		timeToWork = true;
		thread.start();
	}

	public void stopWorker() {
		// System.out.println("do I even go into this(i should show up 5 times)");
		timeToWork = false;
	}

	public void waitToStop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			System.err.println(thread.getName() + " stop malfunction");
		}
	}

	public void run() {
		System.out.println(Thread.currentThread().getName() + " Processing oranges");

		/**
		 * I like this, I gave each of my workers an ID so I could assign them to the
		 * jobs I wanted them to do
		 */
		while (timeToWork) {
			if (ID == 0) {
				fetchOrange();
			} else if (ID == 1) {
				peelOrange();
			} else if (ID == 2) {
				squeezeOrange();
			} else if (ID == 3) {
				bottleOrange();
			} else if (ID == 4) {
				processOrange();
			}
			// System.out.println("");
		}
		// System.out.println("");
	}

	/**
	 * below, I created methods for my lil workers. They each get their job, as you
	 * can see.
	 */
	public void fetchOrange() {
		Orange o = new Orange();
		Plant.orangesProvided[0]++;
		out.addOrange(o);
		// System.out.println("Orange added to peel");
	}

	public void peelOrange() {
		Orange o = in.getOrange();
		if (o.getState() == Orange.State.Fetched) {
			o.runProcess();
			out.addOrange(o);
		}
	}

	public void squeezeOrange() {
		Orange o = in.getOrange();
		if (o.getState() == Orange.State.Peeled) {
			o.runProcess();
			out.addOrange(o);
		}
	}

	public void bottleOrange() {
		Orange o = in.getOrange();
		if (o.getState() == Orange.State.Squeezed) {
			o.runProcess();
			out.addOrange(o);
		}
	}

	public void processOrange() {
		Orange o = in.getOrange();
		if (o.getState() == Orange.State.Bottled) {
			Plant.orangesProcessed[0]++;
		}
	}
}
