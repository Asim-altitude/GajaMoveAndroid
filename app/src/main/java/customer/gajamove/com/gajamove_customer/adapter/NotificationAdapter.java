package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Notification_Data;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;

public class NotificationAdapter extends BaseAdapter {

    List<Notification_Data> notification_dataList;
    Context context;

    public NotificationAdapter(List<Notification_Data> notification_dataList, Context context) {
        this.notification_dataList = notification_dataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notification_dataList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_item,null);

        TextView title = convertView.findViewById(R.id.notification_title);
        TextView desc = convertView.findViewById(R.id.notification_desc);
        TextView order_id = convertView.findViewById(R.id.order_id);
        TextView date = convertView.findViewById(R.id.date_time_txt);

        title.setText(notification_dataList.get(position).getTitle());
        desc.setText(notification_dataList.get(position).getBody());
        order_id.setText(notification_dataList.get(position).getOrder_id());
        date.setText(UtilsManager.parseDateToddMMyyyy(notification_dataList.get(position).getDate_time()));

        return convertView;
    }
}
