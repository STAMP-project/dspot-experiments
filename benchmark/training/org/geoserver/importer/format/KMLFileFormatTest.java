/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.format;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.geotools.data.FeatureReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;


public class KMLFileFormatTest extends TestCase {
    private KMLFileFormat kmlFileFormat;

    static final String DOC_EL = "<kml xmlns=\"http://www.opengis.net/kml/2.2\">";

    public void testParseFeatureTypeNoPlacemarks() throws IOException {
        String kmlInput = (KMLFileFormatTest.DOC_EL) + "</kml>";
        try {
            kmlFileFormat.parseFeatureTypes("foo", IOUtils.toInputStream(kmlInput));
        } catch (IllegalArgumentException e) {
            TestCase.assertTrue(true);
            return;
        }
        TestCase.fail("Expected Illegal Argument Exception for no features");
    }

    public void testParseFeatureTypeMinimal() throws Exception {
        String kmlInput = (KMLFileFormatTest.DOC_EL) + "<Placemark></Placemark></kml>";
        List<SimpleFeatureType> featureTypes = kmlFileFormat.parseFeatureTypes("foo", IOUtils.toInputStream(kmlInput));
        TestCase.assertEquals("Unexpected number of feature types", 1, featureTypes.size());
        SimpleFeatureType featureType = featureTypes.get(0);
        TestCase.assertEquals("Unexpected number of feature type attributes", 10, featureType.getAttributeCount());
    }

    public void testExtendedUserData() throws Exception {
        String kmlInput = ((((((KMLFileFormatTest.DOC_EL) + "<Placemark>") + "<ExtendedData>") + "<Data name=\"foo\"><value>bar</value></Data>") + "<Data name=\"quux\"><value>morx</value></Data>") + "</ExtendedData>") + "</Placemark></kml>";
        List<SimpleFeatureType> featureTypes = kmlFileFormat.parseFeatureTypes("fleem", IOUtils.toInputStream(kmlInput));
        TestCase.assertEquals("Unexpected number of feature types", 1, featureTypes.size());
        SimpleFeatureType featureType = featureTypes.get(0);
        TestCase.assertEquals("Unexpected number of feature type attributes", 12, featureType.getAttributeCount());
        TestCase.assertEquals("Invalid attribute descriptor", String.class, featureType.getDescriptor("foo").getType().getBinding());
        TestCase.assertEquals("Invalid attribute descriptor", String.class, featureType.getDescriptor("quux").getType().getBinding());
    }

    public void testReadFeatureWithNameAndDescription() throws Exception {
        String kmlInput = (KMLFileFormatTest.DOC_EL) + "<Placemark><name>foo</name><description>bar</description></Placemark></kml>";
        SimpleFeatureType featureType = kmlFileFormat.parseFeatureTypes("foo", IOUtils.toInputStream(kmlInput)).get(0);
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = kmlFileFormat.read(featureType, IOUtils.toInputStream(kmlInput));
        TestCase.assertTrue("No features found", reader.hasNext());
        SimpleFeature feature = reader.next();
        TestCase.assertNotNull("Expecting feature", feature);
        TestCase.assertEquals("Invalid name attribute", "foo", feature.getAttribute("name"));
        TestCase.assertEquals("Invalid description attribute", "bar", feature.getAttribute("description"));
    }

    public void testReadFeatureWithUntypedExtendedData() throws Exception {
        String kmlInput = ((((((KMLFileFormatTest.DOC_EL) + "<Placemark>") + "<ExtendedData>") + "<Data name=\"foo\"><value>bar</value></Data>") + "<Data name=\"quux\"><value>morx</value></Data>") + "</ExtendedData>") + "</Placemark></kml>";
        SimpleFeatureType featureType = kmlFileFormat.parseFeatureTypes("foo", IOUtils.toInputStream(kmlInput)).get(0);
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = kmlFileFormat.read(featureType, IOUtils.toInputStream(kmlInput));
        TestCase.assertTrue("No features found", reader.hasNext());
        SimpleFeature feature = ((SimpleFeature) (reader.next()));
        TestCase.assertNotNull("Expecting feature", feature);
        TestCase.assertEquals("Invalid ext attr foo", "bar", feature.getAttribute("foo"));
        TestCase.assertEquals("Invalid ext attr quux", "morx", feature.getAttribute("quux"));
    }

    public void testReadFeatureWithTypedExtendedData() throws Exception {
        String kmlInput = ((((((((((KMLFileFormatTest.DOC_EL) + "<Schema name=\"myschema\">") + "<SimpleField type=\"int\" name=\"foo\"></SimpleField>") + "</Schema>") + "<Placemark>") + "<ExtendedData>") + "<SchemaData schemaUrl=\"#myschema\">") + "<SimpleData name=\"foo\">42</SimpleData>") + "</SchemaData>") + "</ExtendedData>") + "</Placemark></kml>";
        SimpleFeatureType featureType = kmlFileFormat.parseFeatureTypes("foo", IOUtils.toInputStream(kmlInput)).get(0);
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = kmlFileFormat.read(featureType, IOUtils.toInputStream(kmlInput));
        TestCase.assertTrue("No features found", reader.hasNext());
        SimpleFeature feature = reader.next();
        TestCase.assertNotNull("Expecting feature", feature);
        TestCase.assertEquals("Invalid ext attr foo", 42, feature.getAttribute("foo"));
    }

    public void testMultipleSchemas() throws Exception {
        String kmlInput = ((((((((((((((((KMLFileFormatTest.DOC_EL) + "<Schema name=\"schema1\">") + "<SimpleField type=\"int\" name=\"foo\"></SimpleField>") + "</Schema>") + "<Schema name=\"schema2\">") + "<SimpleField type=\"float\" name=\"bar\"></SimpleField>") + "</Schema>") + "<Placemark>") + "<ExtendedData>") + "<SchemaData schemaUrl=\"#schema1\">") + "<SimpleData name=\"foo\">42</SimpleData>") + "</SchemaData>") + "<SchemaData schemaUrl=\"#schema2\">") + "<SimpleData name=\"bar\">4.2</SimpleData>") + "</SchemaData>") + "</ExtendedData>") + "</Placemark></kml>";
        List<SimpleFeatureType> featureTypes = kmlFileFormat.parseFeatureTypes("multiple", IOUtils.toInputStream(kmlInput));
        TestCase.assertEquals("Unexpected number of feature types", 1, featureTypes.size());
        SimpleFeatureType ft = featureTypes.get(0);
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = kmlFileFormat.read(ft, IOUtils.toInputStream(kmlInput));
        SimpleFeature feature1 = reader.next();
        TestCase.assertNotNull("Expecting feature", feature1);
        TestCase.assertEquals("Invalid ext attr foo", 42, feature1.getAttribute("foo"));
        TestCase.assertEquals("Invalid ext attr bar", 4.2F, ((Float) (feature1.getAttribute("bar"))), 0.01);
    }

    public void testTypedAndUntyped() throws Exception {
        String kmlInput = ((((((((((((KMLFileFormatTest.DOC_EL) + "<Schema name=\"myschema\">") + "<SimpleField type=\"int\" name=\"foo\"></SimpleField>") + "</Schema>") + "<Placemark>") + "<ExtendedData>") + "<SchemaData schemaUrl=\"#myschema\">") + "<SimpleData name=\"foo\">42</SimpleData>") + "</SchemaData>") + "<Data name=\"fleem\"><value>bar</value></Data>") + "<Data name=\"quux\"><value>morx</value></Data>") + "</ExtendedData>") + "</Placemark></kml>";
        List<SimpleFeatureType> featureTypes = kmlFileFormat.parseFeatureTypes("typed-and-untyped", IOUtils.toInputStream(kmlInput));
        TestCase.assertEquals("Unexpected number of feature types", 1, featureTypes.size());
        SimpleFeatureType featureType = featureTypes.get(0);
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = kmlFileFormat.read(featureType, IOUtils.toInputStream(kmlInput));
        SimpleFeature feature = reader.next();
        TestCase.assertNotNull("Expecting feature", feature);
        TestCase.assertEquals("Invalid ext attr foo", 42, feature.getAttribute("foo"));
        TestCase.assertEquals("bar", feature.getAttribute("fleem"));
        TestCase.assertEquals("morx", feature.getAttribute("quux"));
    }

    public void testReadCustomSchema() throws Exception {
        String kmlInput = (((((KMLFileFormatTest.DOC_EL) + "<Schema name=\"myschema\">") + "<SimpleField type=\"int\" name=\"foo\"></SimpleField>") + "</Schema>") + "<myschema><foo>7</foo></myschema>") + "</kml>";
        List<SimpleFeatureType> featureTypes = kmlFileFormat.parseFeatureTypes("custom-schema", IOUtils.toInputStream(kmlInput));
        TestCase.assertEquals("Unexpected number of feature types", 1, featureTypes.size());
        SimpleFeatureType featureType = featureTypes.get(0);
        Map<Object, Object> userData = featureType.getUserData();
        List<String> schemaNames = ((List<String>) (userData.get("schemanames")));
        TestCase.assertEquals(1, schemaNames.size());
        TestCase.assertEquals("Did not find expected schema name metadata", "myschema", schemaNames.get(0));
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = kmlFileFormat.read(featureType, IOUtils.toInputStream(kmlInput));
        SimpleFeature feature = reader.next();
        TestCase.assertNotNull("Expecting feature", feature);
        TestCase.assertEquals("Invalid ext attr foo", 7, feature.getAttribute("foo"));
    }
}

