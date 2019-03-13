package com.salesmanager.test.shop.integration.user;


import HttpMethod.GET;
import com.salesmanager.shop.application.ShopApplication;
import com.salesmanager.shop.model.user.ReadableUser;
import com.salesmanager.test.shop.common.ServicesTestSupport;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = ShopApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UserApiIntegrationTest extends ServicesTestSupport {
    private static String DEFAULT_USER = "admin";

    @Inject
    private TestRestTemplate testRestTemplate;

    @Test
    public void getUser() throws Exception {
        final HttpEntity<String> httpEntity = new HttpEntity(getHeader());
        final ResponseEntity<ReadableUser> response = testRestTemplate.exchange(String.format(("/api/v1/private/users/" + (UserApiIntegrationTest.DEFAULT_USER))), GET, httpEntity, ReadableUser.class);
        if ((response.getStatusCode()) != (HttpStatus.OK)) {
            throw new Exception(response.toString());
        } else {
            final ReadableUser user = response.getBody();
            Assert.assertNotNull(user);
        }
    }
}

