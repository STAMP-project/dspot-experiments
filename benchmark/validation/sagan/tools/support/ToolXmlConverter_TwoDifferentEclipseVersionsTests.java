package sagan.tools.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.Architecture;
import sagan.tools.EclipseVersion;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.ToolSuitePlatform;


public class ToolXmlConverter_TwoDifferentEclipseVersionsTests {
    private ToolSuiteDownloads toolSuite;

    private ToolXmlConverter toolXmlConverter;

    @Test
    public void setsTheReleaseName() {
        MatcherAssert.assertThat(toolSuite.getReleaseName(), Matchers.equalTo("3.3.0.RELEASE"));
    }

    @Test
    public void addsTheMacPlatform() throws Exception {
        MatcherAssert.assertThat(toolSuite.getPlatformList().get(1).getName(), Matchers.equalTo("Mac"));
    }

    @Test
    public void addsBothEclipseVersionsToThePlatform() throws Exception {
        ToolSuitePlatform mac = toolSuite.getPlatformList().get(1);
        MatcherAssert.assertThat(mac.getEclipseVersions().size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(mac.getEclipseVersions().get(0).getName(), Matchers.equalTo("4.3"));
        MatcherAssert.assertThat(mac.getEclipseVersions().get(1).getName(), Matchers.equalTo("3.8.2"));
    }

    @Test
    public void addsAnArchitectureToEachEclipseVersion() throws Exception {
        ToolSuitePlatform mac = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = mac.getEclipseVersions().get(0);
        MatcherAssert.assertThat(eclipseVersion.getArchitectures().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(eclipseVersion.getArchitectures().get(0).getName(), Matchers.equalTo("Mac OS X (Cocoa)"));
        EclipseVersion windowsEclipseVersion = mac.getEclipseVersions().get(1);
        MatcherAssert.assertThat(windowsEclipseVersion.getArchitectures().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windowsEclipseVersion.getArchitectures().get(0).getName(), Matchers.equalTo("Mac OS X (Cocoa)"));
    }

    @Test
    public void addsADownloadLinkTheArchitectureInEachPlatform() throws Exception {
        Architecture v43Architecture = toolSuite.getPlatformList().get(1).getEclipseVersions().get(0).getArchitectures().get(0);
        MatcherAssert.assertThat(v43Architecture.getDownloadLinks().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(v43Architecture.getDownloadLinks().get(0).getUrl(), Matchers.equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));
        Architecture v38Architecture = toolSuite.getPlatformList().get(1).getEclipseVersions().get(1).getArchitectures().get(0);
        MatcherAssert.assertThat(v38Architecture.getDownloadLinks().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(v38Architecture.getDownloadLinks().get(0).getUrl(), Matchers.equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e3.8/spring-tool-suite-3.3.0.RELEASE-e3.8.2-macosx-cocoa-installer.dmg"));
    }
}

