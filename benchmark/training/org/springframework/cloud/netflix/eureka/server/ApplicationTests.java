/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.netflix.eureka.server;


import EurekaServerAutoConfiguration.CloudServerCodecs;
import HttpMethod.GET;
import HttpStatus.OK;
import InstanceInfo.Builder;
import MediaType.APPLICATION_JSON;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.wrappers.CodecWrapper;
import com.netflix.eureka.resources.ServerCodecs;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTests.Application.class, webEnvironment = RANDOM_PORT, properties = { "spring.jmx.enabled=true", "management.security.enabled=false", "management.endpoints.web.exposure.include=*" })
public class ApplicationTests {
    private static final String BASE_PATH = new WebEndpointProperties().getBasePath();

    @LocalServerPort
    private int port = 0;

    @Autowired
    private ServerCodecs serverCodecs;

    @Test
    public void catalogLoads() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/eureka/apps"), Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void adminLoads() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(APPLICATION_JSON));
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = new TestRestTemplate().exchange(((("http://localhost:" + (this.port)) + (ApplicationTests.BASE_PATH)) + "/env"), GET, new org.springframework.http.HttpEntity("parameters", headers), Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void noDoubleSlashes() {
        String basePath = ("http://localhost:" + (this.port)) + "/";
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(basePath, String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        String body = entity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.contains((basePath + "/"))).as("basePath contains double slashes").isFalse();
    }

    @Test
    public void cssParsedByLess() {
        String basePath = ("http://localhost:" + (this.port)) + "/eureka/css/wro.css";
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(basePath, String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        String body = entity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.contains("spring-logo")).as("css wasn't preprocessed").isTrue();
    }

    @Test
    public void customCodecWorks() throws Exception {
        assertThat(this.serverCodecs).as("serverCodecs is wrong type").isInstanceOf(CloudServerCodecs.class);
        CodecWrapper codec = this.serverCodecs.getFullJsonCodec();
        assertThat(codec).as("codec is wrong type").isInstanceOf(CloudJacksonJson.class);
        InstanceInfo instanceInfo = Builder.newBuilder().setAppName("fooapp").add("instanceId", "foo").build();
        String encoded = codec.encode(instanceInfo);
        InstanceInfo decoded = codec.decode(encoded, InstanceInfo.class);
        assertThat(decoded.getInstanceId()).as("instanceId was wrong").isEqualTo("foo");
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableEurekaServer
    protected static class Application {}
}

