package com.baeldung.functional;


import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.MULTIPART_FORM_DATA;
import org.junit.Test;
import org.springframework.boot.web.server.WebServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;


public class FunctionalWebApplicationIntegrationTest {
    private static WebTestClient client;

    private static WebServer server;

    @Test
    public void givenRouter_whenGetTest_thenGotHelloWorld() throws Exception {
        FunctionalWebApplicationIntegrationTest.client.get().uri("/test").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("helloworld");
    }

    @Test
    public void givenIndexFilter_whenRequestRoot_thenRewrittenToTest() throws Exception {
        FunctionalWebApplicationIntegrationTest.client.get().uri("/").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("helloworld");
    }

    @Test
    public void givenLoginForm_whenPostValidToken_thenSuccess() throws Exception {
        MultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap(1);
        formData.add("user", "baeldung");
        formData.add("token", "you_know_what_to_do");
        FunctionalWebApplicationIntegrationTest.client.post().uri("/login").contentType(APPLICATION_FORM_URLENCODED).body(BodyInserters.fromFormData(formData)).exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("welcome back!");
    }

    @Test
    public void givenLoginForm_whenRequestWithInvalidToken_thenFail() throws Exception {
        MultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap(2);
        formData.add("user", "baeldung");
        formData.add("token", "try_again");
        FunctionalWebApplicationIntegrationTest.client.post().uri("/login").contentType(APPLICATION_FORM_URLENCODED).body(BodyInserters.fromFormData(formData)).exchange().expectStatus().isBadRequest();
    }

    @Test
    public void givenUploadForm_whenRequestWithMultipartData_thenSuccess() throws Exception {
        Resource resource = new ClassPathResource("/baeldung-weekly.png");
        FunctionalWebApplicationIntegrationTest.client.post().uri("/upload").contentType(MULTIPART_FORM_DATA).body(fromResource(resource)).exchange().expectStatus().isOk().expectBody(String.class).isEqualTo(String.valueOf(resource.contentLength()));
    }

    @Test
    public void givenActors_whenAddActor_thenAdded() throws Exception {
        FunctionalWebApplicationIntegrationTest.client.get().uri("/actor").exchange().expectStatus().isOk().expectBodyList(Actor.class).hasSize(2);
        FunctionalWebApplicationIntegrationTest.client.post().uri("/actor").body(fromObject(new Actor("Clint", "Eastwood"))).exchange().expectStatus().isOk();
        FunctionalWebApplicationIntegrationTest.client.get().uri("/actor").exchange().expectStatus().isOk().expectBodyList(Actor.class).hasSize(3);
    }

    @Test
    public void givenResources_whenAccess_thenGot() throws Exception {
        FunctionalWebApplicationIntegrationTest.client.get().uri("/files/hello.txt").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("hello");
    }
}

