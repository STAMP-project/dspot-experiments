package json.chao.com.wanandroid.testStudyExample.Robolectric;


import Constants.MY_SHARED_PREFERENCE;
import Context.MODE_PRIVATE;
import MyReceiver.APP_NAME;
import android.content.Intent;
import android.content.SharedPreferences;
import json.chao.com.wanandroid.BuildConfig;
import json.chao.com.wanandroid.app.WanAndroidApp;
import json.chao.com.wanandroid.test.MyReceiver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;


/**
 *
 *
 * @author quchao
 * @unknown 2018/6/6
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class MyReceiverTest {
    private static final String action = "test_broadcast_receiver";

    private Intent intent;

    @Test
    public void registerReceiver() {
        ShadowApplication application = ShadowApplication.getInstance();
        Assert.assertNotNull(application.hasReceiverForIntent(intent));
    }

    @Test
    public void onReceive() {
        intent.putExtra(APP_NAME, "WanAndroid");
        MyReceiver myReceiver = new MyReceiver();
        myReceiver.onReceive(WanAndroidApp.getInstance(), intent);
        SharedPreferences mPreferences = WanAndroidApp.getInstance().getSharedPreferences(MY_SHARED_PREFERENCE, MODE_PRIVATE);
        String appName = mPreferences.getString(APP_NAME, null);
        Assert.assertEquals("WanAndroid", appName);
    }
}

