package org.roboguice.astroboy.controller;


import Robolectric.application;
import android.os.Vibrator;
import com.google.inject.AbstractModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;


/**
 * A testcase that swaps in a TestVibrator to verify that
 * Astroboy's {@link org.roboguice.astroboy.controller.Astroboy#brushTeeth()} method
 * works properly.
 */
@RunWith(RobolectricTestRunner.class)
public class Astroboy3Test {
    protected Vibrator vibratorMock = Mockito.mock(Vibrator.class);

    @Test
    public void brushingTeethShouldCausePhoneToVibrate() {
        // get the astroboy instance
        final Astroboy astroboy = RoboGuice.getInjector(application).getInstance(Astroboy.class);
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

