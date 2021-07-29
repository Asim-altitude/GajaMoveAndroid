package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Industry;
import customer.gajamove.com.gajamove_customer.models.SubIndustry;

public class IndustryAdapter extends BaseExpandableListAdapter {

    Context context;
    ArrayList<Industry> industries;

    public IndustryAdapter(Context context, ArrayList<Industry> industries) {
        this.context = context;
        this.industries = industries;
    }

    @Override
    public int getGroupCount() {
        return industries.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return industries.get(i).getSubIndustries().size();
    }

    @Override
    public Object getGroup(int i) {
        return industries.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return industries.get(i).getSubIndustries().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if (view==null)
            view = LayoutInflater.from(context).inflate(R.layout.industry_list_group,null);

        TextView textView = view.findViewById(R.id.industry_name);
        ImageView imageView = view.findViewById(R.id.arrow);
        Industry industry = (Industry) getGroup(i);
        textView.setText(industry.getName());

        if (industry.getSubIndustries().size() > 0)
            imageView.setVisibility(View.VISIBLE);
        else
            imageView.setVisibility(View.GONE);


        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view==null)
            view = LayoutInflater.from(context).inflate(R.layout.industry_sub_item,null);

        TextView textView = view.findViewById(R.id.industry_sub_name);
        SubIndustry industry = (SubIndustry) getChild(i,i1);
        textView.setText(industry.getName());


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
