package customer.gajamove.com.gajamove_customer.chat;

/**
 * Created by PC-GetRanked on 3/13/2018.
 */

public class FireBaseChatHead
{
    public static final String TGS_CHAT = "gaja_chat";
    public static final String MEMBERS = "members";
    public static final String BUMBLE_RIDE = "bumble_ride";

    public static String getUniqueChatId(String mem_id,String cust_id,String order_id)
    {
        return "chat_"+mem_id+"_"+cust_id+"_"+order_id;
    }
    public static String getUniqueRideId(String mem_id,String cust_id,String order_id)
    {
        return "Ride_"+mem_id+"_"+cust_id+"_"+order_id;
    }
}
