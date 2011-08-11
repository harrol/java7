/**
 * User: hlis Date: 29/07/11 Time: 11:16
 */
public class Main {


	public static void main(String[] args) {

		System.out.println("---- Directory listing ----");
		new FileWalker().walk("c:/tmp/java7");

		System.out.println("---- Watching directory ----");
		new FileWatcher().watch("c:/tmp/java7");

	}


}
