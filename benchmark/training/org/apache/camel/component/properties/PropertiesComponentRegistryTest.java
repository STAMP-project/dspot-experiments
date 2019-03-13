/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.properties;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.TestSupport;
import org.apache.camel.component.bean.MyDummyBean;
import org.apache.camel.component.bean.MyFooBean;
import org.junit.Assert;
import org.junit.Test;


public class PropertiesComponentRegistryTest extends ContextTestSupport {
    private MyFooBean foo;

    private MyDummyBean bar;

    @Test
    public void testPropertiesComponentRegistryPlain() throws Exception {
        context.start();
        Assert.assertSame(foo, context.getRegistry().lookupByName("foo"));
        Assert.assertSame(bar, context.getRegistry().lookupByName("bar"));
        Assert.assertNull(context.getRegistry().lookupByName("unknown"));
    }

    @Test
    public void testPropertiesComponentRegistryLookupName() throws Exception {
        context.start();
        Assert.assertSame(foo, context.getRegistry().lookupByName("{{bean.foo}}"));
        Assert.assertSame(bar, context.getRegistry().lookupByName("{{bean.bar}}"));
        try {
            context.getRegistry().lookupByName("{{bean.unknown}}");
            Assert.fail("Should have thrown exception");
        } catch (RuntimeCamelException e) {
            IllegalArgumentException cause = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Property with key [bean.unknown] not found in properties from text: {{bean.unknown}}", cause.getMessage());
        }
    }

    @Test
    public void testPropertiesComponentRegistryLookupNameAndType() throws Exception {
        context.start();
        Assert.assertSame(foo, context.getRegistry().lookupByNameAndType("{{bean.foo}}", MyFooBean.class));
        Assert.assertSame(bar, context.getRegistry().lookupByNameAndType("{{bean.bar}}", MyDummyBean.class));
        try {
            context.getRegistry().lookupByNameAndType("{{bean.unknown}}", MyDummyBean.class);
            Assert.fail("Should have thrown exception");
        } catch (RuntimeCamelException e) {
            IllegalArgumentException cause = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Property with key [bean.unknown] not found in properties from text: {{bean.unknown}}", cause.getMessage());
        }
    }
}

