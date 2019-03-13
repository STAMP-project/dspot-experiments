/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.test.web.servlet.samples.context;


import MediaType.APPLICATION_JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


/**
 * Tests with XML configuration.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("src/test/resources/META-INF/web-resources")
@ContextHierarchy({ @ContextConfiguration("root-context.xml"), @ContextConfiguration("servlet-context.xml") })
public class XmlConfigTests {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PersonDao personDao;

    private MockMvc mockMvc;

    @Test
    public void person() throws Exception {
        this.mockMvc.perform(get("/person/5").accept(APPLICATION_JSON)).andDo(print()).andExpect(status().isOk()).andExpect(content().string("{\"name\":\"Joe\",\"someDouble\":0.0,\"someBoolean\":false}"));
    }

    @Test
    public void tilesDefinitions() throws Exception {
        // 
        // 
        this.mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(forwardedUrl("/WEB-INF/layouts/standardLayout.jsp"));
    }
}

