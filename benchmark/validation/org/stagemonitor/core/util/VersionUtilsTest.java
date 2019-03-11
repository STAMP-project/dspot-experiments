package org.stagemonitor.core.util;


import net.bytebuddy.ByteBuddy;
import org.junit.Test;


public class VersionUtilsTest {
    private static final String BYTEBUDDY_GROUP = "net.bytebuddy";

    private static final String VERSION_PATTERN = "\\d\\.\\d+\\.\\d+";

    @Test
    public void getVersionFromPomProperties() throws Exception {
        assertThat(VersionUtils.getVersionFromPomProperties(ByteBuddy.class, VersionUtilsTest.BYTEBUDDY_GROUP, "byte-buddy")).matches(VersionUtilsTest.VERSION_PATTERN);
    }

    @Test
    public void testGetMavenCentralDownloadLink() throws Exception {
        assertThat(VersionUtils.getMavenCentralDownloadLink(VersionUtilsTest.BYTEBUDDY_GROUP, "byte-buddy-agent", "1.7.5")).isEqualTo("http://central.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.7.5/byte-buddy-agent-1.7.5.jar");
    }

    @Test
    public void testByteBuddyDownloadUrl() throws Exception {
        final String byteBuddyVersion = VersionUtils.getVersionFromPomProperties(ByteBuddy.class, VersionUtilsTest.BYTEBUDDY_GROUP, "byte-buddy");
        final String mavenCentralDownloadLink = VersionUtils.getMavenCentralDownloadLink(VersionUtilsTest.BYTEBUDDY_GROUP, "byte-buddy-agent", byteBuddyVersion);
        assertThat(mavenCentralDownloadLink).isEqualTo((((("http://central.maven.org/maven2/net/bytebuddy/byte-buddy-agent/" + byteBuddyVersion) + "/byte-buddy-agent-") + byteBuddyVersion) + ".jar"));
    }
}

