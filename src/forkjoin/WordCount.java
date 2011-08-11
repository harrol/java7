package forkjoin;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WordCount {

	private final ForkJoinPool forkJoinPool = new ForkJoinPool();


	class FolderSearchTask extends RecursiveTask<Long> {
		private final Folder folder;
		private final String searchedWord;

		FolderSearchTask(Folder folder, String searchedWord) {
			super();
			this.folder = folder;
			this.searchedWord = searchedWord;
		}

		@Override
		protected Long compute() {
			long count = 0L;
			List<RecursiveTask<Long>> forks = new LinkedList<>();
			for (Folder subFolder : folder.getSubFolders()) {
				FolderSearchTask task = new FolderSearchTask(subFolder, searchedWord);
				forks.add(task);
				task.fork();
			}
			for (Document document : folder.getDocuments()) {
				DocumentSearchTask task = new DocumentSearchTask(document, searchedWord);
				forks.add(task);
				task.fork();
			}
			for (RecursiveTask<Long> task : forks) {
				count = count + task.join();
			}
			return count;
		}
	}


	class DocumentSearchTask extends RecursiveTask<Long> {
		private final Document document;
		private final String searchedWord;

		DocumentSearchTask(Document document, String searchedWord) {
			super();
			this.document = document;
			this.searchedWord = searchedWord;
		}

		@Override
		protected Long compute() {
			return occurrencesCount(document, searchedWord);
		}
	}

	String[] wordsIn(String line) {
		return line.trim().split("(\\s|\\p{Punct})+");
	}

	Long occurrencesCount(Document document, String searchedWord) {
		long count = 0;
		for (String line : document.getLines()) {
			for (String word : wordsIn(line)) {
				if (searchedWord.equalsIgnoreCase(word)) {
					count = count + 1;
				}
			}
		}
		return count;
	}

	Long countOccurrrencesParallel(Folder folder, String searchedWord) {
		return forkJoinPool.invoke(new FolderSearchTask(folder, searchedWord));
	}

	Long countOccurrencesOnSingleThread(Folder folder, String searchedWord) {
		long count = 0;
		for (Folder subFolder : folder.getSubFolders()) {
			count = count + countOccurrencesOnSingleThread(subFolder, searchedWord);
		}
		for (Document document : folder.getDocuments()) {
			count = count + occurrencesCount(document, searchedWord);
		}
		return count;
	}

	public static void main(String[] args) throws Exception {
		WordCount main = new WordCount();
		System.out.print("Reading folders...");
		long start = System.currentTimeMillis();
		Folder folder = Folder.fromDirectory(new File("c:/projects"));
		System.out.println("...done. Millis: " + (System.currentTimeMillis() - start));

		String search = "class";

		System.out.print("Searching single threaded...");
		long result;
		result = main.countOccurrencesOnSingleThread(folder, search);
		result = main.countOccurrencesOnSingleThread(folder, search);

		// start timing
		start = System.currentTimeMillis();
		result = main.countOccurrencesOnSingleThread(folder, search);
		float took1 = (System.currentTimeMillis() - start);

		System.out.print(" Millis: " + took1);
		System.out.println(". Found: " + result);

		System.out.print("Searching in parallel...");
		result = main.countOccurrrencesParallel(folder, search);
		result = main.countOccurrrencesParallel(folder, search);

		// start timing
		start = System.currentTimeMillis();
		result = main.countOccurrrencesParallel(folder, search);
		float took2 = (System.currentTimeMillis() - start);

		System.out.print(" Millis: " + took2);
		System.out.println(". Found: " + result);

		float improvement = took1/took2;
		System.out.println("Improvement: " + improvement);

	}
}
