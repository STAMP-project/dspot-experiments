package org.robolectric.shadows;


import AccessibilityButtonController.AccessibilityButtonCallback;
import android.accessibilityservice.AccessibilityButtonController;
import android.accessibilityservice.AccessibilityService;
import android.os.Build.VERSION_CODES;
import android.view.accessibility.AccessibilityEvent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Unit tests for {@link ShadowAccessibilityButtonController}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.P)
public class ShadowAccessibilityButtonControllerTest {
    private AccessibilityButtonController accessibilityButtonController;

    private AccessibilityButtonCallback accessibilityButtonCallback;

    private boolean isClicked;

    private ShadowAccessibilityButtonControllerTest.MyService service;

    @Test
    public void shouldAccessibilityButtonClickedTriggered() {
        createAndRegisterAccessibilityButtonCallback();
        Shadows.shadowOf(accessibilityButtonController).performAccessibilityButtonClick();
        assertThat(isClicked).isTrue();
    }

    /**
     * AccessibilityService for {@link ShadowAccessibilityButtonControllerTest}
     */
    public static class MyService extends AccessibilityService {
        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onAccessibilityEvent(AccessibilityEvent arg0) {
            // Do nothing
        }

        @Override
        public void onInterrupt() {
            // Do nothing
        }
    }
}

