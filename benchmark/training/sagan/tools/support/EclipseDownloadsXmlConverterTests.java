package sagan.tools.support;


import java.util.List;
import java.util.Map;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import sagan.tools.EclipseDownloads;
import sagan.tools.EclipsePackage;
import sagan.tools.EclipsePlatform;
import sagan.tools.EclipseRelease;


public class EclipseDownloadsXmlConverterTests {
    private EclipseDownloads eclipseDownloads;

    private Map<String, EclipsePlatform> platforms;

    @Test
    public void hasTheThreePlatforms() throws Exception {
        Assert.assertThat(platforms.size(), Is.is(3));
        Assert.assertThat(platforms, hasKey("windows"));
        Assert.assertThat(platforms, hasKey("mac"));
        Assert.assertThat(platforms, hasKey("linux"));
    }

    @Test
    public void hasTheCorrectPlatformName() throws Exception {
        Assert.assertThat(platforms.get("windows").getName(), Is.is("Windows"));
    }

    @Test
    public void excludesGroovyAndStsPackages() throws Exception {
        List<EclipseRelease> packages = platforms.get("windows").getReleases();
        for (EclipseRelease eclipsePackage : packages) {
            Assert.assertThat(eclipsePackage.getName(), not(containsString("Spring Tool Suite")));
            Assert.assertThat(eclipsePackage.getName(), not(containsString("Groovy")));
        }
    }

    @Test
    public void hasProducts() throws Exception {
        List<EclipseRelease> products = platforms.get("windows").getReleases();
        Assert.assertThat(products.size(), Is.is(5));
        Assert.assertThat(products.get(0).getName(), equalTo("Eclipse Kepler"));
        Assert.assertThat(products.get(0).getEclipseVersion(), equalTo("Eclipse 4.3"));
        Assert.assertThat(products.get(1).getName(), equalTo("Eclipse Juno"));
        Assert.assertThat(products.get(1).getEclipseVersion(), equalTo("Eclipse 4.2.2"));
        Assert.assertThat(products.get(2).getName(), equalTo("Eclipse Indigo"));
        Assert.assertThat(products.get(2).getEclipseVersion(), equalTo("Eclipse 3.7.2"));
        Assert.assertThat(products.get(3).getName(), equalTo("Eclipse Helios"));
        Assert.assertThat(products.get(3).getEclipseVersion(), equalTo("Eclipse 3.6.2"));
        Assert.assertThat(products.get(4).getName(), equalTo("Eclipse Galileo"));
        Assert.assertThat(products.get(4).getEclipseVersion(), equalTo("Eclipse 3.5.2"));
    }

    @Test
    public void productHasPackages() throws Exception {
        List<EclipsePackage> packages = platforms.get("windows").getReleases().get(0).getPackages();
        Assert.assertThat(packages.size(), Is.is(4));
        Assert.assertThat(packages.get(0).getName(), equalTo("Eclipse Standard 4.3"));
        Assert.assertThat(packages.get(1).getName(), equalTo("Eclipse IDE for Java EE Developers"));
        Assert.assertThat(packages.get(2).getName(), equalTo("Eclipse IDE for Java Developers"));
        Assert.assertThat(packages.get(3).getName(), equalTo("Eclipse for RCP and RAP Developers"));
    }
}

