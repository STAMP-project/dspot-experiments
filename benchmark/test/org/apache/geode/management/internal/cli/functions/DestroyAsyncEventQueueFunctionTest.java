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


import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.management.internal.configuration.domain.XmlEntity;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DestroyAsyncEventQueueFunctionTest {
    private static final String TEST_AEQ_ID = "Test-AEQ";

    private AsyncEventQueue mockAEQ;

    private FunctionContext mockContext;

    private DestroyAsyncEventQueueFunctionArgs mockArgs;

    private GemFireCacheImpl cache;

    private ResultSender resultSender;

    private ArgumentCaptor<CliFunctionResult> resultCaptor;

    private DestroyAsyncEventQueueFunction function;

    @Test
    public void execute_validAeqId_OK() throws Throwable {
        XmlEntity xmlEntity = Mockito.mock(XmlEntity.class);
        Mockito.doReturn(xmlEntity).when(function).getAEQXmlEntity(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.when(cache.getAsyncEventQueue(DestroyAsyncEventQueueFunctionTest.TEST_AEQ_ID)).thenReturn(mockAEQ);
        function.execute(mockContext);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getXmlEntity()).isNotNull();
        assertThat(result.getThrowable()).isNull();
    }

    @Test
    public void execute_nonexistentAeqId_returnsError() throws Throwable {
        Mockito.when(cache.getAsyncEventQueue(DestroyAsyncEventQueueFunctionTest.TEST_AEQ_ID)).thenReturn(null);
        function.execute(mockContext);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getMessage()).containsPattern(((DestroyAsyncEventQueueFunctionTest.TEST_AEQ_ID) + ".*not found"));
    }

    @Test
    public void execute_nonexistentAeqIdIfExists_returnsSuccess() throws Throwable {
        Mockito.when(cache.getAsyncEventQueue(DestroyAsyncEventQueueFunctionTest.TEST_AEQ_ID)).thenReturn(null);
        Mockito.when(mockArgs.isIfExists()).thenReturn(true);
        function.execute(mockContext);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getMessage()).containsPattern((("Skipping:.*" + (DestroyAsyncEventQueueFunctionTest.TEST_AEQ_ID)) + ".*not found"));
    }
}

