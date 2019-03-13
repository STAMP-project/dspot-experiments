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
package com.navercorp.pinpoint.plugin.common.servlet;


import com.navercorp.pinpoint.bootstrap.context.SpanRecorder;
import com.navercorp.pinpoint.bootstrap.plugin.request.RequestAdaptor;
import com.navercorp.pinpoint.bootstrap.plugin.request.ServerRequestRecorder;
import com.navercorp.pinpoint.bootstrap.plugin.request.ServerRequestWrapper;
import com.navercorp.pinpoint.bootstrap.plugin.request.ServerRequestWrapperAdaptor;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author jaehong.kim
 */
public class ServerRequestRecorderTest {
    private static final String RPC_NAME = "rpcName";

    private static final String END_POINT = "endPoint";

    private static final String REMOTE_ADDRESS = "remoteAddress";

    private static final String ACCEPTOR_HOST = "acceptorHost";

    private static final String GET_HEADER = "getHeader";

    @Test
    public void record() throws Exception {
        RequestAdaptor<ServerRequestWrapper> requestAdaptor = new ServerRequestWrapperAdaptor();
        final ServerRequestRecorder<ServerRequestWrapper> recorder = new ServerRequestRecorder<ServerRequestWrapper>(requestAdaptor);
        // SpanRecorder
        SpanRecorder spanRecorder = Mockito.mock(SpanRecorder.class);
        recorder.record(spanRecorder, new ServerRequestRecorderTest.MockServerRequestWrapper());
        Mockito.verify(spanRecorder).recordRpcName(ServerRequestRecorderTest.RPC_NAME);
        Mockito.verify(spanRecorder).recordEndPoint(ServerRequestRecorderTest.END_POINT);
        Mockito.verify(spanRecorder).recordRemoteAddress(ServerRequestRecorderTest.REMOTE_ADDRESS);
        Mockito.verify(spanRecorder).recordAcceptorHost(ServerRequestRecorderTest.GET_HEADER);
    }

    private class MockServerRequestWrapper implements ServerRequestWrapper {
        @Override
        public String getRpcName() {
            return ServerRequestRecorderTest.RPC_NAME;
        }

        @Override
        public String getEndPoint() {
            return ServerRequestRecorderTest.END_POINT;
        }

        @Override
        public String getRemoteAddress() {
            return ServerRequestRecorderTest.REMOTE_ADDRESS;
        }

        @Override
        public String getAcceptorHost() {
            return ServerRequestRecorderTest.ACCEPTOR_HOST;
        }

        @Override
        public String getHeader(String name) {
            return ServerRequestRecorderTest.GET_HEADER;
        }
    }
}

