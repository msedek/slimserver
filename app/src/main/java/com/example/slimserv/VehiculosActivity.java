package com.example.proyandrea;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyandrea.Interfaz.iSendCliente;
import com.example.proyandrea.Model.Cliente;
import com.example.proyandrea.Model.Vehiculo;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VehiculosActivity extends AppCompatActivity {

    Spinner spinner;
    Button  btn_regv;
    Button  btn_regv3;
    Cliente cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos);

        spinner  = findViewById(R.id.spinner);
        btn_regv = findViewById(R.id.btn_regv);
        btn_regv3 = findViewById(R.id.btn_regv3);

        getdata();
        //loadVehiculos(cliente);

        btn_regv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegvehiculoActivity.class);
                intent.putExtra("data", cliente);
                startActivity(intent);
            }
        });

        btn_regv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

    }

    private void loadVehiculos(final Cliente cliente)
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.tecfomatica.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final iSendCliente request = retrofit.create(iSendCliente.class);

        Call<ArrayList<Vehiculo>> call = request.getJSONVehiculos();

        call.enqueue(new Callback<ArrayList<Vehiculo>>()
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Vehiculo>> call, @NonNull Response<ArrayList<Vehiculo>> response)
            {

                if(response.code() == 200)
                {

                    ArrayList<Vehiculo> vehiculoll = response.body();
                    ArrayList<String> vehiculos = new ArrayList<>();

                    for(Vehiculo vec : vehiculoll)
                    {
                        if(vec.getCli_dni().equals(cliente.getCli_dni()))
                        {
                            vehiculos.add(vec.getVeh_marca() + " " + vec.getVeh_modelo() + " " + vec.getVeh_placa());
                        }
                    }

                    if(vehiculos.size() > 0) {

                        String[] varray = new String[vehiculos.size()];
                        varray = vehiculos.toArray(varray);

                        ArrayAdapter<String> adapterVehiculo = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_row, varray);
                        spinner.setAdapter(adapterVehiculo);
                        spinner.setSelection(0);

                    }
                    else
                    {
                        showmessage("Registrar Vehiculo");
                    }
                }
                else
                {
                    showmessage("Error inesperado en el server");
                }

            }
            @Override
            public void onFailure(@NonNull Call<ArrayList<Vehiculo>> call, @NonNull Throwable t)
            {

                t.printStackTrace();

            }
        });
    }

    private void getdata()
    {
        Intent intent = getIntent();
        cliente = (Cliente) Objects.requireNonNull(intent.getExtras()).getSerializable("data");
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

    @Override
    protected void onResume() {
        super.onResume();

        spinner.setAdapter(null);

        loadVehiculos(cliente);

    }
}
