package sagan.tools.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.Architecture;
import sagan.tools.EclipseVersion;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.ToolSuitePlatform;


public class ToolXmlConverter_SingleDownloadTests {
    private ToolSuiteDownloads toolSuite;

    private ToolXmlConverter toolXmlConverter;

    @Test
    public void addsAReleaseName() throws Exception {
        MatcherAssert.assertThat(toolSuite.getReleaseName(), Matchers.equalTo("3.3.0.RELEASE"));
    }

    @Test
    public void addsAPlatform() throws Exception {
        MatcherAssert.assertThat(toolSuite.getPlatformList().size(), Matchers.equalTo(3));
        MatcherAssert.assertThat(toolSuite.getPlatformList().get(1).getName(), Matchers.equalTo("Mac"));
    }

    @Test
    public void addsAnEclipseVersionToThePlatform() throws Exception {
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        MatcherAssert.assertThat(platform.getEclipseVersions().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(platform.getEclipseVersions().get(0).getName(), Matchers.equalTo("4.3"));
    }

    @Test
    public void addsAnArchitectureToTheEclipseVersion() throws Exception {
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
        MatcherAssert.assertThat(eclipseVersion.getArchitectures().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(eclipseVersion.getArchitectures().get(0).getName(), Matchers.equalTo("Mac OS X (Cocoa)"));
    }

    @Test
    public void addsADownloadLinkTheArchitecture() throws Exception {
        ToolSuitePlatform platform = toolSuite.getPlatformList().get(1);
        EclipseVersion eclipseVersion = platform.getEclipseVersions().get(0);
        Architecture architecture = eclipseVersion.getArchitectures().get(0);
        MatcherAssert.assertThat(architecture.getDownloadLinks().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(architecture.getDownloadLinks().get(0).getUrl(), Matchers.equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));
        MatcherAssert.assertThat(architecture.getDownloadLinks().get(0).getOs(), Matchers.equalTo("mac"));
        MatcherAssert.assertThat(architecture.getDownloadLinks().get(0).getArchitecture(), Matchers.equalTo("32"));
    }
}

