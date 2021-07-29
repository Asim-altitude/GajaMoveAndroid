package customer.gajamove.com.gajamove_customer.chat;



import java.util.Map;

import customer.gajamove.com.gajamove_customer.models.BaseModel;

/**
 * Created by PC-GetRanked on 3/13/2018.
 */

public class Message extends BaseModel
{
    private String message_text;
    private String date_time;
    private String sender;
    private Map<String,String> server_time;
    private boolean isShown;

    private String date;
    private boolean show_this_date = false;

    public boolean isShow_this_date() {
        return show_this_date;
    }

    public void setShow_this_date(boolean show_this_date) {
        this.show_this_date = show_this_date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Message() {
    }

    public Message(String message_text, String date_time, String sender) {
        this.message_text = message_text;
        this.date_time = date_time;
        this.sender = sender;
    }



    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Map<String, String> getServer_time() {
        return server_time;
    }

    public void setServer_time(Object server_time) {
        if (server_time instanceof Map)
            this.server_time = (Map<String, String>) server_time;
        else if (server_time instanceof Long)
            this.server_time_stamp = (Long) server_time;
    }

    private Long server_time_stamp;

    public Long getServerTimeValue()
    {
        return server_time_stamp;
    }
}
