package es.dsrroma.garantator.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.dsrroma.garantator.R;
import es.dsrroma.garantator.data.model.Warranty;
import es.dsrroma.garantator.utils.CursorToBeanUtils;

public class WarrantyAdapter extends RecyclerView.Adapter<WarrantyAdapter.WarrantyAdapterViewHolder>{

    private Cursor cursor;

    private final OnItemClickListener clickHandler;

    public WarrantyAdapter(Cursor cursor, OnItemClickListener clickHandler) {
        this.cursor = cursor;
        this.clickHandler = clickHandler;
    }

    public class WarrantyAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView tvName;

        public WarrantyAdapterViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvWarrantyName);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            if (clickHandler != null) {
                clickHandler.onItemClick(itemView, position);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    @Override
    public WarrantyAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.warranty_list_item, parent, false);
        return new WarrantyAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WarrantyAdapterViewHolder holder, int position) {
        Warranty warranty = CursorToBeanUtils.cursorToBean(cursor, position, Warranty.class);
        holder.itemView.setTag(warranty);
        holder.tvName.setText(warranty.getName());
    }

    public Warranty getItem(int position) {
        return CursorToBeanUtils.cursorToBean(cursor, position, Warranty.class);
    }

    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor cursor) {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
