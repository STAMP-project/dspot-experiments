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


package org.apache.ibatis.io;


public class AmplClassLoaderWrapperTest extends org.apache.ibatis.BaseDataTest {
    org.apache.ibatis.io.ClassLoaderWrapper wrapper;

    java.lang.ClassLoader loader;

    private final java.lang.String RESOURCE_NOT_FOUND = "some_resource_that_does_not_exist.properties";

    private final java.lang.String CLASS_NOT_FOUND = "some.random.class.that.does.not.Exist";

    private final java.lang.String CLASS_FOUND = "java.lang.Object";

    @org.junit.Before
    public void beforeClassLoaderWrapperTest() {
        wrapper = new org.apache.ibatis.io.ClassLoaderWrapper();
        loader = getClass().getClassLoader();
    }

    @org.junit.Test
    public void classForName() throws java.lang.ClassNotFoundException {
        org.junit.Assert.assertNotNull(wrapper.classForName(CLASS_FOUND));
    }

    @org.junit.Test(expected = java.lang.ClassNotFoundException.class)
    public void classForNameNotFound() throws java.lang.ClassNotFoundException {
        org.junit.Assert.assertNotNull(wrapper.classForName(CLASS_NOT_FOUND));
    }

    @org.junit.Test
    public void classForNameWithClassLoader() throws java.lang.ClassNotFoundException {
        org.junit.Assert.assertNotNull(wrapper.classForName(CLASS_FOUND, loader));
    }

    @org.junit.Test
    public void getResourceAsURL() {
        org.junit.Assert.assertNotNull(wrapper.getResourceAsURL(org.apache.ibatis.BaseDataTest.JPETSTORE_PROPERTIES));
    }

    @org.junit.Test
    public void getResourceAsURLNotFound() {
        org.junit.Assert.assertNull(wrapper.getResourceAsURL(RESOURCE_NOT_FOUND));
    }

    @org.junit.Test
    public void getResourceAsURLWithClassLoader() {
        org.junit.Assert.assertNotNull(wrapper.getResourceAsURL(org.apache.ibatis.BaseDataTest.JPETSTORE_PROPERTIES, loader));
    }

    @org.junit.Test
    public void getResourceAsStream() {
        org.junit.Assert.assertNotNull(wrapper.getResourceAsStream(org.apache.ibatis.BaseDataTest.JPETSTORE_PROPERTIES));
    }

    @org.junit.Test
    public void getResourceAsStreamNotFound() {
        org.junit.Assert.assertNull(wrapper.getResourceAsStream(RESOURCE_NOT_FOUND));
    }

    @org.junit.Test
    public void getResourceAsStreamWithClassLoader() {
        org.junit.Assert.assertNotNull(wrapper.getResourceAsStream(org.apache.ibatis.BaseDataTest.JPETSTORE_PROPERTIES, loader));
    }
}

