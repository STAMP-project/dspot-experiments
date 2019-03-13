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
package org.apache.dubbo.rpc.protocol.dubbo;


import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.protocol.dubbo.filter.FutureFilter;
import org.apache.dubbo.rpc.protocol.dubbo.support.DemoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * EventFilterTest.java
 * TODO rely on callback integration test for now
 */
public class FutureFilterTest {
    private static RpcInvocation invocation;

    private Filter eventFilter = new FutureFilter();

    @Test
    public void testSyncCallback() {
        @SuppressWarnings("unchecked")
        Invoker<DemoService> invoker = Mockito.mock(Invoker.class);
        BDDMockito.given(invoker.isAvailable()).willReturn(true);
        BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
        RpcResult result = new RpcResult();
        result.setValue("High");
        BDDMockito.given(invoker.invoke(FutureFilterTest.invocation)).willReturn(result);
        URL url = URL.valueOf("test://test:11/test?group=dubbo&version=1.1");
        BDDMockito.given(invoker.getUrl()).willReturn(url);
        Result filterResult = eventFilter.invoke(invoker, FutureFilterTest.invocation);
        Assertions.assertEquals("High", filterResult.getValue());
    }

    @Test
    public void testSyncCallbackHasException() throws Throwable, RpcException {
        Assertions.assertThrows(RuntimeException.class, () -> {
            @SuppressWarnings("unchecked")
            Invoker<DemoService> invoker = Mockito.mock(Invoker.class);
            BDDMockito.given(invoker.isAvailable()).willReturn(true);
            BDDMockito.given(invoker.getInterface()).willReturn(DemoService.class);
            RpcResult result = new RpcResult();
            result.setException(new RuntimeException());
            BDDMockito.given(invoker.invoke(FutureFilterTest.invocation)).willReturn(result);
            URL url = URL.valueOf((("test://test:11/test?group=dubbo&version=1.1&" + (Constants.ON_THROW_METHOD_KEY)) + "=echo"));
            BDDMockito.given(invoker.getUrl()).willReturn(url);
            eventFilter.invoke(invoker, FutureFilterTest.invocation).recreate();
        });
    }
}

