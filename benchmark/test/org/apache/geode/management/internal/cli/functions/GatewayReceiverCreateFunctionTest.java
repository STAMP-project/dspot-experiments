/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.functions;


import org.apache.geode.cache.Cache;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.wan.GatewayReceiver;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class GatewayReceiverCreateFunctionTest {
    private GatewayReceiverCreateFunction function = Mockito.mock(GatewayReceiverCreateFunction.class);

    private FunctionContext context = Mockito.mock(FunctionContext.class);

    private Cache cache = Mockito.mock(Cache.class);

    private GatewayReceiverFunctionArgs args = Mockito.mock(GatewayReceiverFunctionArgs.class);

    private GatewayReceiver receiver = Mockito.mock(GatewayReceiver.class);

    private ResultSender resultSender = Mockito.mock(ResultSender.class);

    @Test
    public void testFunctionSuccessResult() {
        function.execute(context);
        ArgumentCaptor<Object> resultObject = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultObject.capture());
        CliFunctionResult result = ((CliFunctionResult) (resultObject.getValue()));
        assertThat(result.getStatusMessage()).contains("5555");
    }
}

