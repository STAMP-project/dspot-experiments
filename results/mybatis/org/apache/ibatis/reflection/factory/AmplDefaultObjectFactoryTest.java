/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.reflection.factory;


/**
 * DefaultObjectFactoryTest
 *
 * @author Ryan Lamore
 */
public class AmplDefaultObjectFactoryTest {
    @org.junit.Test
    public void instantiateClass() throws java.lang.Exception {
        org.apache.ibatis.reflection.factory.DefaultObjectFactory defaultObjectFactory = new org.apache.ibatis.reflection.factory.DefaultObjectFactory();
        org.apache.ibatis.reflection.factory.TestClass testClass = defaultObjectFactory.instantiateClass(org.apache.ibatis.reflection.factory.TestClass.class, java.util.Arrays.<java.lang.Class<?>>asList(java.lang.String.class, java.lang.Integer.class), java.util.Arrays.<java.lang.Object>asList("foo", 0));
        org.junit.Assert.assertEquals("myInteger didn't match expected", ((java.lang.Integer) (0)), testClass.myInteger);
        org.junit.Assert.assertEquals("myString didn't match expected", "foo", testClass.myString);
    }

    @org.junit.Test
    public void instantiateClassThrowsProperErrorMsg() {
        org.apache.ibatis.reflection.factory.DefaultObjectFactory defaultObjectFactory = new org.apache.ibatis.reflection.factory.DefaultObjectFactory();
        try {
            defaultObjectFactory.instantiateClass(org.apache.ibatis.reflection.factory.TestClass.class, java.util.Collections.<java.lang.Class<?>>singletonList(java.lang.String.class), java.util.Collections.<java.lang.Object>singletonList("foo"));
            org.junit.Assert.fail("Should have thrown ReflectionException");
        } catch (java.lang.Exception e) {
            org.junit.Assert.assertTrue("Should be ReflectionException", (e instanceof org.apache.ibatis.reflection.ReflectionException));
            org.junit.Assert.assertTrue("Should not have trailing commas in types list", e.getMessage().contains("(String)"));
            org.junit.Assert.assertTrue("Should not have trailing commas in values list", e.getMessage().contains("(foo)"));
        }
    }
}

