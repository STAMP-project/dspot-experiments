package org.jivesoftware.util;


import ReleaseStatus.Beta;
import ReleaseStatus.Release;
import ReleaseStatus.Snapshot;
import org.hamcrest.CoreMatchers;
import org.jivesoftware.util.Version.ReleaseStatus;
import org.junit.Assert;
import org.junit.Test;


public class VersionTest {
    @Test
    public void testVersionWithInitializingConstructor() {
        Version test = new Version(3, 2, 1, ReleaseStatus.Beta, 4);
        Assert.assertEquals(3, test.getMajor());
        Assert.assertEquals(2, test.getMinor());
        Assert.assertEquals(1, test.getMicro());
        Assert.assertEquals(Beta, test.getStatus());
        Assert.assertEquals(4, test.getStatusVersion());
        Assert.assertEquals("3.2.1 Beta 4", test.getVersionString());
    }

    @Test
    public void testVersionWithRegularStringConstructor() {
        Version test = new Version("1.2.3 Beta 3");
        Assert.assertEquals(1, test.getMajor());
        Assert.assertEquals(2, test.getMinor());
        Assert.assertEquals(3, test.getMicro());
        Assert.assertEquals(Beta, test.getStatus());
        Assert.assertEquals(3, test.getStatusVersion());
        Assert.assertEquals("1.2.3 Beta 3", test.getVersionString());
    }

    @Test
    public void testVersionWithRegularStringConstructorB() {
        Version test = new Version("1.2.3 Release 3");
        Assert.assertEquals(1, test.getMajor());
        Assert.assertEquals(2, test.getMinor());
        Assert.assertEquals(3, test.getMicro());
        Assert.assertEquals(Release, test.getStatus());
        Assert.assertEquals(3, test.getStatusVersion());
        Assert.assertEquals("1.2.3 Release 3", test.getVersionString());
    }

    @Test
    public void testVersionWithNullStringConstructor() {
        Version test = new Version(null);
        Assert.assertEquals(0, test.getMajor());
        Assert.assertEquals(0, test.getMinor());
        Assert.assertEquals(0, test.getMicro());
        Assert.assertEquals(Release, test.getStatus());
        Assert.assertEquals((-1), test.getStatusVersion());
        Assert.assertEquals("0.0.0", test.getVersionString());
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testVersionComparisons() {
        Version test123 = new Version("1.2.3");
        Version test321 = new Version("3.2.1");
        Version test322 = new Version("3.2.2");
        Version test333 = new Version("3.3.3");
        Version test300 = new Version("3.0.0");
        Version test3100 = new Version("3.10.0");
        Version test29999 = new Version("2.999.999");
        Version test3100Alpha = new Version("3.10.0 Alpha");
        Version test3100Beta = new Version("3.10.0 Beta");
        Version test3100Beta1 = new Version("3.10.0 Beta 1");
        Version test3100Beta2 = new Version("3.10.0 Beta 2");
        Assert.assertEquals((-1), test123.compareTo(test321));
        Assert.assertEquals(0, test123.compareTo(test123));
        Assert.assertEquals(1, test321.compareTo(test123));
        Assert.assertTrue(test322.isNewerThan(test321));
        Assert.assertFalse(test322.isNewerThan(test333));
        Assert.assertFalse(test300.isNewerThan(test321));
        Assert.assertTrue(test3100.isNewerThan(test333));
        Assert.assertTrue(test3100.isNewerThan(test29999));
        Assert.assertTrue(test300.isNewerThan(test29999));
        Assert.assertTrue(test3100Beta.isNewerThan(test3100Alpha));
        Assert.assertTrue(test3100Beta2.isNewerThan(test3100Beta1));
    }

    @Test
    public void testVersionEquals() {
        Version version1 = new Version(3, 11, 0, ReleaseStatus.Alpha, (-1));
        Version version2 = new Version(3, 11, 0, ReleaseStatus.Alpha, (-1));
        Assert.assertEquals(version1, version2);
        Assert.assertEquals(((version1.compareTo(version2)) == 0), version1.equals(version2));
    }

    @Test
    public void willVersionAThreeDigitSnapshot() {
        final String versionString = "1.2.3-SNAPSHOT";
        Version test = new Version(versionString);
        Assert.assertThat(test.getMajor(), CoreMatchers.is(1));
        Assert.assertThat(test.getMinor(), CoreMatchers.is(2));
        Assert.assertThat(test.getMicro(), CoreMatchers.is(3));
        Assert.assertThat(test.getStatusVersion(), CoreMatchers.is((-1)));
        Assert.assertThat(test.getStatus(), CoreMatchers.is(Snapshot));
        Assert.assertThat(test.getVersionString(), CoreMatchers.is(versionString));
    }

    @Test
    public void willVersionAFourDigitSnapshot() {
        final String versionString = "1.2.3.4-snapshot";
        Version test = new Version(versionString);
        Assert.assertThat(test.getMajor(), CoreMatchers.is(1));
        Assert.assertThat(test.getMinor(), CoreMatchers.is(2));
        Assert.assertThat(test.getMicro(), CoreMatchers.is(3));
        Assert.assertThat(test.getStatusVersion(), CoreMatchers.is(4));
        Assert.assertThat(test.getStatus(), CoreMatchers.is(Snapshot));
        Assert.assertThat(test.getVersionString(), CoreMatchers.is(versionString.toUpperCase()));
    }

    @Test
    public void anAlphaVersionIgnoringTheReleaseStatusIsNotNewerThanTheReleaseVersion() {
        final Version releaseVersion = new Version("4.3.0");
        final Version alphaVersion = new Version("4.3.0 alpha");
        Assert.assertThat(releaseVersion.isNewerThan(alphaVersion), CoreMatchers.is(true));
        Assert.assertThat(releaseVersion.isNewerThan(alphaVersion.ignoringReleaseStatus()), CoreMatchers.is(false));
    }

    @Test
    public void willVersionAFourDigitRelease() {
        final String versionString = "1.2.3.4";
        final Version test = new Version(versionString);
        Assert.assertThat(test.getMajor(), CoreMatchers.is(1));
        Assert.assertThat(test.getMinor(), CoreMatchers.is(2));
        Assert.assertThat(test.getMicro(), CoreMatchers.is(3));
        Assert.assertThat(test.getStatusVersion(), CoreMatchers.is(4));
        Assert.assertThat(test.getStatus(), CoreMatchers.is(Release));
        Assert.assertThat(test.getVersionString(), CoreMatchers.is("1.2.3 Release 4"));
    }
}

