package org.robolectric.shadows;


import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION_CODES;
import android.view.accessibility.AccessibilityManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowAccessibilityManagerTest {
    private AccessibilityManager accessibilityManager;

    @Test
    public void shouldReturnTrueWhenEnabled() throws Exception {
        Shadows.shadowOf(accessibilityManager).setEnabled(true);
        assertThat(accessibilityManager.isEnabled()).isTrue();
        assertThat(ShadowAccessibilityManagerTest.getAccessibilityManagerInstance().isEnabled()).isTrue();
    }

    @Test
    public void shouldReturnTrueForTouchExplorationWhenEnabled() {
        Shadows.shadowOf(accessibilityManager).setTouchExplorationEnabled(true);
        assertThat(accessibilityManager.isTouchExplorationEnabled()).isTrue();
    }

    @Test
    public void shouldReturnExpectedEnabledServiceList() {
        List<AccessibilityServiceInfo> expected = new java.util.ArrayList(Arrays.asList(new AccessibilityServiceInfo()));
        Shadows.shadowOf(accessibilityManager).setEnabledAccessibilityServiceList(expected);
        assertThat(accessibilityManager.getEnabledAccessibilityServiceList(0)).isEqualTo(expected);
    }

    @Test
    public void shouldReturnExpectedInstalledServiceList() {
        List<AccessibilityServiceInfo> expected = new java.util.ArrayList(Arrays.asList(new AccessibilityServiceInfo()));
        Shadows.shadowOf(accessibilityManager).setInstalledAccessibilityServiceList(expected);
        assertThat(accessibilityManager.getInstalledAccessibilityServiceList()).isEqualTo(expected);
    }

    @Test
    public void shouldReturnExpectedAccessibilityServiceList() {
        List<ServiceInfo> expected = new java.util.ArrayList(Arrays.asList(new ServiceInfo()));
        Shadows.shadowOf(accessibilityManager).setAccessibilityServiceList(expected);
        assertThat(accessibilityManager.getAccessibilityServiceList()).isEqualTo(expected);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O_MR1)
    public void isAccessibilityButtonSupported() {
        assertThat(AccessibilityManager.isAccessibilityButtonSupported()).isTrue();
        ShadowAccessibilityManager.setAccessibilityButtonSupported(false);
        assertThat(AccessibilityManager.isAccessibilityButtonSupported()).isFalse();
        ShadowAccessibilityManager.setAccessibilityButtonSupported(true);
        assertThat(AccessibilityManager.isAccessibilityButtonSupported()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void performAccessibilityShortcut_shouldEnableAccessibilityAndTouchExploration() {
        accessibilityManager.performAccessibilityShortcut();
        assertThat(accessibilityManager.isEnabled()).isTrue();
        assertThat(accessibilityManager.isTouchExplorationEnabled()).isTrue();
    }
}

