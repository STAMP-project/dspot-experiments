/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw;


import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import net.opengis.cat.csw20.DescribeRecordType;
import org.apache.commons.io.IOUtils;
import org.geoserver.csw.kvp.DescribeRecordKvpRequestReader;
import org.geoserver.csw.xml.v2_0_2.CSWXmlReader;
import org.geoserver.util.EntityResolverProvider;
import org.geotools.csw.CSWConfiguration;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class DescribeRecordTest extends CSWSimpleTestSupport {
    @Test
    public void testKVPReaderNS() throws Exception {
        Map<String, Object> raw = new HashMap<String, Object>();
        raw.put("service", "CSW");
        raw.put("version", "2.0.2");
        raw.put("request", "DescribeRecord");
        raw.put("namespace", "xmlns(csw=http://www.opengis.net/cat/csw/2.0.2),xmlns(rim=urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0)");
        raw.put("typename", "csw:Record,rim:RegistryPackage");
        raw.put("schemalanguage", "XMLSCHEMA");
        raw.put("outputFormat", "application/xml");
        DescribeRecordKvpRequestReader reader = new DescribeRecordKvpRequestReader();
        Object request = reader.createRequest();
        DescribeRecordType dr = ((DescribeRecordType) (reader.read(request, parseKvp(raw), raw)));
        assertDescribeRecordValid(dr);
    }

    @Test
    public void testKVPReaderNoNamespace() throws Exception {
        Map<String, Object> raw = new HashMap<String, Object>();
        raw.put("service", "CSW");
        raw.put("version", "2.0.2");
        raw.put("request", "DescribeRecord");
        raw.put("typename", "csw:Record,rim:RegistryPackage");
        raw.put("schemalanguage", "XMLSCHEMA");
        raw.put("outputFormat", "application/xml");
        DescribeRecordKvpRequestReader reader = new DescribeRecordKvpRequestReader();
        Object request = reader.createRequest();
        DescribeRecordType dr = ((DescribeRecordType) (reader.read(request, parseKvp(raw), raw)));
        assertDescribeRecordValid(dr);
    }

    @Test
    public void testKVPReaderDefaultNamespace() throws Exception {
        Map<String, Object> raw = new HashMap<String, Object>();
        raw.put("service", "CSW");
        raw.put("version", "2.0.2");
        raw.put("request", "DescribeRecord");
        raw.put("namespace", "xmlns(=http://www.opengis.net/cat/csw/2.0.2),xmlns(rim=urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0)");
        raw.put("typename", "Record,rim:RegistryPackage");
        raw.put("schemalanguage", "XMLSCHEMA");
        raw.put("outputFormat", "application/xml");
        DescribeRecordKvpRequestReader reader = new DescribeRecordKvpRequestReader();
        Object request = reader.createRequest();
        DescribeRecordType dr = ((DescribeRecordType) (reader.read(request, parseKvp(raw), raw)));
        assertDescribeRecordValid(dr);
    }

    @Test
    public void testXMLReader() throws Exception {
        CSWXmlReader reader = new CSWXmlReader("DescribeRecord", "2.0.2", new CSWConfiguration(), EntityResolverProvider.RESOLVE_DISABLED_PROVIDER);
        DescribeRecordType dr = ((DescribeRecordType) (reader.read(null, getResourceAsReader("DescribeRecord.xml"), ((Map) (null)))));
        assertDescribeRecordValid(dr);
    }

    // this is one of the CITE tests, unknown type names should just be ignored
    @Test
    public void testDummyRecord() throws Exception {
        Document dom = getAsDOM("csw?service=CSW&version=2.0.2&request=DescribeRecord&typeName=csw:DummyRecord");
        checkValidationErrors(dom);
        print(dom);
        assertXpathEvaluatesTo("1", "count(/csw:DescribeRecordResponse)", dom);
        assertXpathEvaluatesTo("0", "count(//csw:SchemaComponent)", dom);
    }

    @Test
    public void testBasicGetLocalSchema() throws Exception {
        Document dom = getAsDOM("csw?service=CSW&version=2.0.2&request=DescribeRecord");
        checkValidationErrors(dom);
        print(dom);
        assertCswRecordSchema(dom, false);
        // check we can really read those schemas
        MockHttpServletResponse response = getAsServletResponse("/schemas/csw/2.0.2/rec-dcterms.xsd");
        Assert.assertEquals(200, response.getStatus());
        dom = dom(new java.io.ByteArrayInputStream(response.getContentAsString().getBytes("UTF-8")));
        assertXpathEvaluatesTo("dc:SimpleLiteral", "//xs:element[@name='abstract']/@type", dom);
    }

    @Test
    public void testBasicGetCanonicalSchema() throws Exception {
        try {
            CSWInfo csw = getGeoServer().getService(CSWInfo.class);
            csw.setCanonicalSchemaLocation(true);
            getGeoServer().save(csw);
            Document dom = getAsDOM("csw?service=CSW&version=2.0.2&request=DescribeRecord");
            checkValidationErrors(dom);
            // print(dom);
            assertCswRecordSchema(dom, true);
        } finally {
            CSWInfo csw = getGeoServer().getService(CSWInfo.class);
            csw.setCanonicalSchemaLocation(false);
            getGeoServer().save(csw);
        }
    }

    @Test
    public void testBasicPost() throws Exception {
        String request = IOUtils.toString(getResourceAsReader("DescribeCswRecord.xml"));
        Document dom = postAsDOM("csw", request);
        checkValidationErrors(dom);
        // print(dom);
        assertCswRecordSchema(dom, false);
    }

    @Test
    public void testAlternativeNamespacePrefix() throws Exception {
        Document dom = getAsDOM("csw?service=CSW&version=2.0.2&request=DescribeRecord&typeName=fuffa:Record&namespace=xmlns(fuffa=http://www.opengis.net/cat/csw/2.0.2)");
        assertCswRecordSchema(dom, false);
    }

    @Test
    public void testDefaultNamespacePrefix() throws Exception {
        Document dom = getAsDOM("csw?service=CSW&version=2.0.2&request=DescribeRecord&typeName=Record&namespace=xmlns(=http://www.opengis.net/cat/csw/2.0.2)");
        // print(dom);
        assertCswRecordSchema(dom, false);
    }

    @Test
    public void testMissingOutputFormat() throws Exception {
        Document dom = getAsDOM("csw?service=CSW&version=2.0.2&request=DescribeRecord&outputFormat=text/sgml");
        checkOws10Exception(dom, ServiceException.INVALID_PARAMETER_VALUE, "outputFormat");
    }

    @Test
    public void testInvalidSchemaLanguage() throws Exception {
        Document dom = getAsDOM("csw?service=CSW&version=2.0.2&request=DescribeRecord&schemaLanguage=http://purl.oclc.org/dsdl/schematron");
        checkOws10Exception(dom, ServiceException.INVALID_PARAMETER_VALUE, "schemaLanguage");
    }
}

