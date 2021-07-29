package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import customer.gajamove.com.gajamove_customer.R;


public class AdditionalServicesAdapter extends BaseAdapter {

    String[] list;
    Context context;

    public AdditionalServicesAdapter(String[] list, Context context) {
        this.list = list;
        this.context = context;
    }

    boolean showPrice = false;

    public void setShowPrice(boolean showPrice) {
        this.showPrice = showPrice;
    }

    @Override
    public int getCount() {
        return list.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.additional_service_item,null);

        TextView service_name = convertView.findViewById(R.id.service_name);
        TextView service_price = convertView.findViewById(R.id.service_price);

        if (list[position].equalsIgnoreCase("") || list[position].equalsIgnoreCase("@")){
            service_name.setVisibility(View.GONE);
            service_price.setVisibility(View.GONE);
        }else {
            service_name.setVisibility(View.VISIBLE);
            service_price.setVisibility(View.VISIBLE);
            service_name.setText(list[position].split("@")[0]);
            service_price.setText(list[position].split("@")[1]);
        }

        return convertView;
    }
}
