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


import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.internal.cache.InternalCache;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GatewaySenderDestroyFunctionTest {
    private GatewaySenderDestroyFunction function;

    private FunctionContext context;

    private InternalCache cache;

    private ResultSender resultSender;

    private ArgumentCaptor<CliFunctionResult> resultCaptor;

    private GatewaySenderDestroyFunctionArgs args;

    @Test
    public void gateWaySenderNotFound_ifExists_false() throws Exception {
        Mockito.when(cache.getGatewaySender(ArgumentMatchers.any())).thenReturn(null);
        Mockito.when(args.isIfExists()).thenReturn(false);
        function.execute(context);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getThrowable()).isNull();
        assertThat(result.getMessage()).isEqualTo("Gateway sender id not found.");
    }

    @Test
    public void gateWaySenderNotFound_ifExists_true() throws Exception {
        Mockito.when(cache.getGatewaySender(ArgumentMatchers.any())).thenReturn(null);
        Mockito.when(args.isIfExists()).thenReturn(true);
        function.execute(context);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getThrowable()).isNull();
        assertThat(result.getMessage()).isEqualTo("Skipping: Gateway sender id not found.");
    }
}

