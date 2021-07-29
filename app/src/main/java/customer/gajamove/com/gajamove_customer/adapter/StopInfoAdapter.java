package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.DeleteCallback;

public class StopInfoAdapter extends BaseAdapter {


    Context context;
    ArrayList<Prediction> predictions;
    DeleteCallback deleteCallback;

    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public StopInfoAdapter(Context context, ArrayList<Prediction> predictions) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.stop_info_lay,null);


        TextView location_header = convertView.findViewById(R.id.pickup_header_text);
        TextView location_name = convertView.findViewById(R.id.pickup_location_name);


        RelativeLayout top = convertView.findViewById(R.id.top);
        RelativeLayout end = convertView.findViewById(R.id.end);

        try {
            if (position < predictions.size() - 1) {

                top.setVisibility(View.VISIBLE);
                end.setVisibility(View.GONE);
            } else {
                top.setVisibility(View.GONE);
                end.setVisibility(View.VISIBLE);
            }


            location_header.setText(predictions.get(position).getLocation_title());
            location_name.setText(predictions.get(position).getLocation_name());
        }
        catch (Exception e){
            e.printStackTrace();
        }




        return convertView;
    }
}
