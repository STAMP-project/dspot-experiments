package io.github.hidroh.materialistic;


import R.id.drawer;
import R.id.drawer_layout;
import android.app.Activity;
import io.github.hidroh.materialistic.test.TestListActivity;
import io.github.hidroh.materialistic.test.shadow.ShadowSupportDrawerLayout;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;

import static junit.framework.Assert.assertEquals;


@SuppressWarnings("ConstantConditions")
@Config(shadows = { ShadowSupportDrawerLayout.class })
@RunWith(ParameterizedRobolectricTestRunner.class)
public class DrawerActivityTest {
    private final int drawerResId;

    private final Class<? extends Activity> startedActivity;

    private ActivityController<TestListActivity> controller;

    private TestListActivity activity;

    public DrawerActivityTest(int drawerResId, Class<? extends Activity> startedActivity) {
        this.drawerResId = drawerResId;
        this.startedActivity = startedActivity;
    }

    @Test
    public void test() {
        ((ShadowSupportDrawerLayout) (Shadow.extract(activity.findViewById(drawer_layout)))).getDrawerListeners().get(0).onDrawerClosed(activity.findViewById(drawer));
        Assert.assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
        findViewById(drawerResId).performClick();
        ((ShadowSupportDrawerLayout) (Shadow.extract(activity.findViewById(drawer_layout)))).getDrawerListeners().get(0).onDrawerClosed(activity.findViewById(drawer));
        assertEquals(startedActivity.getName(), Shadows.shadowOf(activity).getNextStartedActivity().getComponent().getClassName());
    }
}

