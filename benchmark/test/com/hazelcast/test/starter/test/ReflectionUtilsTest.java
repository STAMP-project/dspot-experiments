/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.test.starter.test;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.starter.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReflectionUtilsTest {
    private ReflectionUtilsTest.ReflectionTestClass testClass = new ReflectionUtilsTest.ReflectionTestClass();

    @Test
    public void testGetClass() {
        Assert.assertEquals(ReflectionUtilsTest.ReflectionTestClass.class, ReflectionUtils.getClass(testClass));
    }

    @Test
    public void testIsInstanceOf() {
        Assert.assertTrue("Expected testClass to be instanceOf ReflectionTestClass", ReflectionUtils.isInstanceOf(testClass, ReflectionUtilsTest.ReflectionTestClass.class));
    }

    @Test
    public void testGetFieldValueReflectively() throws Exception {
        testClass.testString = "foobar";
        Assert.assertEquals("foobar", ReflectionUtils.getFieldValueReflectively(testClass, "testString"));
    }

    @Test
    public void testGetFieldValueReflectively_whenNull() throws Exception {
        Assert.assertNull(ReflectionUtils.getFieldValueReflectively(testClass, "testString"));
    }

    @Test(expected = NoSuchFieldError.class)
    public void testGetFieldValueReflectively_whenInvalidField() throws Exception {
        Assert.assertNull(ReflectionUtils.getFieldValueReflectively(testClass, "invalidField"));
    }

    @Test
    public void testSetFieldValueReflectively() throws Exception {
        ReflectionUtils.setFieldValueReflectively(testClass, "testInteger", 23);
        Assert.assertEquals(23, testClass.testInteger);
    }

    @Test(expected = NoSuchFieldError.class)
    public void testSetFieldValueReflectively_whenInvalidField() throws Exception {
        ReflectionUtils.setFieldValueReflectively(testClass, "invalidField", 23);
    }

    @Test
    public void testGetAllFieldsByName() {
        Map<String, Field> fields = ReflectionUtils.getAllFieldsByName(ReflectionUtilsTest.ReflectionTestClass.class);
        Assert.assertTrue((((("Expected at least 2 fields, but was " + (fields.size())) + " (") + fields) + ")"), ((fields.size()) >= 2));
        Assert.assertTrue((("Expected to find field 'testString' (" + fields) + ")"), fields.containsKey("testString"));
        Assert.assertTrue((("Expected to find field 'testInteger' (" + fields) + ")"), fields.containsKey("testInteger"));
        Field stringField = fields.get("testString");
        Assert.assertEquals("Expected field 'testString' to be of type String", String.class, stringField.getType());
        Assert.assertFalse("Expected field 'testString' to be not accessible", stringField.isAccessible());
        Field integerField = fields.get("testInteger");
        Assert.assertEquals("Expected field 'testInteger' to be of type int", int.class, integerField.getType());
        Assert.assertFalse("Expected field 'testInteger' to be not accessible", integerField.isAccessible());
    }

    @SuppressWarnings("unused")
    private class ReflectionTestClass {
        private String testString;

        private int testInteger;
    }
}

