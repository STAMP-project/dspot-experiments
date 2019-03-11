/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v1_1;


import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.util.ReaderUtils;
import org.geoserver.wfs.WFSTestSupport;
import org.geoserver.wfs.xml.v1_1_0.XmlSchemaEncoder;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class DescribeFeatureResponseTest extends WFSTestSupport {
    @Test
    public void testSingle() throws Exception {
        FeatureTypeInfo meta = getFeatureTypeInfo(CiteTestData.BASIC_POLYGONS);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmlSchemaEncoder response = new XmlSchemaEncoder.V11(getGeoServer());
        response.write(new FeatureTypeInfo[]{ meta }, output, request());
        Element schema = ReaderUtils.parse(new StringReader(new String(output.toByteArray())));
        Assert.assertEquals("xsd:schema", schema.getNodeName());
        NodeList types = schema.getElementsByTagName("xsd:complexType");
        Assert.assertEquals(1, types.getLength());
    }

    @Test
    public void testWithDifferntNamespaces() throws Exception {
        FeatureTypeInfo meta1 = getFeatureTypeInfo(CiteTestData.BASIC_POLYGONS);
        FeatureTypeInfo meta2 = getFeatureTypeInfo(CiteTestData.POLYGONS);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmlSchemaEncoder response = new XmlSchemaEncoder.V11(getGeoServer());
        response.write(new FeatureTypeInfo[]{ meta1, meta2 }, output, request());
        Element schema = ReaderUtils.parse(new StringReader(new String(output.toByteArray())));
        Assert.assertEquals("xsd:schema", schema.getNodeName());
        NodeList imprts = schema.getElementsByTagName("xsd:import");
        Assert.assertEquals(2, imprts.getLength());
    }
}

