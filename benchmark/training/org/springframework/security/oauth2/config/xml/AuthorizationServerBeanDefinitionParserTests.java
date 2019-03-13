/**
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.config.xml;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;


/**
 *
 *
 * @author Dave Syer
 */
@RunWith(Parameterized.class)
public class AuthorizationServerBeanDefinitionParserTests {
    private static final String CHECK_TOKEN_CUSTOM_ENDPOINT_RESOURCE = "authorization-server-check-token-custom-endpoint";

    private ConfigurableApplicationContext context;

    private String resource;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    public AuthorizationServerBeanDefinitionParserTests(String resource) {
        this.resource = resource;
        this.context = new GenericXmlApplicationContext(getClass(), (resource + ".xml"));
    }

    @Test
    public void testDefaults() {
        Assert.assertTrue(context.containsBeanDefinition("oauth2AuthorizationEndpoint"));
    }

    @Test
    public void testCheckTokenCustomEndpoint() {
        if (!(AuthorizationServerBeanDefinitionParserTests.CHECK_TOKEN_CUSTOM_ENDPOINT_RESOURCE.equals(this.resource))) {
            return;
        }
        FrameworkEndpointHandlerMapping frameworkEndpointHandlerMapping = context.getBean(FrameworkEndpointHandlerMapping.class);
        Assert.assertNotNull(frameworkEndpointHandlerMapping);
        Assert.assertEquals("/custom_check_token", frameworkEndpointHandlerMapping.getPath("/oauth/check_token"));
    }
}

