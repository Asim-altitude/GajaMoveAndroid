package customer.gajamove.com.gajamove_customer.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    private List<Chat_Message> chat_list;
    private Context context;
    private View layoutView;

    public ChatListAdapter(List<Chat_Message> chat_list, Context context) {
        this.chat_list = chat_list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        MyViewHolder _ViewHolder = new MyViewHolder(layoutView);

        return _ViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Chat_Message message = chat_list.get(position);

        if (message.getMessage().getSender().equalsIgnoreCase("mem")) {

            try {
                holder.mem_layout.setVisibility(View.VISIBLE);
                holder.cust_layout.setVisibility(View.GONE);
                holder.chat_message.setText(message.getMessage().getMessage_text());
               // holder.chat_time.setText(convertToReadableDateTime(message.getMessage().getServerTimeValue()));
                holder.chat_time.setText(message.getMessage().getDate_time());
                if (context!=null && message.getMember()!=null)
                    Picasso.with(context).load(message.getMember().getMem_image()).into(holder.chat_icon);


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                holder.cust_layout.setVisibility(View.VISIBLE);
                holder.mem_layout.setVisibility(View.GONE);
                holder.cust_chat_message.setText(message.getMessage().getMessage_text());
                //holder.cust_chat_time.setText(convertToReadableDateTime(chat_list.get(position).getMessage().getServerTimeValue()));
                holder.cust_chat_time.setText(message.getMessage().getDate_time());
                if (context!=null && message.getCustomer()!=null)
                    Picasso.with(context).load(message.getCustomer().getC_image()).into(holder.cust_chat_icon);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


        if (position!=0)
        {
            if (shouldShowdate(position,message.getMessage().getServerTimeValue()))
            {
                chat_list.get(position).getMessage().setShow_this_date(true);
            }
        }
        else
        {
            saveDate(position,message.getMessage().getServerTimeValue());
            chat_list.get(position).getMessage().setShow_this_date(true);
        }
    }

    private void saveDate(int position,Long datetime)
    {
        try {
            if (datetime != null) {
                Date date = new Date(datetime);
                SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd yyyy");
                String date_formated = dateformat.format(date);

                chat_list.get(position).getMessage().setDate(date_formated);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean shouldShowdate(int position,Long datetime)
    {

        try {
            if (datetime!=null) {
                Date date = new Date(datetime);
                SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd yyyy");
                String date_formated = dateformat.format(date);
                calendar = Calendar.getInstance();
                calendar.setTime(new Date(date_formated));

                chat_list.get(position).getMessage().setDate(date_formated);

                if (!chat_list.get(position - 1).getMessage().getDate().equalsIgnoreCase(date_formated))
                    return true;
                else
                    return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        return false;
    }

    String am_pm = "";
    Calendar calendar;
    private String convertToReadableDateTime(Long datetime) {
        try {
            if (datetime!=null) {
                Date date = new Date(datetime);
                SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
                String date_formated = dateformat.format(date);
                calendar = Calendar.getInstance();
                calendar.setTime(new Date(date_formated));

                if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) {
                    am_pm = "pm";
                } else {
                    am_pm = "am";
                }

                return dateformat.format(date).toString() + " " + am_pm;
            }
        }
        catch (Exception e)
        {
            return "";
        }

        return "";
    }

    @Override
    public int getItemCount() {
        return chat_list.size();
    }

    //holder
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView chat_message,chat_time;
        public CircleImageView chat_icon;

        public TextView cust_chat_message,cust_chat_time;
        public CircleImageView cust_chat_icon;

        private RelativeLayout mem_layout,cust_layout;


        public MyViewHolder(View itemView) {
            super(itemView);
            chat_icon = (CircleImageView) itemView.findViewById(R.id.chat_image);
            chat_message = (TextView) itemView.findViewById(R.id.chat_message);
            chat_time = (TextView) itemView.findViewById(R.id.chat_message_time);

            cust_chat_icon = (CircleImageView) itemView.findViewById(R.id.cust_chat_image);
            cust_chat_message = (TextView) itemView.findViewById(R.id.cust_chat_message);
            cust_chat_time = (TextView) itemView.findViewById(R.id.cust_chat_message_time);

            mem_layout = (RelativeLayout) itemView.findViewById(R.id.member_item);
            cust_layout = (RelativeLayout) itemView.findViewById(R.id.customer_item);
        }

    }
}
