package com.imagepicker.testing;


import Activity.RESULT_CANCELED;
import ImagePickerModule.REQUEST_LAUNCH_IMAGE_CAPTURE;
import R.style;
import android.app.Activity;
import android.net.Uri;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import java.io.File;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;


/**
 * Created by rusfearuth on 10.04.17.
 */
@RunWith(RobolectricTestRunner.class)
@SuppressStaticInitializationFor("com.facebook.react.common.build.ReactBuildConfig")
@PrepareForTest({ Arguments.class })
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@Config(manifest = Config.NONE)
public class ImagePickerModuleTest {
    private static final int DEFAULT_THEME = style.DefaultExplainingPermissionsTheme;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ActivityController<Activity> activityController;

    private Activity activity;

    private ReactApplicationContext reactContext;

    private TestableImagePickerModule module;

    @Test
    public void testCancelTakingPhoto() {
        final SampleCallback callback = new SampleCallback();
        module.setCallback(callback);
        module.setCameraCaptureUri(Uri.fromFile(new File("")));
        module.onActivityResult(activity, REQUEST_LAUNCH_IMAGE_CAPTURE, RESULT_CANCELED, null);
        Assert.assertFalse("Camera's been launched", callback.hasError());
        Assert.assertTrue("User's cancelled of taking a photo", callback.didCancel());
    }
}

