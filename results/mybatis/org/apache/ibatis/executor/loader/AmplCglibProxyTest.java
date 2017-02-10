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


package org.apache.ibatis.executor.loader;


public class AmplCglibProxyTest extends org.apache.ibatis.executor.loader.SerializableProxyTest {
    public AmplCglibProxyTest() {
        proxyFactory = new org.apache.ibatis.executor.loader.CglibProxyFactory();
    }

    @org.junit.Test
    public void shouldCreateAProxyForAPartiallyLoadedBean() throws java.lang.Exception {
        org.apache.ibatis.executor.loader.ResultLoaderMap loader = new org.apache.ibatis.executor.loader.ResultLoaderMap();
        loader.addLoader("id", null, null);
        java.lang.Object proxy = proxyFactory.createProxy(author, loader, new org.apache.ibatis.session.Configuration(), new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>());
        org.apache.ibatis.domain.blog.Author author2 = ((org.apache.ibatis.domain.blog.Author) (deserialize(serialize(((java.io.Serializable) (proxy))))));
        org.junit.Assert.assertTrue((author2 instanceof net.sf.cglib.proxy.Factory));
    }

    @org.junit.Test
    public void shouldLetCallALoadedProperty() throws java.lang.Exception {
        org.apache.ibatis.domain.blog.Author author2 = ((org.apache.ibatis.domain.blog.Author) (((org.apache.ibatis.executor.loader.CglibProxyFactory) (proxyFactory)).createDeserializationProxy(author, new java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair>(), new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>())));
        org.junit.Assert.assertEquals(999, author2.getId());
    }

    @org.junit.Test
    public void shouldSerizalizeADeserlizaliedProxy() throws java.lang.Exception {
        java.lang.Object proxy = ((org.apache.ibatis.executor.loader.CglibProxyFactory) (proxyFactory)).createDeserializationProxy(author, new java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair>(), new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>());
        org.apache.ibatis.domain.blog.Author author2 = ((org.apache.ibatis.domain.blog.Author) (deserialize(serialize(((java.io.Serializable) (proxy))))));
        org.junit.Assert.assertEquals(author, author2);
        org.junit.Assert.assertFalse(author.getClass().equals(author2.getClass()));
    }

    @org.junit.Test
    public void shouldSerizaliceAFullLoadedObjectToOriginalClass() throws java.lang.Exception {
        java.lang.Object proxy = proxyFactory.createProxy(author, new org.apache.ibatis.executor.loader.ResultLoaderMap(), new org.apache.ibatis.session.Configuration(), new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>());
        java.lang.Object proxy2 = deserialize(serialize(((java.io.Serializable) (proxy))));
        org.junit.Assert.assertEquals(author.getClass(), proxy2.getClass());
    }

    @org.junit.Test(expected = org.apache.ibatis.executor.ExecutorException.class)
    public void shouldFailCallingAnUnloadedProperty() throws java.lang.Exception {
        // yes, it must go in uppercase
        java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair> unloadedProperties = new java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair>();
        // AssertGenerator replace invocation
        org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair o_shouldFailCallingAnUnloadedProperty__4 = unloadedProperties.put("ID", null);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_shouldFailCallingAnUnloadedProperty__4);
        org.apache.ibatis.domain.blog.Author author2 = ((org.apache.ibatis.domain.blog.Author) (((org.apache.ibatis.executor.loader.CglibProxyFactory) (proxyFactory)).createDeserializationProxy(author, unloadedProperties, new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>())));
        author2.getId();
    }

    /* amplification of org.apache.ibatis.executor.loader.CglibProxyTest#shouldFailCallingAnUnloadedProperty */
    @org.junit.Test(timeout = 1000)
    public void shouldFailCallingAnUnloadedProperty_add2_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // yes, it must go in uppercase
            java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair> unloadedProperties = new java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair>();
            // MethodCallAdder
            unloadedProperties.put("ID", null);
            unloadedProperties.put("ID", null);
            org.apache.ibatis.domain.blog.Author author2 = ((org.apache.ibatis.domain.blog.Author) (((org.apache.ibatis.executor.loader.CglibProxyFactory) (proxyFactory)).createDeserializationProxy(author, unloadedProperties, new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>())));
            author2.getId();
            org.junit.Assert.fail("shouldFailCallingAnUnloadedProperty_add2 should have thrown ExecutorException");
        } catch (org.apache.ibatis.executor.ExecutorException eee) {
        }
    }

    /* amplification of org.apache.ibatis.executor.loader.CglibProxyTest#shouldFailCallingAnUnloadedProperty */
    @org.junit.Test(timeout = 1000)
    public void shouldFailCallingAnUnloadedProperty_add3_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // yes, it must go in uppercase
            java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair> unloadedProperties = new java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair>();
            unloadedProperties.put("ID", null);
            org.apache.ibatis.domain.blog.Author author2 = ((org.apache.ibatis.domain.blog.Author) (((org.apache.ibatis.executor.loader.CglibProxyFactory) (proxyFactory)).createDeserializationProxy(author, unloadedProperties, new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>())));
            // MethodCallAdder
            author2.getId();
            author2.getId();
            org.junit.Assert.fail("shouldFailCallingAnUnloadedProperty_add3 should have thrown ExecutorException");
        } catch (org.apache.ibatis.executor.ExecutorException eee) {
        }
    }

    /* amplification of org.apache.ibatis.executor.loader.CglibProxyTest#shouldFailCallingAnUnloadedProperty */
    @org.junit.Test(timeout = 1000)
    public void shouldFailCallingAnUnloadedProperty_add2_failAssert0_add5() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // yes, it must go in uppercase
            java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair> unloadedProperties = new java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair>();
            // AssertGenerator replace invocation
            org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair o_shouldFailCallingAnUnloadedProperty_add2_failAssert0_add5__6 = // MethodCallAdder
unloadedProperties.put("ID", null);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_shouldFailCallingAnUnloadedProperty_add2_failAssert0_add5__6);
            // AssertGenerator replace invocation
            org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair o_shouldFailCallingAnUnloadedProperty_add2_failAssert0_add5__8 = // MethodCallAdder
unloadedProperties.put("ID", null);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_shouldFailCallingAnUnloadedProperty_add2_failAssert0_add5__8);
            unloadedProperties.put("ID", null);
            org.apache.ibatis.domain.blog.Author author2 = ((org.apache.ibatis.domain.blog.Author) (((org.apache.ibatis.executor.loader.CglibProxyFactory) (proxyFactory)).createDeserializationProxy(author, unloadedProperties, new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>())));
            author2.getId();
            org.junit.Assert.fail("shouldFailCallingAnUnloadedProperty_add2 should have thrown ExecutorException");
        } catch (org.apache.ibatis.executor.ExecutorException eee) {
        }
    }

    /* amplification of org.apache.ibatis.executor.loader.CglibProxyTest#shouldFailCallingAnUnloadedProperty */
    @org.junit.Test(timeout = 1000)
    public void shouldFailCallingAnUnloadedProperty_add2_failAssert0_add6_add14() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // yes, it must go in uppercase
            java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair> unloadedProperties = new java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair>();
            // AssertGenerator replace invocation
            org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair o_shouldFailCallingAnUnloadedProperty_add2_failAssert0_add6__6 = // MethodCallAdder
unloadedProperties.put("ID", null);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_shouldFailCallingAnUnloadedProperty_add2_failAssert0_add6__6);
            // AssertGenerator replace invocation
            org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair o_shouldFailCallingAnUnloadedProperty_add2_failAssert0_add6_add14__10 = // MethodCallAdder
unloadedProperties.put("ID", null);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_shouldFailCallingAnUnloadedProperty_add2_failAssert0_add6_add14__10);
            unloadedProperties.put("ID", null);
            org.apache.ibatis.domain.blog.Author author2 = ((org.apache.ibatis.domain.blog.Author) (((org.apache.ibatis.executor.loader.CglibProxyFactory) (proxyFactory)).createDeserializationProxy(author, unloadedProperties, new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>())));
            // MethodCallAdder
            author2.getId();
            author2.getId();
            org.junit.Assert.fail("shouldFailCallingAnUnloadedProperty_add2 should have thrown ExecutorException");
        } catch (org.apache.ibatis.executor.ExecutorException eee) {
        }
    }

    @org.junit.Test
    public void shouldGenerateWriteReplace() throws java.lang.Exception {
        try {
            author.getClass().getDeclaredMethod("writeReplace");
            org.junit.Assert.fail("Author should not have a writeReplace method");
        } catch (java.lang.NoSuchMethodException e) {
            // ok
        }
        java.lang.Object proxy = proxyFactory.createProxy(author, new org.apache.ibatis.executor.loader.ResultLoaderMap(), new org.apache.ibatis.session.Configuration(), new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>());
        java.lang.reflect.Method m = proxy.getClass().getDeclaredMethod("writeReplace");
    }

    /* amplification of org.apache.ibatis.executor.loader.CglibProxyTest#shouldLetCallALoadedProperty */
    @org.junit.Test
    public void shouldLetCallALoadedProperty_literalMutation21_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.domain.blog.Author author2 = ((org.apache.ibatis.domain.blog.Author) (((org.apache.ibatis.executor.loader.CglibProxyFactory) (proxyFactory)).createDeserializationProxy(author, new java.util.HashMap<java.lang.String, org.apache.ibatis.executor.loader.ResultLoaderMap.LoadPair>(), new org.apache.ibatis.reflection.factory.DefaultObjectFactory(), new java.util.ArrayList<java.lang.Class<?>>(), new java.util.ArrayList<java.lang.Object>())));
            // MethodAssertGenerator build local variable
            Object o_7_0 = author2.getId();
            org.junit.Assert.fail("shouldLetCallALoadedProperty_literalMutation21 should have thrown Throwable");
        } catch (java.lang.Throwable eee) {
        }
    }
}

