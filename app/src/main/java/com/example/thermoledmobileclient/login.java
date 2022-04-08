package com.example.thermoledmobileclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {

    Button bt;
    EditText editTextEndereco;
    EditText editTextPorta;
    int editTextSessao = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEndereco = (EditText) findViewById(R.id.editTextTextPersonName5);
        editTextPorta = (EditText) findViewById(R.id.editTextTextPersonName);

        bt = (Button) findViewById(R.id.button);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String endereco = editTextEndereco.getText().toString();
                String porta = editTextPorta.getText().toString();
                Intent i = new Intent(login.this, MainActivity.class);
                i.putExtra("key", endereco);
                i.putExtra("key2", porta);
                i.putExtra("key3",editTextSessao);
                startActivity(i);
            }
        });


    }



}