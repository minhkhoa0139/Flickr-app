package vn.edu.usth.flickrapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.flickrapp.Adapter.ImageProfileAdapter;
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private User user;

    public AlbumsFragment(User user) {
        this.user = user;
    }

    public AlbumsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumsFragment newInstance(String param1, String param2) {
        AlbumsFragment fragment = new AlbumsFragment();
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
        View v = inflater.inflate(R.layout.fragment_albums, container, false);
        reloadImage(v);

        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayoutAlbum);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadImage(v);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    public void reloadImage(View v){
        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewAlbum);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Image> imgLst = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference imagesRef = database.getReference("images_url");
        DatabaseReference reactionRef = database.getReference("reaction");

        imagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                        reactionRef.orderByChild("uri").equalTo(uri).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String email = userSnapshot.child("email").getValue(String.class);
                                    String liked = userSnapshot.child("liked").getValue(String.class);
                                    if (email != null && email.equals(user.email) && liked.equals("liked")) {
                                        imgLst.add(new Image(email, uri, likeCount, commentCount, content, "", "", type));
                                        ImageProfileAdapter adapter = new ImageProfileAdapter(getContext(), imgLst, user);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Xử lý lỗi nếu có
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public String getValue(String path, DataSnapshot userSnapshot)
    {
        return userSnapshot.child(path).getValue(String.class);
    }
}