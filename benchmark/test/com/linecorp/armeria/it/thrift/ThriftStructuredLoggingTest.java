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
package com.linecorp.armeria.it.thrift;


import HelloService.Iface;
import TMessageType.CALL;
import TMessageType.REPLY;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.Request;
import com.linecorp.armeria.common.Response;
import com.linecorp.armeria.common.logging.RequestLog;
import com.linecorp.armeria.common.thrift.ThriftCall;
import com.linecorp.armeria.common.thrift.ThriftReply;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.Service;
import com.linecorp.armeria.server.logging.structured.StructuredLoggingService;
import com.linecorp.armeria.server.thrift.THttpService;
import com.linecorp.armeria.server.thrift.ThriftStructuredLog;
import com.linecorp.armeria.service.test.thrift.main.HelloService;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ThriftStructuredLoggingTest {
    private static final BlockingQueue<ThriftStructuredLog> writtenLogs = new LinkedTransferQueue<>();

    private static class MockedStructuredLoggingService<I extends Request, O extends Response> extends StructuredLoggingService<I, O, ThriftStructuredLog> {
        int closed;

        MockedStructuredLoggingService(Service<I, O> delegate) {
            super(delegate, ThriftStructuredLog::new);
        }

        @Override
        protected void writeLog(RequestLog log, ThriftStructuredLog structuredLog) {
            ThriftStructuredLoggingTest.writtenLogs.add(structuredLog);
        }

        @Override
        protected void close() {
            super.close();
            (closed)++;
        }
    }

    private ThriftStructuredLoggingTest.MockedStructuredLoggingService<HttpRequest, HttpResponse> loggingService;

    @Rule
    public final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            loggingService = new ThriftStructuredLoggingTest.MockedStructuredLoggingService(THttpService.of(((HelloService.Iface) (( name) -> "Hello " + name))));
            sb.service("/hello", loggingService);
        }
    };

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test(timeout = 10000)
    public void testStructuredLogging() throws Exception {
        final HelloService.Iface client = newClient();
        client.hello("kawamuray");
        final ThriftStructuredLog log = ThriftStructuredLoggingTest.writtenLogs.take();
        // assertThat(writtenLogs.size()).isEqualTo(1);
        assertThat(log.timestampMillis()).isGreaterThan(0);
        assertThat(log.responseTimeNanos()).isGreaterThanOrEqualTo(0);
        assertThat(log.thriftServiceName()).isEqualTo(HelloService.class.getCanonicalName());
        assertThat(log.thriftMethodName()).isEqualTo("hello");
        final ThriftCall call = log.thriftCall();
        assertThat(call.header().name).isEqualTo("hello");
        assertThat(call.header().type).isEqualTo(CALL);
        assertThat(call.args()).isEqualTo(new com.linecorp.armeria.service.test.thrift.main.HelloService.hello_args().setName("kawamuray"));
        final ThriftReply reply = log.thriftReply();
        assertThat(reply.header().name).isEqualTo("hello");
        assertThat(reply.header().type).isEqualTo(REPLY);
        assertThat(reply.header().seqid).isEqualTo(call.header().seqid);
        assertThat(reply.result()).isEqualTo(new com.linecorp.armeria.service.test.thrift.main.HelloService.hello_result().setSuccess("Hello kawamuray"));
    }

    @Test(timeout = 10000)
    public void testWriterClosed() throws Exception {
        server.stop().join();
        assertThat(loggingService.closed).isOne();
    }
}

