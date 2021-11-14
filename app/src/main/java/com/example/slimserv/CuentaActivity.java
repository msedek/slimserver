package com.example.proyandrea;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyandrea.Interfaz.iSendCliente;
import com.example.proyandrea.Model.Cita;
import com.example.proyandrea.Model.Cliente;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.design.widget.TabLayout.GRAVITY_CENTER;

public class CuentaActivity   extends AppCompatActivity {

    Cliente      cliente;
    Button       btn_perfil;
    Button       btn_cita;
    Button       btn_vehiculo;
    Button       btn_locales;
    LinearLayout lyo_historial;
    Button       btn_salir;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);

        btn_perfil    = findViewById(R.id.btn_perfil);
        btn_cita      = findViewById(R.id.btn_cita);
        btn_vehiculo  = findViewById(R.id.btn_vehiculo);
        btn_locales   = findViewById(R.id.btn_locales);
        lyo_historial = findViewById(R.id.lyo_historial);
        btn_salir     = findViewById(R.id.btn_salir);


        getdata();
        showmessage("BIENVENIDO");

        btn_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PerfilActivity.class);
                intent.putExtra("data", cliente);
                startActivity(intent);
            }
        });

        btn_cita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CitaActivity.class);
                intent.putExtra("data", cliente);
                startActivity(intent);
            }
        });

        btn_vehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VehiculosActivity.class);
                intent.putExtra("data", cliente);
                startActivity(intent);
            }
        });

        btn_locales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConsultallerActivity.class);
                startActivity(intent);
            }
        });

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


/*        lyo_historial.removeAllViews();
        loadServicios();*/
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

    private void loadServicios()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.tecfomatica.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final iSendCliente request = retrofit.create(iSendCliente.class);

        Call<ArrayList<Cita>> call = request.getJSONCitas();

        call.enqueue(new Callback<ArrayList<Cita>>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ArrayList<Cita>> call, @NonNull Response<ArrayList<Cita>> response)
            {

                if(response.code() == 200)
                {

                    LinearLayout.LayoutParams params;
                    TextView textView;

                    if (response.body().size() > 0)
                    {

                        for(Cita cita : response.body())
                        {
                            if(cita.getCli_dni().equals(cliente.getCli_dni()))
                            {
                                textView = new TextView(getApplicationContext());
                                textView.setTextColor(Color.BLACK);
                                params =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                textView.setGravity(GRAVITY_CENTER);
                                textView.setLayoutParams(params);
                                textView.setText(cita.getVeh_placa() + " " + cita.getServicio() + " " +cita.getEstado_orden() + " " + cita.getTal_id());
                                lyo_historial.addView(textView);
                            }

                        }
                    }
                    else
                    {
                        showmessage("no hay citas pendientes");
                    }



                }
                else
                {
                    showmessage("Error inesperado en el server");
                }

            }
            @Override
            public void onFailure(@NonNull Call<ArrayList<Cita>> call, @NonNull Throwable t)
            {

                t.printStackTrace();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        lyo_historial.removeAllViews();
        loadServicios();


    }
}
