package sagan.tools.support;


import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import sagan.support.cache.CachedRestClient;
import sagan.tools.DownloadLink;
import sagan.tools.EclipseVersion;
import sagan.tools.ToolSuiteDownloads;
import sagan.tools.ToolSuitePlatform;


/**
 * Unit tests for {@link ToolsService}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ToolsServiceTests {
    private ToolsService service;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CachedRestClient restClient;

    @Test
    public void testGetStsDownloads() throws Exception {
        ToolSuiteDownloads toolSuite = service.getStsGaDownloads();
        MatcherAssert.assertThat(toolSuite, Matchers.notNullValue());
        MatcherAssert.assertThat(toolSuite.getWhatsNew(), Matchers.equalTo("http://static.springsource.org/sts/nan/v360/NewAndNoteworthy.html"));
        List<ToolSuitePlatform> platforms = toolSuite.getPlatformList();
        MatcherAssert.assertThat(platforms.size(), Matchers.equalTo(3));
        ToolSuitePlatform windows = platforms.get(0);
        ToolSuitePlatform mac = platforms.get(1);
        MatcherAssert.assertThat(windows.getName(), Matchers.equalTo("Windows"));
        MatcherAssert.assertThat(mac.getName(), Matchers.equalTo("Mac"));
        MatcherAssert.assertThat(platforms.get(2).getName(), Matchers.equalTo("Linux"));
        List<EclipseVersion> eclipseVersions = windows.getEclipseVersions();
        MatcherAssert.assertThat(eclipseVersions.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(eclipseVersions.get(0).getName(), Matchers.equalTo("4.4"));
        MatcherAssert.assertThat(windows.getEclipseVersions().get(0).getArchitectures().get(0).getDownloadLinks().size(), Matchers.equalTo(1));
        DownloadLink downloadLink = windows.getEclipseVersions().get(0).getArchitectures().get(0).getDownloadLinks().get(0);
        MatcherAssert.assertThat(downloadLink.getUrl(), Matchers.equalTo("http://download.springsource.com/release/STS/3.6.0/dist/e4.4/spring-tool-suite-3.6.0.RELEASE-e4.4-win32.zip"));
        MatcherAssert.assertThat(toolSuite.getArchives().size(), Matchers.equalTo(5));
        MatcherAssert.assertThat(toolSuite.getArchives().get(0).getVersion(), Matchers.equalTo("4.4"));
        MatcherAssert.assertThat(toolSuite.getArchives().get(1).getVersion(), Matchers.equalTo("4.3.2"));
        MatcherAssert.assertThat(toolSuite.getArchives().get(2).getVersion(), Matchers.equalTo("4.2.2"));
        MatcherAssert.assertThat(toolSuite.getArchives().get(3).getVersion(), Matchers.equalTo("3.8.2"));
        MatcherAssert.assertThat(toolSuite.getArchives().get(4).getVersion(), Matchers.equalTo("3.7.2"));
    }
}

