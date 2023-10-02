package vn.edu.usth.flickrapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.flickrapp.Adapter.ImageProfileAdapter;
import vn.edu.usth.flickrapp.Adapter.NotificationAdapter;
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.Notification;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;

public class publicFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean isPublic = false;
    private User user;

    public publicFragment() {
    }

    public publicFragment(boolean isPublic, User user) {
        this.isPublic = isPublic;
        this.user = user;
    }


    // TODO: Rename and change types and number of parameters
    public static publicFragment newInstance(String param1, String param2) {
        publicFragment fragment = new publicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_public, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewProfile);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Image> imgLst = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference imagesRef = database.getReference("images_url");

        imagesRef.orderByChild("email").equalTo(user.email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String uri = getValue("uri", snapshot);
                        String email = getValue("email", snapshot);
                        String likeCount = getValue("likeCount", snapshot);
                        String commentCount = getValue("commentCount", snapshot);
                        String content = getValue("content", snapshot);
                        String type = getValue("type", snapshot);
                        if((type.equals("Public") && isPublic) || (type.equals("Private") && !isPublic)) imgLst.add(new Image(user.email, uri, likeCount, commentCount, content, "", email, type));
                    }
                }
                ImageProfileAdapter adapter = new ImageProfileAdapter(getContext(), imgLst, user);
                recyclerView.setAdapter(adapter);
                if (imgLst.size() > 0) {
                    ImageView imageView = v.findViewById(R.id.imageView);
                    TextView textView1 = v.findViewById(R.id.txtProfile1);
                    TextView textView2 = v.findViewById(R.id.txtProfile2);
                    imageView.setVisibility(View.GONE);
                    textView1.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);
                    recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return v;
    }

    public String getValue(String path, DataSnapshot userSnapshot)
    {
        return userSnapshot.child(path).getValue(String.class);
    }
}