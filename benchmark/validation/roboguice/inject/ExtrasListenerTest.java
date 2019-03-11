package roboguice.inject;


import android.content.Intent;
import android.os.IBinder;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.activity.RoboActivity;
import roboguice.service.RoboService;


@RunWith(RobolectricTestRunner.class)
public class ExtrasListenerTest {
    @Test
    public void shouldInjectActivity() {
        final ExtrasListenerTest.MyRoboActivity a1 = Robolectric.buildActivity(ExtrasListenerTest.MyRoboActivity.class).create().get();
        Assert.assertThat(a1.foo, IsEqual.equalTo(10));
    }

    @Test
    public void shouldInjectService() {
        final ExtrasListenerTest.MyRoboService s1 = new ExtrasListenerTest.MyRoboService();
        try {
            onCreate();
            Assert.fail();
        } catch (Exception e) {
            // great
            Assert.assertTrue(true);
        }
    }

    protected static class MyRoboActivity extends RoboActivity {
        @InjectExtra("foo")
        protected int foo;

        @Override
        public Intent getIntent() {
            return new Intent(this, RoboActivity.class).putExtra("foo", 10);
        }
    }

    protected static class MyRoboService extends RoboService {
        @InjectExtra("foo")
        protected int foo;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}

