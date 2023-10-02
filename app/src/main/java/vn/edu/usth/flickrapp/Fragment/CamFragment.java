package vn.edu.usth.flickrapp.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import vn.edu.usth.flickrapp.LoginActivity;
import vn.edu.usth.flickrapp.Model.Image;
import vn.edu.usth.flickrapp.Model.User;
import vn.edu.usth.flickrapp.R;
import vn.edu.usth.flickrapp.SignUpActivity;

public class CamFragment extends Fragment {
    Button btnChoose, btnUpload, btnCamera;
    ImageView imageView;
    EditText ContentImage;
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cam, container, false);
        btnChoose = v.findViewById(R.id.btnChoose);
        btnUpload = v.findViewById(R.id.btnUpload);
        btnCamera = v.findViewById(R.id.btnCamera);
        imageView = v.findViewById(R.id.imageView);
        ContentImage = v.findViewById(R.id.ContentImage);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BitmapDrawable && !TextUtils.isEmpty(ContentImage.getText().toString())) {
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
                            Toast.makeText(getActivity(), "Tải ảnh thành công", Toast.LENGTH_SHORT).show();

                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadUri) {
                                            String imageUrl = downloadUri.toString();
                                            Image image = new Image();
                                            image.setEmail(user.email);
                                            image.setUri(imageUrl);
                                            image.setLikeCount("0");
                                            image.setCommentCount("0");
                                            image.setContent(ContentImage.getText().toString());
                                            databaseReference.push().setValue(image);

                                            Glide.with(getContext())
                                                    .load(R.drawable.baseline_crop_original_24)
                                                    .into(imageView);
                                            ContentImage.setText("");

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
                else {
                    if (!(drawable instanceof BitmapDrawable))
                        Toast.makeText(getActivity(), "Chọn ảnh để tải.", Toast.LENGTH_SHORT).show();
                    if (TextUtils.isEmpty(ContentImage.getText().toString()))
                        Toast.makeText(getActivity(), "Nhập nội dung ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE &&  resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .into(imageView);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static CamFragment newInstance(String text, User user) {
        CamFragment f = new CamFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        f.setUser(user);
        return f;
    }
}