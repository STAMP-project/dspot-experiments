/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.jndi;


import javax.naming.Context;
import javax.naming.NamingException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.mock.jndi.SimpleNamingContext;


/**
 * Unit tests for {@link JndiPropertySource}.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public class JndiPropertySourceTests {
    @Test
    public void nonExistentProperty() {
        JndiPropertySource ps = new JndiPropertySource("jndiProperties");
        Assert.assertThat(ps.getProperty("bogus"), CoreMatchers.nullValue());
    }

    @Test
    public void nameBoundWithoutPrefix() {
        final SimpleNamingContext context = new SimpleNamingContext();
        context.bind("p1", "v1");
        JndiTemplate jndiTemplate = new JndiTemplate() {
            @Override
            protected Context createInitialContext() throws NamingException {
                return context;
            }
        };
        JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
        jndiLocator.setResourceRef(true);
        jndiLocator.setJndiTemplate(jndiTemplate);
        JndiPropertySource ps = new JndiPropertySource("jndiProperties", jndiLocator);
        Assert.assertThat(ps.getProperty("p1"), CoreMatchers.equalTo("v1"));
    }

    @Test
    public void nameBoundWithPrefix() {
        final SimpleNamingContext context = new SimpleNamingContext();
        context.bind("java:comp/env/p1", "v1");
        JndiTemplate jndiTemplate = new JndiTemplate() {
            @Override
            protected Context createInitialContext() throws NamingException {
                return context;
            }
        };
        JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
        jndiLocator.setResourceRef(true);
        jndiLocator.setJndiTemplate(jndiTemplate);
        JndiPropertySource ps = new JndiPropertySource("jndiProperties", jndiLocator);
        Assert.assertThat(ps.getProperty("p1"), CoreMatchers.equalTo("v1"));
    }

    @Test
    public void propertyWithDefaultClauseInResourceRefMode() {
        JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate() {
            @Override
            public Object lookup(String jndiName) throws NamingException {
                throw new IllegalStateException("Should not get called");
            }
        };
        jndiLocator.setResourceRef(true);
        JndiPropertySource ps = new JndiPropertySource("jndiProperties", jndiLocator);
        Assert.assertThat(ps.getProperty("propertyKey:defaultValue"), CoreMatchers.nullValue());
    }

    @Test
    public void propertyWithColonInNonResourceRefMode() {
        JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate() {
            @Override
            public Object lookup(String jndiName) throws NamingException {
                Assert.assertEquals("my:key", jndiName);
                return "my:value";
            }
        };
        jndiLocator.setResourceRef(false);
        JndiPropertySource ps = new JndiPropertySource("jndiProperties", jndiLocator);
        Assert.assertThat(ps.getProperty("my:key"), CoreMatchers.equalTo("my:value"));
    }
}

