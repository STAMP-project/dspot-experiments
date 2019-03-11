package com.ctrip.framework.apollo.portal.controller;


import com.ctrip.framework.apollo.portal.AbstractIntegrationTest;
import java.util.List;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;


/**
 * Created by kezhenxu at 2019/1/14 12:49.
 *
 * @author kezhenxu (kezhenxu at lizhi dot fm)
 */
public class CommitControllerTest extends AbstractIntegrationTest {
    @Test
    public void shouldFailWhenPageOrSiseIsNegative() {
        try {
            restTemplate.getForEntity(url("/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/commits?page=-1"), List.class, "1", "env", "cl", "ns");
            Assert.fail("should throw");
        } catch (final HttpClientErrorException e) {
            Assert.assertThat(new String(e.getResponseBodyAsByteArray()), StringContains.containsString("page should be positive or 0"));
        }
        try {
            restTemplate.getForEntity(url("/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/commits?size=0"), List.class, "1", "env", "cl", "ns");
            Assert.fail("should throw");
        } catch (final HttpClientErrorException e) {
            Assert.assertThat(new String(e.getResponseBodyAsByteArray()), StringContains.containsString("size should be positive number"));
        }
    }
}

