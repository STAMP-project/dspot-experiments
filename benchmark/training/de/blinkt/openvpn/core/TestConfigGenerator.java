/**
 * Copyright (c) 2012-2018 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */
package de.blinkt.openvpn.core;


import Connection.ProxyType;
import PackageManager.NameNotFoundException;
import RuntimeEnvironment.application;
import android.content.Context;
import de.blinkt.openvpn.VpnProfile;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Created by arne on 14.03.18.
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TestConfigGenerator {
    @Test
    public void testAuthRetryGen() throws NameNotFoundException {
        /* Context mc = mock(Context.class);
        PackageManager mpm = mock(PackageManager.class);

        PackageInfo mpi = new PackageInfo();
        mpi.versionCode = 177;
        mpi.versionName = "foo";

        when(mc.getCacheDir()).thenReturn(new File("/j/unit/test/"));
        when(mc.getPackageName()).thenReturn("de.blinkt.openvpn");
        when(mc.getPackageManager()).thenReturn(mpm);
        when(mpm.getPackageInfo(eq("de.blinkt.openvpn"),eq(0))).thenReturn(mpi);
         */
        VpnProfile vp = new VpnProfile("test") {
            @Override
            public String getVersionEnvString(Context c) {
                return "no ver";
            }

            @Override
            public String getPlatformVersionEnvString() {
                return "test";
            }
        };
        vp.mAuthenticationType = VpnProfile.TYPE_USERPASS;
        vp.mAuthRetry = VpnProfile.AUTH_RETRY_NOINTERACT;
        String config = vp.getConfigFile(application, false);
        Assert.assertTrue(config.contains("\nauth-retry nointeract\n"));
        for (Connection connection : vp.mConnections)
            Assert.assertTrue(((connection.mProxyType) == (ProxyType.NONE)));

    }
}

