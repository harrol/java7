import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcher {

	public void watch(String directory) {
		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();
			WatchEvent.Kind[] events = {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};
			Paths.get(directory).register(watchService, events);
			while (true) {
				WatchKey watchKey = watchService.take();
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					switch (event.kind().name()) {
						case "ENTRY_CREATE":
							System.out.println(event.context().toString() + " was created");
							break;
						case "ENTRY_DELETE":
							System.out.println(event.context().toString() + " was deleted");
							break;
						case "ENTRY_MODIFY":
							System.out.println(event.context().toString() + " was modified");
							break;
						default:
							System.out.println("Unknown event: " + event.kind().name() + "on " + event.context().toString());
					}
				}
				watchKey.reset();
			}
		} catch (IOException | InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
