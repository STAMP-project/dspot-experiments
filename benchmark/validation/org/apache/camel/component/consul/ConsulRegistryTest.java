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
package org.apache.camel.component.consul;


import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.camel.NoSuchBeanException;
import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;


/**
 * Unit test for Camel Registry implementation for Consul
 */
public class ConsulRegistryTest implements Serializable {
    private static final long serialVersionUID = -3482971969351609265L;

    private static ConsulRegistry registry;

    private static GenericContainer container;

    public class ConsulTestClass implements Serializable {
        private static final long serialVersionUID = -4815945688487114891L;

        public String hello(String name) {
            return "Hello " + name;
        }
    }

    @Test
    public void storeString() {
        ConsulRegistryTest.registry.put("stringTestKey", "stringValue");
        String result = ((String) (ConsulRegistryTest.registry.lookupByName("stringTestKey")));
        ConsulRegistryTest.registry.remove("stringTestKey");
        Assert.assertNotNull(result);
        Assert.assertEquals("stringValue", result);
    }

    @Test
    public void overrideExistingKey() {
        ConsulRegistryTest.registry.put("uniqueKey", "stringValueOne");
        ConsulRegistryTest.registry.put("uniqueKey", "stringValueTwo");
        String result = ((String) (ConsulRegistryTest.registry.lookupByName("uniqueKey")));
        ConsulRegistryTest.registry.remove("uniqueKey");
        Assert.assertNotNull(result);
        Assert.assertEquals("stringValueTwo", result);
    }

    @Test
    public void checkLookupByName() {
        ConsulRegistryTest.registry.put("namedKey", "namedValue");
        String result = ((String) (ConsulRegistryTest.registry.lookupByName("namedKey")));
        ConsulRegistryTest.registry.remove("namedKey");
        Assert.assertNotNull(result);
        Assert.assertEquals("namedValue", result);
    }

    @Test
    public void checkFailedLookupByName() {
        ConsulRegistryTest.registry.put("namedKey", "namedValue");
        ConsulRegistryTest.registry.remove("namedKey");
        String result = ((String) (ConsulRegistryTest.registry.lookupByName("namedKey")));
        Assert.assertNull(result);
    }

    @Test
    public void checkLookupByNameAndType() {
        ConsulRegistryTest.ConsulTestClass consulTestClass = new ConsulRegistryTest.ConsulTestClass();
        ConsulRegistryTest.registry.put("testClass", consulTestClass);
        ConsulRegistryTest.ConsulTestClass consulTestClassClone = ConsulRegistryTest.registry.lookupByNameAndType("testClass", consulTestClass.getClass());
        ConsulRegistryTest.registry.remove("testClass");
        Assert.assertNotNull(consulTestClassClone);
        Assert.assertEquals(consulTestClass.getClass(), consulTestClassClone.getClass());
    }

    @Test
    public void checkFailedLookupByNameAndType() {
        ConsulRegistryTest.ConsulTestClass consulTestClass = new ConsulRegistryTest.ConsulTestClass();
        ConsulRegistryTest.registry.put("testClass", consulTestClass);
        ConsulRegistryTest.registry.remove("testClass");
        ConsulRegistryTest.ConsulTestClass consulTestClassClone = ConsulRegistryTest.registry.lookupByNameAndType("testClass", consulTestClass.getClass());
        Assert.assertNull(consulTestClassClone);
    }

    @Test
    public void checkFindByTypeWithName() {
        ConsulRegistryTest.ConsulTestClass consulTestClassOne = new ConsulRegistryTest.ConsulTestClass();
        ConsulRegistryTest.ConsulTestClass consulTestClassTwo = new ConsulRegistryTest.ConsulTestClass();
        ConsulRegistryTest.registry.put("testClassOne", consulTestClassOne);
        ConsulRegistryTest.registry.put("testClassTwo", consulTestClassTwo);
        Map<String, ? extends ConsulRegistryTest.ConsulTestClass> consulTestClassMap = ConsulRegistryTest.registry.findByTypeWithName(consulTestClassOne.getClass());
        ConsulRegistryTest.registry.remove("testClassOne");
        ConsulRegistryTest.registry.remove("testClassTwo");
        HashMap<String, ConsulRegistryTest.ConsulTestClass> emptyHashMap = new HashMap<>();
        Assert.assertNotNull(consulTestClassMap);
        Assert.assertEquals(consulTestClassMap.getClass(), emptyHashMap.getClass());
        Assert.assertEquals(2, consulTestClassMap.size());
    }

    @Test
    public void storeObject() {
        ConsulRegistryTest.ConsulTestClass testObject = new ConsulRegistryTest.ConsulTestClass();
        ConsulRegistryTest.registry.put("objectTestClass", testObject);
        ConsulRegistryTest.ConsulTestClass clone = ((ConsulRegistryTest.ConsulTestClass) (ConsulRegistryTest.registry.lookupByName("objectTestClass")));
        Assert.assertEquals(clone.hello("World"), "Hello World");
        ConsulRegistryTest.registry.remove("objectTestClass");
    }

    @Test
    public void findByType() {
        ConsulRegistryTest.ConsulTestClass classOne = new ConsulRegistryTest.ConsulTestClass();
        ConsulRegistryTest.registry.put("classOne", classOne);
        ConsulRegistryTest.ConsulTestClass classTwo = new ConsulRegistryTest.ConsulTestClass();
        ConsulRegistryTest.registry.put("classTwo", classTwo);
        Set<? extends ConsulRegistryTest.ConsulTestClass> results = ConsulRegistryTest.registry.findByType(classOne.getClass());
        Assert.assertNotNull(results);
        HashSet<ConsulRegistryTest.ConsulTestClass> hashSet = new HashSet<>();
        ConsulRegistryTest.registry.remove("classOne");
        ConsulRegistryTest.registry.remove("classTwo");
        Assert.assertEquals(results.getClass(), hashSet.getClass());
        Assert.assertEquals(2, results.size());
    }

    @Test(expected = NoSuchBeanException.class)
    public void deleteNonExisting() {
        ConsulRegistryTest.registry.remove("nonExisting");
    }
}

