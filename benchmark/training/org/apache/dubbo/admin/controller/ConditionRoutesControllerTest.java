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
import HttpMethod.GET;
import HttpMethod.PUT;
import HttpStatus.BAD_REQUEST;
import HttpStatus.CREATED;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.dubbo.admin.AbstractSpringIntegrationTest;
import org.apache.dubbo.admin.common.util.YamlParser;
import org.apache.dubbo.admin.model.dto.ConditionRouteDTO;
import org.apache.dubbo.admin.model.store.RoutingRule;
import org.apache.dubbo.admin.service.ProviderService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;


public class ConditionRoutesControllerTest extends AbstractSpringIntegrationTest {
    private final String env = "whatever";

    @MockBean
    private ProviderService providerService;

    @Test
    public void shouldThrowWhenParamInvalid() {
        String uuid = UUID.randomUUID().toString();
        ConditionRouteDTO dto = new ConditionRouteDTO();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url("/api/{env}/rules/route/condition"), dto, String.class, env);
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(BAD_REQUEST));
        Assert.assertThat(responseEntity.getBody(), Matchers.containsString("serviceName and app is Empty!"));
        dto.setApplication(("application" + uuid));
        Mockito.when(providerService.findVersionInApplication(dto.getApplication())).thenReturn("2.6");
        responseEntity = restTemplate.postForEntity(url("/api/{env}/rules/route/condition"), dto, String.class, env);
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(INTERNAL_SERVER_ERROR));
        Assert.assertThat(responseEntity.getBody(), Matchers.containsString("dubbo 2.6 does not support application scope routing rule"));
    }

    @Test
    public void shouldCreateRule() {
        String uuid = UUID.randomUUID().toString();
        String application = "application" + uuid;
        String service = "service" + uuid;
        List<String> conditions = Collections.singletonList("=> host != 172.22.3.91");
        ConditionRouteDTO dto = new ConditionRouteDTO();
        dto.setService(service);
        dto.setConditions(conditions);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url("/api/{env}/rules/route/condition"), dto, String.class, env);
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(CREATED));
        dto.setApplication(application);
        Mockito.when(providerService.findVersionInApplication(dto.getApplication())).thenReturn("2.7");
        responseEntity = restTemplate.postForEntity(url("/api/{env}/rules/route/condition"), dto, String.class, env);
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(CREATED));
    }

    @Test
    public void shouldUpdateRule() throws Exception {
        String service = "org.apache.dubbo.demo.DemoService";
        String content = ((((("conditions:\n" + (((("- => host != 172.22.3.111\n" + "- => host != 172.22.3.112\n") + "enabled: true\n") + "force: true\n") + "key: ")) + service) + "\n") + "priority: 0\n") + "runtime: false\n") + "scope: service";
        String path = ("/dubbo/config/" + service) + "/condition-router";
        AbstractSpringIntegrationTest.zkClient.create().creatingParentContainersIfNeeded().forPath(path);
        AbstractSpringIntegrationTest.zkClient.setData().forPath(path, content.getBytes());
        List<String> newConditions = Arrays.asList("=> host != 172.22.3.211", "=> host != 172.22.3.212");
        ConditionRouteDTO dto = new ConditionRouteDTO();
        dto.setConditions(newConditions);
        dto.setService(service);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url("/api/{env}/rules/route/condition/{service}"), PUT, new org.springframework.http.HttpEntity(dto, null), String.class, env, service);
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(OK));
        byte[] bytes = AbstractSpringIntegrationTest.zkClient.getData().forPath(path);
        String updatedConfig = new String(bytes);
        RoutingRule rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
        Assert.assertThat(rule.getConditions(), Matchers.containsInAnyOrder(newConditions.toArray()));
    }

    @Test
    public void shouldGetServiceRule() throws Exception {
        String service = "org.apache.dubbo.demo.DemoService";
        String content = ((((("conditions:\n" + (((("- => host != 172.22.3.111\n" + "- => host != 172.22.3.112\n") + "enabled: true\n") + "force: true\n") + "key: ")) + service) + "\n") + "priority: 0\n") + "runtime: false\n") + "scope: service";
        String path = ("/dubbo/config/" + service) + "/condition-router";
        AbstractSpringIntegrationTest.zkClient.create().creatingParentContainersIfNeeded().forPath(path);
        AbstractSpringIntegrationTest.zkClient.setData().forPath(path, content.getBytes());
        ResponseEntity<List<ConditionRouteDTO>> responseEntity = restTemplate.exchange(url("/api/{env}/rules/route/condition/?service={service}"), GET, null, new org.springframework.core.ParameterizedTypeReference<List<ConditionRouteDTO>>() {}, env, service);
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(OK));
        Assert.assertThat(responseEntity.getBody(), Matchers.hasSize(1));
        List<String> conditions = responseEntity.getBody().stream().flatMap(( it) -> it.getConditions().stream()).collect(Collectors.toList());
        Assert.assertThat(conditions, Matchers.hasSize(2));
        Assert.assertThat(conditions, Matchers.containsInAnyOrder("=> host != 172.22.3.111", "=> host != 172.22.3.112"));
    }

    @Test
    public void shouldDeleteRule() throws Exception {
        String service = "org.apache.dubbo.demo.DemoService";
        String content = ((((("conditions:\n" + (((("- => host != 172.22.3.111\n" + "- => host != 172.22.3.112\n") + "enabled: true\n") + "force: true\n") + "key: ")) + service) + "\n") + "priority: 0\n") + "runtime: false\n") + "scope: service";
        String path = ("/dubbo/config/" + service) + "/condition-router";
        AbstractSpringIntegrationTest.zkClient.create().creatingParentContainersIfNeeded().forPath(path);
        AbstractSpringIntegrationTest.zkClient.setData().forPath(path, content.getBytes());
        Assert.assertNotNull("zk path should not be null before deleting", AbstractSpringIntegrationTest.zkClient.checkExists().forPath(path));
        ResponseEntity<String> responseEntity = restTemplate.exchange(url("/api/{env}/rules/route/condition/{service}"), DELETE, null, String.class, env, service);
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(OK));
        Assert.assertNull(AbstractSpringIntegrationTest.zkClient.checkExists().forPath(path));
    }

    @Test
    public void shouldThrowWhenDetailRouteWithUnknownId() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url("/api/{env}/rules/route/condition/{id}"), String.class, env, "non-existed-service");
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(NOT_FOUND));
    }

    @Test
    public void shouldGetRouteDetail() throws Exception {
        String service = "org.apache.dubbo.demo.DemoService";
        String content = ((((("conditions:\n" + (((("- => host != 172.22.3.111\n" + "- => host != 172.22.3.112\n") + "enabled: true\n") + "force: true\n") + "key: ")) + service) + "\n") + "priority: 0\n") + "runtime: false\n") + "scope: service";
        String path = ("/dubbo/config/" + service) + "/condition-router";
        AbstractSpringIntegrationTest.zkClient.create().creatingParentContainersIfNeeded().forPath(path);
        AbstractSpringIntegrationTest.zkClient.setData().forPath(path, content.getBytes());
        ResponseEntity<ConditionRouteDTO> responseEntity = restTemplate.getForEntity(url("/api/{env}/rules/route/condition/{id}"), ConditionRouteDTO.class, env, service);
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(OK));
        ConditionRouteDTO conditionRouteDTO = responseEntity.getBody();
        Assert.assertNotNull(conditionRouteDTO);
        Assert.assertThat(conditionRouteDTO.getConditions(), Matchers.hasSize(2));
        Assert.assertThat(conditionRouteDTO.getConditions(), Matchers.containsInAnyOrder("=> host != 172.22.3.111", "=> host != 172.22.3.112"));
    }

    @Test
    public void shouldEnableRoute() throws Exception {
        String service = "org.apache.dubbo.demo.DemoService";
        String content = ((((("conditions:\n" + (((("- => host != 172.22.3.111\n" + "- => host != 172.22.3.112\n") + "enabled: false\n") + "force: true\n") + "key: ")) + service) + "\n") + "priority: 0\n") + "runtime: false\n") + "scope: service";
        String path = ("/dubbo/config/" + service) + "/condition-router";
        AbstractSpringIntegrationTest.zkClient.create().creatingParentContainersIfNeeded().forPath(path);
        AbstractSpringIntegrationTest.zkClient.setData().forPath(path, content.getBytes());
        byte[] bytes = AbstractSpringIntegrationTest.zkClient.getData().forPath(path);
        String updatedConfig = new String(bytes);
        RoutingRule rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
        Assert.assertFalse(rule.isEnabled());
        restTemplate.put(url("/api/{env}/rules/route/condition/enable/{id}"), null, env, service);
        bytes = AbstractSpringIntegrationTest.zkClient.getData().forPath(path);
        updatedConfig = new String(bytes);
        rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
        Assert.assertTrue(rule.isEnabled());
    }

    @Test
    public void shouldDisableRoute() throws Exception {
        String service = "org.apache.dubbo.demo.DemoService";
        String content = ((((("conditions:\n" + (((("- => host != 172.22.3.111\n" + "- => host != 172.22.3.112\n") + "enabled: true\n") + "force: false\n") + "key: ")) + service) + "\n") + "priority: 0\n") + "runtime: false\n") + "scope: service";
        String path = ("/dubbo/config/" + service) + "/condition-router";
        AbstractSpringIntegrationTest.zkClient.create().creatingParentContainersIfNeeded().forPath(path);
        AbstractSpringIntegrationTest.zkClient.setData().forPath(path, content.getBytes());
        byte[] bytes = AbstractSpringIntegrationTest.zkClient.getData().forPath(path);
        String updatedConfig = new String(bytes);
        RoutingRule rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
        Assert.assertTrue(rule.isEnabled());
        restTemplate.put(url("/api/{env}/rules/route/condition/disable/{id}"), null, env, service);
        bytes = AbstractSpringIntegrationTest.zkClient.getData().forPath(path);
        updatedConfig = new String(bytes);
        rule = YamlParser.loadObject(updatedConfig, RoutingRule.class);
        Assert.assertFalse(rule.isEnabled());
    }
}

