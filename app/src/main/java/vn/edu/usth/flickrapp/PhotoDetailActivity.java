package vn.edu.usth.flickrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.flickrapp.Adapter.CommentAdapter;
import vn.edu.usth.flickrapp.Model.Comment;
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.Notification;
import vn.edu.usth.flickrapp.Model.Reaction;
import vn.edu.usth.flickrapp.Model.User;

public class PhotoDetailActivity extends AppCompatActivity {
    private Image image;
    private User user;
    ImageView imageView;
    ImageView likeImageViewDetail, commentImageViewDetail;
    EditText commentEditText;
    TextView likeCountDetailTextView, commentCountDetailTextView;
    Button sendButton;
    Boolean isLiked = false;
    List<Comment> commentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        image = (Image) getIntent().getSerializableExtra("imageObject");
        user = (User) getIntent().getSerializableExtra("user");
        String focus = getIntent().getStringExtra("focus");

        imageView = findViewById(R.id.imageViewDetail);
        commentEditText = findViewById(R.id.commentEditText);
        likeCountDetailTextView = findViewById(R.id.likeCountDetailTextView);
        commentCountDetailTextView = findViewById(R.id.commentCountDetailTextView);
        likeImageViewDetail = findViewById(R.id.likeImageViewDetail);
        commentImageViewDetail = findViewById(R.id.commentImageViewDetail);

        Glide.with(this).load(image.getUri()).into(imageView);
        likeCountDetailTextView.setText(image.getLikeCount());
        commentCountDetailTextView.setText(image.getCommentCount());
        reloadComment();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reactionRef = database.getReference("reaction");
        DatabaseReference commentRef = database.getReference("comment");
        DatabaseReference notificationRef = database.getReference("notification");

        likeImageViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLiked) {
                    likeImageViewDetail.setImageResource(R.drawable.ic_liked);
                    int likeCount = Integer.parseInt(likeCountDetailTextView.getText().toString());
                    likeCount++;
                    likeCountDetailTextView.setText(String.valueOf(likeCount));
                    isLiked = true;

                    Notification item = new Notification("Đã like ảnh của bạn", image.getEmail(), user.email, image.getUri());
                    notificationRef.push().setValue(item);

                    Reaction reaction = new Reaction(image.getUri(), "liked", user.email);
                    SaveReaction(reaction);
                } else {
                    likeImageViewDetail.setImageResource(R.drawable.ic_like);
                    int likeCount = Integer.parseInt(likeCountDetailTextView.getText().toString());
                    likeCount--;
                    likeCountDetailTextView.setText(String.valueOf(likeCount));
                    isLiked = false;

                    Reaction reaction = new Reaction(image.getUri(), "unliked", user.email);
                    SaveReaction(reaction);
                }
            }
        });

        commentImageViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEditText.requestFocus();
            }
        });

        reactionRef.orderByChild("uri").equalTo(image.getUri()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    String txt_liked = userSnapshot.child("liked").getValue(String.class);
                    if(txt_liked != null && txt_liked.equals("liked")) {
                        count = count + 1;
                        if (email != null && email.equals(user.email)) isLiked = true;
                    }
                }

                likeCountDetailTextView.setText(String.valueOf(count));
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
                commentCountDetailTextView.setText(String.valueOf(count));
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
                                Notification item = new Notification("Đã comment ảnh của bạn", image.getEmail(), user.email, image.getUri());
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

        if(focus != null && focus.equals("1")) commentEditText.requestFocus();
    }

    public void reloadComment()
    {
        commentList = new ArrayList<>();
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
}