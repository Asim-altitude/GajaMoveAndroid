package customer.gajamove.com.gajamove_customer.sos;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.MyApp;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.EmailContact;
import customer.gajamove.com.gajamove_customer.models.PhoneContact;
import customer.gajamove.com.gajamove_customer.utils.Constants;
import customer.gajamove.com.gajamove_customer.utils.UtilsManager;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Asim Shahzad on 12/13/2017.
 */
public class SosPhoneFragment extends Fragment implements View.OnClickListener, ContactUpdateRequest {

    private AsyncHttpClient asHclient = new AsyncHttpClient();
    private String TAG = "AddPhoneFragment";

   // static private Context c;
   private String  accessToken,customerId;

    private RecyclerView phone_recycler;
    private FloatingActionButton add_phone_btn;

    private List<PhoneContact> contactList,selectedContactsList,savedContactList;

    private SOSPhoneListAdater contactsAdapater;

    private  int saved_contacts_num =0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sos_phone_fragment,container,false);

        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        accessToken = settings.getString(Constants.PREFS_ACCESS_TOKEN, "");
        customerId = settings.getString(Constants.PREFS_CUSTOMER_ID, "");
            phone_recycler = (RecyclerView) rootView.findViewById(R.id.sos_phone_recycler);
            add_phone_btn = (FloatingActionButton) rootView.findViewById(R.id.sos_add_phone_btn);

            add_phone_btn.setOnClickListener(this);

            selectedContactsList = new ArrayList<>();

            message = rootView.findViewById(R.id.audio_message);

        accessToken = UtilsManager.getApiKey(getActivity());

        getAllSavedContacts();

       // saveContactOnServer();
        return rootView;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.sos_add_phone_btn:
                if (savedContactList==null)
                    savedContactList = new ArrayList<>();

                saved_contacts_num = savedContactList.size();
                if (saved_contacts_num<3)
                    LoadContacts();
                else
                    UtilsManager.showAlertMessage(getContext(), "", "Maximum 3 numbers Allowed");
                break;
        }

    }

    private static final int CONTACTS_CODE = 0011;
    private void LoadContacts()
    {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},CONTACTS_CODE);
        }
        else
        {
            ReadAllContacts();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case CONTACTS_CODE:
            if (requestCode == CONTACTS_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ReadAllContacts();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isAlreadyAdded(String number)
    {
        boolean isAdded = false;
        for (int i=0;i<contactList.size();i++)
        {
            String contact_1 = contactList.get(i).getNumber().replace(" ", "");;
            String contact_2 = number.replace(" ","");

            if (contact_1.equalsIgnoreCase(contact_2))
            {
                isAdded = true;
                break;
            }
        }



        return isAdded;
    }

    private void ReadAllContacts() {
        contactList = new ArrayList<>();

        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phones.moveToNext()) {

           String name =  phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
           String number =  phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            PhoneContact contact = new PhoneContact(name,number,false,"");

         /*   if (!isAlreadyAdded(number) && shouldAdd(contact))
                contactList.add(contact);
*/
            if (!isAlreadyAdded(number))
                contactList.add(contact);

        }

        phones.close();

        Log.e("contacts list size", contactList.size() + "");
      //  UtilsManager.showAlertMessage(getContext(), contactList.get(0).getName(), contactList.get(0).getNumber());

        contactListAdapter  = new ContactListAdapter(getContext(), contactList, contactSelectionCallback);

        showContactChooser(contactListAdapter);
      //  phone_recycler.setAdapter(contactListAdapter);

    }
     ContactListAdapter contactListAdapter;

    private ContactSelectionCallback contactSelectionCallback = new ContactSelectionCallback() {
        @Override
        public void onContactClick(int index,PhoneContact contact) {

            if (savedContactList==null)
                savedContactList= new ArrayList<>();



                if (contact.isSelected()) {

                   // contactList.get(index).setIsSelected(false);
                    contact.setIsSelected(false);
                    removeFromList(contact);
                    saved_contacts_num--;

                }
                else if (saved_contacts_num<3)
                {

                   // contactList.get(index).setIsSelected(true);
                    if (shouldAdd(contact)) {
                        contact.setIsSelected(true);
                        selectedContactsList.add(contact);
                        saved_contacts_num++;
                    }
                    else
                    {
                        UtilsManager.showAlertMessage(getContext(),"","Already added");
                    }
                }


            contactListAdapter.notifyDataSetChanged();
        }
    };

    private void removeFromList(PhoneContact contact) {

        if (selectedContactsList==null)
            selectedContactsList = new ArrayList<>();

        for (int i =0;i<selectedContactsList.size();i++)
        {
            if (selectedContactsList.get(i).getNumber().equalsIgnoreCase(contact.getNumber()))
                selectedContactsList.remove(i);
        }

    }

    private boolean shouldAdd(PhoneContact contact)
    {
        if (savedContactList==null)
            return true;

        boolean shouldAdd = true;
        for (int i =0;i<savedContactList.size();i++)
        {
            if (savedContactList.get(i).getName().equalsIgnoreCase(contact.getName()))
            {
                shouldAdd = false;
                break;
            }

        }

        return shouldAdd;
    }

    private void showContactChooser(final ContactListAdapter contactListAdapter) {

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.contact_chooser_layout);


        if (selectedContactsList!=null)
            selectedContactsList.clear();

        if (savedContactList==null)
            savedContactList = new ArrayList<>();

        for (int i = 0;i<savedContactList.size();i++)
        {
            for (int j =0;j<contactList.size();j++)
            {
                if (savedContactList.get(i).getNumber().equalsIgnoreCase(contactList.get(j).getNumber()))
                    contactList.remove(j);
            }

        }

       final RecyclerView contactsList = (RecyclerView) dialog.findViewById(R.id.contact_list_recycler);
       final Button done = (Button) dialog.findViewById(R.id.done_btn);
        EditText searchView = (EditText) dialog.findViewById(R.id.search_edittext);
        final TextView status = (TextView) dialog.findViewById(R.id.status_txt);



        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contactsList.getLayoutParams();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                contactListAdapter.getFilter().filter(s);
                if (count == 0) {
                    params.height = dpToPx(20);
                    contactsList.setLayoutParams(params);
                    done.setVisibility(View.GONE);
                    status.setVisibility(View.VISIBLE);
                } else {
                    params.height = dpToPx(150);
                    contactsList.setLayoutParams(params);
                    done.setVisibility(View.VISIBLE);
                    status.setVisibility(View.GONE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                contactListAdapter.getFilter().filter(s.toString());
                if (s.equals(""))
                    contactsList.setVisibility(View.GONE);
                else
                    contactsList.setVisibility(View.VISIBLE);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // progressDialog = ProgressDialog.show(getActivity(), "", "Syncing Contacts", true, true);
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<String> phone = new ArrayList<String>();

                if (selectedContactsList != null) {

                    for (int i = 0; i < selectedContactsList.size(); i++) {

                        names.add(selectedContactsList.get(i).getName());
                        phone.add(selectedContactsList.get(i).getNumber());

                    }

                  /*  contactsAdapater = new SOSPhoneListAdater(getContext(),selectedContactsList);
                    phone_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    saved_contacts_num = selectedContactsList.size();
                    contactsAdapater.setUpdateCallback(SosPhoneFragment.this);
                    phone_recycler.setAdapter(contactsAdapater);*/

                    saveContactOnServer(names, phone);
                }
                // progressDialog = ProgressDialog.show(getActivity(), "", "Syncing Contacts", true, true);
            }
        });

        contactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        contactListAdapter.selectedContactsNum = saved_contacts_num;
        contactsList.setAdapter(contactListAdapter);


        dialog.show();
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    @Override
    public void onContactUpdateRequest(PhoneContact contact, int index) {
        openEditDialog(contact);
    }

    @Override
    public void onEmailUpdateRequest(EmailContact emailContact, int index) {

    }

    @Override
    public void onEmailDeleteRequest(EmailContact contact, int index) {
        
    }

    @Override
    public void onPhoneDeleteRequest(PhoneContact contact,int index) {
        ConfirmDelection(contact, index);
    }



    private class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> implements Filterable
    {
        private Context context;
        private List<PhoneContact> list,contactfilteredList;
        private ContactSelectionCallback callback;

        private LayoutInflater layoutInflater;

        private boolean isAllowed = true;
        public int selectedContactsNum = 0;

        public boolean visibility = false;


        public ContactListAdapter(Context context, List<PhoneContact> list, ContactSelectionCallback callback) {
            this.context = context;
            this.list = list;
            this.contactfilteredList = list;
            this.callback = callback;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.contact_row, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            PhoneContact contact = contactfilteredList.get(position);

            holder.name.setText(contact.getName());
            holder.number.setText(contact.getNumber());

            holder.contact_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onContactClick(position,contactfilteredList.get(position));
                }
            });

            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.contact_layout.performClick();
                }
            });
            holder.number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.contact_layout.performClick();
                }
            });

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.contact_layout.performClick();
                }
            });


            if (contactfilteredList.get(position).isSelected())
                holder.checkBox.setChecked(true);
            else
                holder.checkBox.setChecked(false);

            if (visibility==true)
                holder.contact_layout.setVisibility(View.VISIBLE);
            else
                holder.contact_layout.setVisibility(View.INVISIBLE);


        }

        @Override
        public int getItemCount() {
            return contactfilteredList.size();
        }



        class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView name;
            TextView number;
            CheckBox checkBox;
            LinearLayout contact_layout;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.contact_name);
                number = (TextView) itemView.findViewById(R.id.contact_number);
                checkBox = (CheckBox) itemView.findViewById(R.id.contacts_chekbox);
                contact_layout = (LinearLayout) itemView.findViewById(R.id.contact_row_layout);
            }
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    visibility = true;
                    if (charString.isEmpty()) {
                        contactfilteredList.clear();
                    } else {
                        List<PhoneContact> filteredList = new ArrayList<>();
                        for (PhoneContact row : list) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
                                filteredList.add(row);
                            }
                        }

                        contactfilteredList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = contactfilteredList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    contactfilteredList = (ArrayList<PhoneContact>) filterResults.values;
                    notifyDataSetChanged();
                }
            };

        }


    }

    private interface ContactSelectionCallback
    {
        void onContactClick(int index, PhoneContact contact);
    }

    //SEnd selected contacts to server

    private ProgressDialog progressDialog;
    private void saveContactOnServer(ArrayList<String> names, ArrayList<String> contacts)
    {
        asHclient.setURLEncodingEnabled(false);
        asHclient.setTimeout(60000);
        RequestParams params = new RequestParams();

        params.put("key", accessToken);
        params.put("customer_id",customerId);
        params.put("type","1");
        //PUT SELECTED CONTACTS HERE

        params.put("contact_name",names.toArray(new String[]{}));
        params.put("contact_phone",contacts.toArray(new String[]{}));

        asHclient.post(Constants.Host_Address + "sos/add_sos_contact_api", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(getContext(),R.style.DialogTheme);
                progressDialog.setMessage("Syncing contacts");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    final String responseString = new String(responseBody, "UTF-8");

                    Log.d("RESPONSE SUCCESS", responseString);
                    JSONObject responseJsonObject = new JSONObject(responseString);
                    String status = responseJsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                        if (responseJsonObject.has("data"))
                            parseData(responseJsonObject);
                     }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                try
                {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    final String responseString = new String(responseBody, "UTF-8");
                    Log.e(TAG+" Syncing Failed",responseString);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
                try {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    Log.e(TAG+" On Retry ",retryNo+"");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


    }

    TextView message;

    private void getAllSavedContacts()
    {
        try
        {

           /* savedContactList = new ArrayList<>();
            PhoneContact phoneContact = new PhoneContact("Dad","000000",true,"1");
            PhoneContact phoneContact1 = new PhoneContact("Husband","000000",true,"1");
            PhoneContact phoneContact2 = new PhoneContact("Rakan Cop","000000",true,"1");

            savedContactList.add(phoneContact);
            savedContactList.add(phoneContact1);
            savedContactList.add(phoneContact2);

            contactsAdapater = new SOSPhoneListAdater(getContext(),savedContactList);
            phone_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            saved_contacts_num = savedContactList.size();
            contactsAdapater.setUpdateCallback(this);
            contactsAdapater.setOnMessageSent(new SOSPhoneListAdater.OnMessageSent() {
                @Override
                public void onMessageDelivered() {
                    message.setVisibility(View.VISIBLE);
                }
            });
            phone_recycler.setAdapter(contactsAdapater);*/

            String url = Constants.Host_Address+"sos/get_customer_sos_contacts_api/"+customerId+"/"+UtilsManager.getApiKey(getActivity())+"/1";
            Log.e(TAG, "getAllSavedContacts: "+url);
            getAllContacts(url);

        }
        catch (Exception e)
        {e.printStackTrace();}
    }


    /*private void getSavedContacts()
    {
        try {
            String tag_json_obj = "GET_SAVED_CONTACTS";
            //  String url = Constants.Host_Address + "get_resident_sos_contacts_api/"+residentId+"/"+condoId+"/"+unit_id+"/"+ accessToken;
            String contacs_list_url = Constants.Host_Address + "sos/get_customer_sos_contacts_api/"+customerId+"/"+ accessToken;

            progressDialog = ProgressDialog.show(getContext(),"","Getting Your Contacts",true,false);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    contacs_list_url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            if (response.has("data"))
                                 parseData(response);
                            progressDialog.dismiss();

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    // hide the progress dialog
                    progressDialog.dismiss();
                  //  UtilsManager.showAlertMessage(getActivity(), "Error", "Please, check your Internet connection.");
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    //delete phone

    private void deleteContactPhone(final PhoneContact contact, final int index)
    {
        try {
            String tag_json_obj = "GET_DELETE_CONTACTS";
            //  String url = Constants.Host_Address + "get_resident_sos_contacts_api/"+residentId+"/"+condoId+"/"+unit_id+"/"+ accessToken;
            String contacs_list_url = Constants.Host_Address + "sos/delete_sos_contact_api/"+customerId+"/"+contact.getId()+"/"+UtilsManager.getApiKey(getActivity());
            Log.e(TAG, "deleteContactPhone: "+contacs_list_url);
            delecteContactInfo(contacs_list_url);
           // new deleteContact(contacs_list_url,contact.getId()).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void dropFromLocal() {
        SOS_Settings_PREFS prefs = new SOS_Settings_PREFS(getContext());
        prefs.savephoneContact((ArrayList<PhoneContact>) savedContactList, Constants.USER_SOS_PHONE);
    }

    private void parseData(JSONObject response) {

        try {

            JSONArray contacts = response.getJSONArray("data");

            if (contacts!=null) {

             savedContactList = new ArrayList<>();

                String name,phone,id,email;
                for (int i = 0; i < contacts.length(); i++) {
                    name = contacts.getJSONObject(i).getString("contact_name");
                    phone = contacts.getJSONObject(i).getString("phone");
                    email = contacts.getJSONObject(i).getString("email");
                    id = contacts.getJSONObject(i).getString("id");

                    if (!name.equalsIgnoreCase("") && !name.equalsIgnoreCase("null"))
                    {
                        PhoneContact my_contact = new PhoneContact(name,phone,false,id);
                        savedContactList.add(my_contact);
                    }

                }

                contactsAdapater = new SOSPhoneListAdater(getContext(),savedContactList);
                phone_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                saved_contacts_num = savedContactList.size();
                contactsAdapater.setUpdateCallback(this);
                phone_recycler.setAdapter(contactsAdapater);

                new SOS_Settings_PREFS(getContext()).savephoneContact((ArrayList<PhoneContact>) savedContactList,Constants.USER_SOS_PHONE);
            }
            else
            {

            }
        }
        catch (Exception e)
        {
            savedContactList.clear();
            contactsAdapater.notifyDataSetChanged();
            new SOS_Settings_PREFS(getContext()).savephoneContact((ArrayList<PhoneContact>) savedContactList,Constants.USER_SOS_PHONE);

            // UtilsManager.showAlertMessage(getContext(),"",e.getMessage());
        }

    }




    //update contact

    private void openEditDialog(final PhoneContact contact) {

        final Dialog dialog = new Dialog(getContext(),R.style.Theme_Dialog);
        dialog.setContentView(R.layout.edit_contact_sos);

        final EditText email_edit = (EditText) dialog.findViewById(R.id.email_box);
        final EditText name_edit = (EditText) dialog.findViewById(R.id.name_box);
        final TextView heading = (TextView) dialog.findViewById(R.id.heading);
        heading.setText("Update Contact");

        Button done = (Button) dialog.findViewById(R.id.ok_btn);
        ImageView cancel_dialog = (ImageView) dialog.findViewById(R.id.cancel_btn);
        final TextView error = (TextView) dialog.findViewById(R.id.error_text);

        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                error.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email_edit.setText(contact.getNumber());
        name_edit.setText(contact.getName());

        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email_edit.getText().toString().equals("") && !name_edit.getText().toString().equals("")) {
                    updateContact(name_edit.getText().toString(),email_edit.getText().toString(), contact.getId());
                    dialog.dismiss();
                }
                else
                {
                    error.setVisibility(View.VISIBLE);
                    // dialog.dismiss();
                }
            }
        });


        dialog.show();
    }

    private void updateContact(String name, String contact,String sos_id) {
        asHclient.setURLEncodingEnabled(false);
        asHclient.setTimeout(60000);
        RequestParams params = new RequestParams();

        params.put("key", accessToken);

        //PUT SELECTED CONTACTS HERE

        params.put("customer_id", customerId);
        params.put("contact_name", name);
        params.put("contact_phone",contact);
        params.put("contact_email","");
        params.put("sos_id", sos_id);

        asHclient.post(Constants.Host_Address + "/sos/update_sos_contact_api", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(getContext(),R.style.DialogTheme);
                progressDialog.setMessage("Updating Contact");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    final String responseString = new String(responseBody, "UTF-8");

                    Log.d("RESPONSE SUCCESS", responseString);
                    JSONObject responseJsonObject = new JSONObject(responseString);
                    String status = responseJsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                        if (responseJsonObject.has("data"))
                            parseData(responseJsonObject);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                try {
                    //  if (progressDialog.isShowing())
                    //      progressDialog.dismiss();

                    final String responseString = new String(responseBody, "UTF-8");
                    Log.e(TAG + " Syncing Failed", responseString);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
                try {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    Log.e(TAG + " On Retry ", retryNo + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private AlertDialog d;
    private void ConfirmDelection(final PhoneContact contact, final int index)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you really want to delete");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    deleteContactPhone(contact,index);
                  d.dismiss();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                d.dismiss();
            }
        });

        d= builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                d.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
                d.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });
        d.show();
    }



    class getSavedContacts extends AsyncTask<String,Void,String>
    {

        private String url;
        public getSavedContacts(String url)
        {
          this.url = url;
        }
        @Override
        protected String doInBackground(String... params) {
            try{

                String result = null;
                  result = downloadUrl(new URL(url));
                return result;
            }
            catch (Exception e)
            {
               return null;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s!=null)
            {

                try
                {
                    JSONObject object = new JSONObject(s);

                    if (object.has("data"))
                        parseData(object);
                    progressDialog.dismiss();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    public void delecteContactInfo(String url){

        asHclient.setTimeout(20*1000);

        asHclient.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pd = new ProgressDialog(getContext(),R.style.DialogTheme);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage("Deleting Contact");
                pd.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (pd!=null)
                        pd.dismiss();
                    String res = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+res);
                    getAllSavedContacts();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    if (pd!=null)
                        pd.dismiss();
                    String res = new String(responseBody);
                    Log.e(TAG, "onFailed: "+res);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    ProgressDialog pd;
    //delete contacts
    class deleteContact extends AsyncTask<String,Void,String>
    {

        private String url,sos_id;

        public deleteContact(String url,String sos_id)
        {
            this.url = url;
            this.sos_id = sos_id;
        }
        @Override
        protected String doInBackground(String... params) {
            try{

                String result = null;
                result = downloadUrl(new URL(url));
                return result;
            }
            catch (Exception e)
            {
                return null;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getContext(),R.style.DialogTheme);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Deleting Contact");
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pd.dismiss();

            if (s!=null)
            {

                try
                {
                    JSONObject object = new JSONObject(s);

                    if (object.has("status")) {
                        pd.dismiss();
                        for (int i=0;i<savedContactList.size();i++)
                        {
                            if (this.sos_id.equalsIgnoreCase(savedContactList.get(i).getId()))
                            {
                                savedContactList.remove(i);
                            }
                        }

                        if (contactsAdapater!=null)
                            contactsAdapater.notifyDataSetChanged();
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                UtilsManager.showAlertMessage(getContext(),"","Could not delete");
            }

        }
    }


    public void getAllContacts(String url){

        asHclient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try
                {
                    String s = new String(responseBody);
                    Log.e(TAG, "onSuccess: "+s);
                    JSONObject object = new JSONObject(s);

                    if (object.has("data")) {
                        parseData(object);
                    }
                    progressDialog.dismiss();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Toast.makeText(getActivity(), "Internet/Server Error", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }


    //Perform Network Operation in Function

    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(25000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readFromInputStream(stream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }


    //Convert Stream to String


    private String readFromInputStream(InputStream stream) {
        StringBuilder total = new StringBuilder();
        try {

            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

        return total.toString();
    }

    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }
}
