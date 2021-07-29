package customer.gajamove.com.gajamove_customer.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Industry implements Serializable {
    String id,name;

    ArrayList<SubIndustry> subIndustries;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SubIndustry> getSubIndustries() {
        return subIndustries;
    }

    public void setSubIndustries(ArrayList<SubIndustry> subIndustries) {
        this.subIndustries = subIndustries;
    }
}
