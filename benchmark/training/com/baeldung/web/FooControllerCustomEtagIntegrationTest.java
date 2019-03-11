package com.baeldung.web;


import MediaType.APPLICATION_JSON;
import com.baeldung.persistence.model.Foo;
import com.google.common.net.HttpHeaders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class FooControllerCustomEtagIntegrationTest {
    @Autowired
    private MockMvc mvc;

    private String FOOS_ENDPOINT = "/auth/foos/";

    private String CUSTOM_ETAG_ENDPOINT_SUFFIX = "/custom-etag";

    @Test
    public void givenResourceExists_whenRetrievingResourceUsingCustomEtagEndpoint_thenEtagIsAlsoReturned() throws Exception {
        // Given
        String createdResourceUri = this.mvc.perform(post(FOOS_ENDPOINT).contentType(APPLICATION_JSON).content(FooControllerCustomEtagIntegrationTest.createFooJson())).andExpect(status().isCreated()).andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
        // When
        ResultActions result = this.mvc.perform(get((createdResourceUri + (CUSTOM_ETAG_ENDPOINT_SUFFIX))).contentType(APPLICATION_JSON));
        // Then
        result.andExpect(status().isOk()).andExpect(header().string(HttpHeaders.ETAG, "\"0\""));
    }

    @Test
    public void givenResourceWasRetrieved_whenRetrievingAgainWithEtagUsingCustomEtagEndpoint_thenNotModifiedReturned() throws Exception {
        // Given
        String createdResourceUri = this.mvc.perform(post(FOOS_ENDPOINT).contentType(APPLICATION_JSON).content(FooControllerCustomEtagIntegrationTest.createFooJson())).andExpect(status().isCreated()).andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
        ResultActions findOneResponse = this.mvc.perform(get((createdResourceUri + (CUSTOM_ETAG_ENDPOINT_SUFFIX))).contentType(APPLICATION_JSON));
        String etag = findOneResponse.andReturn().getResponse().getHeader(HttpHeaders.ETAG);
        // When
        ResultActions result = this.mvc.perform(get((createdResourceUri + (CUSTOM_ETAG_ENDPOINT_SUFFIX))).contentType(APPLICATION_JSON).header(HttpHeaders.IF_NONE_MATCH, etag));
        // Then
        result.andExpect(status().isNotModified());
    }

    @Test
    public void givenResourceWasRetrievedThenModified_whenRetrievingAgainWithEtagUsingCustomEtagEndpoint_thenResourceIsReturned() throws Exception {
        // Given
        String createdResourceUri = this.mvc.perform(post(FOOS_ENDPOINT).contentType(APPLICATION_JSON).content(FooControllerCustomEtagIntegrationTest.createFooJson())).andExpect(status().isCreated()).andReturn().getResponse().getHeader(HttpHeaders.LOCATION);
        ResultActions findOneResponse = this.mvc.perform(get((createdResourceUri + (CUSTOM_ETAG_ENDPOINT_SUFFIX))).contentType(APPLICATION_JSON));
        String etag = findOneResponse.andReturn().getResponse().getHeader(HttpHeaders.ETAG);
        Foo createdFoo = FooControllerCustomEtagIntegrationTest.deserializeFoo(findOneResponse.andReturn().getResponse().getContentAsString());
        createdFoo.setName("updated name");
        this.mvc.perform(put(createdResourceUri).contentType(APPLICATION_JSON).content(FooControllerCustomEtagIntegrationTest.serializeFoo(createdFoo)));
        // When
        ResultActions result = this.mvc.perform(get((createdResourceUri + (CUSTOM_ETAG_ENDPOINT_SUFFIX))).contentType(APPLICATION_JSON).header(HttpHeaders.IF_NONE_MATCH, etag));
        // Then
        result.andExpect(status().isOk()).andExpect(header().string(HttpHeaders.ETAG, "\"1\""));
    }
}

