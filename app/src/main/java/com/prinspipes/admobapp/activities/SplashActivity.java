package com.prinspipes.admobapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.prinspipes.admobapp.R;

import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    InterstitialAd mInterstitialAd;
    ConsentForm consentForm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //telephonyManager.getImei();

        Log.e("android_id", "onCreate: "+ telephonyManager);

        getConsentStatus();

        //loadInterstitialAd();

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //mInterstitialAd.show(SplashActivity.this);

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(SplashActivity.this);
                } else {
                    Toast.makeText(SplashActivity.this, "Ad is not ready", Toast.LENGTH_SHORT).show();
                    callMainActivity();
                }
            }
        },10000);*/
    }

    public void getConsentStatus(){
        ConsentInformation.getInstance(SplashActivity.this).addTestDevice("EEF5E362DBD675F1E7209A009DF6F801");
        ConsentInformation.getInstance(SplashActivity.this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);

        ConsentInformation consentInformation = ConsentInformation.getInstance(this);
        String[] publisherIds = {"pub-9017774250533498"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.e("onConsentInfoUpdated", consentStatus.toString());

                // User's consent status successfully updated.
                if (ConsentInformation.getInstance(getBaseContext()).isRequestLocationInEeaOrUnknown()){
                    Log.e("isRequestLocationInEeaOrUnknown", consentStatus.toString());
                    switch (consentStatus) {
                        case UNKNOWN:
                            displayConsentForm();
                            break;
                        case PERSONALIZED:
                            break;
                        case NON_PERSONALIZED:
                            break;
                    }
                } else {
                    Log.e("notRequestLocationInEeaOrUnknown", consentStatus.toString());
                    displayConsentForm();
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                Log.e("onFailedToUpdateConsentInfo", errorDescription);
            }
        });

    }

    private void displayConsentForm() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL("https://www.your.com/privacyurl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        consentForm = new ConsentForm.Builder(SplashActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        Log.e("onConsentFormLoaded", "===");
                        consentForm.show();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                        Log.e("onConsentFormOpened", "===");

                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.
                        Log.e("onConsentFormClosed", "===");

                        switch (consentStatus){
                            case NON_PERSONALIZED:
                                break;
                            case PERSONALIZED:
                                break;
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.e("onConsentFormError", errorDescription);

                        // Consent form error.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();

        consentForm.load();
    }

    private void loadInterstitialAd() {
        MobileAds.initialize(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.e("InterstitialAdError", loadAdError.toString());
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        mInterstitialAd = interstitialAd;

                        //mInterstitialAd.show(SplashActivity.this);

                        interstitialAdCallBacks();
                    }
                });
    }

    private void interstitialAdCallBacks() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                callMainActivity();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                mInterstitialAd = null;
            }
        });
    }

    private void callMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}