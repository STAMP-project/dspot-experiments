package org.baeldung.web;


import HttpStatus.OK;
import SpringBootTest.WebEnvironment;
import java.util.Collections;
import org.baeldung.ssl.HttpsEnabledApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = HttpsEnabledApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("ssl")
public class HttpsApplicationIntegrationTest {
    private static final String WELCOME_URL = "https://localhost:8443/welcome";

    @Value("${trust.store}")
    private Resource trustStore;

    @Value("${trust.store.password}")
    private String trustStorePassword;

    @Test
    public void whenGETanHTTPSResource_thenCorrectResponse() throws Exception {
        ResponseEntity<String> response = restTemplate().getForEntity(HttpsApplicationIntegrationTest.WELCOME_URL, String.class, Collections.emptyMap());
        Assert.assertEquals("<h1>Welcome to Secured Site</h1>", response.getBody());
        Assert.assertEquals(OK, response.getStatusCode());
    }
}

