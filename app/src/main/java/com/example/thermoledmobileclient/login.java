package com.example.thermoledmobileclient;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.idprovider.IdProviderGrpc;
import io.grpc.examples.idprovider.LoginRequest;
import io.grpc.examples.idprovider.LoginReply;

public class login extends AppCompatActivity {

    Button login;
    public EditText editTextEndereco;
    public EditText editTextPorta;
    EditText editTextUsuario;
    EditText editTextPassword;
    static int Sessao = 0;
    TextView resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEndereco    = (EditText) findViewById(R.id.editTextTextPersonName5);
        editTextPorta       = (EditText) findViewById(R.id.editTextTextPersonName);
        editTextUsuario     = (EditText) findViewById(R.id.editTextTextEmailAddress);
        editTextPassword     = (EditText) findViewById(R.id.editTextTextPassword);
        login                  = (Button) findViewById(R.id.button);
    }

    public void login(View view) {
        String endereco = editTextEndereco.getText().toString();
        String porta = editTextPorta.getText().toString();

        new login.GrpcTask(this).execute(endereco, porta,editTextUsuario.getText().toString(),editTextPassword.getText().toString(),"1");

    }

    private class GrpcTask extends AsyncTask<String, Void, String> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;


        private GrpcTask(Activity activity) {
            this.activityReference = new WeakReference<Activity>(activity);
        }

        @Override
        protected String doInBackground(String... params) {

            String endereco = params[0];
            String porta = params[1];
            String usuario = params[2];
            String password = params[3];

            int port = TextUtils.isEmpty(porta) ? 0 : Integer.parseInt(porta);
            try {
                channel = ManagedChannelBuilder.forAddress(endereco, port).usePlaintext().build();
                IdProviderGrpc.IdProviderBlockingStub stub = IdProviderGrpc.newBlockingStub(channel);
                LoginRequest  request = LoginRequest .newBuilder()
                        .setUser(usuario)
                        .setPassword(password)
                        .build();
                LoginReply  reply = stub.login(request);
                return String.valueOf(reply.getSession());
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

            Button login = (Button) activity.findViewById(R.id.button);
            EditText editTextEndereco    = (EditText) activity.findViewById(R.id.editTextTextPersonName5);
            EditText editTextPorta       = (EditText) activity.findViewById(R.id.editTextTextPersonName);

            String endereco = editTextEndereco.getText().toString();
            String porta = editTextPorta.getText().toString();
            Sessao = Integer.parseInt(result);


            if(Sessao != 0)
            {
                Intent i = new Intent(activity, MainActivity.class);
                i.putExtra("key", endereco);
                i.putExtra("key2", porta);
                i.putExtra("key3",Sessao);
                startActivity(i);
            }else
            {
                TextView resultado = (TextView) activity.findViewById(R.id.textView2);
                resultado.setText("Erro");
            }

        }
    }
}