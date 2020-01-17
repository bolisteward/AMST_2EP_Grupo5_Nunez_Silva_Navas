package com.amst.theheroproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public Button btn_Buscar_Heroes ;
    public EditText edtNombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_Buscar_Heroes = findViewById(R.id.btn_busqueda);
        edtNombre = findViewById(R.id.edtNombre);
    }

    public void Resultados(View v){
        Intent intent = new Intent(this , RegistroHero.class);
        intent.putExtra("NOMBRE",edtNombre.getText().toString());
        startActivity(intent);
    }
}
