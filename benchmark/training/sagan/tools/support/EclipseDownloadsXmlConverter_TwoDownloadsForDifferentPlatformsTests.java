package sagan.tools.support;


import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.DownloadLink;
import sagan.tools.EclipseDownloads;
import sagan.tools.EclipsePackage;
import sagan.tools.EclipseRelease;


public class EclipseDownloadsXmlConverter_TwoDownloadsForDifferentPlatformsTests {
    private EclipseDownloads eclipseDownloads;

    @Test
    public void addsTwoPlatforms() throws Exception {
        MatcherAssert.assertThat(eclipseDownloads.getPlatforms().size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(eclipseDownloads.getPlatforms().get("mac").getName(), Matchers.equalTo("Mac"));
        MatcherAssert.assertThat(eclipseDownloads.getPlatforms().get("windows").getName(), Matchers.equalTo("Windows"));
    }

    @Test
    public void addsAProductToEachPlatform() throws Exception {
        List<EclipseRelease> macProducts = eclipseDownloads.getPlatforms().get("mac").getReleases();
        MatcherAssert.assertThat(macProducts.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(macProducts.get(0).getName(), Matchers.equalTo("Eclipse Kepler"));
        List<EclipseRelease> windowsProducts = eclipseDownloads.getPlatforms().get("windows").getReleases();
        MatcherAssert.assertThat(windowsProducts.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windowsProducts.get(0).getName(), Matchers.equalTo("Eclipse Kepler"));
    }

    @Test
    public void addsTwoPackages() throws Exception {
        List<EclipsePackage> macPackages = eclipseDownloads.getPlatforms().get("mac").getReleases().get(0).getPackages();
        MatcherAssert.assertThat(macPackages.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(macPackages.get(0).getName(), Matchers.equalTo("Eclipse Standard 4.3"));
        List<EclipsePackage> windowsPackages = eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        MatcherAssert.assertThat(windowsPackages.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windowsPackages.get(0).getName(), Matchers.equalTo("Eclipse Standard 4.3"));
    }

    @Test
    public void addsDownloadLinksToTheArchitectures() throws Exception {
        List<EclipsePackage> macPackages = eclipseDownloads.getPlatforms().get("mac").getReleases().get(0).getPackages();
        List<DownloadLink> macDownloadLinks = macPackages.get(0).getArchitectures().get(0).getDownloadLinks();
        MatcherAssert.assertThat(macDownloadLinks.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(macDownloadLinks.get(0).getOs(), Matchers.equalTo("mac"));
        MatcherAssert.assertThat(macDownloadLinks.get(0).getUrl(), Matchers.equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-macosx-cocoa.tar.gz"));
        List<EclipsePackage> windowsPackages = eclipseDownloads.getPlatforms().get("windows").getReleases().get(0).getPackages();
        List<DownloadLink> windowsDownloadLinks = windowsPackages.get(0).getArchitectures().get(0).getDownloadLinks();
        MatcherAssert.assertThat(windowsDownloadLinks.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(windowsDownloadLinks.get(0).getOs(), Matchers.equalTo("windows"));
        MatcherAssert.assertThat(windowsDownloadLinks.get(0).getUrl(), Matchers.equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-win32-x86_64.zip"));
    }
}

