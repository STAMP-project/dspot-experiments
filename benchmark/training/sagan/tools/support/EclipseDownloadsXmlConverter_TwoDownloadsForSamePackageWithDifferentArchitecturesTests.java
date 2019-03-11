package sagan.tools.support;


import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.DownloadLink;
import sagan.tools.EclipseDownloads;
import sagan.tools.EclipsePackage;
import sagan.tools.EclipseRelease;


public class EclipseDownloadsXmlConverter_TwoDownloadsForSamePackageWithDifferentArchitecturesTests {
    private EclipseDownloads eclipseDownloads;

    @Test
    public void addsAPlatform() throws Exception {
        MatcherAssert.assertThat(eclipseDownloads.getPlatforms().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(eclipseDownloads.getPlatforms().get("windows").getName(), Matchers.equalTo("Windows"));
    }

    @Test
    public void addsAProductToThePlatform() throws Exception {
        List<EclipseRelease> windowsProducts = eclipseDownloads.getPlatforms().get("windows").getReleases();
        MatcherAssert.assertThat(windowsProducts.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windowsProducts.get(0).getName(), Matchers.equalTo("Eclipse Kepler"));
    }

    @Test
    public void addsAPackage() throws Exception {
        List<EclipsePackage> windowsPackages = eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        MatcherAssert.assertThat(windowsPackages.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windowsPackages.get(0).getName(), Matchers.equalTo("Eclipse Standard 4.3"));
    }

    @Test
    public void addsTwoDownloadLinksToThePackage() throws Exception {
        List<EclipsePackage> windowsPackages = eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        List<DownloadLink> win32DownloadLinks = windowsPackages.get(0).getArchitectures().get(0).getDownloadLinks();
        MatcherAssert.assertThat(win32DownloadLinks.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(win32DownloadLinks.get(0).getOs(), Matchers.equalTo("windows"));
        MatcherAssert.assertThat(win32DownloadLinks.get(0).getArchitecture(), Matchers.equalTo("32"));
        MatcherAssert.assertThat(win32DownloadLinks.get(0).getUrl(), Matchers.equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32.zip"));
        MatcherAssert.assertThat(win32DownloadLinks.get(0).getFileSize(), Matchers.equalTo("123MB"));
        MatcherAssert.assertThat(win32DownloadLinks.get(0).getFileType(), Matchers.equalTo("zip"));
        List<DownloadLink> win64DownloadLinks = windowsPackages.get(0).getArchitectures().get(1).getDownloadLinks();
        MatcherAssert.assertThat(win64DownloadLinks.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(win64DownloadLinks.get(0).getOs(), Matchers.equalTo("windows"));
        MatcherAssert.assertThat(win64DownloadLinks.get(0).getArchitecture(), Matchers.equalTo("64"));
        MatcherAssert.assertThat(win64DownloadLinks.get(0).getUrl(), Matchers.equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win64-x86_64.zip"));
        MatcherAssert.assertThat(win64DownloadLinks.get(0).getFileSize(), Matchers.equalTo("456MB"));
        MatcherAssert.assertThat(win64DownloadLinks.get(0).getFileType(), Matchers.equalTo("zip"));
    }
}

