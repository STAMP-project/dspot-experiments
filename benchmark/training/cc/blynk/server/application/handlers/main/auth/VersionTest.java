package cc.blynk.server.application.handlers.main.auth;


import OsType.IOS;
import OsType.OTHER;
import Version.UNKNOWN_VERSION.osType;
import Version.UNKNOWN_VERSION.versionSingleNumber;
import org.junit.Assert;
import org.junit.Test;

import static Version.UNKNOWN_VERSION;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.17.
 */
public class VersionTest {
    @Test
    public void testCorrectVersion() {
        Version version = new Version("iOS", "1.2.3");
        Assert.assertEquals(IOS, version.osType);
        Assert.assertEquals(10203, version.versionSingleNumber);
    }

    @Test
    public void wrongValues() {
        Version version = new Version("iOS", "RC13");
        Assert.assertEquals(IOS, version.osType);
        Assert.assertEquals(0, version.versionSingleNumber);
    }

    @Test
    public void wrongValues2() {
        Assert.assertEquals(OTHER, osType);
        Assert.assertEquals(0, versionSingleNumber);
    }

    @Test
    public void testToString() {
        Version version = new Version("iOS", "1.2.4");
        Assert.assertEquals("iOS-10204", version.toString());
        version = new Version("iOS", "1.1.1");
        Assert.assertEquals("iOS-10101", version.toString());
        version = UNKNOWN_VERSION;
        Assert.assertEquals("unknown-0", version.toString());
    }
}

