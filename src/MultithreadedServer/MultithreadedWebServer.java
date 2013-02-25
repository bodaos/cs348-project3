package MultithreadedServer;


import java.net.*;
import java.io.*;

public class MultithreadedWebServer {
	public static void main(String[] args) throws IOException {
		
		String computername=InetAddress.getLocalHost().getHostName();
		System.out.println(computername);
		boolean listening = true;
		int maxConnection = Integer.parseInt(args[0]);
		String rootDir = args[1];
		String userDir = args[2];
		//System.out.println(args[0]);
		 
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4444.");
			System.exit(1);
		}
		ThreadQueue threadQueue = new ThreadQueue(maxConnection);
		while(listening){
			ServerThread thread = new ServerThread(threadQueue, serverSocket.accept(), rootDir, userDir);
			thread.start();
			threadQueue.Enqueue(thread);
		}
		serverSocket.close();
	}
}