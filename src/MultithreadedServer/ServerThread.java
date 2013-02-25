package MultithreadedServer;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.*;

public class ServerThread extends Thread {

	/**
	 * @param args
	 */
	public Socket clientSocket; 
	public ThreadQueue threadQueue;
	public PrintWriter out;
	public BufferedReader in;
	public String rootDir;
	public String userDir; 
	public ServerThread(ThreadQueue threadQueue, Socket clientSocket, String rootDir, String userDir){
		this.threadQueue = threadQueue;
		this.clientSocket = clientSocket;
		this.rootDir = rootDir;
		this.userDir = userDir;
	}
	public void KillYourSelf(){

		try {
			//System.out.println("I killed myself");
			clientSocket.close();
			String error = "HTTP/1.1 404 OK \r\n"
					+ "Date: Sun, 10 Feb 2013 18:17:43 GMT\r\n"
					+ "Content-Type: text/html\r\n"
					+ "Content-Length: 54\r\n\r\n"
					+ "<html><body><h1>Connection Closed</h1></body></html>";
			out.println(error);
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread.yield();
	}
	public void run(){

		try {
			OutputStream raw_out = clientSocket.getOutputStream();
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			
			String inputLine;
			byte[] outputLine;
			String request = "";
			while ( (inputLine = in.readLine()) != null) {
				if(inputLine.isEmpty()){
					//System.out.println(request);
					//Handle image specially
					outputLine = HTTPProcessor.process(request, rootDir, userDir);
					raw_out.write(outputLine);
					//out.println(outputLine);
					request = "";
				}else{
					request = request + " "+ inputLine;
					//System.out.println(request);
				}
			}


		}catch(SocketException e){
			System.out.println("socket closed");
			
		}
		catch (IOException e) {
			System.err.println("IOexception in Server thread");
		} 
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
