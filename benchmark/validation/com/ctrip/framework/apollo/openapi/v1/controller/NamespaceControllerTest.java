package com.ctrip.framework.apollo.openapi.v1.controller;


import ConfigFileFormat.Properties;
import InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE;
import InputValidator.INVALID_NAMESPACE_NAMESPACE_MESSAGE;
import com.ctrip.framework.apollo.openapi.auth.ConsumerPermissionValidator;
import com.ctrip.framework.apollo.openapi.dto.OpenAppNamespaceDTO;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;


/**
 * Created by kezhenxu at 2019/1/8 18:17.
 *
 * @author kezhenxu (kezhenxu94@163.com)
 */
@ActiveProfiles("skipAuthorization")
public class NamespaceControllerTest extends AbstractControllerTest {
    @Autowired
    private ConsumerPermissionValidator consumerPermissionValidator;

    @Test
    public void shouldFailWhenAppNamespaceNameIsInvalid() {
        Assert.assertTrue(consumerPermissionValidator.hasCreateNamespacePermission(null, null));
        OpenAppNamespaceDTO dto = new OpenAppNamespaceDTO();
        dto.setAppId("appId");
        dto.setName("invalid name");
        dto.setFormat(Properties.getValue());
        dto.setDataChangeCreatedBy("apollo");
        try {
            restTemplate.postForEntity(url("/openapi/v1/apps/{appId}/appnamespaces"), dto, OpenAppNamespaceDTO.class, dto.getAppId());
            Assert.fail("should throw");
        } catch (HttpClientErrorException e) {
            String result = e.getResponseBodyAsString();
            Assert.assertThat(result, Matchers.containsString(INVALID_CLUSTER_NAMESPACE_MESSAGE));
            Assert.assertThat(result, Matchers.containsString(INVALID_NAMESPACE_NAMESPACE_MESSAGE));
        }
    }
}

