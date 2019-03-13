/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.vfny.geoserver.global;


import java.io.File;
import java.io.IOException;
import javax.xml.namespace.QName;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.data.test.MockData;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests covering the former functionality of GeoServerDataDirectory.
 *
 * <p>Much of this functionality depends on the availability of GeoServerResourceLoader in the
 * application context as the bean "resourceLoader".
 *
 * @author Daniele Romagnoli, GeoSolutions SAS
 */
@Category(SystemTest.class)
public class GeoserverDataDirectoryTest extends GeoServerSystemTestSupport {
    private static final String EXTERNAL_ENTITIES = "externalEntities";

    private static final char SEPARATOR_CHAR = File.separatorChar;

    private static final String RAIN_DATA_PATH = ((("rain" + (GeoserverDataDirectoryTest.SEPARATOR_CHAR)) + "rain") + (GeoserverDataDirectoryTest.SEPARATOR_CHAR)) + "rain.asc";

    private static final QName RAIN = new QName(MockData.SF_URI, "rain", MockData.SF_PREFIX);

    @Test
    public void testFindDataFile() throws IOException {
        GeoServerResourceLoader loader = getResourceLoader();
        final File file = loader.url(("file:" + (GeoserverDataDirectoryTest.RAIN_DATA_PATH)));
        Assert.assertNotNull(file);
    }

    @Test
    public void testFindDataFileForAbsolutePath() throws IOException {
        GeoServerResourceLoader loader = getResourceLoader();
        final File dataDir = loader.getBaseDirectory();
        final String absolutePath = ((dataDir.getCanonicalPath()) + (GeoserverDataDirectoryTest.SEPARATOR_CHAR)) + (GeoserverDataDirectoryTest.RAIN_DATA_PATH);
        final File file = loader.url(absolutePath);
        Assert.assertNotNull(file);
    }

    @Test
    public void testFindDataFileForCustomUrl() throws IOException {
        GeoServerResourceLoader loader = getResourceLoader();
        final File file = loader.url("sde://user:password@server:port");
        Assert.assertNull(file);// Before GEOS-5931 it would have been returned a file again

    }

    @Test
    public void testStyleWithExternalEntities() throws Exception {
        GeoServerDataDirectory dd = getDataDirectory();
        StyleInfo si = getCatalog().getStyleByName(GeoserverDataDirectoryTest.EXTERNAL_ENTITIES);
        try {
            dd.parsedStyle(si);
            Assert.fail("Should have failed with a parse error");
        } catch (Exception e) {
            String message = e.getMessage();
            Assert.assertThat(message, CoreMatchers.containsString("Entity resolution disallowed"));
            Assert.assertThat(message, CoreMatchers.containsString("/this/file/does/not/exist"));
        }
    }
}

