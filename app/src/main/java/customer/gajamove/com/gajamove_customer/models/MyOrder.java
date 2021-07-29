package customer.gajamove.com.gajamove_customer.models;

import java.io.Serializable;
import java.util.ArrayList;

public class MyOrder implements Serializable {

    private String order_id,order_date,main_service_name,driver_plate,basic_price,item_total,total_distance,service_name,pick_location,dest_location,order_total,additional_services,additional_prices,status;
    private boolean is_assigned,isExpanded;

    private String driver_name,driver_image,driver_id;

    private String cust_name,cust_image;
    private ArrayList<Prediction> predictionArrayList;


    public MyOrder() {
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getPick_location() {
        return pick_location;
    }

    public void setPick_location(String pick_location) {
        this.pick_location = pick_location;
    }

    public String getDest_location() {
        return dest_location;
    }

    public void setDest_location(String dest_location) {
        this.dest_location = dest_location;
    }

    public String getOrder_total() {
        return order_total;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public String getAdditional_services() {
        return additional_services;
    }

    public void setAdditional_services(String additional_services) {
        this.additional_services = additional_services;
    }

    public boolean isIs_assigned() {
        return is_assigned;
    }

    public void setIs_assigned(boolean is_assigned) {
        this.is_assigned = is_assigned;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getCust_image() {
        return cust_image;
    }

    public void setCust_image(String cust_image) {
        this.cust_image = cust_image;
    }

    public String getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(String total_distance) {
        this.total_distance = total_distance;
    }

    public String getAdditional_prices() {
        return additional_prices;
    }

    public void setAdditional_prices(String additional_prices) {
        this.additional_prices = additional_prices;
    }

    public String getMain_service_name() {
        return main_service_name;
    }

    public void setMain_service_name(String main_service_name) {
        this.main_service_name = main_service_name;
    }

    public String getItem_total() {
        return item_total;
    }

    public void setItem_total(String item_total) {
        this.item_total = item_total;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getBasic_price() {
        return basic_price;
    }

    public void setBasic_price(String basic_price) {
        this.basic_price = basic_price;
    }

    public ArrayList<Prediction> getPredictionArrayList() {
        return predictionArrayList;
    }

    public void setPredictionArrayList(ArrayList<Prediction> predictionArrayList) {
        this.predictionArrayList = predictionArrayList;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_image() {
        return driver_image;
    }

    public void setDriver_image(String driver_image) {
        this.driver_image = driver_image;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }


    public String getDriver_plate() {
        return driver_plate;
    }

    public void setDriver_plate(String driver_plate) {
        this.driver_plate = driver_plate;
    }
}
