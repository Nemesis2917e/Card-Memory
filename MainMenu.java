package com.nemesis.cardmemory;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class MainMenu extends Activity {
    private InterstitialAd mInterstitialAd;
    private Context context;
    private ProgressBar progressBar;
    //private ConsentInformation consentInformation;
    //private ConsentForm consentForm;

    private AdManagerAdView adManagerAdView;
    private WebView webView;
    private FrameLayout adViewContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = 1.0f;

        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());


        if (!isInternetAvailable()) {
            Toast.makeText(this, "An Internet Connection is Required to Play this Game", Toast.LENGTH_LONG).show();
            System.exit(0);
            finishAffinity();
            finish();
            return;
        }

    // Set the main menu layout
        setContentView(R.layout.activity_main_menu);

        //NO UI
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //Screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        loadInterstitialAd();
        //music

     /*   consentInformation = UserMessagingPlatform.getConsentInformation(this);

            ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();
            consentInformation.requestConsentInfoUpdate(MainMenu.this, params, () -> {
                        loadAndShowConsentForm();
                    }, formError -> {
                        Log.e("UMP", "Error updating consent info: " + formError.getMessage());
                    }
            );
       */

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> Log.d("MainMenu", "Mobile Ads initialized."));

        // Initialize Buttons
        initializeButtons();

//checkConsentStatus();
        //
      //initializeAdaptiveBanner();


        initializeAdaptiveBannernpa();
    }

 /*   public void saveConsentStatus(Context context,boolean consentGiven) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AdConsentPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("consent_status",consentGiven);
        editor.apply();

    }

/*    public boolean getConsentStatus() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AdConsentPrefs",Context.MODE_PRIVATE);
        return
        sharedPreferences.getBoolean("consent_status",false);
    }

  /*  public void checkConsentStatus() {
        if (getConsentStatus()) {
            launchads();
        } else {
            loadAndShowConsentForm();
        }
    }
*/
 /*   private void launchads() {
        if (consentInformation.canRequestAds()) {

            initializeAdaptiveBannernpa();

            } else {
              Log.d("UMP","Ads not shown no consent");
            }

    }


 private void loadAndShowConsentForm() {

        UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                this,formError -> {
                    //progressBar.setVisibility(View.GONE);
                    if (formError != null) {
                        Log.e("UMP","Error Loading consent form: " + formError.getMessage());
                    } else {
                        Log.d("UMP","Consent Form shown successfully.");
                    }
                }
        );
}
*/
    private void end() {
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
    }



    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();

        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this,MusicService.class);
        startService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(this,MusicService.class);
        stopService(intent);

    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }




/*    private void initializeAdaptiveBanner() {
        adViewContainer = findViewById(R.id.ad_frame);


        // Create AdManagerAdView and set Ad Unit ID
        adManagerAdView = new AdManagerAdView(this);



        adManagerAdView.setAdUnitId("ca-app-pub-6316925525229729/6608599398");
        //testads adManagerAdView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");
        adViewContainer.addView(adManagerAdView);

        // Calculate and load adaptive banner
        adViewContainer.post(() -> {
            int adWidth = (int) (adViewContainer.getWidth() / getResources().getDisplayMetrics().density);
            AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
            adManagerAdView.setAdSizes(adSize);

            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
            adManagerAdView.loadAd(adRequest);
        });
    }
           */

    private void loadInterstitialAd() {

        Bundle extras = new Bundle();
        extras.putString("npa","1");

        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class,extras)
                .build();
        InterstitialAd.load(this, "ca-app-pub-6316925525229729/3526312502", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        setAdCallbacks();
                        Log.d("InterstitialAd", "Ad successfully loaded.");
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        Log.e("InterstitialAd", "Ad failed to load: " + adError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void setAdCallbacks() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d("InterstitialAd", "Ad dismissed.");
                    loadInterstitialAd(); // Reload the ad
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.e("InterstitialAd", "Ad failed to show: " + adError.getMessage());
                    loadInterstitialAd(); // Reload the ad
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d("InterstitialAd", "Ad showed.");
                    mInterstitialAd = null; // Reset the ad reference
                }
            });
        }
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            Log.d("InterstitialAd", "Ad not ready. Reloading ad.");
            loadInterstitialAd();
        }
    }

//
    private void initializeAdaptiveBannernpa() {
        adViewContainer = findViewById(R.id.ad_frame);


        // Create AdManagerAdView and set Ad Unit ID
        adManagerAdView = new AdManagerAdView(this);



        
        //adManagerAdView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");
        adViewContainer.addView(adManagerAdView);

        // Calculate and load adaptive banner
        adViewContainer.post(() -> {
            int adWidth = (int) (adViewContainer.getWidth() / getResources().getDisplayMetrics().density);
            AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
            adManagerAdView.setAdSizes(adSize);

            Bundle extras = new Bundle();
            extras.putString("npa","1");


            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
            adManagerAdView.loadAd(adRequest);
        });
    }


    private void initializeButtons() {
        Button startGameButton = findViewById(R.id.startGameButton);


        Button aboutButton = findViewById(R.id.aboutButton);

        Button privacyPolicyButton = findViewById(R.id.privacyPolicyButton);

        Button exitButton = findViewById(R.id.exitButton);

        Button modes = findViewById(R.id.modes);



        startGameButton.setOnClickListener(v -> {

            Intent intent = new Intent(MainMenu.this, SecondMenu.class);
            startActivity(intent);

        });


        modes.setOnClickListener(v -> {
            Intent mode = new Intent(MainMenu.this, modesmenu.class);
            startActivity(mode);


        });

        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, AboutActivity.class);
            startActivity(intent);
        });
        privacyPolicyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this,Privacy.class);
            startActivity(intent);
        });
        exitButton.setOnClickListener(v -> {
            end();
            finishAffinity();
            System.exit(0);
        });
    }




}
