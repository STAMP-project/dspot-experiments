package org.robolectric.shadows;


import PackageInstaller.Session;
import PackageInstaller.SessionCallback;
import PackageInstaller.SessionInfo;
import android.content.IIntentSender;
import android.content.pm.PackageInstaller;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.OutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowPackageInstallerTest {
    private static final String TEST_PACKAGE_NAME = "com.some.other.package";

    private static final String TEST_PACKAGE_LABEL = "My Little App";

    private PackageInstaller packageInstaller;

    @Test
    public void shouldBeNoInProcessSessionsOnRobolectricStartup() {
        assertThat(packageInstaller.getAllSessions()).isEmpty();
    }

    @Test
    public void packageInstallerCreateAndAbandonSession() throws Exception {
        PackageInstaller.SessionCallback mockCallback = Mockito.mock(SessionCallback.class);
        packageInstaller.registerSessionCallback(mockCallback, new Handler());
        int sessionId = packageInstaller.createSession(ShadowPackageInstallerTest.createSessionParams("packageName"));
        PackageInstaller.SessionInfo sessionInfo = packageInstaller.getSessionInfo(sessionId);
        assertThat(sessionInfo.isActive()).isTrue();
        assertThat(sessionInfo.appPackageName).isEqualTo("packageName");
        packageInstaller.abandonSession(sessionId);
        assertThat(packageInstaller.getSessionInfo(sessionId)).isNull();
        assertThat(packageInstaller.getAllSessions()).isEmpty();
        Mockito.verify(mockCallback).onCreated(sessionId);
        Mockito.verify(mockCallback).onFinished(sessionId, false);
    }

    @Test
    public void packageInstallerOpenSession() throws Exception {
        int sessionId = packageInstaller.createSession(ShadowPackageInstallerTest.createSessionParams("packageName"));
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        assertThat(session).isNotNull();
    }

    @Test(expected = SecurityException.class)
    public void packageInstallerOpenSession_nonExistantSessionThrowsException() throws Exception {
        PackageInstaller.Session session = packageInstaller.openSession((-99));
    }

    // TODO: Initial implementation has a no-op OutputStream - complete this implementation.
    @Test
    public void sessionOpenWriteDoesNotThrowException() throws Exception {
        int sessionId = packageInstaller.createSession(ShadowPackageInstallerTest.createSessionParams("packageName"));
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        OutputStream filename = session.openWrite("filename", 0, 0);
        filename.write(10);
    }

    @Test
    public void sessionCommitSession_streamProperlyClosed() throws Exception {
        int sessionId = packageInstaller.createSession(ShadowPackageInstallerTest.createSessionParams("packageName"));
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        OutputStream outputStream = session.openWrite("filename", 0, 0);
        outputStream.close();
        session.commit(new android.content.IntentSender(ReflectionHelpers.createNullProxy(IIntentSender.class)));
    }

    @Test(expected = SecurityException.class)
    public void sessionCommitSession_streamStillOpen() throws Exception {
        int sessionId = packageInstaller.createSession(ShadowPackageInstallerTest.createSessionParams("packageName"));
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        session.openWrite("filename", 0, 0);
        session.commit(new android.content.IntentSender(ReflectionHelpers.createNullProxy(IIntentSender.class)));
    }

    @Test
    public void registerSessionCallback_sessionFails() throws Exception {
        PackageInstaller.SessionCallback mockCallback = Mockito.mock(SessionCallback.class);
        packageInstaller.registerSessionCallback(mockCallback, new Handler());
        int sessionId = packageInstaller.createSession(ShadowPackageInstallerTest.createSessionParams("packageName"));
        Mockito.verify(mockCallback).onCreated(sessionId);
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        OutputStream outputStream = session.openWrite("filename", 0, 0);
        outputStream.close();
        session.abandon();
        assertThat(packageInstaller.getAllSessions()).isEmpty();
        Mockito.verify(mockCallback).onFinished(sessionId, false);
    }

    @Test
    public void registerSessionCallback_sessionSucceeds() throws Exception {
        PackageInstaller.SessionCallback mockCallback = Mockito.mock(SessionCallback.class);
        packageInstaller.registerSessionCallback(mockCallback, new Handler());
        int sessionId = packageInstaller.createSession(ShadowPackageInstallerTest.createSessionParams("packageName"));
        Mockito.verify(mockCallback).onCreated(sessionId);
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        OutputStream outputStream = session.openWrite("filename", 0, 0);
        outputStream.close();
        session.commit(new android.content.IntentSender(ReflectionHelpers.createNullProxy(IIntentSender.class)));
        Shadows.shadowOf(packageInstaller).setSessionProgress(sessionId, 50.0F);
        Mockito.verify(mockCallback).onProgressChanged(sessionId, 50.0F);
        Mockito.verify(mockCallback).onFinished(sessionId, true);
    }
}

