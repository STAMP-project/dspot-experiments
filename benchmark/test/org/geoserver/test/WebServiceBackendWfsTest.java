/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import java.io.IOException;
import java.util.ArrayList;
import org.geoserver.wfs.xml.v1_1_0.WFS;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * WFS GetFeature to test integration of {@link AppSchemaDataAccess} with web service backend with
 * GeoServer.
 *
 * @author Rini Angreani, CSIRO Earth Science and Resource Engineering
 */
public class WebServiceBackendWfsTest extends AbstractAppSchemaTestSupport {
    /**
     * Test whether GetCapabilities returns wfs:WFS_Capabilities.
     */
    @Test
    public void testGetCapabilities() {
        Document doc = getAsDOM("wfs?request=GetCapabilities&version=1.1.0");
        LOGGER.info(("WFS GetCapabilities response:\n" + (prettyString(doc))));
        Assert.assertEquals("wfs:WFS_Capabilities", doc.getDocumentElement().getNodeName());
        // check wfs schema location is canonical
        String schemaLocation = evaluate("wfs:WFS_Capabilities/@xsi:schemaLocation", doc);
        String location = "http://www.opengis.net/wfs " + (WFS.CANONICAL_SCHEMA_LOCATION);
        Assert.assertEquals(location, schemaLocation);
        assertXpathCount(2, "//wfs:FeatureType", doc);
        ArrayList<String> featureTypeNames = new ArrayList<String>(2);
        featureTypeNames.add(evaluate("//wfs:FeatureType[1]/wfs:Name", doc));
        featureTypeNames.add(evaluate("//wfs:FeatureType[2]/wfs:Name", doc));
        // Mapped Feture
        Assert.assertTrue(featureTypeNames.contains("gsml:MappedFeature"));
        // Geologic Unit
        Assert.assertTrue(featureTypeNames.contains("gsml:GeologicUnit"));
    }

    /**
     * Test whether DescribeFeatureType returns xsd:schema, and if the contents are correct. When no
     * type name specified, it should return imports for all name spaces involved. If type name is
     * specified, it should return imports of GML type and the type's top level schema.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDescribeFeatureType() throws IOException {
        /**
         * gsml:MappedFeature
         */
        Document doc = getAsDOM("wfs?request=DescribeFeatureType&version=1.1.0&typename=gsml:MappedFeature");
        LOGGER.info(("WFS DescribeFeatureType, typename=gsml:MappedFeature response:\n" + (prettyString(doc))));
        Assert.assertEquals("xsd:schema", doc.getDocumentElement().getNodeName());
        // check target name space is encoded and is correct
        assertXpathEvaluatesTo(AbstractAppSchemaMockData.GSML_URI, "//@targetNamespace", doc);
        // make sure the content is only relevant include
        assertXpathCount(1, "//xsd:include", doc);
        // no import to GML since it's already imported inside the included schema
        // otherwise it's invalid to import twice
        assertXpathCount(0, "//xsd:import", doc);
        // GSML schemaLocation
        assertXpathEvaluatesTo(AbstractAppSchemaMockData.GSML_SCHEMA_LOCATION_URL, "//xsd:include/@schemaLocation", doc);
        // nothing else
        assertXpathCount(0, "//xsd:complexType", doc);
        assertXpathCount(0, "//xsd:element", doc);
        /**
         * gsml:GeologicUnit
         */
        doc = getAsDOM("wfs?request=DescribeFeatureType&version=1.1.0&typename=gsml:GeologicUnit");
        LOGGER.info(("WFS DescribeFeatureType, typename=gsml:GeologicUnit response:\n" + (prettyString(doc))));
        Assert.assertEquals("xsd:schema", doc.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo(AbstractAppSchemaMockData.GSML_URI, "//@targetNamespace", doc);
        assertXpathCount(1, "//xsd:include", doc);
        assertXpathCount(0, "//xsd:import", doc);
        // GSML schemaLocation
        assertXpathEvaluatesTo(AbstractAppSchemaMockData.GSML_SCHEMA_LOCATION_URL, "//xsd:include/@schemaLocation", doc);
        // nothing else
        assertXpathCount(0, "//xsd:complexType", doc);
        assertXpathCount(0, "//xsd:element", doc);
    }

    /**
     * Test content of GetFeature response for MappedFeature. MappedFeature is a normal app-schema
     * data access with property files backend, but it chains GeologicUnit from a web service
     * backend.
     */
    @Test
    public void testMappedFeature() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=gsml:MappedFeature");
        LOGGER.info(("WFS GetFeature&typename=gsml:MappedFeature response:\n" + (prettyString(doc))));
        assertXpathEvaluatesTo("4", "/wfs:FeatureCollection/@numberOfFeatures", doc);
        assertXpathCount(4, "//gsml:MappedFeature", doc);
        checkSchemaLocation(doc);
        // mf1
        {
            String id = "mf1";
            assertXpathEvaluatesTo(id, "(//gsml:MappedFeature)[1]/@gml:id", doc);
            checkMf1Content(id, doc);
        }
        // mf2
        {
            String id = "mf2";
            assertXpathEvaluatesTo(id, "(//gsml:MappedFeature)[2]/@gml:id", doc);
            checkMf2Content(id, doc);
        }
        // mf3
        {
            String id = "mf3";
            assertXpathEvaluatesTo(id, "(//gsml:MappedFeature)[3]/@gml:id", doc);
            checkMf3Content(id, doc);
        }
        // mf4
        {
            String id = "mf4";
            assertXpathEvaluatesTo(id, "(//gsml:MappedFeature)[4]/@gml:id", doc);
            checkMf4Content(id, doc);
        }
        // check for duplicate gml:id
        assertXpathCount(1, "//gsml:GeologicUnit[@gml:id='lithostratigraphic.unit.1679161041155866313']", doc);
    }

    /**
     * Test content of GetFeature response for GeologicUnit with web service backend. It feature
     * chains observationMethod which is a normal app-schema data access with property files
     * backend. It also feature chains CompositionPart which is another app-schema data access with
     * web service.
     */
    @Test
    public void testGeologicUnit() throws Exception {
        Document doc = getAsDOM("wfs?request=GetFeature&version=1.1.0&typename=gsml:GeologicUnit");
        LOGGER.info(("WFS GetFeature&typename=gsml:GeologicUnit response:\n" + (prettyString(doc))));
        assertXpathEvaluatesTo("3", "/wfs:FeatureCollection/@numberOfFeatures", doc);
        assertXpathCount(3, "//gsml:GeologicUnit", doc);
        checkSchemaLocation(doc);
        /**
         * First Geologic Unit
         */
        String id = "lithostratigraphic.unit.1679161021439131319";
        assertXpathEvaluatesTo(id, "(//gsml:GeologicUnit)[1]/@gml:id", doc);
        // description
        assertXpathEvaluatesTo("Test description 1", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gml:description"), doc);
        // observation method from properties file by feature chaining
        assertXpathCount(1, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:observationMethod"), doc);
        assertXpathEvaluatesTo("value01", ((("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:observationMethod/gsml:CGI_TermValue/gsml:value") + "[@codeSpace='codespace01']"), doc);
        // composition part from another web service by feature chaining
        assertXpathCount(0, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition"), doc);
        /**
         * Second Geologic Unit
         */
        id = "lithostratigraphic.unit.1679161041155866313";
        assertXpathEvaluatesTo(id, "(//gsml:GeologicUnit)[2]/@gml:id", doc);
        // description
        assertXpathEvaluatesTo("Test description 1", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gml:description"), doc);
        // observation method from properties file by feature chaining
        assertXpathCount(1, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:observationMethod"), doc);
        assertXpathEvaluatesTo("value02", ((("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:observationMethod/gsml:CGI_TermValue/gsml:value") + "[@codeSpace='codespace02']"), doc);
        // composition part from another web service by feature chaining
        assertXpathCount(1, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition"), doc);
        assertXpathCount(1, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology"), doc);
        assertXpathEvaluatesTo("167916112856013567", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology/gsml:ControlledConcept/@gml:id"), doc);
        assertXpathCount(1, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology/gsml:ControlledConcept/gml:name"), doc);
        assertXpathEvaluatesTo("urn:cgi:classifier:GSV:LithostratigraphicUnitRank:formation", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology/gsml:ControlledConcept/gml:name"), doc);
        assertXpathCount(0, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology/gsml:ControlledConcept/gml:description"), doc);
        /**
         * Third Geologic Unit
         */
        id = "lithostratigraphic.unit.1679161021439938381";
        assertXpathEvaluatesTo(id, "(//gsml:GeologicUnit)[3]/@gml:id", doc);
        // description
        assertXpathEvaluatesTo("Test description 2", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gml:description"), doc);
        // observation method from properties file by feature chaining
        assertXpathCount(1, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:observationMethod"), doc);
        assertXpathEvaluatesTo("value03", ((("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:observationMethod/gsml:CGI_TermValue/gsml:value") + "[@codeSpace='codespace03']"), doc);
        // composition part from another web service by feature chaining
        assertXpathCount(1, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition"), doc);
        assertXpathCount(2, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology"), doc);
        // testing lithology as multi valued properties from the backend
        assertXpathEvaluatesTo("167916112856013567", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[1]/gsml:ControlledConcept/@gml:id"), doc);
        assertXpathCount(2, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[1]/gsml:ControlledConcept/gml:name"), doc);
        assertXpathEvaluatesTo("Formation [lithostratigraphic]", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[1]/gsml:ControlledConcept/gml:name[1]"), doc);
        assertXpathEvaluatesTo("urn:cgi:classifier:GSV:LithostratigraphicUnitRank:formation", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[1]/gsml:ControlledConcept/gml:name[2]"), doc);
        assertXpathEvaluatesTo("A body of rock strata distinguishable by its lithology; primary formal unit of lithostratigraphic classification.", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[1]/gsml:ControlledConcept/gml:description"), doc);
        assertXpathEvaluatesTo("167916112856013568", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[2]/gsml:ControlledConcept/@gml:id"), doc);
        assertXpathCount(2, (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[2]/gsml:ControlledConcept/gml:name"), doc);
        assertXpathEvaluatesTo("Formation [lithostratigraphic] 2", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[2]/gsml:ControlledConcept/gml:name[1]"), doc);
        assertXpathEvaluatesTo("urn:cgi:classifier:GSV:LithostratigraphicUnitRank:formation2", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[2]/gsml:ControlledConcept/gml:name[2]"), doc);
        assertXpathEvaluatesTo("A body of rock strata distinguishable by its lithology; primary formal unit of lithostratigraphic classification.", (("//gsml:GeologicUnit[@gml:id='" + id) + "']/gsml:composition/gsml:CompositionPart/gsml:lithology[2]/gsml:ControlledConcept/gml:description"), doc);
    }
}

