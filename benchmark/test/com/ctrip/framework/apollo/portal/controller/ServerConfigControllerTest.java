package com.ctrip.framework.apollo.portal.controller;


import com.ctrip.framework.apollo.portal.AbstractIntegrationTest;
import com.ctrip.framework.apollo.portal.entity.po.ServerConfig;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;


/**
 * Created by kezhenxu at 2019/1/14 13:24.
 *
 * @author kezhenxu (kezhenxu at lizhi dot fm)
 */
@ActiveProfiles("skipAuthorization")
public class ServerConfigControllerTest extends AbstractIntegrationTest {
    @Test
    public void shouldSuccessWhenParameterValid() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setKey("validKey");
        serverConfig.setValue("validValue");
        ResponseEntity<ServerConfig> responseEntity = restTemplate.postForEntity(url("/server/config"), serverConfig, ServerConfig.class);
        Assert.assertEquals(responseEntity.getBody().getKey(), serverConfig.getKey());
        Assert.assertEquals(responseEntity.getBody().getValue(), serverConfig.getValue());
    }

    @Test
    public void shouldFailWhenParameterInvalid() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setKey("  ");
        serverConfig.setValue("valid");
        try {
            restTemplate.postForEntity(url("/server/config"), serverConfig, ServerConfig.class);
            Assert.fail("Should throw");
        } catch (final HttpClientErrorException e) {
            Assert.assertThat(new String(e.getResponseBodyAsByteArray()), StringContains.containsString("ServerConfig.Key cannot be blank"));
        }
        serverConfig.setKey("valid");
        serverConfig.setValue("   ");
        try {
            restTemplate.postForEntity(url("/server/config"), serverConfig, ServerConfig.class);
            Assert.fail("Should throw");
        } catch (final HttpClientErrorException e) {
            Assert.assertThat(new String(e.getResponseBodyAsByteArray()), StringContains.containsString("ServerConfig.Value cannot be blank"));
        }
    }
}

