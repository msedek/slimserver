package com.example.proyandrea;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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

import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotActivity extends AppCompatActivity {

    EditText edt_rmail;
    Button   btn_rpass;
    Button   btn_fcancel;
    String   dni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        edt_rmail   = findViewById(R.id.edt_rmail);
        btn_rpass   = findViewById(R.id.btn_rpass);
        btn_fcancel = findViewById(R.id.btn_fcancel);


        Intent intent = getIntent();
        dni= Objects.requireNonNull(intent.getExtras()).getString("dni");//TOMAMOS EL DNI EN LA PANTALLA DE LOGIN


        //ENVIAR CORREO AL PRESIONAR EL BOTON
        btn_rpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String test = edt_rmail.getText().toString().trim();  //LEER LA CAJA DE TEXTO PARA COMPROBAR EL EMAIL

                if(!isValidEmail(test))
                {
                    showmessage("E-MAIL INVALIDO");
                }
                else
                {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.tecfomatica.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    iSendCliente request = retrofit.create(iSendCliente.class);

                    Call<Cliente> call = request.getJSONClienteid(dni); //VERIFICAR SI CORREO EXISTE

                    call.enqueue(new Callback<Cliente>() {
                        @Override
                        public void onResponse(@NonNull Call<Cliente> call, @NonNull Response<Cliente> response) {

                            if (response.code() == 200)//VERIFICAMOS RECEPCION CORRECTA DE GET
                            {
                                Cliente cliente = response.body();

                                assert cliente != null;
                                if (cliente.getCli_correo().equals(edt_rmail.getText().toString()))
                                {
                                    //TODO SEND EMAIL CON EL PASSWORD EN cliente_getpass
                                    showmessage("ENVIANDO EL PASSWORD A SU CORREO");

                                    boolean hayinternet = isOnline(); //VERIFICAMOS SI HAY INTERNET

                                    if(hayinternet)
                                    {

                                        ///ENVIAR CORREO AL CLIENTE CON EL PASSWORD

                                        final String username = "recuperarcontrasenas@tecfomatica.com";
                                        final String password = "andreaandrea";

                                        Properties props = new Properties();
                                        props.put("mail.smtp.auth", "true");
                                        props.put("mail.smtp.starttls.enable", "true");
                                        props.put("mail.smtp.host", "mail.tecfomatica.com");
                                        props.put("mail.debug", "true");
                                        props.put("mail.smtp.port", "587");

                                        Session session = Session.getInstance(props,
                                                new javax.mail.Authenticator() {
                                                    protected PasswordAuthentication getPasswordAuthentication() {
                                                        return new PasswordAuthentication(username, password);
                                                    }
                                                });
                                        try {
                                            Message message = new MimeMessage(session);
                                            message.setFrom(new InternetAddress("recuperarcontrasenas@tecfomatica.com"));
                                            message.setRecipients(Message.RecipientType.TO,
                                                    InternetAddress.parse(cliente.getCli_correo()));
                                            message.setSubject("Recuperacion de Password");
                                            message.setText("Su password es: " + cliente.getCli_contrasena() + " "
                                                    + "\n\n No responda a este correo");

/*                                            MimeBodyPart messageBodyPart = new MimeBodyPart();

                                            Multipart multipart = new MimeMultipart();

                                            messageBodyPart = new MimeBodyPart();
                                            String file = "path of file to be attached";
                                            String fileName = "attachmentName";
                                            DataSource source = new FileDataSource(file);
                                            messageBodyPart.setDataHandler(new DataHandler(source));
                                            messageBodyPart.setFileName(fileName);
                                            multipart.addBodyPart(messageBodyPart);

                                            message.setContent(multipart);*/

                                            Transport.send(message);

                                            System.out.println("Done");

                                        } catch (MessagingException e) {
                                            throw new RuntimeException(e);
                                        }

                                        showmessage("SE ENVIADO UN CORREO CON SU PASSWORD");

                                        finish();
                                    }
                                    else
                                    {
                                        showmessage("VERIFIQUE SU CONEXION A INTERNET");
                                    }
                                }
                                else
                                {
                                    showmessage("EL CORREO NO CORRESPONDE CON EL REGISTRADO");
                                    edt_rmail.requestFocus();
                                }
                            }
                            else
                            {
                                if(response.code() == 500)
                                {
                                    showmessage("DNI NO REGISTRADO");
                                    finish();
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
        });

        btn_fcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}



