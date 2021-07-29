package customer.gajamove.com.gajamove_customer.calender;

/**
 * Created by PC-GetRanked on 11/26/2018.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.R;

public class DriverCalenderView  extends LinearLayout implements MonthChangeListner{
    private static final String TAG = "DriverCalenderView";
    private ImageView previousButton, nextButton;
    private TextView currentDate;
    private TextView prevMonth,currMonth,nextMonth;
    private ListView calendarGridView,timeRecycler;
    private Button addEventButton;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private GridAdapter mAdapter;
    private DatabaseQuery mQuery;
    private DateClicked dateClickedListner;

    public void setDateClickedListner(DateClicked dateClickedListner) {
        this.dateClickedListner = dateClickedListner;
        setGridCellClickEvents();
    }

    public DriverCalenderView(Context context) {
        super(context);
    }
    public DriverCalenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeUILayout();
       // setUpMonthList();
        setListners();
        setUpCalendarAdapter();

        //setGridCellClickEvents();
        Log.d(TAG, "I need to call this method");
    }
    public DriverCalenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initializeUILayout(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calender_custom_layout, this);
        calendarGridView =  view.findViewById(R.id.date_recycler_view);
        timeRecycler =  view.findViewById(R.id.time_recycler_view);
        prevMonth = view.findViewById(R.id.monthNamePrev);
        currMonth = view.findViewById(R.id.monthNameCurrent);
        nextMonth = view.findViewById(R.id.monthNameNext);

    }



    MothsAdapter monthAdapter;
    List<String> months;

    public void setMothList(List<String> months)
    {
        this.months = months;
        RefreshMonthList();
    }

    private void setListners()
    {
        prevMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                current = cal.get(Calendar.MONTH);
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
            }
        });
        nextMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                current = cal.get(Calendar.MONTH);
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
            }
        });
    }

    public void setUpMonthList()
    {
        months = new ArrayList<>();
        monthAdapter = new MothsAdapter(months,context);
        monthAdapter.setMonthSelected(new MonthSelected() {
            @Override
            public void onMonthSelected(String date) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
       /* monthRecycler.setLayoutManager(layoutManager);
        monthRecycler.setAdapter(monthAdapter);
        monthRecycler.addOnScrollListener(scrollListener);*/

    }

    int prevCenterPos = 1;
    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

           /* int center = monthRecycler.getWidth() / 2;
            View centerView = monthRecycler.findChildViewUnder(center, monthRecycler.getTop());
            int centerPos = monthRecycler.getChildAdapterPosition(centerView);
*/
          /*  if (prevCenterPos != centerPos) {
                monthAdapter.setCenterPosition(centerPos);
                // dehighlight the previously highlighted view
              *//*  View prevView = monthRecycler.getLayoutManager().findViewByPosition(prevCenterPos);
                if (prevView != null) {
                    View button = prevView.findViewById(R.id.rv_trends_graph_button);
                    int white = ContextCompat.getColor(context, R.color.white);
                    button.setBackgroundColor(white);
                }

                // highlight view in the middle
                if (centerView != null) {
                    View button = centerView.findViewById(R.id.rv_trends_graph_button);
                    int highlightColor = ContextCompat.getColor(context, R.color.colorAccent);
                    button.setBackgroundColor(highlightColor);
                }
*//*
                prevCenterPos = centerPos;
            }*/
        }
    };

    public void RefreshMonthList()
    {
        monthAdapter.notifyDataSetChanged();
    }

    private int current = 0;
    private void setPreviousButtonClickEvent(){
        previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                current = cal.get(Calendar.MONTH);
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();

            }
        });
    }

    private void showOtherMonths()
    {
        cal.add(Calendar.MONTH,1);
        String sDate = formatter.format(cal.getTime());
        nextMonth.setText(sDate.split(Pattern.quote(" "))[0]);
        cal.add(Calendar.MONTH,-2);
        String sDate1 = formatter.format(cal.getTime());
        prevMonth.setText(sDate1.split(Pattern.quote(" "))[0]);

        cal.add(Calendar.MONTH,1);

    }


    private void setNextButtonClickEvent(){
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                current = cal.get(Calendar.MONTH);
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();

            }
        });
    }

    private void PrepareMonths()
    {

        months = new ArrayList<>();

        String sDate = formatter.format(cal.getTime());
       // currentDate.setText(sDate);
        months.add(sDate);

        cal.add(Calendar.MONTH,1);

        String sDate1 = formatter.format(cal.getTime());
        // currentDate.setText(sDate);
        months.add(sDate1);

        cal.add(Calendar.MONTH,-2);

        String sDate0 = formatter.format(cal.getTime());
        // currentDate.setText(sDate);
        months.add(0,sDate0);

        cal.add(Calendar.MONTH,1);

    }

    private void setGridCellClickEvents(){
       mAdapter.setDateClickedLister(dateClickedListner);
    }
    List<EventObjects> mEvents;

    public void setmEvents(List<EventObjects> mEvents) {
        this.mEvents = mEvents;
        setUpCalendarAdapter();
    }

    public interface DateUpdate{
        void onDateUpdate(Date date);
    }


    DateUpdate dateUpdate = new DateUpdate() {
        @Override
        public void onDateUpdate(Date date) {
            if (date==null){
                return;
            }
            Calendar current = Calendar.getInstance();

            int current_month = current.get(Calendar.MONTH);
            int current_day = current.get(Calendar.DAY_OF_MONTH);
            int current_year = current.get(Calendar.YEAR);

            Calendar old = Calendar.getInstance();
            old.setTime(date);

            int selected_month = old.get(Calendar.MONTH);
            int selected_day = old.get(Calendar.DAY_OF_MONTH);
            int selected_year = old.get(Calendar.YEAR);

            try {
               /* if (current_year > selected_year) {
                    createTimeListForNext();
                }
                else if (current_year == selected_year && current_month < selected_month) {
                    createTimeListForNext();
                }
                else if (current_year == selected_year && current_month == selected_month && current_day < selected_day) {
                    createTimeListForNext();
                }*/
              /*  if (current_year == selected_year && current_month == selected_month && current_day == selected_day) {
                     createTimeList();
                }else {
                    createTimeListForNext();
                }*/

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    };




    private int current_date = 0,position_scroll=0;
    private List<Integer> date_checker_list;
    private TimeAdapter timeAdapter;
    int current_date_index = 0;

    private void setUpCalendarAdapter(){
        List<Date> dayValueInCells = new ArrayList<Date>();
        List<Integer> date_checker_list= new ArrayList<>();
        current = cal.get(Calendar.MONTH)+1;
       // mQuery = new DatabaseQuery(context);
       // List<EventObjects> mEvents = new ArrayList<>();//mQuery.getAllFutureEvents();
        if (mEvents==null)
            mEvents = new ArrayList<>();

        current_date = cal.get(Calendar.DATE);
        Calendar mCal = (Calendar)cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);

        dayValueInCells.add(null);
        dayValueInCells.add(null);

        while(dayValueInCells.size() < MAX_CALENDAR_COLUMN){
            int month = mCal.get(Calendar.MONTH)+1;


            if (month == cal.get(Calendar.MONTH)+1) {

                dayValueInCells.add(mCal.getTime());
                Log.e(TAG, "Month " + month + "date " + mCal.get(Calendar.DAY_OF_MONTH) + " current month " + cal.get(Calendar.MONTH));
                date_checker_list.add(1);

                current_date_index = current_date - 1;

            }

            int date = mCal.get(Calendar.DATE);
            if (current_date==date) {
                position_scroll = dayValueInCells.size() - 1;
               // current_date_index = dayValueInCells.size() - 1;
            }
           /* if (current==month) {
                dayValueInCells.add(mCal.getTime());
                int date = mCal.get(Calendar.DATE);

                if (current_date==date)
                    position_scroll = dayValueInCells.size()-1;

            }*/

            mCal.add(Calendar.DAY_OF_MONTH, 1);


        }
        Log.d(TAG, "Number of date " + dayValueInCells.size());
        String sDate = formatter.format(cal.getTime());

        currMonth.setText(sDate);
        showOtherMonths();

        /*PrepareMonths();
        RefreshMonthList();*/

        createTimeListForNext();
        timeAdapter = new TimeAdapter(context,timelist);
        timeRecycler.setAdapter(timeAdapter);

        timeRecycler.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                timeAdapter.setSelected(firstVisibleItem+2);
                timeAdapter.notifyDataSetChanged();
            }
        });
       // current_time_index = current_time_index -2;
        //current_time_index-=2;
        timeRecycler.setSelection(current_time_index);
        timeAdapter.setSelected(current_time_index+2);
        timeAdapter.setCurrent_index(current_index);
      //  timeRecycler.smoothScrollToPosition(current_time_index);

       // timeRecycler.smoothScrollToPosition(current_index);

        mAdapter = new GridAdapter(context, dayValueInCells, cal, mEvents);
        if (dateClickedListner!=null)
            mAdapter.setDateClickedLister(dateClickedListner);

        mAdapter.setDayNotifier(dayNotifier);
        mAdapter.setMonthChangeListner(this);
        mAdapter.setDate_checker_list(date_checker_list);
        mAdapter.setDateUpdate(dateUpdate);
       // current_date_index = current_date_index;
        calendarGridView.setAdapter(mAdapter);


        calendarGridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mAdapter.setMiddle(firstVisibleItem+2);
                mAdapter.notifyDataSetChanged();



              /*  Date date = dayValueInCells.get(firstVisibleItem+2);
                if (date==null){
                    return;
                }
                Calendar current = Calendar.getInstance();

                int current_month = current.get(Calendar.MONTH);
                int current_day = current.get(Calendar.DAY_OF_MONTH);
                int current_year = current.get(Calendar.YEAR);

                Calendar old = Calendar.getInstance();
                old.setTime(date);

                int month = old.get(Calendar.MONTH);
                int day = old.get(Calendar.DAY_OF_MONTH);
                int year = old.get(Calendar.YEAR);



                try {
                    if (current_year > year) {
                        createTimeListForNext();
                    } else if (current_year == year && current_month > month) {
                        createTimeListForNext();
                    } else if (current_year == year && current_month > month ) {
                        createTimeListForNext();
                    } else if (current_year == year && current_month > month && current_day == day) {
                        createTimeListForNext();
                    }else if (current_year == year && current_month == month && current_day == day) {
                        createTimeList();
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }*/
            }
        });



        calendarGridView.setSelection(current_date_index);
        mAdapter.setMiddle(current_date_index+2);
        //getRecyclerviewDate();
       /* calendarGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                   *//* current = cal.get(Calendar.MONTH);
                    cal.add(Calendar.MONTH, 1);
                    setUpCalendarAdapter();
                    Toast.makeText(context,"Last",Toast.LENGTH_SHORT).show();*//*
                }else if (!recyclerView.canScrollVertically(-1)){
                   *//* current = cal.get(Calendar.MONTH);
                    cal.add(Calendar.MONTH, -1);
                    setUpCalendarAdapter();
                    Toast.makeText(context,"first",Toast.LENGTH_SHORT).show();*//*
                }
            }
        });*/

    }

    DayNotifier dayNotifier = new DayNotifier() {
        @Override
        public void notifyCurrentDate(int position) {
           // calendarGridView.scrollToPosition(position);
        }
    };

    @Override
    public void nextMonth() {
        try {
            nextButton.performClick();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void prevMonth() {
        try {
            previousButton.performClick();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int current_time_index = 0;
    ArrayList<String> currrentTimeList;
    public  void createTimeList(){
        timelist = new ArrayList<>();


        timelist.add("");
        timelist.add("");

        Date current_date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm a");
        String date = dateFormater.format(current_date);

        String am_pm = date.split("\\s+")[1].toLowerCase();
        int hour = Integer.parseInt(date.split("\\s+")[0].toLowerCase().split(":")[0]);
        int min = Integer.parseInt(date.split("\\s+")[0].toLowerCase().split(":")[1]);

        int min_index = 0;
        if (min<15)
            min_index = 2;
        else if (min>15 && min<30)
            min_index = 3;
        else if (min>30 && min<45)
            min_index = 4;
        else {
            hour += 1;
            min_index = 1;

            if (hour==12){
                if (am_pm.toLowerCase().equalsIgnoreCase("AM"))
                    am_pm = "PM";
                else
                    am_pm = "AM";
            }
        }

        if (am_pm.equalsIgnoreCase("am")) {
            genrateTime(hour,min_index, "AM");
            genrateTime(1,min_index, "PM");
        }else if (am_pm.equalsIgnoreCase("pm")){
            genrateTime(1,min_index, "PM");
        }
        timelist.add("");
        timelist.add("");
       // timelist.add("ASAP");


      /*  Date current = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm a");
        String date = dateFormater.format(current);


        String am_pm = date.split("\\s+")[1].toLowerCase();
        String hour = date.split("\\s+")[0].toLowerCase().split(":")[0];
        String min = date.split("\\s+")[0].toLowerCase().split(":")[1];
        for (int i=0;i<timelist.size();i++){
            Log.e(TAG, "createTimeList: "+timelist.get(i).toString());
            if (timelist.get(i).toString().toLowerCase().contains(am_pm)){
                int hours = Integer.parseInt(hour);
                int mins = Integer.parseInt(min);

                int c_hours = Integer.parseInt(timelist.get(i).toString().toLowerCase().split("\\s+")[0].split(":")[0]);
                int c_mins = Integer.parseInt(timelist.get(i).toString().toLowerCase().split("\\s+")[0].split(":")[1]);

                if (hours==c_hours){
                    if (c_mins > mins){
                       // current_time_index = i;
                        current_index = i;

                        currrentTimeList.add("");
                        currrentTimeList.add("");
                        for (int j=current_index;j<timelist.size();j++){
                            currrentTimeList.add(timelist.get(j));
                        }
                        currrentTimeList.add("");
                        currrentTimeList.add("");
                        Log.e(TAG, "CURRENT_INDEX "+i);
                        Log.e(TAG, "CURRENT_TIME"+timelist.get(i));
                        break;
                    }
                }else if (hours<c_hours){
                    current_index = i;

                    currrentTimeList.add("");
                    currrentTimeList.add("");
                    for (int j=current_index;j<timelist.size();j++){
                        currrentTimeList.add(timelist.get(j));
                    }
                    currrentTimeList.add("");
                    currrentTimeList.add("");
                    Log.e(TAG, "CURRENT_INDEX "+i);
                    Log.e(TAG, "CURRENT_TIME"+timelist.get(i));
                    break;
                }

            }

        }*/
    }
    public void createTimeListForNext(){
        timelist = new ArrayList<>();

        timelist.add("");
        timelist.add("");


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();
        String onlyTime = simpleDateFormat.format(date);

        String am_pm = onlyTime.split("\\s+")[1];

        if (am_pm.equalsIgnoreCase("am")){
            genrateTime(1,1, "AM");
            genrateTime(1,1, "PM");
            genrateTime(1,1, "AM");
            genrateTime(1,1, "PM");
        }else {
            genrateTime(1,1, "PM");
            genrateTime(1,1, "AM");
            genrateTime(1,1, "PM");
            genrateTime(1,1, "AM");
        }



        timelist.add("");
        timelist.add("");


        if (timeAdapter!=null)
            timeAdapter.notifyDataSetChanged();
    }

    int current_index = 0;
    ArrayList<String> timelist;
    boolean done = false;
    private void genrateTime(int start,int min,String tag){

        String time = "";
        for (int i=start;i<=12;i++){
             for (int j=min;j<=4;j++){

                 if (i==12 && tag.toLowerCase().equalsIgnoreCase("am"))
                     tag = "PM";



                  if (j==1){
                      time = i+":"+"00 "+tag+"";
                  }else if (j==2){
                      time = i+":"+"15 "+tag+"";
                  }else if (j==3){
                      time = i+":"+"30 "+tag+"";
                  }else if (j==4){
                      time = i+":"+"45 "+tag+"";
                  }

                  min = 1;
                  timelist.add(time);

                 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.getDefault());
                 Date date = Calendar.getInstance().getTime();
                 String onlyTime = simpleDateFormat.format(date);

                 String am_pm = onlyTime.split("\\s+")[1];
                 String hrs = onlyTime.split("\\s+")[0].split(":")[0];
                 String mins = onlyTime.split("\\s+")[0].split(":")[1];

                 if (tag.toLowerCase().equalsIgnoreCase(am_pm)){
                     int thrs = Integer.parseInt(hrs);
                     if (thrs > 12)
                         thrs -= 12;

                     if (i==thrs){
                         int mymin = Integer.parseInt(mins);
                         int c_mins = Integer.parseInt(time.split("\\s+")[0].split(":")[1]);

                         if (mymin > c_mins && !done) {
                             current_time_index = timelist.size() - 1;
                         }else {
                             done =true;
                         }
                     }
                 }

             }
        }

    }



    int allPixelsDate = 0,finalWidthDate,itemWidthDate,paddingDate,firstItemWidthDate;



}
