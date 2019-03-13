/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.wmts;


import java.io.InputStream;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class WMTSXStreamLoaderTest extends GeoServerSystemTestSupport {
    @Test
    public void testLoadSimpleConfiguration() throws Exception {
        // imitating the necessary xml parser and factories
        XStreamPersisterFactory factory = GeoServerExtensions.bean(XStreamPersisterFactory.class);
        XStreamPersister xp = factory.createXMLPersister();
        WMTSXStreamLoader loader = GeoServerExtensions.bean(WMTSXStreamLoader.class);
        loader.initXStreamPersister(xp, getGeoServer());
        // parsing service information
        try (InputStream is = getClass().getResourceAsStream("/wmts-test.xml")) {
            WMTSInfo serviceInfo = loader.initialize(xp.load(is, WMTSInfo.class));
            Assert.assertThat(serviceInfo.getId(), Matchers.is("WMTS-TEST"));
            Assert.assertThat(serviceInfo.isEnabled(), Matchers.is(false));
            Assert.assertThat(serviceInfo.getName(), Matchers.is("WMTS"));
            Assert.assertThat(serviceInfo.getTitle(), Matchers.is("GeoServer Web Map Tile Service"));
            Assert.assertThat(serviceInfo.getMaintainer(), Matchers.is("geoserver"));
            Assert.assertThat(serviceInfo.getAbstract(), Matchers.is("Testing the WMTS service."));
            Assert.assertThat(serviceInfo.getAccessConstraints(), Matchers.is("SOME"));
            Assert.assertThat(serviceInfo.getFees(), Matchers.is("MONEY"));
            Assert.assertThat(serviceInfo.getOnlineResource(), Matchers.is("http://geoserver.org"));
            Assert.assertThat(serviceInfo.getSchemaBaseURL(), Matchers.is("http://schemas.opengis.net"));
        }
    }
}

