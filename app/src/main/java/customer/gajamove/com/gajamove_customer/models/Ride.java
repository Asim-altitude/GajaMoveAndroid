package customer.gajamove.com.gajamove_customer.models;

import java.util.ArrayList;

/**
 * Created by PC-GetRanked on 9/4/2018.
 */

public class Ride extends BaseModel {

    public Ride() {

    }

    private String order_id="",pickup_loc,destination_loc;
    private RideStatus rideStatus;
    private Member memberLocationObject;

    private boolean isMulti, hasMember= false;
    private ArrayList<Prediction> predictionArrayList;

    public RideStatus getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(RideStatus rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getPickup_loc() {
        return pickup_loc;
    }

    public void setPickup_loc(String pickup_loc) {
        this.pickup_loc = pickup_loc;
    }

    public String getDestination_loc() {
        return destination_loc;
    }

    public void setDestination_loc(String destination_loc) {
        this.destination_loc = destination_loc;
    }

    private LatLong meet_location,drop_location;

    public LatLong getMeet_location() {
        return meet_location;
    }

    public void setMeet_location(LatLong meet_location) {
        this.meet_location = meet_location;
    }

    public LatLong getDrop_location() {
        return drop_location;
    }

    public void setDrop_location(LatLong drop_location) {
        this.drop_location = drop_location;
    }

    public boolean isMulti() {
        return isMulti;
    }

    public void setMulti(boolean multi) {
        isMulti = multi;
    }

    public ArrayList<Prediction> getPredictionArrayList() {
        return predictionArrayList;
    }

    public void setPredictionArrayList(ArrayList<Prediction> predictionArrayList) {
        this.predictionArrayList = predictionArrayList;
    }

    public Member getMemberLocationObject() {
        return memberLocationObject;
    }

    public void setMemberLocationObject(Member memberLocationObject) {
        this.memberLocationObject = memberLocationObject;
    }

    public boolean isHasMember() {
        return hasMember;
    }

    public void setHasMember(boolean hasMember) {
        this.hasMember = hasMember;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}
