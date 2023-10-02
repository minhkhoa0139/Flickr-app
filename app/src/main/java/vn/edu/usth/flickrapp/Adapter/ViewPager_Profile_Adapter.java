package vn.edu.usth.flickrapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.edu.usth.flickrapp.Fragment.CamFragment;
import vn.edu.usth.flickrapp.Fragment.statsFragment;
import vn.edu.usth.flickrapp.Fragment.publicFragment;
import vn.edu.usth.flickrapp.Fragment.groupsFragment;
import vn.edu.usth.flickrapp.Fragment.AlbumsFragment;
import vn.edu.usth.flickrapp.Model.User;

public class ViewPager_Profile_Adapter extends FragmentStateAdapter {

    User user;
    public ViewPager_Profile_Adapter(@NonNull FragmentActivity fragmentActivity, User user) {
        super(fragmentActivity);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new publicFragment(false, user);
            case 1:
                return new publicFragment(true, user);
            case 2:
                return new AlbumsFragment();
            case 3:
                return new groupsFragment();
            case 4:
                return new statsFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
