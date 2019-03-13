/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wms.capabilities;


import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.catalog.Catalog;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Test cases for Capabilities' ScaleHint
 *
 * @author Mauricio Pazos
 * @author Niels Charlier
 */
public class GetCapabilitiesScaleDenominatorTest extends WMSTestSupport {
    private final XpathEngine xpath;

    private static final String BASE_URL = "http://localhost/geoserver";

    /**
     * Test layers
     */
    public static final QName REGIONATED = new QName(MockData.SF_URI, "Regionated", MockData.SF_PREFIX);

    public static final QName ACCIDENT = new QName(MockData.SF_URI, "Accident", MockData.SF_PREFIX);

    public static final QName ACCIDENT2 = new QName(MockData.SF_URI, "Accident2", MockData.SF_PREFIX);

    public static final QName ACCIDENT3 = new QName(MockData.SF_URI, "Accident3", MockData.SF_PREFIX);

    private Catalog catalog;

    public GetCapabilitiesScaleDenominatorTest() {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("xlink", "http://www.w3.org/1999/xlink");
        namespaces.put("wms", "http://www.opengis.net/wms");
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(namespaces));
        xpath = XMLUnit.newXpathEngine();
    }

    @Test
    public void testLayerGroups() throws Exception {
        Document dom = findCapabilities(false);
        // print(dom);
        checkWms13ValidationErrors(dom);
        Element layerElement = searchLayerElement("testLayerGroup1", dom);
        NodeList minScaleNode = layerElement.getElementsByTagName("MinScaleDenominator");
        Element minScaleElement = ((Element) (minScaleNode.item(0)));
        NodeList maxScaleNode = layerElement.getElementsByTagName("MaxScaleDenominator");
        Element maxScaleElement = ((Element) (maxScaleNode.item(0)));
        Assert.assertEquals(Double.valueOf(80000000), Double.valueOf(minScaleElement.getTextContent()));
        Assert.assertEquals(Double.valueOf(1000000000), Double.valueOf(maxScaleElement.getTextContent()));
        layerElement = searchLayerElement("testLayerGroup3", dom);
        minScaleNode = layerElement.getElementsByTagName("wms:MinScaleDenominator");
        minScaleElement = ((Element) (minScaleNode.item(0)));
        maxScaleNode = layerElement.getElementsByTagName("wms:MaxScaleDenominator");
        maxScaleElement = ((Element) (minScaleNode.item(0)));
        Assert.assertNull(minScaleElement);
        Assert.assertNull(maxScaleElement);
    }
}

