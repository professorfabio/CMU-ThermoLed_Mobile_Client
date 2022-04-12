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
import io.grpc.examples.iotservice.AttributeRequest;
import io.grpc.examples.iotservice.AttributeReply;
import io.grpc.examples.iotservice.TemperatureReply;
import io.grpc.examples.iotservice.TemperatureRequest;

public class MainActivity extends AppCompatActivity {
    private TextView temperatureResultText;
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
            int aux;
            endereco = extras.getString("key");
            porta    = extras.getString("key2");
            sessao   = extras.getInt("key3");

            aux = Integer.parseInt(porta) + 1;
            porta = String.valueOf(aux);

            editTextAmbiente    = (EditText) findViewById(R.id.editTextTextPersonName2);
            editTextAtributo    = (EditText) findViewById(R.id.editTextTextPersonName3);
            editTextParametro   = (EditText) findViewById(R.id.editTextTextPersonName4);
            executar            = (Button) findViewById(R.id.button2);

        }

    }
    public void executar(View view) {
        new GrpcTask(this).execute(endereco, porta, String.valueOf(sessao),editTextAmbiente.getText().toString(),editTextAtributo.getText().toString(), editTextParametro.getText().toString(),"1");
    }

    private static class GrpcTask extends AsyncTask<String, Void, String> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;


        private GrpcTask(Activity activity) {
            this.activityReference = new WeakReference<Activity>(activity);
        }

        @Override
        protected String doInBackground(String... params) {

            String endereco = params[0];
            String porta = params[1];
            String sessao = params[2];
            String ambiente = params[3];
            String atributo = params[4];
            String parametro = params[5];

            int port = TextUtils.isEmpty(porta) ? 0 : Integer.parseInt(porta);
            try {
                channel = ManagedChannelBuilder.forAddress(endereco, port).usePlaintext().build();
                IoTServiceGrpc.IoTServiceBlockingStub stub = IoTServiceGrpc.newBlockingStub(channel);
                AttributeRequest request = AttributeRequest.newBuilder()
                        .setSession(Integer.parseInt(sessao))
                        .setEnvironment(ambiente)
                        .setAttribute(atributo)
                        .setParameter(parametro)
                        .build();
                AttributeReply reply = stub.callAttribute(request);
                return reply.getValue();
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

            TextView temperatureResultText = (TextView) activity.findViewById(R.id.textView);
            temperatureResultText.setText(result);

        }
    }

}


