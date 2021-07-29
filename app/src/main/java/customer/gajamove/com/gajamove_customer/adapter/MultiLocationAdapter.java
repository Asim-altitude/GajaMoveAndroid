package customer.gajamove.com.gajamove_customer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Prediction;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;

public class MultiLocationAdapter extends BaseAdapter {

    ArrayList<Prediction> predictionArrayList;
    Activity context;

    public MultiLocationAdapter(ArrayList<Prediction> predictionArrayList, Activity context) {
        this.predictionArrayList = predictionArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return predictionArrayList.size();
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        convertView = LayoutInflater.from(context).inflate(R.layout.multi_location_item,null);

        final TextView header_txt = convertView.findViewById(R.id.header_text);
        TextView location_txt = convertView.findViewById(R.id.location_txt);
        TextView contact = convertView.findViewById(R.id.contact);
        TextView address = convertView.findViewById(R.id.address);
        final ImageView expand_collaps_icon = convertView.findViewById(R.id.expand_collapse_icon);
        RelativeLayout main_lay = convertView.findViewById(R.id.stepper_lay);

        RelativeLayout top = convertView.findViewById(R.id.top);
        RelativeLayout end = convertView.findViewById(R.id.end);

        if (position < predictionArrayList.size() - 1){

            top.setVisibility(View.VISIBLE);
            end.setVisibility(View.GONE);
        }else {
            top.setVisibility(View.GONE);
            end.setVisibility(View.VISIBLE);
        }

        /*RelativeLayout top = convertView.findViewById(R.id.top);
        RelativeLayout middle = convertView.findViewById(R.id.middle);
        RelativeLayout end = convertView.findViewById(R.id.end);

        if (position==0){
            middle.setVisibility(View.GONE);
            top.setVisibility(View.VISIBLE);
            end.setVisibility(View.GONE);
        }else if (position == predictionArrayList.size()-1){
            middle.setVisibility(View.GONE);
            top.setVisibility(View.GONE);
            end.setVisibility(View.VISIBLE);
        }else {
            middle.setVisibility(View.VISIBLE);
            top.setVisibility(View.GONE);
            end.setVisibility(View.GONE);
        }*/

        LinearLayout contact_lay = convertView.findViewById(R.id.contact_lay);
        LinearLayout address_lay = convertView.findViewById(R.id.address_lay);
        final LinearLayout extra_info = convertView.findViewById(R.id.location_extra_info_lay);

        expand_collaps_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (extra_info.getVisibility()== View.VISIBLE){
                    extra_info.setVisibility(View.GONE);
                    expand_collaps_icon.setRotation(0);
                }else {
                    extra_info.setVisibility(View.VISIBLE);
                    expand_collaps_icon.setRotation(180);
                }

            }
        });



        contact_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!predictionArrayList.get(position).getContact().trim().equalsIgnoreCase("")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + predictionArrayList.get(position).getContact() + ""));
                    context.startActivity(intent);
                }
            }
        });

        try {
            String[] pick_header_arr = predictionArrayList.get(position).getLocation_name().split("\\s+");


            header_txt.setText((pick_header_arr[0] + " " + pick_header_arr[1]).replaceAll(",",""));
            location_txt.setText(predictionArrayList.get(position).getLocation_name());

            contact.setText(predictionArrayList.get(position).getContact());
            address.setText(predictionArrayList.get(position).getAddress());

        }
        catch (Exception e){
            e.printStackTrace();
        }


        header_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*String waypoints = "";
                if (predictionArrayList.size() > 2){

                    for (int i = 1;i<predictionArrayList.size()-1;i++){

                        if (waypoints.equalsIgnoreCase("")){
                            waypoints = waypoints + predictionArrayList.get(i).getLocation_name();
                        }else {
                            waypoints += "|";
                            waypoints = waypoints + predictionArrayList.get(i).getLocation_name();
                        }
                    }

                }

                String mapLink = "";
                if (waypoints.equalsIgnoreCase("")) {
                    mapLink = "https://www.google.com/maps/dir/?api=1&origin=" + predictionArrayList.get(0).getLocation_name() + "&destination=" + predictionArrayList.get(predictionArrayList.size() - 1).getLocation_name() + "&travelmode=driving";
                }else {
                    mapLink = "https://www.google.com/maps/dir/?api=1&origin=" + predictionArrayList.get(0).getLocation_name() + "&waypoints="+waypoints+"&destination=" + predictionArrayList.get(predictionArrayList.size() - 1).getLocation_name() + "&travelmode=driving";

                }
                Uri gmmIntentUri = Uri.parse(mapLink);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    UtilsManager.OpenRoutGoogleMap(mapIntent,context);
                    return;
                }

                try {
                    LatLng latLng = UtilsManager.getLocationFromAddress(context,predictionArrayList.get(position).getLocation_name());
                    Constants.OpenLocationOnMap(context,latLng,predictionArrayList.get(position).getLocation_name());
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
                }*/
            }
        });

        location_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                header_txt.performClick();
            }
        });

        if (predictionArrayList.get(position).getContact().equalsIgnoreCase(""))
            contact_lay.setVisibility(View.GONE);
        else
            contact_lay.setVisibility(View.VISIBLE);

        if (predictionArrayList.get(position).getAddress().equalsIgnoreCase(""))
            address_lay.setVisibility(View.GONE);
        else
            address_lay.setVisibility(View.VISIBLE);


        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) main_lay.getLayoutParams();

        if (address_lay.getVisibility()== View.VISIBLE && contact_lay.getVisibility()== View.VISIBLE){
            int px = UtilsManager.convertDipToPixels(context,110);
            layoutParams.height = px;
            main_lay.setLayoutParams(layoutParams);

        }else if ((address_lay.getVisibility()== View.GONE && contact_lay.getVisibility()== View.VISIBLE)
           ||
                (address_lay.getVisibility()== View.VISIBLE && contact_lay.getVisibility()== View.GONE))
        {
            int px = UtilsManager.convertDipToPixels(context,100);
            layoutParams.height = px;
            main_lay.setLayoutParams(layoutParams);
        }else {
            int px = UtilsManager.convertDipToPixels(context,70);
            layoutParams.height = px;
            main_lay.setLayoutParams(layoutParams);
        }


        return convertView;
    }
}
