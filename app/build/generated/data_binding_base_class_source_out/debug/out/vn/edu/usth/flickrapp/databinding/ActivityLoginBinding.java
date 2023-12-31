// Generated by view binder compiler. Do not edit!
package vn.edu.usth.flickrapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import vn.edu.usth.flickrapp.R;

public final class ActivityLoginBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView errorMessage;

  @NonNull
  public final TextView errorMessagePass;

  @NonNull
  public final Button loginButton;

  @NonNull
  public final Button signupButton;

  @NonNull
  public final TextInputEditText txtLoginEmail;

  @NonNull
  public final TextInputEditText txtLoginPassword;

  private ActivityLoginBinding(@NonNull RelativeLayout rootView, @NonNull TextView errorMessage,
      @NonNull TextView errorMessagePass, @NonNull Button loginButton, @NonNull Button signupButton,
      @NonNull TextInputEditText txtLoginEmail, @NonNull TextInputEditText txtLoginPassword) {
    this.rootView = rootView;
    this.errorMessage = errorMessage;
    this.errorMessagePass = errorMessagePass;
    this.loginButton = loginButton;
    this.signupButton = signupButton;
    this.txtLoginEmail = txtLoginEmail;
    this.txtLoginPassword = txtLoginPassword;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.errorMessage;
      TextView errorMessage = ViewBindings.findChildViewById(rootView, id);
      if (errorMessage == null) {
        break missingId;
      }

      id = R.id.errorMessagePass;
      TextView errorMessagePass = ViewBindings.findChildViewById(rootView, id);
      if (errorMessagePass == null) {
        break missingId;
      }

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

      id = R.id.txtLoginEmail;
      TextInputEditText txtLoginEmail = ViewBindings.findChildViewById(rootView, id);
      if (txtLoginEmail == null) {
        break missingId;
      }

      id = R.id.txtLoginPassword;
      TextInputEditText txtLoginPassword = ViewBindings.findChildViewById(rootView, id);
      if (txtLoginPassword == null) {
        break missingId;
      }

      return new ActivityLoginBinding((RelativeLayout) rootView, errorMessage, errorMessagePass,
          loginButton, signupButton, txtLoginEmail, txtLoginPassword);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
