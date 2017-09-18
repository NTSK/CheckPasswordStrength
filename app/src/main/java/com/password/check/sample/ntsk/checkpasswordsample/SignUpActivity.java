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

import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SignUpActivity extends AppCompatActivity {

    private static final String[] PASSWORD_STRENGTH_MESSAGES = new String[]{
            "Weak", "Fair", "Good", "Strong", "Very Strong"
    };

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressBar mStrengthProgressBar;
    private TextView mStrengthTextView;
    private TextView mFeedbackTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("SIGN UP");

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mStrengthProgressBar = (ProgressBar) findViewById(R.id.password_strength_bar);
        mStrengthTextView = (TextView) findViewById(R.id.password_strength_text);
        mFeedbackTextView = (TextView) findViewById(R.id.feedback);

        Zxcvbn zxcvbn = new Zxcvbn();
        RxTextView.textChanges(mPasswordView)
                .observeOn(Schedulers.computation())
                .map(CharSequence::toString)
                .map(zxcvbn::measure)
                .map(Strength::getScore)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(score -> {
                    mStrengthProgressBar.setProgress(score);
                    mStrengthTextView.setText(PASSWORD_STRENGTH_MESSAGES[score]);
                });

        RxTextView.textChanges(mPasswordView)
                .observeOn(Schedulers.computation())
                .map(CharSequence::toString)
                .map(zxcvbn::measure)
                .map(Strength::getFeedback)
                .map(feedback -> makeFeedBackText(feedback.getSuggestions(Locale.JAPAN)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feedbackText -> {
                    if (TextUtils.isEmpty(feedbackText)) {
                        mFeedbackTextView.setVisibility(View.GONE);
                        return;
                    }
                    mFeedbackTextView.setVisibility(View.VISIBLE);
                    mFeedbackTextView.setText(feedbackText);
                });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());
    }

    private String makeFeedBackText(List<String> suggestions) {
        return TextUtils.join("\n", suggestions);
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
            Toast.makeText(this, "Successful!", Toast.LENGTH_LONG).show();
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

