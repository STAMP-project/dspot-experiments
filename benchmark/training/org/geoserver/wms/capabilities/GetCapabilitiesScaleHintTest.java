/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wms.capabilities;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import junit.framework.Assert;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.Catalog;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertNull;


/**
 * Test cases for Capabilities' ScaleHint
 *
 * @author Mauricio Pazos
 * @author Niels Charlier
 */
public class GetCapabilitiesScaleHintTest extends WMSTestSupport {
    private final XpathEngine xpath;

    private static final String BASE_URL = "http://localhost/geoserver";

    private static final Set<String> FORMATS = Collections.singleton("image/png");

    private static final Set<String> LEGEND_FORMAT = Collections.singleton("image/png");

    /**
     * Test layers
     */
    public static final QName REGIONATED = new QName(MockData.SF_URI, "Regionated", MockData.SF_PREFIX);

    public static final QName ACCIDENT = new QName(MockData.SF_URI, "Accident", MockData.SF_PREFIX);

    public static final QName ACCIDENT2 = new QName(MockData.SF_URI, "Accident2", MockData.SF_PREFIX);

    public static final QName ACCIDENT3 = new QName(MockData.SF_URI, "Accident3", MockData.SF_PREFIX);

    private Catalog catalog;

    public GetCapabilitiesScaleHintTest() {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("xlink", "http://www.w3.org/1999/xlink");
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(namespaces));
        xpath = XMLUnit.newXpathEngine();
    }

    @Test
    public void testLayerGroups() throws Exception {
        Document dom = findCapabilities(false);
        // print(dom);
        Element layerElement = searchLayerElement("testLayerGroup1", dom);
        NodeList scaleNode = layerElement.getElementsByTagName("ScaleHint");
        Element scaleElement = ((Element) (scaleNode.item(0)));
        Assert.assertEquals(Double.valueOf(80000000), Double.valueOf(scaleElement.getAttribute("min")));
        Assert.assertEquals(Double.valueOf(1000000000), Double.valueOf(scaleElement.getAttribute("max")));
        layerElement = searchLayerElement("testLayerGroup3", dom);
        scaleNode = layerElement.getElementsByTagName("ScaleHint");
        scaleElement = ((Element) (scaleNode.item(0)));
        assertNull(scaleElement);
    }

    /**
     * Default values for ScaleHint should be set.
     *
     * <pre>
     * The computation of Min and Max values return:
     * 		Min: 0.0
     * 		Max: infinity
     *
     * Capabilities document Expected:
     *
     * 		ScaleHint element shouldn't be generated.
     * </pre>
     */
    @Test
    public void scaleHintDefaultValues() throws Exception {
        Document dom = findCapabilities(false);
        Element layerElement = searchLayerElement(getLayerId(GetCapabilitiesScaleHintTest.ACCIDENT), dom);
        NodeList scaleNode = layerElement.getElementsByTagName("ScaleHint");
        Element scaleElement = ((Element) (scaleNode.item(0)));
        Assert.assertTrue((scaleElement == null));// scale hint is not generated

    }

    /**
     * Default values for ScaleHint should be set.
     *
     * <pre>
     * Check the min and max values return:
     * 		Min: 0.0
     * 		Max: a value
     *
     * Capabilities document Expected:
     *
     *   <ScaleHint min=0 max=value/>
     * </pre>
     */
    @Test
    public void scaleHintDefaultMinValue() throws Exception {
        Document dom = findCapabilities(false);
        Element layerElement = searchLayerElement(getLayerId(GetCapabilitiesScaleHintTest.ACCIDENT2), dom);
        NodeList scaleNode = layerElement.getElementsByTagName("ScaleHint");
        Element scaleElement = ((Element) (scaleNode.item(0)));
        Assert.assertEquals(0.0, Double.valueOf(scaleElement.getAttribute("min")));
        Assert.assertEquals(Double.valueOf(640000000), Double.valueOf(scaleElement.getAttribute("max")));
    }

    /**
     * Default values for ScaleHint should be set.
     *
     * <pre>
     * The computation of Min and Max values when the option
     * 'Scalehint in units per diagonal pixel' is set. Return:
     * 		Min: 0.0
     * 		Max: a value
     *
     * Capabilities document Expected:
     *
     *   <ScaleHint min=0 max=value/>
     * </pre>
     */
    @Test
    public void scaleHintUnitsPerPixelDefaultMinValue() throws Exception {
        Document dom = findCapabilities(true);
        Element layerElement = searchLayerElement(getLayerId(GetCapabilitiesScaleHintTest.ACCIDENT2), dom);
        NodeList scaleNode = layerElement.getElementsByTagName("ScaleHint");
        Element scaleElement = ((Element) (scaleNode.item(0)));
        Assert.assertEquals(0.0, Double.valueOf(scaleElement.getAttribute("min")));
        Assert.assertEquals(Double.valueOf(253427.07037725858), Double.valueOf(scaleElement.getAttribute("max")));
    }

    /**
     * Default values for ScaleHint should be set.
     *
     * <pre>
     * Check the Min and Max values return:
     * 		Min: a value
     * 		Max: Infinity
     *
     * Capabilities document Expected:
     *
     *   <ScaleHint min=value max=infinity/>
     * </pre>
     */
    @Test
    public void scaleHintDefaultMaxValue() throws Exception {
        Document dom = findCapabilities(false);
        Element layerElement = searchLayerElement(getLayerId(GetCapabilitiesScaleHintTest.ACCIDENT3), dom);
        NodeList scaleNode = layerElement.getElementsByTagName("ScaleHint");
        Element scaleElement = ((Element) (scaleNode.item(0)));
        Assert.assertEquals(Double.valueOf(320000000), Double.valueOf(scaleElement.getAttribute("min")));
        Assert.assertEquals(Double.POSITIVE_INFINITY, Double.valueOf(scaleElement.getAttribute("max")));
    }

    /**
     * Default values for ScaleHint should be set.
     *
     * <pre>
     * The computation of Min and Max values when the option
     * 'Scalehint in units per diagonal pixel' is set. Return:
     * 		Min: a value
     * 		Max: Infinity
     *
     * Capabilities document Expected:
     *
     *   <ScaleHint min=value max=infinity/>
     * </pre>
     */
    @Test
    public void scaleHintUnitsPerPixelDefaultMaxValue() throws Exception {
        Document dom = findCapabilities(true);
        Element layerElement = searchLayerElement(getLayerId(GetCapabilitiesScaleHintTest.ACCIDENT3), dom);
        NodeList scaleNode = layerElement.getElementsByTagName("ScaleHint");
        Element scaleElement = ((Element) (scaleNode.item(0)));
        Assert.assertEquals(Double.valueOf(126713.53518862929), Double.valueOf(scaleElement.getAttribute("min")));
        Assert.assertEquals(Double.POSITIVE_INFINITY, Double.valueOf(scaleElement.getAttribute("max")));
    }

    /**
     * <pre>
     * Max is the maximum value found in the set of rules
     * Min is the minimum value found in the set of rules
     * </pre>
     */
    @Test
    public void scaleHintFoundMaxMinDenominators() throws Exception {
        Document dom = findCapabilities(false);
        final String layerName = getLayerId(GetCapabilitiesScaleHintTest.REGIONATED);
        Element layerElement = searchLayerElement(layerName, dom);
        NodeList scaleNode = layerElement.getElementsByTagName("ScaleHint");
        Element scaleElement = ((Element) (scaleNode.item(0)));
        Assert.assertEquals(Double.valueOf(80000000), Double.valueOf(scaleElement.getAttribute("min")));
        Assert.assertEquals(Double.valueOf(640000000), Double.valueOf(scaleElement.getAttribute("max")));
    }

    /**
     * <pre>
     * Max is the maximum value found in the set of rules
     * Min is the minimum value found in the set of rules
     * Both values are computed as units per diagonal pixel
     * </pre>
     */
    @Test
    public void scaleHintUnitsPerPixelFoundMaxMinDenominators() throws Exception {
        Document dom = findCapabilities(true);
        final String layerName = getLayerId(GetCapabilitiesScaleHintTest.REGIONATED);
        Element layerElement = searchLayerElement(layerName, dom);
        NodeList scaleNode = layerElement.getElementsByTagName("ScaleHint");
        Element scaleElement = ((Element) (scaleNode.item(0)));
        Assert.assertEquals(Double.valueOf(31678.383797157323), Double.valueOf(scaleElement.getAttribute("min")));
        Assert.assertEquals(Double.valueOf(253427.07037725858), Double.valueOf(scaleElement.getAttribute("max")));
    }
}

