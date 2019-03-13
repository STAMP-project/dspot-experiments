/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.alibaba.fescar.spring.annotation;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;


/**
 *
 *
 * @author Wu
 * @unknown 2019/3/8
 */
public class MethodDescTest {
    private static final GlobalTransactionScanner GLOBAL_TRANSACTION_SCANNER = new GlobalTransactionScanner("global-trans-scanner-test");

    private static Method method = null;

    private static GlobalTransactional transactional = null;

    public MethodDescTest() throws NoSuchMethodException {
        MethodDescTest.method = MethodDescTest.MockBusiness.class.getDeclaredMethod("doBiz", String.class);
        MethodDescTest.transactional = MethodDescTest.method.getAnnotation(GlobalTransactional.class);
    }

    @Test
    public void testGetTransactionAnnotation() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodDesc methodDesc = getMethodDesc();
        assertThat(methodDesc.getTransactionAnnotation()).isEqualTo(MethodDescTest.transactional);
    }

    @Test
    public void testGetMethod() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodDesc methodDesc = getMethodDesc();
        assertThat(methodDesc.getMethod()).isEqualTo(MethodDescTest.method);
    }

    @Test
    public void testSetTransactionAnnotation() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodDesc methodDesc = getMethodDesc();
        assertThat(methodDesc.getTransactionAnnotation()).isNotNull();
        methodDesc.setTransactionAnnotation(null);
        assertThat(methodDesc.getTransactionAnnotation()).isNull();
    }

    @Test
    public void testSetMethod() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MethodDesc methodDesc = getMethodDesc();
        assertThat(methodDesc.getMethod()).isNotNull();
        methodDesc.setMethod(null);
        assertThat(methodDesc.getMethod()).isNull();
    }

    /**
     * the type mock business
     */
    private static class MockBusiness {
        @GlobalTransactional(timeoutMills = 300000, name = "busi-doBiz")
        public String doBiz(String msg) {
            return "hello " + msg;
        }
    }
}

