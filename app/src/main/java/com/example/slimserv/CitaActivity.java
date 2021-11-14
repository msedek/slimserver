package com.example.proyandrea;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyandrea.Interfaz.iSendCliente;
import com.example.proyandrea.Model.Cita;
import com.example.proyandrea.Model.Cliente;
import com.example.proyandrea.Model.Servicio;
import com.example.proyandrea.Model.Taller;
import com.example.proyandrea.Model.Vehiculo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CitaActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    Cliente  cliente;
    ArrayList<Taller>  talleres;
    ArrayList<Servicio> servicios;
    ArrayList<Cita> citas;
    TextView txt_fecha;
    TextView txt_hora;
    Button   btn_fecha;
    Button   btn_hora;
    Button   btn_cancelar;
    Button   btn_agendar;
    TextView txt_cliente;
    TextView txt_dni;

    Spinner spinVehiculo;
    Spinner spinLocal;
    Spinner spinServicio;

    boolean isflag_vehiculo;
    boolean isflag_taller;
    boolean isflag_fecha;
    boolean isIsflag_hora;
    boolean isCanEnviar;
    boolean isflag_servicio;

    Cita cita;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cita);



        txt_fecha    = findViewById(R.id.txt_fecha);
        txt_hora     = findViewById(R.id.txt_hora);
        btn_fecha    = findViewById(R.id.btn_fecha);
        btn_hora     = findViewById(R.id.btn_hora);
        btn_cancelar = findViewById(R.id.btn_cancelcita);
        btn_agendar  = findViewById(R.id.btn_cita);
        txt_cliente  = findViewById(R.id.txt_cliente);
        txt_dni      = findViewById(R.id.txt_dni);


        spinVehiculo = findViewById(R.id.spinVehiculo);
        spinLocal    = findViewById(R.id.spinLocal);
        spinServicio = findViewById(R.id.spinServicio);

        isflag_vehiculo = false;
        isflag_taller   = false;
        isflag_fecha    = false;
        isIsflag_hora   = false;
        isflag_servicio = false;

        getdata();
        loadVehiculos(cliente);
        talleres = new ArrayList<>();
        servicios = new ArrayList<>();
        citas = new ArrayList<>();
        loadTalleres();
        loadServicios();

        txt_dni.setText(cliente.getCli_dni());
        txt_cliente.setText(cliente.getCli_nombre() + " " + cliente.getCli_apellido());


        btn_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar now = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(CitaActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });

        btn_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(CitaActivity.this, now.get(Calendar.HOUR),now.get(Calendar.MINUTE),true);
                tpd.show(getFragmentManager(), "Timepickerdialog");

            }
        });

        btn_agendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isCanEnviar = true;

                if(isIsflag_hora && isIsflag_hora)
                {

                    Locale locale = new Locale("es","PE");
                    String curDate = txt_fecha.getText().toString() + " " + txt_hora.getText().toString();
                    SimpleDateFormat curDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", locale);
                    try {
                        Date date = curDateFormat.parse(curDate);
                        if (System.currentTimeMillis() > date.getTime()) {
                            showmessage("SELECCIONO FECHA NO VALIDA", 1500);
                        }
                        else
                        {
                            Calendar cal = Calendar.getInstance(); //Create Calendar-Object
                            cal.setTime(date);               //Set the Calendar to now
                            int hour = cal.get(Calendar.HOUR_OF_DAY); //Get the hour from the calendar
                            if(hour >= 9 && hour < 17)              // Check if hour is between 8 am and 5pm
                            {
                                if(!isflag_servicio)
                                {
                                    showmessage("Debe Seleccionar algun servicio",1500);
                                }
                                else
                                {
                                    if(!isflag_vehiculo)
                                    {
                                        showmessage("Debe Seleccionar algun Vehiculo", 1500);
                                    }
                                    else
                                    {
                                        if(!isflag_taller)
                                        {
                                            showmessage("Debe seleccionar algun taller", 1500);
                                        }
                                        else
                                        {
                                            if(!isflag_fecha)
                                            {
                                                showmessage("Debe seleccionar fecha", 1500);
                                            }
                                            else
                                            {
                                                if(!isIsflag_hora)
                                                {
                                                    showmessage("Debe seleccionar hora", 1500);
                                                }
                                                else
                                                {

                                                    cita = new Cita();

                                                    cita.setVeh_placa(spinVehiculo.getSelectedItem().toString());
                                                    cita.setServicio(spinServicio.getSelectedItem().toString());

                                                    for(Taller taller : talleres)
                                                    {
                                                        if(taller.getTal_nombre().equals(spinLocal.getSelectedItem().toString()))
                                                        {
                                                            cita.setTal_id(taller.getTal_id());
                                                            break;
                                                        }
                                                    }

                                                    cita.setFecha(curDate);

                                                    cita.setEstado_orden("pendiente");
                                                    cita.setCli_dni(cliente.getCli_dni());

                                                    showmessage("ENVIANDO", 700);

                                                    Retrofit retrofit = new Retrofit.Builder()
                                                            .baseUrl("http://www.tecfomatica.com/")
                                                            .addConverterFactory(GsonConverterFactory.create())
                                                            .build();

                                                    iSendCliente request = retrofit.create(iSendCliente.class);

                                                    Call<ArrayList<Cita>> call = request.getJSONCitas();

                                                    call.enqueue(new Callback<ArrayList<Cita>>()
                                                    {
                                                        @Override
                                                        public void onResponse(@NonNull Call<ArrayList<Cita>> call, @NonNull Response<ArrayList<Cita>> response)
                                                        {


                                                            if(response.code() == 200)
                                                            {


                                                                if(Objects.requireNonNull(response.body()).size() > 0)
                                                                {
                                                                    int cuentaCita = 1;

                                                                    for (Cita citare : Objects.requireNonNull(response.body()))
                                                                    {
                                                                        String cid = citare.getTal_id();

                                                                        if (cid.equals(cita.getTal_id()))
                                                                        {
                                                                            String cr = citare.getFecha();

                                                                            if (cr.equals(cita.getFecha()))
                                                                            {
                                                                                if (cuentaCita == 15) {

                                                                                    isCanEnviar = false;
                                                                                    break;
                                                                                }
                                                                                else
                                                                                {
                                                                                    cuentaCita++;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    if(isCanEnviar)
                                                                    {
                                                                        sendCita(cita);
                                                                        finish();
                                                                    }
                                                                    else
                                                                    {
                                                                        showmessage("EL CUPO DE CITAS DE ESTE DIA ESTA COPADO",2000);
                                                                    }

                                                                }

                                                            }
                                                            else
                                                            {
                                                                showmessage("Error inesperado en el server",700);
                                                            }

                                                        }
                                                        @Override
                                                        public void onFailure(@NonNull Call<ArrayList<Cita>> call, @NonNull Throwable t)
                                                        {

                                                            t.printStackTrace();

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                            else
                            {
                                showmessage("FUERA DE HORARIO 9 - 5",1500);
                            }
                        }
                    } catch (Exception e) {e.printStackTrace();}
                }
                else
                {
                    showmessage("Debe Seleccionar Fecha y Hora",1500);
                }

            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getdata() {
        Intent intent = getIntent();
        cliente = (Cliente) Objects.requireNonNull(intent.getExtras()).getSerializable("data");
    }

    private void showmessage(String mensaje, int tiempo) {
        final Toast toast = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, tiempo);

    }

    private void loadVehiculos(final Cliente cliente) {

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

                    assert vehiculoll != null;
                    for(Vehiculo vec : vehiculoll)
                    {
                        if(vec.getCli_dni().equals(cliente.getCli_dni()))
                        {
                            vehiculos.add(vec.getVeh_placa());
                        }
                    }

                    if(vehiculos.size() > 0) {

                        String[] varray = new String[vehiculos.size()];
                        varray = vehiculos.toArray(varray);

                        ArrayAdapter<String>  adapterVehiculo = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_row, varray);
                        spinVehiculo.setAdapter(adapterVehiculo);
                        spinVehiculo.setSelection(0);
                        isflag_vehiculo = true;


                    }
                    else
                    {
                        showmessage("Registrar Vehiculo",1000);
                    }
                }
                else
                {
                    showmessage("Error inesperado en el server",700);
                }

            }
            @Override
            public void onFailure(@NonNull Call<ArrayList<Vehiculo>> call, @NonNull Throwable t)
            {

                t.printStackTrace();

            }
        });
    }

    private void loadTalleres() {

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

                    ArrayList<String> talleresr = new ArrayList<>();

                    for(Taller taller : Objects.requireNonNull(response.body()))
                    {
                        talleresr.add(taller.getTal_nombre());
                    }

                    if(talleresr.size() > 0) {

                        talleres.addAll(response.body());

                        String[] tarray = new String[talleresr.size()];
                        tarray = talleresr.toArray(tarray);

                        ArrayAdapter<String>  adapterTaller = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_row, tarray);
                        spinLocal.setAdapter(adapterTaller);
                        spinLocal.setSelection(0);
                        isflag_taller = true;


                    }
                    else
                    {
                        showmessage("No hay talleres disponibles por el momento",1000);
                    }
                }
                else
                {
                    showmessage("Error inesperado en el server",700);
                }

            }
            @Override
            public void onFailure(@NonNull Call<ArrayList<Taller>> call, @NonNull Throwable t)
            {

                t.printStackTrace();

            }
        });
    }

    private void loadServicios() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.tecfomatica.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final iSendCliente request = retrofit.create(iSendCliente.class);

        Call<ArrayList<Servicio>> call = request.getJSONServicios();

        call.enqueue(new Callback<ArrayList<Servicio>>()
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Servicio>> call, @NonNull Response<ArrayList<Servicio>> response)
            {

                if(response.code() == 200)
                {

                    ArrayList<String> serviciosr = new ArrayList<>();

                    for(Servicio servicio : Objects.requireNonNull(response.body()))
                    {
                        serviciosr.add(servicio.getSer_nombre());
                    }

                    if(serviciosr.size() > 0) {

                        servicios.addAll(response.body());

                        String[] tarray = new String[serviciosr.size()];
                        tarray = serviciosr.toArray(tarray);

                        ArrayAdapter<String>  adapterTaller = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_row, tarray);
                        spinServicio.setAdapter(adapterTaller);
                        spinServicio.setSelection(0);
                        isflag_servicio = true;


                    }
                    else
                    {
                        showmessage("No hay talleres disponibles por el momento",1000);
                    }
                }
                else
                {
                    showmessage("Error inesperado en el server",700);
                }

            }
            @Override
            public void onFailure(@NonNull Call<ArrayList<Servicio>> call, @NonNull Throwable t)
            {

                t.printStackTrace();

            }
        });
    }

    public void sendCita(Cita cita) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.tecfomatica.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        final iSendCliente request = retrofit.create(iSendCliente.class);

        Call<Cita> call = request.addCita(cita);

        call.enqueue(new Callback<Cita>()
        {
            @Override
            public void onResponse(@NonNull Call<Cita> call, @NonNull Response<Cita> response)
            {

                if (response.code() == 200)
                {
                    showmessage("Registro Exitoso",2000);

                    finish();

                }
                else
                    showmessage(response.code() + "",1000);
            }

            @Override
            public void onFailure(@NonNull Call<Cita> call, @NonNull Throwable t)
            {

                t.printStackTrace();
            }
        });
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        String time = hourOfDay+":"+minute;
        txt_hora.setText(time);
        isIsflag_hora = true;

    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        txt_fecha.setText(date);
        isflag_fecha = true;
    }
}






