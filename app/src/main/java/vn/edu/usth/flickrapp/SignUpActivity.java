package vn.edu.usth.flickrapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.flickrapp.Adapter.HistoryAdapter;
import vn.edu.usth.flickrapp.Model.User;


public class SignUpActivity extends AppCompatActivity {
    private Button btnSignUp;
    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtBirthDay;
    private EditText txtEmail;
    private EditText txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtBirthDay = findViewById(R.id.txtBirthDay);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnSignUp = findViewById(R.id.signup_button);

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pass = charSequence.toString();
                TextView valid1 = findViewById(R.id.validPass1);
                TextView valid2 = findViewById(R.id.validPass2);

                if(pass.length() < 12) valid1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_clear_24, 0, 0, 0);
                else valid1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_circle_24, 0, 0, 0);

                if(pass.contains(" ")) valid2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_clear_24, 0, 0, 0);
                else valid2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_circle_24, 0, 0, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setMessage("Kiểm tra thông tin đăng ký...");
                progressDialog.setCancelable(false);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("users");

                String firstName = txtFirstName.getText().toString();
                String lastName = txtLastName.getText().toString();
                String birthday = txtBirthDay.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                User user = new User(firstName, lastName, birthday, email, password, "");

                TextView errorMessageFirstName = findViewById(R.id.errorMessageFirstName);
                TextView errorMessageLastName = findViewById(R.id.errorMessageLastName);
                TextView errorMessageBirthday = findViewById(R.id.errorMessageBirthday);
                TextView errorMessageEmail = findViewById(R.id.errorMessageEmail);
                TextView errorMessagePassword = findViewById(R.id.errorMessagePass);
                TextView errorMessagePassword1 = findViewById(R.id.errorMessagePass1);
                TextView errorMessagePassword2 = findViewById(R.id.errorMessagePass2);
                Boolean isValid = true;

                if(TextUtils.isEmpty(firstName))
                {
                    errorMessageFirstName.setVisibility(View.VISIBLE);
                    errorMessageFirstName.setTextColor(Color.RED);
                    isValid = false;
                }
                else errorMessageFirstName.setVisibility(View.GONE);

                if(TextUtils.isEmpty(lastName))
                {
                    errorMessageLastName.setVisibility(View.VISIBLE);
                    errorMessageLastName.setTextColor(Color.RED);
                    isValid = false;
                }
                else errorMessageLastName.setVisibility(View.GONE);

                if(TextUtils.isEmpty(birthday))
                {
                    errorMessageBirthday.setVisibility(View.VISIBLE);
                    errorMessageBirthday.setTextColor(Color.RED);
                    isValid = false;
                }
                else errorMessageBirthday.setVisibility(View.GONE);

                if(TextUtils.isEmpty(email))
                {
                    errorMessageEmail.setVisibility(View.VISIBLE);
                    errorMessageEmail.setTextColor(Color.RED);
                    isValid = false;
                }
                else errorMessageEmail.setVisibility(View.GONE);

                if(TextUtils.isEmpty(password))
                {
                    errorMessagePassword.setVisibility(View.VISIBLE);
                    errorMessagePassword.setTextColor(Color.RED);
                    isValid = false;
                }
                else errorMessagePassword.setVisibility(View.GONE);

//                if(password.length() < 12)
//                {
//                    errorMessagePassword1.setVisibility(View.VISIBLE);
//                    errorMessagePassword1.setTextColor(Color.RED);
//                    isValid = false;
//                }
//                else errorMessagePassword1.setVisibility(View.GONE);

                if(password.contains(" "))
                {
                    errorMessagePassword2.setVisibility(View.VISIBLE);
                    errorMessagePassword2.setTextColor(Color.RED);
                    isValid = false;
                }
                else errorMessagePassword2.setVisibility(View.GONE);

                if(isValid){
                    usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDialog.show();
                            if (dataSnapshot.exists()) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                usersRef.child(email.replace(".", ",")).setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                                builder.setTitle("Xác nhận");
                                                builder.setMessage("Bạn có muốn chuyển sang trang đăng nhập không?");

                                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        startActivity(new Intent(SignUpActivity.this , LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    }
                                                });

                                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                });

                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Lỗi kiểm tra email", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
