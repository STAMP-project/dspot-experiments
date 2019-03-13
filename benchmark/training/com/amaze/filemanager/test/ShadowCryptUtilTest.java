package com.amaze.filemanager.test;


import RuntimeEnvironment.application;
import com.amaze.filemanager.BuildConfig;
import com.amaze.filemanager.database.UtilsHandler;
import com.amaze.filemanager.filesystem.ssh.SshClientUtils;
import com.amaze.filemanager.utils.files.CryptUtil;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.multidex.ShadowMultiDex;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = { ShadowMultiDex.class, ShadowCryptUtil.class })
public class ShadowCryptUtilTest {
    @Test
    public void testEncryptDecrypt() throws IOException, GeneralSecurityException {
        String text = "test";
        String encrypted = CryptUtil.encryptPassword(application, text);
        Assert.assertEquals(text, CryptUtil.decryptPassword(application, encrypted));
    }

    @Test
    public void testWithUtilsHandler() throws IOException, GeneralSecurityException {
        UtilsHandler utilsHandler = new UtilsHandler(RuntimeEnvironment.application);
        utilsHandler.onCreate(utilsHandler.getWritableDatabase());
        String fingerprint = "00:11:22:33:44:55:66:77:88:99:aa:bb:cc:dd:ee:ff";
        String url = "ssh://test:test@127.0.0.1:22";
        utilsHandler.addSsh("Test", SshClientUtils.encryptSshPathAsNecessary(url), fingerprint, null, null);
        Assert.assertEquals(fingerprint, utilsHandler.getSshHostKey(url));
    }
}

