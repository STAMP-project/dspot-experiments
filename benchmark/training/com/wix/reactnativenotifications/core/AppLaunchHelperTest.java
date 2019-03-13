package com.wix.reactnativenotifications.core;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AppLaunchHelperTest {
    private static final String LAUNCHED_FROM_NOTIF_BOOLEAN_EXTRA_NAME = "launchedFromNotification";

    static class ActivityMock extends Activity {}

    private final String APP_PACKAGE_NAME = "the.package";

    private final String APP_MAIN_ACTIVITY_NAME = AppLaunchHelperTest.ActivityMock.class.getName();

    @Mock
    private Context mContext;

    @Mock
    private PackageManager mPackageManager;

    @Test
    public void getLaunchIntent__returnsCustomIntentWithNotifFlagExtra() throws Exception {
        final AppLaunchHelper uut = getUUT();
        Intent intent = uut.getLaunchIntent(mContext);
        Assert.assertNotNull(intent);
        Assert.assertEquals(APP_PACKAGE_NAME, intent.getComponent().getPackageName());
        Assert.assertEquals(APP_MAIN_ACTIVITY_NAME, intent.getComponent().getClassName());
        Assert.assertEquals(((Intent.FLAG_ACTIVITY_NEW_TASK) | (Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)), intent.getFlags());
        Assert.assertTrue(intent.getBooleanExtra(AppLaunchHelperTest.LAUNCHED_FROM_NOTIF_BOOLEAN_EXTRA_NAME, false));
    }

    @Test
    public void isLaunchIntentsActivity_activityIsMainLauncherActivity_returnTrue() throws Exception {
        Activity activity = getActivityMock(APP_MAIN_ACTIVITY_NAME);
        final AppLaunchHelper uut = getUUT();
        boolean result = uut.isLaunchIntentsActivity(activity);
        Assert.assertTrue(result);
    }

    @Test
    public void isLaunchIntentsActivity_activityIsNotMainActivity_returnFalse() throws Exception {
        Activity activity = getActivityMock("other.activity");
        final AppLaunchHelper uut = getUUT();
        boolean result = uut.isLaunchIntentsActivity(activity);
        Assert.assertFalse(result);
    }

    @Test
    public void isLaunchIntentOfNotification_hasFlagInBundle_returnTrue() throws Exception {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.getBooleanExtra(ArgumentMatchers.eq(AppLaunchHelperTest.LAUNCHED_FROM_NOTIF_BOOLEAN_EXTRA_NAME), ArgumentMatchers.eq(false))).thenReturn(true);
        final AppLaunchHelper uut = getUUT();
        boolean result = uut.isLaunchIntentOfNotification(intent);
        Assert.assertTrue(result);
    }

    @Test
    public void isLaunchIntentOfNotification_noFlagInBundle_returnFalse() throws Exception {
        Intent intent = Mockito.mock(Intent.class);
        final AppLaunchHelper uut = getUUT();
        boolean result = uut.isLaunchIntentOfNotification(intent);
        Assert.assertFalse(result);
    }
}

