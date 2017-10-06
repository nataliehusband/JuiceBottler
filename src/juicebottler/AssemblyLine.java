package juicebottler;

import java.util.ArrayList;
import java.util.List;

public class AssemblyLine {
	Plant p = new Plant();

	private final List<Orange> oranges;

	AssemblyLine() {
		// System.out.println("Entered AssemblyLine class");
		oranges = new ArrayList<Orange>();
	}

	public synchronized void addOrange(Orange o) {
		oranges.add(o);
		if (countOranges() == 1) {
			notifyAll();
		}
	}

	public synchronized Orange getOrange() {
		while (countOranges() == 0) {
			try {
				wait();
			} catch (InterruptedException ignored) {
			}
		}
		return oranges.remove(0);
	}

	public synchronized int countOranges() {
		return oranges.size();
	}
}
