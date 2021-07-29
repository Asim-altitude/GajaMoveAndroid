package customer.gajamove.com.gajamove_customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import customer.gajamove.com.gajamove_customer.R;
import customer.gajamove.com.gajamove_customer.bank.NotifyClick;
import customer.gajamove.com.gajamove_customer.models.BankObj;

public class MemberBankAdapter extends BaseAdapter {

    ArrayList<BankObj> bankObjArrayList;
    Context context;
    NotifyClick notifyClick;

    public MemberBankAdapter(ArrayList<BankObj> bankObjArrayList, Context context) {
        this.bankObjArrayList = bankObjArrayList;
        this.context = context;
    }

    public void setNotifyClick(NotifyClick notifyClick) {
        this.notifyClick = notifyClick;
    }

    @Override
    public int getCount() {
        return bankObjArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.bank_list_item,null);

        TextView bank_name = convertView.findViewById(R.id.bank_name);
        TextView account_name = convertView.findViewById(R.id.account_name);
        TextView account_number = convertView.findViewById(R.id.account_number);
        ImageView delete_btn = convertView.findViewById(R.id.delete_btn);

        bank_name.setText(bankObjArrayList.get(position).getName());
        account_name.setText(bankObjArrayList.get(position).getAcc_title());
        account_number.setText(bankObjArrayList.get(position).getAccount_number());

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifyClick!=null)
                    notifyClick.onNotify(position);
            }
        });


        return convertView;
    }


}
