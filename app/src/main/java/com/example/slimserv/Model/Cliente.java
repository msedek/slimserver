package com.example.proyandrea.Model;

import java.io.Serializable;

public class Cliente implements Serializable {


    private String cli_dni;
    private String cli_nombre;
    private String cli_apellido;
    private String cli_contrasena;
    private String cli_correo;
    private String cli_celular;

    public String getCli_dni() {
        return cli_dni;
    }

    public void setCli_dni(String cli_dni) {
        this.cli_dni = cli_dni;
    }

    public String getCli_nombre() {
        return cli_nombre;
    }

    public void setCli_nombre(String cli_nombre) {
        this.cli_nombre = cli_nombre;
    }

    public String getCli_apellido() {
        return cli_apellido;
    }

    public void setCli_apellido(String cli_apellido) {
        this.cli_apellido = cli_apellido;
    }

    public String getCli_contrasena() {
        return cli_contrasena;
    }

    public void setCli_contrasena(String cli_contrasena) {
        this.cli_contrasena = cli_contrasena;
    }

    public String getCli_correo() {
        return cli_correo;
    }

    public void setCli_correo(String cli_correo) {
        this.cli_correo = cli_correo;
    }

    public String getCli_celular() {
        return cli_celular;
    }

    public void setCli_celular(String cli_celular) {
        this.cli_celular = cli_celular;
    }

}
