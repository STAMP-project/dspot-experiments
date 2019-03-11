package org.robolectric.shadows;


import AudioAttributes.USAGE_NOTIFICATION;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OnOpChangedListener;
import android.app.AppOpsManager.PackageOps;
import android.os.Binder;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAppOpsManager.ModeAndException;


/**
 * Unit tests for {@link ShadowAppOpsManager}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.KITKAT)
public class ShadowAppOpsManagerTest {
    private static final String PACKAGE_NAME1 = "com.company1.pkg1";

    private static final String PACKAGE_NAME2 = "com.company2.pkg2";

    private static final int UID_1 = 10000;

    private static final int UID_2 = 10001;

    // Can be used as an argument of getOpsForPackage().
    private static final int[] NO_OP_FILTER = null;

    private AppOpsManager appOps;

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void checkOpNoThrow_noModeSet_atLeastP_shouldReturnModeAllowed() {
        assertThat(appOps.checkOpNoThrow(AppOpsManager.OPSTR_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ALLOWED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void setMode_withModeDefault_atLeastP_checkOpNoThrow_shouldReturnModeDefault() {
        appOps.setMode(AppOpsManager.OPSTR_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, AppOpsManager.MODE_DEFAULT);
        assertThat(appOps.checkOpNoThrow(AppOpsManager.OPSTR_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_DEFAULT);
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void checkOpNoThrow_noModeSet_atLeastKitKat_shouldReturnModeAllowed() {
        assertThat(/* op= */
        appOps.checkOpNoThrow(2, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ALLOWED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void setMode_withModeDefault_atLeastKitKat_checkOpNoThrow_shouldReturnModeDefault() {
        /* op= */
        appOps.setMode(2, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, AppOpsManager.MODE_DEFAULT);
        assertThat(/* op= */
        appOps.checkOpNoThrow(2, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_DEFAULT);
    }

    @Test
    @Config(maxSdk = VERSION_CODES.O_MR1)
    public void setMode_checkOpNoThrow_belowP() {
        assertThat(appOps.checkOpNoThrow(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ALLOWED);
        appOps.setMode(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, AppOpsManager.MODE_ERRORED);
        assertThat(appOps.checkOpNoThrow(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ERRORED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void setMode_checkOpNoThrow_atLeastP() {
        assertThat(appOps.checkOpNoThrow(AppOpsManager.OPSTR_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ALLOWED);
        appOps.setMode(AppOpsManager.OPSTR_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, AppOpsManager.MODE_ERRORED);
        assertThat(appOps.checkOpNoThrow(AppOpsManager.OPSTR_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ERRORED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O_MR1)
    public void noModeSet_atLeastO_noteProxyOpNoThrow_shouldReturnModeAllowed() {
        assertThat(appOps.noteProxyOpNoThrow(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ALLOWED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O_MR1)
    public void setMode_withModeDefault_atLeastO_noteProxyOpNoThrow_shouldReturnModeDefault() {
        appOps.setMode(AppOpsManager.OP_GPS, Binder.getCallingUid(), ShadowAppOpsManagerTest.PACKAGE_NAME1, AppOpsManager.MODE_DEFAULT);
        assertThat(appOps.noteProxyOpNoThrow(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_DEFAULT);
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void setMode_noteProxyOpNoThrow_atLeastO() {
        assertThat(appOps.noteProxyOpNoThrow(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ALLOWED);
        appOps.setMode(AppOpsManager.OP_GPS, Binder.getCallingUid(), ShadowAppOpsManagerTest.PACKAGE_NAME1, AppOpsManager.MODE_ERRORED);
        assertThat(appOps.noteProxyOpNoThrow(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ERRORED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void startStopWatchingMode() {
        OnOpChangedListener callback = Mockito.mock(OnOpChangedListener.class);
        appOps.startWatchingMode(AppOpsManager.OPSTR_FINE_LOCATION, ShadowAppOpsManagerTest.PACKAGE_NAME1, callback);
        appOps.setMode(AppOpsManager.OP_FINE_LOCATION, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, AppOpsManager.MODE_ERRORED);
        Mockito.verify(callback).onOpChanged(AppOpsManager.OPSTR_FINE_LOCATION, ShadowAppOpsManagerTest.PACKAGE_NAME1);
        appOps.stopWatchingMode(callback);
        appOps.setMode(AppOpsManager.OP_FINE_LOCATION, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, AppOpsManager.MODE_ALLOWED);
        Mockito.verifyNoMoreInteractions(callback);
    }

    @Test
    public void noteOp() {
        assertThat(appOps.noteOp(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ALLOWED);
        // Use same op more than once
        assertThat(appOps.noteOp(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ALLOWED);
        assertThat(appOps.noteOp(AppOpsManager.OP_SEND_SMS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1)).isEqualTo(AppOpsManager.MODE_ALLOWED);
    }

    @Test
    public void getOpsForPackage_noOps() {
        List<PackageOps> results = appOps.getOpsForPackage(ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, ShadowAppOpsManagerTest.NO_OP_FILTER);
        assertOps(results);
    }

    @Test
    public void getOpsForPackage_hasOps() {
        appOps.noteOp(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1);
        appOps.noteOp(AppOpsManager.OP_SEND_SMS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1);
        // PACKAGE_NAME2 has ops.
        List<PackageOps> results = appOps.getOpsForPackage(ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, ShadowAppOpsManagerTest.NO_OP_FILTER);
        assertOps(results, AppOpsManager.OP_GPS, AppOpsManager.OP_SEND_SMS);
        // PACKAGE_NAME2 has no ops.
        results = appOps.getOpsForPackage(ShadowAppOpsManagerTest.UID_2, ShadowAppOpsManagerTest.PACKAGE_NAME2, ShadowAppOpsManagerTest.NO_OP_FILTER);
        assertOps(results);
    }

    @Test
    public void getOpsForPackage_withOpFilter() {
        List<PackageOps> results = appOps.getOpsForPackage(ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, new int[]{ AppOpsManager.OP_GPS });
        assertOps(results);
        appOps.noteOp(AppOpsManager.OP_SEND_SMS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1);
        results = appOps.getOpsForPackage(ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, new int[]{ AppOpsManager.OP_GPS });
        assertOps(results);
        appOps.noteOp(AppOpsManager.OP_GPS, ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1);
        results = appOps.getOpsForPackage(ShadowAppOpsManagerTest.UID_1, ShadowAppOpsManagerTest.PACKAGE_NAME1, new int[]{ AppOpsManager.OP_GPS });
        assertOps(results, AppOpsManager.OP_GPS);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void setRestrictions() {
        appOps.setRestriction(AppOpsManager.OP_VIBRATE, USAGE_NOTIFICATION, AppOpsManager.MODE_ERRORED, new String[]{ ShadowAppOpsManagerTest.PACKAGE_NAME1 });
        ModeAndException modeAndException = Shadows.shadowOf(appOps).getRestriction(AppOpsManager.OP_VIBRATE, USAGE_NOTIFICATION);
        assertThat(modeAndException.mode).isEqualTo(AppOpsManager.MODE_ERRORED);
        assertThat(modeAndException.exceptionPackages).containsExactly(ShadowAppOpsManagerTest.PACKAGE_NAME1);
    }

    @Test
    public void checkPackage_doesntExist() {
        try {
            appOps.checkPackage(123, ShadowAppOpsManagerTest.PACKAGE_NAME1);
            Assert.fail();
        } catch (SecurityException e) {
            // expected
        }
    }

    @Test
    public void checkPackage_doesntBelong() {
        Shadows.shadowOf(ApplicationProvider.getApplicationContext().getPackageManager()).setPackagesForUid(111, ShadowAppOpsManagerTest.PACKAGE_NAME1);
        try {
            appOps.checkPackage(123, ShadowAppOpsManagerTest.PACKAGE_NAME1);
            Assert.fail();
        } catch (SecurityException e) {
            // expected
        }
    }

    @Test
    public void checkPackage_belongs() {
        Shadows.shadowOf(ApplicationProvider.getApplicationContext().getPackageManager()).setPackagesForUid(123, ShadowAppOpsManagerTest.PACKAGE_NAME1);
        appOps.checkPackage(123, ShadowAppOpsManagerTest.PACKAGE_NAME1);
        // check passes without exception
    }
}

