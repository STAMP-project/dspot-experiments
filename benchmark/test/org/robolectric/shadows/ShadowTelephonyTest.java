package org.robolectric.shadows;


import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.provider.Telephony.Sms;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowTelephony.ShadowSms;


/**
 * Unit tests for {@link ShadowTelephony}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.KITKAT)
public class ShadowTelephonyTest {
    private static final String TEST_PACKAGE_NAME = "test.package.name";

    private Context context;

    @Test
    public void shadowSms_getDefaultSmsPackage() {
        ShadowSms.setDefaultSmsPackage(ShadowTelephonyTest.TEST_PACKAGE_NAME);
        assertThat(Sms.getDefaultSmsPackage(context)).isEqualTo(ShadowTelephonyTest.TEST_PACKAGE_NAME);
    }

    @Test
    public void shadowSms_getDefaultSmsPackage_returnsNull_whenNoSmsPackageIsSet() {
        // Make sure #reset is doing its job
        ShadowSms.setDefaultSmsPackage(ShadowTelephonyTest.TEST_PACKAGE_NAME);
        ShadowSms.reset();
        assertThat(Sms.getDefaultSmsPackage(context)).isNull();
    }
}

