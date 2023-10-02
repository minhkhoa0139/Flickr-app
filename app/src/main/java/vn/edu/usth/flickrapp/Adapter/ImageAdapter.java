package vn.edu.usth.flickrapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;

import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.Notification;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.PhotoDetailActivity;
import vn.edu.usth.flickrapp.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Image> imagelst;

    private User user;

    public ImageAdapter(Context context, List<Image> imagelst, User user) {
        this.context = context;
        this.imagelst = imagelst;
        this.user = user;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_list_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image obj = imagelst.get(position);
        Glide.with(context).load(obj.getUri()).into(holder.imageView);
        Glide.with(context).load(R.drawable.ic_ava).into(holder.avatarImageView);
        holder.usernameTextView.setText(obj.getName());
        holder.txtContentNews.setText(obj.getContent());
        holder.likeCountTextView.setText(obj.getLikeCount());
        holder.commentCountTextView.setText(obj.getCommentCount());
        holder.emailTextView.setText(obj.getEmail());
        holder.emailPhuTextView.setText(obj.getEmailPhu());
        holder.uriTextView.setText(obj.getUri());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
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
        return imagelst.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView avatarImageView;
        TextView usernameTextView;
        TextView txtContentNews;
        TextView likeCountTextView;
        TextView commentCountTextView;
        TextView emailTextView, emailPhuTextView, uriTextView;
        private boolean isLiked = false;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageContentImageView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            txtContentNews = itemView.findViewById(R.id.txtContentNews);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            emailPhuTextView = itemView.findViewById(R.id.emailPhuTextView);
            uriTextView = itemView.findViewById(R.id.uriTextView);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference notificationRef = database.getReference("notification");

            ImageView likeImageView = itemView.findViewById(R.id.likeImageView);
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý sự kiện khi nhấn vào nút "like"
                    if (!isLiked) {
                        likeImageView.setImageResource(R.drawable.ic_liked);
                        int likeCount = Integer.parseInt(likeCountTextView.getText().toString());
                        likeCount++;
                        likeCountTextView.setText(String.valueOf(likeCount));
                        isLiked = true;

                        Notification item = new Notification(R.drawable.ic_ava, R.drawable.ic_liked, "Đã like ảnh của bạn", emailTextView.getText().toString(), emailPhuTextView.getText().toString(), uriTextView.getText().toString());
                        notificationRef.push().setValue(item);
                    } else {
                        likeImageView.setImageResource(R.drawable.ic_like);
                        int likeCount = Integer.parseInt(likeCountTextView.getText().toString());
                        likeCount--;
                        likeCountTextView.setText(String.valueOf(likeCount));
                        isLiked = false;
                    }
                }
            });
        }
    }
}
