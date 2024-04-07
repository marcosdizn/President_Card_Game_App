package com.example.president;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class Activity2 extends AppCompatActivity {
    TextInputEditText name, numberRounds;

    static TextView roundsError;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        name = (TextInputEditText) findViewById(R.id.name);
        numberRounds = (TextInputEditText) findViewById(R.id.numberRounds);

        roundsError = (TextView) findViewById(R.id.roundsError);
    }

    public void openActivity3(View view) {
        String username = name.getText().toString();
        String numberOfRounds = numberRounds.getText().toString();

        // Verifica si numberOfRounds es cero
        if (Integer.parseInt(numberOfRounds) == 0) {
            // Muestra un mensaje de error en TextInputLayout
            roundsError.setText("Rounds must be above cero");
        } else {
            // Borra el mensaje de error si estaba presente
            roundsError.setText("");

            // Abre Activity3 con los datos proporcionados
            Intent intent = new Intent(this, Activity3.class);
            intent.putExtra("keyusername", username);
            intent.putExtra("keyrounds", numberOfRounds);
            startActivity(intent);
        }
    }




    public void Tirar(View view) {
//        Activity3.pa
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}