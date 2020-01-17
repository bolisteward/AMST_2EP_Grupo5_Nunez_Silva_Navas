package com.amst.theheroproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistroHero extends AppCompatActivity {

    public BarChart graficoBarras;
    private RequestQueue ListaRequest = null;
    private String token = "3429546483754576";

    //Generar un token propio
    private  String nombre;
    private LinearLayout contHeroe;
    private RegistroHero contexto;
    private TextView txtResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_hero);

        ListaRequest = Volley.newRequestQueue(this);
        contexto = this;
        nombre =  getIntent().getStringExtra("NOMBRE");

        solicitarDatos(nombre);
    }

    /**
     * Función solicitarDatos();
     * Esta función se encarga de llamar al RESTAPI obtener la información solicitada, que en este caso es a la informacion
     * de todos los heroes que contengan la palabra de la varibale name dentro en sus nombres. Esta funcion
     * llama otra uncion: mostrarHeroes().
     */
    public void solicitarDatos(String name){
        String url_registros = "https://superheroapi.com/api/"+token+"/search/"+name;
        JsonObjectRequest requestRegistros =
                new JsonObjectRequest( Request.Method.GET, url_registros, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mostrarHeroes(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                    }
                }
                ){ @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "JWT " + token);
                    return params;
                }
                };
        ListaRequest.add(requestRegistros);
    }

    /**
     * Función mostrarHeroes();
     * Esta función obtiene la información de la base de datos del API y se de filtrar los datos de
     * los nombres de los heroes para mostrarlos en una lista al usuario, en forma de botones para que
     * el usuario elija a cual se esta refiriendo. Cada boton implementa la accion de redirigir al usuario a
     * la activity perfilHero y envia datos como nombre y id del heroe.
     */
    private void mostrarHeroes(JSONObject hero){

        JSONArray resultados;
        JSONObject listHero;
        txtResultado = findViewById(R.id.txtResultado);

        contHeroe = findViewById(R.id.Heroe);
        try
        {
            resultados = (JSONArray) hero.get("results");
            String cantHeroes="Resultado: "+String.valueOf(resultados.length());
            txtResultado.setText(cantHeroes);

            for (int i = 0; i < resultados.length(); i++) {
                listHero = (JSONObject) resultados.get(i);
                TextView btn = new Button(RegistroHero.this);
                final String nombre;
                final String ID;
                nombre = listHero.getString("name");
                ID = listHero.getString("id");

                btn.setText(nombre);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Heroe confirmado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), perfilHero.class);
                        intent.putExtra("name", nombre);
                        intent.putExtra("id", ID);
                        startActivity(intent);
                    }
                });

                contHeroe.addView(btn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }


}
