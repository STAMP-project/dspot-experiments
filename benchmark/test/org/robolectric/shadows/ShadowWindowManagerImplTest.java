package org.robolectric.shadows;


import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Unit test for {@link ShadowWindowManagerImpl}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.O)
public class ShadowWindowManagerImplTest {
    private View view;

    private LayoutParams layoutParams;

    private WindowManager windowManager;

    @Test
    public void getViews_isInitiallyEmpty() {
        List<View> views = getViews();
        assertThat(views).isEmpty();
    }

    @Test
    public void getViews_returnsAnAddedView() {
        windowManager.addView(view, layoutParams);
        List<View> views = getViews();
        assertThat(views).hasSize(1);
        assertThat(views.get(0)).isSameAs(view);
    }

    @Test
    public void getViews_doesNotReturnAViewThatWasRemoved() {
        windowManager.addView(view, layoutParams);
        windowManager.removeView(view);
        List<View> views = getViews();
        assertThat(views).isEmpty();
    }

    @Test
    public void getViews_doesNotReturnAViewThatWasRemovedImmediately() {
        windowManager.addView(view, layoutParams);
        windowManager.removeViewImmediate(view);
        List<View> views = getViews();
        assertThat(views).isEmpty();
    }
}

