package com.baeldung.reactive.urlmatch;


import org.junit.Test;
import org.springframework.boot.web.server.WebServer;
import org.springframework.test.web.reactive.server.WebTestClient;


public class ExploreSpring5URLPatternUsingRouterFunctionsIntegrationTest {
    private static WebTestClient client;

    private static WebServer server;

    @Test
    public void givenRouter_whenGetPathWithSingleCharWildcard_thenGotPathPattern() throws Exception {
        ExploreSpring5URLPatternUsingRouterFunctionsIntegrationTest.client.get().uri("/paths").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("/p?ths");
    }

    @Test
    public void givenRouter_whenMultipleURIVariablePattern_thenGotPathVariable() throws Exception {
        ExploreSpring5URLPatternUsingRouterFunctionsIntegrationTest.client.get().uri("/test/ab/cd").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("/ab/cd");
    }

    @Test
    public void givenRouter_whenGetMultipleCharWildcard_thenGotPathPattern() throws Exception {
        ExploreSpring5URLPatternUsingRouterFunctionsIntegrationTest.client.get().uri("/wildcard").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("/*card path was accessed");
    }

    @Test
    public void givenRouter_whenGetMultiplePathVaribleInSameSegment_thenGotPathVariables() throws Exception {
        ExploreSpring5URLPatternUsingRouterFunctionsIntegrationTest.client.get().uri("/baeldung_tutorial").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("baeldung , tutorial");
    }

    @Test
    public void givenRouter_whenGetRegexInPathVarible_thenGotPathVariable() throws Exception {
        ExploreSpring5URLPatternUsingRouterFunctionsIntegrationTest.client.get().uri("/abcd").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("/{baeldung:[a-z]+} was accessed and baeldung=abcd");
        ExploreSpring5URLPatternUsingRouterFunctionsIntegrationTest.client.get().uri("/1234").exchange().expectStatus().is4xxClientError();
    }

    @Test
    public void givenResources_whenAccess_thenGot() throws Exception {
        ExploreSpring5URLPatternUsingRouterFunctionsIntegrationTest.client.get().uri("/files/test/test.txt").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("test");
        ExploreSpring5URLPatternUsingRouterFunctionsIntegrationTest.client.get().uri("/files/hello.txt").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("hello");
    }
}

