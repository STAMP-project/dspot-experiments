/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;


import MockData.PRIMITIVEGEOFEATURE;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class LayerWorkspaceTest extends WMSTestSupport {
    private Catalog catalog;

    /**
     * Test layer names order from GetCapabilities
     */
    @Test
    public void testLayerOrderGetCapabilities() throws Exception {
        Document doc = getAsDOM("/wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        List<String> originalList = layerNameList(doc);
        Assert.assertFalse(originalList.isEmpty());
        List<String> names = originalList.stream().map(( x) -> removeLayerPrefix(x)).collect(Collectors.toList());
        List<String> orderedNames = names.stream().sorted().collect(Collectors.toList());
        Assert.assertTrue(orderedNames.equals(names));
    }

    /**
     * Test layer names order from GetCapabilities on workspace
     */
    @Test
    public void testWorkspaceLayerOrderGetCapabilities() throws Exception {
        Document doc = getAsDOM("/cite/wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        List<String> originalList = layerNameList(doc);
        Assert.assertFalse(originalList.isEmpty());
        Assert.assertTrue(originalList.stream().noneMatch(( x) -> (x.indexOf(":")) > (-1)));
        List<String> orderedNames = originalList.stream().sorted().collect(Collectors.toList());
        Assert.assertTrue(orderedNames.equals(originalList));
    }

    @Test
    public void testGlobalCapabilities() throws Exception {
        LayerInfo layer = layer(catalog, PRIMITIVEGEOFEATURE);
        Document doc = getAsDOM("/wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathExists((("//Layer[Name='" + (layer.prefixedName())) + "']"), doc);
    }

    @Test
    public void testGlobalDescribeLayer() throws Exception {
        LayerInfo layer = layer(catalog, PRIMITIVEGEOFEATURE);
        Document doc = getAsDOM(("/wms?service=WMS&request=describeLayer&version=1.1.1&LAYERS=" + (layer.getName())), true);
        assertXpathExists((("//LayerDescription[@name='" + (layer.prefixedName())) + "']"), doc);
    }

    @Test
    public void testWorkspaceCapabilities() throws Exception {
        Document doc = getAsDOM("/sf/wms?service=WMS&request=getCapabilities&version=1.1.1", true);
        assertXpathExists((("//Layer[Name='" + (PRIMITIVEGEOFEATURE.getLocalPart())) + "']"), doc);
    }

    @Test
    public void testWorkspaceDescribeLayer() throws Exception {
        Document doc = getAsDOM(("/sf/wms?service=WMS&request=describeLayer&version=1.1.1&LAYERS=" + (PRIMITIVEGEOFEATURE.getLocalPart())), true);
        assertXpathExists((("//LayerDescription[@name='" + (PRIMITIVEGEOFEATURE.getLocalPart())) + "']"), doc);
    }
}

