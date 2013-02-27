package MultithreadedServer;

import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("serial")
public class ThreadQueue extends ConcurrentLinkedQueue<ServerThread> {
	public int max;
	public int count = 0;
	public ServerThread latestThread;
	
	/**
	 * @param args
	 */
	public ThreadQueue(int maxSize){
		this.max = maxSize;
	}
	public synchronized boolean Enqueue(ServerThread t){
		//Add a new thread to the queue. 
		if (count == max){
			this.poll().KillYourSelf();
			count = count-1;
			//System.out.println("the server is full, killed the oldest thread");
		}
		this.add(t);
		count = count + 1;
		latestThread = t;
		return true;
	}
	public synchronized boolean Dequeue(){
		//Dequeue the thread. 
		count = count -1;
		this.poll();
		return true;	
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
