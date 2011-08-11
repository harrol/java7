import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;

public class Concurrent1 {

	static class Sum implements Callable<Long> {
		private final long from;
		private final long to;

		Sum(long from, long to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public Long call() {
			long acc = 0;
			for (long i = from; i <= to; i++) {
				acc = acc + i;
			}
			return acc;
		}
	}


	public static void main(String[] args) throws Exception {
		System.currentTimeMillis();
		long start;
		long duration;

		ExecutorService executor = Executors.newFixedThreadPool(2);

		List<Sum> tasks = asList(
				new Sum(0, 10),
				new Sum(10, 1_000),
				new Sum(1_000, 10_000),
				new Sum(10_000, 1_000_000),
				new Sum(1_000_000, 10_000_000),
				new Sum(10_000_000, 1_000_000_00));

		start = System.currentTimeMillis();
		List<Future<Long>> futures = executor.invokeAll(tasks);
		duration = System.currentTimeMillis() - start;

		executor.shutdown();

		for (Future<Long> result : futures) {
			System.out.println(result.get());
		}

		System.out.println("Millis: " + duration);


	}
}
