package com.baeldung.common.web;


import MediaType.APPLICATION_JSON_VALUE;
import com.baeldung.persistence.model.Foo;
import com.baeldung.web.util.HTTPLinkHeaderUtil;
import com.google.common.net.HttpHeaders;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.io.Serializable;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.AnyOf;
import org.junit.Assert;
import org.junit.Test;


// template method
public abstract class AbstractDiscoverabilityLiveTest<T extends Serializable> extends AbstractLiveTest<T> {
    public AbstractDiscoverabilityLiveTest(final Class<T> clazzToSet) {
        super(clazzToSet);
    }

    // tests
    // discoverability
    @Test
    public void whenInvalidPOSTIsSentToValidURIOfResource_thenAllowHeaderListsTheAllowedActions() {
        // Given
        final String uriOfExistingResource = createAsUri();
        // When
        final Response res = RestAssured.post(uriOfExistingResource);
        // Then
        final String allowHeader = res.getHeader(HttpHeaders.ALLOW);
        Assert.assertThat(allowHeader, AnyOf.anyOf(AbstractDiscoverabilityLiveTest.containsString("GET"), AbstractDiscoverabilityLiveTest.containsString("PUT"), AbstractDiscoverabilityLiveTest.containsString("DELETE")));
    }

    @Test
    public void whenResourceIsCreated_thenUriOfTheNewlyCreatedResourceIsDiscoverable() {
        // When
        final Foo newResource = new Foo(RandomStringUtils.randomAlphabetic(6));
        final Response createResp = RestAssured.given().contentType(APPLICATION_JSON_VALUE).body(newResource).post(getURL());
        final String uriOfNewResource = createResp.getHeader(HttpHeaders.LOCATION);
        // Then
        final Response response = RestAssured.given().header(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE).get(uriOfNewResource);
        final Foo resourceFromServer = response.body().as(Foo.class);
        Assert.assertThat(newResource, Matchers.equalTo(resourceFromServer));
    }

    @Test
    public void whenResourceIsRetrieved_thenUriToGetAllResourcesIsDiscoverable() {
        // Given
        final String uriOfExistingResource = createAsUri();
        // When
        final Response getResponse = RestAssured.get(uriOfExistingResource);
        // Then
        final String uriToAllResources = HTTPLinkHeaderUtil.extractURIByRel(getResponse.getHeader("Link"), "collection");
        final Response getAllResponse = RestAssured.get(uriToAllResources);
        Assert.assertThat(getAllResponse.getStatusCode(), AbstractDiscoverabilityLiveTest.is(200));
    }
}

