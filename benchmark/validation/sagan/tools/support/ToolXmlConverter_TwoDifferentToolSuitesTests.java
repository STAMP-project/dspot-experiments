package sagan.tools.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.Architecture;
import sagan.tools.ToolSuiteDownloads;


public class ToolXmlConverter_TwoDifferentToolSuitesTests {
    private ToolSuiteDownloads toolSuite;

    private ToolXmlConverter toolXmlConverter;

    @Test
    public void addsADownloadLinkForStsOnly() throws Exception {
        Architecture macArchitecture = toolSuite.getPlatformList().get(1).getEclipseVersions().get(0).getArchitectures().get(0);
        MatcherAssert.assertThat(macArchitecture.getDownloadLinks().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(macArchitecture.getDownloadLinks().get(0).getUrl(), Matchers.equalTo("http://dist.springsource.com/release/STS/3.3.0/dist/e4.3/spring-tool-suite-3.3.0.RELEASE-e4.3-macosx-cocoa-installer.dmg"));
    }
}

