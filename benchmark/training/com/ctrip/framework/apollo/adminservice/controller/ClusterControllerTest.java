package com.ctrip.framework.apollo.adminservice.controller;


import ConfigConsts.CLUSTER_NAME_DEFAULT;
import InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE;
import com.ctrip.framework.apollo.biz.entity.Cluster;
import com.ctrip.framework.apollo.biz.service.ClusterService;
import com.ctrip.framework.apollo.common.dto.ClusterDTO;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;


public class ClusterControllerTest extends AbstractControllerTest {
    private ClusterController clusterController;

    @Mock
    private ClusterService clusterService;

    @Test(expected = BadRequestException.class)
    public void testDeleteDefaultFail() {
        Cluster cluster = new Cluster();
        cluster.setName(CLUSTER_NAME_DEFAULT);
        Mockito.when(clusterService.findOne(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class))).thenReturn(cluster);
        clusterController.delete("1", "2", "d");
    }

    @Test
    public void testDeleteSuccess() {
        Cluster cluster = new Cluster();
        Mockito.when(clusterService.findOne(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class))).thenReturn(cluster);
        clusterController.delete("1", "2", "d");
        Mockito.verify(clusterService, Mockito.times(1)).findOne("1", "2");
    }

    @Test
    public void shouldFailWhenRequestBodyInvalid() {
        ClusterDTO cluster = new ClusterDTO();
        cluster.setAppId("valid");
        cluster.setName("notBlank");
        ResponseEntity<ClusterDTO> response = restTemplate.postForEntity(((baseUrl()) + "/apps/{appId}/clusters"), cluster, ClusterDTO.class, cluster.getAppId());
        ClusterDTO createdCluster = response.getBody();
        Assert.assertNotNull(createdCluster);
        Assert.assertEquals(cluster.getAppId(), createdCluster.getAppId());
        Assert.assertEquals(cluster.getName(), createdCluster.getName());
        cluster.setName("invalid app name");
        try {
            restTemplate.postForEntity(((baseUrl()) + "/apps/{appId}/clusters"), cluster, ClusterDTO.class, cluster.getAppId());
            Assert.fail("Should throw");
        } catch (HttpClientErrorException e) {
            Assert.assertThat(new String(e.getResponseBodyAsByteArray()), Matchers.containsString(INVALID_CLUSTER_NAMESPACE_MESSAGE));
        }
    }
}

