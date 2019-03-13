/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.jdbcconfig.internal;


import java.io.File;
import java.io.IOException;
import org.geoserver.jdbcconfig.JDBCConfigTestSupport;
import org.geoserver.jdbcloader.JDBCLoaderPropertiesFactoryBean;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Files;
import org.geoserver.platform.resource.Resources;
import org.geotools.util.URLs;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class JDBCConfigPropertiesTest {
    protected static final String CONFIG_FILE = "jdbcconfig.properties";

    protected static final String CONFIG_SYSPROP = "jdbcconfig.properties";

    protected static final String JDBCURL_SYSPROP = "jdbcconfig.jdbcurl";

    protected static final String INITDB_SYSPROP = "jdbcconfig.initdb";

    protected static final String IMPORT_SYSPROP = "jdbcconfig.import";

    GeoServerResourceLoader loader;

    @Test
    public void testLoadDefaults() throws IOException {
        JDBCConfigPropertiesFactoryBean factory = new JDBCConfigPropertiesFactoryBean(loader);
        JDBCConfigProperties props = ((JDBCConfigProperties) (factory.createProperties()));
        Assert.assertFalse(props.isEnabled());
        Assert.assertTrue(props.isInitDb());
        Assert.assertTrue(props.isImport());
        // assert files copied over
        Assert.assertNotNull(loader.find("jdbcconfig", "jdbcconfig.properties"));
        Assert.assertNotNull(loader.find("jdbcconfig", "scripts", "initdb.postgres.sql"));
        // assert file location are accessible
        Assert.assertNotNull(factory.getFileLocations());
        // assert configuration can be stored successfully on another resource loader
        File tmpDir = JDBCConfigTestSupport.createTempDir();
        Resources.directory(Files.asResource(tmpDir).get("jdbcconfig"), true);
        GeoServerResourceLoader resourceLoader = new GeoServerResourceLoader(tmpDir);
        factory.saveConfiguration(resourceLoader);
        Assert.assertEquals(factory.getFileLocations().size(), (((resourceLoader.find("jdbcconfig").list().length) - 1) + (resourceLoader.find("jdbcconfig/scripts").list().length)));
    }

    @Test
    public void testLoadFromFile() throws Exception {
        File configFile = createDummyConfigFile();
        System.setProperty(JDBCConfigPropertiesTest.CONFIG_SYSPROP, configFile.getAbsolutePath());
        try {
            JDBCLoaderPropertiesFactoryBean factory = new JDBCConfigPropertiesFactoryBean(loader);
            JDBCConfigProperties props = ((JDBCConfigProperties) (factory.createProperties()));
            Assert.assertEquals("bar", props.getProperty("foo"));
            Assert.assertFalse(props.isInitDb());
            Assert.assertFalse(props.isImport());
        } finally {
            System.clearProperty(JDBCConfigPropertiesTest.CONFIG_SYSPROP);
        }
    }

    @Test
    public void testLoadFromURL() throws Exception {
        File configFile = createDummyConfigFile();
        System.setProperty(JDBCConfigPropertiesTest.CONFIG_SYSPROP, URLs.fileToUrl(configFile).toString());
        try {
            JDBCLoaderPropertiesFactoryBean factory = new JDBCConfigPropertiesFactoryBean(loader);
            JDBCConfigProperties props = ((JDBCConfigProperties) (factory.createProperties()));
            Assert.assertEquals("bar", props.getProperty("foo"));
            Assert.assertFalse(props.isInitDb());
            Assert.assertFalse(props.isImport());
        } finally {
            System.clearProperty(JDBCConfigPropertiesTest.CONFIG_SYSPROP);
        }
    }

    @Test
    public void testLoadFromSysProps() throws Exception {
        System.setProperty(JDBCConfigPropertiesTest.JDBCURL_SYSPROP, "jdbc:h2:nofile");
        System.setProperty(JDBCConfigPropertiesTest.INITDB_SYSPROP, "false");
        System.setProperty(JDBCConfigPropertiesTest.IMPORT_SYSPROP, "false");
        try {
            JDBCLoaderPropertiesFactoryBean factory = new JDBCConfigPropertiesFactoryBean(loader);
            JDBCConfigProperties props = ((JDBCConfigProperties) (factory.createProperties()));
            Assert.assertEquals("jdbc:h2:nofile", props.getJdbcUrl().get());
            Assert.assertFalse(props.isInitDb());
            Assert.assertFalse(props.isImport());
        } finally {
            System.clearProperty(JDBCConfigPropertiesTest.JDBCURL_SYSPROP);
            System.clearProperty(JDBCConfigPropertiesTest.INITDB_SYSPROP);
            System.clearProperty(JDBCConfigPropertiesTest.IMPORT_SYSPROP);
        }
    }

    @Test
    public void testDataDirPlaceholder() throws Exception {
        JDBCConfigPropertiesFactoryBean factory = new JDBCConfigPropertiesFactoryBean(loader);
        JDBCConfigProperties props = ((JDBCConfigProperties) (factory.createProperties()));
        props.setJdbcUrl("jdbc:h2:file:${GEOSERVER_DATA_DIR}");
        Assert.assertThat(props.getJdbcUrl().get(), CoreMatchers.containsString(loader.getBaseDirectory().getAbsolutePath()));
    }
}

