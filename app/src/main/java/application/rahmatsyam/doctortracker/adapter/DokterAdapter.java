package application.rahmatsyam.doctortracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import application.rahmatsyam.doctortracker.R;
import application.rahmatsyam.doctortracker.ProfilDokter;

/**
 * Created by Rahmat Syam on 31/1/2017.
 */

public class DokterAdapter extends RecyclerView.Adapter<DokterAdapter.ViewHolder> {


    public Context context;

    private List<GetDataAdapter> getDataAdapter;


    public DokterAdapter(List<GetDataAdapter> getDataAdapter, Context context) {

        super();

        this.getDataAdapter = getDataAdapter;
        this.context = context;


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_dokter, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final GetDataAdapter getDataAdapter3 = getDataAdapter.get(position);

        holder.nama_dokterTextView.setText(getDataAdapter3.getNama_dokter());

        holder.nama_spesialisasiTextView.setText(getDataAdapter3.getNama_spesialisasi());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfilDokter.class);
                i.putExtra("nama_dokter", getDataAdapter3.getNama_dokter());
                i.putExtra("nama_spesialisasi", getDataAdapter3.getNama_spesialisasi());
                i.putExtra("alamat_praktek", getDataAdapter3.getAlamat_praktek());
                i.putExtra("notlpn_praktek", getDataAdapter3.getNoTlpn_praktek());
                i.putExtra("hari_praktek", getDataAdapter3.getHari_praktek());
                i.putExtra("id_dokter", getDataAdapter3.getId_dokter());
                i.putExtra("id_lokasi", getDataAdapter3.getId_lokasi());
                i.putExtra("layanan", getDataAdapter3.getLayanan());
                i.putExtra("jam_buka", getDataAdapter3.getJam_buka());
                i.putExtra("jam_tutup", getDataAdapter3.getJam_tutup());
                Log.i("gogo", "" + holder.getItemId());
                context.startActivity(i);

            }
        });


    }


    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nama_dokterTextView;
        TextView nama_spesialisasiTextView;


        ViewHolder(View itemView) {

            super(itemView);

            nama_dokterTextView = itemView.findViewById(R.id.textViewDokter);
            nama_spesialisasiTextView = itemView.findViewById(R.id.textViewSpesialisasi);


        }


    }

    public void clear() {
        int size = getDataAdapter.size();
        getDataAdapter.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void setFilter(List<GetDataAdapter> countryModels) {
        getDataAdapter = new ArrayList<>();
        getDataAdapter.addAll(countryModels);
        notifyDataSetChanged();
    }


}