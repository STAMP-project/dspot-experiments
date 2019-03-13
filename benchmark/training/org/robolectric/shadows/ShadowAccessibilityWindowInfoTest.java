package org.robolectric.shadows;


import android.os.Build.VERSION_CODES;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowAccessibilityWindowInfoTest {
    private AccessibilityWindowInfo window;

    private ShadowAccessibilityWindowInfo shadow;

    @Test
    public void shouldNotHaveRootNode() {
        assertThat(((shadow.getRoot()) == null)).isEqualTo(true);
    }

    @Test
    public void shouldHaveAssignedRoot() {
        AccessibilityNodeInfo node = AccessibilityNodeInfo.obtain();
        shadow.setRoot(node);
        assertThat(shadow.getRoot()).isEqualTo(node);
    }

    @Test
    public void testSetTitle() {
        assertThat(shadow.getTitle()).isNull();
        CharSequence title = "Title";
        shadow.setTitle(title);
        assertThat(shadow.getTitle()).isEqualTo(title);
    }
}

