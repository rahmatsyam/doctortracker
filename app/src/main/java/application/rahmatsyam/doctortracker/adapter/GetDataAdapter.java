package application.rahmatsyam.doctortracker.adapter;

/**
 * Created by Rahmat Syam on 31/1/2017.
 */

public class GetDataAdapter {

    private String nama_pasien, nohp_pasien, tgl_registrasi, no_antrian;// variabel pasien
    private String id_dokter,id_lokasi,nama_dokter, nama_spesialisasi, nama_praktek, alamat_praktek, noTlpn_praktek, hari_praktek,jam_buka,jam_tutup, layanan;  // variabel dokter


    //untuk data list pasien
    public String getNama_pasien() {

        return nama_pasien;
    }

    public void setNama_pasien(String nama_pasien) {

        this.nama_pasien = nama_pasien;
    }


    public String getNohp_pasien() {

        return nohp_pasien;
    }

    public void setNohp_pasien(String nohp_pasien1) {

        this.nohp_pasien = nohp_pasien1;
    }

    String getTgl_registrasi() {

        return tgl_registrasi;
    }

    public void setTgl_registrasi(String tgl_registrasi) {

        this.tgl_registrasi = tgl_registrasi;
    }

    String getNo_antrian(){

        return no_antrian;
    }

    public void setNo_antrian(String no_antrian){

        this.no_antrian = no_antrian;
    }


    //untuk list dokter

   String getId_dokter() {

        return id_dokter;
    }

    public void setId_dokter(String id_dokter){

        this.id_dokter = id_dokter;
    }

     String getId_lokasi(){

        return id_lokasi;
    }

    public void setId_lokasi(String id_lokasi){

        this.id_lokasi = id_lokasi;
    }


    public String getNama_dokter() {

        return nama_dokter;
    }

    public void setNama_dokter(String nama_dokter) {

        this.nama_dokter = nama_dokter;
    }

    public String getNama_spesialisasi() {

        return nama_spesialisasi;
    }

    public void setNama_spesialisasi(String nama_spesialisasi) {

        this.nama_spesialisasi = nama_spesialisasi;
    }

    String getNama_praktek() {

        return nama_praktek;
    }

    public void setNama_praktek(String nama_praktek) {

        this.nama_praktek = nama_praktek;
    }

    String getAlamat_praktek() {

        return alamat_praktek;
    }

    public void setAlamat_praktek(String alamat_praktek) {

        this.alamat_praktek = alamat_praktek;
    }

    String getNoTlpn_praktek() {

        return noTlpn_praktek;
    }

    public void setNoTlpn_praktek(String noTlpn_praktek) {

        this.noTlpn_praktek = noTlpn_praktek;
    }

    String getHari_praktek() {

        return hari_praktek;
    }

   public void setHari_praktek(String hari_praktek) {

        this.hari_praktek = hari_praktek;
    }

    String getJam_buka(){

        return jam_buka;
    }

    public void setJam_buka(String jam_buka) {

        this.jam_buka = jam_buka;
    }

    String getJam_tutup(){

        return jam_tutup;
    }

    public void setJam_tutup(String jam_tutup){

        this.jam_tutup = jam_tutup;
    }

    String getLayanan(){

        return layanan;
    }

    public void setLayanan(String layanan){

        this.layanan = layanan;
    }


}