/**
 * Copyright 2018 LINE Corporation
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
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.thrift.THttpService;
import com.linecorp.armeria.server.thrift.ThriftCallService;
import com.linecorp.armeria.server.throttling.ThrottlingRpcService;
import com.linecorp.armeria.service.test.thrift.main.HelloService;
import com.linecorp.armeria.testing.server.ServerRule;
import org.apache.thrift.TApplicationException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ThrottlingRpcServiceTest {
    @Rule
    public final ServerRule server = new ServerRule(false) {
        @Override
        protected void configure(ServerBuilder sb) {
            sb.service("/thrift-never", ThriftCallService.of(serviceHandler).decorate(ThrottlingRpcService.newDecorator(never())).decorate(THttpService.newDecorator()));
            sb.service("/thrift-always", ThriftCallService.of(serviceHandler).decorate(ThrottlingRpcService.newDecorator(always())).decorate(THttpService.newDecorator()));
        }
    };

    @Rule
    public MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private Iface serviceHandler;

    @Test
    public void serve() throws Exception {
        final HelloService.Iface client = build(Iface.class);
        Mockito.when(serviceHandler.hello("foo")).thenReturn("bar");
        assertThat(client.hello("foo")).isEqualTo("bar");
    }

    @Test
    public void throttle() throws Exception {
        final HelloService.Iface client = build(Iface.class);
        assertThatThrownBy(() -> client.hello("foo")).isInstanceOf(TApplicationException.class);
        Mockito.verifyNoMoreInteractions(serviceHandler);
    }
}

