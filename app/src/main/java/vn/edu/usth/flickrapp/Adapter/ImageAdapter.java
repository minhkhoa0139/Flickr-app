package vn.edu.usth.flickrapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.PrivateKey;
import java.util.List;

import vn.edu.usth.flickrapp.FollowerActivity;
import vn.edu.usth.flickrapp.Model.Follow;
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
        holder.txtContentNews.setText(obj.getContent());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reactionRef = database.getReference("reaction");
        DatabaseReference notificationRef = database.getReference("notification");
        DatabaseReference commentRef = database.getReference("comment");
        DatabaseReference usersRef = database.getReference("users");
        DatabaseReference followRef = database.getReference("follow");

        usersRef.child(obj.getEmail().replace(".",",")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    String avatar = getValue("avatar", userSnapshot);
                    String firstName = getValue("firstName", userSnapshot);
                    String lastName = getValue("lastName", userSnapshot);
                    if(!TextUtils.isEmpty(avatar)) Glide.with(context).load(avatar).into(holder.avatarImageViewListItem);
                    holder.usernameTextViewListItem.setText(firstName + " " + lastName);

                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.activity_follower);

                    ImageView imageAvatarFollow = dialog.findViewById(R.id.imageAvatarFollow);
                    TextView txtNameFollow = dialog.findViewById(R.id.txtNameFollow);

                    if(!TextUtils.isEmpty(avatar)) Glide.with(context).load(avatar).into(imageAvatarFollow);
                    txtNameFollow.setText(firstName + " " + lastName);

                    Button btnClose = dialog.findViewById(R.id.btnClose);
                    Button btnFollow = dialog.findViewById(R.id.btnFollow);

                    if(obj.getEmail().equals(user.email)) btnFollow.setVisibility(View.GONE);

                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    followRef.orderByChild("email").equalTo(obj.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String emailPhu = userSnapshot.child("emailPhu").getValue(String.class);
                                String txt_followed = userSnapshot.child("followed").getValue(String.class);

                                if (emailPhu != null && emailPhu.equals(user.email) && txt_followed.equals("Follow")) {
                                    btnFollow.setText("UnFollow");
                                    btnFollow.setBackgroundColor(Color.parseColor("#0000FF"));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String followed = btnFollow.getText().toString();
                            if(followed.equals("Follow")){
                                btnFollow.setText("Unfollow");
                                Follow follow = new Follow(obj.getEmail(), user.email,"Follow");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference notificationRef = database.getReference("notification");
                                Notification item = new Notification("Đã follow bạn", obj.getEmail(), user.email, "0");
                                notificationRef.push().setValue(item);
                                SaveFollow(follow);
                            }
                            else {
                                btnFollow.setText("Follow");
                                Follow follow = new Follow(obj.getEmail(), user.email,"Unfollow");
                                SaveFollow(follow);
                            }
                        }
                    });

                    holder.avatarImageViewListItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        reactionRef.orderByChild("uri").equalTo(obj.getUri()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    String txt_liked = userSnapshot.child("liked").getValue(String.class);
                    if(txt_liked != null && txt_liked.equals("liked")) {
                        count = count + 1;
                        if (email != null && email.equals(user.email)) holder.isLiked = true;
                    }
                }

                holder.likeCountTextView.setText(String.valueOf(count));
                if(holder.isLiked) Glide.with(context).load(R.drawable.ic_liked).into(holder.likeImageView);
                else Glide.with(context).load(R.drawable.ic_like).into(holder.likeImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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

        holder.commentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("imageObject", obj);
                intent.putExtra("user", user);
                intent.putExtra("focus", "1");
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

                    Notification item = new Notification("Đã like ảnh của bạn", obj.getEmail(), user.email, obj.getUri());
                    notificationRef.push().setValue(item);

                    Reaction reaction = new Reaction(obj.getUri(), "liked", user.email);
                    SaveReaction(reaction);
                } else {
                    holder.likeImageView.setImageResource(R.drawable.ic_like);
                    int likeCount = Integer.parseInt(holder.likeCountTextView.getText().toString());
                    likeCount--;
                    holder.likeCountTextView.setText(String.valueOf(likeCount));
                    holder.isLiked = false;

                    Reaction reaction = new Reaction(obj.getUri(), "unliked", user.email);
                    SaveReaction(reaction);
                }
            }
        });

        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ tiêu đề (nếu cần)");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Nội dung bạn muốn chia sẻ");

                if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(Intent.createChooser(shareIntent, "Chọn ứng dụng chia sẻ"));
                } else {
                    Toast.makeText(context, "Không tìm thấy ứng dụng chia sẻ", Toast.LENGTH_SHORT).show();
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
        ImageView avatarImageViewListItem;
        ImageView commentImageView;
        ImageView likeImageView;
        ImageView btnShare;
        TextView usernameTextViewListItem;
        TextView txtContentNews;
        TextView likeCountTextView;
        TextView commentCountTextView;
        boolean isLiked = false;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageContentImageView);
            avatarImageViewListItem = itemView.findViewById(R.id.avatarImageViewListItem);
            usernameTextViewListItem = itemView.findViewById(R.id.usernameTextViewListItem);
            txtContentNews = itemView.findViewById(R.id.txtContentNews);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            likeImageView = itemView.findViewById(R.id.likeImageView);
            commentImageView = itemView.findViewById(R.id.commentImageView);
            btnShare = itemView.findViewById(R.id.btnShare);
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

                DatabaseReference userRef;
                if (TextUtils.isEmpty(userKey)) userRef = reactionRef.push();
                else userRef = reactionRef.child(userKey);
                userRef.child("email").setValue(reaction.getEmail());
                userRef.child("liked").setValue(reaction.GetLiked());
                userRef.child("uri").setValue(reaction.getUri());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
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

    public String getValue(String path, DataSnapshot userSnapshot) {
        return userSnapshot.child(path).getValue(String.class);
    }
}
