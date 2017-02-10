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


package org.apache.ibatis.exceptions;


public class AmplGeneralExceptionsTest {
    private static final java.lang.String EXPECTED_MESSAGE = "Test Message";

    private static final java.lang.Exception EXPECTED_CAUSE = new java.lang.Exception("Nested Exception");

    @org.junit.Test
    public void should() {
        java.lang.RuntimeException thrown = org.apache.ibatis.exceptions.ExceptionFactory.wrapException(org.apache.ibatis.exceptions.AmplGeneralExceptionsTest.EXPECTED_MESSAGE, org.apache.ibatis.exceptions.AmplGeneralExceptionsTest.EXPECTED_CAUSE);
        org.junit.Assert.assertTrue("Exception should be wrapped in RuntimeSqlException.", (thrown instanceof org.apache.ibatis.exceptions.PersistenceException));
        testThrowException(thrown);
    }

    @org.junit.Test
    public void shouldInstantiateAndThrowAllCustomExceptions() throws java.lang.Exception {
        java.lang.Class<?>[] exceptionTypes = new java.lang.Class<?>[]{ org.apache.ibatis.binding.BindingException.class , org.apache.ibatis.cache.CacheException.class , org.apache.ibatis.datasource.DataSourceException.class , org.apache.ibatis.executor.ExecutorException.class , org.apache.ibatis.logging.LogException.class , org.apache.ibatis.parsing.ParsingException.class , org.apache.ibatis.builder.BuilderException.class , org.apache.ibatis.plugin.PluginException.class , org.apache.ibatis.reflection.ReflectionException.class , org.apache.ibatis.exceptions.PersistenceException.class , org.apache.ibatis.session.SqlSessionException.class , org.apache.ibatis.transaction.TransactionException.class , org.apache.ibatis.type.TypeException.class , org.apache.ibatis.scripting.ScriptingException.class };
        for (java.lang.Class<?> exceptionType : exceptionTypes) {
            testExceptionConstructors(exceptionType);
        }
    }

    private void testExceptionConstructors(java.lang.Class<?> exceptionType) throws java.lang.IllegalAccessException, java.lang.InstantiationException, java.lang.NoSuchMethodException, java.lang.reflect.InvocationTargetException {
        java.lang.Exception e = ((java.lang.Exception) (exceptionType.newInstance()));
        testThrowException(e);
        e = ((java.lang.Exception) (exceptionType.getConstructor(java.lang.String.class).newInstance(org.apache.ibatis.exceptions.AmplGeneralExceptionsTest.EXPECTED_MESSAGE)));
        testThrowException(e);
        e = ((java.lang.Exception) (exceptionType.getConstructor(java.lang.String.class, java.lang.Throwable.class).newInstance(org.apache.ibatis.exceptions.AmplGeneralExceptionsTest.EXPECTED_MESSAGE, org.apache.ibatis.exceptions.AmplGeneralExceptionsTest.EXPECTED_CAUSE)));
        testThrowException(e);
        e = ((java.lang.Exception) (exceptionType.getConstructor(java.lang.Throwable.class).newInstance(org.apache.ibatis.exceptions.AmplGeneralExceptionsTest.EXPECTED_CAUSE)));
        testThrowException(e);
    }

    private void testThrowException(java.lang.Exception thrown) {
        try {
            throw thrown;
        } catch (java.lang.Exception caught) {
            org.junit.Assert.assertEquals(thrown.getMessage(), caught.getMessage());
            org.junit.Assert.assertEquals(thrown.getCause(), caught.getCause());
        }
    }
}

