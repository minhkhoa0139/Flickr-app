package vn.edu.usth.flickrapp.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import vn.edu.usth.flickrapp.Adapter.ViewPager_Profile_Adapter;
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.Notification;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;
import vn.edu.usth.flickrapp.WelcomeActivity;

public class ProfileFragment extends Fragment {
    private User user;
    private final int SELECT_PICTURE_AVA = 111;
    TextView textNameProfile;
    ImageView imageAvatar;
    ViewPager2 viewPager;
    ImageView btnSignOut;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, container, false);
        textNameProfile = v.findViewById(R.id.textNameProfile);
        textNameProfile.setText(user.firstName + " " + user.lastName);
        imageAvatar = v.findViewById(R.id.imageAvatar);
        viewPager = v.findViewById(R.id.view_pagerProfile);
        btnSignOut = v.findViewById(R.id.btnSignOut);

        if(!TextUtils.isEmpty(user.avatar)) Glide.with(getContext()).load(user.avatar).into(imageAvatar);

        ViewPager_Profile_Adapter pagerAdapter = new ViewPager_Profile_Adapter(getActivity(), user);
        viewPager.setAdapter(pagerAdapter);

        imageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE_AVA);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference followRef = database.getReference("follow");
        followRef.orderByChild("followed").equalTo("Follow").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long countFollow = 0;
                long countFollowing = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    String emailPhu = userSnapshot.child("emailPhu").getValue(String.class);
                    if(email.equals(user.email)) countFollow++;
                    if(emailPhu.equals(user.email)) countFollowing++;
                }
                TextView txtCoutFollow = v.findViewById(R.id.txtCoutFollow);
                txtCoutFollow.setText("Theo dõi: " + countFollow + " - Đang theo dõi: "+countFollowing);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        TabLayout tabLayout = v.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Camera Roll");
                            break;
                        case 1:
                            tab.setText("Public");
                            break;
                        case 2:
                            tab.setText("Albums");
                            break;
                        case 3:
                            tab.setText("Groups");
                            break;
                        case 4:
                            tab.setText("Stats");
                            break;
                    }
                }).attach();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE_AVA &&  resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource instanceof BitmapDrawable)
                            {
                                uploadAvatar(resource);
                            }
                            return false;
                        }
                    })
                    .into(imageAvatar);
        }
    }

    public void uploadAvatar(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Đang tải ảnh...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bitmap imageBitmap = bitmapDrawable.getBitmap();

        // Tạo một luồng dữ liệu từ Bitmap
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Đặt tên cho ảnh trên Firebase Storage
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy_hh:mm:ss");
        String imageName = dateFormat.format(currentDate);

        // Tham chiếu đến Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("images_url");

        // Tham chiếu đến vị trí lưu trữ trên Firebase
        StorageReference imageRef = storageRef.child("images/" + imageName);

        // Tải ảnh lên Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Tải avatar thành công", Toast.LENGTH_SHORT).show();

                taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                String imageUrl = downloadUri.toString();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference usersRef = database.getReference("users");

                                String userKey = user.email.replace(".", ",");
                                DatabaseReference userRef = usersRef.child(userKey);

                                userRef.child("avatar").setValue(imageUrl);
                                progressDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static ProfileFragment newInstance(String text, User user) {
        ProfileFragment f = new ProfileFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        f.setUser(user);
        return f;
    }
}