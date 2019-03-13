/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_3;


import LayerGroupInfo.Mode.CONTAINER;
import LayerGroupInfo.Mode.EO;
import LayerGroupInfo.Mode.NAMED;
import MockData.BRIDGES;
import MockData.BUILDINGS;
import MockData.LOCKS;
import junit.framework.Assert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.wms.WMSTestSupport;
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
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0");
        assertXpathExists("//wms:Layer/wms:Name[text() = 'base']", dom);
        assertBounds(global, "base", dom);
        assertXpathExists("//wms:Layer/wms:Name[text() = 'sf:base']", dom);
        assertBounds(sf, "sf:base", dom);
        assertXpathExists("//wms:Layer/wms:Name[text() = 'cite:base']", dom);
        assertBounds(cite, "cite:base", dom);
        String layer = "base";
        assertXpathNotExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3005']"), dom);
        addSRSAndSetFlag();
        dom = getAsDOM("wms?request=getcapabilities&version=1.3.0", true);
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:4326']"), dom);
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3005']"), dom);
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3857']"), dom);
    }

    @Test
    public void testLayerGroupTitleInCapabilities() throws Exception {
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0");
        assertXpathExists("//wms:Layer/wms:Title[text() = 'title for layer group base default']", dom);
        assertXpathExists("//wms:Layer/wms:Title[text() = 'title for layer group sf base']", dom);
        assertXpathExists("//wms:Layer/wms:Title[text() = 'title for layer group cite base']", dom);
    }

    @Test
    public void testLayerGroupAbstractInCapabilities() throws Exception {
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0", false);
        assertXpathExists("//wms:Layer/wms:Abstract[text() = 'abstract for layer group base default']", dom);
        assertXpathExists("//wms:Layer/wms:Abstract[text() = 'abstract for layer group sf base']", dom);
        assertXpathExists("//wms:Layer/wms:Abstract[text() = 'abstract for layer group cite base']", dom);
    }

    @Test
    public void testSingleLayerGroupInCapabilities() throws Exception {
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0");
        // check layer group is present
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'base']", dom);
        // check it doesn't have children Layers
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[Name = 'base']/wms:Layer", dom);
        // check its layers are present at the same level
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Lakes']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Forests']", dom);
    }

    @Test
    public void testNamedLayerGroupInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo layerGroup = cat.getLayerGroupByName("base");
        layerGroup.setMode(NAMED);
        cat.save(layerGroup);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0");
        // check layer group is present
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'base']", dom);
        // check its layers are no more present at the same level
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Forests']", dom);
        // check its layers are present as its children
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name[text() = 'base']]/wms:Layer/wms:Name[text() = 'cite:Lakes']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name[text() = 'base']]/wms:Layer/wms:Name[text() = 'cite:Forests']", dom);
    }

    @Test
    public void testContainerLayerGroupInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo layerGroup = cat.getLayerGroupByName("base");
        layerGroup.setMode(CONTAINER);
        cat.save(layerGroup);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0");
        // check layer group doesn't have a name but eventually a title
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'base']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Title[text() = 'title for layer group base default']", dom);
        // check its layers are no more present at the same level
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Forests']", dom);
        // check its layers are present as its children
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Title[text() = 'title for layer group base default']]/wms:Layer/wms:Name[text() = 'cite:Lakes']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Title[text() = 'title for layer group base default']]/wms:Layer/wms:Name[text() = 'cite:Forests']", dom);
    }

    @Test
    public void testEoLayerGroupInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo layerGroup = cat.getLayerGroupByName("base");
        layerGroup.setMode(EO);
        layerGroup.setRootLayer(layer(cat, BUILDINGS));
        layerGroup.setRootLayerStyle(cat.getStyleByName("Buildings"));
        cat.save(layerGroup);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0");
        // check layer group exists
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'base']", dom);
        // check its layers are no more present at the same level
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Forests']", dom);
        // check its layers are present as its children
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name[text() = 'base']]/wms:Layer/wms:Name[text() = 'cite:Lakes']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name[text() = 'base']]/wms:Layer/wms:Name[text() = 'cite:Forests']", dom);
    }

    @Test
    public void testWorkspaceCapabilities() throws Exception {
        Document dom = getAsDOM("sf/wms?request=getcapabilities&version=1.3.0");
        assertXpathExists("//wms:Layer/wms:Name[text() = 'base']", dom);
        assertXpathNotExists("//wms:Layer/wms:Name[text() = 'sf:base']", dom);
        assertBounds(sf, "base", dom);
        String layer = "base";
        assertXpathNotExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3005']"), dom);
        addSRSAndSetFlag();
        dom = getAsDOM("wms?request=getcapabilities&version=1.3.0", true);
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:4326']"), dom);
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3005']"), dom);
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3857']"), dom);
        layer = "world";
        assertXpathExists((("//wms:Layer[wms:Name='" + layer) + "']/wms:BoundingBox[@CRS = 'EPSG:3857']"), dom);
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
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0");
        // print(dom);
        // check top level layer group exists
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'base']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'base2']", dom);
        // check their layers are no more present at the same level
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Forests']", dom);
        // check its layers are present as their children (in both groups)
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'base']/wms:Layer[wms:Name = 'cite:Lakes']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'base']/wms:Layer[wms:Name = 'cite:Forests']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'base2']/wms:Layer[wms:Name = 'cite:Lakes']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'base2']/wms:Layer[wms:Name = 'cite:Forests']", dom);
    }

    @Test
    public void testNestedSingleInCapabilities() throws Exception {
        Catalog cat = getCatalog();
        nested = createLayerGroup(cat, "nested", "nested", layer(cat, BRIDGES), global);
        nested.setMode(Mode.NAMED);
        cat.add(nested);
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0");
        // print(dom);
        // check top level layer group exists
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'nested']", dom);
        // check its layers, and nested layers, and nested groups, are no more present at the same
        // level
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Bridges']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'base']", dom);
        // check its layers are present as its children, as well as the nested group
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'nested']/wms:Layer[wms:Name = 'cite:Bridges']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'nested']/wms:Layer[wms:Name = 'base']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'nested']/wms:Layer[wms:Name = 'base']/wms:Layer[wms:Name = 'cite:Lakes']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'nested']/wms:Layer[wms:Name = 'base']/wms:Layer[wms:Name = 'cite:Forests']", dom);
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
        Document dom = getAsDOM("wms?request=getcapabilities&version=1.3.0");
        // print(dom);
        // check top level layer group exists
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'nested']", dom);
        // check its layers, and nested layers, and nested groups, are no more present at the same
        // level
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Bridges']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Lakes']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'cite:Forests']", dom);
        assertXpathNotExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer/wms:Name[text() = 'base']", dom);
        // check its layers are present as its children, as well as the nested group
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'nested']/wms:Layer[wms:Name = 'cite:Bridges']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'nested']/wms:Layer[wms:Name = 'base']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'nested']/wms:Layer[wms:Name = 'base']/wms:Layer[wms:Name = 'cite:Lakes']", dom);
        assertXpathExists("/wms:WMS_Capabilities/wms:Capability/wms:Layer/wms:Layer[wms:Name = 'nested']/wms:Layer[wms:Name = 'base']/wms:Layer[wms:Name = 'cite:Forests']", dom);
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
}

