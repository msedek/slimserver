package com.example.proyandrea.Model;

import java.io.Serializable;

public class Vehiculo implements Serializable {

        private String veh_placa;
        private String veh_marca;
        private String veh_modelo;
        private String veh_afab;
        private String cli_dni;

    public String getVeh_placa() {
        return veh_placa;
    }

    public void setVeh_placa(String veh_placa) {
        this.veh_placa = veh_placa;
    }

    public String getVeh_marca() {
        return veh_marca;
    }

    public void setVeh_marca(String veh_marca) {
        this.veh_marca = veh_marca;
    }

    public String getVeh_modelo() {
        return veh_modelo;
    }

    public void setVeh_modelo(String veh_modelo) {
        this.veh_modelo = veh_modelo;
    }

    public String getVeh_afab() {
        return veh_afab;
    }

    public void setVeh_afab(String veh_afab) {
        this.veh_afab = veh_afab;
    }

    public String getCli_dni() {
        return cli_dni;
    }

    public void setCli_dni(String cli_dni) {
        this.cli_dni = cli_dni;
    }
}
