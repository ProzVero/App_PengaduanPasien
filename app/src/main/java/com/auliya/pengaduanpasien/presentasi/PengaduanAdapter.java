package com.auliya.pengaduanpasien.presentasi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;
import com.auliya.pengaduanpasien.model.PengaduanModel;
import com.auliya.pengaduanpasien.view.DetailPengaduan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class PengaduanAdapter extends RecyclerView.Adapter<PengaduanAdapter.HolderData> {
    private Context context;
    private ArrayList<PengaduanModel> dataPengaduan;

    public PengaduanAdapter(Context context, ArrayList<PengaduanModel> dataPengaduan) {
        this.context = context;
        this.dataPengaduan = dataPengaduan;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_pengaduan,  parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        PengaduanModel  pengaduanModel = dataPengaduan.get(position);

        holder.txt_id.setText(String.valueOf(pengaduanModel.getId()));
        holder.txt_tanggal.setText(pengaduanModel.getCreated_at());
        holder.txt_saran.setText(pengaduanModel.getSaran());
        holder.nama.setText(pengaduanModel.getNama());
        holder.pengaduandetail.setOnClickListener(v -> {
            Intent i = new Intent(context, DetailPengaduan.class);
            i.putExtra("id_pengaduan", holder.txt_id.getText().toString().trim());
            context.startActivity(i);
        });
        Glide.with(context)
                .load(URLServer.URL_IMAGE_ADUAN + String.valueOf(pengaduanModel.getId())+".png")
                .fitCenter()
                .placeholder(ContextCompat.getDrawable(context,R.drawable.ic_image_24))
                .apply(RequestOptions.fitCenterTransform())
                .into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return dataPengaduan.size();
    }

    public class HolderData extends RecyclerView.ViewHolder{
        private TextView   txt_saran, txt_tanggal, txt_id, nama;
        private CardView pengaduandetail;
        private ImageView imgView;
        public HolderData(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.nama);
            txt_saran = itemView.findViewById(R.id.saran);
            txt_tanggal = itemView.findViewById(R.id.tanggal);
            txt_id  = itemView.findViewById(R.id.txt_id);
            pengaduandetail  = itemView.findViewById(R.id.pengaduan_detail);
            imgView = itemView.findViewById(R.id.img_aduan);

        }
    }
}
