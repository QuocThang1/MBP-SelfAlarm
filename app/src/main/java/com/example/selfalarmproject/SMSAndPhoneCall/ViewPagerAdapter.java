package com.example.selfalarmproject.SMSAndPhoneCall;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.selfalarmproject.Fragment.CallFragment;
import com.example.selfalarmproject.Fragment.SMSFragment;
import com.example.selfalarmproject.Fragment.BlacklistFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CallFragment();
            case 1:
                return new SMSFragment();
            case 2:
                return new BlacklistFragment();
            default:
                return new CallFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}