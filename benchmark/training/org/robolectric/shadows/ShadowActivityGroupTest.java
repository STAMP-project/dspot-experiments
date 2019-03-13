package org.robolectric.shadows;


import android.app.Activity;
import android.app.ActivityGroup;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowActivityGroupTest {
    @Test
    public void getCurrentActivity_shouldReturnTheProvidedCurrentActivity() throws Exception {
        ActivityGroup activityGroup = new ActivityGroup();
        Activity activity = new Activity();
        Shadows.shadowOf(activityGroup).setCurrentActivity(activity);
        assertThat(activityGroup.getCurrentActivity()).isSameAs(activity);
    }
}

