/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.opensearch.eo;


import DescriptionResponse.OS_DESCRIPTION_MIME;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class DescriptionTest extends OSEOTestSupport {
    @Test
    public void testExceptionInternalError() throws Exception {
        final GeoServer gs = getGeoServer();
        OSEOInfo service = gs.getService(OSEOInfo.class);
        String storeId = service.getOpenSearchAccessStoreId();
        // this will raise an internal error
        service.setOpenSearchAccessStoreId(null);
        try {
            gs.save(service);
            // run a request that's going to fail
            Document dom = getAsOpenSearchException("oseo/description", 500);
            // print(dom);
            Assert.assertThat(dom, hasXPath("/rss/channel/item/title", Matchers.equalTo("OpenSearchAccess is not configured in the OpenSearch for EO panel, please do so")));
            Assert.assertThat(dom, hasXPath("/rss/channel/item/description", Matchers.equalTo("OpenSearchAccess is not configured in the OpenSearch for EO panel, please do so")));
        } finally {
            // reset old values
            service.setOpenSearchAccessStoreId(storeId);
            gs.save(service);
        }
    }

    @Test
    public void testExceptionInternalErrorVerbose() throws Exception {
        final GeoServer gs = getGeoServer();
        GeoServerInfo gsInfo = gs.getGlobal();
        gsInfo.getSettings().setVerboseExceptions(true);
        gs.save(gsInfo);
        OSEOInfo service = gs.getService(OSEOInfo.class);
        String storeId = service.getOpenSearchAccessStoreId();
        // this will raise an internal error
        service.setOpenSearchAccessStoreId(null);
        try {
            gs.save(service);
            // run a request that's going to fail
            Document dom = getAsOpenSearchException("oseo/description", 500);
            // print(dom);
            Assert.assertThat(dom, hasXPath("/rss/channel/item/title", Matchers.equalTo("OpenSearchAccess is not configured in the OpenSearch for EO panel, please do so")));
            Assert.assertThat(dom, hasXPath("/rss/channel/item/description", Matchers.startsWith("OpenSearchAccess is not configured in the OpenSearch for EO panel, please do so")));
            Assert.assertThat(dom, hasXPath("/rss/channel/item/description", Matchers.containsString("org.geoserver.platform.OWS20Exception")));
        } finally {
            // reset old values
            service.setOpenSearchAccessStoreId(storeId);
            gs.save(service);
            gsInfo.getSettings().setVerboseExceptions(false);
            gs.save(gsInfo);
        }
    }

    @Test
    public void testExceptionInvalidParentId() throws Exception {
        // run a request that's going to fail
        Document dom = getAsOpenSearchException("oseo/description?parentId=IAmNotThere", 400);
        // print(dom);
        Assert.assertThat(dom, hasXPath("/rss/channel/item/title", Matchers.equalTo("Unknown parentId 'IAmNotThere'")));
    }

    @Test
    public void testGlobalDescription() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("oseo/description");
        Assert.assertEquals(OS_DESCRIPTION_MIME, response.getContentType());
        Assert.assertEquals(200, response.getStatus());
        Document dom = dom(new java.io.ByteArrayInputStream(response.getContentAsByteArray()));
        // print(dom);
        // generic contents check
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription"));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:ShortName", Matchers.equalTo("OSEO")));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:LongName", Matchers.equalTo("OpenSearch for Earth Observation")));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:Description", Matchers.containsString("Earth Observation")));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:Tags", Matchers.equalTo("EarthObservation OGC CEOS-OS-BP-V1.2/L1")));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:LongName", Matchers.containsString("OpenSearch")));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:SyndicationRight", Matchers.equalTo("open")));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:AdultContent", Matchers.equalTo("false")));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:Language", Matchers.equalTo("en-us")));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:OutputEncoding", Matchers.equalTo("UTF-8")));
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription/os:InputEncoding", Matchers.equalTo("UTF-8")));
        // check the self link
        Assert.assertThat(dom, hasXPath(("/os:OpenSearchDescription/os:Url[@rel='self' " + "and @type='application/opensearchdescription+xml']")));
        Assert.assertThat(dom, hasXPath(("/os:OpenSearchDescription/os:Url[@rel='self' " + "and @type='application/opensearchdescription+xml']/@template"), Matchers.containsString("/oseo/description")));
        // check the result link
        String resultsBase = "/os:OpenSearchDescription/os:Url[@rel='collection'and @type='application/atom+xml']";
        Assert.assertThat(dom, hasXPath(resultsBase));
        Assert.assertThat(dom, hasXPath((resultsBase + "/@template"), // 
        // 
        // 
        Matchers.allOf(Matchers.containsString("/oseo/search?"), Matchers.containsString("searchTerms={searchTerms?}"), Matchers.containsString("lat={geo:lat?}"), Matchers.containsString("timeStart={time:start?}"))));
        Assert.assertThat(dom, hasXPath((resultsBase + "/@indexOffset"), Matchers.equalTo("1")));
        // check some parameters have been described
        String paramBase = resultsBase + "/param:Parameter";
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='searchTerms' and @value='{searchTerms}' and @minimum='0']")));
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='count' and @value='{count}' and @minimum='0' and  @minInclusive='0' and @maxInclusive='100']")));
        // search profiles
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='searchTerms']/atom:link[@rel='profile' and @href='http://localhost:8080/geoserver/docs/searchTerms.html']")));
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='geometry']/atom:link[@rel='profile' and @href='http://www.opengis.net/wkt/LINESTRING']")));
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='geometry']/atom:link[@rel='profile' and @href='http://www.opengis.net/wkt/POINT']")));
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='geometry']/atom:link[@rel='profile' and @href='http://www.opengis.net/wkt/POLYGON']")));
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='geometry']/atom:link[@rel='profile' and @href='http://www.opengis.net/wkt/MULTILINESTRING']")));
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='geometry']/atom:link[@rel='profile' and @href='http://www.opengis.net/wkt/MULTIPOINT']")));
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='geometry']/atom:link[@rel='profile' and @href='http://www.opengis.net/wkt/MULTIPOLYGON']")));
        // check some EO parameter
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='wavelength' and @value='{eo:wavelength}' and @minimum='0']")));
        Assert.assertThat(dom, hasXPath((paramBase + "[@name='identifier' and @value='{eo:identifier}' and @minimum='0']")));
        // general validation
        checkValidOSDD(dom);
    }

    @Test
    public void testOpticalCollectionDescription() throws Exception {
        Document dom = getAsDOM("oseo/description?parentId=SENTINEL2");
        print(dom);
        // we got a opensearch descriptor
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription"));
        // self is there and uses the right parentId
        Assert.assertThat(dom, hasXPath(("/os:OpenSearchDescription/os:Url[@rel='self' " + "and @type='application/opensearchdescription+xml']")));
        Assert.assertThat(dom, hasXPath(("/os:OpenSearchDescription/os:Url[@rel='self' " + "and @type='application/opensearchdescription+xml']/@template"), Matchers.containsString("/oseo/description?parentId=SENTINEL2")));
        // check the results link is there
        String resultsBase = "/os:OpenSearchDescription/os:Url[@rel='results'and @type='application/atom+xml']";
        Assert.assertThat(dom, hasXPath(resultsBase));
        // ... and has the right parentId, and basic search params
        Assert.assertThat(dom, hasXPath((resultsBase + "/@template"), // 
        // 
        // 
        // 
        Matchers.allOf(Matchers.containsString("/oseo/search?"), Matchers.containsString("parentId=SENTINEL2"), Matchers.containsString("searchTerms={searchTerms?}"), Matchers.containsString("lat={geo:lat?}"), Matchers.containsString("timeStart={time:start?}"))));
        // ... and has generic EOP parameters
        Assert.assertThat(dom, hasXPath((resultsBase + "/@template"), // 
        // 
        Matchers.allOf(Matchers.containsString("productQualityStatus={eo:productQualityStatus?}"), Matchers.containsString("processorName={eo:processorName?}"), Matchers.containsString("modificationDate={eo:modificationDate?}"))));
        // ... and has OPT parameters
        Assert.assertThat(dom, hasXPath((resultsBase + "/@template"), // 
        Matchers.allOf(Matchers.containsString("cloudCover={eo:cloudCover?}"), Matchers.containsString("snowCover={eo:snowCover?}"))));
        // ... but no SAR parameters
        Assert.assertThat(dom, hasXPath((resultsBase + "/@template"), Matchers.not(// 
        Matchers.anyOf(Matchers.containsString("polarisationMode={eo:polarisationMode?}"), Matchers.containsString("polarisationChannels={eo:polarisationChannels?}")))));
    }

    @Test
    public void testRadarCollectionDescription() throws Exception {
        Document dom = getAsDOM("oseo/description?parentId=SENTINEL1");
        print(dom);
        // we got a opensearch descriptor
        Assert.assertThat(dom, hasXPath("/os:OpenSearchDescription"));
        // self is there and uses the right parentId
        Assert.assertThat(dom, hasXPath(("/os:OpenSearchDescription/os:Url[@rel='self' " + "and @type='application/opensearchdescription+xml']")));
        Assert.assertThat(dom, hasXPath(("/os:OpenSearchDescription/os:Url[@rel='self' " + "and @type='application/opensearchdescription+xml']/@template"), Matchers.containsString("/oseo/description?parentId=SENTINEL1")));
        // check the results link is there
        String resultsBase = "/os:OpenSearchDescription/os:Url[@rel='results'and @type='application/atom+xml']";
        Assert.assertThat(dom, hasXPath(resultsBase));
        // ... and has the right parentId, and basic search params
        Assert.assertThat(dom, hasXPath((resultsBase + "/@template"), // 
        // 
        // 
        // 
        // 
        // 
        Matchers.allOf(Matchers.containsString("/oseo/search?"), Matchers.containsString("parentId=SENTINEL1"), Matchers.containsString("searchTerms={searchTerms?}"), Matchers.containsString("lat={geo:lat?}"), Matchers.containsString("timeStart={time:start?}"), Matchers.containsString("timeEnd={time:end?}"), Matchers.containsString("timeRelation={time:relation?}"))));
        // ... and has generic EOP parameters
        Assert.assertThat(dom, hasXPath((resultsBase + "/@template"), // 
        // 
        Matchers.allOf(Matchers.containsString("productQualityStatus={eo:productQualityStatus?}"), Matchers.containsString("processorName={eo:processorName?}"), Matchers.containsString("modificationDate={eo:modificationDate?}"))));
        // ... and SAR parameters
        Assert.assertThat(dom, hasXPath((resultsBase + "/@template"), // 
        Matchers.allOf(Matchers.containsString("polarisationMode={eo:polarisationMode?}"), Matchers.containsString("polarisationChannels={eo:polarisationChannels?}"))));
        // ... but no OPT parameters
        Assert.assertThat(dom, hasXPath((resultsBase + "/@template"), Matchers.not(// 
        Matchers.anyOf(Matchers.containsString("cloudCover={eo:cloudCover?}"), Matchers.containsString("snowCover={eo:snowCover?}")))));
    }
}

