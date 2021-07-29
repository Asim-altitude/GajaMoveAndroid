package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



import androidx.core.content.ContextCompat;
import customer.gajamove.com.gajamove_customer.R;


/**
 * Created by PC-GetRanked on 11/28/2018.
 */

public class CreditPickAdapter extends BaseAdapter {

    private String[] amountList;
    private Context context;
    private int selected = 0;

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public CreditPickAdapter(String[] amountList, Context context) {
        this.amountList = amountList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return amountList.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.amount_credit_item,parent,false);

        TextView amount_box = convertView.findViewById(R.id.amount_text);

        amount_box.setText(amountList[position]);

        if (position==selected) {
            amount_box.setBackgroundResource(R.drawable.next_btn_drawable);
            amount_box.setTextColor(ContextCompat.getColor(context,R.color.white_color));
        }
        else
        {
            amount_box.setBackgroundResource(R.drawable.grey_rect_empty);
            amount_box.setTextColor(ContextCompat.getColor(context,R.color.black_color));
        }

        return convertView;
    }
}
