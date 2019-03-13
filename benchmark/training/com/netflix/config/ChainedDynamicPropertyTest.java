/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.config;


import ChainedDynamicProperty.BooleanProperty;
import ChainedDynamicProperty.FloatProperty;
import com.netflix.config.ChainedDynamicProperty.DynamicBooleanPropertyThatSupportsNull;
import com.netflix.config.ChainedDynamicProperty.IntProperty;
import com.netflix.config.ChainedDynamicProperty.StringProperty;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ChainedDynamicPropertyTest {
    @Test
    public void testString() throws Exception {
        DynamicStringProperty pString = DynamicPropertyFactory.getInstance().getStringProperty("defaultString", "default-default");
        ChainedDynamicProperty.StringProperty fString = new ChainedDynamicProperty.StringProperty("overrideString", pString);
        Assert.assertTrue("default-default".equals(fString.get()));
        ConfigurationManager.getConfigInstance().setProperty("defaultString", "default");
        Assert.assertTrue("default".equals(fString.get()));
        ConfigurationManager.getConfigInstance().setProperty("overrideString", "override");
        Assert.assertTrue("override".equals(fString.get()));
        ConfigurationManager.getConfigInstance().clearProperty("overrideString");
        Assert.assertTrue("default".equals(fString.get()));
        ConfigurationManager.getConfigInstance().clearProperty("defaultString");
        Assert.assertTrue("default-default".equals(fString.get()));
        Assert.assertEquals("default-default", fString.getDefaultValue());
    }

    @Test
    public void testInteger() throws Exception {
        DynamicIntProperty pInt = DynamicPropertyFactory.getInstance().getIntProperty("defaultInt", (-1));
        ConfigurationManager.getConfigInstance().setProperty("defaultInt", (-1));
        ChainedDynamicProperty.IntProperty fInt = new ChainedDynamicProperty.IntProperty("overrideInt", pInt);
        Assert.assertTrue(((-1) == (fInt.get())));
        ConfigurationManager.getConfigInstance().setProperty("defaultInt", 10);
        Assert.assertTrue((10 == (fInt.get())));
        ConfigurationManager.getConfigInstance().setProperty("overrideInt", 11);
        Assert.assertTrue((11 == (fInt.get())));
        ConfigurationManager.getConfigInstance().clearProperty("overrideInt");
        Assert.assertTrue((10 == (fInt.get())));
        ConfigurationManager.getConfigInstance().clearProperty("defaultInt");
        Assert.assertTrue(((-1) == (fInt.get())));
        Assert.assertEquals(Integer.valueOf((-1)), fInt.getDefaultValue());
    }

    @Test
    public void testBoolean() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("defaultInt", 1234);
        DynamicBooleanPropertyThatSupportsNull pBoolean = new DynamicBooleanPropertyThatSupportsNull("defaultBoolean", Boolean.FALSE);
        ConfigurationManager.getConfigInstance().setProperty("defaultBoolean", Boolean.TRUE);
        ChainedDynamicProperty.BooleanProperty fBoolean = new ChainedDynamicProperty.BooleanProperty("overrideBoolean", pBoolean);
        Assert.assertTrue(fBoolean.get());
        ConfigurationManager.getConfigInstance().setProperty("defaultBoolean", Boolean.FALSE);
        Assert.assertFalse(fBoolean.get());
        ConfigurationManager.getConfigInstance().setProperty("overrideBoolean", Boolean.TRUE);
        Assert.assertTrue(fBoolean.get());
        ConfigurationManager.getConfigInstance().clearProperty("overrideBoolean");
        Assert.assertFalse(fBoolean.get());
        ConfigurationManager.getConfigInstance().clearProperty("defaultBoolean");
        Assert.assertFalse(fBoolean.get());
        Assert.assertFalse(fBoolean.getDefaultValue());
    }

    @Test
    public void testFloat() throws Exception {
        DynamicFloatProperty pFloat = DynamicPropertyFactory.getInstance().getFloatProperty("defaultFloat", (-1.0F));
        ChainedDynamicProperty.FloatProperty fFloat = new ChainedDynamicProperty.FloatProperty("overrideFloat", pFloat);
        Assert.assertTrue(((-1.0F) == (fFloat.get())));
        ConfigurationManager.getConfigInstance().setProperty("defaultFloat", 10.0F);
        Assert.assertTrue((10.0F == (fFloat.get())));
        ConfigurationManager.getConfigInstance().setProperty("overrideFloat", 11.0F);
        Assert.assertTrue((11.0F == (fFloat.get())));
        ConfigurationManager.getConfigInstance().clearProperty("overrideFloat");
        Assert.assertTrue((10.0F == (fFloat.get())));
        ConfigurationManager.getConfigInstance().clearProperty("defaultFloat");
        Assert.assertTrue(((-1.0F) == (fFloat.get())));
        Assert.assertEquals(Float.valueOf((-1.0F)), fFloat.getDefaultValue());
    }

    @Test
    public void testChainingString() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("node1", "v1");
        ConfigurationManager.getConfigInstance().clearProperty("node2");
        ConfigurationManager.getConfigInstance().clearProperty("node3");
        DynamicStringProperty node1 = DynamicPropertyFactory.getInstance().getStringProperty("node1", "v1");
        StringProperty node2 = new ChainedDynamicProperty.StringProperty("node2", node1);
        ChainedDynamicProperty.StringProperty node3 = new ChainedDynamicProperty.StringProperty("node3", node2);
        Assert.assertTrue(("" + (node3.get())), "v1".equals(node3.get()));
        ConfigurationManager.getConfigInstance().setProperty("node1", "v11");
        Assert.assertTrue("v11".equals(node3.get()));
        ConfigurationManager.getConfigInstance().setProperty("node2", "v22");
        Assert.assertTrue("v22".equals(node3.get()));
        ConfigurationManager.getConfigInstance().clearProperty("node1");
        Assert.assertTrue("v22".equals(node3.get()));
        ConfigurationManager.getConfigInstance().setProperty("node3", "v33");
        Assert.assertTrue("v33".equals(node3.get()));
        ConfigurationManager.getConfigInstance().clearProperty("node2");
        Assert.assertTrue("v33".equals(node3.get()));
        ConfigurationManager.getConfigInstance().setProperty("node2", "v222");
        Assert.assertTrue("v33".equals(node3.get()));
        ConfigurationManager.getConfigInstance().clearProperty("node3");
        Assert.assertTrue("v222".equals(node3.get()));
        ConfigurationManager.getConfigInstance().clearProperty("node2");
        Assert.assertTrue("v1".equals(node3.get()));
        ConfigurationManager.getConfigInstance().setProperty("node2", "v2222");
        Assert.assertTrue("v2222".equals(node3.get()));
        Assert.assertEquals("v1", node3.getDefaultValue());
    }

    @Test
    public void testChainingInteger() throws Exception {
        DynamicIntProperty node1 = DynamicPropertyFactory.getInstance().getIntProperty("node1", 1);
        IntProperty node2 = new ChainedDynamicProperty.IntProperty("node2", node1);
        ChainedDynamicProperty.IntProperty node3 = new ChainedDynamicProperty.IntProperty("node3", node2);
        Assert.assertTrue(("" + (node3.get())), (1 == (node3.get())));
        ConfigurationManager.getConfigInstance().setProperty("node1", 11);
        Assert.assertTrue((11 == (node3.get())));
        ConfigurationManager.getConfigInstance().setProperty("node2", 22);
        Assert.assertTrue((22 == (node3.get())));
        ConfigurationManager.getConfigInstance().clearProperty("node1");
        Assert.assertTrue((22 == (node3.get())));
        ConfigurationManager.getConfigInstance().setProperty("node3", 33);
        Assert.assertTrue((33 == (node3.get())));
        ConfigurationManager.getConfigInstance().clearProperty("node2");
        Assert.assertTrue((33 == (node3.get())));
        ConfigurationManager.getConfigInstance().setProperty("node2", 222);
        Assert.assertTrue((33 == (node3.get())));
        ConfigurationManager.getConfigInstance().clearProperty("node3");
        Assert.assertTrue((222 == (node3.get())));
        ConfigurationManager.getConfigInstance().clearProperty("node2");
        Assert.assertTrue((1 == (node3.get())));
        ConfigurationManager.getConfigInstance().setProperty("node2", 2222);
        Assert.assertTrue((2222 == (node3.get())));
    }

    @Test
    public void testAddCallback() throws Exception {
        final DynamicStringProperty node1 = DynamicPropertyFactory.getInstance().getStringProperty("n1", "n1");
        final ChainedDynamicProperty.StringProperty node2 = new ChainedDynamicProperty.StringProperty("n2", node1);
        final AtomicInteger callbackCount = new AtomicInteger(0);
        node2.addCallback(new Runnable() {
            @Override
            public void run() {
                callbackCount.incrementAndGet();
            }
        });
        Assert.assertTrue((0 == (callbackCount.get())));
        Assert.assertTrue("n1".equals(node2.get()));
        Assert.assertTrue((0 == (callbackCount.get())));
        ConfigurationManager.getConfigInstance().setProperty("n1", "n11");
        Assert.assertTrue("n11".equals(node2.get()));
        Assert.assertTrue((0 == (callbackCount.get())));
        ConfigurationManager.getConfigInstance().setProperty("n2", "n22");
        Assert.assertTrue("n22".equals(node2.get()));
        Assert.assertTrue((1 == (callbackCount.get())));
        ConfigurationManager.getConfigInstance().clearProperty("n1");
        Assert.assertTrue("n22".equals(node2.get()));
        Assert.assertTrue((1 == (callbackCount.get())));
        ConfigurationManager.getConfigInstance().setProperty("n2", "n222");
        Assert.assertTrue("n222".equals(node2.get()));
        Assert.assertTrue((2 == (callbackCount.get())));
        ConfigurationManager.getConfigInstance().clearProperty("n2");
        Assert.assertTrue("n1".equals(node2.get()));
        Assert.assertTrue((3 == (callbackCount.get())));
    }
}

