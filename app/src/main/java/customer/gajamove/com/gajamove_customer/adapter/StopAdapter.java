package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.DeleteCallback;

public class StopAdapter extends BaseAdapter {


    Context context;
    ArrayList<Prediction> predictions;
    DeleteCallback deleteCallback;

    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public StopAdapter(Context context, ArrayList<Prediction> predictions) {
        this.context = context;
        this.predictions = predictions;
    }

    @Override
    public int getCount() {
        return predictions.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void remove(Object item){
        predictions.remove(item);
        notifyDataSetChanged();
    }

    public void insert(Object item,int to){
        predictions.add(to, (Prediction) item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.stop_item_lay,null);


        try {



            TextView textView = convertView.findViewById(R.id.stop_text);
            RelativeLayout relativeLayout = convertView.findViewById(R.id.base_lay);
            ImageView imageView = convertView.findViewById(R.id.delete_icon);


            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position==0 || position==predictions.size()-1){
                        if (!predictions.get(position).getLocation_title().equalsIgnoreCase("")){
                            textView.setClickable(true);
                        }
                    }

                    if (position==0 || position==predictions.size()-1) {
                        if (deleteCallback != null)
                            deleteCallback.onLocationRequest(position);

                    }
                }
            });


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relativeLayout.performClick();
                }
            });

            textView.setText(predictions.get(position).getLocation_title());


            if (position==0){
                //First
                imageView.setImageResource(R.drawable.ic_edit);
                imageView.setColorFilter(ContextCompat.getColor(context,R.color.dark_gray_color));

                if (predictions.get(position).getLocation_title().equalsIgnoreCase("")){
                    textView.setHint(context.getResources().getString(R.string.pick_location));
                    imageView.setVisibility(View.GONE);
                }else {
                    textView.setText(predictions.get(position).getLocation_title());
                    imageView.setVisibility(View.VISIBLE);
                }


            }else if (position==predictions.size()-1){
                //Last
                imageView.setImageResource(R.drawable.ic_edit);
                imageView.setColorFilter(ContextCompat.getColor(context,R.color.dark_gray_color));


                if (predictions.get(position).getLocation_title().equalsIgnoreCase("")){
                    textView.setHint(context.getResources().getString(R.string.drop_location));
                    imageView.setVisibility(View.GONE);
                }else {
                    textView.setText(predictions.get(position).getLocation_title());
                    imageView.setVisibility(View.VISIBLE);
                }

            }else {
                //Stop
                imageView.setImageResource(R.drawable.cross);

            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (position==0 || position==predictions.size()-1){
                        if (deleteCallback != null)
                            deleteCallback.onLocationRequest(position);

                        return;
                    }

                    if (deleteCallback != null)
                        deleteCallback.onItemDelete(position);
                }
            });


            if (predictions.get(position).getLocation_title().equalsIgnoreCase("")){
                textView.setClickable(true);
            }else {
                textView.setClickable(false);
                relativeLayout.setClickable(false);
            }

        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }


        return convertView;
    }
}
