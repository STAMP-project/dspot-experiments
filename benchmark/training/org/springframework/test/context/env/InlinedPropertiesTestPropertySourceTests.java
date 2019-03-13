/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.test.context.env;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration tests for {@link TestPropertySource @TestPropertySource} support with
 * inlined properties.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestPropertySource(properties = { "", "foo = bar", "baz quux", "enigma: 42", "x.y.z = a=b=c", "server.url = http://example.com", "key.value.1: key=value", "key.value.2 key=value", "key.value.3 key:value" })
public class InlinedPropertiesTestPropertySourceTests {
    @Autowired
    private ConfigurableEnvironment env;

    @Test
    public void propertiesAreAvailableInEnvironment() {
        // Simple key/value pairs
        Assert.assertThat(property("foo"), CoreMatchers.is("bar"));
        Assert.assertThat(property("baz"), CoreMatchers.is("quux"));
        Assert.assertThat(property("enigma"), CoreMatchers.is("42"));
        // Values containing key/value delimiters (":", "=", " ")
        Assert.assertThat(property("x.y.z"), CoreMatchers.is("a=b=c"));
        Assert.assertThat(property("server.url"), CoreMatchers.is("http://example.com"));
        Assert.assertThat(property("key.value.1"), CoreMatchers.is("key=value"));
        Assert.assertThat(property("key.value.2"), CoreMatchers.is("key=value"));
        Assert.assertThat(property("key.value.3"), CoreMatchers.is("key:value"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void propertyNameOrderingIsPreservedInEnvironment() {
        final String[] expectedPropertyNames = new String[]{ "foo", "baz", "enigma", "x.y.z", "server.url", "key.value.1", "key.value.2", "key.value.3" };
        EnumerablePropertySource eps = ((EnumerablePropertySource) (env.getPropertySources().get(INLINED_PROPERTIES_PROPERTY_SOURCE_NAME)));
        Assert.assertArrayEquals(expectedPropertyNames, eps.getPropertyNames());
    }

    // -------------------------------------------------------------------
    /* no user beans required for these tests */
    @Configuration
    static class Config {}
}

