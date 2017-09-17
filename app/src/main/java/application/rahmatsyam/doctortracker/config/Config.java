package application.rahmatsyam.doctortracker.config;

/**
 * Created by Rahmat Syam on 3/5/2017.
 */

public class Config {
    //class NavigasiDokter
    public static final String KEY_TOTAL_PASIEN = "total_pasien";
    public static final String TOTAL_ARRAY      = "result";
    public static final String TOTAL_URL        = "http://appdrtracker.16mb.com/doctortracker/totalPasien.php?iddokter=";


    //class NavigasiPasien
    public static final String DATA_URL        = "http://appdrtracker.16mb.com/doctortracker/nomorAntrian.php?nohp_pasien=";
    public static final String KEY_NO_ANTRIAN  = "no_antrian";
    public static final String KEY_NAMA_PASIEN = "nama_pasien";
    public static final String KEY_NAMA_DOKTER = "nama_dokter";
    public static final String JSON_ARRAY      = "result";
    public static final String KEY_LONGITUDE   = "longitude";
    public static final String KEY_LATITUDE    = "latitude";

    //class DaftarDokter
    public static final String DAFTAR_DOKTER = "http://appdrtracker.16mb.com/doctortracker/registerdokter.php";

    //class LoginDokter
    public static final String LOGIN_DOKTER =  "http://appdrtracker.16mb.com/doctortracker/logindokter.php?";

    //class DaftarPasien
    public static final String REGISTER_PASIEN =  "http://appdrtracker.16mb.com/doctortracker/registerpasien.php";

    //class LoginPasien
    public static final String LOGIN_PASIEN = "http://appdrtracker.16mb.com/doctortracker/loginpasien.php?user=";

    //class ListDokter
    public static final String LIST_DOKTER = "http://appdrtracker.16mb.com/doctortracker/listdokter.php";

    //class ListPasien
    public static final String LIST_PASIEN = "http://appdrtracker.16mb.com/doctortracker/listpasien.php";

    //class LokasiPraktik
    public static final String LOKASI_PRAKTIK = "http://appdrtracker.16mb.com/doctortracker/lokasipraktek.php";

    //class ProfilDokter
    public static final String DAFTAR_PASIEN = "http://appdrtracker.16mb.com/doctortracker/daftarpasien.php?";
    public static final String DAFTAR_DIRI   = "http://appdrtracker.16mb.com/doctortracker/daftardiri.php?";

    //class TambahLokasi
    public static final String TAMBAH_LOKASI =  "http://appdrtracker.16mb.com/doctortracker/tambahlokasi.php";

    //class PasienAdapter
    public static final String HAPUS_PASIEN = "http://appdrtracker.16mb.com/doctortracker/hapusPasien.php?iddokter=";

}