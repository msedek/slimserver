package com.example.proyandrea.Model;

public class Taller {

    private String tal_id;
    private String tal_nombre;
    private String tal_direccion;
    private String cli_dni;

    public String getTal_id() {
        return tal_id;
    }

    public void setTal_id(String tal_id) {
        this.tal_id = tal_id;
    }

    public String getTal_nombre() {
        return tal_nombre;
    }

    public void setTal_nombre(String tal_nombre) {
        this.tal_nombre = tal_nombre;
    }

    public String getTal_direccion() {
        return tal_direccion;
    }

    public void setTal_direccion(String tal_direccion) {
        this.tal_direccion = tal_direccion;
    }

    public String getCli_dni() {
        return cli_dni;
    }

    public void setCli_dni(String cli_dni) {
        this.cli_dni = cli_dni;
    }
}
