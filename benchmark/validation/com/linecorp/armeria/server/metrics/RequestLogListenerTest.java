/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.metrics;


import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.logging.RequestLog;
import com.linecorp.armeria.common.logging.RequestLogListener;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RequestLogListenerTest {
    @Test
    public void testComposition() throws Exception {
        // Given
        final RequestLog mockRequest = Mockito.mock(RequestLog.class);
        final RequestContext mockRequestContext = Mockito.mock(RequestContext.class);
        final int[] executeCounters = new int[]{ 0 };
        final RequestLogListener consumer = ( log) -> (executeCounters[0])++;
        final RequestLogListener finalConsumer = consumer.andThen(consumer).andThen(consumer);
        // When
        Mockito.when(mockRequest.context()).thenReturn(mockRequestContext);
        Mockito.when(mockRequestContext.push()).thenReturn(() -> {
        });
        finalConsumer.onRequestLog(mockRequest);
        // Then
        Assert.assertEquals("onRequestLog() should be invoked 3 times", 3, executeCounters[0]);
    }
}

