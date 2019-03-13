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
package org.apache.dubbo.rpc.filter;


import com.alibaba.com.caucho.hessian.HessianException;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.support.DemoService;
import org.apache.dubbo.rpc.support.LocalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * ExceptionFilterTest
 */
public class ExceptionFilterTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testRpcException() {
        Logger logger = Mockito.mock(Logger.class);
        RpcContext.getContext().setRemoteAddress("127.0.0.1", 1234);
        RpcException exception = new RpcException("TestRpcException");
        ExceptionFilter exceptionFilter = new ExceptionFilter(logger);
        RpcInvocation invocation = new RpcInvocation("sayHello", new Class<?>[]{ String.class }, new Object[]{ "world" });
        Invoker<DemoService> invoker = Mockito.mock(Invoker.class);
        BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
        BDDMockito.given(invoker.invoke(ArgumentMatchers.eq(invocation))).willThrow(exception);
        try {
            exceptionFilter.invoke(invoker, invocation);
        } catch (RpcException e) {
            Assertions.assertEquals("TestRpcException", e.getMessage());
        }
        Mockito.verify(logger).error(ArgumentMatchers.eq((((("Got unchecked and undeclared exception which called by 127.0.0.1. service: " + (DemoService.class.getName())) + ", method: sayHello, exception: ") + (RpcException.class.getName())) + ": TestRpcException")), ArgumentMatchers.eq(exception));
        RpcContext.removeContext();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testJavaException() {
        ExceptionFilter exceptionFilter = new ExceptionFilter();
        RpcInvocation invocation = new RpcInvocation("sayHello", new Class<?>[]{ String.class }, new Object[]{ "world" });
        RpcResult rpcResult = new RpcResult();
        rpcResult.setException(new IllegalArgumentException("java"));
        Invoker<DemoService> invoker = Mockito.mock(Invoker.class);
        Mockito.when(invoker.invoke(invocation)).thenReturn(rpcResult);
        Mockito.when(invoker.getInterface()).thenReturn(DemoService.class);
        Result newResult = exceptionFilter.invoke(invoker, invocation);
        Assertions.assertEquals(rpcResult.getException(), newResult.getException());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRuntimeException() {
        ExceptionFilter exceptionFilter = new ExceptionFilter();
        RpcInvocation invocation = new RpcInvocation("sayHello", new Class<?>[]{ String.class }, new Object[]{ "world" });
        RpcResult rpcResult = new RpcResult();
        rpcResult.setException(new LocalException("localException"));
        Invoker<DemoService> invoker = Mockito.mock(Invoker.class);
        Mockito.when(invoker.invoke(invocation)).thenReturn(rpcResult);
        Mockito.when(invoker.getInterface()).thenReturn(DemoService.class);
        Result newResult = exceptionFilter.invoke(invoker, invocation);
        Assertions.assertEquals(rpcResult.getException(), newResult.getException());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConvertToRunTimeException() {
        ExceptionFilter exceptionFilter = new ExceptionFilter();
        RpcInvocation invocation = new RpcInvocation("sayHello", new Class<?>[]{ String.class }, new Object[]{ "world" });
        RpcResult rpcResult = new RpcResult();
        rpcResult.setException(new HessianException("hessian"));
        Invoker<DemoService> invoker = Mockito.mock(Invoker.class);
        Mockito.when(invoker.invoke(invocation)).thenReturn(rpcResult);
        Mockito.when(invoker.getInterface()).thenReturn(DemoService.class);
        Result newResult = exceptionFilter.invoke(invoker, invocation);
        newResult = exceptionFilter.onResponse(newResult, invoker, invocation);
        Assertions.assertFalse(((newResult.getException()) instanceof HessianException));
        Assertions.assertEquals(newResult.getException().getClass(), RuntimeException.class);
        Assertions.assertEquals(newResult.getException().getMessage(), StringUtils.toString(rpcResult.getException()));
    }
}

