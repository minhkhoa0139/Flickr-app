package vn.edu.usth.flickrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.flickrapp.Adapter.CommentAdapter;
import vn.edu.usth.flickrapp.Model.Comment;
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.Notification;
import vn.edu.usth.flickrapp.Model.User;

public class PhotoDetailActivity extends AppCompatActivity {
    private Image image;
    private User user;
    ImageView imageView;
    ImageView likeImageViewDetail;
    EditText commentEditText;
    TextView likeCountTextView, commentCountTextView;
    Button sendButton;
    Boolean isLiked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        image = (Image) getIntent().getSerializableExtra("imageObject");
        user = (User) getIntent().getSerializableExtra("user");

        imageView = findViewById(R.id.imageViewDetail);
        commentEditText = findViewById(R.id.commentEditText);
        likeCountTextView = findViewById(R.id.likeCountDetailTextView);
        commentCountTextView = findViewById(R.id.commentCountDetailTextView);
        likeImageViewDetail = findViewById(R.id.likeImageViewDetail);

        Glide.with(this).load(image.getUri()).into(imageView);
        likeCountTextView.setText(image.getLikeCount());
        commentCountTextView.setText(image.getCommentCount());
        reloadComment();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reactionRef = database.getReference("reaction");
        DatabaseReference commentRef = database.getReference("comment");

        reactionRef.orderByChild("uri").equalTo(image.getUri()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    String txt_liked = userSnapshot.child("liked").getValue(String.class);
                    if (email != null && email.equals(user.email)) {
                        if(txt_liked == null || txt_liked.equals("1")) isLiked = true;
                    }
                    if(txt_liked == null || txt_liked.equals("1")) count++;
                }

                likeCountTextView.setText(String.valueOf(count));
                if(isLiked) Glide.with(getBaseContext()).load(R.drawable.ic_liked).into(likeImageViewDetail);
                else Glide.with(getBaseContext()).load(R.drawable.ic_like).into(likeImageViewDetail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        commentRef.orderByChild("linkUri").equalTo(image.getUri()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                commentCountTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentEditText.getText().toString();
                if(TextUtils.isEmpty(comment))
                {
                    Toast.makeText(getApplicationContext(), "Nhập comment", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Comment item = new Comment(user.email, comment, image.getUri());
                    commentRef.push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                reloadComment();
                                commentEditText.setText("");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference notificationRef = database.getReference("notification");
                                Notification item = new Notification(R.drawable.ic_ava, R.drawable.ic_liked, "Đã comment ảnh của bạn", user.email, image.getEmail(), image.getUri());
                                notificationRef.push().setValue(item);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Lỗi comment", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void reloadComment()
    {
        List<Comment> commentList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference commentsRef = database.getReference("comment");
        commentsRef.orderByChild("linkUri").equalTo(image.getUri()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment cmt = new Comment();
                    cmt.setLinkUri(getValue("linkUri", snapshot));
                    cmt.setEmail(getValue("email", snapshot));
                    cmt.setTimestamp(getValue("timestamp", snapshot));
                    cmt.setComment(getValue("comment", snapshot));

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("users");
                    usersRef.orderByChild("email").equalTo(cmt.getEmail()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String firstName = getValue("firstName", snapshot);
                                String lastName = getValue("lastName", snapshot);
                                cmt.setName(firstName+" "+lastName);
                                commentList.add(cmt);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                RecyclerView recyclerView = findViewById(R.id.commentsRecyclerView);
                CommentAdapter commentAdapter = new CommentAdapter(getBaseContext(), commentList);
                recyclerView.setAdapter(commentAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
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
}