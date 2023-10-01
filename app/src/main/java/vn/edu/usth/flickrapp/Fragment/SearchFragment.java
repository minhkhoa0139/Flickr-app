package vn.edu.usth.flickrapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;

public class SearchFragment extends Fragment {
    private static User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_view, container, false);
        return v;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static SearchFragment newInstance(String text, User user) {
        SearchFragment f = new SearchFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        f.setUser(user);
        return f;
    }
}
