/**
 * Copyright 2016-2018 the original author or authors.
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
package example.users;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.ProjectingJackson2HttpMessageConverter;
import org.springframework.data.web.XmlBeamHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Integration tests for {@link UserController} to demonstrate client-side resilience of the payload type against
 * changes in the representation.
 *
 * @author Oliver Gierke
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerClientTests {
    @Autowired
    TestRestTemplate template;

    /**
     * Custom configuration for the test to enrich the {@link TestRestTemplate} with the {@link HttpMessageConverter}s for
     * XML and JSON projections.
     *
     * @author Oliver Gierke
     */
    @Configuration
    @Import(Application.class)
    static class Config {
        @Bean
        RestTemplateBuilder builder() {
            return // 
            // 
            new RestTemplateBuilder().additionalMessageConverters(new ProjectingJackson2HttpMessageConverter()).additionalMessageConverters(new XmlBeamHttpMessageConverter());
        }
    }

    @Test
    public void accessJsonFieldsOnSimplePayload() {
        UserControllerClientTests.assertDave(issueGet("/", APPLICATION_JSON));
    }

    @Test
    public void accessJsonFieldsOnNestedPayload() {
        UserControllerClientTests.assertDave(issueGet("/changed", APPLICATION_JSON));
    }

    @Test
    public void accessXmlElementsOnSimplePayload() {
        UserControllerClientTests.assertDave(issueGet("/", APPLICATION_XML));
    }

    @Test
    public void accessXmlElementsOnNestedPayload() {
        UserControllerClientTests.assertDave(issueGet("/changed", APPLICATION_XML));
    }
}

