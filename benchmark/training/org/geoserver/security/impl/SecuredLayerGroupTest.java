/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import java.util.ArrayList;
import org.geoserver.security.decorators.SecuredLayerGroupInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Niels Charlier
 */
public class SecuredLayerGroupTest extends GeoServerSystemTestSupport {
    @Test
    public void testCreateNewLayerGroup() throws Exception {
        // create mocks
        final LayerGroupInfo lg = createNiceMock(LayerGroupInfo.class);
        final CatalogFactory factory = createNiceMock(CatalogFactory.class);
        final Catalog catalog = createNiceMock(Catalog.class);
        expect(catalog.getFactory()).andReturn(factory);
        replay(catalog);
        expect(factory.createLayerGroup()).andReturn(lg);
        replay(factory);
        // tests
        final Catalog secureCatalog = new org.geoserver.security.SecureCatalogImpl(catalog);
        final LayerGroupInfo layerGroup = secureCatalog.getFactory().createLayerGroup();
        Assert.assertTrue((layerGroup instanceof SecuredLayerGroupInfo));
        Assert.assertTrue(((unwrap(LayerGroupInfo.class)) == lg));
    }

    @Test
    public void testGetLayerGroup() throws Exception {
        // create mocks
        final LayerGroupInfo lg = createNiceMock(LayerGroupInfo.class);
        expect(lg.getWorkspace()).andReturn(null);
        final ArrayList<PublishedInfo> layers = new ArrayList<PublishedInfo>();
        expect(lg.getLayers()).andReturn(layers);
        replay(lg);
        final Catalog catalog = createNiceMock(Catalog.class);
        expect(catalog.getLayerGroup("lg")).andReturn(lg);
        replay(catalog);
        // tests
        final Catalog secureCatalog = new org.geoserver.security.SecureCatalogImpl(catalog);
        final LayerGroupInfo layerGroup = secureCatalog.getLayerGroup("lg");
        Assert.assertTrue((layerGroup instanceof SecuredLayerGroupInfo));
        Assert.assertTrue(((unwrap(LayerGroupInfo.class)) == lg));
    }

    @Test
    public void testLayerGroupSynchronised() throws Exception {
        // create mocks
        final LayerInfo layer1 = createNiceMock(LayerInfo.class);
        final LayerInfo layer2 = createNiceMock(LayerInfo.class);
        final LayerGroupInfo lg = createNiceMock(LayerGroupInfo.class);
        final ArrayList<PublishedInfo> layers = new ArrayList<PublishedInfo>();
        expect(lg.getLayers()).andReturn(layers).anyTimes();
        lg.setRootLayer(layer1);
        expectLastCall();
        replay(lg);
        // tests
        final ArrayList<PublishedInfo> securedLayers = new ArrayList<PublishedInfo>();
        final SecuredLayerGroupInfo securedLg = new SecuredLayerGroupInfo(lg, null, securedLayers, new ArrayList());
        securedLg.getLayers().add(new org.geoserver.security.decorators.SecuredLayerInfo(layer1, null));
        securedLg.getLayers().add(new org.geoserver.security.decorators.SecuredLayerInfo(layer2, null));
        Assert.assertEquals(2, securedLg.getLayers().size());
        Assert.assertEquals(2, layers.size());
        Assert.assertTrue(((layers.get(0)) == layer1));
        Assert.assertTrue(((layers.get(1)) == layer2));
        securedLg.getLayers().remove(1);
        Assert.assertEquals(1, securedLg.getLayers().size());
        Assert.assertEquals(1, layers.size());
        securedLg.setRootLayer(new org.geoserver.security.decorators.SecuredLayerInfo(layer2, null));
        // expect is test enough
    }

    @Test
    public void testStyleGroup() throws Exception {
        final LayerGroupInfo lg = createNiceMock(LayerGroupInfo.class);
        expect(lg.getWorkspace()).andReturn(null);
        // Setup null layer with not-null style
        final ArrayList<PublishedInfo> layers = new ArrayList<>();
        layers.add(null);
        final ArrayList<StyleInfo> styles = new ArrayList<>();
        final StyleInfo s = createNiceMock(StyleInfo.class);
        expect(s.getName()).andReturn("styleGroup");
        styles.add(s);
        replay(s);
        expect(lg.getLayers()).andReturn(layers);
        expect(lg.getStyles()).andReturn(styles);
        replay(lg);
        final Catalog catalog = createNiceMock(Catalog.class);
        expect(catalog.getLayerGroup("lg")).andReturn(lg);
        replay(catalog);
        // tests
        final Catalog secureCatalog = new org.geoserver.security.SecureCatalogImpl(catalog);
        final LayerGroupInfo layerGroup = secureCatalog.getLayerGroup("lg");
        Assert.assertTrue((layerGroup instanceof SecuredLayerGroupInfo));
        Assert.assertTrue(((unwrap(LayerGroupInfo.class)) == lg));
        Assert.assertEquals(1, layerGroup.getLayers().size());
        Assert.assertEquals(1, layerGroup.getStyles().size());
    }
}

