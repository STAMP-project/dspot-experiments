package org.roboguice.astroboy.activity;


import android.app.Activity;
import android.os.Vibrator;
import com.google.inject.AbstractModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.roboguice.astroboy.controller.AstroboyRemoteControl;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;


/**
 * A testcase that swaps in a dependency of a RoboActivity to verify that
 * it properly uses it.
 */
@RunWith(RobolectricTestRunner.class)
public class AstroboyMasterConsoleTest {
    protected Vibrator vibratorMock = Mockito.mock(Vibrator.class);

    private AstroboyRemoteControl astroboyRemoteControlMock = Mockito.mock(AstroboyRemoteControl.class, Mockito.RETURNS_DEEP_STUBS);

    private AstroboyMasterConsole astroboyMasterConsole;

    private ActivityController<AstroboyMasterConsole> astroboyMasterConsoleController;

    @Test
    public void clickOnBrushTeethTriggersRemoteControl() {
        astroboyMasterConsole.brushTeethButton.callOnClick();
        Mockito.verify(astroboyRemoteControlMock).brushTeeth();
    }

    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Vibrator.class).toInstance(vibratorMock);
            bind(Activity.class).toInstance(astroboyMasterConsole);
            bind(AstroboyRemoteControl.class).toInstance(astroboyRemoteControlMock);
        }
    }
}

