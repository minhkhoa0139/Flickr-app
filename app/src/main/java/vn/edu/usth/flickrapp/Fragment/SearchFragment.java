package vn.edu.usth.flickrapp.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;

public class SearchFragment extends Fragment {
    private static User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_view, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Image> imgLst = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference imagesRef = database.getReference("images_url");

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
                        if(type.equals("Public")) imgLst.add(new Image(user.email, uri, likeCount, commentCount, content, "", email, type));
                    }
                }
                ImageProfileAdapter adapter = new ImageProfileAdapter(getContext(), imgLst, user);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        EditText txtSearch = v.findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Thực hiện các hành động trước khi văn bản thay đổi
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString();
                imgLst.clear();
                imagesRef.orderByChild("content").startAt(searchText).endAt(searchText + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
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
                                if(type.equals("Public")) imgLst.add(new Image(user.email, uri, likeCount, commentCount, content, "", email, type));
                            }
                        }
                        ImageProfileAdapter adapter = new ImageProfileAdapter(getContext(), imgLst, user);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Thực hiện các hành động sau khi văn bản đã thay đổi
            }
        });

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

    public String getValue(String path, DataSnapshot userSnapshot)
    {
        return userSnapshot.child(path).getValue(String.class);
    }
}
