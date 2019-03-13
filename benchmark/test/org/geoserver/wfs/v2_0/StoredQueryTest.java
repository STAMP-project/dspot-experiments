/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v2_0;


import StoredQuery.DEFAULT;
import java.io.ByteArrayInputStream;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.data.test.MockData;
import org.geoserver.wfs.xml.FeatureTypeSchemaBuilder;
import org.geoserver.wfs.xml.v1_1_0.WFSConfiguration;
import org.geotools.filter.v2_0.FES;
import org.geotools.gml3.v3_2.GML;
import org.geotools.wfs.v2_0.WFS;
import org.geotools.xsd.Parser;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class StoredQueryTest extends WFS20TestSupport {
    @Test
    public void testListStoredQueries() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("wfs?request=ListStoredQueries&service=wfs&version=2.0.0");
        Document dom = dom(new ByteArrayInputStream(response.getContentAsByteArray()));
        XMLAssert.assertXpathExists((("//wfs:StoredQuery[@id = '" + (DEFAULT.getName())) + "']"), dom);
        // schema validate the response
        FeatureTypeSchemaBuilder sb = new FeatureTypeSchemaBuilder.GML3(getGeoServer());
        WFSConfiguration configuration = new WFSConfiguration(getGeoServer(), sb, new org.geoserver.wfs.xml.v1_1_0.WFS(sb));
        Parser parser = new Parser(configuration);
        parser.parse(new ByteArrayInputStream(response.getContentAsByteArray()));
        Assert.assertEquals(0, parser.getValidationErrors().size());
    }

    @Test
    public void testListStoredQueries2() throws Exception {
        testCreateStoredQuery();
        Document dom = getAsDOM("wfs?request=ListStoredQueries&service=wfs&version=2.0.0");
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:StoredQuery)", dom);
        XMLAssert.assertXpathExists((("//wfs:StoredQuery[@id = '" + (DEFAULT.getName())) + "']"), dom);
        XMLAssert.assertXpathExists("//wfs:StoredQuery[@id = 'myStoredQuery']", dom);
    }

    @Test
    public void testCreateUnknownLanguage() throws Exception {
        String xml = "<CreateStoredQuery xmlns=\"http://www.opengis.net/wfs/2.0\" service=\"WFS\" " + ((((((((((((("version=\"2.0.0\">\n" + "  <StoredQueryDefinition xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n") + "                          id=\"urn:example:wfs2-query:InvalidLang\">\n") + "      <Title>GetFeatureByTypeName</Title>\n") + "      <Abstract>Returns feature representations by type name.</Abstract>\n") + "      <Parameter name=\"typeName\" type=\"xsd:QName\">\n") + "         <Abstract>Qualified name of feature type (required).</Abstract>\n") + "      </Parameter>\n") + "      <QueryExpressionText isPrivate=\"false\" language=\"http://qry.example.org\" ") + "returnFeatureTypes=\"\">\n") + "         <Query typeNames=\"${typeName}\"/>\n") + "      </QueryExpressionText>\n") + "  </StoredQueryDefinition>\n") + "</CreateStoredQuery>");
        MockHttpServletResponse response = postAsServletResponse("wfs", xml);
        Assert.assertEquals(400, response.getStatus());
        Document dom = dom(new ByteArrayInputStream(response.getContentAsByteArray()));
        checkOws11Exception(dom, "2.0.0", ServiceException.INVALID_PARAMETER_VALUE, "language");
    }

    @Test
    public void testCreateStoredQuery() throws Exception {
        String xml = (("<wfs:ListStoredQueries service='WFS' version='2.0.0' " + " xmlns:wfs='") + (WFS.NAMESPACE)) + "'/>";
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        Assert.assertEquals("wfs:ListStoredQueriesResponse", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:StoredQuery)", dom);
        xml = getCreatePrimitiveWithinQuery(false);
        dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:CreateStoredQueryResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("OK", dom.getDocumentElement().getAttribute("status"));
        dom = getAsDOM("wfs?request=ListStoredQueries");
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:StoredQuery)", dom);
        XMLAssert.assertXpathExists("//wfs:StoredQuery[@id = 'myStoredQuery']", dom);
        XMLAssert.assertXpathExists("//wfs:ReturnFeatureType[text() = 'sf:PrimitiveGeoFeature']", dom);
    }

    @Test
    public void testDuplicateStoredQuery() throws Exception {
        String xml = getCreatePrimitiveWithinQuery(false);
        Document dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:CreateStoredQueryResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("OK", dom.getDocumentElement().getAttribute("status"));
        MockHttpServletResponse response = postAsServletResponse("wfs", xml);
        Assert.assertEquals(400, response.getStatus());
        dom = dom(new ByteArrayInputStream(response.getContentAsByteArray()));
        checkOws11Exception(dom, "2.0.0", WFSException.DUPLICATE_STORED_QUERY_ID_VALUE, "myStoredQuery");
    }

    @Test
    public void testCreateStoredQueryMismatchingTypes() throws Exception {
        String xml = (("<wfs:ListStoredQueries service='WFS' version='2.0.0' " + " xmlns:wfs='") + (WFS.NAMESPACE)) + "'/>";
        Document dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:ListStoredQueriesResponse", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:StoredQuery)", dom);
        xml = ((((((((((((((((((("<wfs:CreateStoredQuery service='WFS' version='2.0.0' " + ((("   xmlns:wfs='http://www.opengis.net/wfs/2.0' " + "   xmlns:fes='http://www.opengis.net/fes/2.0' ") + "   xmlns:gml='http://www.opengis.net/gml/3.2' ") + "   xmlns:sf='")) + (MockData.SF_URI)) + "'>") + "   <wfs:StoredQueryDefinition id='myStoredQuery'> ") + "      <wfs:Parameter name='AreaOfInterest' type='gml:Polygon'/> ") + "      <wfs:QueryExpressionText ") + "           returnFeatureTypes='sf:PrimitiveGeoFeature' ") + "           language='urn:ogc:def:queryLanguage:OGC-WFS::WFS_QueryExpression' ") + "           isPrivate='false'> ") + "         <wfs:Query typeNames='sf:AggregateGeoFeature'> ") + "            <fes:Filter> ") + "               <fes:Within> ") + "                  <fes:ValueReference>pointProperty</fes:ValueReference> ") + "                  ${AreaOfInterest} ") + "               </fes:Within> ") + "            </fes:Filter> ") + "         </wfs:Query> ") + "      </wfs:QueryExpressionText> ") + "   </wfs:StoredQueryDefinition> ") + "</wfs:CreateStoredQuery>";
        dom = postAsDOM("wfs", xml);
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        dom = getAsDOM("wfs?request=ListStoredQueries");
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:StoredQuery)", dom);
    }

    @Test
    public void testDescribeStoredQueries() throws Exception {
        Document dom = getAsDOM("wfs?request=DescribeStoredQueries&storedQueryId=myStoredQuery", 400);
        checkOws11Exception(dom, "2.0.0", ServiceException.INVALID_PARAMETER_VALUE, "STOREDQUERY_ID");
        testCreateStoredQuery();
        String xml = ((("<wfs:DescribeStoredQueries xmlns:wfs='" + (WFS.NAMESPACE)) + "' service='WFS'>") + "<wfs:StoredQueryId>myStoredQuery</wfs:StoredQueryId>") + "</wfs:DescribeStoredQueries>";
        dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:DescribeStoredQueriesResponse", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathExists("//wfs:StoredQueryDescription[@id='myStoredQuery']", dom);
    }

    @Test
    public void testDescribeStoredQueries2() throws Exception {
        Document dom = getAsDOM("wfs?request=DescribeStoredQueries&storedQuery_Id=myStoredQuery");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        testCreateStoredQuery();
        dom = getAsDOM("wfs?request=DescribeStoredQueries&storedQuery_Id=myStoredQuery");
        Assert.assertEquals("wfs:DescribeStoredQueriesResponse", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathExists("//wfs:StoredQueryDescription[@id='myStoredQuery']", dom);
    }

    @Test
    public void testDescribeDefaultStoredQuery() throws Exception {
        Document dom = getAsDOM(("wfs?request=DescribeStoredQueries&storedQueryId=" + (DEFAULT.getName())));
        Assert.assertEquals("wfs:DescribeStoredQueriesResponse", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathExists((("//wfs:StoredQueryDescription[@id = '" + (DEFAULT.getName())) + "']"), dom);
        XMLAssert.assertXpathExists("//wfs:Parameter[@name = 'ID']", dom);
        XMLAssert.assertXpathExists("//wfs:QueryExpressionText[@isPrivate = 'true']", dom);
        XMLAssert.assertXpathNotExists("//wfs:QueryExpressionText/*", dom);
    }

    @Test
    public void testDropStoredQuery() throws Exception {
        Document dom = getAsDOM("wfs?request=DropStoredQuery&id=myStoredQuery");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        testCreateStoredQuery();
        String xml = ("<wfs:DropStoredQuery xmlns:wfs='" + (WFS.NAMESPACE)) + "' service='WFS' id='myStoredQuery'/>";
        dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:DropStoredQueryResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("OK", dom.getDocumentElement().getAttribute("status"));
        dom = getAsDOM("wfs?request=DropStoredQuery&id=myStoredQuery");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testDropStoredQuery2() throws Exception {
        Document dom = getAsDOM("wfs?request=DropStoredQuery&storedQuery_id=myStoredQuery");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        testCreateStoredQuery();
        dom = getAsDOM("wfs?request=DropStoredQuery&storedQuery_id=myStoredQuery");
        Assert.assertEquals("wfs:DropStoredQueryResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("OK", dom.getDocumentElement().getAttribute("status"));
        dom = getAsDOM("wfs?request=DropStoredQuery&storedQuery_id=myStoredQuery");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
    }

    @Test
    public void testCreateStoredQuerySOAP() throws Exception {
        String xml = ((((((((((((((((((((("<soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope'> " + (((((((" <soap:Header/> " + " <soap:Body>") + "<wfs:CreateStoredQuery service='WFS' version='2.0.0' ") + "   xmlns:wfs='http://www.opengis.net/wfs/2.0' ") + "   xmlns:fes='http://www.opengis.net/fes/2.0' ") + "   xmlns:gml='http://www.opengis.net/gml/3.2' ") + "   xmlns:myns='http://www.someserver.com/myns' ") + "   xmlns:sf='")) + (MockData.SF_URI)) + "'>") + "   <wfs:StoredQueryDefinition id='myStoredQuery'> ") + "      <wfs:Parameter name='AreaOfInterest' type='gml:Polygon'/> ") + "      <wfs:QueryExpressionText ") + "           returnFeatureTypes='sf:PrimitiveGeoFeature' ") + "           language='urn:ogc:def:queryLanguage:OGC-WFS::WFS_QueryExpression' ") + "           isPrivate='false'> ") + "         <wfs:Query typeNames='sf:PrimitiveGeoFeature'> ") + "            <fes:Filter> ") + "               <fes:Within> ") + "                  <fes:ValueReference>pointProperty</fes:ValueReference> ") + "                  ${AreaOfInterest} ") + "               </fes:Within> ") + "            </fes:Filter> ") + "         </wfs:Query> ") + "      </wfs:QueryExpressionText> ") + "   </wfs:StoredQueryDefinition> ") + "</wfs:CreateStoredQuery>") + " </soap:Body> ") + "</soap:Envelope> ";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/soap+xml");
        Assert.assertEquals("application/soap+xml", resp.getContentType());
        Document dom = dom(new ByteArrayInputStream(resp.getContentAsString().getBytes()));
        Assert.assertEquals("soap:Envelope", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(1, dom.getElementsByTagName("wfs:CreateStoredQueryResponse").getLength());
    }

    @Test
    public void testDescribeStoredQueriesSOAP() throws Exception {
        testCreateStoredQuery();
        String xml = (((((("<soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope'> " + ((" <soap:Header/> " + " <soap:Body>") + "<wfs:DescribeStoredQueries xmlns:wfs='")) + (WFS.NAMESPACE)) + "' service='WFS'>") + "<wfs:StoredQueryId>myStoredQuery</wfs:StoredQueryId>") + "</wfs:DescribeStoredQueries>") + " </soap:Body> ") + "</soap:Envelope> ";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/soap+xml");
        Assert.assertEquals("application/soap+xml", resp.getContentType());
        Document dom = dom(new ByteArrayInputStream(resp.getContentAsString().getBytes()));
        Assert.assertEquals("soap:Envelope", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(1, dom.getElementsByTagName("wfs:DescribeStoredQueriesResponse").getLength());
    }

    @Test
    public void testListStoredQueriesSOAP() throws Exception {
        testCreateStoredQuery();
        String xml = (((("<soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope'> " + (((" <soap:Header/> " + " <soap:Body>") + "<wfs:ListStoredQueries service='WFS' version='2.0.0' ") + " xmlns:wfs='")) + (WFS.NAMESPACE)) + "'/>") + " </soap:Body> ") + "</soap:Envelope> ";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/soap+xml");
        Assert.assertEquals("application/soap+xml", resp.getContentType());
        Document dom = dom(new ByteArrayInputStream(resp.getContentAsString().getBytes()));
        Assert.assertEquals("soap:Envelope", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(1, dom.getElementsByTagName("wfs:ListStoredQueriesResponse").getLength());
    }

    @Test
    public void testDropStoredQuerySOAP() throws Exception {
        testCreateStoredQuery();
        String xml = (((("<soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope'> " + (((" <soap:Header/> " + " <soap:Body>") + "<wfs:DropStoredQuery service='WFS' version='2.0.0' ") + " xmlns:wfs='")) + (WFS.NAMESPACE)) + "' id='myStoredQuery'/>") + " </soap:Body> ") + "</soap:Envelope> ";
        MockHttpServletResponse resp = postAsServletResponse("wfs", xml, "application/soap+xml");
        Assert.assertEquals("application/soap+xml", resp.getContentType());
        Document dom = dom(new ByteArrayInputStream(resp.getContentAsString().getBytes()));
        Assert.assertEquals("soap:Envelope", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(1, dom.getElementsByTagName("wfs:DropStoredQueryResponse").getLength());
    }

    @Test
    public void testDropUnknownStoredQuery() throws Exception {
        Document dom = getAsDOM("wfs?request=DropStoredQuery&storedQuery_Id=myStoredQuery", 400);
        checkOws11Exception(dom, "2.0.0", ServiceException.INVALID_PARAMETER_VALUE, "id");
    }

    @Test
    public void testCreateParametrizedOnTypename() throws Exception {
        // this evil thing comes from the CITE tests
        String xml = "<CreateStoredQuery xmlns=\"http://www.opengis.net/wfs/2.0\" service=\"WFS\" version=\"2.0.0\">\n" + (((((((((((((("  <StoredQueryDefinition xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" + "                          id=\"urn:example:wfs2-query:GetFeatureByTypeName\">\n") + "      <Title>GetFeatureByTypeName</Title>\n") + "      <Abstract>Returns feature representations by type name.</Abstract>\n") + "      <Parameter name=\"typeName\" type=\"xsd:QName\">\n") + "         <Abstract>Qualified name of feature type (required).</Abstract>\n") + "      </Parameter>\n") + "      <QueryExpressionText isPrivate=\"false\"\n") + "                           ") + "language=\"urn:ogc:def:queryLanguage:OGC-WFS::WFSQueryExpression\"\n") + "                           returnFeatureTypes=\"\">\n") + "         <Query typeNames=\"${typeName}\"/>\n") + "      </QueryExpressionText>\n") + "</StoredQueryDefinition>\n") + "</CreateStoredQuery>");
        // create
        Document dom = postAsDOM("wfs", xml);
        Assert.assertEquals("wfs:CreateStoredQueryResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("OK", dom.getDocumentElement().getAttribute("status"));
        // verify exists
        dom = getAsDOM("wfs?request=ListStoredQueries");
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:StoredQuery)", dom);
        XMLAssert.assertXpathExists("//wfs:StoredQuery[@id = 'urn:example:wfs2-query:GetFeatureByTypeName']", dom);
        // run it (the cite tests use a random prefix
        dom = getAsDOM(("wfs?service=WFS&version=2.0.0&request=GetFeature&storedQuery_id=urn:example:wfs2-query" + ":GetFeatureByTypeName&typename=tns:Fifteen"), 200);
        // print(dom);
        XMLAssert.assertXpathExists("/wfs:FeatureCollection", dom);
        XMLAssert.assertXpathEvaluatesTo("15", "count(//cdf:Fifteen)", dom);
    }

    @Test
    public void testCreateWithLocalNamespaceDeclaration() throws Exception {
        String xml = "<CreateStoredQuery xmlns=\"http://www.opengis.net/wfs/2.0\" service=\"WFS\" " + ((((((((((((((((((((((((("version=\"2.0.0\">\n" + "  <StoredQueryDefinition xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n") + "                          id=\"urn:example:wfs2-query:GetFeatureByName\">\n") + "      <Title>GetFeatureByName</Title>\n") + "      <Abstract>Returns feature representations by name. The name value must occur in a gml:name ") + "property.</Abstract>\n") + "      <Parameter name=\"name\" type=\"xsd:string\">\n") + "         <Abstract>Name of feature instance (required)</Abstract>\n") + "      </Parameter>\n") + "      <QueryExpressionText xmlns:fes=\"http://www.opengis.net/fes/2.0\"\n") + "                           xmlns:gml=\"http://www.opengis.net/gml/3.2\"\n") + "                           xmlns:ns42=\"http://cite.opengeospatial.org/gmlsf\"\n") + "                           isPrivate=\"false\"\n") + "                           language=\"urn:ogc:def:queryLanguage:OGC-WFS::WFSQueryExpression\"\n") + "                           returnFeatureTypes=\"ns42:Entit\u00e9G\u00e9n\u00e9rique\">\n") + "         <Query typeNames=\"ns42:Entit\u00e9G\u00e9n\u00e9rique\">\n") + "            <fes:Filter>\n") + "               <fes:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">\n") + "                  <fes:ValueReference>gml:name</fes:ValueReference>\n") + "                  <fes:Literal>*${name}*</fes:Literal>\n") + "               </fes:PropertyIsLike>\n") + "            </fes:Filter>\n") + "         </Query>\n") + "      </QueryExpressionText>\n") + "  </StoredQueryDefinition>\n") + "</CreateStoredQuery>");
        // create
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        Assert.assertEquals("wfs:CreateStoredQueryResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("OK", dom.getDocumentElement().getAttribute("status"));
        // verify exists
        dom = getAsDOM("wfs?request=ListStoredQueries");
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:StoredQuery)", dom);
        XMLAssert.assertXpathExists("//wfs:StoredQuery[@id = 'urn:example:wfs2-query:GetFeatureByName']", dom);
    }

    @Test
    public void testCreateStoredQueryWithEmptyReturnFeatureTypes() throws Exception {
        String xml = (("<wfs:ListStoredQueries service='WFS' version='2.0.0' " + " xmlns:wfs='") + (WFS.NAMESPACE)) + "'/>";
        Document dom = postAsDOM("wfs", xml);
        // print(dom);
        Assert.assertEquals("wfs:ListStoredQueriesResponse", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//wfs:StoredQuery)", dom);
        xml = getCreatePrimitiveWithinQuery(true);
        dom = postAsDOM("wfs", xml);
        // print(dom);
        Assert.assertEquals("wfs:CreateStoredQueryResponse", dom.getDocumentElement().getNodeName());
        Assert.assertEquals("OK", dom.getDocumentElement().getAttribute("status"));
        // verify it can be listed
        dom = getAsDOM("wfs?request=ListStoredQueries");
        // print(dom);
        XMLAssert.assertXpathEvaluatesTo("2", "count(//wfs:StoredQuery)", dom);
        XMLAssert.assertXpathExists("//wfs:StoredQuery[@id = 'myStoredQuery']", dom);
        XMLAssert.assertXpathExists("//wfs:ReturnFeatureType[text() = 'sf:PrimitiveGeoFeature']", dom);
        // verify it describes correctly
        xml = ((("<wfs:DescribeStoredQueries xmlns:wfs='" + (WFS.NAMESPACE)) + "' service='WFS'>") + "<wfs:StoredQueryId>myStoredQuery</wfs:StoredQueryId>") + "</wfs:DescribeStoredQueries>";
        dom = postAsDOM("wfs", xml);
        // print(dom);
        Assert.assertEquals("wfs:DescribeStoredQueriesResponse", dom.getDocumentElement().getNodeName());
        XMLAssert.assertXpathExists("//wfs:StoredQueryDescription[@id='myStoredQuery']", dom);
        XMLAssert.assertXpathExists("//wfs:QueryExpressionText/@returnFeatureTypes", dom);
        XMLAssert.assertXpathEvaluatesTo("", "//wfs:QueryExpressionText/@returnFeatureTypes", dom);
        // verify it can be run
        xml = ((((((((((((((("<wfs:GetFeature service='WFS' version='2.0.0' xmlns:gml='" + (GML.NAMESPACE)) + "'") + "       xmlns:wfs='") + (WFS.NAMESPACE)) + "' xmlns:fes='") + (FES.NAMESPACE)) + "'>") + "   <wfs:StoredQuery id='myStoredQuery'> ") + "      <wfs:Parameter name='AreaOfInterest'>") + "   <gml:Envelope srsName='EPSG:4326'>") + "      <gml:lowerCorner>57.0 -4.5</gml:lowerCorner>") + "      <gml:upperCorner>62.0 1.0</gml:upperCorner>") + "   </gml:Envelope>") + "      </wfs:Parameter> ") + "   </wfs:StoredQuery> ") + "</wfs:GetFeature>";
        dom = postAsDOM("wfs", xml);
        // print(dom);
        assertGML32(dom);
        XMLAssert.assertXpathEvaluatesTo("1", "count(//sf:PrimitiveGeoFeature)", dom);
        XMLAssert.assertXpathExists("//sf:PrimitiveGeoFeature/gml:name[text() = 'name-f002']", dom);
    }

    @Test
    public void testCreateStoredQueryXXE() throws Exception {
        String xml = (((((((((((((((((((((("<wfs:CreateStoredQuery service='WFS' version='2.0.0' " + (((("   xmlns:wfs='http://www.opengis.net/wfs/2.0' " + "   xmlns:fes='http://www.opengis.net/fes/2.0' ") + "   xmlns:gml='http://www.opengis.net/gml/3.2' ") + "   xmlns:myns='http://www.someserver.com/myns' ") + "   xmlns:sf='")) + (MockData.SF_URI)) + "'>") + "   <wfs:StoredQueryDefinition id='testXXE'> ") + "      <wfs:Parameter name='AreaOfInterest' type='gml:Polygon'/> ") + "      <wfs:QueryExpressionText ") + "           returnFeatureTypes='sf:PrimitiveGeoFeature' ") + "           language='urn:ogc:def:queryLanguage:OGC-WFS::WFS_QueryExpression' ") + "           isPrivate='false'><![CDATA[<?xml version='1.0' encoding='UTF-8'?> ") + "         <!DOCTYPE Query [ ") + "         <!ELEMENT Query ANY> ") + "         <!ENTITY xxe SYSTEM \"file:///this/file/does/not/exist\">]> ") + "         <wfs:Query typeNames='sf:PrimitiveGeoFeature'> ") + "            <fes:Filter> ") + "               <fes:Within> ") + "                  <fes:ValueReference>&xxe;</fes:ValueReference> ") + "                  ${AreaOfInterest} ") + "               </fes:Within> ") + "            </fes:Filter> ") + "         </wfs:Query>]]> ") + "      </wfs:QueryExpressionText> ") + "   </wfs:StoredQueryDefinition> ") + "</wfs:CreateStoredQuery>";
        Document dom = postAsDOM("wfs", xml);
        String message = checkOws11Exception(dom, "2.0.0", "OperationProcessingFailed", "CreateStoredQuery");
        Assert.assertThat(message, CoreMatchers.containsString("Entity resolution disallowed"));
    }
}

