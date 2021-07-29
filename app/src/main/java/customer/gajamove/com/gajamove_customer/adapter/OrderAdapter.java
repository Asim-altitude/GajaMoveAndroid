package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.MyOrder;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class OrderAdapter extends BaseAdapter {


    List<MyOrder> myOrderList;
    Context context;

    public OrderAdapter(List<MyOrder> myOrderList, Context context) {
        this.myOrderList = myOrderList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return myOrderList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.order_item,null);

        TextView user_name = convertView.findViewById(R.id.user_name);
        CircleImageView user_image = convertView.findViewById(R.id.user_image);

        TextView date_text = convertView.findViewById(R.id.date_time_txt);
        TextView order_total = convertView.findViewById(R.id.total_cost);



        MyOrder myOrder = myOrderList.get(position);

        if (myOrder.getCust_name().equalsIgnoreCase("null") || myOrder.getCust_name().equalsIgnoreCase("")){
            myOrder.setCust_name("");
        }

        order_total.setText("RM"+myOrder.getOrder_total());
        date_text.setText(UtilsManager.parseNewDateToddMMyyyy(myOrder.getOrder_date()));

        user_name.setText(myOrderList.get(position).getCust_name());

        try {
            Picasso.with(context).load(Constants.SERVICE_IMAGE_BASE_PATH+myOrder.getCust_image()).placeholder(R.drawable.profile_icon).into(user_image);

        }
        catch (Exception e){
            e.printStackTrace();
        }


        return convertView;
    }
}
