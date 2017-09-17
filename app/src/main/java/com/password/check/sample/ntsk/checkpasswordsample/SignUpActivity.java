package com.password.check.sample.ntsk.checkpasswordsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SignUpActivity extends AppCompatActivity {

    private static final String[] PASSWORD_STRENGTH_SCORES = new String[] {
            "Weak", "Fair", "Good", "Strong", "Very Strong"
    };

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressBar mPasswordStrengthBar;
    private TextView mPasswordStrengthText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("SIGN UP");

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordStrengthBar = (ProgressBar) findViewById(R.id.password_strength);
        mPasswordStrengthText = (TextView) findViewById(R.id.password_strength_text);

        Zxcvbn zxcvbn = new Zxcvbn();
        RxTextView.textChanges(mPasswordView)
                .observeOn(Schedulers.computation())
                .map(CharSequence::toString)
                .map(zxcvbn::measure)
                .map(Strength::getScore)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(score -> {
                    mPasswordStrengthBar.setProgress(score);
                    mPasswordStrengthText.setText(makeStrengthScoreText(score));
                });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());
    }

    private String makeStrengthScoreText(int strength) {
        return PASSWORD_STRENGTH_SCORES[strength];
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Toast.makeText(this, "Successful!" , Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}

