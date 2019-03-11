/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.store.internal;


import OWS.NAMESPACE;
import java.util.HashMap;
import java.util.Map;
import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.csw.CSWTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class GetCapabilitiesTest extends CSWInternalTestSupport {
    static XpathEngine xpath = XMLUnit.newXpathEngine();

    static {
        Map<String, String> prefixMap = new HashMap<String, String>();
        prefixMap.put("ows", NAMESPACE);
        prefixMap.put("ogc", OGC.NAMESPACE);
        prefixMap.put("gml", "http://www.opengis.net/gml");
        prefixMap.put("gmd", "http://www.isotc211.org/2005/gmd");
        prefixMap.put("xlink", XLINK.NAMESPACE);
        NamespaceContext nameSpaceContext = new SimpleNamespaceContext(prefixMap);
        GetCapabilitiesTest.xpath.setNamespaceContext(nameSpaceContext);
    }

    @Test
    public void testGetBasic() throws Exception {
        Document dom = getAsDOM(((CSWTestSupport.BASEPATH) + "?service=csw&version=2.0.2&request=GetCapabilities"));
        // print(dom);
        checkValidationErrors(dom);
        // basic check on local name
        Element e = dom.getDocumentElement();
        Assert.assertEquals("Capabilities", e.getLocalName());
        // basic check on xpath node
        assertXpathEvaluatesTo("1", "count(/csw:Capabilities)", dom);
        Assert.assertTrue(((GetCapabilitiesTest.xpath.getMatchingNodes("//ows:OperationsMetadata/ows:Operation", dom).getLength()) > 0));
        Assert.assertEquals("5", GetCapabilitiesTest.xpath.evaluate("count(//ows:Operation)", dom));
        // basic check on GetCapabilities operation constraint
        Assert.assertEquals("XML", GetCapabilitiesTest.xpath.evaluate("//ows:OperationsMetadata/ows:Operation[@name=\"GetCapabilities\"]/ows:Constraint/ows:Value", dom));
        // check we have csw:AnyText among the queriables
        assertXpathEvaluatesTo("1", "count(//ows:Operation[@name='GetRecords']/ows:Constraint[@name='SupportedDublinCoreQueryables' and ows:Value = 'csw:AnyText'])", dom);
        // check we have BoundingBox among the queriables
        assertXpathEvaluatesTo("1", "count(//ows:Operation[@name='GetRecords']/ows:Constraint[@name='SupportedISOQueryables' and ows:Value = 'BoundingBox'])", dom);
        // check we have dc:subject among the domain property names
        assertXpathEvaluatesTo("1", "count(//ows:Operation[@name='GetDomain']/ows:Parameter[@name='PropertyName' and ows:Value = 'dc:title'])", dom);
        // check we have Abstract among the domain property names
        assertXpathEvaluatesTo("1", "count(//ows:Operation[@name='GetDomain']/ows:Parameter[@name='PropertyName' and ows:Value = 'Abstract'])", dom);
    }

    @Test
    public void testPostBasic() throws Exception {
        Document dom = postAsDOM(((CSWTestSupport.BASEPATH) + "?service=csw&version=2.0.2&request=GetCapabilities"));
        // print(dom);
        checkValidationErrors(dom);
        // basic check on local name
        Element e = dom.getDocumentElement();
        Assert.assertEquals("Capabilities", e.getLocalName());
        // basic check on xpath node
        assertXpathEvaluatesTo("1", "count(/csw:Capabilities)", dom);
        Assert.assertTrue(((GetCapabilitiesTest.xpath.getMatchingNodes("//ows:OperationsMetadata/ows:Operation", dom).getLength()) > 0));
        Assert.assertEquals("5", GetCapabilitiesTest.xpath.evaluate("count(//ows:Operation)", dom));
        // basic check on GetCapabilities operation constraint
        Assert.assertEquals("XML", GetCapabilitiesTest.xpath.evaluate("//ows:OperationsMetadata/ows:Operation[@name=\"GetCapabilities\"]/ows:Constraint/ows:Value", dom));
    }

    @Test
    public void testSections() throws Exception {
        Document dom = getAsDOM(((CSWTestSupport.BASEPATH) + "?service=csw&version=2.0.2&request=GetCapabilities&sections=ServiceIdentification,ServiceProvider"));
        // print(dom);
        checkValidationErrors(dom);
        // basic check on local name
        Element e = dom.getDocumentElement();
        Assert.assertEquals("Capabilities", e.getLocalName());
        // basic check on xpath node
        assertXpathEvaluatesTo("1", "count(/csw:Capabilities)", dom);
        Assert.assertEquals("1", GetCapabilitiesTest.xpath.evaluate("count(//ows:ServiceIdentification)", dom));
        Assert.assertEquals("1", GetCapabilitiesTest.xpath.evaluate("count(//ows:ServiceProvider)", dom));
        Assert.assertEquals("0", GetCapabilitiesTest.xpath.evaluate("count(//ows:OperationsMetadata)", dom));
        // this one is mandatory, cannot be skipped
        Assert.assertEquals("1", GetCapabilitiesTest.xpath.evaluate("count(//ogc:Filter_Capabilities)", dom));
        Assert.assertTrue(((GetCapabilitiesTest.xpath.getMatchingNodes("//ows:OperationsMetadata/ows:Operation", dom).getLength()) == 0));
        Assert.assertEquals("0", GetCapabilitiesTest.xpath.evaluate("count(//ows:Operation)", dom));
    }
}

