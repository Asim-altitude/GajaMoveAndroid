package customer.gajamove.com.gajamove_customer.chat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.Profile_Screen;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.Customer;
import customer.gajamove.com.gajamove_customer.models.Member;
import customer.gajamove.com.gajamove_customer.notifications.FcmMessagingService;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

public class ChatActivity extends AppCompatActivity {

    private Member member;
    private FirebaseDatabase firebaseDatabase;
    private String chat_id = null;
    private SharedPreferences settings;
    private Customer customer;
    private EditText message_box;
    private ImageView send_btn;
    private RecyclerView chat_recycler;
    private ChatListAdapter chatListAdapter;
    private List<Chat_Message> chat_list;
     java.util.Calendar c;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constants.isChatting = false;
    }

    private String order_id;

    public void clearNotfcations()
    {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(10);
        FcmMessagingService.count = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_chat);

        setupToolBar();
        c = java.util.Calendar.getInstance();

        chat_recycler = (RecyclerView) findViewById(R.id.chat_list);
        message_box = (EditText) findViewById(R.id.message_box);
        send_btn = (ImageView) findViewById(R.id.send_btn);

        Constants.isChatting = true;

        clearNotfcations();

        settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String name = settings.getString(Constants.PREFS_USER_NAME,"");
        String id = settings.getString(Constants.PREFS_CUSTOMER_ID,"");
        String image = settings.getString(Constants.PREFS_USER_IMAGE,"");
        String lat = settings.getString("lastLocationLatitude","");
        String lng = settings.getString("lastLocationLongitude","");
        customer = new Customer(name,id,image,lat,lng);

        member = (Member) getIntent().getSerializableExtra(Constants.MEMBER_CHAT_TAG);

        chat_id = getIntent().getStringExtra("chat_id");

        order_id = getIntent().getStringExtra("order_id");

        if (chat_id==null)
            chat_id = FireBaseChatHead.getUniqueChatId(member.getMem_id(),customer.getC_id(),order_id);

        chat_list = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chat_list,ChatActivity.this);
        chat_recycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this, RecyclerView.VERTICAL,false));
        chat_recycler.setAdapter(chatListAdapter);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message_box.getText().toString().equals(""))
                {
                    try
                    {
                        Chat_Message message = new Chat_Message();
                        message.setCustomer(customer);
                        message.setMember(member);
                        DateFormat format1 = new SimpleDateFormat("dd MMM yyyy  hh:mm", Locale.ENGLISH);
                        String time = format1.format(c.getTime());
                        String am_pm;
                        int check = c.get(java.util.Calendar.AM_PM);
                        if (check==1)
                            am_pm = "PM";
                        else
                            am_pm = "AM";
                        time = time+" "+am_pm;
                        Message message1 = new Message(message_box.getText().toString(),time, "cust");
                        message1.setShown(false);
                        message1.setServer_time(ServerValue.TIMESTAMP);
                        message.setMessage(message1);


                        String key = firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).push().getKey();
                        firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).child(key).setValue(message);
                        message_box.setText("");
                        sendNotifcationCustomer(member.getMem_id(),customer.getC_id(),message1.getMessage_text(),chat_id);
                        showSoftwareKeyboard(false);
                        chat_recycler.smoothScrollToPosition(chat_list.size()-1);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        loadAllOldData();

        AddChatValuChangeListner();
    }

    public void setListViewHeightBasedOnChildren() {

        /*try {

            ListAdapter listAdapter = visitorList.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, visitorList);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = visitorList.getLayoutParams();
            params.height = totalHeight + (visitorList.getDividerHeight() * (listAdapter.getCount() - 1));
            visitorList.setLayoutParams(params);
            visitorList.requestLayout();

            View v = getActivity().getCurrentFocus();
            if (v != null)
                v.clearFocus();

        } catch (Exception e) {
            Log.e("cause of error", e.getMessage());
        }*/
    }

    private void setupToolBar(){

        ImageView back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Chat");
    }


    private void AddChatValuChangeListner()
    {
        try {
            firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try
                    {
                        Chat_Message message = dataSnapshot.getValue(Chat_Message.class);
                        Log.e("sender",message.getMessage().getSender());

                        try {
                            if (message.getCustomer() != null) {
                                if (!message.getCustomer().getC_image().contains("staging")) {
                                    message.getCustomer().getC_image().replaceAll("gajamove.com", "staging.gajamove.com");
                                }
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        chat_list.add(message);
                        chatListAdapter.notifyDataSetChanged();

                        if (member==null)
                            member = message.getMember();

                        chat_recycler.smoothScrollToPosition(chat_list.size()-1);

                        message.getMessage().setShown(true);
                        updateMessageStatus(chat_id,dataSnapshot.getKey(),message);

                        setTitle(member.getMem_name());

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            Log.e("error",e.getMessage());
        }
    }

    private void updateMessageStatus(String chat_id,String key, Chat_Message message_val)
    {
        firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).child(key).setValue(message_val);

    }

    private void loadAllOldData()
    {
        try {
            firebaseDatabase.getReference().child(FireBaseChatHead.TGS_CHAT).child(chat_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try
                    {
                        String s = new String();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            Log.e("error",e.getMessage());
        }
    }

    protected void showSoftwareKeyboard(boolean showKeyboard) {
        try {
            final AppCompatActivity activity = ChatActivity.this;
            final InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.isChatting = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constants.isChatting = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.isChatting = true;
    }

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private void sendNotifcationCustomer(String member_id,String customer_id,String messge,String chat_id)
    {
        asyncHttpClient.setConnectTimeout(30000);

        Log.e("url",Constants.Host_Address + "customers/send_chat_notification_to_member/" +member_id+"/"+customer_id+"/"+chat_id+"/"+messge+"/tgs_appkey_amin");
        asyncHttpClient.get(ChatActivity.this,Constants.Host_Address + "customers/send_chat_notification_to_member/" +member_id+"/"+customer_id+"/"+chat_id+"/"+messge+"/"+UtilsManager.getApiKey(ChatActivity.this)+"", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.e("response ",response);
                }
                catch (Exception e)
                {
                    Log.e("error_notification",e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String response = new String(responseBody);
                    Log.e("response ",response);
                }
                catch (Exception e)
                {
                    Log.e("error_notification",e.getMessage());
                }
            }
        });


    }

}
