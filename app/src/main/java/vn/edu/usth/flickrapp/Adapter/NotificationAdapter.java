package vn.edu.usth.flickrapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

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
}
