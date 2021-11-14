package com.example.proyandrea;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyandrea.Interfaz.iSendCliente;
import com.example.proyandrea.Model.Cliente;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText edt_dni;
    EditText edt_pass;
    Button   btn_ingre;
    TextView txt_reg;
    TextView txt_forgot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edt_dni    = findViewById(R.id.edt_dni);
        edt_pass   = findViewById(R.id.edt_pass);
        btn_ingre  = findViewById(R.id.btn_ingre);
        txt_reg    = findViewById(R.id.txt_reg);
        txt_forgot = findViewById(R.id.txt_forgot);


        btn_ingre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String test = edt_dni.getText().toString().trim();

                if(test.length() < 8)
                {
                    showmessage("DNI INVALIDO");
                }
                else
                {
                    test = edt_pass.getText().toString().trim();

                    if (test.length() < 8) {
                        showmessage("PASSWORD DEBE SER DE 8 CARACTERES");
                    }
                    else
                    {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://www.tecfomatica.com/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        iSendCliente request = retrofit.create(iSendCliente.class);

                        Call<Cliente> call = request.getJSONClienteid(edt_dni.getText().toString()); //VERIFICAR SI CORREO EXISTE

                        call.enqueue(new Callback<Cliente>() {
                            @Override
                            public void onResponse(@NonNull Call<Cliente> call, @NonNull Response<Cliente> response) {

                                if (response.code() == 200)//VERIFICAMOS RECEPCION CORRECTA DE GET
                                {
                                    Cliente cliente = response.body();

                                    assert cliente != null;
                                    if(cliente.getCli_contrasena().equals(edt_pass.getText().toString()))
                                    {
                                        Intent intent = new Intent(getApplicationContext(), CuentaActivity.class);
                                        intent.putExtra("data", cliente);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        showmessage("PASSWORD NO VALIDO");
                                        edt_pass.requestFocus();
                                    }

                                }
                                else
                                {
                                    if(response.code() == 500)
                                    {
                                        showmessage("DNI NO REGISTRADO");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Cliente> call, @NonNull Throwable t) {

                                t.printStackTrace();

                            }
                        });
                    }
                }
            }
        });


        txt_reg.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), RegActivity.class);
                startActivity(intent);
            }
        });

        txt_forgot.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                String test = edt_dni.getText().toString().trim();

                if(test.length() < 8)//VERIFICAMOS QUE HAYA UN DNI VALIDO ESCRITO EN LA CAJA DNI
                {
                    showmessage("DNI INVALIDO");
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), ForgotActivity.class);

                    String dni = edt_dni.getText().toString();
                    intent.putExtra("dni", dni);//PASAMOS EL DNI A LA ACTIVIDAD FORGOT PARA VERIFICAR USUARIO REGISTRADO

                    startActivity(intent);
                }
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
        }, 1000);

    }

}