package org.robolectric.shadows;


import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback;
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.hardware.fingerprint.FingerprintManager.CryptoObject;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.security.Signature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.M)
public class ShadowFingerprintManagerTest {
    private FingerprintManager manager;

    @Test
    public void authenticate_success() {
        AuthenticationCallback mockCallback = Mockito.mock(AuthenticationCallback.class);
        CryptoObject cryptoObject = new CryptoObject(Mockito.mock(Signature.class));
        manager.authenticate(cryptoObject, null, 0, mockCallback, null);
        Shadows.shadowOf(manager).authenticationSucceeds();
        ArgumentCaptor<AuthenticationResult> result = ArgumentCaptor.forClass(AuthenticationResult.class);
        Mockito.verify(mockCallback).onAuthenticationSucceeded(result.capture());
        assertThat(result.getValue().getCryptoObject()).isEqualTo(cryptoObject);
    }

    @Test
    public void authenticate_failure() {
        AuthenticationCallback mockCallback = Mockito.mock(AuthenticationCallback.class);
        CryptoObject cryptoObject = new CryptoObject(Mockito.mock(Signature.class));
        manager.authenticate(cryptoObject, null, 0, mockCallback, null);
        Shadows.shadowOf(manager).authenticationFails();
        Mockito.verify(mockCallback).onAuthenticationFailed();
    }

    @Test
    public void hasEnrolledFingerprints() {
        assertThat(manager.hasEnrolledFingerprints()).isFalse();
        Shadows.shadowOf(manager).setHasEnrolledFingerprints(true);
        assertThat(manager.hasEnrolledFingerprints()).isTrue();
    }

    @Test
    public void setDefaultFingerprints() {
        assertThat(Shadows.shadowOf(manager).getEnrolledFingerprints()).isEmpty();
        Shadows.shadowOf(manager).setDefaultFingerprints(1);
        assertThat(manager.getEnrolledFingerprints().get(0).getName().toString()).isEqualTo("Fingerprint 0");
        assertThat(Shadows.shadowOf(manager).getFingerprintId(0)).isEqualTo(0);
        assertThat(manager.hasEnrolledFingerprints()).isTrue();
        Shadows.shadowOf(manager).setDefaultFingerprints(0);
        assertThat(manager.getEnrolledFingerprints()).isEmpty();
        assertThat(manager.hasEnrolledFingerprints()).isFalse();
    }

    @Test
    public void setHasEnrolledFingerprints_shouldSetNumberOfFingerprints() {
        assertThat(Shadows.shadowOf(manager).getEnrolledFingerprints()).isEmpty();
        Shadows.shadowOf(manager).setHasEnrolledFingerprints(true);
        assertThat(manager.getEnrolledFingerprints()).hasSize(1);
        assertThat(manager.hasEnrolledFingerprints()).isTrue();
        Shadows.shadowOf(manager).setHasEnrolledFingerprints(false);
        assertThat(manager.getEnrolledFingerprints()).isEmpty();
        assertThat(manager.hasEnrolledFingerprints()).isFalse();
    }

    @Test
    public void isHardwareDetected() {
        assertThat(manager.isHardwareDetected()).isFalse();
        Shadows.shadowOf(manager).setIsHardwareDetected(true);
        assertThat(manager.isHardwareDetected()).isTrue();
    }
}

