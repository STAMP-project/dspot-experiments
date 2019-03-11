package org.mockserver.server.initialize;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.mock.Expectation;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class ExpectationInitializerLoaderTest {
    @Test
    public void shouldLoadExpectationsFromJson() {
        // given
        String initializationJsonPath = ConfigurationProperties.initializationJsonPath();
        try {
            ConfigurationProperties.initializationJsonPath("org/mockserver/server/initialize/initializerJson.json");
            // when
            final Expectation[] expectations = ExpectationInitializerLoader.loadExpectations();
            // then
            MatcherAssert.assertThat(expectations, CoreMatchers.is(new Expectation[]{ new Expectation(HttpRequest.request().withPath("/simpleFirst")).thenRespond(HttpResponse.response().withBody("some first response")), new Expectation(HttpRequest.request().withPath("/simpleSecond")).thenRespond(HttpResponse.response().withBody("some second response")) }));
        } finally {
            ConfigurationProperties.initializationJsonPath(initializationJsonPath);
        }
    }

    @Test
    public void shouldLoadExpectationsFromInitializerClass() {
        // given
        String initializationClass = ConfigurationProperties.initializationClass();
        try {
            ConfigurationProperties.initializationClass(ExpectationInitializerExample.class.getName());
            // when
            final Expectation[] expectations = ExpectationInitializerLoader.loadExpectations();
            // then
            MatcherAssert.assertThat(expectations, CoreMatchers.is(new Expectation[]{ new Expectation(HttpRequest.request("/simpleFirst")).thenRespond(HttpResponse.response("some first response")), new Expectation(HttpRequest.request("/simpleSecond")).thenRespond(HttpResponse.response("some second response")) }));
        } finally {
            ConfigurationProperties.initializationClass(initializationClass);
        }
    }
}

