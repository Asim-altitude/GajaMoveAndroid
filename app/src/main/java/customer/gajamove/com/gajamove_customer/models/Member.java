package customer.gajamove.com.gajamove_customer.models;

import java.util.List;

/**
 * Created by Asim Shahzad on 12/5/2017.
 */
public class Member extends BaseModel
{

    private String mem_id;
    private String mem_name;
    private String mem_passport;
    private String mem_image;
    private String mem_lat;
    private String item_id;
    private String mem_lon;
    private String mem_phone;
    private String mem_email;
    private String mem_dob;
    private String mem_accepted_time;
    private String mem_reached_time;
    private String mem_address;
    private String distnace = "Away";
    private String distnace_km = "1";
    private boolean isSelected = false;
    private String[] service_names,service_ids;
    private int approved;
    private boolean disabled;
    private String completed_jobs;
    private String rating = "5.0";
    private String service_name = "";
    private List<RatingObject> ratings;

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    // private String mem_id;


    public String[] getService_ids() {
        return service_ids;
    }

    public void setService_ids(String[] service_ids) {
        this.service_ids = service_ids;
    }

    public String[] getService_names() {
        return service_names;
    }

    public void setService_names(String[] service_names) {
        this.service_names = service_names;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDistnace() {
        return distnace;
    }

    public void setDistnace(String distnace) {
        this.distnace = distnace;
    }

    public Member() {
        mem_image = "";
        mem_email = "abc@gmail.com";
    }

    public Member(String mem_id, String mem_name, String mem_passport, String mem_image, String mem_lat, String mem_lon, String mem_email, String mem_dob, String mem_address) {
        this.mem_id = mem_id;
        this.mem_name = mem_name;
        this.mem_passport = mem_passport;
        this.mem_image = mem_image;
        this.mem_lat = mem_lat;
        this.mem_lon = mem_lon;
        this.mem_email = mem_email;
        this.mem_dob = mem_dob;
        this.mem_address = mem_address;

        this.distnace = "Away";
    }


    public String getDistnace_km() {
        return distnace_km;
    }

    public void setDistnace_km(String distnace_km) {
        this.distnace_km = distnace_km;
    }

    public String getMem_phone() {
        return mem_phone;
    }

    public void setMem_phone(String mem_phone) {
        this.mem_phone = mem_phone;
    }

    public String getMem_id() {
        return mem_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public String getMem_name() {
        return mem_name;
    }

    public void setMem_name(String mem_name) {
        this.mem_name = mem_name;
    }

    public String getMem_passport() {
        return mem_passport;
    }

    public void setMem_passport(String mem_passport) {
        this.mem_passport = mem_passport;
    }

    public String getMem_image() {
        return mem_image;
    }

    public void setMem_image(String mem_image) {
        this.mem_image = mem_image;
    }

    public String getMem_lat() {
        return mem_lat;
    }

    public void setMem_lat(String mem_lat) {
        this.mem_lat = mem_lat;
    }

    public String getMem_lon() {
        return mem_lon;
    }

    public void setMem_lon(String mem_lon) {
        this.mem_lon = mem_lon;
    }

    public String getMem_email() {
        return mem_email;
    }

    public void setMem_email(String mem_email) {
        this.mem_email = mem_email;
    }

    public String getMem_dob() {
        return mem_dob;
    }

    public void setMem_dob(String mem_dob) {
        this.mem_dob = mem_dob;
    }

    public String getMem_address() {
        return mem_address;
    }

    public void setMem_address(String mem_address) {
        this.mem_address = mem_address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<RatingObject> getRatings() {
        return ratings;
    }

    public void setRatings(List<RatingObject> ratings) {
        this.ratings = ratings;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getCompleted_jobs() {
        return completed_jobs;
    }

    public void setCompleted_jobs(String completed_jobs) {
        this.completed_jobs = completed_jobs;
    }

    public String getMem_accepted_time() {
        return mem_accepted_time;
    }

    public void setMem_accepted_time(String mem_accepted_time) {
        this.mem_accepted_time = mem_accepted_time;
    }


    public String getMem_reached_time() {
        return mem_reached_time;
    }

    public void setMem_reached_time(String mem_reached_time) {
        this.mem_reached_time = mem_reached_time;
    }
}
