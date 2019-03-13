package roboguice;


import RoboGuice.Util;
import RoboGuice.injectors;
import Robolectric.application;
import Stage.DEVELOPMENT;
import android.app.Activity;
import com.google.inject.AbstractModule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.activity.RoboActivity;


@RunWith(RobolectricTestRunner.class)
public class RoboGuiceTest {
    @Test
    public void destroyInjectorShouldRemoveContext() {
        final Activity activity = Robolectric.buildActivity(RoboActivity.class).get();
        RoboGuice.getInjector(activity);
        Assert.assertThat(injectors.size(), CoreMatchers.equalTo(1));
        RoboGuice.destroyInjector(activity);
        Assert.assertThat(injectors.size(), CoreMatchers.equalTo(1));
        RoboGuice.destroyInjector(application);
        Assert.assertThat(injectors.size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void resetShouldRemoveContext() {
        final Activity activity = Robolectric.buildActivity(RoboActivity.class).get();
        RoboGuice.getInjector(activity);
        Assert.assertThat(injectors.size(), CoreMatchers.equalTo(1));
        Util.reset();
        Assert.assertThat(injectors.size(), CoreMatchers.equalTo(0));
    }

    // https://github.com/roboguice/roboguice/issues/87
    @Test
    public void shouldOnlyCallConfigureOnce() {
        final int[] i = new int[]{ 0 };
        RoboGuice.getOrCreateBaseApplicationInjector(application, DEVELOPMENT, RoboGuice.newDefaultRoboModule(application), new AbstractModule() {
            @Override
            protected void configure() {
                ++(i[0]);
            }
        });
        Assert.assertThat(i[0], CoreMatchers.equalTo(1));
    }
}

