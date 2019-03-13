package org.robolectric.shadows;


import PackageManager.PERMISSION_DENIED;
import PackageManager.PERMISSION_GRANTED;
import android.app.slice.SliceManager;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link ShadowSliceManager}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.P)
public final class ShadowSliceManagerTest {
    private static final String PACKAGE_NAME_1 = "com.google.testing.slicemanager.foo";

    private static final int PACKAGE_1_UID = 10;

    private Uri sliceUri1;

    private static final String PACKAGE_NAME_2 = "com.google.testing.slicemanager.bar";

    private static final int PACKAGE_2_UID = 20;

    private Uri sliceUri2;

    private SliceManager sliceManager;

    @Test
    public void testGrantSlicePermission_grantsPermissionToPackage() {
        sliceManager.grantSlicePermission(ShadowSliceManagerTest.PACKAGE_NAME_1, sliceUri1);
        assertThat(/* pid= */
        sliceManager.checkSlicePermission(sliceUri1, 1, ShadowSliceManagerTest.PACKAGE_1_UID)).isEqualTo(PERMISSION_GRANTED);
    }

    @Test
    public void testGrantSlicePermission_doesNotGrantPermissionToOtherPackage() {
        sliceManager.grantSlicePermission(ShadowSliceManagerTest.PACKAGE_NAME_1, sliceUri1);
        assertThat(/* pid= */
        sliceManager.checkSlicePermission(sliceUri1, 1, ShadowSliceManagerTest.PACKAGE_2_UID)).isEqualTo(PERMISSION_DENIED);
    }

    @Test
    public void testGrantSlicePermission_doesNotGrantPermissionToOtherSliceUri() {
        sliceManager.grantSlicePermission(ShadowSliceManagerTest.PACKAGE_NAME_1, sliceUri1);
        assertThat(/* pid= */
        sliceManager.checkSlicePermission(sliceUri2, 1, ShadowSliceManagerTest.PACKAGE_1_UID)).isEqualTo(PERMISSION_DENIED);
    }

    @Test
    public void testRevokeSlicePermission_revokesPermissionToPackage() {
        sliceManager.grantSlicePermission(ShadowSliceManagerTest.PACKAGE_NAME_1, sliceUri1);
        sliceManager.revokeSlicePermission(ShadowSliceManagerTest.PACKAGE_NAME_1, sliceUri1);
        assertThat(/* pid= */
        sliceManager.checkSlicePermission(sliceUri1, 1, ShadowSliceManagerTest.PACKAGE_1_UID)).isEqualTo(PERMISSION_DENIED);
    }
}

