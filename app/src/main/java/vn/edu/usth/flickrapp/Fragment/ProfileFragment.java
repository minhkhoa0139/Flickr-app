package vn.edu.usth.flickrapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.edu.usth.flickrapp.Adapter.ViewPager_Profile_Adapter;
import vn.edu.usth.flickrapp.Model.Notification;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;
import vn.edu.usth.flickrapp.WelcomeActivity;

public class ProfileFragment extends Fragment {
    private static User user;
    TextView textNameProfile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, container, false);
        textNameProfile = v.findViewById(R.id.textNameProfile);
        textNameProfile.setText(user.firstName+" "+user.lastName);

        ViewPager2 viewPager = v.findViewById(R.id.view_pagerProfile);
        ViewPager_Profile_Adapter pagerAdapter = new ViewPager_Profile_Adapter(getActivity(), user);
        viewPager.setAdapter(pagerAdapter);

        ImageView btnSignOut = v.findViewById(R.id.button7);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        TabLayout tabLayout = v.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Camera Roll");
                            break;
                        case 1:
                            tab.setText("Public");
                            break;
                        case 2:
                            tab.setText("Albums");
                            break;
                        case 3:
                            tab.setText("Groups");
                            break;
                        case 4:
                            tab.setText("Stats");
                            break;
                    }
                }).attach();

        return v;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static ProfileFragment newInstance(String text, User user) {
        ProfileFragment f = new ProfileFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        f.setUser(user);
        return f;
    }
}