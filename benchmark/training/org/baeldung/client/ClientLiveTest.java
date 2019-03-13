package org.baeldung.client;


import HttpMethod.GET;
import org.baeldung.client.spring.ClientConfig;
import org.baeldung.web.dto.Foo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ClientConfig.class }, loader = AnnotationConfigContextLoader.class)
public class ClientLiveTest {
    @Autowired
    private RestTemplate secureRestTemplate;

    // tests
    @Test
    public final void whenContextIsBootstrapped_thenNoExceptions() {
        // 
    }

    @Test
    public final void whenSecuredRestApiIsConsumed_then200OK() {
        final ResponseEntity<Foo> responseEntity = secureRestTemplate.exchange("http://localhost:8082/spring-security-rest-basic-auth/api/foos/1", GET, null, Foo.class);
        Assert.assertThat(responseEntity.getStatusCode().value(), Matchers.is(200));
    }

    @Test(expected = ResourceAccessException.class)
    public final void whenHttpsUrlIsConsumed_thenException() {
        final String urlOverHttps = "https://localhost:8443/spring-security-rest-basic-auth/api/bars/1";
        final ResponseEntity<String> response = new RestTemplate().exchange(urlOverHttps, GET, null, String.class);
        Assert.assertThat(response.getStatusCode().value(), Matchers.equalTo(200));
    }
}

