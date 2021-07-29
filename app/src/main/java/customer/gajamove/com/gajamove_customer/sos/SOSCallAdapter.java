package customer.gajamove.com.gajamove_customer.sos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.PhoneContact;

public class SOSCallAdapter extends BaseAdapter {


    private Context context;
    private List<PhoneContact> list;

    public SOSCallAdapter(Context context, List<PhoneContact> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.sos_call_lay,null);


        TextView contact_name = convertView.findViewById(R.id.contact_name);
        TextView contact_number = convertView.findViewById(R.id.conatct_number);

        contact_name.setText(list.get(position).getName());
        contact_number.setText(list.get(position).getNumber());

        return convertView;
    }
}
