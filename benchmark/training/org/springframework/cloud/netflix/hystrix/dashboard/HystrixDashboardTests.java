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
package org.springframework.cloud.netflix.hystrix.dashboard;


import HttpStatus.OK;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dave Syer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HystrixDashboardTests.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = { "spring.application.name=hystrix-dashboard" })
public class HystrixDashboardTests {
    @Value("${local.server.port}")
    private int port = 0;

    @Test
    public void homePage() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/hystrix"), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        String body = entity.getBody();
        assertThat(body.contains("<base href=\"/hystrix\">")).isTrue();
        assertThat(body.contains("\"/webjars")).isTrue();
        assertThat(body.contains("= \"/hystrix/monitor")).isTrue();
    }

    @Test
    public void cssAvailable() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/hystrix/css/global.css"), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void monitorPage() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/hystrix/monitor"), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        String body = entity.getBody();
        assertThat(body.contains("<base href=\"/hystrix/monitor\">")).isTrue();
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableHystrixDashboard
    protected static class Application {}
}

