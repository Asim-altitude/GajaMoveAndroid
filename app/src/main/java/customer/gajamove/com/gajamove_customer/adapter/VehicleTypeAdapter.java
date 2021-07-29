package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import androidx.core.content.ContextCompat;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.VehicleType;

public class VehicleTypeAdapter extends BaseAdapter {

    private Context context;
    private List<VehicleType> vehicleTypeList;

    public VehicleTypeAdapter(Context context, List<VehicleType> vehicleTypeList) {
        this.context = context;
        this.vehicleTypeList = vehicleTypeList;
    }

    private int selected = 0;

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @Override
    public int getCount() {
        return vehicleTypeList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.vehicle_type_item,null);


        LinearLayout main_lay = convertView.findViewById(R.id.main_lay);
        TextView vehicle_type = convertView.findViewById(R.id.vehicle_type_label);
        TextView vehicle_price = convertView.findViewById(R.id.vehicle_price_label);


        vehicle_price.setVisibility(View.VISIBLE);

        vehicle_price.setText("RM"+vehicleTypeList.get(position).getPrice());

        vehicle_type.setText(vehicleTypeList.get(position).getV_type());

        if (vehicleTypeList.get(position).isSelected()) {
            main_lay.setBackgroundResource(R.drawable.next_btn_drawable);
            vehicle_type.setTextColor(ContextCompat.getColor(context,R.color.white_color));
            vehicle_price.setTextColor(ContextCompat.getColor(context,R.color.white_color));
        } else {
            main_lay.setBackgroundResource(R.drawable.grey_rect_empty);
            vehicle_type.setTextColor(ContextCompat.getColor(context,R.color.black_color));
            vehicle_price.setTextColor(ContextCompat.getColor(context,R.color.black_color));
        }


        return convertView;
    }

    boolean isMulti = true;

    public boolean isMulti() {
        return isMulti;
    }

    public void setMulti(boolean multi) {
        isMulti = multi;
    }
}
