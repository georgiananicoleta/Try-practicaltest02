package ro.pub.cs.systems.pdsd.practicaltest02pdsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

public class CommunicationThread extends Thread {
	private ServerThread serverThread;
	private Socket       socket;
	
	String TAG = "CommunicationThread";
	
	public CommunicationThread(ServerThread serverThread, Socket socket) {
		this.serverThread = serverThread;
		this.socket       = socket;
	}
	
	@Override
	public void run() {
		if (socket != null) {
			try {
				BufferedReader bufferedReader = Utilities.getReader(socket);
				PrintWriter printWriter = Utilities.getWriter(socket);
				
				if (bufferedReader != null && printWriter != null) {
					Log.i(TAG, "Waiting for parameters from client!" );
					String op = bufferedReader.readLine();
										
					if (op != null && !op.isEmpty()) {
						int rez = 0;
						String[] ops = op.split(",");
						if (ops[0].equals("add")) {
							rez = Integer.parseInt(ops[1]) + Integer.parseInt(ops[2]);
						} else if (ops[0].equals("mul")) {
							rez = Integer.parseInt(ops[1]) * Integer.parseInt(ops[2]);
							Thread.sleep(1000);
						}
						printWriter.println(rez);
						printWriter.flush();
						
					} else {
						Log.e(TAG, "Error receiving parameters from client!");
					}
				} else {
					Log.e(TAG, "BufferedReader / PrintWriter are null!");
				}
				socket.close();
			} catch (IOException ioException) {
				Log.e(TAG, "An exception has occurred: " + ioException.getMessage());
				ioException.printStackTrace();
			} catch (InterruptedException e) {
				Log.e(TAG, "Can't sleep: " + e.getMessage());
				e.printStackTrace();
			} 
		}
	}
}
