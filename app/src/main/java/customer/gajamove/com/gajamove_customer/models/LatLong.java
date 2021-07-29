package customer.gajamove.com.gajamove_customer.models;


/**
 * Created by PC-GetRanked on 9/5/2018.
 */

public class LatLong extends BaseModel {

   private double latitude,longitude;

    public LatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
