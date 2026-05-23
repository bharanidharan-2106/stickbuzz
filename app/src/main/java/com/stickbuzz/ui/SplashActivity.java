package com.stickbuzz.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stickbuzz.R;
import com.stickbuzz.ui.admin.AdminDashboardActivity;
import com.stickbuzz.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.stickbuzz.ui.user.UserDashboardActivity;
import com.stickbuzz.utils.FirebaseUtil;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView title = findViewById(R.id.txtSplashTitle);
        TextView subtitle = findViewById(R.id.txtSubtitle);
        ProgressBar loader = findViewById(R.id.progressBar);
        View underline = findViewById(R.id.viewUnderline);

        // ---------------- ANIMATIONS ----------------

        ObjectAnimator titleFade = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f);
        ObjectAnimator titleSlide = ObjectAnimator.ofFloat(title, "translationY", 80f, 0f);
        titleFade.setDuration(1000);
        titleSlide.setDuration(1000);

        ObjectAnimator pulseX = ObjectAnimator.ofFloat(title, "scaleX", 1f, 1.05f, 1f);
        pulseX.setRepeatCount(ValueAnimator.INFINITE);
        pulseX.setDuration(2000);

        ObjectAnimator pulseY = ObjectAnimator.ofFloat(title, "scaleY", 1f, 1.05f, 1f);
        pulseY.setRepeatCount(ValueAnimator.INFINITE);
        pulseY.setDuration(2000);

        ObjectAnimator subtitleFade = ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f);
        subtitleFade.setDuration(800);

        ObjectAnimator loaderFade = ObjectAnimator.ofFloat(loader, "alpha", 0f, 1f);
        loaderFade.setDuration(800);

        ValueAnimator lineExpand = ValueAnimator.ofInt(0, 250);
        lineExpand.setDuration(800);
        lineExpand.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            underline.getLayoutParams().width = value;
            underline.requestLayout();
        });

        AnimatorSet set = new AnimatorSet();
        set.play(titleFade).with(titleSlide);
        set.play(lineExpand).after(titleFade);
        set.play(subtitleFade).after(lineExpand);
        set.play(loaderFade).after(subtitleFade);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();

        pulseX.start();
        pulseY.start();

        new Handler().postDelayed(() -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseUtil.getUsersRef().child(uid).child("role")
                        .get().addOnSuccessListener(snapshot -> {

                            String role = snapshot.getValue(String.class);

                            if ("admin".equals(role))
                                startActivity(new Intent(this, AdminDashboardActivity.class));
                            else
                                startActivity(new Intent(this, UserDashboardActivity.class));

                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        });

            } else {

                startActivity(new Intent(this, LoginActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 3000);
    }
}
