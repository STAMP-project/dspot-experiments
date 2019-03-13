package org.baeldung.rest;


import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GithubBasicLiveTest {
    // simple request - response
    @Test
    public void givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived() throws IOException, ClientProtocolException {
        // Given
        final String name = RandomStringUtils.randomAlphabetic(8);
        final HttpUriRequest request = new HttpGet(("https://api.github.com/users/" + name));
        // When
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        // Then
        Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), Matchers.equalTo(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson() throws IOException, ClientProtocolException {
        // Given
        final String jsonMimeType = "application/json";
        final HttpUriRequest request = new HttpGet("https://api.github.com/users/eugenp");
        // When
        final HttpResponse response = HttpClientBuilder.create().build().execute(request);
        // Then
        final String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        Assert.assertEquals(jsonMimeType, mimeType);
    }

    @Test
    public void givenUserExists_whenUserInformationIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException, ClientProtocolException {
        // Given
        final HttpUriRequest request = new HttpGet("https://api.github.com/users/eugenp");
        // When
        final HttpResponse response = HttpClientBuilder.create().build().execute(request);
        // Then
        final GitHubUser resource = RetrieveUtil.retrieveResourceFromResponse(response, GitHubUser.class);
        Assert.assertThat("eugenp", Matchers.is(resource.getLogin()));
    }
}

