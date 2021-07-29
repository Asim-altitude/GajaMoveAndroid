package customer.gajamove.com.gajamove_customer.notifications;


import customer.gajamove.com.gajamove_customer.models.Member;

/**
 * Created by PC-GetRanked on 7/11/2018.
 */

public interface NotifyCancelRequest {
    void onCancelRequestReceived(Member member, String order_id);
}
