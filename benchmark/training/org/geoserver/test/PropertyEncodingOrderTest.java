/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.test;


import org.geoserver.wfs.WFSInfo;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 *
 *
 * @author Xiangtan Lin, CSIRO Information Management and Technology
 */
public class PropertyEncodingOrderTest extends AbstractAppSchemaTestSupport {
    /**
     * Test the gmsl:Borehole is encoded in the right order, in particular this test is created for
     * an encoding order issue with gsml:indexData according to the schema
     * http://www.geosciml.org/geosciml/2.0/xsd/borehole.xsd
     */
    @Test
    public void testPropertyEncodingOrder_Borehole() throws Exception {
        String path = "wfs?request=GetFeature&version=1.1.0&typename=gsml:Borehole";
        Document doc = getAsDOM(path);
        LOGGER.info(("WFS GetFeature&gsml:Borehole:\n" + (prettyString(doc))));
        assertXpathEvaluatesTo("1", "/wfs:FeatureCollection/@numberOfFeatures", doc);
        assertXpathCount(1, "//gsml:Borehole[@gml:id='BOREHOLE.WTB5']", doc);
        Node borehole = doc.getElementsByTagName("gsml:Borehole").item(0);
        Assert.assertEquals("gsml:Borehole", borehole.getNodeName());
        // check for gml:id
        Assert.assertEquals("BOREHOLE.WTB5", borehole.getAttributes().getNamedItem("gml:id").getNodeValue());
        // gml:name
        Node name = borehole.getFirstChild();
        Assert.assertEquals("gml:name", name.getNodeName());
        Assert.assertEquals("WTB5 TEST", name.getFirstChild().getNodeValue());
        // sa:sampledFeature
        Node sampledFeature = name.getNextSibling();
        Assert.assertEquals("sa:sampledFeature", sampledFeature.getNodeName());
        // sa:shape
        Node shape = sampledFeature.getNextSibling();
        Assert.assertEquals("sa:shape", shape.getNodeName());
        Node posList = shape.getFirstChild().getFirstChild();
        Assert.assertEquals("gml:posList", posList.getNodeName());
        Assert.assertEquals("-28.4139 121.142 -28.4139 121.142", posList.getFirstChild().getNodeValue());
        // gsml:collarLocation
        Node collarLocation = shape.getNextSibling();
        Assert.assertEquals("gsml:collarLocation", collarLocation.getNodeName());
        Node boreholeCollar = collarLocation.getFirstChild();
        Assert.assertEquals("gsml:BoreholeCollar", boreholeCollar.getNodeName());
        Assert.assertEquals("BOREHOLE.COLLAR.WTB5", boreholeCollar.getAttributes().getNamedItem("gml:id").getNodeValue());
        Assert.assertEquals("-28.4139 121.142", boreholeCollar.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getNodeValue());
        Assert.assertEquals("1.0", boreholeCollar.getFirstChild().getNextSibling().getFirstChild().getNodeValue());
        // gsml:indexData
        Node indexData = collarLocation.getNextSibling();
        Assert.assertEquals("gsml:indexData", indexData.getNodeName());
        Node boreholeDetails = indexData.getFirstChild();
        Assert.assertEquals("gsml:BoreholeDetails", boreholeDetails.getNodeName());
        Node operator = boreholeDetails.getFirstChild();
        Assert.assertEquals("GSWA", operator.getAttributes().getNamedItem("xlink:title").getNodeValue());
        Node dateOfDrilling = operator.getNextSibling();
        Assert.assertEquals("2004-09-17", dateOfDrilling.getFirstChild().getNodeValue());
        Node drillingMethod = dateOfDrilling.getNextSibling();
        Assert.assertEquals("diamond core", drillingMethod.getFirstChild().getNodeValue());
        Node startPoint = drillingMethod.getNextSibling();
        Assert.assertEquals("natural ground surface", startPoint.getFirstChild().getNodeValue());
        Node inclinationType = startPoint.getNextSibling();
        Assert.assertEquals("vertical", inclinationType.getFirstChild().getNodeValue());
        Node coreInterval = inclinationType.getNextSibling();
        Assert.assertEquals("106.0", coreInterval.getFirstChild().getFirstChild().getFirstChild().getNodeValue());
        Assert.assertEquals("249.0", coreInterval.getFirstChild().getFirstChild().getNextSibling().getFirstChild().getNodeValue());
        Node coreCustodian = coreInterval.getNextSibling();
        Assert.assertEquals("CSIRONR", coreCustodian.getAttributes().getNamedItem("xlink:title").getNodeValue());
        validateGet(path);
    }

    /**
     * Test the gmsl:PlanarOrientation is encoded in the order of aziumth, convention, dip, polarity
     * according to the schema CGI_Value.xsd
     */
    @Test
    public void testPropertyEncodingOrder_PlanarOrientation() throws Exception {
        String path = "wfs?request=GetFeature&version=1.1.0&typename=er:MineralOccurrence";
        Document doc = getAsDOM(path);
        LOGGER.info(("WFS GetFeature&er:MineralOccurrence:\n" + (prettyString(doc))));
        assertXpathCount(1, "//er:MineralOccurrence[@gml:id='er.mineraloccurrence.S0032895']", doc);
        Node feature = doc.getElementsByTagName("er:MineralOccurrence").item(0);
        Assert.assertEquals("er:MineralOccurrence", feature.getNodeName());
        // check for gml:id
        assertXpathEvaluatesTo("er.mineraloccurrence.S0032895", "//er:MineralOccurrence/@gml:id", doc);
        Node name = feature.getFirstChild();
        Assert.assertEquals("gml:name", name.getNodeName());
        assertXpathEvaluatesTo("Robinson Range - Deposit D", "//er:MineralOccurrence[@gml:id='er.mineraloccurrence.S0032895']/gml:name", doc);
        // er:planarOrientation
        Node planarOrientation = name.getNextSibling();
        Assert.assertEquals("er:planarOrientation", planarOrientation.getNodeName());
        // gsml:CGI_PlanarOrientation
        Node gsml_planarOrientation = planarOrientation.getFirstChild();
        Assert.assertEquals("gsml:CGI_PlanarOrientation", gsml_planarOrientation.getNodeName());
        // convention
        Node convention = gsml_planarOrientation.getFirstChild();
        Assert.assertEquals("gsml:convention", convention.getNodeName());
        assertXpathEvaluatesTo("strike dip right hand rule", "//er:MineralOccurrence[@gml:id='er.mineraloccurrence.S0032895']/er:planarOrientation/gsml:CGI_PlanarOrientation/gsml:convention", doc);
        // azimuth
        Node azimuth = convention.getNextSibling();
        Assert.assertEquals("gsml:azimuth", azimuth.getNodeName());
        assertXpathEvaluatesTo("50.0", "//er:MineralOccurrence[@gml:id='er.mineraloccurrence.S0032895']/er:planarOrientation/gsml:CGI_PlanarOrientation/gsml:azimuth/gsml:CGI_NumericValue/gsml:principalValue", doc);
        // dip
        Node dip = azimuth.getNextSibling();
        Assert.assertEquals("gsml:dip", dip.getNodeName());
        assertXpathEvaluatesTo("60-80", "//er:MineralOccurrence[@gml:id='er.mineraloccurrence.S0032895']/er:planarOrientation/gsml:CGI_PlanarOrientation/gsml:dip/gsml:CGI_TermValue/gsml:value", doc);
        // polarity
        Node polarity = dip.getNextSibling();
        Assert.assertEquals("gsml:polarity", polarity.getNodeName());
        assertXpathEvaluatesTo("not applicable", "//er:MineralOccurrence[@gml:id='er.mineraloccurrence.S0032895']/er:planarOrientation/gsml:CGI_PlanarOrientation/gsml:polarity", doc);
        // FIXME: this feature type is not yet complete
        // validateGet(path);
    }

    /**
     * Test elements are encoded in the order as defined in the schema GeologicUnit is tested here
     */
    @Test
    public void testPropertyEncodingOrder_GeologicUnit() throws Exception {
        WFSInfo wfs = getGeoServer().getService(WFSInfo.class);
        wfs.setEncodeFeatureMember(true);
        getGeoServer().save(wfs);
        String path = "wfs?request=GetFeature&version=1.1.0&typename=gsml:GeologicUnit&featureid=gu.25699";
        Document doc = getAsDOM(path);
        LOGGER.info(("WFS GetFeature&typename=gsml:GeologicUnit&featureid=gu.25699:\n" + (prettyString(doc))));
        Assert.assertEquals(1, doc.getElementsByTagName("gml:featureMember").getLength());
        assertXpathCount(1, "//gsml:GeologicUnit[@gml:id='gu.25699']", doc);
        // GeologicUnit
        Node feature = doc.getElementsByTagName("gsml:GeologicUnit").item(0);
        Assert.assertEquals("gsml:GeologicUnit", feature.getNodeName());
        // description
        Node description = feature.getFirstChild();
        Assert.assertEquals("gml:description", description.getNodeName());
        assertXpathEvaluatesTo("Olivine basalt, tuff, microgabbro, minor sedimentary rocks", "//gsml:GeologicUnit[@gml:id='gu.25699']/gml:description", doc);
        // name1
        Node name1 = description.getNextSibling();
        Assert.assertEquals("gml:name", name1.getNodeName());
        assertXpathEvaluatesTo("Yaugher Volcanic Group", "//gsml:GeologicUnit[@gml:id='gu.25699']/gml:name[1]", doc);
        // name2
        Node name2 = name1.getNextSibling();
        Assert.assertEquals("gml:name", name2.getNodeName());
        assertXpathEvaluatesTo("-Py", "//gsml:GeologicUnit[@gml:id='gu.25699']/gml:name[2]", doc);
        // observationMethod
        Node observationMethod = name2.getNextSibling();
        Assert.assertEquals("gsml:observationMethod", observationMethod.getNodeName());
        assertXpathEvaluatesTo("urn:ogc:def:nil:OGC::missing", "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:observationMethod/gsml:CGI_TermValue/gsml:value", doc);
        assertXpathEvaluatesTo("http://urn.opengis.net", "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:observationMethod/gsml:CGI_TermValue/gsml:value/@codeSpace", doc);
        // purpose
        Node purpose = observationMethod.getNextSibling();
        Assert.assertEquals("gsml:purpose", purpose.getNodeName());
        assertXpathEvaluatesTo("instance", "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:purpose", doc);
        // occurrence
        Node occurrence = purpose.getNextSibling();
        Assert.assertEquals("gsml:occurrence", occurrence.getNodeName());
        assertXpathCount(1, "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:occurrence[@xlink:href='urn:cgi:feature:MappedFeature:mf1']", doc);
        // geologicUnitType
        Node geologicUnitType = occurrence.getNextSibling();
        Assert.assertEquals("gsml:geologicUnitType", geologicUnitType.getNodeName());
        assertXpathEvaluatesTo("urn:ogc:def:nil:OGC::unknown", "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:geologicUnitType/@xlink:href", doc);
        // exposureColor
        Node exposureColor = geologicUnitType.getNextSibling();
        Assert.assertEquals("gsml:exposureColor", exposureColor.getNodeName());
        assertXpathEvaluatesTo("Blue", "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:exposureColor/gsml:CGI_TermValue/gsml:value", doc);
        // outcropCharacter
        Node outcropCharacter = exposureColor.getNextSibling();
        Assert.assertEquals("gsml:outcropCharacter", outcropCharacter.getNodeName());
        assertXpathEvaluatesTo("x", "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:outcropCharacter/gsml:CGI_TermValue/gsml:value", doc);
        // composition
        Node composition = outcropCharacter.getNextSibling();
        Assert.assertEquals("gsml:composition", composition.getNodeName());
        Node compositionPart = doc.getElementsByTagName("gsml:CompositionPart").item(0);
        Assert.assertEquals("gsml:CompositionPart", compositionPart.getNodeName());
        // role
        Node role = compositionPart.getFirstChild();
        Assert.assertEquals("gsml:role", role.getNodeName());
        assertXpathEvaluatesTo("fictitious component", "//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:composition/gsml:CompositionPart/gsml:role", doc);
        // lithology
        Node lithology = role.getNextSibling();
        Assert.assertEquals("gsml:lithology", lithology.getNodeName());
        assertXpathEvaluatesTo("urn:ogc:def:nil:OGC::missing", ("//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:composition/gsml:CompositionPart/gsml:lithology" + "/gsml:ControlledConcept/gsml:vocabulary/@xlink:href"), doc);
        // proportion
        Node proportion = lithology.getNextSibling();
        Assert.assertEquals("gsml:proportion", proportion.getNodeName());
        assertXpathEvaluatesTo("nonexistent", ("//gsml:GeologicUnit[@gml:id='gu.25699']/gsml:composition/gsml:CompositionPart/gsml:proportion" + "/gsml:CGI_TermValue/gsml:value"), doc);
        validateGet(path);
    }
}

