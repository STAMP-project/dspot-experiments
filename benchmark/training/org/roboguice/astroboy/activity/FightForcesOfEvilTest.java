package org.roboguice.astroboy.activity;


import com.google.inject.AbstractModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.roboguice.astroboy.controller.Astroboy;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;


/**
 * A testcase that swaps in a dependency of a RoboActivity to verify that
 * it properly uses it.
 */
@RunWith(RobolectricTestRunner.class)
public class FightForcesOfEvilTest {
    private Astroboy astroboyMock = Mockito.mock(Astroboy.class);

    @Test
    public void createTriggersPunch() throws InterruptedException {
        Robolectric.buildActivity(FightForcesOfEvilActivity.class).create().start();
        Thread.sleep((6 * 1000));
        Mockito.verify(astroboyMock, Mockito.times(10)).punch();
    }

    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Astroboy.class).toInstance(astroboyMock);
        }
    }
}

