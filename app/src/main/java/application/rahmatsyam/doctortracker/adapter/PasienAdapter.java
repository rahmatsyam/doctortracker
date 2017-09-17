package application.rahmatsyam.doctortracker.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import application.rahmatsyam.doctortracker.R;
import application.rahmatsyam.doctortracker.config.Config;
import application.rahmatsyam.doctortracker.config.SessionManager;
import es.dmoral.toasty.Toasty;

/**
 * Created by Rahmat Syam on 31/1/2017.
 */

public class PasienAdapter extends RecyclerView.Adapter<PasienAdapter.ViewHolder> {

    private Context context;

    private List<GetDataAdapter> getDataAdapter;

    private SessionManager sessionManager;


    public PasienAdapter(List<GetDataAdapter> getDataAdapter, Context context) {

        super();

        this.getDataAdapter = getDataAdapter;
        this.context = context;
        sessionManager = new SessionManager(context);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_pasien, parent, false);

        return new ViewHolder(v);


    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final GetDataAdapter getDataAdapter1 = getDataAdapter.get(position);

        holder.nama_pasienTextView.setText(getDataAdapter1.getNama_pasien());

        holder.nohp_pasienTextView.setText(getDataAdapter1.getNohp_pasien());

        holder.tgl_registrasiTextView.setText(getDataAdapter1.getTgl_registrasi());

        holder.no_antrianTextView.setText(getDataAdapter1.getNo_antrian());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Apa anda yakin ingin hapus?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deletePasien(getDataAdapter1.getNo_antrian());
                                getDataAdapter.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getDataAdapter.size());

                            }
                        })
                        .setNegativeButton("Batal", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .create()
                        .show();
                return false;
            }


        });

        animate(holder);


    }

    private void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }


    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nama_pasienTextView;
        TextView nohp_pasienTextView;
        TextView tgl_registrasiTextView;
        TextView no_antrianTextView;


        ViewHolder(View itemView) {

            super(itemView);

            nama_pasienTextView    = itemView.findViewById(R.id.textView4);
            nohp_pasienTextView    = itemView.findViewById(R.id.textView6);
            tgl_registrasiTextView = itemView.findViewById(R.id.tgl_registrasi);
            no_antrianTextView     = itemView.findViewById(R.id.textView8);


        }

    }


    private void deletePasien(String noantrian) {


        String delete_url = Config.HAPUS_PASIEN
                + sessionManager.getUserDetails().get(SessionManager.KEY_ID_DOKTER) + "&noantrian=" + noantrian;

        StringRequest strReq = new StringRequest(Request.Method.GET, delete_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response", response);
                try {
                    JSONObject json = new JSONObject(response);
                    int success = json.getInt("success");
                    if (success == 1) {

                        Toasty.info(context,
                                "Data berhasil dihapus \n" +
                                        "Silakan swipe untuk melihat perubahan",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        Toasty.info(context,
                                "Gagal dihapus", Toast.LENGTH_SHORT)
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(strReq);
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