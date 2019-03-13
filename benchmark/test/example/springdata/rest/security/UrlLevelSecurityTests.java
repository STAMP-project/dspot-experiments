/**
 * Copyright 2014-2018 the original author or authors.
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
package example.springdata.rest.security;


import HttpHeaders.ACCEPT;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.CONTENT_TYPE;
import HttpHeaders.LOCATION;
import MediaType.APPLICATION_JSON_VALUE;
import MediaTypes.HAL_JSON;
import MediaTypes.HAL_JSON_VALUE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


/**
 * Test cases that verify the URL level of security by using the Spring MVC test framework.
 *
 * @author Greg Turnquist
 * @author Oliver Gierke
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UrlLevelSecurityTests {
    static final String PAYLOAD = "{\"firstName\": \"Saruman\", \"lastName\": \"the White\", " + "\"title\": \"Wizard\"}";

    @Autowired
    WebApplicationContext context;

    @Autowired
    FilterChainProxy filterChain;

    MockMvc mvc;

    @Test
    public void allowsAccessToRootResource() throws Exception {
        // 
        // 
        mvc.perform(// 
        get("/").accept(HAL_JSON)).andExpect(content().contentTypeCompatibleWith(HAL_JSON)).andExpect(status().isOk());
    }

    @Test
    public void rejectsPostAccessToCollectionResource() throws Exception {
        // 
        mvc.perform(// 
        // 
        post("/employees").content(UrlLevelSecurityTests.PAYLOAD).accept(HAL_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    public void allowsGetRequestsButRejectsPostForUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCEPT, HAL_JSON_VALUE);
        headers.add(AUTHORIZATION, ("Basic " + (new String(Base64.getEncoder().encodeToString("greg:turnquist".getBytes())))));
        // 
        // 
        mvc.perform(// 
        get("/employees").headers(headers)).andExpect(content().contentTypeCompatibleWith(HAL_JSON)).andExpect(status().isOk());
        // 
        mvc.perform(// 
        post("/employees").headers(headers)).andExpect(status().isForbidden());
    }

    @Test
    public void allowsPostRequestForAdmin() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, HAL_JSON_VALUE);
        headers.set(AUTHORIZATION, ("Basic " + (new String(Base64.getEncoder().encodeToString("ollie:gierke".getBytes())))));
        // 
        // 
        mvc.perform(// 
        get("/employees").headers(headers)).andExpect(content().contentTypeCompatibleWith(HAL_JSON)).andExpect(status().isOk());
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        String location = // 
        // 
        mvc.perform(// 
        // 
        post("/employees").content(UrlLevelSecurityTests.PAYLOAD).headers(headers)).andExpect(status().isCreated()).andReturn().getResponse().getHeader(LOCATION);
        ObjectMapper mapper = new ObjectMapper();
        String content = // 
        mvc.perform(get(location)).andReturn().getResponse().getContentAsString();
        Employee employee = mapper.readValue(content, Employee.class);
        Assert.assertThat(employee.getFirstName(), CoreMatchers.is("Saruman"));
        Assert.assertThat(employee.getLastName(), CoreMatchers.is("the White"));
        Assert.assertThat(employee.getTitle(), CoreMatchers.is("Wizard"));
    }
}

