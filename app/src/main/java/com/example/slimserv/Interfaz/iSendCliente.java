package com.example.proyandrea.Interfaz;

import com.example.proyandrea.Model.Cita;
import com.example.proyandrea.Model.Cliente;
import com.example.proyandrea.Model.Servicio;
import com.example.proyandrea.Model.Taller;
import com.example.proyandrea.Model.Vehiculo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface iSendCliente
{
    @GET("api/cliente/{cli_dni}")
    Call<Cliente> getJSONClienteid(@Path("cli_dni") String cli_dni);

    @GET("api/vehiculos")
    Call<ArrayList<Vehiculo>> getJSONVehiculos();

    @Headers({"CONNECT_TIMEOUT:10000", "READ_TIMEOUT:10000", "WRITE_TIMEOUT:10000"})
    @GET("api/citas")
    Call<ArrayList<Cita>> getJSONCitas();

    @GET("api/talleres")
    Call<ArrayList<Taller>> getJSONTalleres();

    @GET("api/servicios")
    Call<ArrayList<Servicio>> getJSONServicios();

/*    @GET("api/clientes")
    Call<ArrayList<Cliente>> getJSONClientes();*/

    @POST("api/cliente/agregar")
    Call<Cliente> addCliente(@Body Cliente cliente);

    @POST("api/cita/agregar")
    Call<Cita> addCita(@Body Cita Cita);

    @POST("api/vehiculo/agregar")
    Call<Vehiculo> addVehiculo(@Body Vehiculo vehiculo);

    @PUT("api/cliente/actualizar/{cli_dni}")
    Call<Cliente> updateCliente(@Path("cli_dni") String cli_dni, @Body Cliente cliente);

/*    @DELETE("api/cliente/borrar/{cli_dni}")
    Call<Cliente> deleteCliente(@Path("cli_dni") String cli_dni);*/
}
