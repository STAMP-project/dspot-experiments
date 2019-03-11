/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.admin.controller;


import HttpMethod.DELETE;
import HttpMethod.PUT;
import HttpStatus.CREATED;
import HttpStatus.OK;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.apache.dubbo.admin.AbstractSpringIntegrationTest;
import org.apache.dubbo.admin.model.dto.BalancingDTO;
import org.apache.dubbo.admin.service.OverrideService;
import org.apache.dubbo.admin.service.ProviderService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;


public class LoadBalanceControllerTest extends AbstractSpringIntegrationTest {
    private final String env = "whatever";

    @MockBean
    private OverrideService overrideService;

    @MockBean
    private ProviderService providerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createLoadbalance() throws IOException {
        BalancingDTO balancingDTO = new BalancingDTO();
        ResponseEntity<String> response;
        // service and application are all blank
        response = restTemplate.postForEntity(url("/api/{env}/rules/balancing"), balancingDTO, String.class, env);
        Assert.assertFalse("should return a fail response, when service and application are all blank", ((Boolean) (objectMapper.readValue(response.getBody(), Map.class).get("success"))));
        // dubbo version is 2.6
        balancingDTO.setApplication("test application");
        balancingDTO.setService("test service");
        Mockito.when(providerService.findVersionInApplication("test application")).thenReturn("2.6");
        response = restTemplate.postForEntity(url("/api/{env}/rules/balancing"), balancingDTO, String.class, env);
        Assert.assertFalse("should return a fail response, when dubbo version is 2.6", ((Boolean) (objectMapper.readValue(response.getBody(), Map.class).get("success"))));
        // dubbo version is 2.7
        Mockito.when(providerService.findVersionInApplication("test application")).thenReturn("2.7");
        response = restTemplate.postForEntity(url("/api/{env}/rules/balancing"), balancingDTO, String.class, env);
        Assert.assertEquals(CREATED, response.getStatusCode());
        Assert.assertTrue(Boolean.valueOf(response.getBody()));
    }

    @Test
    public void updateLoadbalance() throws IOException {
        String id = "1";
        BalancingDTO balancingDTO = new BalancingDTO();
        URI uri;
        ResponseEntity<String> response;
        // unknown id
        response = restTemplate.exchange(url("/api/{env}/rules/balancing/{id}"), PUT, new org.springframework.http.HttpEntity(balancingDTO, null), String.class, env, id);
        Assert.assertFalse("should return a fail response, when id is null", ((Boolean) (objectMapper.readValue(response.getBody(), Map.class).get("success"))));
        // valid id
        BalancingDTO balancing = Mockito.mock(BalancingDTO.class);
        Mockito.when(overrideService.findBalance(id)).thenReturn(balancing);
        Assert.assertTrue(restTemplate.exchange(url("/api/{env}/rules/balancing/{id}"), PUT, new org.springframework.http.HttpEntity(balancingDTO, null), Boolean.class, env, id).getBody());
        Mockito.verify(overrideService).saveBalance(ArgumentMatchers.any(BalancingDTO.class));
    }

    @Test
    public void searchLoadbalances() throws IOException {
        String service = "test service";
        String application = "test application";
        ResponseEntity<String> response;
        // service and application are all blank
        response = restTemplate.getForEntity(url("/api/{env}/rules/balancing"), String.class, env);
        Assert.assertFalse("should return a fail response, when service and application are all blank", ((Boolean) (objectMapper.readValue(response.getBody(), Map.class).get("success"))));
        // service is valid
        response = restTemplate.getForEntity(url("/api/{env}/rules/balancing?service={service}&application={application}"), String.class, env, service, null);
        Assert.assertEquals(OK, response.getStatusCode());
        Mockito.verify(overrideService).findBalance(service);
        // application is valid
        response = restTemplate.getForEntity(url("/api/{env}/rules/balancing?service={service}&application={application}"), String.class, env, null, application);
        Assert.assertEquals(OK, response.getStatusCode());
        Mockito.verify(overrideService).findBalance(application);
        // findBalance return a notnull
        BalancingDTO balancingDTO = new BalancingDTO();
        Mockito.when(overrideService.findBalance(ArgumentMatchers.anyString())).thenReturn(balancingDTO);
        response = restTemplate.getForEntity(url("/api/{env}/rules/balancing?service={service}&application={application}"), String.class, env, null, application);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(1, objectMapper.readValue(response.getBody(), List.class).size());
    }

    @Test
    public void detailLoadBalance() throws IOException {
        String id = "1";
        ResponseEntity<String> response;
        // when balancing is not exist
        response = restTemplate.getForEntity(url("/api/{env}/rules/balancing/{id}"), String.class, env, id);
        Assert.assertFalse("should return a fail response, when id is null", ((Boolean) (objectMapper.readValue(response.getBody(), Map.class).get("success"))));
        // when balancing is not null
        BalancingDTO balancingDTO = new BalancingDTO();
        Mockito.when(overrideService.findBalance(id)).thenReturn(balancingDTO);
        response = restTemplate.getForEntity(url("/api/{env}/rules/balancing/{id}"), String.class, env, id);
        Assert.assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void deleteLoadBalance() {
        String id = "1";
        URI uri;
        ResponseEntity<String> response;
        response = restTemplate.exchange(url("/api/{env}/rules/balancing/{id}"), DELETE, new org.springframework.http.HttpEntity(null), String.class, env, id);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(Boolean.valueOf(response.getBody()));
    }
}

