package com.aeeehooo.flask_integration.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class NewConsoleActivity extends AppCompatActivity {

    private EditText editName, editYear, editPrice, editGames;
    private CheckBox editActive;
    private long id;
    private Console console;
    private boolean checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_console);

        editName = findViewById(R.id.editName);
        editYear = findViewById(R.id.editYear);
        editPrice = findViewById(R.id.editPrice);
        editGames = findViewById(R.id.editGames);
        editActive = findViewById(R.id.editActive);

        id = getIntent().getLongExtra("ID",0);

        checked=false;
        editActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checked=isChecked;
                }else{
                    checked=isChecked;
                }
            }
        });

        if(id != 0){
            loadConsole();
        }

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

                    checked=console.getActive();

                    editName.setText(console.getName());
                    editYear.setText(String.valueOf(console.getYear()));
                    editPrice.setText(String.valueOf(console.getPrice()));
                    editGames.setText(String.valueOf(console.getGames()));
                    editActive.setChecked(console.getActive());

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

    private void createConsole(String url, final int method){

        if(editName.toString().isEmpty()){
            editName.setError("O nome não pode estar vazio");
            editName.requestFocus();
            return;
        }
        if(editYear.toString().isEmpty()){
            editYear.setError("O ano não pode estar vazio");
            editYear.requestFocus();
            return;
        }
        if(editPrice.toString().isEmpty()){
            editPrice.setError("O Preço não pode estar vazio");
            editPrice.requestFocus();
            return;
        }
        if(editGames.toString().isEmpty()){
            editGames.setError("O numero de jogos não pode estar vazio");
            editGames.requestFocus();
            return;
        }

        JSONObject object = new JSONObject();

        try {
            object.put("name",editName.getText().toString().toUpperCase());
            object.put("year",Integer.parseInt(editYear.getText().toString()));
            object.put("price",Double.parseDouble(editPrice.getText().toString()));
            object.put("games",Integer.parseInt(editGames.getText().toString()));
            object.put("active", checked);

            JsonObjectRequest request = new JsonObjectRequest(method, url, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = "";
                        if(method == Request.Method.POST)
                            message = "Console "+response.getString("name")+" salvo com sucesso!";
                        else
                            message = "Console "+response.getLong("id")+" atualizado com sucesso!";
                        Toast.makeText(NewConsoleActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent main = new Intent(NewConsoleActivity.this,MainActivity.class);
                        startActivity(main);
                        finish();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveConsole(View view){
        String url = "http://10.0.2.2:5000/api/console";
        if(id != 0)
            createConsole(url+"/"+id,Request.Method.PUT);
        else
            createConsole(url,Request.Method.POST);
    }
}