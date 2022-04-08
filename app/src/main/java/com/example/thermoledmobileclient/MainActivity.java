package com.example.thermoledmobileclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private String endereco;
    private String porta;
    private int    sessao;
    private Button executar;
    private EditText editTextAmbiente;
    private EditText editTextAtributo;
    private EditText editTextParametro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            endereco = extras.getString("key");
            porta    = extras.getString("key2");
            sessao   = extras.getInt("key3");

            editTextAmbiente = (EditText) findViewById(R.id.editTextTextPersonName2);
            editTextAtributo = (EditText) findViewById(R.id.editTextTextPersonName3);
            editTextParametro = (EditText) findViewById(R.id.editTextTextPersonName4);

            executar = (Button) findViewById(R.id.button2);

            executar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ambiente  = editTextAmbiente.getText().toString();
                    String atributo  = editTextAtributo.getText().toString();
                    String parametro = editTextParametro.getText().toString();


                    TextView temperatureResultText = (TextView) findViewById(R.id.textView);
                    temperatureResultText.setText(ambiente);
                }

            });


        }

    }

/*

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
        }*/

    /*
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

     */
}


