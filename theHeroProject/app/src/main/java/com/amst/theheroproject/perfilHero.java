package com.amst.theheroproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class perfilHero extends AppCompatActivity {

    public BarChart graficoBarras;
    private RequestQueue ListaRequest = null;
    private String token = "3429546483754576";
    private perfilHero contexto;
    private String name;
    private String id;
    private TextView txtNombreCompleto, txtNombreHero;
    private String[] valuesX= new String[]{"Intelligence","Strength","Speed","Durability", "Power", "Combat"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_hero);

        //setTitle("Grafico de barras");
        ListaRequest = Volley.newRequestQueue(this);
        contexto = this;

        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");



        /* GRAFICO */
        this.iniciarGrafico();
        this.solicitarDatos(name, id);
    }

    /**
     * Función IniciarGrafico();
     * Obtiene el grafico desde la vista (layout) y coloca las propiedades de inicio. Propiedades como: tamaño,
     * descripción, animaciones, títulos colocadas en esta función. (Estas propiedades son finales y no
     * cambian por ninguna condición.)
     */
    public void iniciarGrafico() {
        graficoBarras = findViewById(R.id.barChart);
        graficoBarras.getDescription().setEnabled(false);
        graficoBarras.setMaxVisibleValueCount(60);
        graficoBarras.setPinchZoom(false);
        graficoBarras.setDrawBarShadow(false);
        graficoBarras.setDrawGridBackground(false);
        XAxis xAxis = graficoBarras.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        graficoBarras.getAxisLeft().setDrawGridLines(false);
        graficoBarras.animateY(1500);
        graficoBarras.getLegend().setEnabled(false);
    }

    /**
     * Función solicitarDatos();
     * Esta función se encarga de llamar al RESTAPI obtener la información solicitada del heroe basados en el id,
     * que es enviada a la funcion de mostrarResultados() Y actualizarGrafico(). Tambien presenta el nombre del heroe
     * en el TextView txtNombreHero.
     */
    public void solicitarDatos(String nombre, String Id){
        txtNombreHero = findViewById(R.id.txtNombreHero);

        txtNombreHero.setText(nombre);
        System.out.println(Id);

        String url_registros = "https://superheroapi.com/api/"+token+"/"+Id;
        JsonObjectRequest requestRegistros =
                new JsonObjectRequest( Request.Method.GET, url_registros, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mostrarResultados(response);
                        actualizarGrafico(response);
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
     * Función mostrarResultados();
     * Esta función obtiene la información de la bibliografia del heroe. Se encarga de filtrar los datos de
     * de nombre completo del heroe para mostrarlos en el TextView txtNombreCompleto.
     */
    private void mostrarResultados(JSONObject hero){
        JSONObject biografia;
        String fullname;
        txtNombreCompleto = findViewById(R.id.txtNombreCompleto);
        try{
            biografia = (JSONObject) hero.get("biography");
            fullname = biografia.getString("full-name");
            txtNombreCompleto.setText(fullname);

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }

    /**
     * Función actualizarGrafico();
     * Esta función actualiza y  obtiene los datos de estadisticas del heroe para actualizar el grafico en el app.
     * Llamamos a la función llenarGrafico() ingresando el nuevo dato obtenido.
     */
    private void actualizarGrafico(JSONObject hero){

        JSONObject powerStats;

        float intelligence;
        float Strength;
        float Speed;
        float Durability;
        float Power;
        float Combat;

        ArrayList<BarEntry> dato_hero = new ArrayList<>();

        try{

            powerStats = (JSONObject) hero.get("powerstats");

            intelligence = powerStats.getInt("intelligence");
            dato_hero.add(new BarEntry(0, intelligence));

            Strength = powerStats.getInt("strength");
            dato_hero.add(new BarEntry(1, Strength));

            Speed = powerStats.getInt("speed");
            dato_hero.add(new BarEntry(2, Speed));

            Durability = powerStats.getInt("durability");
            dato_hero.add(new BarEntry(3, Durability));

            Power = powerStats.getInt("power");
            dato_hero.add(new BarEntry(4, Power));

            Combat = powerStats.getInt("combat");
            dato_hero.add(new BarEntry(5, Combat));

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("error");
        }
        System.out.println(dato_hero);
        llenarGrafico(dato_hero);
    }

    /**
     * Función llenarGrafico();
     * Esta función ocurrirá el Live Reload durante llenarGrafico() actualizaremos el grafico actual con el
     * nuevo dato encontrado. Pero también ejecutaremos un Hilo (como Runnable) para ejecutar una acción
     * de fondo, donde se volverá a buscar un nuevo dato (si es que este existe) al llamar nuevamente a la
     * función solicitarDatos() iniciando el ciclo..
     */
    private void llenarGrafico(ArrayList<BarEntry> dato_hero){
        BarDataSet heroDataSet;

        if ( graficoBarras.getData() != null && graficoBarras.getData().getDataSetCount() > 0) {
            heroDataSet = (BarDataSet) graficoBarras.getData().getDataSetByIndex(0);
            heroDataSet.setValues(dato_hero);
            graficoBarras.getData().notifyDataChanged();
            graficoBarras.notifyDataSetChanged();

        } else {
            heroDataSet = new BarDataSet(dato_hero, "Data Set");
            heroDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            heroDataSet.setDrawValues(true);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();

            graficoBarras.getXAxis().setValueFormatter(new IndexAxisValueFormatter(valuesX));
            dataSets.add(heroDataSet);
            BarData data = new BarData(dataSets);
            graficoBarras.setData(data); graficoBarras.setFitBars(true);
        }
        graficoBarras.invalidate();
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                solicitarDatos(name, id);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

}
