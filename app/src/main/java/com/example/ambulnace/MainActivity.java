package com.example.ambulnace;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ImageView map, ambulance;
    TextView text1, text2;

    Animation Top_anim, Bottom_anim, Left_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize views
        map = findViewById(R.id.map);
        ambulance = findViewById(R.id.ambulance);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);

        // Load animations
        Top_anim = AnimationUtils.loadAnimation(this, R.anim.topanim);
        Bottom_anim = AnimationUtils.loadAnimation(this, R.anim.bottomanim);
        Left_anim = AnimationUtils.loadAnimation(this, R.anim.leftanim);

        // Set animations to views
        map.setAnimation(Top_anim);
        ambulance.setAnimation(Left_anim);
        text1.setAnimation(Bottom_anim);
        text2.setAnimation(Bottom_anim);

        // Set animation listener on the last animation to move to the next activity
        Bottom_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // No action required
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Move to the next activity when the animation ends
                Intent intent = new Intent(MainActivity.this, home.class);
                startActivity(intent);
                finish();  // Optional: finish the current activity so user cannot navigate back
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No action required
            }
        });
    }
}
