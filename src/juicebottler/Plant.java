package juicebottler;

//hires/creates workers

public class Plant implements Runnable {
	// How long do we want to run the juice processing
	public static final long PROCESSING_TIME = 5 * 1000;
	public static Worker[] w = new Worker[5];
	public static AssemblyLine[] a = new AssemblyLine[4];
	private static final int NUM_PLANTS = 6;

	public static void main(String[] args) {
		// Startup the plants

		for (int i = 0; i < 4; i++) {
			a[i] = new AssemblyLine();
		}

		/**
		 * this is where, just below, I struggled until the death to figure out why I
		 * was getting an illegal exception then realized that making my thread sleep
		 * fixed this... WHY I don't get it, but it worked
		 */
		Plant[] plants = new Plant[NUM_PLANTS];
		for (int i = 0; i < NUM_PLANTS; i++) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			plants[i] = new Plant();
			plants[i].startPlant();
		}

		// Give the plants time to do work
		delay(PROCESSING_TIME, "Plant malfunction");

		// Stop the plant, and wait for it to shutdown
		for (Plant p : plants) {
			p.stopPlant();
		}
		for (Plant p : plants) {
			p.waitToStop();
		}

		for (int i = 0; i < 5; i++) {
			w[i].waitToStop();
		}

		// Summarize the results
		int totalProvided = 0;
		int totalProcessed = 0;
		int totalBottles = 0;
		int totalWasted = 0;
		for (Plant p : plants) {
			totalProvided += p.getProvidedOranges();
			totalProcessed += p.getProcessedOranges();
			totalBottles += p.getBottles();
			totalWasted += p.getWaste();
		}
		System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
		System.out.println("Created " + totalBottles + ", wasted " + totalWasted + " oranges");
		System.exit(1);
	}

	private static void delay(long time, String errMsg) {
		long sleepTime = Math.max(1, time);
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			System.err.println(errMsg);
		}
	}

	public final int ORANGES_PER_BOTTLE = 3;

	private final Thread thread;
	public static int[] orangesProvided = new int[1]; // had to change these two dudes to arrays
	public static int[] orangesProcessed = new int[1];
	private volatile boolean timeToWork;

	Plant() {
		orangesProvided[0] = 0;
		orangesProcessed[0] = 0;
		thread = new Thread(this, "Plant");
	}

	public void startPlant() {
		timeToWork = true;
		thread.start();
	}

	public void stopPlant() {
		timeToWork = false;
		for (int i = 0; i < 5; i++) {
			w[i].stopWorker();
		}
	}

	public void waitToStop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			System.err.println(thread.getName() + " stop malfunction");
		}
	}

	public void run() {
		System.out.println(Thread.currentThread().getName() + " has started");

		w[0] = new Worker(null, a[0]);
		for (int i = 1; i < 4; i++) {
			w[i] = new Worker(a[i - 1], a[i]);

		}
		w[4] = new Worker(a[3], null);

		for (int i = 0; i < 5; i++) {
			w[i].ID = i;
			w[i].startWorker();
		}
	}

	public void processEntireOrange(Orange o) {
		while (o.getState() != Orange.State.Bottled) {
			System.out.println("I'm supposed to go to the orange class here");
			o.runProcess();
		}
		orangesProcessed[0]++;
	}

	public int getProvidedOranges() {
		return orangesProvided[0];
	}

	public int getProcessedOranges() {
		return orangesProcessed[0];
	}

	public int getBottles() {
		return orangesProcessed[0] / ORANGES_PER_BOTTLE;
	}

	public int getWaste() {
		return orangesProcessed[0] % ORANGES_PER_BOTTLE;
	}
}
