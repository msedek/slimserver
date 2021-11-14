package com.example.proyandrea;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyandrea.Interfaz.iSendCliente;
import com.example.proyandrea.Model.Taller;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultallerActivity extends AppCompatActivity {

    Spinner spinner2;
    Button btn_regv2;

    ArrayList<Taller> talleres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultaller);

        spinner2  = findViewById(R.id.spinner2);
        btn_regv2 = findViewById(R.id.btn_regv2);


        loadTalleres();

        btn_regv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadTalleres()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.tecfomatica.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final iSendCliente request = retrofit.create(iSendCliente.class);

        Call<ArrayList<Taller>> call = request.getJSONTalleres();

        call.enqueue(new Callback<ArrayList<Taller>>()
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Taller>> call, @NonNull Response<ArrayList<Taller>> response)
            {

                if(response.code() == 200)
                {

                    talleres = response.body();

                    ArrayList<String> talleresr = new ArrayList<>();

                    if(talleres.size() > 0)
                    {

                        for(Taller ta : talleres)
                        {
                            talleresr.add(ta.getTal_id() + " " + ta.getTal_direccion());
                        }

                        String[] tarray = new String[talleresr.size()];
                        tarray = talleresr.toArray(tarray);
                        ArrayAdapter<String>  adapterTaller = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_row, tarray);
                        spinner2.setAdapter(adapterTaller);
                         spinner2.setSelection(0);

                    }


                    else
                    {
                        showmessage("No hay talleres disponibles por el momento");
                    }
                }
                else
                {
                    showmessage("Error inesperado en el server");
                }

            }
            @Override
            public void onFailure(@NonNull Call<ArrayList<Taller>> call, @NonNull Throwable t)
            {

                t.printStackTrace();

            }
        });
    }


    private void showmessage(String mensaje)
    {
        final Toast toast = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1500);

    }

}
