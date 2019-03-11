package sagan.tools.support;


import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.DownloadLink;
import sagan.tools.EclipseDownloads;
import sagan.tools.EclipsePackage;
import sagan.tools.EclipseRelease;


public class EclipseDownloadsXmlConverter_TwoDownloadsForSameReleaseWithDifferentPackagesTests {
    private EclipseDownloads eclipseDownloads;

    @Test
    public void addsAPlatform() throws Exception {
        MatcherAssert.assertThat(eclipseDownloads.getPlatforms().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(eclipseDownloads.getPlatforms().get("windows").getName(), Matchers.equalTo("Windows"));
    }

    @Test
    public void addsAReleaseToThePlatform() throws Exception {
        List<EclipseRelease> windowsProducts = eclipseDownloads.getPlatforms().get("windows").getReleases();
        MatcherAssert.assertThat(windowsProducts.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windowsProducts.get(0).getName(), Matchers.equalTo("Eclipse Kepler"));
    }

    @Test
    public void addsTwoPackages() throws Exception {
        List<EclipsePackage> windowsPackages = eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        MatcherAssert.assertThat(windowsPackages.size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(windowsPackages.get(0).getName(), Matchers.equalTo("Eclipse Standard 4.3"));
        MatcherAssert.assertThat(windowsPackages.get(1).getName(), Matchers.equalTo("Eclipse IDE for Java EE Developers"));
    }

    @Test
    public void addsDownloadLinkToEachPackage() throws Exception {
        List<EclipsePackage> windowsPackages = eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        List<DownloadLink> standardDownloadLinks = windowsPackages.get(0).getArchitectures().get(0).getDownloadLinks();
        List<DownloadLink> javaEEDownloadLinks = windowsPackages.get(1).getArchitectures().get(0).getDownloadLinks();
        MatcherAssert.assertThat(standardDownloadLinks.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(standardDownloadLinks.get(0).getOs(), Matchers.equalTo("windows"));
        MatcherAssert.assertThat(standardDownloadLinks.get(0).getUrl(), Matchers.equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32.zip"));
        MatcherAssert.assertThat(javaEEDownloadLinks.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(javaEEDownloadLinks.get(0).getOs(), Matchers.equalTo("windows"));
        MatcherAssert.assertThat(javaEEDownloadLinks.get(0).getUrl(), Matchers.equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-jee-kepler-R-win32.zip"));
    }
}

