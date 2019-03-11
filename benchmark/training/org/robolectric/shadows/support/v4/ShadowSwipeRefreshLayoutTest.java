package org.robolectric.shadows.support.v4;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.TestRunnerWithManifest;


@RunWith(TestRunnerWithManifest.class)
public class ShadowSwipeRefreshLayoutTest {
    @Test
    public void getOnRefreshListener_shouldReturnTheListener() {
        final OnRefreshListener listener = Mockito.mock(OnRefreshListener.class);
        final SwipeRefreshLayout layout = new SwipeRefreshLayout(RuntimeEnvironment.application);
        layout.setOnRefreshListener(listener);
        assertThat(Shadows.shadowOf(layout).getOnRefreshListener()).isSameAs(listener);
    }
}

