package com.baeldung.common.web;


import ContentType.JSON;
import com.baeldung.persistence.model.Foo;
import com.baeldung.web.util.HTTPLinkHeaderUtil;
import com.google.common.net.HttpHeaders;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractBasicLiveTest<T extends Serializable> extends AbstractLiveTest<T> {
    public AbstractBasicLiveTest(final Class<T> clazzToSet) {
        super(clazzToSet);
    }

    // find - all - paginated
    @Test
    public void whenResourcesAreRetrievedPaged_then200IsReceived() {
        create();
        final Response response = RestAssured.get(((getURL()) + "?page=0&size=10"));
        Assert.assertThat(response.getStatusCode(), AbstractBasicLiveTest.is(200));
    }

    @Test
    public void whenPageOfResourcesAreRetrievedOutOfBounds_then404IsReceived() {
        final String url = (((getURL()) + "?page=") + (RandomStringUtils.randomNumeric(5))) + "&size=10";
        final Response response = RestAssured.get(url);
        Assert.assertThat(response.getStatusCode(), AbstractBasicLiveTest.is(404));
    }

    @Test
    public void givenResourcesExist_whenFirstPageIsRetrieved_thenPageContainsResources() {
        create();
        final Response response = RestAssured.get(((getURL()) + "?page=0&size=10"));
        Assert.assertFalse(response.body().as(List.class).isEmpty());
    }

    @Test
    public void whenFirstPageOfResourcesAreRetrieved_thenSecondPageIsNext() {
        create();
        create();
        create();
        final Response response = RestAssured.get(((getURL()) + "?page=0&size=2"));
        final String uriToNextPage = HTTPLinkHeaderUtil.extractURIByRel(response.getHeader(HttpHeaders.LINK), "next");
        Assert.assertEquals(((getURL()) + "?page=1&size=2"), uriToNextPage);
    }

    @Test
    public void whenFirstPageOfResourcesAreRetrieved_thenNoPreviousPage() {
        final Response response = RestAssured.get(((getURL()) + "?page=0&size=2"));
        final String uriToPrevPage = HTTPLinkHeaderUtil.extractURIByRel(response.getHeader(HttpHeaders.LINK), "prev");
        Assert.assertNull(uriToPrevPage);
    }

    @Test
    public void whenSecondPageOfResourcesAreRetrieved_thenFirstPageIsPrevious() {
        create();
        create();
        final Response response = RestAssured.get(((getURL()) + "?page=1&size=2"));
        final String uriToPrevPage = HTTPLinkHeaderUtil.extractURIByRel(response.getHeader(HttpHeaders.LINK), "prev");
        Assert.assertEquals(((getURL()) + "?page=0&size=2"), uriToPrevPage);
    }

    @Test
    public void whenLastPageOfResourcesIsRetrieved_thenNoNextPageIsDiscoverable() {
        create();
        create();
        create();
        final Response first = RestAssured.get(((getURL()) + "?page=0&size=2"));
        final String uriToLastPage = HTTPLinkHeaderUtil.extractURIByRel(first.getHeader(HttpHeaders.LINK), "last");
        final Response response = RestAssured.get(uriToLastPage);
        final String uriToNextPage = HTTPLinkHeaderUtil.extractURIByRel(response.getHeader(HttpHeaders.LINK), "next");
        Assert.assertNull(uriToNextPage);
    }

    // etags
    @Test
    public void givenResourceExists_whenRetrievingResource_thenEtagIsAlsoReturned() {
        // Given
        final String uriOfResource = createAsUri();
        // When
        final Response findOneResponse = RestAssured.given().header("Accept", "application/json").get(uriOfResource);
        // Then
        Assert.assertNotNull(findOneResponse.getHeader(HttpHeaders.ETAG));
    }

    @Test
    public void givenResourceWasRetrieved_whenRetrievingAgainWithEtag_thenNotModifiedReturned() {
        // Given
        final String uriOfResource = createAsUri();
        final Response findOneResponse = RestAssured.given().header("Accept", "application/json").get(uriOfResource);
        final String etagValue = findOneResponse.getHeader(HttpHeaders.ETAG);
        // When
        final Response secondFindOneResponse = RestAssured.given().header("Accept", "application/json").headers("If-None-Match", etagValue).get(uriOfResource);
        // Then
        Assert.assertTrue(((secondFindOneResponse.getStatusCode()) == 304));
    }

    @Test
    public void givenResourceWasRetrievedThenModified_whenRetrievingAgainWithEtag_thenResourceIsReturned() {
        // Given
        final String uriOfResource = createAsUri();
        final Response firstFindOneResponse = RestAssured.given().header("Accept", "application/json").get(uriOfResource);
        final String etagValue = firstFindOneResponse.getHeader(HttpHeaders.ETAG);
        final long createdId = firstFindOneResponse.jsonPath().getLong("id");
        Foo updatedFoo = new Foo("updated value");
        updatedFoo.setId(createdId);
        Response updatedResponse = RestAssured.given().contentType(JSON).body(updatedFoo).put(uriOfResource);
        Assert.assertThat(((updatedResponse.getStatusCode()) == 200));
        // When
        final Response secondFindOneResponse = RestAssured.given().header("Accept", "application/json").headers("If-None-Match", etagValue).get(uriOfResource);
        // Then
        Assert.assertTrue(((secondFindOneResponse.getStatusCode()) == 200));
    }
}

