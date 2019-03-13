/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.test.context;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Tests for {@link SpringBootTest} configured with {@link WebEnvironment#DEFINED_PORT}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
@DirtiesContext
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, properties = { "server.port=0", "value=123" })
@ContextHierarchy({ @ContextConfiguration(classes = SpringBootTestWebEnvironmentContextHierarchyTests.ParentConfiguration.class), @ContextConfiguration(classes = SpringBootTestWebEnvironmentContextHierarchyTests.ChildConfiguration.class) })
@RunWith(SpringRunner.class)
public class SpringBootTestWebEnvironmentContextHierarchyTests {
    @Autowired
    private ApplicationContext context;

    @Test
    public void testShouldOnlyStartSingleServer() {
        ApplicationContext parent = this.context.getParent();
        assertThat(this.context).isInstanceOf(WebApplicationContext.class);
        assertThat(parent).isNotInstanceOf(WebApplicationContext.class);
    }

    @Configuration
    protected static class ParentConfiguration extends AbstractSpringBootTestWebServerWebEnvironmentTests.AbstractConfig {}

    @Configuration
    @EnableWebMvc
    @RestController
    protected static class ChildConfiguration extends AbstractSpringBootTestWebServerWebEnvironmentTests.AbstractConfig {}
}

