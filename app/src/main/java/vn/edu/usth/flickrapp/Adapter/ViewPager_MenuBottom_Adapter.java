package vn.edu.usth.flickrapp.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import vn.edu.usth.flickrapp.Fragment.CamFragment;
import vn.edu.usth.flickrapp.Fragment.NewsFragment;
import vn.edu.usth.flickrapp.Fragment.NotificationFragment;
import vn.edu.usth.flickrapp.Fragment.ProfileFragment;
import vn.edu.usth.flickrapp.Fragment.SearchFragment;
import vn.edu.usth.flickrapp.Model.User;

public class ViewPager_MenuBottom_Adapter extends FragmentPagerAdapter {
    private static User user;
    public ViewPager_MenuBottom_Adapter(FragmentManager fm, User user) {
        super(fm);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 1:
                return SearchFragment.newInstance("SearchFragment", user);

            case 2:
                return CamFragment.newInstance("CamFragment", user);

            case 3:
                return NotificationFragment.newInstance("NotificationFragment", user);

            case 4:
                return ProfileFragment.newInstance("ProfileFragment", user);

            default:
                return NewsFragment.newInstance("NewsFragment", user);
        }

    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Home";
                break;

            case 1:
                title = "Search";
                break;

            case 2:
                title = "Camera";
                break;

            case 3:
                title = "Notification";
                break;

            case 4:
                title = "Profile";
                break;
        }
        return title;
    }
}