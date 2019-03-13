/**
 * Copyright 2002-2011 the original author or authors.
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
package org.springframework.web.context.support;


import StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
import StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME;
import StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME;
import StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME;
import StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.tests.mock.jndi.SimpleNamingContextBuilder;


/**
 * Unit tests for {@link StandardServletEnvironment}.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class StandardServletEnvironmentTests {
    @Test
    public void propertySourceOrder() throws Exception {
        SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        ConfigurableEnvironment env = new StandardServletEnvironment();
        MutablePropertySources sources = env.getPropertySources();
        Assert.assertThat(sources.precedenceOf(PropertySource.named(SERVLET_CONFIG_PROPERTY_SOURCE_NAME)), CoreMatchers.equalTo(0));
        Assert.assertThat(sources.precedenceOf(PropertySource.named(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME)), CoreMatchers.equalTo(1));
        Assert.assertThat(sources.precedenceOf(PropertySource.named(JNDI_PROPERTY_SOURCE_NAME)), CoreMatchers.equalTo(2));
        Assert.assertThat(sources.precedenceOf(PropertySource.named(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)), CoreMatchers.equalTo(3));
        Assert.assertThat(sources.precedenceOf(PropertySource.named(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)), CoreMatchers.equalTo(4));
        Assert.assertThat(sources.size(), CoreMatchers.is(5));
    }
}

