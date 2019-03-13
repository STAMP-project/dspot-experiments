package org.robolectric.shadows.support.v4;


import DrawerLayout.DrawerListener;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.util.TestRunnerWithManifest;


@RunWith(TestRunnerWithManifest.class)
public class ShadowDrawerLayoutTest {
    @Test
    public void canGetAndSetDrawerListener() throws Exception {
        DrawerLayout drawerLayout = new DrawerLayout(Robolectric.buildActivity(Activity.class).create().get());
        DrawerLayout.DrawerListener mockDrawerListener = Mockito.mock(DrawerListener.class);
        drawerLayout.setDrawerListener(mockDrawerListener);
        assertThat(shadowOf(drawerLayout).getDrawerListener()).isSameAs(mockDrawerListener);
    }
}

