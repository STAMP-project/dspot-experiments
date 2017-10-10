/**
 * Copyright 2009-2016 the original author or authors.
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


package org.apache.ibatis.mapping;


public class AmplBoundSqlTest {
    @org.junit.Test
    public void testHasAdditionalParameter() throws java.lang.Exception {
        java.util.List<org.apache.ibatis.mapping.ParameterMapping> params = java.util.Collections.emptyList();
        org.apache.ibatis.mapping.BoundSql boundSql = new org.apache.ibatis.mapping.BoundSql(new org.apache.ibatis.session.Configuration(), "some sql", params, new java.lang.Object());
        java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<java.lang.String, java.lang.String>();
        map.put("key1", "value1");
        boundSql.setAdditionalParameter("map", map);
        org.apache.ibatis.mapping.AmplBoundSqlTest.Person bean = new org.apache.ibatis.mapping.AmplBoundSqlTest.Person();
        bean.id = 1;
        boundSql.setAdditionalParameter("person", bean);
        java.lang.String[] array = new java.lang.String[]{ "User1" , "User2" };
        boundSql.setAdditionalParameter("array", array);
        org.junit.Assert.assertFalse(boundSql.hasAdditionalParameter("pet"));
        org.junit.Assert.assertFalse(boundSql.hasAdditionalParameter("pet.name"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("map"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("map.key1"));
        org.junit.Assert.assertTrue("should return true even if the child property does not exists.", boundSql.hasAdditionalParameter("map.key2"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("person"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("person.id"));
        org.junit.Assert.assertTrue("should return true even if the child property does not exists.", boundSql.hasAdditionalParameter("person.name"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("array[0]"));
        org.junit.Assert.assertTrue("should return true even if the element does not exists.", boundSql.hasAdditionalParameter("array[99]"));
    }

    public static class Person {
        public java.lang.Integer id;
    }

    /* amplification of org.apache.ibatis.mapping.BoundSqlTest#testHasAdditionalParameter */
    @org.junit.Test(timeout = 1000)
    public void testHasAdditionalParameter_add1() throws java.lang.Exception {
        java.util.List<org.apache.ibatis.mapping.ParameterMapping> params = java.util.Collections.emptyList();
        org.apache.ibatis.mapping.BoundSql boundSql = new org.apache.ibatis.mapping.BoundSql(new org.apache.ibatis.session.Configuration(), "some sql", params, new java.lang.Object());
        java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<java.lang.String, java.lang.String>();
        // AssertGenerator replace invocation
        java.lang.String o_testHasAdditionalParameter_add1__9 = // MethodCallAdder
map.put("key1", "value1");
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testHasAdditionalParameter_add1__9);
        map.put("key1", "value1");
        boundSql.setAdditionalParameter("map", map);
        org.apache.ibatis.mapping.AmplBoundSqlTest.Person bean = new org.apache.ibatis.mapping.AmplBoundSqlTest.Person();
        bean.id = 1;
        boundSql.setAdditionalParameter("person", bean);
        java.lang.String[] array = new java.lang.String[]{ "User1" , "User2" };
        boundSql.setAdditionalParameter("array", array);
        org.junit.Assert.assertFalse(boundSql.hasAdditionalParameter("pet"));
        org.junit.Assert.assertFalse(boundSql.hasAdditionalParameter("pet.name"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("map"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("map.key1"));
        org.junit.Assert.assertTrue("should return true even if the child property does not exists.", boundSql.hasAdditionalParameter("map.key2"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("person"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("person.id"));
        org.junit.Assert.assertTrue("should return true even if the child property does not exists.", boundSql.hasAdditionalParameter("person.name"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("array[0]"));
        org.junit.Assert.assertTrue("should return true even if the element does not exists.", boundSql.hasAdditionalParameter("array[99]"));
    }

    /* amplification of org.apache.ibatis.mapping.BoundSqlTest#testHasAdditionalParameter */
    @org.junit.Test(timeout = 1000)
    public void testHasAdditionalParameter_cf51() throws java.lang.Exception {
        java.util.List<org.apache.ibatis.mapping.ParameterMapping> params = java.util.Collections.emptyList();
        org.apache.ibatis.mapping.BoundSql boundSql = new org.apache.ibatis.mapping.BoundSql(new org.apache.ibatis.session.Configuration(), "some sql", params, new java.lang.Object());
        java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<java.lang.String, java.lang.String>();
        map.put("key1", "value1");
        boundSql.setAdditionalParameter("map", map);
        org.apache.ibatis.mapping.AmplBoundSqlTest.Person bean = new org.apache.ibatis.mapping.AmplBoundSqlTest.Person();
        bean.id = 1;
        boundSql.setAdditionalParameter("person", bean);
        java.lang.String[] array = new java.lang.String[]{ "User1" , "User2" };
        boundSql.setAdditionalParameter("array", array);
        org.junit.Assert.assertFalse(boundSql.hasAdditionalParameter("pet"));
        org.junit.Assert.assertFalse(boundSql.hasAdditionalParameter("pet.name"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("map"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("map.key1"));
        org.junit.Assert.assertTrue("should return true even if the child property does not exists.", boundSql.hasAdditionalParameter("map.key2"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("person"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("person.id"));
        org.junit.Assert.assertTrue("should return true even if the child property does not exists.", boundSql.hasAdditionalParameter("person.name"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("array[0]"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1 = "map.key2";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "map.key2");
        // AssertGenerator replace invocation
        java.lang.Object o_testHasAdditionalParameter_cf51__37 = // StatementAdderMethod cloned existing statement
boundSql.getAdditionalParameter(String_vc_1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testHasAdditionalParameter_cf51__37);
        org.junit.Assert.assertTrue("should return true even if the element does not exists.", boundSql.hasAdditionalParameter("array[99]"));
    }

    /* amplification of org.apache.ibatis.mapping.BoundSqlTest#testHasAdditionalParameter */
    @org.junit.Test(timeout = 1000)
    public void testHasAdditionalParameter_cf48_failAssert5() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.List<org.apache.ibatis.mapping.ParameterMapping> params = java.util.Collections.emptyList();
            org.apache.ibatis.mapping.BoundSql boundSql = new org.apache.ibatis.mapping.BoundSql(new org.apache.ibatis.session.Configuration(), "some sql", params, new java.lang.Object());
            java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<java.lang.String, java.lang.String>();
            map.put("key1", "value1");
            boundSql.setAdditionalParameter("map", map);
            org.apache.ibatis.mapping.AmplBoundSqlTest.Person bean = new org.apache.ibatis.mapping.AmplBoundSqlTest.Person();
            bean.id = 1;
            boundSql.setAdditionalParameter("person", bean);
            java.lang.String[] array = new java.lang.String[]{ "User1" , "User2" };
            boundSql.setAdditionalParameter("array", array);
            // MethodAssertGenerator build local variable
            Object o_17_0 = boundSql.hasAdditionalParameter("pet");
            // MethodAssertGenerator build local variable
            Object o_19_0 = boundSql.hasAdditionalParameter("pet.name");
            // MethodAssertGenerator build local variable
            Object o_21_0 = boundSql.hasAdditionalParameter("map");
            // MethodAssertGenerator build local variable
            Object o_23_0 = boundSql.hasAdditionalParameter("map.key1");
            // MethodAssertGenerator build local variable
            Object o_25_0 = boundSql.hasAdditionalParameter("map.key2");
            // MethodAssertGenerator build local variable
            Object o_27_0 = boundSql.hasAdditionalParameter("person");
            // MethodAssertGenerator build local variable
            Object o_29_0 = boundSql.hasAdditionalParameter("person.id");
            // MethodAssertGenerator build local variable
            Object o_31_0 = boundSql.hasAdditionalParameter("person.name");
            // MethodAssertGenerator build local variable
            Object o_33_0 = boundSql.hasAdditionalParameter("array[0]");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_1 = "map.key2";
            // StatementAdderOnAssert create null value
            org.apache.ibatis.mapping.BoundSql vc_4 = (org.apache.ibatis.mapping.BoundSql)null;
            // StatementAdderMethod cloned existing statement
            vc_4.getAdditionalParameter(String_vc_1);
            // MethodAssertGenerator build local variable
            Object o_41_0 = boundSql.hasAdditionalParameter("array[99]");
            org.junit.Assert.fail("testHasAdditionalParameter_cf48 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.mapping.BoundSqlTest#testHasAdditionalParameter */
    @org.junit.Test(timeout = 1000)
    public void testHasAdditionalParameter_cf42_literalMutation280() throws java.lang.Exception {
        java.util.List<org.apache.ibatis.mapping.ParameterMapping> params = java.util.Collections.emptyList();
        org.apache.ibatis.mapping.BoundSql boundSql = new org.apache.ibatis.mapping.BoundSql(new org.apache.ibatis.session.Configuration(), "some sql", params, new java.lang.Object());
        java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<java.lang.String, java.lang.String>();
        map.put("key1", "value1");
        boundSql.setAdditionalParameter("map", map);
        org.apache.ibatis.mapping.AmplBoundSqlTest.Person bean = new org.apache.ibatis.mapping.AmplBoundSqlTest.Person();
        bean.id = 1;
        boundSql.setAdditionalParameter("person", bean);
        java.lang.String[] array = new java.lang.String[]{ "UsTr2" , "User2" };
        boundSql.setAdditionalParameter("array", array);
        org.junit.Assert.assertFalse(boundSql.hasAdditionalParameter("pet"));
        org.junit.Assert.assertFalse(boundSql.hasAdditionalParameter("pet.name"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("map"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("map.key1"));
        org.junit.Assert.assertTrue("should return true even if the child property does not exists.", boundSql.hasAdditionalParameter("map.key2"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("person"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("person.id"));
        org.junit.Assert.assertTrue("should return true even if the child property does not exists.", boundSql.hasAdditionalParameter("person.name"));
        org.junit.Assert.assertTrue(boundSql.hasAdditionalParameter("array[0]"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_0 = "person";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_0, "person");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_0, "person");
        // AssertGenerator replace invocation
        boolean o_testHasAdditionalParameter_cf42__37 = // StatementAdderMethod cloned existing statement
boundSql.hasAdditionalParameter(String_vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testHasAdditionalParameter_cf42__37);
        org.junit.Assert.assertTrue("should return true even if the element does not exists.", boundSql.hasAdditionalParameter("array[99]"));
    }

    /* amplification of org.apache.ibatis.mapping.BoundSqlTest#testHasAdditionalParameter */
    @org.junit.Test(timeout = 1000)
    public void testHasAdditionalParameter_cf43_cf466_failAssert26() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.List<org.apache.ibatis.mapping.ParameterMapping> params = java.util.Collections.emptyList();
            org.apache.ibatis.mapping.BoundSql boundSql = new org.apache.ibatis.mapping.BoundSql(new org.apache.ibatis.session.Configuration(), "some sql", params, new java.lang.Object());
            java.util.Map<java.lang.String, java.lang.String> map = new java.util.HashMap<java.lang.String, java.lang.String>();
            map.put("key1", "value1");
            boundSql.setAdditionalParameter("map", map);
            org.apache.ibatis.mapping.AmplBoundSqlTest.Person bean = new org.apache.ibatis.mapping.AmplBoundSqlTest.Person();
            bean.id = 1;
            boundSql.setAdditionalParameter("person", bean);
            java.lang.String[] array = new java.lang.String[]{ "User1" , "User2" };
            boundSql.setAdditionalParameter("array", array);
            // MethodAssertGenerator build local variable
            Object o_17_0 = boundSql.hasAdditionalParameter("pet");
            // MethodAssertGenerator build local variable
            Object o_19_0 = boundSql.hasAdditionalParameter("pet.name");
            // MethodAssertGenerator build local variable
            Object o_21_0 = boundSql.hasAdditionalParameter("map");
            // MethodAssertGenerator build local variable
            Object o_23_0 = boundSql.hasAdditionalParameter("map.key1");
            // MethodAssertGenerator build local variable
            Object o_25_0 = boundSql.hasAdditionalParameter("map.key2");
            // MethodAssertGenerator build local variable
            Object o_27_0 = boundSql.hasAdditionalParameter("person");
            // MethodAssertGenerator build local variable
            Object o_29_0 = boundSql.hasAdditionalParameter("person.id");
            // MethodAssertGenerator build local variable
            Object o_31_0 = boundSql.hasAdditionalParameter("person.name");
            // MethodAssertGenerator build local variable
            Object o_33_0 = boundSql.hasAdditionalParameter("array[0]");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_3 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_3, "");
            // AssertGenerator replace invocation
            boolean o_testHasAdditionalParameter_cf43__37 = // StatementAdderMethod cloned existing statement
boundSql.hasAdditionalParameter(vc_3);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_testHasAdditionalParameter_cf43__37);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_79 = new java.lang.Object();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_11 = "array[0]";
            // StatementAdderMethod cloned existing statement
            boundSql.setAdditionalParameter(String_vc_11, vc_79);
            // MethodAssertGenerator build local variable
            Object o_49_0 = boundSql.hasAdditionalParameter("array[99]");
            org.junit.Assert.fail("testHasAdditionalParameter_cf43_cf466 should have thrown ArrayStoreException");
        } catch (java.lang.ArrayStoreException eee) {
        }
    }
}

