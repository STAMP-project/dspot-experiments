package org.robolectric.shadows;


import KeyguardManager.KeyguardLock;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardDismissCallback;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowKeyguardManagerTest {
    private static final int USER_ID = 1001;

    private KeyguardManager manager;

    @Test
    public void testIsInRestrictedInputMode() {
        assertThat(manager.inKeyguardRestrictedInputMode()).isFalse();
        Shadows.shadowOf(manager).setinRestrictedInputMode(true);
        assertThat(manager.inKeyguardRestrictedInputMode()).isTrue();
    }

    @Test
    public void testIsKeyguardLocked() {
        assertThat(manager.isKeyguardLocked()).isFalse();
        Shadows.shadowOf(manager).setKeyguardLocked(true);
        assertThat(manager.isKeyguardLocked()).isTrue();
    }

    @Test
    public void testShouldBeAbleToDisableTheKeyguardLock() throws Exception {
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock(KEYGUARD_SERVICE);
        assertThat(Shadows.shadowOf(lock).isEnabled()).isTrue();
        lock.disableKeyguard();
        assertThat(Shadows.shadowOf(lock).isEnabled()).isFalse();
        lock.reenableKeyguard();
        assertThat(Shadows.shadowOf(lock).isEnabled()).isTrue();
    }

    @Test
    public void isKeyguardSecure() {
        assertThat(manager.isKeyguardSecure()).isFalse();
        Shadows.shadowOf(manager).setIsKeyguardSecure(true);
        assertThat(manager.isKeyguardSecure()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void isDeviceSecure() {
        assertThat(manager.isDeviceSecure()).isFalse();
        Shadows.shadowOf(manager).setIsDeviceSecure(true);
        assertThat(manager.isDeviceSecure()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void isDeviceSecureByUserId() {
        assertThat(manager.isDeviceSecure(ShadowKeyguardManagerTest.USER_ID)).isFalse();
        Shadows.shadowOf(manager).setIsDeviceSecure(ShadowKeyguardManagerTest.USER_ID, true);
        assertThat(manager.isDeviceSecure(ShadowKeyguardManagerTest.USER_ID)).isTrue();
        assertThat(manager.isDeviceSecure(((ShadowKeyguardManagerTest.USER_ID) + 1))).isFalse();
        Shadows.shadowOf(manager).setIsDeviceSecure(ShadowKeyguardManagerTest.USER_ID, false);
        assertThat(manager.isDeviceSecure(ShadowKeyguardManagerTest.USER_ID)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP_MR1)
    public void isDeviceLocked() {
        assertThat(manager.isDeviceLocked()).isFalse();
        Shadows.shadowOf(manager).setIsDeviceLocked(true);
        assertThat(manager.isDeviceLocked()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP_MR1)
    public void isDeviceLockedByUserId() {
        assertThat(manager.isDeviceLocked(ShadowKeyguardManagerTest.USER_ID)).isFalse();
        Shadows.shadowOf(manager).setIsDeviceLocked(ShadowKeyguardManagerTest.USER_ID, true);
        assertThat(manager.isDeviceLocked(ShadowKeyguardManagerTest.USER_ID)).isTrue();
        assertThat(manager.isDeviceLocked(((ShadowKeyguardManagerTest.USER_ID) + 1))).isFalse();
        Shadows.shadowOf(manager).setIsDeviceLocked(ShadowKeyguardManagerTest.USER_ID, false);
        assertThat(manager.isDeviceLocked(ShadowKeyguardManagerTest.USER_ID)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void requestDismissKeyguard_dismissCancelled() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        KeyguardDismissCallback mockCallback = Mockito.mock(KeyguardDismissCallback.class);
        Shadows.shadowOf(manager).setKeyguardLocked(true);
        manager.requestDismissKeyguard(activity, mockCallback);
        // Keep the keyguard locked
        Shadows.shadowOf(manager).setKeyguardLocked(true);
        Mockito.verify(mockCallback).onDismissCancelled();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void requestDismissKeyguard_dismissSucceeded() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        KeyguardDismissCallback mockCallback = Mockito.mock(KeyguardDismissCallback.class);
        Shadows.shadowOf(manager).setKeyguardLocked(true);
        manager.requestDismissKeyguard(activity, mockCallback);
        // Unlock the keyguard
        Shadows.shadowOf(manager).setKeyguardLocked(false);
        Mockito.verify(mockCallback).onDismissSucceeded();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O_MR1)
    public void testCreateConfirmFactoryResetCredentialIntent_nullIntent() {
        assertThat(manager.isDeviceLocked()).isFalse();
        Shadows.shadowOf(manager).setConfirmFactoryResetCredentialIntent(null);
        assertThat(manager.createConfirmFactoryResetCredentialIntent(null, null, null)).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O_MR1)
    public void testCreateConfirmFactoryResetCredentialIntent() {
        assertThat(manager.isDeviceLocked()).isFalse();
        Intent intent = new Intent();
        Shadows.shadowOf(manager).setConfirmFactoryResetCredentialIntent(intent);
        assertThat(manager.createConfirmFactoryResetCredentialIntent(null, null, null)).isEqualTo(intent);
    }
}

