package com.example.proyandrea;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyandrea.Interfaz.iSendCliente;
import com.example.proyandrea.Model.Cliente;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegActivity extends AppCompatActivity {

    EditText edt_nombre;
    EditText edt_apellido;
    EditText edt_mail;
    EditText edt_cel;
    EditText edt_dni;
    EditText edt_pass;
    EditText edt_passc;
    EditText edt_placas;
    Button   btn_save;
    Button   btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);


        edt_nombre   = findViewById(R.id.edt_nombre);
        edt_apellido = findViewById(R.id.edt_apellido);
        edt_mail     = findViewById(R.id.edt_mail);
        edt_cel      = findViewById(R.id.edt_cel);
        edt_dni      = findViewById(R.id.edt_dni);
        edt_pass     = findViewById(R.id.edt_pass);
        edt_passc     = findViewById(R.id.edt_passc);
        edt_placas   = findViewById(R.id.edt_placas);
        btn_save     = findViewById(R.id.btn_save);
        btn_cancel   = findViewById(R.id.btn_cancel);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String test = edt_nombre.getText().toString().trim();

                if(test.length() < 3 || test.matches(".*\\d+.*"))
                {
                    showmessage("ESCRIBA NOMBRE VALIDO");
                }
                else
                {
                    test = edt_apellido.getText().toString().trim();

                    if(test.length() < 3 || test.matches(".*\\d+.*"))
                    {
                        showmessage("ESCRIBA APELLIDO VALIDO");
                    }
                    else
                    {
                        test = edt_mail.getText().toString().trim();

                        if(!isValidEmail(test))
                        {
                            showmessage("E-MAIL INVALIDO");
                        }
                        else
                        {
                            test = edt_cel.getText().toString().trim();

                            if(test.length() < 9)
                            {
                                showmessage("NUMERO CELULAR INVALIDO");
                            }
                            else
                            {
                                test = edt_dni.getText().toString().trim();

                                if(test.length() < 8)
                                {
                                    showmessage("DNI INVALIDO");
                                }
                                else
                                {
                                    test = edt_pass.getText().toString().trim();

                                    if(test.length() < 8)
                                    {
                                        showmessage("PASSWORD DEBE SER DE 8 CARACTERES");
                                    }
                                    else
                                    {
                                        test = edt_passc.getText().toString().trim();

                                        if(!test.equals(edt_pass.getText().toString()))
                                        {
                                            showmessage("PASSWORD NO COINCIDE");
                                        }
                                        else
                                        {
                                            showmessage("REGISTRANDO USUARIO");

                                            Cliente cliente = new Cliente();

                                            cliente.setCli_nombre(edt_nombre.getText().toString());
                                            cliente.setCli_apellido(edt_apellido.getText().toString());
                                            cliente.setCli_correo(edt_mail.getText().toString());
                                            cliente.setCli_celular(edt_cel.getText().toString());
                                            cliente.setCli_dni(edt_dni.getText().toString());
                                            cliente.setCli_contrasena(edt_pass.getText().toString());

                                            sendCliente(cliente);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void sendCliente(Cliente cliente)
    {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.tecfomatica.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        iSendCliente request = retrofit.create(iSendCliente.class);

        Call<Cliente> call = request.addCliente(cliente);

        call.enqueue(new Callback<Cliente>()
        {
            @Override
            public void onResponse(@NonNull Call<Cliente> call, @NonNull Response<Cliente> response)
            {

                if (response.code() == 200)
                {
                    showmessage("Registro Exitoso");

                    finish();

                }
                else
                {
                    if(response.code() == 500)
                    {
                        showmessage("CLIENTE YA EXISTE");
                        edt_dni.requestFocus();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Cliente> call, @NonNull Throwable t)
            {

                t.printStackTrace();
            }
        });
    }


}
