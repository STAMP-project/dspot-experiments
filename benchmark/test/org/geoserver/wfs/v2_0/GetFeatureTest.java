/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v2_0;


import Dispatcher.SOAP_11_NS;
import Dispatcher.SOAP_12_NS;
import MockData.FIFTEEN;
import MockData.NAMED_PLACES;
import MockData.SF_URI;
import StoredQuery.DEFAULT;
import WFSInfo.Version.V_11;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.util.KvpUtils;
import org.geoserver.wfs.GMLInfo;
import org.geoserver.wfs.WFSInfo;
import org.geotools.filter.v2_0.FES;
import org.geotools.gml3.v3_2.GML;
import org.geotools.wfs.v2_0.WFS;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class GetFeatureTest extends WFS20TestSupport {
    @Test
    public void testSkipNumberMatched() throws Exception {
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName("Fifteen");
        fti.setSkipNumberMatched(true);
        getCatalog().save(fti);
        Assert.assertEquals(true, fti.getSkipNumberMatched());
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs");
        Assert.assertEquals("unknown", dom.getDocumentElement().getAttribute("numberMatched"));
        Assert.assertEquals("15", dom.getDocumentElement().getAttribute("numberReturned"));
        XMLAssert.assertXpathEvaluatesTo("15", "count(//cdf:Fifteen)", dom);
        dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs&resultType=hits");
        Assert.assertEquals("15", dom.getDocumentElement().getAttribute("numberMatched"));
        Assert.assertEquals("0", dom.getDocumentElement().getAttribute("numberReturned"));
        XMLAssert.assertXpathEvaluatesTo("0", "count(//cdf:Fifteen)", dom);
        fti.setSkipNumberMatched(false);
        getCatalog().save(fti);
    }

    @Test
    public void testGet() throws Exception {
        testGetFifteenAll("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs");
        testGetFifteenAll("wfs?request=GetFeature&typenames=(cdf:Fifteen)&version=2.0.0&service=wfs");
    }

    @Test
    public void testAlternatePrefix() throws Exception {
        testGetFifteenAll((("wfs?request=GetFeature&typenames=abc:Fifteen&version=2.0.0&service=wfs&namespaces=xmlns(abc," + (MockData.CDF_URI)) + ")"));
        testGetFifteenAll((((("wfs?request=GetFeature&typenames=abc:Fifteen&version=2.0.0&service=wfs&namespaces=xmlns(abc," + (MockData.CDF_URI)) + "),xmlns(wfs,") + (WFS.NAMESPACE)) + ")"));
        testGetFifteenAll((((("wfs?request=GetFeature&typenames=Fifteen&version=2.0.0&service=wfs&namespaces=xmlns(" + (MockData.CDF_URI)) + "),xmlns(wfs,") + (WFS.NAMESPACE)) + ")"));
    }

    @Test
    public void testConcurrentGet() throws Exception {
        ExecutorCompletionService<Object> es = new ExecutorCompletionService<>(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        final int REQUESTS = 200;
        for (int i = 0; i < REQUESTS; i++) {
            es.submit(() -> {
                testGetFifteenAll("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs");
                return null;
            });
        }
        // just check there are no exceptions
        for (int i = 0; i < REQUESTS; i++) {
            es.take().get();
        }
    }

    @Test
    public void testConcurrentPost() throws Exception {
        ExecutorCompletionService<Object> es = new ExecutorCompletionService<>(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        final int REQUESTS = 200;
        for (int i = 0; i < REQUESTS; i++) {
            es.submit(() -> {
                testPost();
                return null;
            });
        }
        // just check there are no exceptions
        long start = System.currentTimeMillis();
        for (int i = 0; i < REQUESTS; i++) {
            es.take().get();
            if ((i % 100) == 0) {
                long curr = System.currentTimeMillis();
                LOGGER.info(((i + " - ") + (curr - start)));
                start = curr;
            }
        }
    }

    @Test
    public void testGetTypeNames() throws Exception {
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=(cdf:Fifteen)(cdf:Seven)&version=2.0.0&service=wfs");
        XMLAssert.assertXpathEvaluatesTo("15", "count(//cdf:Fifteen)", dom);
        XMLAssert.assertXpathEvaluatesTo("7", "count(//cdf:Seven)", dom);
    }

    @Test
    public void testGetTypeName() throws Exception {
        testGetFifteenAll("wfs?request=GetFeature&typename=cdf:Fifteen&version=2.0.0&service=wfs");
    }

    @Test
    public void testGetWithCount() throws Exception {
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs&count=5");
        XMLAssert.assertXpathEvaluatesTo("5", "count(//cdf:Fifteen)", dom);
        Assert.assertEquals("5", dom.getDocumentElement().getAttribute("numberReturned"));
        Assert.assertEquals("15", dom.getDocumentElement().getAttribute("numberMatched"));
    }

    @Test
    public void testGetWithCountAndStartIndex0() throws Exception {
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs&count=5&startIndex=0");
        XMLAssert.assertXpathEvaluatesTo("5", "count(//cdf:Fifteen)", dom);
        Assert.assertEquals("5", dom.getDocumentElement().getAttribute("numberReturned"));
        Assert.assertEquals("15", dom.getDocumentElement().getAttribute("numberMatched"));
    }

    @Test
    public void testGetWithCountAndStartIndexMiddle() throws Exception {
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs&count=5&startIndex=7");
        XMLAssert.assertXpathEvaluatesTo("5", "count(//cdf:Fifteen)", dom);
        Assert.assertEquals("5", dom.getDocumentElement().getAttribute("numberReturned"));
        Assert.assertEquals("15", dom.getDocumentElement().getAttribute("numberMatched"));
    }

    @Test
    public void testGetWithCountAndStartIndexEnd() throws Exception {
        Document dom = getAsDOM("wfs?request=GetFeature&typenames=cdf:Fifteen&version=2.0.0&service=wfs&count=5&startIndex=11");
        XMLAssert.assertXpathEvaluatesTo("4", "count(//cdf:Fifteen)", dom);
        Assert.assertEquals("4", dom.getDocumentElement().getAttribute("numberReturned"));
        Assert.assertEquals("15", dom.getDocumentElement().getAttribute("numberMatched"));
    }

    @Test
    public void testGetPropertyNameEmpty() throws Exception {
        testGetFifteenAll("wfs?request=GetFeature&typename=cdf:Fifteen&version=2.0.0&service=wfs&propertyname=");
    }

    @Test
    public void testGetPropertyNameStar() throws Exception {
        testGetFifteenAll("wfs?request=GetFeature&typename=cdf:Fifteen&version=2.0.0&service=wfs&propertyname=*");
    }

    // see GEOS-1287
    @Test
    public void testGetWithFeatureId() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typeName=cdf:Fifteen&version=2.0.0&service=wfs&featureid=Fifteen.2");
        assertGML32(doc);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection/wfs:member/cdf:Fifteen)", doc);
        XMLAssert.assertXpathEvaluatesTo("Fifteen.2", "//wfs:FeatureCollection/wfs:member/cdf:Fifteen/@gml:id", doc);
        doc = getAsDOM("wfs?request=GetFeature&typeName=cite:NamedPlaces&version=2.0.0&service=wfs&featureId=NamedPlaces.1107531895891");
        // super.print(doc);
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection/wfs:member/cite:NamedPlaces)", doc);
        XMLAssert.assertXpathEvaluatesTo("NamedPlaces.1107531895891", "//wfs:FeatureCollection/wfs:member/cite:NamedPlaces/@gml:id", doc);
    }

    @Test
    public void testGetWithResourceId() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typeNames=cdf:Fifteen&version=2.0.0&service=wfs&resourceid=Fifteen.2");
        assertGML32(doc);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection/wfs:member/cdf:Fifteen)", doc);
        XMLAssert.assertXpathEvaluatesTo("Fifteen.2", "//wfs:FeatureCollection/wfs:member/cdf:Fifteen/@gml:id", doc);
        doc = getAsDOM("wfs?request=GetFeature&typeName=cite:NamedPlaces&version=2.0.0&service=wfs&resourceid=NamedPlaces.1107531895891");
        // super.print(doc);
        Assert.assertEquals("wfs:FeatureCollection", doc.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection/wfs:member/cite:NamedPlaces)", doc);
        XMLAssert.assertXpathEvaluatesTo("NamedPlaces.1107531895891", "//wfs:FeatureCollection/wfs:member/cite:NamedPlaces/@gml:id", doc);
    }

    @Test
    public void testGetWithInconsistentResourceId() throws Exception {
        GeoServer gs = getGeoServer();
        WFSInfo wfs = gs.getService(WFSInfo.class);
        wfs.setCiteCompliant(true);
        gs.save(wfs);
        try {
            // ask for a typename but with a reosurceid of another one
            Document doc = getAsDOM("wfs?request=GetFeature&typeNames=sf:AggregateGeoFeature&version=2.0.0&service=wfs&resourceid=Fifteen.2", 400);
            checkOws11Exception(doc, "2.0.0", ServiceException.INVALID_PARAMETER_VALUE, "RESOURCEID");
        } finally {
            wfs.setCiteCompliant(false);
            gs.save(wfs);
        }
    }

    @Test
    public void testGetWithConsistentResourceId() throws Exception {
        GeoServer gs = getGeoServer();
        WFSInfo wfs = gs.getService(WFSInfo.class);
        wfs.setCiteCompliant(true);
        gs.save(wfs);
        try {
            Document doc = getAsDOM("wfs?request=GetFeature&typeNames=cdf:Fifteen&version=2.0.0&service=wfs&resourceid=Fifteen.2", 200);
            assertGML32(doc);
            XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection/wfs:member/cdf:Fifteen)", doc);
            XMLAssert.assertXpathEvaluatesTo("Fifteen.2", "//wfs:FeatureCollection/wfs:member/cdf:Fifteen/@gml:id", doc);
        } finally {
            wfs.setCiteCompliant(false);
            gs.save(wfs);
        }
    }

    @Test
    public void testGetWithIdentifier() throws Exception {
        Document dom = getAsDOM("wfs?request=GetFeature&version=2.0.0&typeName=sf:PrimitiveGeoFeatureId&BBOX=57.0,-4.5,62.0,1.0,EPSG:4326");
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//sf:PrimitiveGeoFeatureId)", dom);
        XMLAssert.assertXpathEvaluatesTo("f002", "//sf:PrimitiveGeoFeatureId/gml:identifier", dom);
        XMLAssert.assertXpathEvaluatesTo(SF_URI, "//sf:PrimitiveGeoFeatureId/gml:identifier/@codeSpace", dom);
    }

    @Test
    public void testGetWithBBOX() throws Exception {
        Document dom = getAsDOM("wfs?request=GetFeature&version=2.0.0&typeName=sf:PrimitiveGeoFeature&BBOX=57.0,-4.5,62.0,1.0,EPSG:4326");
        XMLAssert.assertXpathEvaluatesTo("1", "count(//sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("//sf:PrimitiveGeoFeature/gml:name[text() = 'name-f002']", dom);
    }

    @Test
    public void testGetWithFilter() throws Exception {
        Document dom = getAsDOM("wfs?request=GetFeature&version=2.0.0&typeName=sf:PrimitiveGeoFeature&FILTER=%3Cfes%3AFilter+xmlns%3Agml%3D%22http%3A%2F%2Fwww.opengis.net%2Fgml%2F3.2%22+xmlns%3Afes%3D%22http%3A%2F%2Fwww.opengis.net%2Ffes%2F2.0%22%3E%3Cfes%3ABBOX%3E%3Cgml%3AEnvelope+srsName%3D%22EPSG%3A4326%22%3E%3Cgml%3AlowerCorner%3E57.0+-4.5%3C%2Fgml%3AlowerCorner%3E%3Cgml%3AupperCorner%3E62.0+1.0%3C%2Fgml%3AupperCorner%3E%3C%2Fgml%3AEnvelope%3E%3C%2Ffes%3ABBOX%3E%3C%2Ffes%3AFilter%3E");
        XMLAssert.assertXpathEvaluatesTo("1", "count(//sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("//sf:PrimitiveGeoFeature/gml:name[text() = 'name-f002']", dom);
    }

    @Test
    public void testPost() throws Exception {
        String xml = "<wfs:GetFeature " + (((((((("service='WFS' " + "version='2.0.0' ") + "xmlns:cdf='http://www.opengis.net/cite/data' ") + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='cdf:Other'> ") + "<wfs:PropertyName>cdf:string2</wfs:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document doc = postAsDOM("wfs", xml);
        assertGML32(doc);
        NodeList features = doc.getElementsByTagName("cdf:Other");
        Assert.assertFalse(((features.getLength()) == 0));
        for (int i = 0; i < (features.getLength()); i++) {
            Element feature = ((Element) (features.item(i)));
            Assert.assertTrue(feature.hasAttribute("gml:id"));
        }
    }

    @Test
    public void testPostMultipleQueriesDifferentNamespaces() throws Exception {
        String xml = "<wfs:GetFeature " + ((((((("service='WFS' " + "version='2.0.0' ") + "xmlns:cdf='http://www.opengis.net/cite/data' ") + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='cdf:Other'/> ") + "<wfs:Query typeNames='sf:PrimitiveGeoFeature'/> ") + "</wfs:GetFeature>");
        Document doc = postAsDOM("wfs", xml);
        print(doc);
        assertGML32(doc);
    }

    @Test
    public void testPostFormEncoded() throws Exception {
        String request = (("wfs?service=WFS&version=2.0.0&request=GetFeature&typename=sf:PrimitiveGeoFeature" + "&namespace=xmlns(") + (URLEncoder.encode("sf=http://cite.opengeospatial.org/gmlsf", "UTF-8"))) + ")";
        Document doc = postAsDOM(request);
        assertGML32(doc);
        Assert.assertEquals(5, doc.getElementsByTagName("sf:PrimitiveGeoFeature").getLength());
    }

    @Test
    public void testPostWithFilter() throws Exception {
        String xml = ((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0' " + (("outputFormat='text/xml; subtype=gml/3.2' " + "xmlns:cdf=\"http://www.opengis.net/cite/data\" ") + "xmlns:wfs='")) + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "<wfs:Query typeNames=\"cdf:Other\"> ") + "<fes:Filter> ") + "<fes:PropertyIsEqualTo> ") + "<fes:ValueReference>cdf:integers</fes:ValueReference> ") + "<fes:Literal>7</fes:Literal> ") + "</fes:PropertyIsEqualTo> ") + "</fes:Filter> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        assertGML32(doc);
        NodeList features = doc.getElementsByTagName("cdf:Other");
        Assert.assertFalse(((features.getLength()) == 0));
        for (int i = 0; i < (features.getLength()); i++) {
            Element feature = ((Element) (features.item(i)));
            Assert.assertTrue(feature.hasAttribute("gml:id"));
        }
    }

    @Test
    public void testPostWithBboxFilter() throws Exception {
        String xml = (((((((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0' " + (("outputFormat='text/xml; subtype=gml/3.2' " + "xmlns:sf=\"http://cite.opengeospatial.org/gmlsf\" ") + "xmlns:gml='")) + (GML.NAMESPACE)) + "' ") + "xmlns:wfs='") + (WFS.NAMESPACE)) + "' ") + "xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "<wfs:Query typeNames='sf:PrimitiveGeoFeature'>") + "<fes:Filter>") + "<fes:BBOX>") + "   <fes:ValueReference>pointProperty</fes:ValueReference>") + "   <gml:Envelope srsName='EPSG:4326'>") + "      <gml:lowerCorner>57.0 -4.5</gml:lowerCorner>") + "      <gml:upperCorner>62.0 1.0</gml:upperCorner>") + "   </gml:Envelope>") + "</fes:BBOX>") + "</fes:Filter>") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        assertGML32(doc);
        NodeList features = doc.getElementsByTagName("sf:PrimitiveGeoFeature");
        Assert.assertEquals(1, features.getLength());
    }

    @Test
    public void testPostWithBboxFilterOnBoundedBy() throws Exception {
        // CITE tests check filters against gml:BoundedBy property....
        String xml = (((((((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0' " + (("outputFormat='text/xml; subtype=gml/3.2' " + "xmlns:sf=\"http://cite.opengeospatial.org/gmlsf\" ") + "xmlns:gml='")) + (GML.NAMESPACE)) + "' ") + "xmlns:wfs='") + (WFS.NAMESPACE)) + "' ") + "xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "<wfs:Query typeNames='sf:PrimitiveGeoFeature'>") + "<fes:Filter>") + "<fes:BBOX>") + "   <fes:ValueReference>gml:boundedBy</fes:ValueReference>") + "   <gml:Envelope srsName='EPSG:4326'>") + "      <gml:lowerCorner>57.0 -4.5</gml:lowerCorner>") + "      <gml:upperCorner>62.0 1.0</gml:upperCorner>") + "   </gml:Envelope>") + "</fes:BBOX>") + "</fes:Filter>") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        assertGML32(doc);
        NodeList features = doc.getElementsByTagName("sf:PrimitiveGeoFeature");
        Assert.assertEquals(1, features.getLength());
    }

    @Test
    public void testPostWithLessThanOnBoundedBy() throws Exception {
        // CITE tests check this filter against bounded by and wants
        // to check an internal error shows up...
        GeoServer gs = getGeoServer();
        // CITE compliance forces XML validation, which comes from sources in this test
        WFSInfo wfs = gs.getService(WFSInfo.class);
        wfs.setCiteCompliant(true);
        gs.save(wfs);
        try {
            String xml = ((((((((((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0' " + (("outputFormat='text/xml; subtype=gml/3.2' " + "xmlns:sf=\"http://cite.opengeospatial.org/gmlsf\" ") + "xmlns:gml='")) + (GML.NAMESPACE)) + "' ") + "xmlns:wfs='") + (WFS.NAMESPACE)) + "' ") + "xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "<wfs:Query typeNames='sf:PrimitiveGeoFeature'>") + "<fes:Filter>") + "<fes:PropertyIsLessThanOrEqualTo matchAction=\"Any\" matchCase=\"true\">\n") + "    <fes:Literal>\n") + "      <gml:Envelope xmlns:gml=\"http://www.opengis.net/gml/3.2\"\n") + "        srsName=\"urn:ogc:def:crs:EPSG::4326\">\n") + "        <gml:lowerCorner>-90 -180</gml:lowerCorner>\n") + "        <gml:upperCorner>90 180</gml:upperCorner>\n") + "      </gml:Envelope>\n") + "    </fes:Literal>\n") + "    <fes:ValueReference>gml:boundedBy</fes:ValueReference>\n") + "  </fes:PropertyIsLessThanOrEqualTo>") + "</fes:Filter>") + "</wfs:Query>") + "</wfs:GetFeature>";
            MockHttpServletResponse response = postAsServletResponse("wfs", xml);
            Assert.assertEquals(500, response.getStatus());
            Document doc = dom(new ByteArrayInputStream(response.getContentAsByteArray()));
            checkOws11Exception(doc, "2.0.0", WFSException.OPERATION_PROCESSING_FAILED, "GetFeature");
        } finally {
            wfs.setCiteCompliant(false);
            gs.save(wfs);
        }
    }

    @Test
    public void testPostWithFailingUrnBboxFilter() throws Exception {
        String xml = ((((((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0'  outputFormat='text/xml; subtype=gml/3.2' " + ("xmlns:sf='http://cite.opengeospatial.org/gmlsf' " + "xmlns:wfs='")) + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "' ") + "xmlns:gml='") + (GML.NAMESPACE)) + "'>") + "<wfs:Query typeNames=\"sf:PrimitiveGeoFeature\">") + "<fes:Filter>") + "<fes:BBOX>") + "   <fes:PropertyName>pointProperty</fes:PropertyName>") + "   <gml:Envelope srsName='urn:ogc:def:crs:EPSG:6.11.2:4326'>") + "      <gml:lowerCorner>57.0 -4.5</gml:lowerCorner>") + "      <gml:upperCorner>62.0 1.0</gml:upperCorner>") + "   </gml:Envelope>") + "</fes:BBOX>") + "</fes:Filter>") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        assertGML32(doc);
        NodeList features = doc.getElementsByTagName("sf:PrimitiveGeoFeature");
        Assert.assertEquals(0, features.getLength());
    }

    @Test
    public void testPostWithMatchingUrnBboxFilter() throws Exception {
        String xml = ((((((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0'  outputFormat='text/xml; subtype=gml/3.2' " + ("xmlns:sf='http://cite.opengeospatial.org/gmlsf' " + "xmlns:wfs='")) + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "' ") + "xmlns:gml='") + (GML.NAMESPACE)) + "'>") + "<wfs:Query typeNames=\"sf:PrimitiveGeoFeature\">") + "<fes:Filter>") + "<fes:BBOX>") + "   <fes:PropertyName>pointProperty</fes:PropertyName>") + "   <gml:Envelope srsName='urn:ogc:def:crs:EPSG:6.11.2:4326'>") + "      <gml:lowerCorner>-4.5 57.0</gml:lowerCorner>") + "      <gml:upperCorner>1.0 62.0</gml:upperCorner>") + "   </gml:Envelope>") + "</fes:BBOX>") + "</fes:Filter>") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        assertGML32(doc);
        NodeList features = doc.getElementsByTagName("sf:PrimitiveGeoFeature");
        Assert.assertEquals(1, features.getLength());
    }

    @Test
    public void testPostWithFunctionFilter() throws Exception {
        String xml = ((((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0'  outputFormat='text/xml; subtype=gml/3.2' " + ("xmlns:sf='http://cite.opengeospatial.org/gmlsf' " + "xmlns:wfs='")) + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "' ") + "xmlns:gml='") + (GML.NAMESPACE)) + "'>") + "<wfs:Query typeNames=\"sf:PrimitiveGeoFeature\">") + "<fes:Filter>") + "<fes:PropertyIsLessThan>") + "<fes:Function name='random'>") + "</fes:Function>") + "<fes:Literal>0.5</fes:Literal>") + "</fes:PropertyIsLessThan>") + "</fes:Filter>") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        assertGML32(doc);
    }

    @Test
    public void testResultTypeHitsGet() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typename=cdf:Fifteen&version=2.0.0&resultType=hits&service=wfs");
        print(doc);
        assertGML32(doc);
        NodeList features = doc.getElementsByTagName("cdf:Fifteen");
        Assert.assertEquals(0, features.getLength());
        Assert.assertEquals("15", doc.getDocumentElement().getAttribute("numberMatched"));
    }

    @Test
    public void testResultTypeHitsGetWithCount() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typename=cdf:Fifteen&version=2.0.0&resultType=hits&service=wfs&count=2");
        print(doc);
        assertGML32(doc);
        NodeList features = doc.getElementsByTagName("cdf:Fifteen");
        Assert.assertEquals(0, features.getLength());
        Assert.assertEquals("15", doc.getDocumentElement().getAttribute("numberMatched"));
        // there must be a next link pointing to the beginning of the result set
        String nextLink = doc.getDocumentElement().getAttribute("next");
        Assert.assertNotNull(nextLink);
        Map<String, Object> kvp = KvpUtils.parseQueryString(nextLink);
        Assert.assertEquals("results", kvp.get("RESULTTYPE"));
        Assert.assertEquals("2", kvp.get("COUNT"));
        Assert.assertEquals("0", kvp.get("STARTINDEX"));
    }

    @Test
    public void testResultTypeHitsPost() throws Exception {
        String xml = ((((("<wfs:GetFeature service='WFS' version='2.0.0' outputFormat='text/xml; subtype=gml/3.2' " + ("xmlns:cdf=\"http://www.opengis.net/cite/data\" " + "xmlns:wfs='")) + (WFS.NAMESPACE)) + "' ") + "resultType='hits'> ") + "<wfs:Query typeNames=\"cdf:Seven\"/> ") + "</wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        assertGML32(doc);
        NodeList features = doc.getElementsByTagName("cdf:Seven");
        Assert.assertEquals(0, features.getLength());
        Assert.assertEquals("7", doc.getDocumentElement().getAttribute("numberMatched"));
    }

    @Test
    public void testResultTypeHitsNumReturnedMatched() throws Exception {
        String xml = ((((("<wfs:GetFeature service='WFS' version='2.0.0' outputFormat='text/xml; subtype=gml/3.2' " + ("xmlns:cdf=\"http://www.opengis.net/cite/data\" " + "xmlns:wfs='")) + (WFS.NAMESPACE)) + "' ") + "resultType='hits'> ") + "<wfs:Query typeNames=\"cdf:Seven\"/> ") + "</wfs:GetFeature>";
        Document doc = postAsDOM("wfs", xml);
        assertGML32(doc);
        Assert.assertEquals("7", doc.getDocumentElement().getAttribute("numberMatched"));
        Assert.assertEquals("0", doc.getDocumentElement().getAttribute("numberReturned"));
    }

    @Test
    public void testResultTypeHitsNumberMatched() throws Exception {
        WFSInfo wfs = getWFS();
        int oldMaxFeatures = wfs.getMaxFeatures();
        boolean hitsIgnoreMaxFeatures = wfs.isHitsIgnoreMaxFeatures();
        try {
            // we ignore max features for number matched, regardless of the hits ingore max features
            // setting
            wfs.setMaxFeatures(1);
            wfs.setHitsIgnoreMaxFeatures(true);
            getGeoServer().save(wfs);
            Document doc = getAsDOM("wfs?request=GetFeature&typename=cdf:Seven&version=2.0.0&resultType=hits&service=wfs");
            assertGML32(doc);
            print(doc);
            Assert.assertEquals("7", doc.getDocumentElement().getAttribute("numberMatched"));
            Assert.assertEquals("0", doc.getDocumentElement().getAttribute("numberReturned"));
            // and we are consistent in the results mode too
            doc = getAsDOM("wfs?request=GetFeature&typename=cdf:Seven&version=2.0.0&resultType=results&service=wfs");
            assertGML32(doc);
            Assert.assertEquals("7", doc.getDocumentElement().getAttribute("numberMatched"));
            Assert.assertEquals("1", doc.getDocumentElement().getAttribute("numberReturned"));
            // try hits ignores max features the other way
            doc = getAsDOM("wfs?request=GetFeature&typename=cdf:Seven&version=2.0.0&resultType=hits&service=wfs");
            wfs.setHitsIgnoreMaxFeatures(false);
            getGeoServer().save(wfs);
            Assert.assertEquals("7", doc.getDocumentElement().getAttribute("numberMatched"));
            Assert.assertEquals("0", doc.getDocumentElement().getAttribute("numberReturned"));
            // and we are consistent in the results mode too
            doc = getAsDOM("wfs?request=GetFeature&typename=cdf:Seven&version=2.0.0&resultType=results&service=wfs");
            assertGML32(doc);
            Assert.assertEquals("7", doc.getDocumentElement().getAttribute("numberMatched"));
            Assert.assertEquals("1", doc.getDocumentElement().getAttribute("numberReturned"));
        } finally {
            wfs.setMaxFeatures(oldMaxFeatures);
            wfs.setHitsIgnoreMaxFeatures(hitsIgnoreMaxFeatures);
            getGeoServer().save(wfs);
        }
    }

    @Test
    public void testNumReturnedMatchedWithMaxFeatures() throws Exception {
        WFSInfo wfs = getWFS();
        int oldMaxFeatures = wfs.getMaxFeatures();
        boolean oldHitsIgnoreMaxFeatures = wfs.isHitsIgnoreMaxFeatures();
        try {
            wfs.setMaxFeatures(1);
            wfs.setHitsIgnoreMaxFeatures(true);
            getGeoServer().save(wfs);
            Document doc = getAsDOM("wfs?request=GetFeature&typename=cdf:Seven&version=2.0.0&resultType=results&service=wfs");
            assertGML32(doc);
            Assert.assertEquals("7", doc.getDocumentElement().getAttribute("numberMatched"));
            Assert.assertEquals("1", doc.getDocumentElement().getAttribute("numberReturned"));
        } finally {
            wfs.setMaxFeatures(oldMaxFeatures);
            wfs.setHitsIgnoreMaxFeatures(oldHitsIgnoreMaxFeatures);
            getGeoServer().save(wfs);
        }
    }

    @Test
    public void testWithSRS() throws Exception {
        String xml = (((("<wfs:GetFeature version='2.0.0' service='WFS' xmlns:wfs='" + (WFS.NAMESPACE)) + "' >") + "<wfs:Query xmlns:cdf='http://www.opengis.net/cite/data' typeNames='cdf:Other' ") + "srsName='urn:ogc:def:crs:EPSG:4326'/>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        assertGML32(dom);
        Assert.assertEquals(1, dom.getElementsByTagName("cdf:Other").getLength());
    }

    @Test
    public void testWithSillyLiteral() throws Exception {
        String xml = ((((((((((((((((((("<wfs:GetFeature version='2.0.0' service='WFS' " + ("xmlns:cdf='http://www.opengis.net/cite/data' " + "xmlns:wfs='")) + (WFS.NAMESPACE)) + "' ") + "xmlns:gml='") + (GML.NAMESPACE)) + "' ") + "xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "<wfs:Query  typeNames='cdf:Other' srsName='urn:ogc:def:crs:EPSG:4326'>") + "<fes:Filter>") + "  <fes:PropertyIsEqualTo>") + "   <fes:ValueReference>description</fes:ValueReference>") + "   <fes:Literal>") + "       <wfs:Native vendorId=\"foo\" safeToIgnore=\"true\"/>") + "   </fes:Literal>") + "   </fes:PropertyIsEqualTo>") + " </fes:Filter>") + "</wfs:Query>") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        assertGML32(dom);
        Assert.assertEquals(0, dom.getElementsByTagName("cdf:Other").getLength());
    }

    // 
    // public void testWithGmlObjectId() throws Exception {
    // String xml = "<wfs:GetFeature xmlns:cdf=\"http://www.opengis.net/cite/data\"
    // xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:gml=\"http://www.opengis.net/gml\"
    // xmlns:ogc=\"http://www.opengis.net/ogc\" version=\"1.1.0\" service=\"WFS\">"
    // + "<wfs:Query  typeName=\"cdf:Seven\"
    // srsName=\"urn:x-ogc:def:crs:EPSG:6.11.2:4326\">"
    // + "</wfs:Query>" + "</wfs:GetFeature>";
    // 
    // Document dom = postAsDOM("wfs", xml);
    // assertEquals("wfs:FeatureCollection", dom.getDocumentElement()
    // .getNodeName());
    // assertEquals(7, dom.getElementsByTagName("cdf:Seven")
    // .getLength());
    // 
    // NodeList others = dom.getElementsByTagName("cdf:Seven");
    // String id = ((Element) others.item(0)).getAttributeNS(GML.NAMESPACE,
    // "id");
    // assertNotNull(id);
    // 
    // xml = "<wfs:GetFeature xmlns:cdf=\"http://www.opengis.net/cite/data\"
    // xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:gml=\"http://www.opengis.net/gml\"
    // xmlns:ogc=\"http://www.opengis.net/ogc\" version=\"1.1.0\" service=\"WFS\">"
    // + "<wfs:Query  typeName=\"cdf:Seven\"
    // srsName=\"urn:x-ogc:def:crs:EPSG:6.11.2:4326\">"
    // + "<ogc:Filter>"
    // + "<ogc:GmlObjectId gml:id=\""
    // + id
    // + "\"/>"
    // + "</ogc:Filter>" + "</wfs:Query>" + "</wfs:GetFeature>";
    // dom = postAsDOM("wfs", xml);
    // 
    // assertEquals(1, dom.getElementsByTagName("cdf:Seven")
    // .getLength());
    // }
    // 
    @Test
    public void testPostWithBoundsEnabled() throws Exception {
        // enable feature bounds computation
        WFSInfo wfs = getWFS();
        boolean oldFeatureBounding = wfs.isFeatureBounding();
        wfs.setFeatureBounding(true);
        getGeoServer().save(wfs);
        try {
            String xml = (((((((("<wfs:GetFeature service='WFS' version='2.0.0' " + ("xmlns:cdf='http://www.opengis.net/cite/data' " + "xmlns:fes='")) + (FES.NAMESPACE)) + "' ") + "xmlns:wfs='") + (WFS.NAMESPACE)) + "'> ") + "<wfs:Query typeNames='cdf:Other'> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
            Document doc = postAsDOM("wfs", xml);
            assertGML32(doc);
            NodeList aggregatedBoundList = doc.getElementsByTagName("wfs:boundedBy");
            Assert.assertFalse(((aggregatedBoundList.getLength()) == 0));
            NodeList features = doc.getElementsByTagName("cdf:Other");
            Assert.assertFalse(((features.getLength()) == 0));
            for (int i = 0; i < (features.getLength()); i++) {
                Element feature = ((Element) (features.item(i)));
                Assert.assertTrue(feature.hasAttribute("gml:id"));
                NodeList boundList = feature.getElementsByTagName("gml:boundedBy");
                Assert.assertEquals(1, boundList.getLength());
                Element boundedBy = ((Element) (boundList.item(0)));
                NodeList boxList = boundedBy.getElementsByTagName("gml:Envelope");
                Assert.assertEquals(1, boxList.getLength());
                Element box = ((Element) (boxList.item(0)));
                Assert.assertTrue(box.hasAttribute("srsName"));
            }
        } finally {
            wfs.setFeatureBounding(oldFeatureBounding);
            getGeoServer().save(wfs);
        }
    }

    @Test
    public void testPostWithBoundsDisabled() throws Exception {
        // enable feature bounds computation
        WFSInfo wfs = getWFS();
        boolean oldFeatureBounding = wfs.isFeatureBounding();
        wfs.setFeatureBounding(false);
        getGeoServer().save(wfs);
        try {
            String xml = (((((((("<wfs:GetFeature service='WFS' version='2.0.0' " + ("xmlns:cdf='http://www.opengis.net/cite/data' " + "xmlns:fes='")) + (FES.NAMESPACE)) + "' ") + "xmlns:wfs='") + (WFS.NAMESPACE)) + "'> ") + "<wfs:Query typeNames='cdf:Other'> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
            Document doc = postAsDOM("wfs", xml);
            assertGML32(doc);
            NodeList aggregatedBoundList = doc.getElementsByTagName("wfs:boundedBy");
            Assert.assertTrue(((aggregatedBoundList.getLength()) == 0));
            NodeList features = doc.getElementsByTagName("cdf:Other");
            Assert.assertFalse(((features.getLength()) == 0));
            for (int i = 0; i < (features.getLength()); i++) {
                Element feature = ((Element) (features.item(i)));
                Assert.assertTrue(feature.hasAttribute("gml:id"));
                NodeList boundList = feature.getElementsByTagName("gml:boundedBy");
                Assert.assertEquals(0, boundList.getLength());
            }
        } finally {
            wfs.setFeatureBounding(oldFeatureBounding);
            getGeoServer().save(wfs);
        }
    }

    @Test
    public void testAfterFeatureTypeAdded() throws Exception {
        Document dom = getAsDOM("wfs?request=getfeature&service=wfs&version=2.0.0&typename=sf:new");
        Assert.assertEquals("ExceptionReport", dom.getDocumentElement().getLocalName());
        getTestData().addVectorLayer(new QName(SystemTestData.SF_URI, "new", SystemTestData.SF_PREFIX), Collections.EMPTY_MAP, org.geoserver.wfs.v1_1.GetFeatureTest.class, getCatalog());
        // reloadCatalogAndConfiguration();
        dom = getAsDOM("wfs?request=getfeature&service=wfs&version=2.0.0&typename=sf:new");
        print(dom);
        assertGML32(dom);
    }

    @Test
    public void testWithGMLProperties() throws Exception {
        Document dom = getAsDOM("wfs?request=getfeature&service=wfs&version=2.0.0&typename=sf:WithGMLProperties");
        assertGML32(dom);
        NodeList features = dom.getElementsByTagName("sf:WithGMLProperties");
        Assert.assertEquals(1, features.getLength());
        for (int i = 0; i < (features.getLength()); i++) {
            Element feature = ((Element) (features.item(i)));
            Assert.assertEquals("one", getFirstElementByTagName(feature, "gml:name").getFirstChild().getNodeValue());
            Assert.assertEquals("1", getFirstElementByTagName(feature, "sf:foo").getFirstChild().getNodeValue());
            Element location = getFirstElementByTagName(feature, "gml:location");
            Assert.assertNotNull(getFirstElementByTagName(location, "gml:Point"));
        }
    }

    @Test
    public void testLayerQualified() throws Exception {
        testGetFifteenAll("cdf/Fifteen/wfs?request=GetFeature&typename=cdf:Fifteen&version=2.0.0&service=wfs");
        Document dom = getAsDOM("cdf/Seven/wfs?request=GetFeature&typename=cdf:Fifteen&version=2.0.0&service=wfs");
        XMLAssert.assertXpathEvaluatesTo("1", "count(//ows:ExceptionReport)", dom);
    }

    @Test
    public void testUserSuppliedNamespacePrefix() throws Exception {
        testGetFifteenAll(((("wfs?request=GetFeature&typename=myPrefix:Fifteen&version=2.0.0&service=wfs&" + "namespaces=xmlns(myPrefix,") + (URLEncoder.encode(FIFTEEN.getNamespaceURI(), "UTF-8"))) + ")"));
    }

    @Test
    public void testUserSuppliedDefaultNamespace() throws Exception {
        testGetFifteenAll(((("wfs?request=GetFeature&typename=Fifteen&version=2.0.0&service=wfs&" + "namespace=xmlns(") + (URLEncoder.encode(FIFTEEN.getNamespaceURI(), "UTF-8"))) + ")"));
    }

    @Test
    public void testGML32OutputFormat() throws Exception {
        testGetFifteenAll("wfs?request=getfeature&typename=cdf:Fifteen&version=2.0.0&service=wfs&outputFormat=gml32");
    }

    @Test
    public void testGML32OutputFormatAlternate() throws Exception {
        testGetFifteenAll("wfs?request=getfeature&typename=cdf:Fifteen&version=2.0.0&service=wfs&outputFormat=application/gml%2Bxml; version%3D3.2");
    }

    @Test
    public void testGMLAttributeMapping() throws Exception {
        WFSInfo wfs = getWFS();
        GMLInfo gml = wfs.getGML().get(V_11);
        gml.setOverrideGMLAttributes(false);
        getGeoServer().save(wfs);
        Document dom = getAsDOM((("ows?service=WFS&version=2.0.0&request=GetFeature" + "&typename=") + (getLayerId(MockData.PRIMITIVEGEOFEATURE))));
        XMLAssert.assertXpathExists("//gml:name", dom);
        XMLAssert.assertXpathExists("//gml:description", dom);
        XMLAssert.assertXpathNotExists("//sf:name", dom);
        XMLAssert.assertXpathNotExists("//sf:description", dom);
        gml.setOverrideGMLAttributes(true);
        getGeoServer().save(wfs);
        dom = getAsDOM((("ows?service=WFS&version=2.0.0&request=GetFeature" + "&typename=") + (getLayerId(MockData.PRIMITIVEGEOFEATURE))));
        XMLAssert.assertXpathNotExists("//gml:name", dom);
        XMLAssert.assertXpathNotExists("//gml:description", dom);
        XMLAssert.assertXpathExists("//sf:name", dom);
        XMLAssert.assertXpathExists("//sf:description", dom);
        gml.setOverrideGMLAttributes(false);
        getGeoServer().save(wfs);
    }

    @Test
    public void testStoredQuery() throws Exception {
        String xml = ((((((((((((((((((("<wfs:CreateStoredQuery service='WFS' version='2.0.0' " + (((("   xmlns:wfs='http://www.opengis.net/wfs/2.0' " + "   xmlns:fes='http://www.opengis.net/fes/2.0' ") + "   xmlns:gml='http://www.opengis.net/gml/3.2' ") + "   xmlns:myns='http://www.someserver.com/myns' ") + "   xmlns:sf='")) + (MockData.SF_URI)) + "'>") + "   <wfs:StoredQueryDefinition id='myStoredQuery'> ") + "      <wfs:Parameter name='integers' type='xs:integer'/> ") + "      <wfs:QueryExpressionText ") + "           returnFeatureTypes='cdf:Other' ") + "           language='urn:ogc:def:queryLanguage:OGC-WFS::WFS_QueryExpression' ") + "           isPrivate='false'> ") + "<wfs:Query typeNames=\"cdf:Other\"> ") + "<fes:Filter> ") + "<fes:PropertyIsEqualTo> ") + "<fes:ValueReference>cdf:integers</fes:ValueReference> ") + "${integers}") + "</fes:PropertyIsEqualTo> ") + "</fes:Filter> ") + "</wfs:Query> ") + "      </wfs:QueryExpressionText> ") + "   </wfs:StoredQueryDefinition> ") + "</wfs:CreateStoredQuery>";
        Document dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:CreateStoredQueryResponse", dom.getDocumentElement().getNodeName());
        xml = (((((((((("<wfs:GetFeature service='WFS' version='2.0.0' " + "       xmlns:wfs='") + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "   <wfs:StoredQuery id='myStoredQuery'> ") + "      <wfs:Parameter name='integers'>") + "        <fes:Literal>7</fes:Literal>") + "      </wfs:Parameter> ") + "   </wfs:StoredQuery> ") + "</wfs:GetFeature>";
        dom = postAsDOM("wfs", xml);
        print(dom);
        assertGML32(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//cdf:Other)", dom);
        XMLAssert.assertXpathExists("//cdf:Other/cdf:integers[text() = '7']", dom);
    }

    @Test
    public void testDefaultStoredQueryGet() throws Exception {
        Document dom = getAsDOM((("wfs?request=GetFeature&version=2.0.0&storedQueryId=" + (DEFAULT.getName())) + "&ID=PrimitiveGeoFeature.f001"));
        // GetFeatureById is special, should not be wrapped by wfs:FeatureCollection
        XMLAssert.assertXpathNotExists("//wfs:FeatureCollection", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("/sf:PrimitiveGeoFeature[@gml:id = 'PrimitiveGeoFeature.f001']", dom);
    }

    @Test
    public void testUnknownStoredQuery() throws Exception {
        Document dom = getAsDOM("wfs?request=GetFeature&version=2.0.0&storedQueryId=foobar", 400);
        checkOws11Exception(dom, "2.0.0", "InvalidParameterValue", "STOREDQUERY_ID");
    }

    /**
     * Used to NPE, see GEOS-7532
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDefaultStoredQueryGetWorkspaceQualified() throws Exception {
        Document dom = getAsDOM((("sf/wfs?request=GetFeature&version=2.0.0&storedQueryId=" + (DEFAULT.getName())) + "&ID=PrimitiveGeoFeature.f001"));
        // GetFeatureById is special, should not be wrapped by wfs:FeatureCollection
        XMLAssert.assertXpathNotExists("//wfs:FeatureCollection", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("/sf:PrimitiveGeoFeature[@gml:id = 'PrimitiveGeoFeature.f001']", dom);
    }

    @Test
    public void testDefaultStoredQueryPost() throws Exception {
        // this one has a lowercase id, as specification requires
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((("<wfs:GetFeature xmlns:wfs=\"http://www.opengis.net/wfs/2.0\" service=\"WFS\" startIndex=\"0\" " + "version=\"2.0.0\">\n") + "\t<wfs:StoredQuery id=\"urn:ogc:def:query:OGC-WFS::GetFeatureById\">\n") + "\t\t<wfs:Parameter name=\"id\">PrimitiveGeoFeature.f001</wfs:Parameter>\n") + "\t</wfs:StoredQuery>\n") + "</wfs:GetFeature>\n");
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("//sf:PrimitiveGeoFeature[@gml:id = 'PrimitiveGeoFeature.f001']", dom);
        dom = getAsDOM((("wfs?request=GetFeature&version=2.0.0&storedQuery_Id=" + (DEFAULT.getName())) + "&ID=PrimitiveGeoFeature.f001"));
        // print(dom);
        XMLAssert.assertXpathNotExists("//wfs:FeatureCollection", dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(/sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("/sf:PrimitiveGeoFeature[@gml:id = 'PrimitiveGeoFeature.f001']", dom);
    }

    @Test
    public void testStoredQueryBBOX() throws Exception {
        String xml = ((((((((((((((((((("<wfs:CreateStoredQuery service='WFS' version='2.0.0' " + (((("   xmlns:wfs='http://www.opengis.net/wfs/2.0' " + "   xmlns:fes='http://www.opengis.net/fes/2.0' ") + "   xmlns:gml='http://www.opengis.net/gml/3.2' ") + "   xmlns:myns='http://www.someserver.com/myns' ") + "   xmlns:sf='")) + (MockData.SF_URI)) + "'>") + "   <wfs:StoredQueryDefinition id='myStoredBBOXQuery'> ") + "      <wfs:Parameter name='BBOX' type='gml:Envelope'/> ") + "      <wfs:QueryExpressionText ") + "           returnFeatureTypes='sf:PrimitiveGeoFeature' ") + "           language='urn:ogc:def:queryLanguage:OGC-WFS::WFS_QueryExpression' ") + "           isPrivate='false'> ") + "<wfs:Query typeNames='sf:PrimitiveGeoFeature'> ") + "<fes:Filter> ") + "<fes:BBOX>") + "   <fes:ValueReference>pointProperty</fes:ValueReference>") + "    ${BBOX}") + "</fes:BBOX>") + "</fes:Filter> ") + "</wfs:Query> ") + "      </wfs:QueryExpressionText> ") + "   </wfs:StoredQueryDefinition> ") + "</wfs:CreateStoredQuery>";
        Document dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:CreateStoredQueryResponse", dom.getDocumentElement().getNodeName());
        xml = ((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0' xmlns:gml='" + (GML.NAMESPACE)) + "'") + "       xmlns:wfs='") + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "   <wfs:StoredQuery id='myStoredBBOXQuery'> ") + "      <wfs:Parameter name='BBOX'>") + "   <gml:Envelope srsName='EPSG:4326'>") + "      <gml:lowerCorner>57.0 -4.5</gml:lowerCorner>") + "      <gml:upperCorner>62.0 1.0</gml:upperCorner>") + "   </gml:Envelope>") + "      </wfs:Parameter> ") + "   </wfs:StoredQuery> ") + "</wfs:GetFeature>";
        dom = postAsDOM("wfs", xml);
        print(dom);
        assertGML32(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("//sf:PrimitiveGeoFeature/gml:name[text() = 'name-f002']", dom);
    }

    @Test
    public void testTemporalFilter() throws Exception {
        print(getAsDOM("wfs?version=2.0.0&service=wfs&request=getfeature&typename=sf:PrimitiveGeoFeature&propertyName=dateProperty,dateTimeProperty"));
        // 2006-06-26T18:00:00-06:00
        String xml = (((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0' " + ("outputFormat='text/xml; subtype=gml/3.2' " + "xmlns:sf='")) + (MockData.SF_URI)) + "' ") + "xmlns:wfs='") + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "<wfs:Query typeNames=\"sf:PrimitiveGeoFeature\"> ") + "<fes:Filter> ") + "<fes:After> ") + "<fes:ValueReference>dateTimeProperty</fes:ValueReference> ") + "<fes:Literal>2006-06-25T18:00:00-06:00</fes:Literal> ") + "</fes:After> ") + "</fes:Filter> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("//sf:PrimitiveGeoFeature[@gml:id = 'PrimitiveGeoFeature.f008']", dom);
        xml = (((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0' " + ("outputFormat='text/xml; subtype=gml/3.2' " + "xmlns:sf='")) + (MockData.SF_URI)) + "' ") + "xmlns:wfs='") + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "<wfs:Query typeNames=\"sf:PrimitiveGeoFeature\"> ") + "<fes:Filter> ") + "<fes:Before> ") + "<fes:ValueReference>dateTimeProperty</fes:ValueReference> ") + "<fes:Literal>2006-06-27T18:00:00-06:00</fes:Literal> ") + "</fes:Before> ") + "</fes:Filter> ") + "</wfs:Query> ") + "</wfs:GetFeature>";
        dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("//sf:PrimitiveGeoFeature[@gml:id = 'PrimitiveGeoFeature.f008']", dom);
    }

    @Test
    public void testGetFeatureInvalidPropertyName() throws Exception {
        Document dom = getAsDOM("wfs?version=2.0.0&service=wfs&request=GetFeature&typename=sf:PrimitiveGeoFeature&propertyName=foo");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathExists("//ows:Exception[@exceptionCode = 'InvalidParameterValue']", dom);
    }

    @Test
    public void testGetFeatureWithMultiplePropertyName() throws Exception {
        Document dom = getAsDOM("wfs?version=2.0.0&service=wfs&request=GetFeature&typename=cdf:Fifteen,cdf:Seven");
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("2", "count(wfs:FeatureCollection/wfs:member)", dom);
        XMLAssert.assertXpathEvaluatesTo("2", "count(wfs:FeatureCollection/wfs:member/wfs:FeatureCollection)", dom);
        XMLAssert.assertXpathEvaluatesTo("15", "count(wfs:FeatureCollection/wfs:member[position() = 1]/wfs:FeatureCollection//cdf:Fifteen)", dom);
        XMLAssert.assertXpathEvaluatesTo("7", "count(wfs:FeatureCollection/wfs:member[position() = 2]/wfs:FeatureCollection//cdf:Seven)", dom);
        String xml = "<wfs:GetFeature " + ((((((("service='WFS' " + "version='2.0.0' ") + "xmlns:cdf='http://www.opengis.net/cite/data' ") + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='cdf:Fifteen'/> ") + "<wfs:Query typeNames='cdf:Seven'/> ") + "</wfs:GetFeature>");
        dom = getAsDOM("wfs?version=2.0.0&service=wfs&request=GetFeature&typename=cdf:Fifteen,cdf:Seven");
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("2", "count(wfs:FeatureCollection/wfs:member)", dom);
        XMLAssert.assertXpathEvaluatesTo("2", "count(wfs:FeatureCollection/wfs:member/wfs:FeatureCollection)", dom);
        XMLAssert.assertXpathEvaluatesTo("15", "count(wfs:FeatureCollection/wfs:member[position() = 1]/wfs:FeatureCollection//cdf:Fifteen)", dom);
        XMLAssert.assertXpathEvaluatesTo("7", "count(wfs:FeatureCollection/wfs:member[position() = 2]/wfs:FeatureCollection//cdf:Seven)", dom);
    }

    @Test
    public void testSOAP11() throws Exception {
        String xml = ((((((((((((((("<soap:Envelope xmlns:soap='" + (Dispatcher.SOAP_11_NS)) + "'> ") + " <soap:Header/> ") + " <soap:Body>") + "<wfs:GetFeature ") + "service='WFS' ") + "version='2.0.0' ") + "xmlns:cdf='http://www.opengis.net/cite/data' ") + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='cdf:Other'> ") + "<wfs:PropertyName>cdf:string2</wfs:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>") + " </soap:Body> ") + "</soap:Envelope> ";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/soap+xml");
        Assert.assertEquals("application/soap+xml", resp.getContentType());
        Document dom = dom(new ByteArrayInputStream(resp.getContentAsByteArray()));
        Assert.assertEquals(SOAP_11_NS, dom.getDocumentElement().getAttribute("xmlns:soap"));
    }

    @Test
    public void testSOAP12() throws Exception {
        String xml = ((((((((((((((("<soap:Envelope xmlns:soap='" + (Dispatcher.SOAP_12_NS)) + "'> ") + " <soap:Header/> ") + " <soap:Body>") + "<wfs:GetFeature ") + "service='WFS' ") + "version='2.0.0' ") + "xmlns:cdf='http://www.opengis.net/cite/data' ") + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='cdf:Other'> ") + "<wfs:PropertyName>cdf:string2</wfs:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>") + " </soap:Body> ") + "</soap:Envelope> ";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/soap+xml");
        Assert.assertEquals("application/soap+xml", resp.getContentType());
        Document dom = dom(new ByteArrayInputStream(resp.getContentAsByteArray()));
        // print(dom);
        Assert.assertEquals(SOAP_12_NS, dom.getDocumentElement().getAttribute("xmlns:soap"));
    }

    @Test
    public void testSOAPWithEntity() throws Exception {
        String xml = (((((((((((((((("<!DOCTYPE Envelope [" + (("<!ELEMENT Envelope ANY>" + "<!ENTITY xxe SYSTEM \"file:///this/file/does/not/exist\">]>") + "<soap:Envelope xmlns:soap='")) + (Dispatcher.SOAP_12_NS)) + "'> ") + " <soap:Header/> ") + " <soap:Body>") + "<wfs:GetFeature ") + "service='WFS' ") + "version='2.0.0' ") + "xmlns:cdf='http://www.opengis.net/cite/data' ") + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='cdf:Other'> ") + "<wfs:PropertyName>&xxe;</wfs:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>") + " </soap:Body> ") + "</soap:Envelope> ";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/soap+xml");
        Assert.assertEquals("application/soap+xml", resp.getContentType());
        String message = resp.getContentAsString();
        Assert.assertThat(message, CoreMatchers.containsString("Entity resolution disallowed"));
    }

    @Test
    public void testBogusSrsName() throws Exception {
        String xml = "<wfs:GetFeature service='WFS' version='2.0.0' " + (((((("xmlns:cdf='http://www.opengis.net/cite/data' " + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='cdf:Other' srsName='EPSG:XYZ'> ") + "<wfs:PropertyName>cdf:string2</wfs:PropertyName> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("wfs", xml);
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("InvalidParameterValue", "//ows:Exception/@exceptionCode", dom);
        XMLAssert.assertXpathEvaluatesTo("srsName", "//ows:Exception/@locator", dom);
        dom = getAsDOM("wfs?service=WFS&version=2.0.0&request=getFeature&typeName=cdf:Other&srsName=EPSG:XYZ");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("InvalidParameterValue", "//ows:Exception/@exceptionCode", dom);
        XMLAssert.assertXpathEvaluatesTo("srsName", "//ows:Exception/@locator", dom);
    }

    @Test
    public void testQueryHandleInExceptionReport() throws Exception {
        String xml = "<wfs:GetFeature service='WFS' version='2.0.0' " + (((((((((((("xmlns:cdf='http://www.opengis.net/cite/data' " + "xmlns:fes='http://www.opengis.net/fes/2.0' ") + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='cdf:Other' srsName='EPSG:XYZ' handle='myHandle'> ") + "<fes:Filter>") + "<fes:PropertyIsEqualTo>") + "<fes:ValueReference>foobar</fes:ValueReference>") + "<fes:Literal>xyz</fes:Literal>") + "</fes:PropertyIsEqualTo>") + "</fes:Filter>") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("myHandle", "//ows:Exception/@locator", dom);
    }

    @Test
    public void testBogusTypeNames() throws Exception {
        String xml = "<wfs:GetFeature service='WFS' version='2.0.0' " + ((((("xmlns:cdf='http://www.opengis.net/cite/data' " + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='foobbar'> ") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("InvalidParameterValue", "//ows:Exception/@exceptionCode", dom);
        XMLAssert.assertXpathEvaluatesTo("typeName", "//ows:Exception/@locator", dom);
        dom = getAsDOM("wfs?service=WFS&version=2.0.0&request=getFeature&typeNames=foobar");
        XMLAssert.assertXpathEvaluatesTo("InvalidParameterValue", "//ows:Exception/@exceptionCode", dom);
        XMLAssert.assertXpathEvaluatesTo("typeName", "//ows:Exception/@locator", dom);
    }

    @Test
    public void testInvalidRequest() throws Exception {
        String xml = "<wfs:GetFeature service='WFS' version='2.0.0' " + (((((((((((("xmlns:cdf='http://www.opengis.net/cite/data' " + "xmlns:fes='http://www.opengis.net/fes/2.0' ") + "xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "> ") + "<wfs:Query typeNames='cdf:Other' srsName='EPSG:XYZ' handle='myHandle'> ") + "<fes:Filter>") + "<fes:PropertyIsEqualTo>") + "<fes:ValueReference>foobar</fes:ValueReference>") + "<fes:Literal>xyz</fes:Literal>") + "</fes:PropertyIsEqualTo>") + "</fes:foo>") + "</wfs:Query> ") + "</wfs:GetFeature>");
        Document dom = postAsDOM("wfs", xml);
        XMLAssert.assertXpathEvaluatesTo("OperationParsingFailed", "//ows:Exception/@exceptionCode", dom);
    }

    @Test
    public void testWfs11AndGML32() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&typeName=cdf:Fifteen&version=1.1.0&service=wfs&featureid=Fifteen.2&outputFormat=gml32");
        // print(doc);
        assertGML32(doc);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:FeatureCollection/wfs:member/cdf:Fifteen)", doc);
        XMLAssert.assertXpathEvaluatesTo("Fifteen.2", "//wfs:FeatureCollection/wfs:member/cdf:Fifteen/@gml:id", doc);
    }

    @Test
    public void testGml32MimeType() throws Exception {
        // test GET request
        String url = "wfs?request=GetFeature&typeName=cdf:Fifteen&version=2.0" + "&service=wfs&featureid=Fifteen.2&outputFormat=gml32";
        MockHttpServletResponse response = getAsServletResponse(url);
        Assert.assertThat(response.getContentType(), CoreMatchers.is("application/gml+xml; version=3.2"));
        // override GML 3.2 MIME type with text / xml
        setGmlMimeTypeOverride("text/xml");
        response = getAsServletResponse(url);
        Assert.assertThat(response.getContentType(), CoreMatchers.is("text/xml"));
        setGmlMimeTypeOverride(null);
        // test POST request
        String xml = "<wfs:GetFeature service='WFS' version='2.0.0'" + ((((("                xmlns:cdf='http://www.opengis.net/cite/data'" + "                xmlns:wfs='http://www.opengis.net/wfs/2.0'>") + "    <wfs:Query typeNames='cdf:Other'>") + "        <wfs:PropertyName>cdf:string2</wfs:PropertyName>") + "    </wfs:Query>") + "</wfs:GetFeature>");
        response = postAsServletResponse("wfs", xml);
        Assert.assertThat(response.getContentType(), CoreMatchers.is("application/gml+xml; version=3.2"));
        // override GML 3.2 MIME type with text / xml
        setGmlMimeTypeOverride("text/xml");
        response = postAsServletResponse("wfs", xml);
        Assert.assertThat(response.getContentType(), CoreMatchers.is("text/xml"));
    }

    /**
     * Test that a Filter 2.0 {@code fes:PropertyIsLike} without {@code matchCase} matches zero
     * Buildings.
     */
    @Test
    public void testPropertyIsLikeWithoutMatchCase() throws Exception {
        checkPropertyIsLikeMatchCase(null, 0);
    }

    /**
     * Test that a Filter 2.0 {@code fes:PropertyIsLike} with {@code matchCase="true"} matches zero
     * Buildings.
     */
    @Test
    public void testPropertyIsLikeMatchCaseTrue() throws Exception {
        checkPropertyIsLikeMatchCase(true, 0);
    }

    /**
     * Test that a Filter 2.0 {@code fes:PropertyIsLike} with {@code matchCase="false"} matches two
     * Buildings.
     */
    @Test
    public void testPropertyIsLikeMatchCaseFalse() throws Exception {
        checkPropertyIsLikeMatchCase(false, 2);
    }

    @Test
    public void testNumberOfDecimals() throws Exception {
        int oldDecimals = setDecimals(NAMED_PLACES, 3);
        try {
            Document doc = getAsDOM("wfs?request=GetFeature&typeName=cite:NamedPlaces&version=2.0.0&service=wfs&featureId=NamedPlaces.1107531895891");
            // print(doc);
            XMLAssert.assertXpathEvaluatesTo("-0.001 0.002 -0.001 0.002 -0.001 0.003 -0.001 0.003 -0.001 0.002", "//gml:posList", doc);
            setDecimals(NAMED_PLACES, 4);
            doc = getAsDOM("wfs?request=GetFeature&typeName=cite:NamedPlaces&version=2.0.0&service=wfs&featureId=NamedPlaces.1107531895891");
            // print(doc);
            XMLAssert.assertXpathEvaluatesTo("-0.0011 0.0017 -6.0E-4 0.0017 -6.0E-4 0.0025 -0.0011 0.0025 -0.0011 0.0017", "//gml:posList", doc);
        } finally {
            setDecimals(NAMED_PLACES, oldDecimals);
        }
    }
}

