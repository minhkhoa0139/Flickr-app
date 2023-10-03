package vn.edu.usth.flickrapp.Fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.flickrapp.Adapter.ImageAdapter;
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;

public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Image> lstImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_news, container, false);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        lstImage = new ArrayList<>();
        imageAdapter = new ImageAdapter(getContext(), lstImage, user);
        recyclerView.setAdapter(imageAdapter);
        getNewsImage();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewsImage();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition == totalItemCount - 1) {
                    swipeRefreshLayout.setRefreshing(true);
                    getNewsImage();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        return v;
    }

    public void getNewsImage()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference imagesRef = database.getReference("images_url");
        imagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uri = getValue("uri", snapshot);
                    String email = getValue("email", snapshot);
                    String likeCount = getValue("likeCount", snapshot);
                    String commentCount = getValue("commentCount", snapshot);
                    String content = getValue("content", snapshot);
                    String type = getValue("type", snapshot);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("users");
                    usersRef.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String firstName = getValue("firstName", snapshot);
                                String lastName = getValue("lastName", snapshot);
                                Image item = new Image(email, uri, likeCount, commentCount, content, firstName + " " + lastName, "", type);
                                if(type.equals("Public")) lstImage.add(item);
                            }
                            imageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
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

    public void setUser(User user) {
        this.user = user;
    }

    public static NewsFragment newInstance(String text, User user) {
        NewsFragment f = new NewsFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        f.setUser(user);
        return f;
    }
}