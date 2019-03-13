/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import LayerGroupInfo.Mode.CONTAINER;
import LayerGroupInfo.Mode.EO;
import LayerGroupInfo.Mode.NAMED;
import MockData.BRIDGES;
import MockData.BUILDINGS;
import MockData.LOCKS;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class LayerGroupWorkspaceTest extends WMSTestSupport {
    LayerGroupInfo global;

    LayerGroupInfo global2;

    LayerGroupInfo sf;

    LayerGroupInfo cite;

    LayerGroupInfo nested;

    LayerGroupInfo world;

    @Test
    public void testAddLayerGroup() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo lg = createLayerGroup(cat, "base", "base", layer(cat, LOCKS));
        try {
            cat.add(lg);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testGlobalCapabilities() throws Exception {
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        assertXpathExists("//Layer/Name[text() = 'base']", dom);
        assertBounds(global, "base", dom);
        assertXpathExists("//Layer/Name[text() = 'sf:base']", dom);
        assertBounds(sf, "sf:base", dom);
        assertXpathExists("//Layer/Name[text() = 'cite:base']", dom);
        assertBounds(cite, "cite:base", dom);
        String layer = "base";
        assertXpathNotExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:3005']"), dom);
        addSRSAndSetFlag();
        dom = getAsDOM("wms?request=getcapabilities&version=1.1.1", true);
        assertXpathExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:4326']"), dom);
        assertXpathExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:3005']"), dom);
        assertXpathExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:3857']"), dom);
    }

    @Test
    public void testLayerGroupTitleInCapabilities() throws Exception {
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        assertXpathExists("//Layer/Title[text() = 'title for layer group base default']", dom);
        assertXpathExists("//Layer/Title[text() = 'title for layer group sf base']", dom);
        assertXpathExists("//Layer/Title[text() = 'title for layer group cite base']", dom);
    }

    @Test
    public void testLayerGroupAbstractInCapabilities() throws Exception {
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        assertXpathExists("//Layer/Abstract[text() = 'abstract for layer group base default']", dom);
        assertXpathExists("//Layer/Abstract[text() = 'abstract for layer group sf base']", dom);
        assertXpathExists("//Layer/Abstract[text() = 'abstract for layer group cite base']", dom);
    }

    @Test
    public void testSingleLayerGroupInCapabilities() throws Exception {
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        // check layer group is present
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'base']", dom);
        // check it doesn't have children Layers
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'base']/Layer", dom);
        // check its layers are present at the same level
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Lakes']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Forests']", dom);
    }

    @Test
    public void testNamedLayerGroupInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo layerGroup = cat.getLayerGroupByName("base");
        layerGroup.setMode(NAMED);
        cat.save(layerGroup);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        // check layer group is present
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'base']", dom);
        // check its layers are no more present at the same level
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Forests']", dom);
        // check its layers are present as its children
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name[text() = 'base']]/Layer/Name[text() = 'cite:Lakes']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name[text() = 'base']]/Layer/Name[text() = 'cite:Forests']", dom);
    }

    @Test
    public void testContainerLayerGroupInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo layerGroup = cat.getLayerGroupByName("base");
        layerGroup.setMode(CONTAINER);
        cat.save(layerGroup);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        // check layer group doesn't have a name but eventually a title
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'base']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Title[text() = 'title for layer group base default']", dom);
        // check its layers are no more present at the same level
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Forests']", dom);
        // check its layers are present as its children
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Title[text() = 'title for layer group base default']]/Layer/Name[text() = 'cite:Lakes']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Title[text() = 'title for layer group base default']]/Layer/Name[text() = 'cite:Forests']", dom);
    }

    @Test
    public void testEoLayerGroupInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo layerGroup = cat.getLayerGroupByName("base");
        layerGroup.setMode(EO);
        layerGroup.setRootLayer(layer(cat, BUILDINGS));
        layerGroup.setRootLayerStyle(cat.getStyleByName("Buildings"));
        cat.save(layerGroup);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        // check layer group exists
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'base']", dom);
        // check its layers are no more present at the same level
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Forests']", dom);
        // check its layers are present as its children
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name[text() = 'base']]/Layer/Name[text() = 'cite:Lakes']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name[text() = 'base']]/Layer/Name[text() = 'cite:Forests']", dom);
    }

    @Test
    public void testWorkspaceCapabilities() throws Exception {
        Document dom = getAsDOM("sf/wms?request=getcapabilities&version=1.1.1");
        assertXpathExists("//Layer/Name[text() = 'base']", dom);
        assertXpathNotExists("//Layer/Name[text() = 'sf:base']", dom);
        assertBounds(sf, "base", dom);
        String layer = "base";
        assertXpathNotExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:3005']"), dom);
        addSRSAndSetFlag();
        dom = getAsDOM("wms?request=getcapabilities&version=1.1.1", true);
        assertXpathExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:4326']"), dom);
        assertXpathExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:3005']"), dom);
        assertXpathExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:3857']"), dom);
        layer = "world";
        assertXpathExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:3857']"), dom);
    }

    @Test
    public void testGlobalGetMap() throws Exception {
        Document dom = getAsDOM("wms/reflect?layers=base&format=rss");
        assertXpathExists("rss/channel/title[text() = 'cite:Lakes,cite:Forests']", dom);
        dom = getAsDOM("wms/reflect?layers=sf:base&format=rss");
        assertXpathExists("rss/channel/title[text() = 'sf:PrimitiveGeoFeature,sf:AggregateGeoFeature']", dom);
        dom = getAsDOM("wms/reflect?layers=cite:base&format=rss");
        assertXpathExists("rss/channel/title[text() = 'cite:Bridges,cite:Buildings']", dom);
    }

    @Test
    public void testWorkspaceGetMap() throws Exception {
        Document dom = getAsDOM("sf/wms?request=reflect&layers=base&format=rss");
        assertXpathExists("rss/channel/title[text() = 'sf:PrimitiveGeoFeature,sf:AggregateGeoFeature']", dom);
        dom = getAsDOM("cite/wms?request=reflect&layers=base&format=rss");
        assertXpathExists("rss/channel/title[text() = 'cite:Bridges,cite:Buildings']", dom);
        dom = getAsDOM("sf/wms?request=reflect&layers=cite:base&format=rss");
        assertXpathExists("rss/channel/title[text() = 'sf:PrimitiveGeoFeature,sf:AggregateGeoFeature']", dom);
    }

    @Test
    public void testSharedLayersInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo global = cat.getLayerGroupByName("base");
        global.setMode(Mode.NAMED);
        cat.save(global);
        LayerGroupInfo global2 = cat.getLayerGroupByName("base2");
        global2.setMode(Mode.NAMED);
        cat.save(global2);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        // print(dom);
        // check top level layer group exists
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'base']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'base2']", dom);
        // check their layers are no more present at the same level
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Forests']", dom);
        // check its layers are present as their children (in both groups)
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'base']/Layer[Name = 'cite:Lakes']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'base']/Layer[Name = 'cite:Forests']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'base2']/Layer[Name = 'cite:Lakes']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'base2']/Layer[Name = 'cite:Forests']", dom);
    }

    @Test
    public void testNestedSingleInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        nested = createLayerGroup(cat, "nested", "nested", layer(cat, BRIDGES), global);
        nested.setMode(Mode.NAMED);
        cat.add(nested);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        // print(dom);
        // check top level layer group exists
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'nested']", dom);
        // check its layers, and nested layers, and nested groups, are no more present at the same
        // level
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Bridges']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'base']", dom);
        // check its layers are present as its children, as well as the nested group
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'nested']/Layer[Name = 'cite:Bridges']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'nested']/Layer[Name = 'base']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'nested']/Layer[Name = 'base']/Layer[Name = 'cite:Lakes']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'nested']/Layer[Name = 'base']/Layer[Name = 'cite:Forests']", dom);
    }

    @Test
    public void testNestedNamedInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        nested = createLayerGroup(cat, "nested", "nested", layer(cat, BRIDGES), global);
        nested.setMode(Mode.NAMED);
        cat.add(nested);
        LayerGroupInfo global = cat.getLayerGroupByName("base");
        global.setMode(Mode.NAMED);
        cat.save(global);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.1.1");
        // print(dom);
        // check top level layer group exists
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'nested']", dom);
        // check its layers, and nested layers, and nested groups, are no more present at the same
        // level
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Bridges']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'cite:Forests']", dom);
        assertXpathNotExists("/WMT_MS_Capabilities/Capability/Layer/Layer/Name[text() = 'base']", dom);
        // check its layers are present as its children, as well as the nested group
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'nested']/Layer[Name = 'cite:Bridges']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'nested']/Layer[Name = 'base']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'nested']/Layer[Name = 'base']/Layer[Name = 'cite:Lakes']", dom);
        assertXpathExists("/WMT_MS_Capabilities/Capability/Layer/Layer[Name = 'nested']/Layer[Name = 'base']/Layer[Name = 'cite:Forests']", dom);
    }

    @Test
    public void testNestedNamedGetMap() throws Exception {
        Catalog cat = getCatalog();
        nested = createLayerGroup(cat, "nested", "nested", layer(cat, BRIDGES), global);
        nested.setMode(Mode.NAMED);
        cat.add(nested);
        LayerGroupInfo global = cat.getLayerGroupByName("base");
        global.setMode(Mode.NAMED);
        cat.save(global);
        Document dom = getAsDOM("wms?request=reflect&layers=nested&format=rss");
        // print(dom);
        assertXpathExists("rss/channel/title[text() = 'cite:Bridges,cite:Lakes,cite:Forests']", dom);
    }

    @Test
    public void testNestedSharedGetMap() throws Exception {
        Catalog cat = getCatalog();
        nested = createLayerGroup(cat, "nested", "nested", global, global2);
        nested.setMode(Mode.NAMED);
        cat.add(nested);
        Document dom = getAsDOM("wms?request=reflect&layers=nested&format=rss");
        assertXpathExists("rss/channel/title[text() = 'cite:Lakes,cite:Forests,cite:Lakes,cite:Forests']", dom);
    }

    @Test
    public void testNestedSingleGetMap() throws Exception {
        Catalog cat = getCatalog();
        nested = createLayerGroup(cat, "nested", "nested", layer(cat, BRIDGES), global);
        nested.setMode(Mode.NAMED);
        cat.add(nested);
        Document dom = getAsDOM("wms?request=reflect&layers=nested&format=rss");
        // print(dom);
        assertXpathExists("rss/channel/title[text() = 'cite:Bridges,cite:Lakes,cite:Forests']", dom);
    }

    /**
     * Tests Layer group order
     */
    @Test
    public void testGetCapabilitiesGroupOrder() throws Exception {
        Document doc = getAsDOM("/wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        List<String> originalList = layerGroupNameList(doc);
        Assert.assertFalse(originalList.isEmpty());
        List<String> normal = originalList.stream().map(( x) -> removeLayerPrefix(x)).collect(Collectors.toList());
        List<String> ordered = normal.stream().sorted().collect(Collectors.toList());
        Assert.assertTrue(ordered.equals(normal));
    }

    /**
     * Test Layer group order on a workspace virtual service
     */
    @Test
    public void testWorkspaceGetCapabilitiesGroupOrder() throws Exception {
        Document doc = getAsDOM("sf/wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathExists("//Layer/Name[text() = 'base']", doc);
        assertXpathNotExists("//Layer/Name[text() = 'sf:base']", doc);
        assertBounds(sf, "base", doc);
        String layer = "base";
        assertXpathNotExists((("//Layer[Name='" + layer) + "']/BoundingBox[@SRS = 'EPSG:3005']"), doc);
        List<String> originalList = layerGroupNameList(doc);
        Assert.assertFalse(originalList.isEmpty());
        List<String> normal = originalList.stream().map(( x) -> removeLayerPrefix(x)).collect(Collectors.toList());
        List<String> ordered = normal.stream().sorted().collect(Collectors.toList());
        Assert.assertTrue(ordered.equals(normal));
    }
}

