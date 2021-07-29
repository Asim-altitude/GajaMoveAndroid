package customer.gajamove.com.gajamove_customer.notifications;

/**
 * Created by PC-GetRanked on 6/19/2018.
 */

public interface BodyguardFoundListner {
    void onBodyguardFound(String order_id);
    void onBodyguardFound(String name, String body, String order_id);
    void onBodyguardFound(String name, String body, String order_id, String image);
}
