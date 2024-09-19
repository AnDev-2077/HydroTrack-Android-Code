package com.example.tank.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tank.ui.HistoryFragment;
import com.example.tank.ui.SettingsFragment;
import com.example.tank.ui.homeFragment;

public class MainAdapter extends FragmentStateAdapter  {

    public MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MainAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MainAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new homeFragment();
        }
        if(position==1){
           return new HistoryFragment();
        }
        if(position==2){
            return  new SettingsFragment();
        }

        return new homeFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
