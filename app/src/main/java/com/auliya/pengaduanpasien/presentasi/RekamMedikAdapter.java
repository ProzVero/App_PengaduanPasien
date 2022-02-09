package com.auliya.pengaduanpasien.presentasi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.model.RekamMedikModel;
import com.auliya.pengaduanpasien.view.DetailPengaduan;
import com.auliya.pengaduanpasien.view.DetailRekamMedik;

import java.util.ArrayList;
import java.util.Collection;

public class RekamMedikAdapter extends RecyclerView.Adapter<RekamMedikAdapter.HolderData> {

    private Context context;
    private ArrayList<RekamMedikModel> dataRekam;

    public RekamMedikAdapter(Context context, ArrayList<RekamMedikModel> dataRekam) {
        this.context = context;
        this.dataRekam = dataRekam;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_rekammedik, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        RekamMedikModel rekamMedikModel = dataRekam.get(position);
        holder.txt_idrekam.setText(String.valueOf(rekamMedikModel.getId_rekam()));
        holder.txt_diagnosa.setText(rekamMedikModel.getDiagnosa());
        holder.txt_diagnosa.setText(rekamMedikModel.getDiagnosa());
        holder.txt_no_rm.setText(rekamMedikModel.getNo_rm());
        holder.txt_no_jk.setText(rekamMedikModel.getNo_jaminan());
        holder.rekamedikdetail.setOnClickListener(v -> {
            Intent i = new Intent(context, DetailRekamMedik.class);
            i.putExtra("id_rekam", holder.txt_idrekam.getText().toString().trim());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return dataRekam.size();
    }

    Filter searchData = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RekamMedikModel> searchList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                searchList.addAll(dataRekam);
            } else {
                for (RekamMedikModel getRekamMedik : dataRekam) {
                    if (getRekamMedik.getDiagnosa().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getRekamMedik.getNo_jaminan().toLowerCase().contains(constraint.toString().toLowerCase())
                            || getRekamMedik.getNo_rm().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        searchList.add(getRekamMedik);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = searchList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataRekam.clear();
            dataRekam.addAll((Collection<? extends RekamMedikModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getSearchData() {
        return searchData;
    }

    public class HolderData extends RecyclerView.ViewHolder {
        private TextView txt_no_rm, txt_no_jk, txt_diagnosa, txt_idrekam;
        private RelativeLayout rekamedikdetail;

        public HolderData(@NonNull View itemView) {
            super(itemView);
            txt_idrekam = itemView.findViewById(R.id.id_rekam);
            txt_no_jk = itemView.findViewById(R.id.no_jk);
            txt_no_rm = itemView.findViewById(R.id.no_rm);
            txt_diagnosa = itemView.findViewById(R.id.diagnosis);
            rekamedikdetail = itemView.findViewById(R.id.rekamandetail);
        }
    }
}
