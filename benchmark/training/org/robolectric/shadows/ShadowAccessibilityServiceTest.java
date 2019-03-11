package org.robolectric.shadows;


import AccessibilityService.GLOBAL_ACTION_BACK;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowAccessibilityServiceTest {
    private ShadowAccessibilityServiceTest.MyService service;

    private ShadowAccessibilityService shadow;

    /**
     * After performing a global action, it should be recorded.
     */
    @Test
    public void shouldRecordPerformedAction() {
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        assertThat(shadow.getGlobalActionsPerformed().size()).isEqualTo(1);
        assertThat(shadow.getGlobalActionsPerformed().get(0)).isEqualTo(1);
    }

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

