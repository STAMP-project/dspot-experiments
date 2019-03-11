/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.inspire.wfs;


import CREATE_EXTENDED_CAPABILITIES.key;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.config.ServiceInfo;
import org.geoserver.inspire.InspireSchema;
import org.geoserver.inspire.InspireTestSupport;
import org.geoserver.inspire.UniqueResourceIdentifier;
import org.geoserver.inspire.UniqueResourceIdentifiers;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wfs.WFSInfo;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class WFSExtendedCapabilitiesTest extends GeoServerSystemTestSupport {
    private static final String WFS_1_0_0_GETCAPREQUEST = "wfs?request=GetCapabilities&service=WFS&version=1.0.0";

    private static final String WFS_1_1_0_GETCAPREQUEST = "wfs?request=GetCapabilities&service=WFS&version=1.1.0";

    private static final String WFS_2_0_0_GETCAPREQUEST = "wfs?request=GetCapabilities&service=WFS&acceptVersions=2.0.0";

    @Test
    public void testNoInspireSettings() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        final NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 0, nodeList.getLength());
    }

    @Test
    public void testCreateExtCapsOff() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, false);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        final NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 0, nodeList.getLength());
    }

    @Test
    public void testExtendedCaps110WithFullSettings() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one;two,http://www.geoserver.org/two,http://metadata.geoserver.org/id?two");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_1_1_0_GETCAPREQUEST);
        NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 1, nodeList.getLength());
        String schemaLocation = dom.getDocumentElement().getAttribute("xsi:schemaLocation");
        InspireTestSupport.assertSchemaLocationContains(schemaLocation, InspireSchema.DLS_NAMESPACE, InspireSchema.DLS_SCHEMA);
        final Element extendedCaps = ((Element) (nodeList.item(0)));
        InspireTestSupport.assertInspireCommonScenario1Response(extendedCaps, "http://foo.com?bar=baz", "application/vnd.iso.19139+xml", "fre");
        final UniqueResourceIdentifiers ids = new UniqueResourceIdentifiers();
        ids.add(new UniqueResourceIdentifier("one", "http://www.geoserver.org/one"));
        ids.add(new UniqueResourceIdentifier("two", "http://www.geoserver.org/two", "http://metadata.geoserver.org/id?two"));
        InspireTestSupport.assertInspireDownloadSpatialDataSetIdentifierResponse(extendedCaps, ids);
    }

    @Test
    public void testExtendedCaps200WithFullSettings() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one;two,http://www.geoserver.org/two,http://metadata.geoserver.org/id?two");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 1, nodeList.getLength());
        String schemaLocation = dom.getDocumentElement().getAttribute("xsi:schemaLocation");
        InspireTestSupport.assertSchemaLocationContains(schemaLocation, InspireSchema.DLS_NAMESPACE, InspireSchema.DLS_SCHEMA);
        final Element extendedCaps = ((Element) (nodeList.item(0)));
        InspireTestSupport.assertInspireCommonScenario1Response(extendedCaps, "http://foo.com?bar=baz", "application/vnd.iso.19139+xml", "fre");
        final UniqueResourceIdentifiers ids = new UniqueResourceIdentifiers();
        ids.add(new UniqueResourceIdentifier("one", "http://www.geoserver.org/one"));
        ids.add(new UniqueResourceIdentifier("two", "http://www.geoserver.org/two", "http://metadata.geoserver.org/id?two"));
        InspireTestSupport.assertInspireDownloadSpatialDataSetIdentifierResponse(extendedCaps, ids);
    }

    @Test
    public void testReloadSettings() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one;two,http://www.geoserver.org/two,http://metadata.geoserver.org/id?two");
        getGeoServer().save(serviceInfo);
        getGeoServer().reload();
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements after settings reload", 1, nodeList.getLength());
    }

    // No INSPIRE ExtendedCapabilities should be returned in a WFS 1.0.0 response
    @Test
    public void testExtCaps100WithFullSettings() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one;two,http://www.geoserver.org/two,http://metadata.geoserver.org/id?two");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_1_0_0_GETCAPREQUEST);
        final NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertTrue(((nodeList.getLength()) == 0));
    }

    // Test ExtendedCapabilities is not produced if required settings missing
    @Test
    public void testNoMetadataUrl() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one;two,http://www.geoserver.org/two,http://metadata.geoserver.org/id?two");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        final NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 0, nodeList.getLength());
    }

    @Test
    public void testNoSpatialDataset() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        final NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 0, nodeList.getLength());
    }

    @Test
    public void testNoSpatialDatasetCode() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, ",http://www.geoserver.org/one;,http://www.geoserver.org/two,http://metadata.geoserver.org/id?two");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        final NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 0, nodeList.getLength());
    }

    // Test ExtendedCapabilities response when optional settings missing
    @Test
    public void testNoMediaType() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one;two,http://www.geoserver.org/two,http://metadata.geoserver.org/id?two");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 1, nodeList.getLength());
        nodeList = dom.getElementsByTagNameNS(InspireSchema.COMMON_NAMESPACE, "MediaType");
        Assert.assertEquals("Number of MediaType elements", 0, nodeList.getLength());
    }

    // If settings were created with older version of INSPIRE extension before
    // the on/off check box setting existed we create the extended capabilities
    // if the other required settings exist and don't if they don't
    @Test
    public void testCreateExtCapMissingWithRequiredSettings() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 1, nodeList.getLength());
    }

    @Test
    public void testCreateExtCapMissingWithoutRequiredSettings() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        getGeoServer().save(serviceInfo);
        final Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        final NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        Assert.assertEquals("Number of INSPIRE ExtendedCapabilities elements", 0, nodeList.getLength());
    }

    @Test
    public void testChangeMediaType() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one");
        getGeoServer().save(serviceInfo);
        Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.COMMON_NAMESPACE, "MetadataUrl");
        Assert.assertEquals("Number of MediaType elements", 1, nodeList.getLength());
        Element mdUrl = ((Element) (nodeList.item(0)));
        InspireTestSupport.assertInspireMetadataUrlResponse(mdUrl, "http://foo.com?bar=baz", "application/vnd.iso.19139+xml");
        serviceInfo.getMetadata().put(SERVICE_METADATA_TYPE.key, "application/vnd.ogc.csw.GetRecordByIdResponse_xml");
        getGeoServer().save(serviceInfo);
        dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        nodeList = dom.getElementsByTagNameNS(InspireSchema.COMMON_NAMESPACE, "MetadataUrl");
        Assert.assertEquals("Number of MediaType elements", 1, nodeList.getLength());
        mdUrl = ((Element) (nodeList.item(0)));
        InspireTestSupport.assertInspireMetadataUrlResponse(mdUrl, "http://foo.com?bar=baz", "application/vnd.ogc.csw.GetRecordByIdResponse_xml");
    }

    @Test
    public void testAddSpatialDatasetIdentifier() throws Exception {
        final ServiceInfo serviceInfo = getGeoServer().getService(WFSInfo.class);
        final MetadataMap metadata = serviceInfo.getMetadata();
        InspireTestSupport.clearInspireMetadata(metadata);
        metadata.put(key, true);
        metadata.put(SERVICE_METADATA_URL.key, "http://foo.com?bar=baz");
        metadata.put(SERVICE_METADATA_TYPE.key, "application/vnd.iso.19139+xml");
        metadata.put(LANGUAGE.key, "fre");
        metadata.put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, "one,http://www.geoserver.org/one");
        getGeoServer().save(serviceInfo);
        Document dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        NodeList nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "SpatialDataSetIdentifier");
        Assert.assertEquals(1, nodeList.getLength());
        serviceInfo.getMetadata().put(SPATIAL_DATASET_IDENTIFIER_TYPE.key, ((metadata.get(SPATIAL_DATASET_IDENTIFIER_TYPE.key)) + ";two,,http://metadata.geoserver.org/id?two"));
        getGeoServer().save(serviceInfo);
        dom = getAsDOM(WFSExtendedCapabilitiesTest.WFS_2_0_0_GETCAPREQUEST);
        nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "SpatialDataSetIdentifier");
        Assert.assertEquals(2, nodeList.getLength());
        final UniqueResourceIdentifiers ids = new UniqueResourceIdentifiers();
        ids.add(new UniqueResourceIdentifier("one", "http://www.geoserver.org/one"));
        ids.add(new UniqueResourceIdentifier("two", null, "http://metadata.geoserver.org/id?two"));
        nodeList = dom.getElementsByTagNameNS(InspireSchema.DLS_NAMESPACE, "ExtendedCapabilities");
        final Element extendedCaps = ((Element) (nodeList.item(0)));
        InspireTestSupport.assertInspireDownloadSpatialDataSetIdentifierResponse(extendedCaps, ids);
    }
}

