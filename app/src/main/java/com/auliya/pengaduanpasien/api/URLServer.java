package com.auliya.pengaduanpasien.api;

public class URLServer {
    public static final String BASE_URL = "http://rekdikpepas.my.id/";
    public static final String URL_IMAGE = "http://rekdikpepas.my.id/assets/images/";
    public static final String URL_IMAGE_ADUAN = "http://rekdikpepas.my.id/assets/images/pengaduan/";
    public static final String API = "api/";
    public static final String LOGIN = BASE_URL + API + "auth/login";
    public static final String LOGOUT = BASE_URL + API + "auth/logout";
    public static final String REGISTER = BASE_URL + API + "auth/register";
    public static final String UPDATE_PROFILE = BASE_URL + API + "user/profil";
    public static final String UPDATE_PASSWORD = BASE_URL + API + "user/password";
    public static final String KIRIM_ADUAN = BASE_URL + API + "pengaduan/kirim";
    public static final String GETPENGADUAN = BASE_URL + API + "pengaduan?user_id=";
    public static final String GETPENGADUANID = BASE_URL + API + "pengaduan?id=";
    public static final String GETREKAMMEDIK = BASE_URL + API + "rekammedik";
    public static final String GETREKAMMEDIKID = BASE_URL + API + "rekammedik?id_rekam=";
    public static final String POSTGAMBARPFORILE = BASE_URL + API + "user/upload";
    public static final String POSTGAMBARADUAN = BASE_URL + API + "pengaduan/upload";
    public static final String GETDETAIL = BASE_URL + API + "pengaduan/detail?user_id=";
    public static final String GETUSER = BASE_URL + API + "user?id_regis=";
}
