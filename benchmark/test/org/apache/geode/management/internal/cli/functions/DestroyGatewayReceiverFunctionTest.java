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


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.wan.GatewayReceiver;
import org.apache.geode.internal.cache.InternalCache;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class DestroyGatewayReceiverFunctionTest {
    private DestroyGatewayReceiverFunction function;

    private FunctionContext context;

    private InternalCache cache;

    private ResultSender resultSender;

    private ArgumentCaptor<CliFunctionResult> resultCaptor;

    @Test
    public void getGatewayReceiversNull_doesNotThrowException() {
        Mockito.when(cache.getGatewayReceivers()).thenReturn(null);
        function.execute(context);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getThrowable()).isNull();
        assertThat(result.getMessage()).isEqualTo("Gateway receiver not found.");
    }

    @Test
    public void getGatewayReceiversNotFound_returnsStatusIgnored() {
        Mockito.when(cache.getGatewayReceivers()).thenReturn(Collections.emptySet());
        function.execute(context);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.getStatus(true)).contains("IGNORED");
        assertThat(result.getThrowable()).isNull();
        assertThat(result.getMessage()).isEqualTo("Gateway receiver not found.");
    }

    @Test
    public void runningReceivers_stopCalledBeforeDestroying() {
        GatewayReceiver receiver = Mockito.mock(GatewayReceiver.class);
        Set<GatewayReceiver> receivers = new HashSet<>();
        receivers.add(receiver);
        Mockito.when(cache.getGatewayReceivers()).thenReturn(receivers);
        Mockito.when(receiver.isRunning()).thenReturn(true);
        function.execute(context);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        Mockito.verify(receiver).stop();
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.getStatus(true)).isEqualTo("OK");
    }

    @Test
    public void stoppedReceivers_stopNotCalledBeforeDestroying() {
        GatewayReceiver receiver = Mockito.mock(GatewayReceiver.class);
        Set<GatewayReceiver> receivers = new HashSet<>();
        receivers.add(receiver);
        Mockito.when(cache.getGatewayReceivers()).thenReturn(receivers);
        Mockito.when(receiver.isRunning()).thenReturn(false);
        function.execute(context);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        Mockito.verify(receiver, Mockito.never()).stop();
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.getStatus(true)).isEqualTo("OK");
    }
}

