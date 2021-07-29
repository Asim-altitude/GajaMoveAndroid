package customer.gajamove.com.gajamove_customer.utils;

public interface DeleteCallback {
    void onItemDelete(int pos);
    void onLocationRequest(int pos);
    void onChangeLocation(int pos);
    void onCurrentLocationReq();
}
