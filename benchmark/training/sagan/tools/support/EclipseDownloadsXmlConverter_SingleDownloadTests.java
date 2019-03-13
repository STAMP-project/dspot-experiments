package sagan.tools.support;


import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.tools.DownloadLink;
import sagan.tools.EclipseDownloads;
import sagan.tools.EclipsePackage;
import sagan.tools.EclipseRelease;


public class EclipseDownloadsXmlConverter_SingleDownloadTests {
    private EclipseDownloads eclipseDownloads;

    @Test
    public void addsAPlatform() throws Exception {
        MatcherAssert.assertThat(eclipseDownloads.getPlatforms().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(eclipseDownloads.getPlatforms().get("mac").getName(), Matchers.equalTo("Mac"));
    }

    @Test
    public void addsAProduct() throws Exception {
        List<EclipseRelease> products = eclipseDownloads.getPlatforms().get("mac").getReleases();
        MatcherAssert.assertThat(products.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(products.get(0).getName(), Matchers.equalTo("Eclipse Kepler"));
        MatcherAssert.assertThat(products.get(0).getEclipseVersion(), Matchers.equalTo("Eclipse 4.3"));
    }

    @Test
    public void addsAPackage() throws Exception {
        List<EclipsePackage> packages = eclipseDownloads.getPlatforms().get("mac").getReleases().get(0).getPackages();
        MatcherAssert.assertThat(packages.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(packages.get(0).getName(), Matchers.equalTo("Eclipse Standard 4.3"));
    }

    @Test
    public void addsAnArchitecture() throws Exception {
        EclipsePackage eclipsePackage = eclipseDownloads.getPlatforms().get("mac").getReleases().get(0).getPackages().get(0);
        MatcherAssert.assertThat(eclipsePackage.getArchitectures().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(eclipsePackage.getArchitectures().get(0).getName(), Matchers.equalTo("Mac OS X (Cocoa)"));
    }

    @Test
    public void addsADownloadLink() throws Exception {
        EclipsePackage eclipsePackage = eclipseDownloads.getPlatforms().get("mac").getReleases().get(0).getPackages().get(0);
        List<DownloadLink> downloadLinks = eclipsePackage.getArchitectures().get(0).getDownloadLinks();
        MatcherAssert.assertThat(downloadLinks.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(downloadLinks.get(0).getOs(), Matchers.equalTo("mac"));
        MatcherAssert.assertThat(downloadLinks.get(0).getArchitecture(), Matchers.equalTo("32"));
        MatcherAssert.assertThat(downloadLinks.get(0).getUrl(), Matchers.equalTo("http://eclipseXmlDownload.springsource.com/release/ECLIPSE/kepler/R/eclipse-standard-kepler-R-macosx-cocoa.tar.gz"));
        MatcherAssert.assertThat(downloadLinks.get(0).getFileSize(), Matchers.equalTo("196MB"));
        MatcherAssert.assertThat(downloadLinks.get(0).getFileType(), Matchers.equalTo("tar.gz"));
    }
}

