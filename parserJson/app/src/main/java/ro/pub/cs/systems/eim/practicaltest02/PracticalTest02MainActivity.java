package ro.pub.cs.systems.eim.practicaltest02;

import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

import android.util.Log;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    TextView server;
    EditText server_port_edit_text;
    Button connect_button;
    TextView client;
    EditText client_address_edit_text;
    EditText client_port_edit_text;
    EditText city_edit_text;
    Spinner information_type_spinner;
    Button get_weather_forecast_button;
    TextView weather_forecast_text_view;
    ServerThread serverThread;
    ClientThread clientThread;

    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = server_port_edit_text.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("TAG","Port is " + Integer.parseInt(serverPort));
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e("TAG", "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = client_address_edit_text.getText().toString();
            String clientPort = client_port_edit_text.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String city = city_edit_text.getText().toString();
            String informationType = information_type_spinner.getSelectedItem().toString();
            if (city == null || city.isEmpty()
                    || informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            weather_forecast_text_view.setText("");

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), city, informationType, weather_forecast_text_view
            );
            clientThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        server = findViewById(R.id.server);
        server_port_edit_text = findViewById(R.id.server_port_edit_text);
        connect_button = findViewById(R.id.connect_button);
        client = findViewById(R.id.client);
        client_address_edit_text = findViewById(R.id.client_address_edit_text);
        client_port_edit_text = findViewById(R.id.server_port_edit_text);
        city_edit_text = findViewById(R.id.city_edit_text);
        information_type_spinner = findViewById(R.id.information_type_spinner);
        get_weather_forecast_button = findViewById(R.id.get_weather_forecast_button);
        weather_forecast_text_view = findViewById(R.id.weather_forecast_text_view);

        connect_button.setOnClickListener(new ConnectButtonClickListener());
        get_weather_forecast_button.setOnClickListener((new GetWeatherForecastButtonClickListener()));
    }

    @Override
    protected void onDestroy() {
        Log.i("TAG", "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
