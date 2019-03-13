/**
 * (c) 2017 -2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.records.iso;


import org.junit.Test;
import org.w3c.dom.Document;


/**
 *
 *
 * @author Niels Charlier
 */
public class GetRecordsTest extends MDTestSupport {
    @Test
    public void testAllRecordsPaged() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=gmd:MD_Metadata" + "&resultType=results&elementSetName=full&outputSchema=http://www.isotc211.org/2005/gmd";
        Document d = getAsDOM(request);
        // print(d);
        // validateSchema(d.getElementsByTagName("//gmd:MD_MetaData"));
        // we have the right kind of document
        assertXpathEvaluatesTo("1", "count(/csw:GetRecordsResponse)", d);
        // check we have the expected results
        assertXpathEvaluatesTo("full", "//csw:SearchResults/@elementSet", d);
        assertXpathEvaluatesTo("29", "//csw:SearchResults/@numberOfRecordsMatched", d);
        assertXpathEvaluatesTo("10", "//csw:SearchResults/@numberOfRecordsReturned", d);
        assertXpathEvaluatesTo("11", "//csw:SearchResults/@nextRecord", d);
        assertXpathEvaluatesTo("10", "count(//csw:SearchResults/*)", d);
    }

    @Test
    public void testAllRecords() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=gmd:MD_Metadata" + ("&resultType=results&elementSetName=full&outputSchema=http://www.isotc211.org/2005/gmd" + "&maxRecords=100");
        Document d = getAsDOM(request);
        // print(d);
        // validateSchema(d.getElementsByTagName("//gmd:MD_MetaData"));
        // we have the right kind of document
        assertXpathEvaluatesTo("1", "count(/csw:GetRecordsResponse)", d);
        // check we have the expected results
        assertXpathEvaluatesTo("full", "//csw:SearchResults/@elementSet", d);
        assertXpathEvaluatesTo("29", "//csw:SearchResults/@numberOfRecordsMatched", d);
        assertXpathEvaluatesTo("29", "//csw:SearchResults/@numberOfRecordsReturned", d);
        assertXpathEvaluatesTo("0", "//csw:SearchResults/@nextRecord", d);
        assertXpathEvaluatesTo("29", "count(//csw:SearchResults/*)", d);
        // check contents Forests record
        assertXpathEvaluatesTo("abstract about Forests", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString", d);
        assertXpathEvaluatesTo("Forests", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString", d);
        assertXpathEvaluatesTo("http://purl.org/dc/dcmitype/Dataset", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue", d);
        assertXpathEvaluatesTo("urn:x-ogc:def:crs:EPSG:6.11:4326", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/@crs", d);
        assertXpathEvaluatesTo("-100.0", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude", d);
        assertXpathEvaluatesTo("-200.0", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude", d);
        assertXpathEvaluatesTo("-90.0", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude", d);
        assertXpathEvaluatesTo("-180.0", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude", d);
        // check the multi-valued field alternate title
        assertXpathEvaluatesTo("Land with lots of trees on.", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:alternateTitle[1]/gco:CharacterString", d);
        assertXpathEvaluatesTo("Bush", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:alternateTitle[2]/gco:CharacterString", d);
        assertXpathEvaluatesTo("Woods", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:alternateTitle[3]/gco:CharacterString", d);
        assertXpathEvaluatesTo("##Forests", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:alternateTitle[4]/gco:CharacterString", d);
        // check the service information
        assertXpathEvaluatesTo("OGC:WFS", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource[1]/gmd:protocol/gco:CharacterString", d);
        assertXpathEvaluatesTo("Forests", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource[1]/gmd:name/gco:CharacterString", d);
        assertXpathEvaluatesTo("http://localhost:8080/geoserver/wfs", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource[1]/gmd:linkage/gmd:URL", d);
        assertXpathEvaluatesTo("OGC:WMS", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource[2]/gmd:protocol/gco:CharacterString", d);
        assertXpathEvaluatesTo("Forests", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource[2]/gmd:name/gco:CharacterString", d);
        assertXpathEvaluatesTo("http://localhost:8080/geoserver/wms", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource[2]/gmd:linkage/gmd:URL", d);
    }

    @Test
    public void testAllRecordsBrief() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=gmd:MD_Metadata" + ("&resultType=results&elementSetName=brief&outputSchema=http://www.isotc211.org/2005/gmd" + "&maxRecords=100");
        Document d = getAsDOM(request);
        print(d);
        // validateSchema(d.getElementsByTagName("//gmd:MD_MetaData"));
        // check we have the expected results
        assertXpathEvaluatesTo("brief", "//csw:SearchResults/@elementSet", d);
        assertXpathEvaluatesTo("29", "//csw:SearchResults/@numberOfRecordsMatched", d);
        assertXpathEvaluatesTo("29", "//csw:SearchResults/@numberOfRecordsReturned", d);
        assertXpathEvaluatesTo("0", "//csw:SearchResults/@nextRecord", d);
        assertXpathEvaluatesTo("29", "count(//csw:SearchResults/*)", d);
        // check contents Forests record
        assertXpathNotExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString", d);
        assertXpathNotExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString", d);
        assertXpathNotExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue", d);
        assertXpathEvaluatesTo("urn:x-ogc:def:crs:EPSG:6.11:4326", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/@crs", d);
        assertXpathEvaluatesTo("-100.0", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude", d);
        assertXpathEvaluatesTo("-200.0", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude", d);
        assertXpathEvaluatesTo("-90.0", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude", d);
        assertXpathEvaluatesTo("-180.0", "//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude", d);
    }

    @Test
    public void testTitleFilter1() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=gmd:MD_Metadata&resultType=results&elementSetName=brief&outputSchema=http://www.isotc211.org/2005/gmd&constraint=Title = 'Forests'";
        Document d = getAsDOM(request);
        // print(d);
        // validateSchema(d.getElementsByTagName("//gmd:MD_MetaData"));
        assertXpathEvaluatesTo("1", "//csw:SearchResults/@numberOfRecordsMatched", d);
        assertXpathEvaluatesTo("1", "//csw:SearchResults/@numberOfRecordsReturned", d);
        assertXpathEvaluatesTo("1", "count(//csw:SearchResults/*)", d);
        assertXpathEvaluatesTo("Forests", "//gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString", d);
    }

    @Test
    public void testTitleFilter2() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=gmd:MD_Metadata&resultType=results&elementSetName=brief&outputSchema=http://www.isotc211.org/2005/gmd&constraint=Title like 'S%25'";
        Document d = getAsDOM(request);
        // print(d);
        // validateSchema(d.getElementsByTagName("//gmd:MD_MetaData"));
        assertXpathEvaluatesTo("2", "//csw:SearchResults/@numberOfRecordsMatched", d);
        assertXpathEvaluatesTo("2", "//csw:SearchResults/@numberOfRecordsReturned", d);
        assertXpathEvaluatesTo("2", "count(//csw:SearchResults/*)", d);
        assertXpathExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Streams']", d);
        assertXpathExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Seven']", d);
    }

    @Test
    public void testFullTextSearch() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=gmd:MD_Metadata&resultType=results&elementSetName=brief&outputSchema=http://www.isotc211.org/2005/gmd&constraint=AnyText like '%25about B%25'";
        Document d = getAsDOM(request);
        // print(d);
        // validateSchema(d.getElementsByTagName("//gmd:MD_MetaData"));
        // basic checks
        assertXpathEvaluatesTo("3", "//csw:SearchResults/@numberOfRecordsMatched", d);
        assertXpathEvaluatesTo("3", "//csw:SearchResults/@numberOfRecordsReturned", d);
        assertXpathEvaluatesTo("0", "//csw:SearchResults/@nextRecord", d);
        assertXpathEvaluatesTo("3", "count(//csw:SearchResults/*)", d);
        assertXpathExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='BasicPolygons']", d);
        assertXpathExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Bridges']", d);
        assertXpathExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Buildings']", d);
    }

    @Test
    public void testFilterBBox() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&outputSchema=http://www.isotc211.org/2005/gmd&typeNames=gmd:MD_Metadata&resultType=results" + "&constraint=BBOX(BoundingBox, -250, -250, -190, -100)&maxRecords=100";
        Document d = getAsDOM(request);
        // print(d);
        // validateSchema(d.getElementsByTagName("//gmd:MD_MetaData"));
        // basic checks
        // assertXpathEvaluatesTo("15", "//csw:SearchResults/@numberOfRecordsMatched", d);
        // assertXpathEvaluatesTo("10", "//csw:SearchResults/@numberOfRecordsReturned", d);
        // assertXpathEvaluatesTo("11", "//csw:SearchResults/@nextRecord", d);
        // assertXpathEvaluatesTo("10", "count(//csw:SearchResults/*)", d);
        // verify we got the expected records
        assertXpathExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Forests']", d);
    }

    @Test
    public void testTitleFilterCSWRecord() throws Exception {
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=csw:Record&resultType=results&elementSetName=brief&outputSchema=http://www.isotc211.org/2005/gmd&constraint=dc:title like 'S%25'";
        Document d = getAsDOM(request);
        // print(d);
        // validateSchema(d.getElementsByTagName("//gmd:MD_MetaData"));
        assertXpathEvaluatesTo("2", "//csw:SearchResults/@numberOfRecordsMatched", d);
        assertXpathEvaluatesTo("2", "//csw:SearchResults/@numberOfRecordsReturned", d);
        assertXpathEvaluatesTo("2", "count(//csw:SearchResults/*)", d);
        assertXpathExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Streams']", d);
        assertXpathExists("//gmd:MD_Metadata[gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString='Seven']", d);
    }

    /**
     * Tests CSW service disabled on layer-resource
     */
    @Test
    public void testLayerDisabledServiceRecords() throws Exception {
        disableCWSOnLinesLayer();
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=gmd:MD_Metadata" + "&resultType=results&elementSetName=full&outputSchema=http://www.isotc211.org/2005/gmd";
        Document doc = getAsDOM(request);
        assertXpathEvaluatesTo("0", ("count(/csw:GetRecordsResponse/csw:SearchResults/gmd:MD_Metadata/" + ("gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/" + "gmd:onLine/gmd:CI_OnlineResource/gmd:name/gco:CharacterString[.='Lines'])")), doc);
        enableCWSOnLinesLayer();
    }

    /**
     * Tests CSW service enabled on layer-resource
     */
    @Test
    public void testLayerEnabledServiceRecords() throws Exception {
        enableCWSOnLinesLayer();
        String request = "csw?service=CSW&version=2.0.2&request=GetRecords&typeNames=gmd:MD_Metadata" + "&resultType=results&elementSetName=full&outputSchema=http://www.isotc211.org/2005/gmd";
        Document doc = getAsDOM(request);
        assertXpathEvaluatesTo("2", ("count(/csw:GetRecordsResponse/csw:SearchResults/gmd:MD_Metadata/" + ("gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/" + "gmd:onLine/gmd:CI_OnlineResource/gmd:name/gco:CharacterString[.='Lines'])")), doc);
    }
}

