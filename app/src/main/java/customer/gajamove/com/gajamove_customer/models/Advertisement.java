package customer.gajamove.com.gajamove_customer.models;

import java.io.Serializable;

public class Advertisement implements Serializable {

    private String ad_url;
    private String ad_image;

    public Advertisement() {
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getAd_image() {
        return ad_image;
    }

    public void setAd_image(String ad_image) {
        this.ad_image = ad_image;
    }
}
