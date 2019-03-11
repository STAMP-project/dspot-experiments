/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.xml;


import GML.GEOMETRYASSOCIATIONTYPE;
import GML.LINESTRINGPROPERTYTYPE;
import GML.NAMESPACE;
import SystemTestData.LINES;
import javax.xml.namespace.QName;
import org.custommonkey.xmlunit.XpathEngine;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.wfs.WFSTestSupport;
import org.geotools.xsd.Schemas;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class FeatureTypeInfoSchemaBuilderTest extends WFSTestSupport {
    protected QName UUID_TEST = new QName(MockData.CITE_URI, "uuid", MockData.CITE_PREFIX);

    @Test
    public void testBuildGml2() throws Exception {
        FeatureTypeSchemaBuilder builder = new FeatureTypeSchemaBuilder.GML2(getGeoServer());
        FeatureTypeInfo lines = getFeatureTypeInfo(LINES);
        XSDSchema schema = builder.build(new FeatureTypeInfo[]{ lines }, null);
        Assert.assertNotNull(schema);
        XSDElementDeclaration element = Schemas.getElementDeclaration(schema, LINES);
        Assert.assertNotNull(element);
        Assert.assertTrue(((element.getType()) instanceof XSDComplexTypeDefinition));
        XSDElementDeclaration id = Schemas.getChildElementDeclaration(element, new QName(SystemTestData.CGF_URI, "id"));
        Assert.assertNotNull(id);
        XSDElementDeclaration lineStringProperty = Schemas.getChildElementDeclaration(element, new QName(SystemTestData.CGF_URI, "lineStringProperty"));
        Assert.assertNotNull(lineStringProperty);
        XSDTypeDefinition lineStringPropertyType = lineStringProperty.getType();
        Assert.assertEquals(NAMESPACE, lineStringPropertyType.getTargetNamespace());
        Assert.assertEquals(LINESTRINGPROPERTYTYPE.getLocalPart(), lineStringPropertyType.getName());
        XSDTypeDefinition geometryAssociationType = lineStringPropertyType.getBaseType();
        Assert.assertNotNull(geometryAssociationType);
        Assert.assertEquals(NAMESPACE, geometryAssociationType.getTargetNamespace());
        Assert.assertEquals(GEOMETRYASSOCIATIONTYPE.getLocalPart(), geometryAssociationType.getName());
    }

    protected XpathEngine xpath;

    @Test
    public void testUUID() throws Exception {
        Document dom = getAsDOM(("wfs?service=wfs&version=1.1.0&request=DescribeFeatureType&typeName=" + (getLayerId(UUID_TEST))));
        Assert.assertEquals("1", xpath.evaluate("count(//xsd:element[@name='uuid' and @type='xsd:string'])", dom));
    }
}

