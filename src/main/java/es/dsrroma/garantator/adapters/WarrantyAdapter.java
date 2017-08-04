package es.dsrroma.garantator.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import es.dsrroma.garantator.R;
import es.dsrroma.garantator.data.model.Warranty;

public class WarrantyAdapter extends RecyclerView.Adapter<WarrantyAdapter.WarrantyAdapterViewHolder>{

    private List<Warranty> warranties;

    public WarrantyAdapter() {

    }

    public class WarrantyAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;

        public WarrantyAdapterViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvWarrantyName);
        }
    }

    @Override
    public WarrantyAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.warranty_list_item, parent, false);
        return new WarrantyAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WarrantyAdapterViewHolder holder, int position) {
        holder.tvName.setText(warranties.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return warranties == null ? 0 : warranties.size();
    }

    public void setWarranties(List<Warranty> warranties) {
        this.warranties = warranties;
        notifyDataSetChanged();
    }
}
