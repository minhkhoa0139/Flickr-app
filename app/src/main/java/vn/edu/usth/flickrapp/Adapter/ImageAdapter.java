package vn.edu.usth.flickrapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.Notification;
import vn.edu.usth.flickrapp.Model.Reaction;
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
        if(!TextUtils.isEmpty(user.avatar)) Glide.with(context).load(user.avatar).into(holder.avatarImageView);

        holder.usernameTextView.setText(obj.getName());
        holder.txtContentNews.setText(obj.getContent());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reactionRef = database.getReference("reaction");
        DatabaseReference notificationRef = database.getReference("notification");
        DatabaseReference commentRef = database.getReference("comment");

        reactionRef.orderByChild("uri").equalTo(obj.getUri()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    String txt_liked = userSnapshot.child("liked").getValue(String.class);
                    if (email != null && email.equals(user.email)) {
                        if(txt_liked == null || txt_liked.equals("1")) holder.isLiked = true;
                    }
                    if(txt_liked == null || txt_liked.equals("1")) count++;
                }

                holder.likeCountTextView.setText(String.valueOf(count));
                if(holder.isLiked) Glide.with(context).load(R.drawable.ic_liked).into(holder.likeImageView);
                else Glide.with(context).load(R.drawable.ic_like).into(holder.likeImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        commentRef.orderByChild("linkUri").equalTo(obj.getUri()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                holder.commentCountTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("imageObject", obj);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });

        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.isLiked) {
                    holder.likeImageView.setImageResource(R.drawable.ic_liked);
                    int likeCount = Integer.parseInt(holder.likeCountTextView.getText().toString());
                    likeCount++;
                    holder.likeCountTextView.setText(String.valueOf(likeCount));
                    holder.isLiked = true;

                    Notification item = new Notification(R.drawable.ic_ava, R.drawable.ic_liked, "Đã like ảnh của bạn", obj.getEmail(), obj.getEmailPhu(), obj.getUri());
                    notificationRef.push().setValue(item);

                    Reaction reaction = new Reaction(obj.getUri(), "1", user.email);
                    SaveReaction(reaction);
                } else {
                    holder.likeImageView.setImageResource(R.drawable.ic_like);
                    int likeCount = Integer.parseInt(holder.likeCountTextView.getText().toString());
                    likeCount--;
                    holder.likeCountTextView.setText(String.valueOf(likeCount));
                    holder.isLiked = false;

                    Reaction reaction = new Reaction(obj.getUri(), "0", user.email);
                    SaveReaction(reaction);
                }
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
        ImageView likeImageView;
        TextView usernameTextView;
        TextView txtContentNews;
        TextView likeCountTextView;
        TextView commentCountTextView;
        private boolean isLiked = false;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageContentImageView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            txtContentNews = itemView.findViewById(R.id.txtContentNews);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            likeImageView = itemView.findViewById(R.id.likeImageView);
        }
    }

    public void SaveReaction(Reaction reaction) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reactionRef = database.getReference("reaction");
        Query query = reactionRef.orderByChild("email").equalTo(reaction.getEmail());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userKey = "";
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String uri = userSnapshot.child("uri").getValue(String.class);
                    if (uri != null && uri.equals(reaction.getUri())) {
                        userKey = userSnapshot.getKey();
                        break;
                    }
                }

                if (TextUtils.isEmpty(userKey)) {
                    reactionRef.push().setValue(reaction);
                } else {
                    DatabaseReference userRef = reactionRef.child(userKey);
                    userRef.child("liked").setValue(reaction.GetLiked());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }
}
