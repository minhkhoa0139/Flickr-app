package vn.edu.usth.flickrapp.Adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<String> dataList;
    private EditText txtSearch;
    private LinearLayout layout_history;
    private User user;
    public HistoryAdapter(List<String> dataList, EditText txtSearch, User user) {
        this.dataList = dataList;
        this.txtSearch = txtSearch;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = dataList.get(position);
        holder.textView.setText(item);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText(holder.textView.getText().toString());
                txtSearch.clearFocus();
            }
        });

        holder.textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableRightX = holder.textView.getRight() - holder.textView.getCompoundDrawables()[2].getBounds().width();
                    if (event.getRawX() >= drawableRightX) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference historyRef = database.getReference("history");
                        historyRef.orderByChild("email").equalTo(user.email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        String content = getValue("content", userSnapshot);
                                        if(content.equals(holder.textView.getText().toString()))
                                        {
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("history").child(userSnapshot.getKey());
                                            databaseReference.removeValue();
                                            holder.layout_history.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public LinearLayout layout_history;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txt_history);
            layout_history = itemView.findViewById(R.id.layout_history);
        }
    }

    public String getValue(String path, DataSnapshot userSnapshot)
    {
        return userSnapshot.child(path).getValue(String.class);
    }
}
