package com.amaze.filemanager.filesystem.ssh;


import com.amaze.filemanager.BuildConfig;
import com.amaze.filemanager.database.UtilsHandler;
import com.amaze.filemanager.filesystem.ssh.test.TestKeyProvider;
import com.amaze.filemanager.test.ShadowCryptUtil;
import java.io.IOException;
import net.schmizz.sshj.common.SecurityUtils;
import org.apache.sshd.server.SshServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.multidex.ShadowMultiDex;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = { ShadowMultiDex.class, ShadowCryptUtil.class })
public class SshConnectionPoolTest {
    private SshServer server;

    private UtilsHandler utilsHandler;

    private static TestKeyProvider hostKeyProvider;

    private static TestKeyProvider userKeyProvider;

    @Test
    public void testGetConnectionWithUsernameAndPassword() throws IOException {
        createSshServer("testuser", "testpassword");
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("127.0.0.1", 22222, SecurityUtils.getFingerprint(SshConnectionPoolTest.hostKeyProvider.getKeyPair().getPublic()), "testuser", "testpassword", null));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("127.0.0.1", 22222, SecurityUtils.getFingerprint(SshConnectionPoolTest.hostKeyProvider.getKeyPair().getPublic()), "invaliduser", "invalidpassword", null));
    }

    @Test
    public void testGetConnectionWithUsernameAndKey() throws IOException {
        createSshServer("testuser", null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("127.0.0.1", 22222, SecurityUtils.getFingerprint(SshConnectionPoolTest.hostKeyProvider.getKeyPair().getPublic()), "testuser", null, SshConnectionPoolTest.userKeyProvider.getKeyPair()));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("127.0.0.1", 22222, SecurityUtils.getFingerprint(SshConnectionPoolTest.hostKeyProvider.getKeyPair().getPublic()), "invaliduser", null, SshConnectionPoolTest.userKeyProvider.getKeyPair()));
    }

    @Test
    public void testGetConnectionWithUrl() throws IOException {
        String validPassword = "testpassword";
        createSshServer("testuser", validPassword);
        saveSshConnectionSettings("testuser", validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://testuser:testpassword@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlAndKeyAuth() throws IOException {
        createSshServer("testuser", null);
        saveSshConnectionSettings("testuser", null, SshConnectionPoolTest.userKeyProvider.getKeyPair().getPrivate());
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://testuser@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingComplexPassword1() throws IOException {
        String validPassword = "testP@ssw0rd";
        createSshServer("testuser", validPassword);
        saveSshConnectionSettings("testuser", validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://testuser:testP@ssw0rd@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingComplexPassword2() throws IOException {
        String validPassword = "testP@##word";
        createSshServer("testuser", validPassword);
        saveSshConnectionSettings("testuser", validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://testuser:testP@##word@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingComplexCredential1() throws IOException {
        String validPassword = "testP@##word";
        createSshServer("testuser", validPassword);
        saveSshConnectionSettings("testuser", validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://testuser:testP@##word@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingComplexCredential2() throws IOException {
        String validPassword = "testP@##word";
        createSshServer("testuser", validPassword);
        saveSshConnectionSettings("testuser", validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://testuser:testP@##word@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingComplexCredential3() throws IOException {
        String validUsername = "test@example.com";
        String validPassword = "testP@ssw0rd";
        createSshServer(validUsername, validPassword);
        saveSshConnectionSettings(validUsername, validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://test@example.com:testP@ssw0rd@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingComplexCredential4() throws IOException {
        String validUsername = "test@example.com";
        String validPassword = "testP@ssw0##$";
        createSshServer(validUsername, validPassword);
        saveSshConnectionSettings(validUsername, validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://test@example.com:testP@ssw0##$@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingMinusSignInPassword1() throws IOException {
        String validUsername = "test@example.com";
        String validPassword = "abcd-efgh";
        createSshServer(validUsername, validPassword);
        saveSshConnectionSettings(validUsername, validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://test@example.com:abcd-efgh@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingMinusSignInPassword2() throws IOException {
        String validUsername = "test@example.com";
        String validPassword = "---------------";
        createSshServer(validUsername, validPassword);
        saveSshConnectionSettings(validUsername, validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://test@example.com:---------------@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingMinusSignInPassword3() throws IOException {
        String validUsername = "test@example.com";
        String validPassword = "--agdiuhdpost15";
        createSshServer(validUsername, validPassword);
        saveSshConnectionSettings(validUsername, validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://test@example.com:--agdiuhdpost15@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }

    @Test
    public void testGetConnectionWithUrlHavingMinusSignInPassword4() throws IOException {
        String validUsername = "test@example.com";
        String validPassword = "t-h-i-s-i-s-p-a-s-s-w-o-r-d-";
        createSshServer(validUsername, validPassword);
        saveSshConnectionSettings(validUsername, validPassword, null);
        Assert.assertNotNull(SshConnectionPool.getInstance().getConnection("ssh://test@example.com:t-h-i-s-i-s-p-a-s-s-w-o-r-d-@127.0.0.1:22222"));
        Assert.assertNull(SshConnectionPool.getInstance().getConnection("ssh://invaliduser:invalidpassword@127.0.0.1:22222"));
    }
}

