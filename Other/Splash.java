package com.software.app.update.smart.Other;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.addsdemo.mysdk.ADPrefrences.Ads_Interstitial;
import com.addsdemo.mysdk.ADPrefrences.AppOpenAdManager;
import com.addsdemo.mysdk.ADPrefrences.MyApp;
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class;
import com.addsdemo.mysdk.model.RemoteConfigModel;
import com.addsdemo.mysdk.retrofit.InstallerID;
import com.addsdemo.mysdk.retrofit.MyReferrer;
import com.addsdemo.mysdk.utils.CustomTabLinkOpen;
import com.addsdemo.mysdk.utils.UtilsClass;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.software.app.update.smart.Activity.BaseActivity;
import com.software.app.update.smart.Activity.MainActivity;
import com.software.app.update.smart.Activity.AppShowActivity;
import com.software.app.update.smart.Language.LanguageActivity;
import com.software.app.update.smart.R;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.onesignal.notifications.INotificationClickEvent;
import com.onesignal.notifications.INotificationClickListener;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class Splash extends BaseActivity {

    FirebaseRemoteConfig mFirebaseRemoteConfig;

    RemoteConfigModel remoteConfigModel;

    public static boolean checkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String langCode = new PreferencesHelper11(newBase).getSelectedLanguage();
        if (langCode == null) langCode = "en";
        super.attachBaseContext(setLocale(newBase, langCode));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);


//        Window window = getWindow();
//        window.setStatusBarColor(getColor(R.color.white));
//        window.setNavigationBarColor(getColor(R.color.white));
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        setContentView(R.layout.activity_splash);


        Log.e("CheckLangua", "Language111  :" + new PreferencesHelper(this).getSelectedLanguage());
//        setApplicationLocale(new PreferencesHelper(this).getSelectedLanguage());

        initviews();
//        fetchRemoteData();
    }

    String ONESIGNAL_APP_ID = "47298ab4-338d-4548-8686-3014305b3053";

    private void initviews() {

        if (checkConnection(Splash.this)) {

            OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
            OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
            OneSignal.getNotifications().addClickListener(new INotificationClickListener() {
                @Override
                public void onClick(@NonNull INotificationClickEvent iNotificationClickEvent) {
                    JSONObject data = iNotificationClickEvent.getNotification().getAdditionalData();
                    if (data != null) {
                        String actionType = data.optString("action_type", null);
                        String url = data.optString("url", null);

                        if ("open_url".equals(actionType) && url != null) {

                            // Open the URL in a browser
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(browserIntent);
                            finishAffinity();

                        }
                    } else {

                        // Handle the notification with no additional data
                        Log.d("OneSignal", "No additional data in notification");

                    }
                }
            });


            AppsFlyerLib.getInstance().init("gjn6MRmt9neongBrb6mAE5", new AppsFlyerConversionListener() {
                @Override
                public void onConversionDataSuccess(Map<String, Object> map) {
                }

                @Override
                public void onConversionDataFail(String s) {
                }

                @Override
                public void onAppOpenAttribution(Map<String, String> map) {
                }

                @Override
                public void onAttributionFailure(String s) {
                }
            }, getApplicationContext());
            AppsFlyerLib.getInstance().waitForCustomerUserId(true);
            AppsFlyerLib.getInstance().start(this);
            AppsFlyerLib.getInstance().setDebugLog(true);
            AppsFlyerLib.getInstance().setCustomerIdAndLogSession("gjn6MRmt9neongBrb6mAE5", this);

            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(5)
                    .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            new InstallerID().callInstallerID(Splash.this);
            fetchAndSetRemoteConfig();
        } else {
            checkConnectivity();
        }
    }

    private void checkConnectivity() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (activeNetwork == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);


            dialogBuilder.setMessage("Make sure that WI-FI or mobile data is turned on, then try again")

                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            recreate();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });


            AlertDialog alert = dialogBuilder.create();

            alert.setTitle(getString(R.string.no_internet_connection));
            alert.setIcon(R.drawable.s_logo);

            alert.show();
        }
    }


    private void fetchAndSetRemoteConfig() {


        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {


                        Log.e("InstallerID", "InstallerID " + InstallerID.referrerUrl);

                        if (InstallerID.referrerUrl != null &&
                                (InstallerID.referrerUrl.contains("organic") || InstallerID.referrerUrl.contains("not%20set"))) {

                            remoteConfigModel = new Gson().fromJson(mFirebaseRemoteConfig.getString("app_orgdata"), RemoteConfigModel.class);

                        } else {
                            remoteConfigModel = new Gson().fromJson(mFirebaseRemoteConfig.getString("app_data"), RemoteConfigModel.class);

                            Log.e("InstallerID", "InstallerID   config_data");
                        }


                        Log.d("TAG", "fetchAndSetRemoteConfig: 11 " + remoteConfigModel);
                        String versiocode = "0";


                        try {
                            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            versiocode = pInfo.versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (Objects.equals(versiocode, remoteConfigModel.getVersionName())) {
                            remoteConfigModel.setAdShow(false);
                            remoteConfigModel.setOnAdRedirect(false);
                            remoteConfigModel.setOnboardingAdShow(false);
                        }

                        if (!remoteConfigModel.getFacebookSDK().getAppId().isEmpty() &&
                                !remoteConfigModel.getFacebookSDK().getClientToken().isEmpty()) {
                            SetApplication(remoteConfigModel.getFacebookSDK().getAppId(),
                                    remoteConfigModel.getFacebookSDK().getClientToken());
                        }

                        MyReferrer.GetCountryDetails(remoteConfigModel, new MyReferrer.ApiIpCallback() {
                            @Override
                            public void onSuccess(RemoteConfigModel response) {

                                MyApp.ad_preferences.saveRemoteConfig(response);
                                MyApp.ad_preferences.saveIsAppopenShow(response.isResumeShow());

                                preloadAdsIfEnabled();

                                MyApp.getInstance().getAppOpenAdManager().showAdIfAvailable(Splash.this, new AppOpenAdManager.MyAdCallBack() {
                                    @Override
                                    public void onAdClose(boolean value) {
                                        if (!isFinishing()) {
                                            goNext(1);



                                        }
                                    }
                                }, response.isOpenShow(), response.isAdShow());
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                MyApp.ad_preferences.saveRemoteConfig(remoteConfigModel);
                                MyApp.ad_preferences.saveIsAppopenShow(remoteConfigModel.isResumeShow());

                                Log.d("TAG123456789", "fetchAndSetRemoteConfig:22 " + remoteConfigModel);

                                preloadAdsIfEnabled();

                                MyApp.getInstance().getAppOpenAdManager().showAdIfAvailable(Splash.this, new AppOpenAdManager.MyAdCallBack() {
                                    @Override
                                    public void onAdClose(boolean value) {
                                        if (!isFinishing()) {
                                            goNext(2);
                                            if (!value && remoteConfigModel.getOpenAdType().equals("Redirect") && remoteConfigModel.isOpenShow() && remoteConfigModel.isAdShow()) {
                                                CustomTabLinkOpen.openLink(Splash.this, UtilsClass.getRandomRedirectLink(MyApp.ad_preferences.getRemoteConfig().getCustomLinks().getOpenRedirectLink()), "appOpen_click");
                                            } else if (value && remoteConfigModel.isOnAdRedirect()) {
                                                CustomTabLinkOpen.openLink(Splash.this, UtilsClass.getRandomRedirectLink(MyApp.ad_preferences.getRemoteConfig().getCustomLinks().getOpenRedirectLink()), "appOpen_click");
                                            }

                                        }
                                    }
                                }, remoteConfigModel.isOpenShow(), remoteConfigModel.isAdShow());
                            }
                        });

                    } else {

                        RemoteConfigModel remoteConfigModel = new Gson().fromJson("{\n" +
                                "  \"PackageName\": \"com.software.app.update.smart\",\n" +
                                "  \"isAdShow\": true,\n" +
                                "  \"isOnAdRedirect\": false,\n" +
                                "  \"AdsType\": \"Custom\",\n" +
                                "  \"secondAdType\": \"Custom\",\n" +
                                "  \"isSecondAd\": false,\n" +
                                "  \"AdsLoadType\": \"Preload\",\n" +
                                "  \"FailAdsType\": \"Admob\",\n" +
                                "  \"NativeAdsType\": \"Custom\",\n" +
                                "  \"NativeLoadType\": \"Preload\",\n" +
                                "  \"BannerAdsType\": \"Custom\",\n" +
                                "  \"AdsClick\": \"1\",\n" +
                                "  \"BackClick\": \"0\",\n" +
                                "  \"NativeByPage\": \"1\",\n" +
                                "  \"isCloseShow\": false,\n" +
                                "  \"isOpenShow\": true,\n" +
                                "  \"isInterShow\": true,\n" +
                                "  \"isNativeShow\": true,\n" +
                                "  \"isBannerShow\": true,\n" +
                                "  \"splashAdType\": \"Open\",\n" +
                                "  \"customAdsType\": \"Redirect\",\n" +
                                "  \"openAdType\": \"Layout\",\n" +
                                "  \"customBannerAdType\": \"Image\",\n" +
                                "  \"isOnboardingShow\": true,\n" +
                                "  \"isOnboardingAdShow\": false,\n" +
                                "  \"isOnboardingAlways\": false,\n" +
                                "  \"medium\": \"organic\",\n" +
                                "  \"isOrganicOnboarding\": false,\n" +
                                "  \"OrganiccustomAdsType\": \"Layout\",\n" +
                                "  \"OrganicAdsClick\": \"3\",\n" +
                                "  \"OrgaincopenAdType\": \"Layout\",\n" +
                                "  \"OrganicResumeAdType\": \"Layout\",\n" +
                                "  \"OrganicBackClick\": \"3\",\n" +
                                "  \"privacyPolicy\": \"https://phonestylelauncher.blogspot.com/2024/10/privacy-policy.html\",\n" +
                                "  \"termsOfService\": \"https://phonestylelauncher.blogspot.com/2024/10/privacy-policy.html\",\n" +
                                "  \"versionName\": \"0\",\n" +
                                "  \"feedbackMail\": \"semicoloneclipse02@gmail.com\",\n" +
                                "  \"installApiCount\": \"https://dashboardapi.uniqcrafts.com/user/\",\n" +
                                "  \"isExtraScreenShow\": false,\n" +
                                "  \"isResumeShow\": true,\n" +
                                "  \"ResumeAdType\": \"Layout\",\n" +
                                "  \"isCountryScreen\": false,\n" +
                                "  \"isGetStartedScreen\": false,\n" +
                                "  \"moreApps\": \"\",\n" +
                                "  \"admobIds\": {\n" +
                                "    \"openAdIds\": [\n" +
                                "      \"ca-app-pub-3940256099942544/9257395921\"\n" +
                                "    ],\n" +
                                "    \"interAdIds\": [\n" +
                                "      \"ca-app-pub-3940256099942544/1033173712\"\n" +
                                "    ],\n" +
                                "    \"nativeAdIds\": [\n" +
                                "      \"ca-app-pub-3940256099942544/2247696110\"\n" +
                                "    ],\n" +
                                "    \"bannerAdIds\": [\n" +
                                "      \"ca-app-pub-3940256099942544/9214589741\"\n" +
                                "    ]\n" +
                                "  },\n" +
                                "  \"adxIds\": {\n" +
                                "    \"openAdIds\": [\n" +
                                "      \"ca-app-pub-3940256099942544/9257395921\"\n" +
                                "    ],\n" +
                                "    \"interAdIds\": [\n" +
                                "      \"ca-app-pub-3940256099942544/1033173712\"\n" +
                                "    ],\n" +
                                "    \"nativeAdIds\": [\n" +
                                "      \"ca-app-pub-3940256099942544/2247696110\"\n" +
                                "    ],\n" +
                                "    \"bannerAdIds\": [\n" +
                                "      \"ca-app-pub-3940256099942544/9214589741\"\n" +
                                "    ]\n" +
                                "  },\n" +
                                "  \"facebookIds\": {\n" +
                                "    \"openAdIds\": [\n" +
                                "      \"IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID\"\n" +
                                "    ],\n" +
                                "    \"interAdIds\": [\n" +
                                "      \"IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID\"\n" +
                                "    ],\n" +
                                "    \"nativeAdIds\": [\n" +
                                "      \"IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID\"\n" +
                                "    ],\n" +
                                "    \"bannerAdIds\": [\n" +
                                "      \"IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID\"\n" +
                                "    ]\n" +
                                "  },\n" +
                                "  \"customLinks\": {\n" +
                                "    \"linkColor\": \"#000000\",\n" +
                                "    \"openRedirectLink\": [\n" +
                                "      \"https://www.google.com/\"\n" +
                                "    ],\n" +
                                "    \"interRedirectLink\": [\n" +
                                "      \"https://www.google.com/\"\n" +
                                "    ],\n" +
                                "    \"nativeRedirectLink\": [\n" +
                                "      \"https://www.google.com/\"\n" +
                                "    ],\n" +
                                "    \"bannerRedirectLink\": [\n" +
                                "      \"https://www.google.com/\"\n" +
                                "    ]\n" +
                                "  },\n" +
                                "  \"customAdsConfig\": {\n" +
                                "    \"mainHeadline\": [\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\",\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\",\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\",\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\",\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\"\n" +
                                "    ],\n" +
                                "    \"headline\": [\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\",\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\",\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\",\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\",\n" +
                                "      \"Play & Win Diamond\uD83D\uDC8E\"\n" +
                                "    ],\n" +
                                "    \"description\": [\n" +
                                "      \"Win 1,00,000 Diamonds\uD83D\uDC8E & More\",\n" +
                                "      \"Win 1,00,000 Diamonds\uD83D\uDC8E & More\",\n" +
                                "      \"Win 1,00,000 Diamonds\uD83D\uDC8E & More\",\n" +
                                "      \"Win 1,00,000 Diamonds\uD83D\uDC8E & More\",\n" +
                                "      \"Win 1,00,000 Diamonds\uD83D\uDC8E & More\"\n" +
                                "    ],\n" +
                                "    \"buttonText\": [\n" +
                                "      \"Play Now\",\n" +
                                "      \"Play Now\",\n" +
                                "      \"Play Now\",\n" +
                                "      \"Play Now\",\n" +
                                "      \"Play Now\"\n" +
                                "    ],\n" +
                                "    \"nativeImageLarge\": [\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/n5.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/n4.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/n3.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/n2.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/n1.png\"\n" +
                                "    ],\n" +
                                "    \"nativeImageMedium\": [\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/musicianmagic.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/roadrace2d.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/n3.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/n2.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/n1.png\"\n" +
                                "    ],\n" +
                                "    \"nativeImageSmall\": [\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/musicianmagic.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/roadrace2d.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/pixelfiller.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/gif.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/gif.gif\"\n" +
                                "    ],\n" +
                                "    \"roundImage\": [\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/musicianmagic.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/roadrace2d.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/pixelfiller.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/gif.gif\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/gif.gif\"\n" +
                                "    ],\n" +
                                "    \"bannerImage\": [\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/Banner3.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/Banner1.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/Banner2.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/Banner4.png\",\n" +
                                "      \"https://fff-apk.s3.us-east-1.amazonaws.com/FF+App+Ad+Assets/Banner3.png\"\n" +
                                "    ]\n" +
                                "  },\n" +
                                "  \"rewardAd\": {\n" +
                                "    \"rewardAdType\": \"custom\",\n" +
                                "    \"googleRewardAdId\": \"ca-app-pub-3940256099942544/5224354917\",\n" +
                                "    \"facebookAdId\": \"\",\n" +
                                "    \"unityAdId\": \"\",\n" +
                                "    \"isRewardShow\": false,\n" +
                                "    \"watch_ad_time\": 5,\n" +
                                "    \"watch_count\": 2,\n" +
                                "    \"auto_watch_ad_time\": 5\n" +
                                "  },\n" +
                                "  \"nativeAdConfig\": {\n" +
                                "    \"nativeTypeList\": \"large\",\n" +
                                "    \"nativeTypeOther\": \"large\",\n" +
                                "    \"backgroundColor\": \"#000000\",\n" +
                                "    \"fontColor\": \"#FFFFFF\",\n" +
                                "    \"buttonColor\": \"#007AFF\",\n" +
                                "    \"buttonColor2\": \"#007AFF\",\n" +
                                "    \"buttonFontColor\": \"#FFFFFF\"\n" +
                                "  },\n" +
                                "  \"facebookSDK\": {\n" +
                                "    \"clientToken\": \"\",\n" +
                                "    \"appId\": \"\"\n" +
                                "  },\n" +
                                "  \"isAppLive\": {\n" +
                                "    \"isAppLive\": true,\n" +
                                "    \"appName\": \"VidLink - Video Downloader\",\n" +
                                "    \"appIcon\": \"\",\n" +
                                "    \"appLink\": \"\",\n" +
                                "    \"appDescription\": \"\"\n" +
                                "  },\n" +
                                "  \"CountryList\": [\n" +
                                "    {\n" +
                                "      \"name\": \"India2\",\n" +
                                "      \"native_link\": [\n" +
                                "        \"https://303.play.pokiigame.com/\"\n" +
                                "      ],\n" +
                                "      \"banner_link\": [\n" +
                                "        \"https://303.play.pokiigame.com/\"\n" +
                                "      ],\n" +
                                "      \"inter_link\": [\n" +
                                "        \"https://303.play.pokiigame.com/\"\n" +
                                "      ],\n" +
                                "      \"appopen_link\": [\n" +
                                "        \"https://303.play.pokiigame.com/\"\n" +
                                "      ]\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}", RemoteConfigModel.class);
                        MyApp.ad_preferences.saveRemoteConfig(remoteConfigModel);
                        Log.d("TAG", "fetchAndSetRemoteConfig:33 " + remoteConfigModel);
                        goNext(3);
                    }
                });
    }

    public void goNext(int i) {
        Log.d("checkPos", "goto:2 " + i);
        RemoteConfigModel remoteConfigModel = MyApp.ad_preferences.getRemoteConfig();
        Log.e("checkPos", "Medium : " + remoteConfigModel.getMedium());
        Log.e("checkPos", "isOnboardingAlways : " + remoteConfigModel.isOnboardingAlways());
        Log.e("checkPos", "isOnboardingShow : " + remoteConfigModel.isOnboardingShow());

        if (!SharePref.INSTANCE.isOnboarding(this)||remoteConfigModel.isOnboardingAlways()) {

                startActivity(new Intent(Splash.this, LanguageActivity.class));

        } else {

            if (remoteConfigModel.isOnboardingShow()) {
                startActivity(new Intent(Splash.this, AppShowActivity.class));

            } else {
                startActivity(new Intent(Splash.this, MainActivity.class));
            }

//            startActivity(new Intent(Splash.this, MainActivity.class));     // Skip onboarding
        }
        finish();
    }


    private void SetApplication(String application_id, String token) {

        FacebookSdk.setApplicationId(application_id);
        FacebookSdk.setClientToken(token);

        FacebookSdk.sdkInitialize(Splash.this, new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                FacebookSdk.setAutoLogAppEventsEnabled(true);
                FacebookSdk.setAdvertiserIDCollectionEnabled(true);
                FacebookSdk.setAutoInitEnabled(true);
                FacebookSdk.fullyInitialize();
                FacebookSdk.setAutoLogAppEventsEnabled(true);

                FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
                AppEventsLogger logger = AppEventsLogger.newLogger(Splash.this);

                logger.getApplicationId();

            }
        });
    }

    private void preloadAdsIfEnabled() {
        if (MyApp.ad_preferences.getRemoteConfig() != null && "Preload".equals(MyApp.ad_preferences.getRemoteConfig().getAdsLoadType())) {
            String adsType = MyApp.ad_preferences.getRemoteConfig().getAdsType();

            if ("Admob".equals(adsType)) {
                Ads_Interstitial.Admob_InterstitialAd(this);
            } else if ("Adx".equals(adsType)) {
                Ads_Interstitial.Adx_InterstitialAd(this);
            } else if ("Facebook".equals(adsType)) {
                Ads_Interstitial.Fb_InterstitialAd(this);
            }
        }

        if (MyApp.ad_preferences.getRemoteConfig() != null && "Preload".equals(MyApp.ad_preferences.getRemoteConfig().getNativeLoadType())) {
            String nativeAdsType = MyApp.ad_preferences.getRemoteConfig().getNativeAdsType();

            if ("Admob".equals(nativeAdsType)) {
                NativeAds_Class.AdmobNativeFull(this, null, null);
            } else if ("Adx".equals(nativeAdsType)) {
                NativeAds_Class.AdxNativeFull(this, null, null);
            } else if ("Facebook".equals(nativeAdsType)) {
                NativeAds_Class.FB_NativeAd(this, null, null);
            }
        }
    }

    public Context setLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }


}
