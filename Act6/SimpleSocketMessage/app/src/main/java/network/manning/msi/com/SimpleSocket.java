package network.manning.msi.com;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.spec.ECField;

/**
 * Android direct to Socket example.
 *
 * For this to work you need a server listening on the IP address and port specified. See the
 * NetworkSocketServer project for an example.
 *
 *
 * @author charliecollins
 *
 */
public class SimpleSocket extends Activity {

    private static final String CLASSTAG = SimpleSocket.class.getSimpleName();

    private EditText ipAddress;
    private EditText port;
    private EditText socketInput;
    private TextView socketOutput;
    Handler h;

    private static final String SERVER_IP = "10.0.2.2";

    @Override
    public void onCreate(final Bundle icicle) {
        Button socketButton;

        super.onCreate(icicle);
        this.setContentView(R.layout.simple_socket);

        this.port = (EditText) findViewById(R.id.socket_port);
        this.socketInput = (EditText) findViewById(R.id.socket_input);
        this.socketOutput = (TextView) findViewById(R.id.socket_output);
        socketButton = (Button) findViewById(R.id.socket_button);

        this.h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        try {
                            socketOutput.setText((String) msg.obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                }
                super.handleMessage(msg);
            }
        };

        socketButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    socketOutput.setText("");
                    final Thread tr = new Thread() {
                        public void run() {
                            try {
                                String input = socketInput.getText().toString();

                                Message msg = new Message();
                                msg.what = 0;
                                String output = callSocket(InetAddress.getByName(SERVER_IP), port.getText().toString(), socketInput.getText().toString());
                                msg.obj = output;

                                h.sendMessage(msg);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    tr.start();

                } catch (Exception e) { }

            }
        });

        if (Build.VERSION.SDK_INT >= 23)
            requestPermissions();
    }

    private String callSocket(final InetAddress ad, final String port, final String socketData) {

        Socket socket = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        String output = null;

        try {
            socket = new Socket(ad, Integer.parseInt(port));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // send input terminated with \n
            String input = socketData;
            writer.write(input + "\n", 0, input.length() + 1);
            writer.flush();
            // read back output
            output = reader.readLine();
            Log.d(getText(R.string.log_tag).toString(), " " + SimpleSocket.CLASSTAG + " output - " + output);
            // send EXIT and close
            writer.write("EXIT\n", 0, 5);
            writer.flush();
        } catch (IOException e) {
            Log.e(getText(R.string.log_tag).toString(), " " + SimpleSocket.CLASSTAG + " IOException calling socket", e);
        } finally {
            try {
                if (writer!=null)
                  writer.close();
            } catch (IOException e) { // swallow
            }
            try {
                if (reader!=null)
                    reader.close();
            } catch (IOException e) { // swallow
            }
            try {
                if (socket!=null)
                   socket.close();
            } catch (IOException e) { // swallow
            }
        }
        return output;
    }
    
    private boolean ckeckPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            //String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE};
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.INTERNET) ==
                    PackageManager.PERMISSION_GRANTED)
                return true;
            else
                return false;
        }
        else
            return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(SimpleSocket.this,
                new String[]{Manifest.permission.INTERNET},
                0);
    }
}
