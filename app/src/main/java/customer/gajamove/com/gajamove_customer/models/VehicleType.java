package customer.gajamove.com.gajamove_customer.models;

public class VehicleType {

    private String v_type,v_id,v_s_id,basic,price;
    private boolean isSelected;
    private int order_by;

    public VehicleType() {
        isSelected = false;
    }

    public VehicleType(String v_type, String v_id) {
        this.v_type = v_type;
        this.v_id = v_id;
    }

    public String getV_s_id() {
        return v_s_id;
    }

    public void setV_s_id(String v_s_id) {
        this.v_s_id = v_s_id;
    }

    public String getV_type() {
        return v_type;
    }

    public void setV_type(String v_type) {
        this.v_type = v_type;
    }

    public String getV_id() {
        return v_id;
    }

    public void setV_id(String v_id) {
        this.v_id = v_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getBasic() {
        return basic;
    }

    public void setBasic(String basic) {
        this.basic = basic;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getOrder_by() {
        return order_by;
    }

    public void setOrder_by(int order_by) {
        this.order_by = order_by;
    }
}
