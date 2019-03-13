package org.robobinding.supportwidget.swiperefreshlayout;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @since 
 * @version 
 * @author Liang Song
 */
@Config(manifest = Config.NONE, shadows = { ShadowSwipeRefreshLayout.class })
@RunWith(RobolectricTestRunner.class)
public class SwipeRefreshAddOnTest {
    private SwipeRefreshLayout view;

    private SwipeRefreshLayoutAddOn viewListeners;

    @Mock
    OnRefreshListener listener1;

    @Mock
    OnRefreshListener listener2;

    @Test
    public void shouldSupportMultipleOnRefreshListeners() throws Exception {
        viewListeners.addOnRefreshListener(listener1);
        viewListeners.addOnRefreshListener(listener2);
        ShadowSwipeRefreshLayout shadowView = ((ShadowSwipeRefreshLayout) (Shadows.shadowOf(view)));
        shadowView.getOnRefreshListener().onRefresh();
        Mockito.verify(listener1, Mockito.times(1)).onRefresh();
        Mockito.verify(listener2, Mockito.times(1)).onRefresh();
    }
}

