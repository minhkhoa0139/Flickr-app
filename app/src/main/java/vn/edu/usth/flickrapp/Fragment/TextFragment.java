package vn.edu.usth.flickrapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import vn.edu.usth.flickrapp.R;

public class TextFragment extends Fragment {

    private String text;

    public TextFragment() {
    }

    public static TextFragment newInstance(String text) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString("text");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_item, container, false);
        return view;
    }
}
