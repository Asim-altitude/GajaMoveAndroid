package customer.gajamove.com.gajamove_customer.auth;

import androidx.appcompat.app.AppCompatActivity;
import customer.gajamove.com.gajamove_customer.MyApp ;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.adapter.IndustryAdapter;
import customer.gajamove.com.gajamove_customer.models.Industry;
import customer.gajamove.com.gajamove_customer.models.SubIndustry;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChooseIndustryScreen extends AppCompatActivity {
    private static final String TAG = "ChooseIndustryScreen";

    private void includeActionBar(){
        ImageView back = findViewById(R.id.backBtn);
        TextView title = findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.industry));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    ExpandableListView indutry_list;
    ArrayList<Industry> industries;
    IndustryAdapter industryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_industry_screen);
        includeActionBar();
        Constants.enableSSL(asyncHttpClient);
        indutry_list = findViewById(R.id.industry_list);

        industries = new ArrayList<>();

        indutry_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                if (industries.get(i).getSubIndustries().size()==0){
                    String indust_id = industries.get(i).getId();
                    String name = industries.get(i).getName();

                    Intent intent = new Intent();
                    intent.setData(Uri.parse(indust_id+"#"+name));
                    setResult(RESULT_OK, intent);
                    finish();

                }else {

                }
                return false;
            }
        });

        indutry_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

               // String indust_id = industries.get(i).getId();
                String sub_id = industries.get(i).getSubIndustries().get(i1).getId();

                String name = industries.get(i).getName() + "/" +industries.get(i).getSubIndustries().get(i1).getName();

                Intent intent = new Intent();
                intent.setData(Uri.parse(sub_id+"#"+name));
                setResult(RESULT_OK, intent);
                finish();

                return true;
            }
        });


       // loadData();

        getIndustries();


    }


    private void loadData(){
        industries = new ArrayList<>();

        Industry industry = new Industry();
        industry.setId("1");
        industry.setName("E-Commerce");

        ArrayList<SubIndustry> subIndustries = new ArrayList<>();
        SubIndustry subIndustry = new SubIndustry();
        subIndustry.setId("10");
        subIndustry.setName("E-Com 1");

        subIndustries.add(subIndustry);
        subIndustry = new SubIndustry();
        subIndustry.setId("10");
        subIndustry.setName("E-Com 2");

        subIndustries.add(subIndustry);
        subIndustry = new SubIndustry();
        subIndustry.setId("10");
        subIndustry.setName("E-Com 3");

        subIndustries.add(subIndustry);

        industry.setSubIndustries(subIndustries);

        industries.add(industry);


        industry = new Industry();
        industry.setId("1");
        industry.setName("Industry 2");

        subIndustries = new ArrayList<>();
        subIndustry = new SubIndustry();
        subIndustry.setId("10");
        subIndustry.setName("Industry 2/1");

        subIndustries.add(subIndustry);
        subIndustry = new SubIndustry();
        subIndustry.setId("10");
        subIndustry.setName("Industry 2/2");

        subIndustries.add(subIndustry);


        industry.setSubIndustries(subIndustries);

        industries.add(industry);


        industry = new Industry();
        industry.setId("1");
        industry.setName("Empty Industry");

        subIndustries = new ArrayList<>();

        industry.setSubIndustries(subIndustries);

        industries.add(industry);



    }

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    ArrayList<SubIndustry> subIndustries;
    private void getIndustries() {


        asyncHttpClient.get(Constants.Host_Address + "get_industries/"+ UtilsManager.getApiKey(ChooseIndustryScreen.this)+"", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {

                    String res = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+res);
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    industries = new ArrayList<>();
                    subIndustries = new ArrayList<>();
                    for (int i=0;i<jsonArray.length();i++){


                        String name = jsonArray.getJSONObject(i).getString("name");
                        String id = jsonArray.getJSONObject(i).getString("id");
                        String industry_type = jsonArray.getJSONObject(i).getString("industry_type");
                        String parent_id = jsonArray.getJSONObject(i).getString("parent_id");

                        if (industry_type.equalsIgnoreCase("0")){

                            Industry industry = new Industry();
                            industry.setId(id);
                            industry.setName(name);

                            industries.add(industry);

                        } else {
                            SubIndustry subIndustryindustry = new SubIndustry();
                            subIndustryindustry.setId(id);
                            subIndustryindustry.setName(name);
                            subIndustryindustry.setParent_id(parent_id);

                            subIndustries.add(subIndustryindustry);
                        }



                    }


                    for (int i=0;i<industries.size();i++){

                        ArrayList<SubIndustry> subIndustries1  = new ArrayList<>();

                        for (int j=0;j<subIndustries.size();j++){
                            if (industries.get(i).getId().equalsIgnoreCase(subIndustries.get(j).getParent_id())){
                                subIndustries1.add(subIndustries.get(j));
                            }
                        }

                        industries.get(i).setSubIndustries(subIndustries1);

                    }


                    industryAdapter = new IndustryAdapter(ChooseIndustryScreen.this,industries);
                    indutry_list.setAdapter(industryAdapter);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {

                    String res = new String(responseBody);
                    Log.e(TAG, "onFailure: "+res);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

}
