package customer.gajamove.com.gajamove_customer.calender;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.Create_Order_Screen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;

/**
 * Created by PC-GetRanked on 11/26/2018.
 */

public class TimeAdapter extends BaseAdapter
{
    private static final String TAG = "TimeAdapter";
    
    private LayoutInflater mInflater;
    private List<String> timeList;
     private List<EventObjects> allEvents;
    private Context context;
    private DateClicked dateClickedLister;



    public void setDateClickedLister(DateClicked dateClickedLister) {
        this.dateClickedLister = dateClickedLister;
    }

    public TimeAdapter(Context context, List<String> timeList ) {

        this.timeList = timeList;
        this.allEvents = allEvents;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    private String getDayName(String name){
        return name.substring(0,3);
    }

    @Override
    public int getCount() {
        return timeList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    int selected = -1;
    int current_index = 0;

    public void setCurrent_index(int current_index) {
        this.current_index = current_index;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_calender_cell,null);

        TextView textView = convertView.findViewById(R.id.date_text);

        textView.setText(timeList.get(position));

        /*if (position==selected){
            textView.setTextColor(ContextCompat.getColor(context, R.color.black_color));
            Create_Order_Screen.chosen_time = UtilsManager.parseTime(timeList.get(position));
        }else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.dark_gray_color));
        }*/

        if (selected==position) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.black_color));
            if (timeList.get(position).toLowerCase().equalsIgnoreCase("asap")) {
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String time = simpleDateFormat.format(currentTime);
                Log.e(TAG, "getView: "+time);
                Create_Order_Screen.chosen_time = time;
                Create_Order_Screen.isImmediate = true;
                //timeList.get(position).toLowerCase().replace(" am", "").replace(" pm", "");

            } else {
                Create_Order_Screen.isImmediate = false;

                if (position > current_index && compareDates(timeList.get(position).toLowerCase().replace(" am", "").replace(" pm", ""))) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.black_color));
                    Create_Order_Screen.chosen_time = UtilsManager.parseTime(timeList.get(position));
                }
                else if (position > current_index){
                    textView.setTextColor(ContextCompat.getColor(context, R.color.black_color));
                    Create_Order_Screen.chosen_time = UtilsManager.parseTime(timeList.get(position));
                }

            }
        }
        else {
            if (position==selected){
                textView.setTextColor(ContextCompat.getColor(context, R.color.black_color));
                Create_Order_Screen.chosen_time = UtilsManager.parseTime(timeList.get(position));
            }else {
                textView.setTextColor(ContextCompat.getColor(context, R.color.dark_gray_color));
            }
        }

        return convertView;
    }


    public static final String inputFormat = "HH:mm";

    private Date date;
    private Date dateCompareOne;
    private Date dateCompareTwo;


    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);

    private boolean compareDates(String time){
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);

        date = parseDate(hour + ":" + minute);
        dateCompareOne = parseDate(time);

        if (dateCompareOne.before(date) ) {
            //yada yada

            return false;
        }else {
            return true;
        }
    }

    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }
}
