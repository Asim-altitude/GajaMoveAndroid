package customer.gajamove.com.gajamove_customer.models;

import java.io.Serializable;

/**
 * Created by Asim Shahzad on 12/29/2017.
 */
public class Service_Slot implements Serializable
{

    private String slot_id,service_id,slot_name,image,order_format,description;
    private String selected_id;


    public Service_Slot(String slot_id, String slot_name, boolean isActive) {
        this.slot_id = slot_id;
        this.slot_name = slot_name;
        this.isActive = isActive;
        this.selected_id = "-1";
    }

    public String getSelected_id() {
        return selected_id;
    }

    public void setSelected_id(String selected_id) {
        this.selected_id = selected_id;
    }

    public Service_Slot() {
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    private boolean isActive;

    public Service_Slot(String slot_id, String slot_name) {
        this.slot_id = slot_id;
        this.slot_name = slot_name;
    }

    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public String getSlot_name() {
        return slot_name;
    }

    public void setSlot_name(String slot_name) {
        this.slot_name = slot_name;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrder_format() {
        return order_format;
    }

    public void setOrder_format(String order_format) {
        this.order_format = order_format;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
