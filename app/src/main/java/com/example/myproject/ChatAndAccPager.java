package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

public class ChatAndAccPager extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int activity_code;
    private Intent activity_data;
    private FragmentStatePagerAdapter pagerAdapter;

    public String TYPE_OF_LIST;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.view_pager_fragment,container,false);
        activity_code=MyAccountActivity.CODE_NO_FILTER;
        TYPE_OF_LIST="NO_F";
        tabLayout = v.findViewById(R.id.tab_layout);
        viewPager = v.findViewById(R.id.view_pager);
        pagerAdapter=setAdapter();
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

    private FragmentStatePagerAdapter setAdapter(){
        FragmentStatePagerAdapter adapter=new FragmentStatePagerAdapter(getFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return new AccountFragment();
                } else {
                    if (activity_code==MyAccountActivity.CODE_NO_FILTER){
                        TYPE_OF_LIST="N0_F";
                        return new UsersChatListFragment();
                    }
                    else{
                        TYPE_OF_LIST="F";
                        return FilteredChatListFragment.newInstance(activity_data);
                    }
                }
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                if (object instanceof AccountFragment){
                    return POSITION_UNCHANGED;
                }
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return getResources().getString(R.string.myAccount);
                } else {
                    return getResources().getString(R.string.chat_list);
                }
            }
        };
        return adapter;
    }

    public void checkUsersFragment(int code, Intent data){
        activity_code=code;
        activity_data=data;
        viewPager.getAdapter().notifyDataSetChanged();
        //viewPager.setCurrentItem(1);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}
