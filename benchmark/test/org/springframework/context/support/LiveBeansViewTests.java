/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.context.support;


import LiveBeansView.MBEAN_DOMAIN_PROPERTY_NAME;
import javax.management.MalformedObjectNameException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link LiveBeansView}
 *
 * @author Stephane Nicoll
 */
public class LiveBeansViewTests {
    @Rule
    public TestName name = new TestName();

    private final MockEnvironment environment = new MockEnvironment();

    @Test
    public void registerIgnoredIfPropertyIsNotSet() throws MalformedObjectNameException {
        ConfigurableApplicationContext context = createApplicationContext("app");
        Assert.assertEquals(0, searchLiveBeansViewMeans().size());
        LiveBeansView.registerApplicationContext(context);
        Assert.assertEquals(0, searchLiveBeansViewMeans().size());
        LiveBeansView.unregisterApplicationContext(context);
    }

    @Test
    public void registerUnregisterSingleContext() throws MalformedObjectNameException {
        this.environment.setProperty(MBEAN_DOMAIN_PROPERTY_NAME, this.name.getMethodName());
        ConfigurableApplicationContext context = createApplicationContext("app");
        Assert.assertEquals(0, searchLiveBeansViewMeans().size());
        LiveBeansView.registerApplicationContext(context);
        assertSingleLiveBeansViewMbean("app");
        LiveBeansView.unregisterApplicationContext(context);
        Assert.assertEquals(0, searchLiveBeansViewMeans().size());
    }

    @Test
    public void registerUnregisterSeveralContexts() throws MalformedObjectNameException {
        this.environment.setProperty(MBEAN_DOMAIN_PROPERTY_NAME, this.name.getMethodName());
        ConfigurableApplicationContext context = createApplicationContext("app");
        ConfigurableApplicationContext childContext = createApplicationContext("child");
        Assert.assertEquals(0, searchLiveBeansViewMeans().size());
        LiveBeansView.registerApplicationContext(context);
        assertSingleLiveBeansViewMbean("app");
        LiveBeansView.registerApplicationContext(childContext);
        Assert.assertEquals(1, searchLiveBeansViewMeans().size());// Only one MBean

        LiveBeansView.unregisterApplicationContext(childContext);
        assertSingleLiveBeansViewMbean("app");// Root context removes it

        LiveBeansView.unregisterApplicationContext(context);
        Assert.assertEquals(0, searchLiveBeansViewMeans().size());
    }

    @Test
    public void registerUnregisterSeveralContextsDifferentOrder() throws MalformedObjectNameException {
        this.environment.setProperty(MBEAN_DOMAIN_PROPERTY_NAME, this.name.getMethodName());
        ConfigurableApplicationContext context = createApplicationContext("app");
        ConfigurableApplicationContext childContext = createApplicationContext("child");
        Assert.assertEquals(0, searchLiveBeansViewMeans().size());
        LiveBeansView.registerApplicationContext(context);
        assertSingleLiveBeansViewMbean("app");
        LiveBeansView.registerApplicationContext(childContext);
        assertSingleLiveBeansViewMbean("app");// Only one MBean

        LiveBeansView.unregisterApplicationContext(context);
        LiveBeansView.unregisterApplicationContext(childContext);
        Assert.assertEquals(0, searchLiveBeansViewMeans().size());
    }
}

