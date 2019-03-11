package org.robolectric;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class MavenRoboSettingsTest {
    private String originalMavenRepositoryId;

    private String originalMavenRepositoryUrl;

    private String originalMavenRepositoryUserName;

    private String originalMavenRepositoryPassword;

    @Test
    public void getMavenRepositoryId_defaultSonatype() {
        Assert.assertEquals("sonatype", MavenRoboSettings.getMavenRepositoryId());
    }

    @Test
    public void setMavenRepositoryId() {
        MavenRoboSettings.setMavenRepositoryId("testRepo");
        Assert.assertEquals("testRepo", MavenRoboSettings.getMavenRepositoryId());
    }

    @Test
    public void getMavenRepositoryUrl_defaultSonatype() {
        Assert.assertEquals("https://oss.sonatype.org/content/groups/public/", MavenRoboSettings.getMavenRepositoryUrl());
    }

    @Test
    public void setMavenRepositoryUrl() {
        MavenRoboSettings.setMavenRepositoryUrl("http://local");
        Assert.assertEquals("http://local", MavenRoboSettings.getMavenRepositoryUrl());
    }

    @Test
    public void setMavenRepositoryUserName() {
        MavenRoboSettings.setMavenRepositoryUserName("username");
        Assert.assertEquals("username", MavenRoboSettings.getMavenRepositoryUserName());
    }

    @Test
    public void setMavenRepositoryPassword() {
        MavenRoboSettings.setMavenRepositoryPassword("password");
        Assert.assertEquals("password", MavenRoboSettings.getMavenRepositoryPassword());
    }
}

