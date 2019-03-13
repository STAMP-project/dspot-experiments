package com.baeldung.resttemplate;


import HttpStatus.OK;
import MediaType.APPLICATION_JSON;
import com.baeldung.sampleapp.config.RestClientConfig;
import com.baeldung.transfer.LoginForm;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RestClientConfig.class)
public class RestTemplateLiveTest {
    @Autowired
    RestTemplate restTemplate;

    @Test
    public void givenRestTemplate_whenRequested_thenLogAndModifyResponse() {
        LoginForm loginForm = new LoginForm("userName", "password");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<LoginForm> requestEntity = new HttpEntity<LoginForm>(loginForm, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://httpbin.org/post", requestEntity, String.class);
        Assert.assertThat(responseEntity.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(OK)));
        Assert.assertThat(responseEntity.getHeaders().get("Foo").get(0), CoreMatchers.is(CoreMatchers.equalTo("bar")));
    }
}

