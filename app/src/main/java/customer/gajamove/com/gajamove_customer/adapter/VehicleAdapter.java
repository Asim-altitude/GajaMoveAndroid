package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Service_Slot;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {

    private ArrayList<Service_Slot> service_slots;
    private Context context;
    private int selected = -1;

    public VehicleAdapter(ArrayList<Service_Slot> service_slots, Context context) {
        this.service_slots = service_slots;
        this.context = context;

    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.vehicle_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public interface OnClickItem {
        void onItemClick(int pos);
    }

    OnClickItem onItemClickListener;

    public void setOnItemClickListener(OnClickItem onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Picasso.with(context).load(service_slots.get(position).getImage()).placeholder(R.drawable.bike).into(holder.imageView);
        holder.name.setText(service_slots.get(position).getSlot_name());

        if (selected==position){
            holder.main_lay.setBackgroundResource(R.drawable.circle_bg_red);
            holder.name.setTextColor(ContextCompat.getColor(context,R.color.black_color));
        }else {
            holder.main_lay.setBackgroundResource(R.drawable.circle_bg_light);
            holder.name.setTextColor(ContextCompat.getColor(context,R.color.dark_gray_color));
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null)
                    onItemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return service_slots.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        ImageView imageView;
        LinearLayout main_lay;

        public ViewHolder(View itemView) {
            super(itemView);
             name = itemView.findViewById(R.id.vehicle_name);
             imageView =  itemView.findViewById(R.id.vehicle_image);
             main_lay = itemView.findViewById(R.id.item_bg);
          }
    }

}
