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


import Constants.SERVICE;
import HttpMethod.GET;
import HttpStatus.OK;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.dubbo.admin.AbstractSpringIntegrationTest;
import org.apache.dubbo.admin.model.dto.ServiceDTO;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.Registry;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;


@Ignore
public class ServiceControllerTest extends AbstractSpringIntegrationTest {
    @Autowired
    private Registry registry;

    @Test
    public void shouldGetAllServices() throws Exception {
        final int num = 10;
        for (int i = 0; i < num; i++) {
            final String service = "org.apache.dubbo.admin.test.service" + i;
            final URL url = ((i % 2) == 0) ? generateProviderServiceUrl("dubbo-admin", service) : generateConsumerServiceUrl("dubbo-admin", service);
            registry.register(url);
        }
        TimeUnit.SECONDS.sleep(1);
        final ResponseEntity<Set<String>> response = restTemplate.exchange(url("/api/{env}/services"), GET, null, new org.springframework.core.ParameterizedTypeReference<Set<String>>() {}, "whatever");
        Assert.assertThat(response.getStatusCode(), Matchers.is(OK));
        Assert.assertThat(response.getBody(), Matchers.hasSize(num));
    }

    @Test
    public void shouldGetAllApplications() throws Exception {
        final int num = 10;
        for (int i = 0; i < num; i++) {
            final String service = "org.apache.dubbo.admin.test.service";
            final URL url = generateProviderServiceUrl(("dubbo-admin-" + i), service);
            registry.register(url);
        }
        TimeUnit.SECONDS.sleep(1);
        final ResponseEntity<Set<String>> responseEntity = restTemplate.exchange(url("/api/{env}/applications"), GET, null, new org.springframework.core.ParameterizedTypeReference<Set<String>>() {}, "whatever");
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(OK));
        Assert.assertThat(responseEntity.getBody(), Matchers.hasSize(num));
    }

    @Test
    public void shouldFilterUsingPattern() throws InterruptedException {
        final int num = 10;
        final String application = "dubbo-admin";
        for (int i = 0; i < num; i++) {
            final String service = (("org.apache.dubbo.admin.test.service" + i) + ".pattern") + (i % 2);
            registry.register(generateProviderServiceUrl(application, service));
        }
        TimeUnit.SECONDS.sleep(1);
        ResponseEntity<Set<ServiceDTO>> responseEntity = restTemplate.exchange(url("/api/{env}/service?pattern={pattern}&filter={filter}"), GET, null, new org.springframework.core.ParameterizedTypeReference<Set<ServiceDTO>>() {}, "whatever", SERVICE, "*");
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(OK));
        Assert.assertThat(responseEntity.getBody(), Matchers.hasSize(num));
        responseEntity = restTemplate.exchange(url("/api/{env}/service?pattern={pattern}&filter={filter}"), GET, null, new org.springframework.core.ParameterizedTypeReference<Set<ServiceDTO>>() {}, "whatever", SERVICE, "*pattern0");
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(OK));
        Assert.assertThat(responseEntity.getBody(), Matchers.hasSize((num / 2)));
        responseEntity = restTemplate.exchange(url("/api/{env}/service?pattern={pattern}&filter={filter}"), GET, null, new org.springframework.core.ParameterizedTypeReference<Set<ServiceDTO>>() {}, "whatever", SERVICE, "*pattern1");
        Assert.assertThat(responseEntity.getStatusCode(), Matchers.is(OK));
        Assert.assertThat(responseEntity.getBody(), Matchers.hasSize((num / 2)));
    }
}

