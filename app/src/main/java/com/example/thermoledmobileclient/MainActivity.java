package com.example.thermoledmobileclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.iotservice.IoTServiceGrpc;
import io.grpc.examples.iotservice.LedRequest;
import io.grpc.examples.iotservice.LedReply;
import io.grpc.examples.iotservice.TemperatureReply;
import io.grpc.examples.iotservice.TemperatureRequest;

public class MainActivity extends AppCompatActivity {
    private TextView temperatureResultText;
    private EditText hostEdit;
    private EditText portEdit;
    private Button getTemperatureButton;
    private Button ledOnButton;
    private Button ledOffButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String endereco = extras.getString("key");
            String porta    = extras.getString("key2");
            int    sessao   = extras.getInt("key3");

            //TextView temperatureResultText = (TextView) findViewById(R.id.textView);
            //temperatureResultText.setText( String.valueOf(sessao));
        }
    }

    public void sendTemperatureRequest(View view) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(hostEdit.getWindowToken(), 0);
        getTemperatureButton.setEnabled(false);
        temperatureResultText.setText("");
        new GrpcTask(this).execute(hostEdit.getText().toString(), portEdit.getText().toString(), "sensor01");
    }

    private static class GrpcTask extends AsyncTask<String, Void, String> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;


        private GrpcTask(Activity activity) {
            this.activityReference = new WeakReference<Activity>(activity);
        }

        @Override
        protected String doInBackground(String... params) {

            String host = params[0];
            String portStr = params[1];
            String sensorName = params[2];
            int port = TextUtils.isEmpty(portStr) ? 0 : Integer.parseInt(portStr);
            try {
                channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
                IoTServiceGrpc.IoTServiceBlockingStub stub = IoTServiceGrpc.newBlockingStub(channel);
                TemperatureRequest request = TemperatureRequest.newBuilder().setSensorName(sensorName).build();
                TemperatureReply reply = stub.sayTemperature(request);
                return reply.getTemperature();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return String.format("Failed... : %n%s", sw);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Activity activity = activityReference.get();
            if (activity == null) {
                return;
            }
        }
    }





    private void ledRequest(String ledName, boolean on) {
        new GrpcTask2(this).execute(hostEdit.getText().toString(), portEdit.getText().toString(),
                on ? "1" : "0", ledName);

    }

    private static class GrpcTask2 extends AsyncTask<String, Void, Map<String, Integer>> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;


        private GrpcTask2(Activity activity) {
            this.activityReference = new WeakReference<Activity>(activity);
        }

        @Override
        protected Map<String, Integer> doInBackground(String... params) {

            String host = params[0];
            String portStr = params[1];
            String ledState = params[2];
            String ledName = params[3];
            int port = TextUtils.isEmpty(portStr) ? 0 : Integer.parseInt(portStr);
            try {
                channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
                IoTServiceGrpc.IoTServiceBlockingStub stub = IoTServiceGrpc.newBlockingStub(channel);
                LedRequest request = LedRequest.newBuilder()
                        .setState(Integer.parseInt(ledState))
                        .setLedname(ledName)
                        .build();
                LedReply reply = stub.blinkLed(request);
                return reply.getLedstateMap();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Map<String, Integer> result) {
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Activity activity = activityReference.get();
        }
    }
}