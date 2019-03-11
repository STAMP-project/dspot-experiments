package io.github.hidroh.materialistic;


import ItemManager.MODE_DEFAULT;
import android.os.Bundle;
import io.github.hidroh.materialistic.data.ItemManager;
import javax.inject.Inject;
import javax.inject.Named;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;

import static ActivityModule.POPULAR;


@RunWith(ParameterizedRobolectricTestRunner.class)
public class PopularActivityTest {
    private final int menuResId;

    private final String expectedRange;

    private final int expectedSubtitleResId;

    private ActivityController<PopularActivity> controller;

    private PopularActivity activity;

    @Inject
    @Named(POPULAR)
    ItemManager itemManager;

    public PopularActivityTest(int menuResId, String expectedRange, int expectedSubtitleResId) {
        this.menuResId = menuResId;
        this.expectedRange = expectedRange;
        this.expectedSubtitleResId = expectedSubtitleResId;
    }

    @Test
    public void testFilter() {
        Shadows.shadowOf(activity).clickMenuItem(menuResId);
        Mockito.verify(itemManager, Mockito.atLeastOnce()).getStories(ArgumentMatchers.eq(expectedRange), ArgumentMatchers.eq(MODE_DEFAULT));
        assertThat(activity.getSupportActionBar()).hasSubtitle(expectedSubtitleResId);
        Assert.assertEquals(expectedRange, Preferences.getPopularRange(activity));
        Bundle savedState = new Bundle();
        activity.onSaveInstanceState(savedState);
        controller = Robolectric.buildActivity(PopularActivity.class);
        activity = controller.create(savedState).start().resume().visible().get();
        assertThat(activity.getSupportActionBar()).hasSubtitle(expectedSubtitleResId);
    }
}

