package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import customer.gajamove.com.gajamove_customer.R;

public class BigSidePointAdapter extends BaseAdapter {

    int items;
    Context context;

    public BigSidePointAdapter(int items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items;
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view==null)
            view = LayoutInflater.from(context).inflate(R.layout.big_side_point_item,null);

        RelativeLayout top = view.findViewById(R.id.top);
        RelativeLayout middle = view.findViewById(R.id.middle);
        RelativeLayout end = view.findViewById(R.id.end);

        if (i==0){
            middle.setVisibility(View.GONE);
            top.setVisibility(View.VISIBLE);
            end.setVisibility(View.GONE);
        }else if (i == items-1){
            middle.setVisibility(View.GONE);
            top.setVisibility(View.GONE);
            end.setVisibility(View.VISIBLE);
        }else {
            middle.setVisibility(View.VISIBLE);
            top.setVisibility(View.GONE);
            end.setVisibility(View.GONE);
        }

        return view;
    }
}
