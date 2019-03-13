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


import java.io.File;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DynamicPropertyTest {
    static File configFile;

    private static final String PROP_NAME = "biz.mindyourown.notMine";

    private static final String PROP_NAME2 = "biz.mindyourown.myProperty";

    private static DynamicConfiguration config;

    boolean meGotCalled = false;

    @Test
    public void testAsFileBased() throws Exception {
        // TODO: create a static DynamicProperties class
        DynamicStringProperty prop = new DynamicStringProperty("props1", null);
        DynamicStringProperty prop2 = new DynamicStringProperty("props2", null);
        DynamicIntProperty prop3 = new DynamicIntProperty("props3", 0);
        Thread.sleep(1000);
        Assert.assertEquals("xyz", prop.get());
        Assert.assertEquals("abc", prop2.get());
        Assert.assertEquals(0, prop3.get());
        DynamicPropertyTest.modifyConfigFile();
        // waiting for reload
        Thread.sleep(2000);
        Assert.assertNull(prop.get());
        Assert.assertEquals("456", prop2.get());
        Assert.assertEquals(123, prop3.get());
        DynamicPropertyTest.config.stopLoading();
        Thread.sleep(2000);
        DynamicPropertyTest.config.setProperty("props2", "000");
        Assert.assertEquals("000", prop2.get());
    }

    @Test
    public void testDynamicProperty() {
        DynamicPropertyTest.config.stopLoading();
        DynamicProperty fastProp = DynamicProperty.getInstance(DynamicPropertyTest.PROP_NAME);
        Assert.assertEquals("FastProperty does not have correct name", DynamicPropertyTest.PROP_NAME, fastProp.getName());
        Assert.assertSame("DynamicProperty.getInstance did not find the object", fastProp, DynamicProperty.getInstance(DynamicPropertyTest.PROP_NAME));
        // 
        String hello = "Hello";
        Assert.assertNull("Unset DynamicProperty is not null", fastProp.getString());
        Assert.assertEquals("Unset DynamicProperty does not default correctly", hello, fastProp.getString(hello));
        DynamicPropertyTest.config.setProperty(DynamicPropertyTest.PROP_NAME, hello);
        Assert.assertEquals("Set DynamicProperty does not have correct value", hello, fastProp.getString());
        Assert.assertEquals("Set DynamicProperty uses supplied default", hello, fastProp.getString(("not " + hello)));
        Assert.assertEquals("Non-integer DynamicProperty doesn't default on integer fetch", 123, fastProp.getInteger(Integer.valueOf(123)).intValue());
        Assert.assertEquals("Non-float DynamicProperty doesn't default on float fetch", 2.71838F, fastProp.getFloat(Float.valueOf(2.71838F)).floatValue(), 0.001F);
        try {
            fastProp.getFloat();
            Assert.fail(("Parse should have failed:  " + fastProp));
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
        // 
        String pi = "3.14159";
        String ee = "2.71838";
        DynamicPropertyTest.config.setProperty(DynamicPropertyTest.PROP_NAME, pi);
        Assert.assertEquals("Set DynamicProperty does not have correct value", pi, fastProp.getString());
        Assert.assertEquals("DynamicProperty did not property parse float string", 3.14159F, fastProp.getFloat(Float.valueOf(0.0F)).floatValue(), 0.001F);
        DynamicPropertyTest.config.setProperty(DynamicPropertyTest.PROP_NAME, ee);
        Assert.assertEquals("Set DynamicProperty does not have correct value", ee, fastProp.getString());
        Assert.assertEquals("DynamicProperty did not property parse float string", 2.71838F, fastProp.getFloat(Float.valueOf(0.0F)).floatValue(), 0.001F);
        try {
            fastProp.getInteger();
            Assert.fail(("Integer fetch of non-integer DynamicProperty should have failed:  " + fastProp));
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
        Assert.assertEquals("Integer fetch of non-integer DynamicProperty did not use default value", (-123), fastProp.getInteger(Integer.valueOf((-123))).intValue());
        // 
        String devil = "666";
        DynamicPropertyTest.config.setProperty(DynamicPropertyTest.PROP_NAME, devil);
        Assert.assertEquals("Changing DynamicProperty does not result in correct value", devil, fastProp.getString());
        Assert.assertEquals("Integer fetch of changed DynamicProperty did not return correct value", 666, fastProp.getInteger().intValue());
        // 
        String self = "com.netflix.config.DynamicProperty";
        Assert.assertEquals("Fetch of named class from integer valued DynamicProperty did not use default", DynamicPropertyTest.class, fastProp.getNamedClass(DynamicPropertyTest.class));
        DynamicPropertyTest.config.setProperty(DynamicPropertyTest.PROP_NAME, self);
        Assert.assertEquals("Fetch of named class from DynamicProperty did not find the class", DynamicProperty.class, fastProp.getNamedClass());
        // Check that clearing a property clears all caches
        DynamicPropertyTest.config.clearProperty(DynamicPropertyTest.PROP_NAME);
        Assert.assertNull("Fetch of cleard property did not return null", fastProp.getString());
        Assert.assertEquals("Fetch of cleard property did not use default value", devil, fastProp.getString(devil));
        Assert.assertNull("Fetch of cleard property did not return null", fastProp.getInteger());
        Assert.assertEquals("Fetch of cleard property did not use default value", (-123), fastProp.getInteger(Integer.valueOf((-123))).intValue());
        Assert.assertNull("Fetch of cleard property did not return null", fastProp.getFloat());
        Assert.assertEquals("Fetch of cleard property did not use default value", 2.71838F, fastProp.getFloat(Float.valueOf(2.71838F)).floatValue(), 0.001F);
        Assert.assertNull("Fetch of cleard property did not return null", fastProp.getNamedClass());
        Assert.assertEquals("Fetch of cleard property did not use default value", DynamicProperty.class, fastProp.getNamedClass(DynamicProperty.class));
        // 
        String yes = "yes";
        String maybe = "maybe";
        String no = "Off";
        DynamicPropertyTest.config.setProperty(DynamicPropertyTest.PROP_NAME, yes);
        Assert.assertTrue("boolean property set to 'yes' is not true", fastProp.getBoolean().booleanValue());
        DynamicPropertyTest.config.setProperty(DynamicPropertyTest.PROP_NAME, no);
        Assert.assertTrue("boolean property set to 'no' is not false", (!(fastProp.getBoolean().booleanValue())));
        DynamicPropertyTest.config.setProperty(DynamicPropertyTest.PROP_NAME, maybe);
        try {
            fastProp.getBoolean();
            Assert.fail(("Parse should have failed: " + fastProp));
        } catch (IllegalArgumentException e) {
            Assert.assertNotNull(e);
        }
        Assert.assertTrue(fastProp.getBoolean(Boolean.TRUE).booleanValue());
        Assert.assertTrue((!(fastProp.getBoolean(Boolean.FALSE).booleanValue())));
    }

    @Test
    public void testPerformance() {
        DynamicPropertyTest.config.stopLoading();
        DynamicProperty fastProp = DynamicProperty.getInstance(DynamicPropertyTest.PROP_NAME2);
        String goodbye = "Goodbye";
        int loopCount = 1000000;
        DynamicPropertyTest.config.setProperty(DynamicPropertyTest.PROP_NAME2, goodbye);
        long cnt = 0;
        long start = System.currentTimeMillis();
        for (int i = 0; i < loopCount; i++) {
            cnt += fastProp.getString().length();
        }
        long elapsed = (System.currentTimeMillis()) - start;
        System.out.println((((("Fetched dynamic property " + loopCount) + " times in ") + elapsed) + " milliseconds"));
        // Now for the "normal" time
        cnt = 0;
        start = System.currentTimeMillis();
        for (int i = 0; i < loopCount; i++) {
            cnt += DynamicPropertyTest.config.getString(DynamicPropertyTest.PROP_NAME2).length();
        }
        elapsed = (System.currentTimeMillis()) - start;
        System.out.println((((("Fetched Configuration value " + loopCount) + " times in ") + elapsed) + " milliseconds"));
        // Now for the "system property" time
        cnt = 0;
        System.setProperty(DynamicPropertyTest.PROP_NAME2, goodbye);
        start = System.currentTimeMillis();
        for (int i = 0; i < loopCount; i++) {
            cnt += System.getProperty(DynamicPropertyTest.PROP_NAME2).length();
        }
        elapsed = (System.currentTimeMillis()) - start;
        System.out.println((((("Fetched system property value " + loopCount) + " times in ") + elapsed) + " milliseconds"));
    }

    @Test
    public void testDynamicPropertyListenerPropertyChangeCallback() {
        DynamicPropertyTest.config.stopLoading();
        DynamicStringProperty listOfCountersToExportProperty = new DynamicStringProperty("com.netflix.eds.utils.EdsCounter.listOfCountersToExport", "") {
            @Override
            protected void propertyChanged() {
                meGotCalled = true;
            }
        };
        DynamicPropertyTest.config.setProperty("com.netflix.eds.utils.EdsCounter.listOfCountersToExport", "valuechanged");
        Assert.assertTrue("propertyChanged did not get called", meGotCalled);
        Assert.assertEquals("valuechanged", listOfCountersToExportProperty.get());
    }

    @Test
    public void testFastProperyTimestamp() throws Exception {
        DynamicPropertyTest.config.stopLoading();
        DynamicStringProperty prop = new DynamicStringProperty("com.netflix.testing.timestamp", "hello");
        long initialTime = prop.getChangedTimestamp();
        Thread.sleep(10);
        Assert.assertEquals(prop.getChangedTimestamp(), initialTime);
        DynamicPropertyTest.config.setProperty(prop.getName(), "goodbye");
        Assert.assertTrue((((prop.getChangedTimestamp()) - initialTime) > 8));
    }

    @Test
    public void testDynamicProperySetAdnGets() throws Exception {
        DynamicPropertyTest.config.stopLoading();
        DynamicBooleanProperty prop = new DynamicBooleanProperty("com.netflix.testing.mybool", false);
        Assert.assertFalse(prop.get());
        Assert.assertTrue(prop.prop.getCallbacks().isEmpty());
        for (int i = 0; i < 10; i++) {
            DynamicPropertyTest.config.setProperty("com.netflix.testing.mybool", "true");
            Assert.assertTrue(prop.get());
            Assert.assertTrue(DynamicPropertyTest.config.getString("com.netflix.testing.mybool").equals("true"));
            DynamicPropertyTest.config.setProperty("com.netflix.testing.mybool", "false");
            Assert.assertFalse(prop.get());
            Assert.assertTrue(DynamicPropertyTest.config.getString("com.netflix.testing.mybool").equals("false"));
        }
        for (int i = 0; i < 100; i++) {
            DynamicPropertyTest.config.setProperty("com.netflix.testing.mybool", "true");
            Assert.assertTrue(prop.get());
            Assert.assertTrue(DynamicPropertyTest.config.getString("com.netflix.testing.mybool").equals("true"));
            DynamicPropertyTest.config.clearProperty("com.netflix.testing.mybool");
            Assert.assertFalse(prop.get());
            Assert.assertTrue(((DynamicPropertyTest.config.getString("com.netflix.testing.mybool")) == null));
        }
    }

    @Test
    public void testPropertyCreation() {
        DynamicPropertyTest.config.stopLoading();
        meGotCalled = false;
        final String newValue = "newValue";
        Runnable r = new Runnable() {
            public void run() {
                meGotCalled = true;
            }
        };
        final DynamicStringProperty prop = DynamicPropertyFactory.getInstance().getStringProperty("foo.bar", "xyz", r);
        Assert.assertEquals("xyz", prop.get());
        DynamicPropertyTest.config.setProperty("foo.bar", newValue);
        Assert.assertTrue(meGotCalled);
        Assert.assertEquals(newValue, prop.get());
        Assert.assertTrue(prop.prop.getCallbacks().contains(r));
    }
}

