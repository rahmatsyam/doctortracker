package application.rahmatsyam.doctortracker.config;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;

import application.rahmatsyam.doctortracker.AppIntroSlider;
import application.rahmatsyam.doctortracker.MenuPilihan;
import application.rahmatsyam.doctortracker.R;

/**
 * Created by Rahmat Syam on 26/3/2017.
 */

public class Introduction extends AppIntro {
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        //adding the three slides for introduction app you can ad as many you needed
        addSlide(AppIntroSlider.newInstance(R.layout.app_intro1));
        addSlide(AppIntroSlider.newInstance(R.layout.app_intro2));
        addSlide(AppIntroSlider.newInstance(R.layout.app_intro3));

        // Show and Hide Skip and Done buttons
        showStatusBar(false);
        showSkipButton(true);


        // Turn vibration on and set intensity
        // You will need to add VIBRATE permission in Manifest file
        setVibrate(true);
        setVibrateIntensity(30);

        //Add animation to the intro slider
        setDepthAnimation();
    }

    @Override
    public void onSkipPressed() {

        Intent skip = new Intent(this, MenuPilihan.class);
        startActivity(skip);
    }

    @Override
    public void onNextPressed() {
        // Do something here when users click or tap on Next button.
    }

    @Override
    public void onDonePressed() {

        Intent done = new Intent(this, MenuPilihan.class);
        startActivity(done);
    }

    @Override
    public void onSlideChanged() {
        // Do something here when slide is changed
    }
}
