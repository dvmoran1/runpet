package com.example.aplicacionmoviles.models;

public class Paseo {

    private String id;
    private String idDuenio;
    private String nomPaseador;
    private String nomMascota;
    private String nomDuenio;
    private String dateWalk;
    private long distance;
    private int status;
    private double longitude, latitude;


    public Paseo() {}

    public String getIdDuenio() {
        return idDuenio;
    }

    public void setIdDuenio(String idDuenio) {
        this.idDuenio = idDuenio;
    }

    public String getNomPaseador() {
        return nomPaseador;
    }

    public void setNomPaseador(String nomPaseador) {
        this.nomPaseador = nomPaseador;
    }

    public String getNomMascota() {
        return nomMascota;
    }

    public void setNomMascota(String nomMascota) {
        this.nomMascota = nomMascota;
    }

    public String getDateWalk() {
        return dateWalk;
    }

    public void setDateWalk(String dateWalk) {
        this.dateWalk = dateWalk;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getNomDuenio() {
        return nomDuenio;
    }

    public void setNomDuenio(String nomDuenio) {
        this.nomDuenio = nomDuenio;
    }
}
