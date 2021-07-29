package customer.gajamove.com.gajamove_customer.utils;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Math.atan;

public class  AnimationUtils {



    public static ValueAnimator polylineAnimator(){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(4000);
        return valueAnimator;
    }



    public static ValueAnimator carAnimator(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new LinearInterpolator());

        return valueAnimator;
    }

    public static double getRotation(LatLng start,LatLng end){

        double latDifference = Math.abs(start.latitude - end.latitude);
        double lngDifference = Math.abs(start.longitude - end.longitude);

        double rotation = -1F;

        if (start.latitude <end.latitude && start.longitude <end.longitude)
            rotation = Math.toDegrees(Math.atan(lngDifference/latDifference));
        else if (start.latitude >= end.latitude && start.longitude <end.longitude)
            rotation = (90 - Math.toDegrees(Math.atan(lngDifference/latDifference))) + 90;
        else if (start.latitude >= end.latitude && start.longitude >= end.longitude)
            rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180);
        else if (start.latitude < end.latitude && start.longitude >= end.longitude)
            rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270);


        return rotation;

    }


}
