package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.PriceService;

public class PriceServiceListAdapter extends BaseAdapter {
    List<PriceService> priceServiceList;
    Context context;

    public PriceServiceListAdapter(List<PriceService> priceServiceList, Context context) {
        this.priceServiceList = priceServiceList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return priceServiceList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.services_price_lay,null);

        TextView service_name = convertView.findViewById(R.id.service_name);
        TextView service_price = convertView.findViewById(R.id.service_price);


        service_name.setText(priceServiceList.get(position).getService_name());
        service_price.setText("RM"+priceServiceList.get(position).getService_price());

        return convertView;
    }
}
