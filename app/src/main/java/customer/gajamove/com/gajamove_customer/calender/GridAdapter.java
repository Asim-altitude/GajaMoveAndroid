package customer.gajamove.com.gajamove_customer.calender;

import android.content.Context;
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

public class GridAdapter extends BaseAdapter
{
    private static final String TAG = GridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<Date> monthlyDates;
    private List<Integer> date_checker_list;
    private Calendar currentDate;
    private List<EventObjects> allEvents;
    private Context context;
    private DateClicked dateClickedLister;
    private MonthChangeListner monthChangeListner;
    private DayNotifier dayNotifier;
    private int selected_day = -1;

    public void setDate_checker_list(List<Integer> date_checker_list) {
        this.date_checker_list = date_checker_list;
    }

    public void setMonthChangeListner(MonthChangeListner monthChangeListner) {
        this.monthChangeListner = monthChangeListner;
    }

    public void setDayNotifier(DayNotifier dayNotifier) {
        this.dayNotifier = dayNotifier;
    }

    public void setDateClickedLister(DateClicked dateClickedLister) {
        this.dateClickedLister = dateClickedLister;
    }

    public GridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, List<EventObjects> allEvents) {
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.allEvents = allEvents;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    int displayMonth = 0;
    int selectedMonth = 0;
    int middle = -1;

    DriverCalenderView.DateUpdate dateUpdate;

    public void setDateUpdate(DriverCalenderView.DateUpdate dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public void setMiddle(int middle) {
        this.middle = middle;
    }


    @Override
    public int getCount() {
        return monthlyDates.size();
    }

    public Object getItem(int position) {
        return monthlyDates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    int height =0;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_calender_cell,null);

        TextView textView = convertView.findViewById(R.id.date_text);

        try {

            if (monthlyDates.get(position)==null){
                textView.setText("");
                return  convertView;
            }

            final Date mDate = monthlyDates.get(position);
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(mDate);
            int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
            displayMonth = dateCal.get(Calendar.MONTH) + 1;
            int displayYear = dateCal.get(Calendar.YEAR);
            int currentMonth = currentDate.get(Calendar.MONTH) + 1;
            int currentYear = currentDate.get(Calendar.YEAR);
            int currentDate_ = currentDate.get(Calendar.DAY_OF_MONTH);



            if (displayMonth == currentMonth && displayYear == currentYear) {

                selectedMonth = currentDate.get(Calendar.MONTH) + 1;
                String dayLongName = dateCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                textView.setText(dayValue + " " + getMonthName(selectedMonth) + " (" + getDayName(dayLongName) + ") " + currentYear);

           /* LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.height = 70;
            textView.setLayoutParams(layoutParams);*/

            } else {
                textView.setText("");

           /* LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.height = 0;
            textView.setLayoutParams(layoutParams);*/
            }
            //Add day to calendar


            if (position == middle) {

                if (UtilsManager.isDateValid(dateCal.getTime(),Calendar.getInstance().getTime())) {
                    Create_Order_Screen.chosen_date = currentYear + "-" + selectedMonth + "-" + dayValue;
                    textView.setTextColor(ContextCompat.getColor(context, R.color.black_color));

                    if (dateUpdate!=null)
                        dateUpdate.onDateUpdate(monthlyDates.get(position));
                }
                else {
                    Create_Order_Screen.chosen_date = "";
                    textView.setTextColor(ContextCompat.getColor(context, R.color.background));
                }
            } else {

                if (UtilsManager.isDateValid(dateCal.getTime(),Calendar.getInstance().getTime())) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.dark_gray_color));
                }
                else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.background));
                }

            }

        }
        catch (Exception e){
            e.printStackTrace();
            return convertView;
        }

        return convertView;
    }




    public int getPosition(Object item) {
        return monthlyDates.indexOf(item);
    }

    class DateViewHolder extends RecyclerView.ViewHolder
    {
        TextView date_text;
        private LinearLayout view;
        public DateViewHolder(View itemView) {
            super(itemView);
            date_text = itemView.findViewById(R.id.date_text);


        }
    }

    private String getMonthName(int m){
        String monthName = "";

        switch (m){
            case 1:
                monthName = "Jan";
                break;
            case 2:
                monthName = "Feb";
                break;
            case 3:
                monthName = "Mar";
                break;
            case 4:
                monthName = "Apr";
                break;
            case 5:
                monthName = "May";
                break;
            case 6:
                monthName = "Jun";
                break;
            case 7:
                monthName = "Jul";
                break;
            case 8:
                monthName = "Aug";
                break;
            case 9:
                monthName = "Sep";
                break;
            case 10:
                monthName = "Oct";
                break;
            case 11:
                monthName = "Nov";
                break;
            case 12:
                monthName = "Dec";
                break;
        }

        return monthName;
    }

    private String getDayName(String name){
        return name.substring(0,3);
    }

}
