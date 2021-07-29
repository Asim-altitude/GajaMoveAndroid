package customer.gajamove.com.gajamove_customer.calender;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import customer.gajamove.com.gajamove_customer.R;

/**
 * Created by PC-GetRanked on 11/26/2018.
 */

public class MothsAdapter extends RecyclerView.Adapter<MothsAdapter.MonthViewHolder> {

    List<String> moths;
    Context context;
    MonthSelected monthSelected;
    int centerPosition = 1;

    public void setCenterPosition(int centerPosition)
    {
        this.centerPosition = centerPosition;
        notifyDataSetChanged();
    }

    public void setMonthSelected(MonthSelected monthSelected) {
        this.monthSelected = monthSelected;
    }

    public MothsAdapter(List<String> moths, Context context) {
        this.moths = moths;
        this.context = context;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, final int position) {
        holder.monthName.setText(moths.get(position));
        holder.monthName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monthSelected!=null)
                    monthSelected.onMonthSelected(moths.get(position));
            }
        });

        if (position==centerPosition)
            holder.monthName.setTextColor(ContextCompat.getColor(context,R.color.black_color));
        else
            holder.monthName.setTextColor(ContextCompat.getColor(context,R.color.dark_gray_color));
    }

    @Override
    public int getItemCount() {
        return moths.size();
    }

    class MonthViewHolder extends RecyclerView.ViewHolder
    {
        TextView monthName;
        public MonthViewHolder(View itemView) {
            super(itemView);

        //    monthName = itemView.findViewById(R.id.monthName);
        }
    }
}
