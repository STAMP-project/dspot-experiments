/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.collector.receiver;


import com.navercorp.pinpoint.collector.handler.RequestResponseHandler;
import com.navercorp.pinpoint.collector.handler.SimpleHandler;
import com.navercorp.pinpoint.common.server.util.AcceptedTimeService;
import com.navercorp.pinpoint.io.request.ServerRequest;
import com.navercorp.pinpoint.io.request.ServerResponse;
import com.navercorp.pinpoint.thrift.dto.TResult;
import org.apache.thrift.TBase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Taejin Koo
 */
public class DispatchHandlerTest {
    private static final int MAX_HANDLER_COUNT = 5;

    private static final DispatchHandlerTest.TestSimpleHandler TEST_SIMPLE_HANDLER = new DispatchHandlerTest.TestSimpleHandler();

    private static final DispatchHandlerTest.TestRequestHandler TEST_REQUEST_HANDLER = new DispatchHandlerTest.TestRequestHandler();

    @InjectMocks
    DispatchHandlerTest.TestDispatchHandler testDispatchHandler = new DispatchHandlerTest.TestDispatchHandler();

    @Mock
    private AcceptedTimeService acceptedTimeService;

    @Test(expected = UnsupportedOperationException.class)
    public void throwExceptionTest1() {
        ServerRequest request = Mockito.mock(ServerRequest.class);
        Mockito.when(request.getData()).thenReturn(null);
        testDispatchHandler.dispatchSendMessage(request);
    }

    @Test
    public void dispatchSendMessageTest() {
        ServerRequest serverRequest = Mockito.mock(ServerRequest.class);
        Mockito.when(serverRequest.getData()).thenReturn(new TResult());
        testDispatchHandler.dispatchSendMessage(serverRequest);
        Assert.assertTrue(((DispatchHandlerTest.TEST_SIMPLE_HANDLER.getExecutedCount()) > 0));
    }

    @Test
    public void dispatchRequestMessageTest() {
        ServerRequest request = Mockito.mock(ServerRequest.class);
        Mockito.when(request.getData()).thenReturn(new TResult());
        ServerResponse response = Mockito.mock(ServerResponse.class);
        testDispatchHandler.dispatchRequestMessage(request, response);
        Assert.assertTrue(((DispatchHandlerTest.TEST_REQUEST_HANDLER.getExecutedCount()) > 0));
    }

    private static class TestDispatchHandler implements DispatchHandler {
        @Override
        public void dispatchSendMessage(ServerRequest serverRequest) {
            SimpleHandler simpleHandler = getSimpleHandler(serverRequest);
            simpleHandler.handleSimple(serverRequest);
        }

        @Override
        public void dispatchRequestMessage(ServerRequest serverRequest, ServerResponse serverResponse) {
            RequestResponseHandler requestResponseHandler = getRequestResponseHandler(serverRequest);
            requestResponseHandler.handleRequest(serverRequest, serverResponse);
        }

        private RequestResponseHandler getRequestResponseHandler(ServerRequest serverRequest) {
            final Object data = serverRequest.getData();
            return DispatchHandlerTest.TEST_REQUEST_HANDLER;
        }

        private SimpleHandler getSimpleHandler(ServerRequest serverRequest) {
            final Object data = serverRequest.getData();
            if (data instanceof TBase<?, ?>) {
                return getSimpleHandler(((TBase<?, ?>) (data)));
            }
            throw new UnsupportedOperationException(("data is not support type : " + data));
        }

        private SimpleHandler getSimpleHandler(TBase<?, ?> tBase) {
            if (tBase == null) {
                return null;
            }
            return DispatchHandlerTest.TEST_SIMPLE_HANDLER;
        }
    }

    private static class TestSimpleHandler implements SimpleHandler {
        private int executedCount = 0;

        @Override
        public void handleSimple(ServerRequest serverRequest) {
            final Object data = serverRequest.getData();
            if (data instanceof TBase<?, ?>) {
                (executedCount)++;
            } else {
                throw new UnsupportedOperationException((((serverRequest.getClass()) + "is not support type : ") + serverRequest));
            }
        }

        public int getExecutedCount() {
            return executedCount;
        }
    }

    private static class TestRequestHandler implements RequestResponseHandler {
        private int executedCount = 0;

        @Override
        public void handleRequest(ServerRequest serverRequest, ServerResponse serverResponse) {
            (executedCount)++;
            TResult tResult = new TResult();
            serverResponse.write(tResult);
        }

        public int getExecutedCount() {
            return executedCount;
        }
    }
}

