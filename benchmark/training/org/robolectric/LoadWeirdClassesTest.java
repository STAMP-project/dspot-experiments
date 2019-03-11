package org.robolectric;


import Build.VERSION;
import RuntimeEnvironment.application;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Build.VERSION_CODES;
import android.view.Display;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDisplay;
import org.robolectric.util.ReflectionHelpers;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = Config.ALL_SDKS)
public class LoadWeirdClassesTest {
    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void shouldLoadDisplay() throws Exception {
        ReflectionHelpers.callInstanceMethod(Display.class, ShadowDisplay.getDefaultDisplay(), "getDisplayAdjustments");
    }

    @Test
    public void reset_shouldWorkEvenIfSdkIntIsOverridden() throws Exception {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", 23);
    }

    @Test
    public void shadowOf_shouldCompile() throws Exception {
        Assume.assumeThat("Windows is an affront to decency.", File.separator, Matchers.equalTo("/"));
        Shadows.shadowOf(Robolectric.setupActivity(Activity.class));
    }

    @Test
    public void packageManager() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "test.package";
        Shadows.shadowOf(application.getPackageManager()).addPackage(packageInfo);
    }
}

