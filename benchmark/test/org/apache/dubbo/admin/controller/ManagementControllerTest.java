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


import Constants.GLOBAL_CONFIG;
import HttpMethod.GET;
import HttpMethod.PUT;
import HttpStatus.CREATED;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.dubbo.admin.AbstractSpringIntegrationTest;
import org.apache.dubbo.admin.model.dto.ConfigDTO;
import org.apache.dubbo.admin.service.ProviderService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;


public class ManagementControllerTest extends AbstractSpringIntegrationTest {
    private final String env = "whatever";

    @MockBean
    private ProviderService providerService;

    @Test
    public void shouldCreateGlobalConfig() throws Exception {
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setKey(GLOBAL_CONFIG);
        configDTO.setConfig("key1=val1\nkey2=val2");
        ResponseEntity<Boolean> responseEntity = restTemplate.postForEntity(url("/api/{env}/manage/config"), configDTO, Boolean.class, env);
        Assert.assertEquals(responseEntity.getStatusCode(), CREATED);
        Assert.assertEquals(responseEntity.getBody(), true);
        byte[] bytes = AbstractSpringIntegrationTest.zkClient.getData().forPath(getPath("dubbo"));
        String config = new String(bytes);
        Assert.assertEquals(configDTO.getConfig(), config);
        AbstractSpringIntegrationTest.zkClient.delete().forPath(getPath("dubbo"));
    }

    @Test
    public void shouldCreateApplicationConfig() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String application = "dubbo-admin" + uuid;
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setKey(application);
        configDTO.setConfig("key1=val1\nkey2=val2");
        ResponseEntity<Boolean> responseEntity = restTemplate.postForEntity(url("/api/{env}/manage/config"), configDTO, Boolean.class, env);
        Assert.assertEquals(responseEntity.getStatusCode(), CREATED);
        Assert.assertEquals(responseEntity.getBody(), true);
        byte[] bytes = AbstractSpringIntegrationTest.zkClient.getData().forPath(getPath(application));
        String config = new String(bytes);
        Assert.assertEquals(configDTO.getConfig(), config);
    }

    @Test
    public void shouldThrowWhenUpdateNonExistedConfigKey() {
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setKey(GLOBAL_CONFIG);
        configDTO.setConfig("key1=val1\nkey2=val2");
        ResponseEntity<Void> responseEntity = restTemplate.exchange(url("/api/{env}/manage/config/{key}"), PUT, new org.springframework.http.HttpEntity(configDTO), Void.class, env, "non-existed");
        Assert.assertEquals(responseEntity.getStatusCode(), NOT_FOUND);
    }

    @Test
    public void shouldUpdateConfigSpecifiedKey() throws Exception {
        String key = "shouldUpdateConfigSpecifiedKey";
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setKey(key);
        configDTO.setConfig("key1=val1\nkey2=val2");
        restTemplate.postForEntity(url("/api/{env}/manage/config"), configDTO, Boolean.class, env);
        configDTO.setConfig("key1=updatedVal1\nkey2=updatedVal2");
        ResponseEntity<Void> responseEntity = restTemplate.exchange(url("/api/{env}/manage/config/{key}"), PUT, new org.springframework.http.HttpEntity(configDTO), Void.class, env, key);
        Assert.assertEquals(responseEntity.getStatusCode(), OK);
        byte[] bytes = AbstractSpringIntegrationTest.zkClient.getData().forPath(getPath(key));
        String config = new String(bytes);
        Assert.assertEquals("key1=updatedVal1\nkey2=updatedVal2", config);
    }

    @Test
    public void shouldGetAllConfig() throws Exception {
        int num = 20;
        List<ConfigDTO> configDTOs = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            ConfigDTO configDTO = new ConfigDTO();
            configDTO.setKey(("key" + i));
            configDTO.setConfig("key1=val1\nkey2=val2");
            configDTOs.add(configDTO);
            String path = getPath(configDTO.getKey());
            if ((AbstractSpringIntegrationTest.zkClient.checkExists().forPath(path)) == null) {
                AbstractSpringIntegrationTest.zkClient.create().creatingParentsIfNeeded().forPath(path);
            }
            AbstractSpringIntegrationTest.zkClient.setData().forPath(path, configDTO.getConfig().getBytes());
        }
        Mockito.when(providerService.findApplications()).thenReturn(configDTOs.stream().map(ConfigDTO::getKey).collect(Collectors.toSet()));
        ResponseEntity<List<ConfigDTO>> responseEntity = restTemplate.exchange(url("/api/{env}/manage/config/{key}"), GET, null, new org.springframework.core.ParameterizedTypeReference<List<ConfigDTO>>() {}, env, "*");
        Assert.assertEquals(responseEntity.getStatusCode(), OK);
        Assert.assertThat(responseEntity.getBody(), Matchers.hasSize(num));
    }

    @Test
    public void shouldDeleteConfig() throws Exception {
        int num = 20;
        List<ConfigDTO> configDTOs = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            ConfigDTO configDTO = new ConfigDTO();
            configDTO.setKey(("shouldDeleteConfigKey" + i));
            configDTO.setConfig("key1=val1\nkey2=val2");
            configDTOs.add(configDTO);
            String path = getPath(configDTO.getKey());
            if ((AbstractSpringIntegrationTest.zkClient.checkExists().forPath(path)) == null) {
                AbstractSpringIntegrationTest.zkClient.create().creatingParentsIfNeeded().forPath(path);
            }
            AbstractSpringIntegrationTest.zkClient.setData().forPath(path, configDTO.getConfig().getBytes());
        }
        Mockito.when(providerService.findApplications()).thenReturn(configDTOs.stream().map(ConfigDTO::getKey).collect(Collectors.toSet()));
        restTemplate.delete(url("/api/{env}/manage/config/{key}"), env, "shouldDeleteConfigKey1");
        ResponseEntity<List<ConfigDTO>> responseEntity = restTemplate.exchange(url("/api/{env}/manage/config/{key}"), GET, null, new org.springframework.core.ParameterizedTypeReference<List<ConfigDTO>>() {}, env, "*");
        Assert.assertEquals(responseEntity.getStatusCode(), OK);
        Assert.assertThat(responseEntity.getBody(), Matchers.hasSize((num - 1)));
        restTemplate.delete(url("/api/{env}/manage/config/{key}"), env, "shouldDeleteConfigKey10");
        responseEntity = restTemplate.exchange(url("/api/{env}/manage/config/{key}"), GET, null, new org.springframework.core.ParameterizedTypeReference<List<ConfigDTO>>() {}, env, "*");
        Assert.assertEquals(responseEntity.getStatusCode(), OK);
        Assert.assertThat(responseEntity.getBody(), Matchers.hasSize((num - 2)));
    }
}

