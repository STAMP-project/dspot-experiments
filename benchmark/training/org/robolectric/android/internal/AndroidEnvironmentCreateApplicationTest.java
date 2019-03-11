package org.robolectric.android.internal;


import RuntimeEnvironment.systemContext;
import ShadowApplication.Wrapper;
import android.app.Application;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.FakeApp;
import org.robolectric.Shadows;
import org.robolectric.TestFakeApp;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.testing.TestApplication;

import static AndroidEnvironment.registerBroadcastReceivers;


@RunWith(AndroidJUnit4.class)
public class AndroidEnvironmentCreateApplicationTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = RuntimeException.class)
    public void shouldThrowWhenManifestContainsBadApplicationClassName() throws Exception {
        AndroidEnvironment.createApplication(newConfigWith("<application android:name=\"org.robolectric.BogusTestApplication\"/>)"), null);
    }

    @Test
    public void shouldReturnDefaultAndroidApplicationWhenManifestDeclaresNoAppName() throws Exception {
        Application application = AndroidEnvironment.createApplication(newConfigWith(""), null);
        assertThat(application.getClass()).isEqualTo(Application.class);
    }

    @Test
    public void shouldReturnSpecifiedApplicationWhenManifestDeclaresAppName() throws Exception {
        Application application = AndroidEnvironment.createApplication(newConfigWith("<application android:name=\"org.robolectric.shadows.testing.TestApplication\"/>"), null);
        assertThat(application.getClass()).isEqualTo(TestApplication.class);
    }

    @Test
    public void shouldAssignThePackageNameFromTheManifest() throws Exception {
        Application application = ApplicationProvider.getApplicationContext();
        assertThat(application.getPackageName()).isEqualTo("org.robolectric");
        assertThat(application.getClass()).isEqualTo(TestApplication.class);
    }

    @Test
    public void shouldRegisterReceiversFromTheManifest() throws Exception {
        // gross:
        Shadows.shadowOf(((Application) (ApplicationProvider.getApplicationContext()))).getRegisteredReceivers().clear();
        AndroidManifest appManifest = newConfigWith(("<application>" + ((((("    <receiver android:name=\"org.robolectric.fakes.ConfigTestReceiver\">" + "      <intent-filter>\n") + "        <action android:name=\"org.robolectric.ACTION_SUPERSET_PACKAGE\"/>\n") + "      </intent-filter>") + "    </receiver>") + "</application>")));
        Application application = AndroidEnvironment.createApplication(appManifest, null);
        Shadows.shadowOf(application).callAttach(systemContext);
        registerBroadcastReceivers(application, appManifest);
        List<ShadowApplication.Wrapper> receivers = Shadows.shadowOf(application).getRegisteredReceivers();
        assertThat(receivers).hasSize(1);
        assertThat(receivers.get(0).intentFilter.matchAction("org.robolectric.ACTION_SUPERSET_PACKAGE")).isTrue();
    }

    @Test
    public void shouldDoTestApplicationNameTransform() throws Exception {
        assertThat(AndroidEnvironment.getTestApplicationName(".Applicationz")).isEqualTo(".TestApplicationz");
        assertThat(AndroidEnvironment.getTestApplicationName("Applicationz")).isEqualTo("TestApplicationz");
        assertThat(AndroidEnvironment.getTestApplicationName("com.foo.Applicationz")).isEqualTo("com.foo.TestApplicationz");
    }

    @Test
    public void shouldLoadConfigApplicationIfSpecified() throws Exception {
        Application application = AndroidEnvironment.createApplication(newConfigWith(("<application android:name=\"" + ("ClassNameToIgnore" + "\"/>"))), new Config.Builder().setApplication(TestFakeApp.class).build());
        assertThat(application.getClass()).isEqualTo(TestFakeApp.class);
    }

    @Test
    public void shouldLoadConfigInnerClassApplication() throws Exception {
        Application application = AndroidEnvironment.createApplication(newConfigWith(("<application android:name=\"" + ("ClassNameToIgnore" + "\"/>"))), new Config.Builder().setApplication(AndroidEnvironmentCreateApplicationTest.TestFakeAppInner.class).build());
        assertThat(application.getClass()).isEqualTo(AndroidEnvironmentCreateApplicationTest.TestFakeAppInner.class);
    }

    @Test
    public void shouldLoadTestApplicationIfClassIsPresent() throws Exception {
        Application application = AndroidEnvironment.createApplication(newConfigWith((("<application android:name=\"" + (FakeApp.class.getName())) + "\"/>")), null);
        assertThat(application.getClass()).isEqualTo(TestFakeApp.class);
    }

    @Test
    public void whenNoAppManifestPresent_shouldCreateGenericApplication() throws Exception {
        Application application = AndroidEnvironment.createApplication(null, null);
        assertThat(application.getClass()).isEqualTo(Application.class);
    }

    public static class TestFakeAppInner extends Application {}
}

