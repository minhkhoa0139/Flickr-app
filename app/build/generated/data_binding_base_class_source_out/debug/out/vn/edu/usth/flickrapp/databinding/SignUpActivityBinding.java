// Generated by view binder compiler. Do not edit!
package vn.edu.usth.flickrapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import vn.edu.usth.flickrapp.R;

public final class SignUpActivityBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final Button loginButton;

  @NonNull
  public final Button signupButton;

  @NonNull
  public final EditText txtBirthDay;

  @NonNull
  public final EditText txtEmail;

  @NonNull
  public final EditText txtFirstName;

  @NonNull
  public final EditText txtLastName;

  @NonNull
  public final EditText txtPassword;

  private SignUpActivityBinding(@NonNull RelativeLayout rootView, @NonNull Button loginButton,
      @NonNull Button signupButton, @NonNull EditText txtBirthDay, @NonNull EditText txtEmail,
      @NonNull EditText txtFirstName, @NonNull EditText txtLastName,
      @NonNull EditText txtPassword) {
    this.rootView = rootView;
    this.loginButton = loginButton;
    this.signupButton = signupButton;
    this.txtBirthDay = txtBirthDay;
    this.txtEmail = txtEmail;
    this.txtFirstName = txtFirstName;
    this.txtLastName = txtLastName;
    this.txtPassword = txtPassword;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static SignUpActivityBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static SignUpActivityBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.sign_up_activity, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static SignUpActivityBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.login_button;
      Button loginButton = ViewBindings.findChildViewById(rootView, id);
      if (loginButton == null) {
        break missingId;
      }

      id = R.id.signup_button;
      Button signupButton = ViewBindings.findChildViewById(rootView, id);
      if (signupButton == null) {
        break missingId;
      }

      id = R.id.txtBirthDay;
      EditText txtBirthDay = ViewBindings.findChildViewById(rootView, id);
      if (txtBirthDay == null) {
        break missingId;
      }

      id = R.id.txtEmail;
      EditText txtEmail = ViewBindings.findChildViewById(rootView, id);
      if (txtEmail == null) {
        break missingId;
      }

      id = R.id.txtFirstName;
      EditText txtFirstName = ViewBindings.findChildViewById(rootView, id);
      if (txtFirstName == null) {
        break missingId;
      }

      id = R.id.txtLastName;
      EditText txtLastName = ViewBindings.findChildViewById(rootView, id);
      if (txtLastName == null) {
        break missingId;
      }

      id = R.id.txtPassword;
      EditText txtPassword = ViewBindings.findChildViewById(rootView, id);
      if (txtPassword == null) {
        break missingId;
      }

      return new SignUpActivityBinding((RelativeLayout) rootView, loginButton, signupButton,
          txtBirthDay, txtEmail, txtFirstName, txtLastName, txtPassword);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}