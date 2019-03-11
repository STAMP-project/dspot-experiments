package sagan.tools.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.Architecture;
import sagan.tools.EclipseVersion;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.ToolSuitePlatform;


public class ToolXmlConverter_TwoDifferentPlatformsTests {
    private ToolSuiteDownloads toolSuite;

    private ToolXmlConverter toolXmlConverter;

    @Test
    public void setsTheReleaseName() {
        MatcherAssert.assertThat(toolSuite.getReleaseName(), Matchers.equalTo("3.3.0.RELEASE"));
    }

    @Test
    public void addsBothPlatforms() throws Exception {
        MatcherAssert.assertThat(toolSuite.getPlatformList().get(0).getName(), Matchers.equalTo("Windows"));
        MatcherAssert.assertThat(toolSuite.getPlatformList().get(1).getName(), Matchers.equalTo("Mac"));
    }

    @Test
    public void addsAnEclipseVersionToEachPlatform() throws Exception {
        ToolSuitePlatform mac = toolSuite.getPlatformList().get(0);
        MatcherAssert.assertThat(mac.getEclipseVersions().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(mac.getEclipseVersions().get(0).getName(), Matchers.equalTo("4.3"));
        ToolSuitePlatform windows = toolSuite.getPlatformList().get(0);
        MatcherAssert.assertThat(windows.getEclipseVersions().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windows.getEclipseVersions().get(0).getName(), Matchers.equalTo("4.3"));
    }

    @Test
    public void addsAnArchitectureToTheEclipseVersionInEachPlatform() throws Exception {
        ToolSuitePlatform mac = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = mac.getEclipseVersions().get(0);
        MatcherAssert.assertThat(eclipseVersion.getArchitectures().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(eclipseVersion.getArchitectures().get(0).getName(), Matchers.equalTo("Mac OS X (Cocoa)"));
        ToolSuitePlatform windows = toolSuite.getPlatformList().get(0);
        EclipseVersion windowsEclipseVersion = windows.getEclipseVersions().get(0);
        MatcherAssert.assertThat(windowsEclipseVersion.getArchitectures().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windowsEclipseVersion.getArchitectures().get(0).getName(), Matchers.equalTo("Windows (64bit)"));
    }

    @Test
    public void addsADownloadLinkTheArchitectureInEachPlatform() throws Exception {
        Architecture macArchitecture = toolSuite.getPlatformList().get(1).getEclipseVersions().get(0).getArchitectures().get(0);
        MatcherAssert.assertThat(macArchitecture.getDownloadLinks().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(macArchitecture.getDownloadLinks().get(0).getUrl(), Matchers.equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));
        Architecture windowsArchitecture = toolSuite.getPlatformList().get(0).getEclipseVersions().get(0).getArchitectures().get(0);
        MatcherAssert.assertThat(windowsArchitecture.getDownloadLinks().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windowsArchitecture.getDownloadLinks().get(0).getUrl(), Matchers.equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-win32-x86_64-installer.exe"));
    }
}

