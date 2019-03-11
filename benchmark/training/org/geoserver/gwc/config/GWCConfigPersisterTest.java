/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.config;


import GWCConfigPersister.GWC_CONFIG_FILE;
import Paths.BASE;
import java.io.File;
import junit.framework.Assert;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static GWCConfigPersister.GWC_CONFIG_FILE;


public class GWCConfigPersisterTest {
    private GeoServerResourceLoader resourceLoader;

    private GWCConfigPersister persister;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testPrecondition() throws Exception {
        // gwc-gs.xml shall exists, it's GWCInitializer responsibility
        Mockito.when(resourceLoader.find(ArgumentMatchers.eq(GWC_CONFIG_FILE))).thenReturn(null);
        try {
            persister.getConfig();
            Assert.fail("Expected assertion error");
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains(GWC_CONFIG_FILE));
        }
    }

    @Test
    public void testSaveLoad() throws Exception {
        final File baseDirectory = new File("target");
        baseDirectory.mkdirs();
        final File configFile = new File(baseDirectory, GWC_CONFIG_FILE);
        if (configFile.exists()) {
            Assert.assertTrue(configFile.delete());
        }
        Mockito.when(resourceLoader.get(BASE)).thenReturn(Files.asResource(baseDirectory));
        Mockito.when(resourceLoader.get(ArgumentMatchers.eq(GWC_CONFIG_FILE))).thenReturn(Files.asResource(configFile));
        GWCConfig config = GWCConfig.getOldDefaults();
        config.setCacheNonDefaultStyles(true);
        config.setDirectWMSIntegrationEnabled(true);
        persister.save(config);
        Assert.assertSame(config, persister.getConfig());
        persister = new GWCConfigPersister(new XStreamPersisterFactory(), resourceLoader);
        Assert.assertEquals(config, persister.getConfig());
        // provoque a IOException
        Mockito.when(resourceLoader.get(ArgumentMatchers.eq(GWC_CONFIG_FILE))).thenReturn(Files.asResource(tempFolder.newFile("shall_not_exist")));
        persister = new GWCConfigPersister(new XStreamPersisterFactory(), resourceLoader);
        GWCConfig expected = new GWCConfig();
        GWCConfig actual = persister.getConfig();
        Assert.assertEquals(expected, actual);
    }
}

