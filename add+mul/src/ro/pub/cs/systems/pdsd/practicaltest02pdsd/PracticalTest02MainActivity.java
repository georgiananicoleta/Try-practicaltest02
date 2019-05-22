package ro.pub.cs.systems.pdsd.practicaltest02pdsd;

import android.app.Activity;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class PracticalTest02MainActivity extends Activity {

	private EditText op1EditText;
	private EditText op2EditText;
	private TextView rezAddText;
	private TextView rezMulText;
	private Button addButton;
	private Button mulButton;
	
	private ServerThread serverThread;
	private ClientThread clientThread;
	
	String TAG = "MAIN ACTIVITY";
	int serverPort = 6666;
	
	
	private GetRezButtonClickListener getAddButtonClickListener = new GetRezButtonClickListener("add");
	private GetRezButtonClickListener getMulButtonClickListener = new GetRezButtonClickListener("mul");
	private class GetRezButtonClickListener implements Button.OnClickListener {
		String op;
		public GetRezButtonClickListener(String op) {
			this.op = op;
		}
		@Override
		public void onClick(View view) {
			String clientAddress = "localhost";//clientAddressEditText.getText().toString();
			
			if (serverThread == null || !serverThread.isAlive()) {
				Log.e(TAG, "There is no server to connect to!");
				return;
			}
			
			String op1 = op1EditText.getText().toString();
			String op2 = op2EditText.getText().toString(); 
			if (op1 == null || op1.isEmpty() ||
					op2 == null || op2.isEmpty()) {
				Toast.makeText(
					getApplicationContext(),
					"Parameters from client should be filled!",
					Toast.LENGTH_SHORT
				).show();
				return;
			}
	
			TextView rezText = op.equals("add") ? rezAddText : rezMulText;
			rezText.setText("");
			
			clientThread = new ClientThread(
					clientAddress,
					serverPort,
					op + "," + op1 + "," + op2,
					rezText);
			clientThread.start();
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);
        
        op1EditText = (EditText) findViewById(R.id.op1);
        op2EditText = (EditText) findViewById(R.id.op2);
        rezAddText = (TextView) findViewById(R.id.rezAdd);
        rezMulText = (TextView) findViewById(R.id.rezMul);
        addButton = (Button) findViewById(R.id.buttonAdd);
        mulButton = (Button) findViewById(R.id.buttonMul);
        
        addButton.setOnClickListener(getAddButtonClickListener);
        mulButton.setOnClickListener(getMulButtonClickListener);
        TextView portText = (TextView) findViewById(R.id.portText);
        portText.setText(serverPort + "");
        
        startServer();
    }

    private void startServer() {
    	serverThread = new ServerThread(serverPort);
		if (serverThread.getServerSocket() != null) {
			serverThread.start();
		} else {
			Log.e(TAG, "Could not create server thread!");
		}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.practical_test02_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
