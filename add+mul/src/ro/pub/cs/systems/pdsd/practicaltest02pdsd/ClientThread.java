package ro.pub.cs.systems.pdsd.practicaltest02pdsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;
import android.widget.TextView;

public class ClientThread extends Thread {

	private String address;
	private int port;
	private String op;
	private TextView rez;
	
	private Socket socket;
	String TAG = "ClientThread";

	public ClientThread(
			String address, 
			int port, 
			String op, 
			TextView rez) {
		this.address = address;
		this.port = port;
		this.op = op;
		this.rez = rez;
	}
	
	@Override
	public void run() {
		try {
			socket = new Socket(address, port);
			if (socket == null) {
				Log.e(TAG, "Could not create socket!");
			}
			
			BufferedReader bufferedReader = Utilities.getReader(socket);
			PrintWriter printWriter = Utilities.getWriter(socket);
			if (bufferedReader != null && printWriter != null) {
				printWriter.println(op);
				printWriter.flush();
				String rezText;
				while ((rezText = bufferedReader.readLine()) != null) {
					final String finalizedRezText = rezText;
					rez.post(new Runnable() {
						@Override
						public void run() {
							rez.setText(finalizedRezText);
						}
					});
				}
			}
		} catch (IOException ioException) {
			Log.e(TAG, "An exception has occurred: " + ioException.getMessage());
			ioException.printStackTrace();
		}
	}
}
