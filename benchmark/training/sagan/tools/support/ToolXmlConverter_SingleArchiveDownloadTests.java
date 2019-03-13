package sagan.tools.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.UpdateSiteArchive;


public class ToolXmlConverter_SingleArchiveDownloadTests {
    private ToolSuiteDownloads toolSuite;

    private ToolXmlConverter toolXmlConverter;

    @Test
    public void addsAnUpdateSiteArchive() {
        MatcherAssert.assertThat(toolSuite.getArchives().size(), Matchers.equalTo(1));
        UpdateSiteArchive archive = toolSuite.getArchives().get(0);
        MatcherAssert.assertThat(archive.getVersion(), Matchers.equalTo("4.3.x"));
        MatcherAssert.assertThat(archive.getUrl(), Matchers.equalTo("http://dist.springsource.com/release/TOOLS/update/3.3.0.RELEASE/e4.3/springsource-tool-suite-3.3.0.RELEASE-e4.3-updatesite.zip"));
        MatcherAssert.assertThat(archive.getFileSize(), Matchers.equalTo("172MB"));
        MatcherAssert.assertThat(archive.getFileName(), Matchers.equalTo("springsource-tool-suite-3.3.0.RELEASE-e4.3-updatesite.zip"));
    }
}

