/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.validation.filter;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.validation.Validation;
import org.apache.dubbo.validation.Validator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class ValidationFilterTest {
    private Invoker<?> invoker = Mockito.mock(Invoker.class);

    private Validation validation = Mockito.mock(Validation.class);

    private Validator validator = Mockito.mock(Validator.class);

    private RpcInvocation invocation = Mockito.mock(RpcInvocation.class);

    private ValidationFilter validationFilter;

    @Test
    public void testItWithNotExistClass() throws Exception {
        URL url = URL.valueOf("test://test:11/test?default.validation=true");
        BDDMockito.given(validation.getValidator(url)).willThrow(new IllegalStateException("Not found class test, cause: test"));
        BDDMockito.given(invoker.invoke(invocation)).willReturn(new RpcResult("success"));
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        BDDMockito.given(invocation.getMethodName()).willReturn("echo1");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ String.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "arg1" });
        validationFilter.setValidation(validation);
        Result result = validationFilter.invoke(invoker, invocation);
        MatcherAssert.assertThat(result.getException().getMessage(), Is.is("Not found class test, cause: test"));
    }

    @Test
    public void testItWithExistClass() throws Exception {
        URL url = URL.valueOf("test://test:11/test?default.validation=true");
        BDDMockito.given(validation.getValidator(url)).willReturn(validator);
        BDDMockito.given(invoker.invoke(invocation)).willReturn(new RpcResult("success"));
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        BDDMockito.given(invocation.getMethodName()).willReturn("echo1");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ String.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "arg1" });
        validationFilter.setValidation(validation);
        Result result = validationFilter.invoke(invoker, invocation);
        MatcherAssert.assertThat(String.valueOf(result.getValue()), Is.is("success"));
    }

    @Test
    public void testItWithoutUrlParameters() throws Exception {
        URL url = URL.valueOf("test://test:11/test");
        BDDMockito.given(validation.getValidator(url)).willReturn(validator);
        BDDMockito.given(invoker.invoke(invocation)).willReturn(new RpcResult("success"));
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        BDDMockito.given(invocation.getMethodName()).willReturn("echo1");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ String.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "arg1" });
        validationFilter.setValidation(validation);
        Result result = validationFilter.invoke(invoker, invocation);
        MatcherAssert.assertThat(String.valueOf(result.getValue()), Is.is("success"));
    }

    @Test
    public void testItWhileMethodNameStartWithDollar() throws Exception {
        URL url = URL.valueOf("test://test:11/test");
        BDDMockito.given(validation.getValidator(url)).willReturn(validator);
        BDDMockito.given(invoker.invoke(invocation)).willReturn(new RpcResult("success"));
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        BDDMockito.given(invocation.getMethodName()).willReturn("$echo1");
        BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ String.class });
        BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "arg1" });
        validationFilter.setValidation(validation);
        Result result = validationFilter.invoke(invoker, invocation);
        MatcherAssert.assertThat(String.valueOf(result.getValue()), Is.is("success"));
    }

    @Test
    public void testItWhileThrowoutRpcException() throws Exception {
        Assertions.assertThrows(RpcException.class, () -> {
            URL url = URL.valueOf("test://test:11/test?default.validation=true");
            BDDMockito.given(validation.getValidator(url)).willThrow(new RpcException("rpc exception"));
            BDDMockito.given(invoker.invoke(invocation)).willReturn(new RpcResult("success"));
            BDDMockito.given(invoker.getUrl()).willReturn(url);
            BDDMockito.given(invocation.getMethodName()).willReturn("echo1");
            BDDMockito.given(invocation.getParameterTypes()).willReturn(new Class<?>[]{ String.class });
            BDDMockito.given(invocation.getArguments()).willReturn(new Object[]{ "arg1" });
            validationFilter.setValidation(validation);
            validationFilter.invoke(invoker, invocation);
        });
    }
}

