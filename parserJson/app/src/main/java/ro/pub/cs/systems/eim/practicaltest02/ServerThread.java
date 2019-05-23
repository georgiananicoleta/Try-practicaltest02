package ro.pub.cs.systems.eim.practicaltest02;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import cz.msebera.android.httpclient.client.ClientProtocolException;

class ServerThread extends Thread {
    private boolean isRunning;
    private ServerSocket serverSocket;
    int port;
    private HashMap<String, WeatherForecastInformation> data;

    public ServerThread(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e("Tag", "An exception has occurred: " + ioException.getMessage());
        }
        data = new HashMap<String, WeatherForecastInformation>();
    }

    public void startServer() {
        isRunning = true;
        start();
    }

    public HashMap<String, WeatherForecastInformation> getData() {
        return data;
    }

    public synchronized void setData(String city, WeatherForecastInformation weatherForecastInformation) {
        this.data.put(city, weatherForecastInformation);
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void stopServer() {
        isRunning = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                    Log.v("TAG", "stopServer() method invoked "+serverSocket);
                } catch(IOException ioException) {
                    Log.e("TAG", "An exception has occurred: "+ioException.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i("Tag", "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i("Tag", "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.e("Tag", "[SERVER THREAD] An exception has occurred: " + clientProtocolException.getMessage());
        } catch (IOException ioException) {
            Log.e("Tag", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e("TAG", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
