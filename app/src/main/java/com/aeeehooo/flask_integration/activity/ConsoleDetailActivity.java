package com.aeeehooo.flask_integration.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.aeeehooo.flask_integration.R;
import com.aeeehooo.flask_integration.model.Console;
import com.aeeehooo.flask_integration.util.APISingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class ConsoleDetailActivity extends AppCompatActivity {

    private TextView textName, textYear, textPrice, textGames, textActive;
    private long id;
    private Console console;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console_detail);

        id = getIntent().getLongExtra("ID",0);

        textName = findViewById(R.id.textName);
        textYear = findViewById(R.id.textYear);
        textPrice = findViewById(R.id.textPrice);
        textGames = findViewById(R.id.textGames);
        textActive = findViewById(R.id.textActive);
    }

    @Override
    protected void onStart(){
        super.onStart();
        loadConsole();
    }

    private void loadConsole() {
        String url = "http://10.0.2.2:5000/api/console/"+id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                console = new Console();
                try {
                    console.setId(response.getLong("id"));
                    console.setName(response.getString("name"));
                    console.setYear(response.getInt("year"));
                    console.setPrice(response.getDouble("price"));
                    console.setGames(response.getInt("games"));
                    console.setActive(response.getBoolean("active"));

                    textName.setText(console.getName());
                    textYear.setText(String.valueOf(console.getYear()));
                    textPrice.setText(String.valueOf(console.getPrice()));
                    textGames.setText(String.valueOf(console.getGames()));

                    if(console.getActive()){
                        textActive.setText("SIM");
                    }else{
                        textActive.setText("NÃO");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        APISingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public void updateConsole(View view) {
        Intent newConsole = new Intent(ConsoleDetailActivity.this,NewConsoleActivity.class);
        newConsole.putExtra("ID",console.getId());
        startActivity(newConsole);
    }

    public void deleteConsole(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja excluir o console "+console.getId()+"?");
        builder.setTitle("Excluir");
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String url = "http://10.0.2.2:5000/api/console/"+id;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(ConsoleDetailActivity.this, "Console "+response.getLong("id")+" removido.", Toast.LENGTH_SHORT).show();
                            Intent main = new Intent(ConsoleDetailActivity.this,MainActivity.class);
                            startActivity(main);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                APISingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
        });
        builder.setNegativeButton("NÃO",null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}