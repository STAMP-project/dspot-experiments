package com.amaze.filemanager.filesystem.usb;


import OTGUtil.PREFIX_OTG;
import android.text.TextUtils;
import com.amaze.filemanager.BuildConfig;
import com.amaze.filemanager.activities.MainActivity;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.multidex.ShadowMultiDex;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = { ShadowMultiDex.class })
public class UsbOtgTest {
    @Test
    @Config(minSdk = KITKAT)
    public void usbConnectionTest() {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class).create();
        MainActivity activity = controller.get();
        ReflectionHelpers.addUsbOtgDevice(activity);
        activity = controller.resume().get();
        boolean hasOtgStorage = false;
        ArrayList<String> storageDirectories = activity.getStorageDirectories();
        for (String file : storageDirectories) {
            if (file.startsWith(PREFIX_OTG)) {
                hasOtgStorage = true;
                break;
            }
        }
        Assert.assertTrue((("No usb storage, known storages: '" + (TextUtils.join("', '", storageDirectories))) + "'"), hasOtgStorage);
    }
}

