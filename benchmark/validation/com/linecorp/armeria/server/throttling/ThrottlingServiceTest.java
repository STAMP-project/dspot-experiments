/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.throttling;


import HttpStatus.OK;
import HttpStatus.SERVICE_UNAVAILABLE;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.testing.server.ServerRule;
import org.junit.Rule;
import org.junit.Test;


public class ThrottlingServiceTest {
    static final HttpService SERVICE = new AbstractHttpService() {
        @Override
        protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) throws Exception {
            return HttpResponse.of(OK);
        }
    };

    @Rule
    public ServerRule serverRule = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.service("/http-never", ThrottlingServiceTest.SERVICE.decorate(ThrottlingHttpService.newDecorator(ThrottlingStrategy.never())));
            sb.service("/http-always", ThrottlingServiceTest.SERVICE.decorate(ThrottlingHttpService.newDecorator(ThrottlingStrategy.always())));
        }
    };

    @Test
    public void serve() throws Exception {
        final HttpClient client = HttpClient.of(serverRule.uri("/"));
        assertThat(client.get("/http-always").aggregate().get().status()).isEqualTo(OK);
    }

    @Test
    public void throttle() throws Exception {
        final HttpClient client = HttpClient.of(serverRule.uri("/"));
        assertThat(client.get("/http-never").aggregate().get().status()).isEqualTo(SERVICE_UNAVAILABLE);
    }
}

