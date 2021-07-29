package customer.gajamove.com.gajamove_customer.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import customer.gajamove.com.gajamove_customer.Create_Order_Screen;
import customer.gajamove.com.gajamove_customer.R;

public class HomeFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabs;
    Button add_order;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home_frame = inflater.inflate(R.layout.home_fragment,container,false);

        tabs = home_frame.findViewById(R.id.tabs);
        viewPager = home_frame.findViewById(R.id.viewpager);
        add_order = home_frame.findViewById(R.id.add_order_btn);

        add_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),Create_Order_Screen.class));
            }
        });

        tabs.setupWithViewPager(viewPager);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getFragmentManager());

        ActiveJobTab upcoming_jobs = new ActiveJobTab();
        pagerAdapter.AddFragments(upcoming_jobs,getActivity().getResources().getString(R.string.active));

        ScheduledJobTab suggested_jobs = new ScheduledJobTab();
        pagerAdapter.AddFragments(suggested_jobs,getActivity().getResources().getString(R.string.schduled));


        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(0);

        View root = tabs.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.background));
           // drawable.setPadding(0,0,0,5);
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

        return home_frame;
    }


    public class ViewPagerAdapter extends androidx.fragment.app.FragmentStatePagerAdapter {
        private final List<Fragment> _FragmentList = new ArrayList<>();
        private final List<String> _FragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(androidx.fragment.app.FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return _FragmentList.get(position);
        }

        @Override
        public int getCount() {
            return _FragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _FragmentTitleList.get(position);
        }

        public void AddFragments(Fragment fragment, String title) {
            _FragmentList.add(fragment);
            _FragmentTitleList.add(title);

        }

    }

}
