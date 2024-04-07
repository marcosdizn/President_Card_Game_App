package com.example.president;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class Activity4 extends AppCompatActivity {

    TextView Primero, Segundo, Tercero, Cuarto, puntuacionPrimero, puntuacionSegundo, puntuacionTercero, puntuacionCuarto;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);

        Primero = findViewById(R.id.Primero);
        Segundo = findViewById(R.id.Segundo);
        Tercero = findViewById(R.id.Tercero);
        Cuarto = findViewById(R.id.Cuarto);

        puntuacionPrimero = findViewById(R.id.puntuacionPrimero);
        puntuacionSegundo = findViewById(R.id.puntuacionSegundo);
        puntuacionTercero = findViewById(R.id.puntuacionTercero);
        puntuacionCuarto = findViewById(R.id.puntuacionCuarto);

        Primero.setText(getIntent().getStringExtra("keyname1"));
        Segundo.setText(getIntent().getStringExtra("keyname2"));
        Tercero.setText(getIntent().getStringExtra("keyname3"));
        Cuarto.setText(getIntent().getStringExtra("keyname4"));

        puntuacionPrimero.setText(getIntent().getStringExtra("keyscore1"));
        puntuacionSegundo.setText(getIntent().getStringExtra("keyscore2"));
        puntuacionTercero.setText(getIntent().getStringExtra("keyscore3"));
        puntuacionCuarto.setText(getIntent().getStringExtra("keyscore4"));

    }

    public void SalirJuego(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("EXIT", false)) {
            finish();
        }
    }
}
