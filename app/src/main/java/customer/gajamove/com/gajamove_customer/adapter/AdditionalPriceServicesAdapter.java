package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import customer.gajamove.com.gajamove_customer.R;


public class AdditionalPriceServicesAdapter extends BaseAdapter {

    String[] list,priceList;
    Context context;

    public AdditionalPriceServicesAdapter(String[] list, String[] priceList, Context context) {
        this.list = list;
        this.priceList = priceList;
        this.context = context;
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

        if (!list[position].trim().toString().equalsIgnoreCase("") && !priceList[position].trim().toString().equalsIgnoreCase("")) {
            convertView.setVisibility(View.VISIBLE);
        }else {
            convertView.setVisibility(View.GONE);
        }

        service_name.setText(list[position]);
        service_price.setText("RM"+priceList[position]);

        return convertView;
    }
}
