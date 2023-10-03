package vn.edu.usth.flickrapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import vn.edu.usth.flickrapp.Model.Follow;
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
        holder.notificationText.setText(notification.getContent());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        DatabaseReference followRef = database.getReference("follow");

        usersRef.orderByChild("email").equalTo(notification.getEmailPhu()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String avatar = getValue("avatar", userSnapshot);
                        if(!TextUtils.isEmpty(avatar)) Glide.with(context).load(avatar).into(holder.avatarImageViewNotification);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if(obj.getUri().equals("0")) Glide.with(context).load(R.drawable.baseline_person_outline_24).into(holder.otherImageViewNotification);
        else Glide.with(context).load(obj.getUri()).into(holder.otherImageViewNotification);

        holder.layout_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.getUri().equals("0")){
                    usersRef.child(obj.getEmailPhu().replace(".",",")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            if (userSnapshot.exists()) {
                                String avatar = getValue("avatar", userSnapshot);
                                String firstName = getValue("firstName", userSnapshot);
                                String lastName = getValue("lastName", userSnapshot);

                                Dialog dialog = new Dialog(context);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.activity_follower);

                                ImageView imageAvatarFollow = dialog.findViewById(R.id.imageAvatarFollow);
                                TextView txtNameFollow = dialog.findViewById(R.id.txtNameFollow);

                                if(!TextUtils.isEmpty(avatar)) Glide.with(context).load(avatar).into(imageAvatarFollow);
                                txtNameFollow.setText(firstName + " " + lastName);

                                Button btnClose = dialog.findViewById(R.id.btnClose);
                                Button btnFollow = dialog.findViewById(R.id.btnFollow);

                                if(obj.getEmailPhu().equals(user.email)) btnFollow.setVisibility(View.GONE);

                                btnClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                btnFollow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String followed = btnFollow.getText().toString();
                                        if(followed.equals("Follow")){
                                            btnFollow.setText("Unfollow");
                                            Follow follow = new Follow(obj.getEmailPhu(), user.email,"Follow");
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference notificationRef = database.getReference("notification");
                                            Notification item = new Notification("Đã follow bạn", obj.getEmailPhu(), user.email, "0");
                                            notificationRef.push().setValue(item);
                                            SaveFollow(follow);
                                        }
                                        else {
                                            btnFollow.setText("Follow");
                                            Follow follow = new Follow(obj.getEmailPhu(), user.email,"Unfollow");
                                            SaveFollow(follow);
                                        }
                                    }
                                });
                                followRef.orderByChild("email").equalTo(obj.getEmailPhu()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            String emailPhu = userSnapshot.child("emailPhu").getValue(String.class);
                                            String txt_followed = userSnapshot.child("followed").getValue(String.class);
                                            if (emailPhu != null && emailPhu.equals(obj.getEmail()) && txt_followed.equals("Follow")) {
                                                btnFollow.setText("UnFollow");
                                                btnFollow.setBackgroundColor(Color.parseColor("#0000FF"));
                                            }
                                        }
                                        dialog.show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else{
                    Intent intent = new Intent(context, PhotoDetailActivity.class);
                    intent.putExtra("imageObject", obj);
                    intent.putExtra("user", user);
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageViewNotification;
        TextView notificationText;
        ImageView otherImageViewNotification;
        ConstraintLayout layout_notification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageViewNotification = itemView.findViewById(R.id.avatarImageViewNotification);
            notificationText = itemView.findViewById(R.id.notificationText);
            otherImageViewNotification = itemView.findViewById(R.id.otherImageViewNotification);
            layout_notification = itemView.findViewById(R.id.layout_notification);
        }
    }

    public String getValue(String path, DataSnapshot userSnapshot) {
        return userSnapshot.child(path).getValue(String.class);
    }

    public void SaveFollow(Follow follow) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference followRef = database.getReference("follow");

        followRef.orderByChild("email").equalTo(follow.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userKey = "";
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String emailPhu = userSnapshot.child("emailPhu").getValue(String.class);
                    if (emailPhu != null && emailPhu.equals(user.email)) {
                        userKey = userSnapshot.getKey();
                        break;
                    }
                }

                if (TextUtils.isEmpty(userKey)) {
                    followRef.push().setValue(follow);
                } else {
                    DatabaseReference fRef = followRef.child(userKey);
                    fRef.child("followed").setValue(follow.getFollowed());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }
}
