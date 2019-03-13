package org.robobinding.supportwidget.swiperefreshlayout;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robobinding.widget.EventCommand;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @since 
 * @version 
 * @author Liang Song
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = { ShadowSwipeRefreshLayout.class })
public class OnRefreshAttributeTest {
    private SwipeRefreshLayout view;

    private OnRefreshAttribute attribute;

    private EventCommand eventCommand;

    @Test
    public void givenBoundAttribute_whenPerformOnRefresh_thenEventReceived() throws Exception {
        bindAttribute(new SwipeRefreshLayoutAddOn(view));
        performOnRefresh();
        assertEventReceived();
    }

    @Test
    public void whenBinding_thenRegisterWithViewListeners() {
        SwipeRefreshLayoutAddOn viewAddOn = Mockito.mock(SwipeRefreshLayoutAddOn.class);
        bindAttribute(viewAddOn);
        Mockito.verify(viewAddOn, Mockito.times(1)).addOnRefreshListener(ArgumentMatchers.any(OnRefreshListener.class));
    }
}

