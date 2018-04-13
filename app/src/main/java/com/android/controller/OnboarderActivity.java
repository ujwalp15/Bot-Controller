package com.android.controller;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class OnboarderActivity extends AppCompatActivity {

    Window window;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarder);

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.my_statusbar));

        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView1), getDataForOnboarding(), getApplicationContext());

        engine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int oldElementIndex, int newElementIndex) {
                //Toast.makeText(getApplicationContext(), "Swiped from " + oldElementIndex + " to " + newElementIndex, Toast.LENGTH_SHORT).show();
                if(newElementIndex == 0) {
                    window.setStatusBarColor(ContextCompat.getColor(OnboarderActivity.this,R.color.my_statusbar));
                } else if(newElementIndex == 1) {
                    window.setStatusBarColor(ContextCompat.getColor(OnboarderActivity.this,R.color.my_statusbar1));
                } else if (newElementIndex == 2) {
                    window.setStatusBarColor(ContextCompat.getColor(OnboarderActivity.this,R.color.my_statusbar2));
                }
            }
        });

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                // Probably here will be your exit action
                launchHomeScreen();
            }
        });
    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage("PVG", "Portable Vacuum Gripper, a pick and place robot.",
                Color.parseColor("#678FB4"), R.drawable.pvg, R.drawable.key);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Pick", "Picks flat-surfaced objects.",
                Color.parseColor("#65B0B4"), R.drawable.pick, R.drawable.shopping_cart);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Wireless Control", "Robot mobility is controlled in a wireless manner via the app",
                Color.parseColor("#9B90BC"), R.drawable.connect, R.drawable.wallet);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        return elements;
    }

    public void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(OnboarderActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
