/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.geoserver.ManifestLoader.AboutModel;
import org.geoserver.ManifestLoader.AboutModel.ManifestModel;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.test.GeoServerBaseTestSupport;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.util.Assert;

import static ManifestLoader.PROPERTIES_FILE;
import static ManifestLoader.RESOURCE_ATTRIBUTE_EXCLUSIONS;
import static ManifestLoader.VERSION_ATTRIBUTE_INCLUSIONS;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * Tests for ManifestLoader, AboutModel and ManifestModel
 *
 * @author Carlo Cancellieri - Geo-Solutions SAS
 */
@Category(SystemTest.class)
public class ManifestLoaderTest extends GeoServerSystemTestSupport {
    // singleton
    private static ManifestLoader loader;

    // jar resource name to use for tests
    private static String resourceName = "freemarker-.*";

    @Test
    public void manifestLoaderVersionsTest() {
        Assert.notNull(ManifestLoader.getVersions());
    }

    @Test
    public void manifestLoaderResourcesTest() {
        assertNotNull(ManifestLoader.getResources());
    }

    @Test
    public void filterNameByRegex() throws IllegalArgumentException {
        AboutModel resources = ManifestLoader.getResources();
        AboutModel filtered = resources.filterNameByRegex(ManifestLoaderTest.resourceName);
        // extract first resource
        ManifestModel mm = filtered.getManifests().first();
        if (mm != null) {
            Assert.isTrue(mm.getName().matches(ManifestLoaderTest.resourceName));
        } else {
            GeoServerBaseTestSupport.LOGGER.log(Level.WARNING, (("Unable to test with this resource name: " + (ManifestLoaderTest.resourceName)) + "\nNo resource found."));
        }
    }

    @Test
    public void filterPropertyByKeyOrValueTest() throws IllegalArgumentException {
        AboutModel resources = ManifestLoader.getResources();
        // extract first resource
        ManifestModel mm = resources.getManifests().first();
        if (mm == null) {
            GeoServerBaseTestSupport.LOGGER.log(Level.WARNING, (("Unable to test with this resource name: " + (ManifestLoaderTest.resourceName)) + "\nNo resource found."));
            return;
        }
        // extract first property
        Iterator<Map.Entry<String, String>> it = mm.getEntries().entrySet().iterator();
        if (!(it.hasNext())) {
            GeoServerBaseTestSupport.LOGGER.log(Level.WARNING, "Unable to test with this resource name which does not has properties.");
            return;
        }
        Map.Entry<String, String> entry = it.next();
        String propertyKey = entry.getKey();
        String propertyVal = entry.getValue();
        // check keys
        AboutModel filtered = resources.filterPropertyByKey(propertyKey);
        Iterator<ManifestModel> mit = filtered.getManifests().iterator();
        while (mit.hasNext()) {
            final ManifestModel model = mit.next();
            Assert.isTrue(model.getEntries().containsKey(propertyKey));
        } 
        // check values
        filtered = resources.filterPropertyByValue(propertyVal);
        mit = filtered.getManifests().iterator();
        while (mit.hasNext()) {
            final ManifestModel model = mit.next();
            Assert.isTrue(model.getEntries().containsValue(propertyVal));
        } 
    }

    @Test
    public void filterPropertyByKeyAndValueTest() throws IllegalArgumentException {
        AboutModel resources = ManifestLoader.getResources();
        // extract first resource
        ManifestModel mm = resources.getManifests().first();
        if (mm == null) {
            GeoServerBaseTestSupport.LOGGER.log(Level.WARNING, (("Unable to test with this resource name: " + (ManifestLoaderTest.resourceName)) + "\nNo resource found."));
            return;
        }
        // extract first property
        Iterator<Map.Entry<String, String>> it = mm.getEntries().entrySet().iterator();
        if (!(it.hasNext())) {
            GeoServerBaseTestSupport.LOGGER.log(Level.WARNING, "Unable to test with this resource name which does not has properties.");
            return;
        }
        Map.Entry<String, String> entry = it.next();
        String propertyKey = entry.getKey();
        String propertyVal = entry.getValue();
        // extract models
        AboutModel filtered = resources.filterPropertyByKeyValue(propertyKey, propertyVal);
        // check keys and values
        Iterator<ManifestModel> mit = filtered.getManifests().iterator();
        while (mit.hasNext()) {
            final ManifestModel model = mit.next();
            // check keys
            Assert.isTrue(model.getEntries().containsKey(propertyKey));
            String value = model.getEntries().get(propertyKey);
            // check value
            Assert.isTrue(value.equals(propertyVal));
        } 
    }

    @Test
    public void removeTest() {
        AboutModel resources = ManifestLoader.getResources();
        AboutModel newResources = ManifestLoader.getResources();
        ManifestModel mm = newResources.getManifests().first();
        Assert.isTrue(resources.getManifests().contains(mm));
        // test remove
        resources.remove(mm.getName());
        Assert.isTrue((!(resources.getManifests().contains(mm))));
    }

    /**
     * SubTests to check properties personalizations
     *
     * @author Carlo Cancellieri - GeoSolutions
     */
    @Category(SystemTest.class)
    @TestSetup(run = TestSetupFrequency.REPEAT)
    public static class ManifestPropertiesTest extends GeoServerSystemTestSupport {
        private File properties;

        String propertyKey;

        @Override
        protected void setUpTestData(SystemTestData testData) throws Exception {
        }

        @Before
        public void paranoidCleanup() {
            // this file randomly shows up on the main module root and breaks the test
            // could not find where it's coming from, just going to remove it if it's there.
            File rootMonitor = new File(".", "manifest.properties");
            if (rootMonitor.exists()) {
                rootMonitor.delete();
            }
        }

        @Override
        protected void onSetUp(SystemTestData testData) throws Exception {
            AboutModel resources = ManifestLoader.getResources();
            // extract first resource
            ManifestModel mm = resources.getManifests().first();
            if (mm == null) {
                GeoServerBaseTestSupport.LOGGER.log(Level.WARNING, (("Unable to test with this resource name: " + (ManifestLoaderTest.resourceName)) + "\nNo resource found."));
                return;
            }
            // extract a property
            Iterator<Map.Entry<String, String>> it = mm.getEntries().entrySet().iterator();
            if (!(it.hasNext())) {
                GeoServerBaseTestSupport.LOGGER.log(Level.WARNING, "Unable to test with this resource name which does not has properties.");
                return;
            }
            Map.Entry<String, String> entry = it.next();
            propertyKey = entry.getKey();
            FileWriter writer = null;
            try {
                properties = new File(testData.getDataDirectoryRoot(), PROPERTIES_FILE);
                writer = new FileWriter(properties);
                writer.write(((((VERSION_ATTRIBUTE_INCLUSIONS) + "=") + (propertyKey)) + "\n"));
                writer.write((((RESOURCE_ATTRIBUTE_EXCLUSIONS) + "=") + (propertyKey)));
                writer.flush();
            } catch (IOException e) {
                GeoServerBaseTestSupport.LOGGER.log(Level.WARNING, ("Unable to write test data to:" + (testData.getDataDirectoryRoot())));
                fail(e.getLocalizedMessage());
            } finally {
                IOUtils.closeQuietly(writer);
            }
            // rebuild loader with new configuration
            try {
                ManifestLoaderTest.loader = new ManifestLoader(getResourceLoader());
            } catch (Exception e) {
                GeoServerBaseTestSupport.LOGGER.log(Level.FINER, e.getMessage(), e);
                fail(e.getLocalizedMessage());
            }
        }

        @Override
        protected void onTearDown(SystemTestData testData) throws Exception {
            FileUtils.deleteQuietly(properties);
        }

        @Test
        public void filterExcludingAttributes() {
            // load resources filtering attributes EXCLUDING propertyKey
            final AboutModel resources = ManifestLoader.getResources();
            // extract resources
            final Iterator<ManifestModel> mmit = resources.getManifests().iterator();
            while (mmit.hasNext()) {
                // extract properties
                Iterator<Map.Entry<String, String>> it = mmit.next().getEntries().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    // the propertyKey should NOT be present
                    Assert.isTrue((!(propertyKey.equals(entry.getKey()))));
                } 
            } 
        }

        @Test
        public void filterIncludingAttributes() {
            // load resources filtering attributes INCLUDING propertyKey
            final AboutModel versions = ManifestLoader.getVersions();
            // extract resources
            final Iterator<ManifestModel> mmit = versions.getManifests().iterator();
            while (mmit.hasNext()) {
                // extract first property
                Iterator<Map.Entry<String, String>> it = mmit.next().getEntries().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    // the propertyKey MUST be present
                    Assert.isTrue(propertyKey.equals(entry.getKey()));
                } 
            } 
        }
    }
}

