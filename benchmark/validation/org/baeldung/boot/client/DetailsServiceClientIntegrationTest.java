package org.baeldung.boot.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.baeldung.boot.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;


@RunWith(SpringRunner.class)
@RestClientTest({ DetailsServiceClient.class, Application.class })
public class DetailsServiceClientIntegrationTest {
    @Autowired
    private DetailsServiceClient client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void whenCallingGetUserDetails_thenClientExecutesCorrectCall() throws Exception {
        Details details = this.client.getUserDetails("john");
        assertThat(details.getLogin()).isEqualTo("john");
        assertThat(details.getName()).isEqualTo("John Smith");
    }
}

