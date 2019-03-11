package org.baeldung.client;


import HttpMethod.GET;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpStatus.CREATED;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import org.baeldung.web.dto.Foo;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


public class RestTemplateBasicLiveTest {
    private RestTemplate restTemplate;

    private static final String fooResourceUrl = ("http://localhost:" + (Consts.APPLICATION_PORT)) + "/spring-rest/foos";

    // GET
    @Test
    public void givenResourceUrl_whenSendGetForRequestEntity_thenStatusOk() throws IOException {
        final ResponseEntity<Foo> response = restTemplate.getForEntity(((RestTemplateBasicLiveTest.fooResourceUrl) + "/1"), Foo.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(OK));
    }

    @Test
    public void givenResourceUrl_whenSendGetForRequestEntity_thenBodyCorrect() throws IOException {
        final RestTemplate template = new RestTemplate();
        final ResponseEntity<String> response = template.getForEntity(((RestTemplateBasicLiveTest.fooResourceUrl) + "/1"), String.class);
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(response.getBody());
        final JsonNode name = root.path("name");
        MatcherAssert.assertThat(name.asText(), CoreMatchers.notNullValue());
    }

    @Test
    public void givenResourceUrl_whenRetrievingResource_thenCorrect() throws IOException {
        final Foo foo = restTemplate.getForObject(((RestTemplateBasicLiveTest.fooResourceUrl) + "/1"), Foo.class);
        MatcherAssert.assertThat(foo.getName(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(foo.getId(), CoreMatchers.is(1L));
    }

    // HEAD, OPTIONS
    @Test
    public void givenFooService_whenCallHeadForHeaders_thenReceiveAllHeadersForThatResource() {
        final HttpHeaders httpHeaders = restTemplate.headForHeaders(RestTemplateBasicLiveTest.fooResourceUrl);
        Assert.assertTrue(httpHeaders.getContentType().includes(APPLICATION_JSON));
    }

    // POST
    @Test
    public void givenFooService_whenPostForObject_thenCreatedObjectIsReturned() {
        final HttpEntity<Foo> request = new HttpEntity(new Foo("bar"));
        final Foo foo = restTemplate.postForObject(RestTemplateBasicLiveTest.fooResourceUrl, request, Foo.class);
        MatcherAssert.assertThat(foo, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(foo.getName(), CoreMatchers.is("bar"));
    }

    @Test
    public void givenFooService_whenPostForLocation_thenCreatedLocationIsReturned() {
        final HttpEntity<Foo> request = new HttpEntity(new Foo("bar"));
        final URI location = restTemplate.postForLocation(RestTemplateBasicLiveTest.fooResourceUrl, request);
        MatcherAssert.assertThat(location, CoreMatchers.notNullValue());
    }

    @Test
    public void givenFooService_whenPostResource_thenResourceIsCreated() {
        final Foo foo = new Foo("bar");
        final ResponseEntity<Foo> response = restTemplate.postForEntity(RestTemplateBasicLiveTest.fooResourceUrl, foo, Foo.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.is(CREATED));
        final Foo fooResponse = response.getBody();
        MatcherAssert.assertThat(fooResponse, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(fooResponse.getName(), CoreMatchers.is("bar"));
    }

    @Test
    public void givenFooService_whenCallOptionsForAllow_thenReceiveValueOfAllowHeader() {
        final Set<HttpMethod> optionsForAllow = restTemplate.optionsForAllow(RestTemplateBasicLiveTest.fooResourceUrl);
        final HttpMethod[] supportedMethods = new HttpMethod[]{ HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD };
        Assert.assertTrue(optionsForAllow.containsAll(Arrays.asList(supportedMethods)));
    }

    // PUT
    @Test
    public void givenFooService_whenPutExistingEntity_thenItIsUpdated() {
        final HttpHeaders headers = prepareBasicAuthHeaders();
        final HttpEntity<Foo> request = new HttpEntity(new Foo("bar"), headers);
        // Create Resource
        final ResponseEntity<Foo> createResponse = restTemplate.exchange(RestTemplateBasicLiveTest.fooResourceUrl, POST, request, Foo.class);
        // Update Resource
        final Foo updatedInstance = new Foo("newName");
        updatedInstance.setId(createResponse.getBody().getId());
        final String resourceUrl = ((RestTemplateBasicLiveTest.fooResourceUrl) + '/') + (createResponse.getBody().getId());
        final HttpEntity<Foo> requestUpdate = new HttpEntity(updatedInstance, headers);
        restTemplate.exchange(resourceUrl, PUT, requestUpdate, Void.class);
        // Check that Resource was updated
        final ResponseEntity<Foo> updateResponse = restTemplate.exchange(resourceUrl, GET, new HttpEntity(headers), Foo.class);
        final Foo foo = updateResponse.getBody();
        MatcherAssert.assertThat(foo.getName(), CoreMatchers.is(updatedInstance.getName()));
    }

    @Test
    public void givenFooService_whenPutExistingEntityWithCallback_thenItIsUpdated() {
        final HttpHeaders headers = prepareBasicAuthHeaders();
        final HttpEntity<Foo> request = new HttpEntity(new Foo("bar"), headers);
        // Create entity
        ResponseEntity<Foo> response = restTemplate.exchange(RestTemplateBasicLiveTest.fooResourceUrl, POST, request, Foo.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.is(CREATED));
        // Update entity
        final Foo updatedInstance = new Foo("newName");
        updatedInstance.setId(response.getBody().getId());
        final String resourceUrl = ((RestTemplateBasicLiveTest.fooResourceUrl) + '/') + (response.getBody().getId());
        restTemplate.execute(resourceUrl, PUT, requestCallback(updatedInstance), ( clientHttpResponse) -> null);
        // Check that entity was updated
        response = restTemplate.exchange(resourceUrl, GET, new HttpEntity(headers), Foo.class);
        final Foo foo = response.getBody();
        MatcherAssert.assertThat(foo.getName(), CoreMatchers.is(updatedInstance.getName()));
    }

    // PATCH
    @Test
    public void givenFooService_whenPatchExistingEntity_thenItIsUpdated() {
        final HttpHeaders headers = prepareBasicAuthHeaders();
        final HttpEntity<Foo> request = new HttpEntity(new Foo("bar"), headers);
        // Create Resource
        final ResponseEntity<Foo> createResponse = restTemplate.exchange(RestTemplateBasicLiveTest.fooResourceUrl, POST, request, Foo.class);
        // Update Resource
        final Foo updatedResource = new Foo("newName");
        updatedResource.setId(createResponse.getBody().getId());
        final String resourceUrl = ((RestTemplateBasicLiveTest.fooResourceUrl) + '/') + (createResponse.getBody().getId());
        final HttpEntity<Foo> requestUpdate = new HttpEntity(updatedResource, headers);
        final ClientHttpRequestFactory requestFactory = getSimpleClientHttpRequestFactory();
        final RestTemplate template = new RestTemplate(requestFactory);
        template.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
        template.patchForObject(resourceUrl, requestUpdate, Void.class);
        // Check that Resource was updated
        final ResponseEntity<Foo> updateResponse = restTemplate.exchange(resourceUrl, GET, new HttpEntity(headers), Foo.class);
        final Foo foo = updateResponse.getBody();
        MatcherAssert.assertThat(foo.getName(), CoreMatchers.is(updatedResource.getName()));
    }

    // DELETE
    @Test
    public void givenFooService_whenCallDelete_thenEntityIsRemoved() {
        final Foo foo = new Foo("remove me");
        final ResponseEntity<Foo> response = restTemplate.postForEntity(RestTemplateBasicLiveTest.fooResourceUrl, foo, Foo.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.is(CREATED));
        final String entityUrl = ((RestTemplateBasicLiveTest.fooResourceUrl) + "/") + (response.getBody().getId());
        restTemplate.delete(entityUrl);
        try {
            restTemplate.getForEntity(entityUrl, Foo.class);
            Assert.fail();
        } catch (final HttpClientErrorException ex) {
            MatcherAssert.assertThat(ex.getStatusCode(), CoreMatchers.is(NOT_FOUND));
        }
    }

    @Test
    public void givenFooService_whenFormSubmit_thenResourceIsCreated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap();
        map.add("id", "1");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(((RestTemplateBasicLiveTest.fooResourceUrl) + "/form"), request, String.class);
        MatcherAssert.assertThat(response.getStatusCode(), CoreMatchers.is(CREATED));
        final String fooResponse = response.getBody();
        MatcherAssert.assertThat(fooResponse, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(fooResponse, CoreMatchers.is("1"));
    }
}

