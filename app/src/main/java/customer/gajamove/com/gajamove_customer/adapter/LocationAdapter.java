package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.FavouriteSaver;
import customer.gajamove.com.gajamove_customer.utils.SaveLocationCallBack;

public class LocationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Prediction> predictions;
    FavouriteSaver favouriteSaver;

    public LocationAdapter(Context context, ArrayList<Prediction> predictions) {
        this.context = context;
        this.predictions = predictions;
        favouriteSaver = new FavouriteSaver(context);
    }

    @Override
    public int getCount() {
        return predictions.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (!predictions.get(position).isHeader()){
            convertView = LayoutInflater.from(context).inflate(R.layout.location_item_lay,null);
            TextView location_title = convertView.findViewById(R.id.location_title);
            TextView location_text = convertView.findViewById(R.id.location_text);
            ImageView fav_btn = convertView.findViewById(R.id.fav_btn);

            location_text.setText(predictions.get(position).getLocation_name());
            location_title.setText(predictions.get(position).getLocation_title());

            fav_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!predictions.get(position).isIsfav()){

                        /*predictions.get(position).setIsfav(true);
                        ArrayList<Prediction> fav_predictions = favouriteSaver.getFavList();
                        fav_predictions.add(predictions.get(position));

                        favouriteSaver.saveFav(fav_predictions);*/

                        if (saveLocationCallBack!=null)
                            saveLocationCallBack.onLocationSaved(position);

                        notifyDataSetChanged();
                    }else {
                       /* int id = -1;

                        ArrayList<Prediction> fav_predictions = favouriteSaver.getFavList();
                        for (int i=0;i<fav_predictions.size();i++){
                            if (fav_predictions.get(i).getLocation_id().equalsIgnoreCase(predictions.get(position).getLocation_id())){
                                id = i;
                                break;
                            }
                        }

                        fav_predictions.remove(id);
                        favouriteSaver.saveFav(fav_predictions);
                        predictions.remove(position);
                        notifyDataSetChanged();*/

                        if (saveLocationCallBack!=null)
                            saveLocationCallBack.onRemoveLocation(position);

                        notifyDataSetChanged();

                    }
                }
            });

            if (predictions.get(position).isIsfav()){
                fav_btn.setImageResource(R.drawable.filled_heart);
            }else {
                fav_btn.setImageResource(R.drawable.empty_heart);
            }


        }else {
            convertView = LayoutInflater.from(context).inflate(R.layout.location_header,null);
            TextView location_header = convertView.findViewById(R.id.header_title);
            location_header.setText(predictions.get(position).getHeading());
        }

        return convertView;
    }

    SaveLocationCallBack saveLocationCallBack;


    public void setSaveLocationCallBack(SaveLocationCallBack saveLocationCallBack) {
        this.saveLocationCallBack = saveLocationCallBack;
    }
}
