package vn.edu.usth.flickrapp.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import vn.edu.usth.flickrapp.Adapter.HistoryAdapter;
import vn.edu.usth.flickrapp.Adapter.ImageProfileAdapter;
import vn.edu.usth.flickrapp.Model.History;
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;

public class SearchFragment extends Fragment {
    private static User user;
    EditText txtSearch;
    RecyclerView recyclerViewHistory, recyclerView;
    List<String> lstHistory;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_view, container, false);

        txtSearch = v.findViewById(R.id.txtSearch);
        recyclerViewHistory = v.findViewById(R.id.recyclerViewHistory);
        recyclerView = v.findViewById(R.id.recyclerViewSearch);
        ReloadHistory();
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

        txtSearch.addTextChangedListener(new TextWatcher() {
                                             @Override
                                             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                             }

                                             @Override
                                             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                 String searchText = charSequence.toString();
                                                 recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
                                                 List<String> cloneList =  new ArrayList<>();
                                                 cloneList.addAll(lstHistory);
                                                 if(!TextUtils.isEmpty(searchText)) cloneList.add(0, searchText);
                                                 HistoryAdapter adapterHistory = new HistoryAdapter(cloneList, txtSearch, user);
                                                 recyclerViewHistory.setAdapter(adapterHistory);
                                             }

                                             @Override
                                             public void afterTextChanged(Editable s) {

                                             }
                                         });

        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    recyclerViewHistory.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    ReloadHistory();
                } else {
                    recyclerViewHistory.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    String searchText = txtSearch.getText().toString();
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

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference historyRef = database.getReference("history");
                    History history =  new History(user.email, txtSearch.getText().toString());
                    historyRef.push().setValue(history);
                    ReloadHistory();
                }
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

    public void ReloadHistory()
    {
        lstHistory = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference historyRef = database.getReference("history");

        historyRef.orderByChild("email").equalTo(user.email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String content = getValue("content", userSnapshot);
                        lstHistory.add(content);
                    }
                    recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
                    HistoryAdapter adapterHistory = new HistoryAdapter(lstHistory, txtSearch, user);
                    recyclerViewHistory.setAdapter(adapterHistory);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
