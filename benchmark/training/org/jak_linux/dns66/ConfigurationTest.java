package org.jak_linux.dns66;


import ApplicationInfo.FLAG_SYSTEM;
import BuildConfig.APPLICATION_ID;
import Configuration.Whitelist;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static Configuration.VERSION;


/**
 * Created by jak on 07/04/17.
 */
public class ConfigurationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testIsDownloadable() {
        try {
            newItemForLocation(null).isDownloadable();
            Assert.fail("Was null");
        } catch (NullPointerException e) {
            // OK
        }
        Assert.assertTrue("http:// URI downloadable", newItemForLocation("http://example.com").isDownloadable());
        Assert.assertTrue("https:// URI downloadable", newItemForLocation("https://example.com").isDownloadable());
        Assert.assertFalse("file:// URI downloadable", newItemForLocation("file://example.com").isDownloadable());
        Assert.assertFalse("file:// URI downloadable", newItemForLocation("file:/example.com").isDownloadable());
        Assert.assertFalse("https domain not downloadable", newItemForLocation("https.example.com").isDownloadable());
        Assert.assertFalse("http domain not downloadable", newItemForLocation("http.example.com").isDownloadable());
    }

    @Test
    public void testResolve() throws Exception {
        Configuration.Whitelist wl = new Configuration.Whitelist() {
            @Override
            Intent newBrowserIntent() {
                return Mockito.mock(Intent.class);
            }
        };
        List<ResolveInfo> resolveInfoList = new ArrayList<>();
        List<ApplicationInfo> applicationInfoList = new ArrayList<>();
        // Web browsers
        resolveInfoList.add(newResolveInfo("system-browser", 0));
        applicationInfoList.add(newApplicationInfo("system-browser", FLAG_SYSTEM));
        resolveInfoList.add(newResolveInfo("data-browser", 0));
        applicationInfoList.add(newApplicationInfo("data-browser", 0));
        // Not a browser
        applicationInfoList.add(newApplicationInfo("system-app", FLAG_SYSTEM));
        applicationInfoList.add(newApplicationInfo("data-app", 0));
        // This app
        applicationInfoList.add(newApplicationInfo(APPLICATION_ID, 0));
        PackageManager pm = Mockito.mock(PackageManager.class);
        // noinspection WrongConstant
        Mockito.when(pm.queryIntentActivities(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt())).thenReturn(resolveInfoList);
        // noinspection WrongConstant
        Mockito.when(pm.getInstalledApplications(ArgumentMatchers.anyInt())).thenReturn(applicationInfoList);
        Set<String> onVpn = new HashSet<>();
        Set<String> notOnVpn = new HashSet<>();
        wl.defaultMode = Whitelist.DEFAULT_MODE_NOT_ON_VPN;
        wl.resolve(pm, onVpn, notOnVpn);
        Assert.assertTrue(onVpn.contains(APPLICATION_ID));
        Assert.assertTrue(notOnVpn.contains("system-app"));
        Assert.assertTrue(notOnVpn.contains("data-app"));
        Assert.assertTrue(notOnVpn.contains("system-browser"));
        Assert.assertTrue(notOnVpn.contains("data-browser"));
        // Default allow on vpn
        onVpn.clear();
        notOnVpn.clear();
        wl.defaultMode = Whitelist.DEFAULT_MODE_ON_VPN;
        wl.resolve(pm, onVpn, notOnVpn);
        Assert.assertTrue(onVpn.contains(APPLICATION_ID));
        Assert.assertTrue(onVpn.contains("system-app"));
        Assert.assertTrue(onVpn.contains("data-app"));
        Assert.assertTrue(onVpn.contains("system-browser"));
        Assert.assertTrue(onVpn.contains("data-browser"));
        // Default intelligent on vpn
        onVpn.clear();
        notOnVpn.clear();
        wl.defaultMode = Whitelist.DEFAULT_MODE_INTELLIGENT;
        wl.resolve(pm, onVpn, notOnVpn);
        Assert.assertTrue(onVpn.contains(APPLICATION_ID));
        Assert.assertTrue(notOnVpn.contains("system-app"));
        Assert.assertTrue(onVpn.contains("data-app"));
        Assert.assertTrue(onVpn.contains("system-browser"));
        Assert.assertTrue(onVpn.contains("data-browser"));
        // Default intelligent on vpn
        onVpn.clear();
        notOnVpn.clear();
        wl.items.clear();
        wl.itemsOnVpn.clear();
        wl.items.add(APPLICATION_ID);
        wl.items.add("system-browser");
        wl.defaultMode = Whitelist.DEFAULT_MODE_INTELLIGENT;
        wl.resolve(pm, onVpn, notOnVpn);
        Assert.assertTrue(onVpn.contains(APPLICATION_ID));
        Assert.assertTrue(notOnVpn.contains("system-browser"));
        // Check that blacklisting works
        onVpn.clear();
        notOnVpn.clear();
        wl.items.clear();
        wl.itemsOnVpn.clear();
        wl.itemsOnVpn.add("data-app");
        wl.defaultMode = Whitelist.DEFAULT_MODE_NOT_ON_VPN;
        wl.resolve(pm, onVpn, notOnVpn);
        Assert.assertTrue(onVpn.contains("data-app"));
    }

    @Test
    public void testRead() throws Exception {
        Configuration config = Configuration.read(new StringReader("{}"));
        Assert.assertNotNull(config.hosts);
        Assert.assertNotNull(config.hosts.items);
        Assert.assertNotNull(config.whitelist);
        Assert.assertNotNull(config.whitelist.items);
        Assert.assertNotNull(config.whitelist.itemsOnVpn);
        Assert.assertNotNull(config.dnsServers);
        Assert.assertNotNull(config.dnsServers.items);
        Assert.assertTrue(config.whitelist.items.contains("com.android.vending"));
        Assert.assertTrue(config.ipV6Support);
        Assert.assertFalse(config.watchDog);
        Assert.assertFalse(config.nightMode);
        Assert.assertTrue(config.showNotification);
        Assert.assertFalse(config.autoStart);
    }

    @Test
    public void testReadNewer() throws Exception {
        thrown.expect(IOException.class);
        thrown.expectMessage(CoreMatchers.containsString("version"));
        Configuration.read(new StringReader((("{version: " + ((VERSION) + 1)) + "}")));
    }

    @Test
    public void testReadWrite() throws Exception {
        Configuration config = Configuration.read(new StringReader("{}"));
        StringWriter writer = new StringWriter();
        config.write(writer);
        Configuration config2 = Configuration.read(new StringReader(writer.toString()));
        StringWriter writer2 = new StringWriter();
        config2.write(writer2);
        Assert.assertEquals(writer.toString(), writer2.toString());
    }
}

