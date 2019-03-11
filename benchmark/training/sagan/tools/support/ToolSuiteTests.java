package sagan.tools.support;


import java.util.HashSet;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.DownloadLink;
import sagan.tools.ToolSuiteDownloads;


public class ToolSuiteTests {
    @Test
    public void testPreferredDownloadLinks() throws Exception {
        ToolSuiteDownloads toolSuite = buildToolSuite();
        Set<DownloadLink> links = new HashSet<>();
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-win32.zip", "zip", "323MB", "windows", "32"));
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-win32-x86_64.zip", "zip", "323MB", "windows", "64"));
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-macosx-cocoa-x86_64.dmg", "dmg", "323MB", "mac", "64"));
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-linux-gtk.tar.gz", "tar.gz", "323MB", "linux", "32"));
        links.add(new DownloadLink("http://example.com/spring-tool-suite-3.6.0.RELEASE-e4.4-linux-gtk-x86_64.tar.gz", "tar.gz", "323MB", "linux", "64"));
        MatcherAssert.assertThat(toolSuite.getPreferredDownloadLinks(), Matchers.equalTo(links));
    }
}

