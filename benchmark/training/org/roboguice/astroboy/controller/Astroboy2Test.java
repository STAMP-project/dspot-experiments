package org.roboguice.astroboy.controller;


import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import com.google.inject.AbstractModule;
import org.junit.Test;
import org.mockito.Mockito;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;


/**
 * A testcase that swaps in a TestVibrator to verify that
 * Astroboy's {@link org.roboguice.astroboy.controller.Astroboy#brushTeeth()} method
 * works properly.
 */
public class Astroboy2Test {
    protected Application application = Mockito.mock(Application.class, Mockito.RETURNS_DEEP_STUBS);

    protected Context context = Mockito.mock(RoboActivity.class, Mockito.RETURNS_DEEP_STUBS);

    protected Vibrator vibratorMock = Mockito.mock(Vibrator.class);

    @Test
    public void brushingTeethShouldCausePhoneToVibrate() {
        // get the astroboy instance
        final Astroboy astroboy = RoboGuice.getInjector(context).getInstance(Astroboy.class);
        // do the thing
        astroboy.brushTeeth();
        // verify that by doing the thing, vibratorMock.vibrate was called
        Mockito.verify(vibratorMock).vibrate(new long[]{ 0, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50 }, (-1));
    }

    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Vibrator.class).toInstance(vibratorMock);
        }
    }
}

