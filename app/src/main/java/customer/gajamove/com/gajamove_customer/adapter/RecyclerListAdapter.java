package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.core.view.DragStartHelper;
import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.DeleteCallback;
import customer.gajamove.com.gajamove_customer.utils.ItemTouchHelperAdapter;

public class RecyclerListAdapter extends
        RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    Context context;
    ArrayList<Prediction> predictions;
    DeleteCallback deleteCallback;

    public RecyclerListAdapter(Context context, ArrayList<Prediction> predictions) {
        this.context = context;
        this.predictions = predictions;
    }

    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.stop_item_lay,parent,false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);

        return itemViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        holder.textView.setText(predictions.get(position).getLocation_title());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteCallback!=null)
                    deleteCallback.onItemDelete(position);
            }
        });




    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
      /*  if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(predictions, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(predictions, i, i - 1);
            }
        }*/
        Collections.swap(predictions,fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

             textView = itemView.findViewById(R.id.stop_text);
             imageView = itemView.findViewById(R.id.delete_icon);

        }
    }
}
