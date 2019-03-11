package io.nlopez.smartlocation;


import RuntimeEnvironment.application;
import SmartLocation.ActivityRecognitionControl;
import android.content.Context;
import io.nlopez.smartlocation.activity.config.ActivityParams;
import io.nlopez.smartlocation.util.MockActivityRecognitionProvider;
import io.nlopez.smartlocation.utils.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class ActivityRecognitionControlTest {
    private static final ActivityParams DEFAULT_PARAMS = ActivityParams.NORMAL;

    private MockActivityRecognitionProvider mockProvider;

    private OnActivityUpdatedListener activityUpdatedListener;

    @Test
    public void test_activity_recognition_control_init() {
        Context context = application.getApplicationContext();
        SmartLocation smartLocation = preInitialize(false).build();
        SmartLocation.ActivityRecognitionControl activityRecognitionControl = smartLocation.activity(mockProvider);
        Mockito.verifyZeroInteractions(mockProvider);
        smartLocation = build();
        activityRecognitionControl = smartLocation.activity(mockProvider);
        Mockito.verify(mockProvider).init(ArgumentMatchers.eq(context), ArgumentMatchers.any(Logger.class));
    }

    @Test
    public void test_activity_recognition_control_start_defaults() {
        SmartLocation.ActivityRecognitionControl activityRecognitionControl = createActivityRecognitionControl();
        activityRecognitionControl.start(activityUpdatedListener);
        Mockito.verify(mockProvider).start(activityUpdatedListener, ActivityRecognitionControlTest.DEFAULT_PARAMS);
    }

    @Test
    public void test_activity_recognition_get_last_location() {
        SmartLocation.ActivityRecognitionControl activityRecognitionControl = createActivityRecognitionControl();
        activityRecognitionControl.getLastActivity();
        Mockito.verify(mockProvider).getLastActivity();
    }

    @Test
    public void test_activity_recognition_stop() {
        SmartLocation.ActivityRecognitionControl activityRecognitionControl = createActivityRecognitionControl();
        activityRecognitionControl.stop();
        Mockito.verify(mockProvider).stop();
    }
}

