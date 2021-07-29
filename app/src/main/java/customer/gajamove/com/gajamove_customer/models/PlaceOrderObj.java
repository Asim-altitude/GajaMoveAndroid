package customer.gajamove.com.gajamove_customer.models;

import java.util.List;

public class PlaceOrderObj extends BaseModel {

    private Prediction pickup,destination;
    private String date,display_date;
    private String service_name,payment_amount,service_id,service_type_id;
    private String order_total,user_balance,starting_price,extra_km;
    private String selected_service_image;
    private List<PriceService> priceServiceList;
    private List<Prediction> stopList;
    private boolean isMulti;
    private boolean isValid;
    private boolean isImmediate = true;

    public Prediction getPickup() {
        return pickup;
    }

    public void setPickup(Prediction pickup) {
        this.pickup = pickup;
    }

    public Prediction getDestination() {
        return destination;
    }

    public void setDestination(Prediction destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getService_type_id() {
        return service_type_id;
    }

    public void setService_type_id(String service_type_id) {
        this.service_type_id = service_type_id;
    }

    public String getOrder_total() {
        return order_total;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public String getStarting_price() {
        return starting_price;
    }

    public void setStarting_price(String starting_price) {
        this.starting_price = starting_price;
    }

    public String getExtra_km() {
        return extra_km;
    }

    public void setExtra_km(String extra_km) {
        this.extra_km = extra_km;
    }

    public String getSelected_service_image() {
        return selected_service_image;
    }

    public void setSelected_service_image(String selected_service_image) {
        this.selected_service_image = selected_service_image;
    }

    public List<PriceService> getPriceServiceList() {
        return priceServiceList;
    }

    public void setPriceServiceList(List<PriceService> priceServiceList) {
        this.priceServiceList = priceServiceList;
    }

    public List<Prediction> getStopList() {
        return stopList;
    }

    public void setStopList(List<Prediction> stopList) {
        this.stopList = stopList;
    }

    public boolean isMulti() {
        return isMulti;
    }

    public void setMulti(boolean multi) {
        isMulti = multi;
    }

    public String getUser_balance() {
        return user_balance;
    }

    public void setUser_balance(String user_balance) {
        this.user_balance = user_balance;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(String payment_amount) {
        this.payment_amount = payment_amount;
    }

    public boolean isImmediate() {
        return isImmediate;
    }

    public void setImmediate(boolean immediate) {
        isImmediate = immediate;
    }

    public String getDisplay_date() {
        return display_date;
    }

    public void setDisplay_date(String display_date) {
        this.display_date = display_date;
    }
}
