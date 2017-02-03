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


package org.apache.ibatis.builder.xml.dynamic;


public class AmplExpressionEvaluatorTest {
    private org.apache.ibatis.scripting.xmltags.ExpressionEvaluator evaluator = new org.apache.ibatis.scripting.xmltags.ExpressionEvaluator();

    @org.junit.Test
    public void shouldCompareStringsReturnTrue() {
        boolean value = evaluator.evaluateBoolean("username == 'cbegin'", new org.apache.ibatis.domain.blog.Author(1, "cbegin", "******", "cbegin@apache.org", "N/A", org.apache.ibatis.domain.blog.Section.NEWS));
        org.junit.Assert.assertEquals(true, value);
    }

    @org.junit.Test
    public void shouldCompareStringsReturnFalse() {
        boolean value = evaluator.evaluateBoolean("username == 'norm'", new org.apache.ibatis.domain.blog.Author(1, "cbegin", "******", "cbegin@apache.org", "N/A", org.apache.ibatis.domain.blog.Section.NEWS));
        org.junit.Assert.assertEquals(false, value);
    }

    @org.junit.Test
    public void shouldReturnTrueIfNotNull() {
        boolean value = evaluator.evaluateBoolean("username", new org.apache.ibatis.domain.blog.Author(1, "cbegin", "******", "cbegin@apache.org", "N/A", org.apache.ibatis.domain.blog.Section.NEWS));
        org.junit.Assert.assertEquals(true, value);
    }

    @org.junit.Test
    public void shouldReturnFalseIfNull() {
        boolean value = evaluator.evaluateBoolean("password", new org.apache.ibatis.domain.blog.Author(1, "cbegin", null, "cbegin@apache.org", "N/A", org.apache.ibatis.domain.blog.Section.NEWS));
        org.junit.Assert.assertEquals(false, value);
    }

    @org.junit.Test
    public void shouldReturnTrueIfNotZero() {
        boolean value = evaluator.evaluateBoolean("id", new org.apache.ibatis.domain.blog.Author(1, "cbegin", null, "cbegin@apache.org", "N/A", org.apache.ibatis.domain.blog.Section.NEWS));
        org.junit.Assert.assertEquals(true, value);
    }

    @org.junit.Test
    public void shouldReturnFalseIfZero() {
        boolean value = evaluator.evaluateBoolean("id", new org.apache.ibatis.domain.blog.Author(0, "cbegin", null, "cbegin@apache.org", "N/A", org.apache.ibatis.domain.blog.Section.NEWS));
        org.junit.Assert.assertEquals(false, value);
    }

    @org.junit.Test
    public void shouldIterateOverIterable() {
        final java.util.HashMap<java.lang.String, java.lang.String[]> parameterObject = new java.util.HashMap<java.lang.String, java.lang.String[]>() {
            {
                put("array", new java.lang.String[]{ "1" , "2" , "3" });
            }
        };
        final java.lang.Iterable<?> iterable = evaluator.evaluateIterable("array", parameterObject);
        int i = 0;
        for (java.lang.Object o : iterable) {
            org.junit.Assert.assertEquals(java.lang.String.valueOf((++i)), o);
        }
    }
}

