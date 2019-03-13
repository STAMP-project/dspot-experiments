/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2007 OpenPlans
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wms.capabilities;


import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Unit test suite for {@link GetCapabilitiesResponse}.
 *
 * @author Antonio Cerciello - Geocat
 * @version $Id$
 */
public class GetCapabilitiesSRCOrderTest extends WMSTestSupport {
    /**
     * The xpath.
     */
    private final XpathEngine xpath;

    /**
     * The Constant BASE_URL.
     */
    private static final String BASE_URL = "http://localhost/geoserver";

    /**
     * Instantiates a new gets the capabilities SRC order test.
     */
    public GetCapabilitiesSRCOrderTest() {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("xlink", "http://www.w3.org/1999/xlink");
        namespaces.put("wms", "http://www.opengis.net/wms");
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(namespaces));
        xpath = XMLUnit.newXpathEngine();
    }

    /**
     * Test root layer.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testRootLayer() throws Exception {
        Document dom = findCapabilities(false);
        // print(dom);
        checkWms13ValidationErrors(dom);
        DOMSource domSource = new DOMSource(dom);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        Assert.assertEquals(((writer.toString().indexOf("22222")) < (writer.toString().indexOf("11111"))), true);
    }
}

