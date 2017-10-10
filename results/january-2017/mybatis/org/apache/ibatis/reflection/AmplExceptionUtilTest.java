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


package org.apache.ibatis.reflection;


public class AmplExceptionUtilTest {
    @org.junit.Test
    public void shouldUnwrapThrowable() {
        java.lang.Exception exception = new java.lang.Exception();
        org.junit.Assert.assertEquals(exception, org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable(exception));
        org.junit.Assert.assertEquals(exception, org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable(new java.lang.reflect.InvocationTargetException(exception, "test")));
        org.junit.Assert.assertEquals(exception, org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable(new java.lang.reflect.UndeclaredThrowableException(exception, "test")));
        org.junit.Assert.assertEquals(exception, org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable(new java.lang.reflect.InvocationTargetException(new java.lang.reflect.InvocationTargetException(exception, "test"), "test")));
        org.junit.Assert.assertEquals(exception, org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable(new java.lang.reflect.InvocationTargetException(new java.lang.reflect.UndeclaredThrowableException(exception, "test"), "test")));
    }
}

