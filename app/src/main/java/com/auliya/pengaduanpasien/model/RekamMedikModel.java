package com.auliya.pengaduanpasien.model;

public class RekamMedikModel {
    private int  id_rekam;
    private String no_rm, tgl_lahir, diagnosa, jenis_obat, no_jaminan, created_at;

    public String getNo_jaminan() {
        return no_jaminan;
    }

    public void setNo_jaminan(String no_jaminan) {
        this.no_jaminan = no_jaminan;
    }

    public int getId_rekam() {
        return id_rekam;
    }

    public void setId_rekam(int id_rekam) {
        this.id_rekam = id_rekam;
    }

    public String getNo_rm() {
        return no_rm;
    }

    public void setNo_rm(String no_rm) {
        this.no_rm = no_rm;
    }

    public String getTgl_lahir() {
        return tgl_lahir;
    }

    public void setTgl_lahir(String tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }

    public String getDiagnosa() {
        return diagnosa;
    }

    public void setDiagnosa(String diagnosa) {
        this.diagnosa = diagnosa;
    }

    public String getJenis_obat() {
        return jenis_obat;
    }

    public void setJenis_obat(String jenis_obat) {
        this.jenis_obat = jenis_obat;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
