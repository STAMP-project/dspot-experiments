package frogermcs.io.githubclient.ui.activity;


import frogermcs.io.githubclient.BuildConfig;
import frogermcs.io.githubclient.TestGithubClientApplication;
import frogermcs.io.githubclient.ui.activity.component.SplashActivityComponent;
import frogermcs.io.githubclient.utils.AnalyticsManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Created by Miroslaw Stanek on 19.09.15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = TestGithubClientApplication.class)
public class SplashActivityTests {
    @Mock
    SplashActivityComponent splashActivityComponentMock;

    @Mock
    AnalyticsManager analyticsManagerMock;

    @Test
    public void testName() throws Exception {
        SplashActivity activity = Robolectric.setupActivity(SplashActivity.class);
        Mockito.verify(activity.analyticsManager).logScreenView(ArgumentMatchers.anyString());
    }
}

