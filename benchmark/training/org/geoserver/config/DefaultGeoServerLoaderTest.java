/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config;


import StyleInfo.DEFAULT_POLYGON;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.config.impl.ServiceInfoImpl;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.config.util.XStreamServiceLoader;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.junit.Assert;
import org.junit.Test;


public class DefaultGeoServerLoaderTest {
    DefaultGeoServerLoader loader;

    Catalog catalog;

    XStreamPersister xp;

    boolean helloServiceSaved = false;

    static interface HelloServiceInfo extends ServiceInfo {}

    static final class HelloServiceInfoImpl extends ServiceInfoImpl implements DefaultGeoServerLoaderTest.HelloServiceInfo {}

    static final class HelloServiceXStreamLoader extends XStreamServiceLoader<DefaultGeoServerLoaderTest.HelloServiceInfo> {
        public HelloServiceXStreamLoader(GeoServerResourceLoader resourceLoader, String filenameBase) {
            super(resourceLoader, filenameBase);
        }

        @Override
        public Class<DefaultGeoServerLoaderTest.HelloServiceInfo> getServiceClass() {
            return DefaultGeoServerLoaderTest.HelloServiceInfo.class;
        }

        @Override
        protected DefaultGeoServerLoaderTest.HelloServiceInfo createServiceFromScratch(GeoServer gs) {
            return new DefaultGeoServerLoaderTest.HelloServiceInfoImpl();
        }
    }

    @Test
    public void testGeneratedStyles() throws Exception {
        XStreamPersisterFactory xpf = new XStreamPersisterFactory();
        XStreamPersister xp = xpf.createXMLPersister();
        xp.setCatalog(catalog);
        loader.initializeStyles(catalog, xp);
        StyleInfo polygon = catalog.getStyleByName(DEFAULT_POLYGON);
        Assert.assertEquals("default_polygon.sld", polygon.getFilename());
    }

    @Test
    public void testLoadNestedLayerGroups() throws Exception {
        GeoServerResourceLoader resources = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        Assert.assertSame(catalog.getResourceLoader(), resources);
        loader.readCatalog(catalog, xp);
        LayerGroupInfo simpleLayerGroup = catalog.getLayerGroupByName("topp", "simplegroup");
        Assert.assertNotNull(simpleLayerGroup);
        Assert.assertEquals(101, simpleLayerGroup.getAttribution().getLogoWidth());
        Assert.assertEquals(102, simpleLayerGroup.getAttribution().getLogoHeight());
        Assert.assertEquals(2, simpleLayerGroup.getMetadataLinks().size());
        Assert.assertEquals("http://my/metadata/link/1", simpleLayerGroup.getMetadataLinks().get(0).getContent());
        Assert.assertEquals("text/html", simpleLayerGroup.getMetadataLinks().get(0).getType());
        LayerGroupInfo nestedLayerGroup = catalog.getLayerGroupByName("topp", "nestedgroup");
        Assert.assertNotNull(nestedLayerGroup);
        Assert.assertNotNull(nestedLayerGroup.getLayers());
        Assert.assertEquals(2, nestedLayerGroup.getLayers().size());
        Assert.assertTrue(((nestedLayerGroup.getLayers().get(0)) instanceof LayerGroupInfo));
        Assert.assertNotNull(getLayers());
        Assert.assertTrue(((nestedLayerGroup.getLayers().get(1)) instanceof LayerInfo));
    }

    @Test
    public void testLoadWithoutResaving() throws Exception {
        GeoServerImpl gs = new GeoServerImpl();
        gs.setCatalog(catalog);
        // this one already calls onto loadService
        loader.postProcessBeforeInitialization(gs, "geoServer");
        // for extra measure, also do a reload
        loader.reload();
        Assert.assertFalse("hello.xml should not have been saved during load", helloServiceSaved);
    }
}

