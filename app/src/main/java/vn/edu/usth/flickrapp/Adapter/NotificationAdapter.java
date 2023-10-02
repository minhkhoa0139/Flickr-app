package vn.edu.usth.flickrapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

import vn.edu.usth.flickrapp.LoginActivity;
import vn.edu.usth.flickrapp.MainActivity;
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.Notification;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.PhotoDetailActivity;
import vn.edu.usth.flickrapp.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private Context context;
    private List<Image> objImg;
    private User user;
    public NotificationAdapter(List<Notification> notifications, List<Image> objImg, User user, Context context) {
        this.notifications = notifications;
        this.context = context;
        this.objImg = objImg;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notifications, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        Image obj = objImg.get(position);
        holder.avatarImageView.setImageResource( notification.getAvatarResId());
        holder.notificationText.setText(notification.getContent());
        holder.otherImageView.setImageResource(notification.getOtherImageResId());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.orderByChild("email").equalTo(obj.getEmailPhu()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String avatar = getValue("avatar", userSnapshot);
                        if(!TextUtils.isEmpty(avatar)) Glide.with(context).load(avatar).into(holder.avatarImageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Glide.with(context).load(obj.getUri()).into(holder.otherImageView);

        holder.layout_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("imageObject", obj);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView notificationText;
        ImageView otherImageView;
        ConstraintLayout layout_notification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            notificationText = itemView.findViewById(R.id.notificationText);
            otherImageView = itemView.findViewById(R.id.otherImageView);
            layout_notification = itemView.findViewById(R.id.layout_notification);
        }
    }

    public String getValue(String path, DataSnapshot userSnapshot)
    {
        return userSnapshot.child(path).getValue(String.class);
    }
}
