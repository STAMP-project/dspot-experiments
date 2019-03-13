package roboguice.service;


import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import com.google.inject.ConfigurationException;
import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import roboguice.inject.InjectView;


@RunWith(RobolectricTestRunner.class)
public class ServiceInjectionTest {
    @Test
    public void shouldBeAbleToInjectInRoboService() {
        final ServiceInjectionTest.RoboServiceA roboService = new ServiceInjectionTest.RoboServiceA();
        onCreate();
        Assert.assertThat(roboService.context, CoreMatchers.equalTo(((Context) (roboService))));
    }

    @Test
    public void shouldBeAbleToInjectInRoboIntentService() {
        final ServiceInjectionTest.RoboIntentServiceA roboService = new ServiceInjectionTest.RoboIntentServiceA("");
        onCreate();
        Assert.assertThat(roboService.context, CoreMatchers.equalTo(((Context) (roboService))));
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotAllowViewsInServices() {
        final ServiceInjectionTest.RoboServiceB roboService = new ServiceInjectionTest.RoboServiceB();
        onCreate();
    }

    public static class RoboServiceA extends RoboService {
        @Inject
        Context context;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    public static class RoboIntentServiceA extends RoboIntentService {
        @Inject
        Context context;

        public RoboIntentServiceA(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
        }
    }

    public static class RoboServiceB extends RoboService {
        @InjectView(100)
        View v;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}

