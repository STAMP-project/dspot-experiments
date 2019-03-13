package org.robolectric.shadows;


import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.shadow.api.Shadow;


@RunWith(AndroidJUnit4.class)
public class ShadowTest {
    private ClassLoader myClassLoader;

    @Test
    public void newInstanceOf() throws Exception {
        assertThat(Shadow.newInstanceOf(Activity.class.getName()).getClass().getClassLoader()).isSameAs(myClassLoader);
    }

    @Test
    public void extractor() throws Exception {
        Activity activity = new Activity();
        assertThat(((ShadowActivity) (Shadow.extract(activity)))).isSameAs(Shadows.shadowOf(activity));
    }

    @Test
    public void otherDeprecated_extractor() throws Exception {
        Activity activity = new Activity();
        assertThat(Shadow.<Object>extract(activity)).isSameAs(Shadows.shadowOf(activity));
    }
}

