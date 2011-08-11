package forkjoin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Folder {
	private final List<Folder> subFolders;
	private final List<Document> documents;
	private final String name;

	Folder(List<Folder> subFolders, List<Document> documents, String name) {
		this.subFolders = subFolders;
		this.documents = documents;
		this.name = name;
	}

	List<Folder> getSubFolders() {
		return subFolders;
	}

	List<Document> getDocuments() {
		return documents;
	}

	static Folder fromDirectory(File dir) throws IOException {
		List<Document> documents = new ArrayList<>();
		List<Folder> subFolders = new ArrayList<>();
		for (File entry : dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isFile()) {
					// only accept files with suffix .java
					return pathname.getName().endsWith(".java");
				}
				// is directory, accept
				return true;
			}
		})) {
			if (entry.isDirectory()) {
				subFolders.add(Folder.fromDirectory(entry));
			} else {
				documents.add(Document.fromFile(entry));
			}
		}
		return new Folder(subFolders, documents, dir.getName());
	}

	@Override
	public String toString() {
		return "Folder{" +
				"name=" + name +
				", subFolders=" + subFolders.size() +
				", documents=" + documents.size() +
				'}';
	}
}
