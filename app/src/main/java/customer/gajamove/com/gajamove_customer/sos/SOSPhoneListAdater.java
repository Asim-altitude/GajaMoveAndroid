package customer.gajamove.com.gajamove_customer.sos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.models.PhoneContact;

/**
 * Created by Asim Shahzad on 12/14/2017.
 */
public class SOSPhoneListAdater extends RecyclerView.Adapter<SOSPhoneListAdater.ViewHolder> {

    private Context context;
    private List<PhoneContact> list;
  //  private ContactSelectionCallback callback;
    private OnMessageSent onMessageSent;

    public void setOnMessageSent(OnMessageSent onMessageSent) {
        this.onMessageSent = onMessageSent;
    }

    private LayoutInflater layoutInflater;


    public SOSPhoneListAdater(Context context, List<PhoneContact> list) {
        this.context = context;
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.sos_phone_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final PhoneContact contact = list.get(position);

         holder.name.setText(contact.getName());
         holder.number.setText(contact.getNumber());

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // holder.delete.performClick();
            }
        });

        holder.number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.delete.performClick();
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUpdateCallBack.onPhoneDeleteRequest(list.get(position),position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,number,send_sms_btn,send_email_btn;
        ImageView delete,edit;


        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.sos_person_name);
            number = (TextView) itemView.findViewById(R.id.sos_person_number);
            delete = (ImageView) itemView.findViewById(R.id.delete_contact_btn);
        }
    }
    private ContactUpdateRequest contactUpdateCallBack;
    public void setUpdateCallback(ContactUpdateRequest contactUpdateCallBack)
    {
        this.contactUpdateCallBack = contactUpdateCallBack;
    }

    public interface OnMessageSent{
        void onMessageDelivered();
    }
}
