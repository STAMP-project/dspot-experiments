/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import WFSInfo.Version.V_10;
import WFSInfo.Version.V_11;
import WFSInfo.Version.V_20;
import java.io.InputStream;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.platform.GeoServerExtensions;
import org.junit.Assert;
import org.junit.Test;


public class WFSXStreamLoaderTest extends WFSTestSupport {
    @Test
    public void testGmlCreateFromScratch() throws Exception {
        WFSXStreamLoader loader = GeoServerExtensions.bean(WFSXStreamLoader.class);
        WFSInfo wfs = loader.createServiceFromScratch(null);
        Assert.assertNotNull(wfs);
        Assert.assertTrue(wfs.getGML().containsKey(V_10));
        Assert.assertTrue(wfs.getGML().containsKey(V_11));
        Assert.assertTrue(wfs.getGML().containsKey(V_20));
    }

    @Test
    public void testLoadVersion() throws Exception {
        XStreamPersisterFactory factory = GeoServerExtensions.bean(XStreamPersisterFactory.class);
        XStreamPersister xp = factory.createXMLPersister();
        WFSXStreamLoader loader = GeoServerExtensions.bean(WFSXStreamLoader.class);
        loader.initXStreamPersister(xp, getGeoServer());
        try (InputStream is = getClass().getResourceAsStream("wfs-test.xml")) {
            xp.load(is, WFSInfo.class);
        }
    }

    @Test
    public void testLoadMinimalConfig() throws Exception {
        XStreamPersisterFactory factory = GeoServerExtensions.bean(XStreamPersisterFactory.class);
        XStreamPersister xp = factory.createXMLPersister();
        WFSXStreamLoader loader = GeoServerExtensions.bean(WFSXStreamLoader.class);
        loader.initXStreamPersister(xp, getGeoServer());
        try (InputStream is = getClass().getResourceAsStream("wfs-minimal.xml")) {
            xp.load(is, WFSInfo.class);
        }
    }
}

