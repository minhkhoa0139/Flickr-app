package vn.edu.usth.flickrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.edu.usth.flickrapp.Model.User;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private EditText txtEmail;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPassword);
        login=findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Đang đăng nhập...");
                progressDialog.setCancelable(false);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("users");

                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    TextView errorMessage = findViewById(R.id.errorMessage);
                    TextView errorMessagePass = findViewById(R.id.errorMessagePass);
                    if(TextUtils.isEmpty(email))
                    {
                        errorMessage.setText("Lỗi: Email không để trống");
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setTextColor(Color.RED);
                    }
                    else errorMessage.setVisibility(View.GONE);

                    if(TextUtils.isEmpty(password))
                    {
                        errorMessagePass.setText("Lỗi: Password không để trống");
                        errorMessagePass.setVisibility(View.VISIBLE);
                        errorMessagePass.setTextColor(Color.RED);
                    }
                    else errorMessagePass.setVisibility(View.GONE);
                }
                else {
                    progressDialog.show();
                    usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDialog.dismiss();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String storedPassword = getValue("password", userSnapshot);
                                    if (storedPassword.equals(password)) {
                                        User user = new User(getValue("firstName", userSnapshot),getValue("lastName", userSnapshot), getValue("birthday", userSnapshot), getValue("email", userSnapshot),getValue("password", userSnapshot), getValue("avatar", userSnapshot));
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("user", user);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Không tìm thấy thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Không tìm thấy thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Lỗi đăng nhập ...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public String getValue(String path, DataSnapshot userSnapshot)
    {
        return userSnapshot.child(path).getValue(String.class);
    }
}
