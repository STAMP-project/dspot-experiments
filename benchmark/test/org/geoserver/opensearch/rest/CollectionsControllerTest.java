/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.opensearch.rest;


import CollectionPart.Collection;
import CollectionPart.Description;
import CollectionPart.Metadata;
import CollectionPart.OwsLinks;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.DocumentContext;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.sf.json.JSONObject;
import org.geoserver.opensearch.rest.CollectionsController.CollectionPart;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class CollectionsControllerTest extends OSEORestTestSupport {
    @Test
    public void testGetCollections() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections", 200);
        Assert.assertEquals(5, json.read("$.collections.*", List.class).size());
        // check the first (sorted alphabetically)
        Assert.assertEquals("ATMTEST", json.read("$.collections[0].name"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/ATMTEST", json.read("$.collections[0].href"));
        Assert.assertEquals("http://localhost:8080/geoserver/oseo/description?parentId=ATMTEST", json.read("$.collections[0].search"));
    }

    @Test
    public void testGetCollectionsPaging() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections?offset=3&limit=1", 200);
        Assert.assertEquals(1, json.read("$.collections.*", List.class).size());
        Assert.assertEquals("SENTINEL1", json.read("$.collections[0].name"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL1", json.read("$.collections[0].href"));
        Assert.assertEquals("http://localhost:8080/geoserver/oseo/description?parentId=SENTINEL1", json.read("$.collections[0].search"));
    }

    @Test
    public void testGetCollectionsPagingValidation() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections?offset=-1");
        Assert.assertEquals(400, response.getStatus());
        Assert.assertThat(response.getErrorMessage(), Matchers.containsString("offset"));
        response = getAsServletResponse("/rest/oseo/collections?limit=-1");
        Assert.assertEquals(400, response.getStatus());
        Assert.assertThat(response.getErrorMessage(), Matchers.containsString("limit"));
        response = getAsServletResponse("/rest/oseo/collections?limit=1000");
        Assert.assertEquals(400, response.getStatus());
        Assert.assertThat(response.getErrorMessage(), Matchers.containsString("limit"));
    }

    @Test
    public void testNonExistingCollection() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/foobar");
        Assert.assertEquals(404, response.getStatus());
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("foobar"));
    }

    @Test
    public void testGetCollection() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SENTINEL2", 200);
        Assert.assertEquals("SENTINEL2", json.read("$.id"));
        Assert.assertEquals("Feature", json.read("$.type"));
        Assert.assertEquals("SENTINEL2", json.read("$.properties.name"));
        Assert.assertEquals("S2MSI1C", json.read("$.properties['eo:productType']"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/ogcLinks", json.read("$.properties['ogcLinksHref']"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/metadata", json.read("$.properties['metadataHref']"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/description", json.read("$.properties['descriptionHref']"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/thumbnail", json.read("$.properties['thumbnailHref']"));
    }

    @Test
    public void testCreateCollectionNotJson() throws Exception {
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections", "This is not JSON", MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testCreateCollectionNotGeoJson() throws Exception {
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections", "{foo: 45}", MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testCreateInvalidAttributeSyntax() throws Exception {
        String testData = new String(getTestData("/collection.json"), "UTF-8");
        // inject an invalid attribute name
        String invalidTestData = testData.replace("primary", "1:2:primary");
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections", invalidTestData, MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(400, response.getStatus());
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("1:2:primary"));
    }

    @Test
    public void testCreateInvalidAttributePrefix() throws Exception {
        String testData = new String(getTestData("/collection.json"), "UTF-8");
        // inject an invalid attribute name
        String invalidTestData = testData.replace("eo:productType", "abc:productType");
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections", invalidTestData, MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(400, response.getStatus());
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("abc:productType"));
    }

    @Test
    public void testCreateInvalidAttributeName() throws Exception {
        String testData = new String(getTestData("/collection.json"), "UTF-8");
        // inject an invalid attribute name
        String invalidTestData = testData.replace("eo:productType", "eo:newProductType");
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections", invalidTestData, MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(400, response.getStatus());
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("eo:newProductType"));
    }

    @Test
    public void testCreateCollection() throws Exception {
        MockHttpServletResponse response;
        createTest123Collection();
        assertTest123CollectionCreated();
    }

    @Test
    public void testUpdateCollection() throws Exception {
        MockHttpServletResponse response;
        createTest123Collection();
        // grab the JSON to modify some bits
        JSONObject feature = ((JSONObject) (getAsJSON("/rest/oseo/collections/TEST123")));
        JSONObject properties = feature.getJSONObject("properties");
        properties.element("eo:productType", "PT-123");
        properties.element("timeStart", "2017-01-01T00:00:00Z");
        // send it back
        response = putAsServletResponse("rest/oseo/collections/TEST123", feature.toString(), "application/json");
        Assert.assertEquals(200, response.getStatus());
        // check the changes
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/TEST123", 200);
        Assert.assertEquals("PT-123", json.read("$.properties['eo:productType']"));
        Assert.assertEquals("2017-01-01T00:00:00.000+0000", json.read("$.properties['timeStart']"));
    }

    @Test
    public void testDeleteCollection() throws Exception {
        MockHttpServletResponse response;
        createTest123Collection();
        // it's there
        getAsJSONPath("/rest/oseo/collections/TEST123", 200);
        // and now kill the poor beast
        response = deleteAsServletResponse("/rest/oseo/collections/TEST123");
        Assert.assertEquals(200, response.getStatus());
        // no more there
        response = getAsServletResponse("/rest/oseo/collections/TEST123");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetCollectionLinks() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SENTINEL2/ogcLinks", 200);
        Assert.assertEquals("http://www.opengis.net/spec/owc/1.0/req/atom/wms", json.read("$.links[0].offering"));
        Assert.assertEquals("GET", json.read("$.links[0].method"));
        Assert.assertEquals("GetCapabilities", json.read("$.links[0].code"));
        Assert.assertEquals("application/xml", json.read("$.links[0].type"));
        Assert.assertEquals("${BASE_URL}/sentinel2/ows?service=wms&version=1.3.0&request=GetCapabilities", json.read("$.links[0].href"));
    }

    @Test
    public void testPutCollectionLinks() throws Exception {
        MockHttpServletResponse response;
        createTest123Collection();
        // create the links
        response = putAsServletResponse("rest/oseo/collections/TEST123/ogcLinks", getTestData("/test123-links.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(200, response.getStatus());
        // check they are there
        assertTest123Links();
    }

    @Test
    public void testDeleteCollectionLinks() throws Exception {
        testPutCollectionLinks();
        // delete the links
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/TEST123/ogcLinks");
        Assert.assertEquals(200, response.getStatus());
        // check they are gone
        response = getAsServletResponse("rest/oseo/collections/TEST123/ogcLinks");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetCollectionMetadata() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/metadata");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/xml", response.getContentType());
        Assert.assertThat(response.getContentAsString(), Matchers.both(Matchers.containsString("gmi:MI_Metadata")).and(Matchers.containsString("Sentinel-2")));
    }

    @Test
    public void testPutCollectionMetadata() throws Exception {
        MockHttpServletResponse response;
        createTest123Collection();
        // create the metadata
        response = putAsServletResponse("rest/oseo/collections/TEST123/metadata", getTestData("/test123-metadata.xml"), MediaType.TEXT_XML_VALUE);
        Assert.assertEquals(200, response.getStatus());
        // grab and check
        assertTest123Metadata();
    }

    @Test
    public void testDeleteCollectionMetadata() throws Exception {
        // creates the collection and adds the metadata
        testPutCollectionMetadata();
        // now remove
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/TEST123/metadata");
        Assert.assertEquals(200, response.getStatus());
        // check it's not there anymore
        response = getAsServletResponse("rest/oseo/collections/TEST123/metadata");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetCollectionDescription() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/description");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/html", response.getContentType());
        Assert.assertThat(response.getContentAsString(), Matchers.both(Matchers.containsString("<table>")).and(Matchers.containsString("Sentinel-2")));
    }

    @Test
    public void testPutCollectionDescription() throws Exception {
        MockHttpServletResponse response;
        createTest123Collection();
        // create the description
        response = putAsServletResponse("rest/oseo/collections/TEST123/description", getTestData("/test123-description.html"), MediaType.TEXT_HTML_VALUE);
        Assert.assertEquals(200, response.getStatus());
        // grab and check
        assertTest123Description();
    }

    @Test
    public void testDeleteCollectionDescription() throws Exception {
        // creates the collection and adds the metadata
        testPutCollectionDescription();
        // now remove
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/TEST123/description");
        Assert.assertEquals(200, response.getStatus());
        // check it's not there anymore
        response = getAsServletResponse("rest/oseo/collections/TEST123/description");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetCollectionThumbnail() throws Exception {
        // missing from the DB right now
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/thumbnail");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testCreateCollectionAsZip() throws Exception {
        // build all possible combinations of elements in the zip and check they all work
        Set<Set<CollectionPart>> sets = Sets.powerSet(new java.util.HashSet(Arrays.asList(Collection, Description, Metadata, OwsLinks)));
        for (Set<CollectionPart> parts : sets) {
            if (parts.isEmpty()) {
                continue;
            }
            cleanupTestCollection();
            testCreateCollectionAsZip(parts);
        }
    }
}

