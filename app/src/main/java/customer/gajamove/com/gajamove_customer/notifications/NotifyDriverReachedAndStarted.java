package customer.gajamove.com.gajamove_customer.notifications;

/**
 * Created by PC-GetRanked on 10/19/2018.
 */

public interface NotifyDriverReachedAndStarted
{
    void onDriverReached(String name, String body, String order_id);
    void onDriverStartedRide(String name, String body, String order_id);
}
