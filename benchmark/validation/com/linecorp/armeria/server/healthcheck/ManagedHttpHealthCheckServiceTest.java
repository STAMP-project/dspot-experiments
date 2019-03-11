/**
 * Copyright 2017 LINE Corporation
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
package com.linecorp.armeria.server.healthcheck;


import HttpHeaderNames.CONTENT_TYPE;
import HttpMethod.HEAD;
import HttpMethod.PUT;
import HttpStatus.BAD_REQUEST;
import HttpStatus.OK;
import HttpStatus.SERVICE_UNAVAILABLE;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpRequestWriter;
import com.linecorp.armeria.server.ServiceRequestContext;
import org.junit.Test;


public class ManagedHttpHealthCheckServiceTest {
    private final ManagedHttpHealthCheckService service = new ManagedHttpHealthCheckService();

    private final HttpRequest hcReq = HttpRequest.of(HEAD, "/");

    private final HttpRequest hcTurnOffReq = HttpRequest.of(PUT, "/", PLAIN_TEXT_UTF_8, "off");

    private final HttpRequest hcTurnOnReq = HttpRequest.of(PUT, "/", PLAIN_TEXT_UTF_8, "on");

    @Test
    public void turnOff() throws Exception {
        service.serverHealth.setHealthy(true);
        ServiceRequestContext ctx = ServiceRequestContext.of(hcTurnOffReq);
        AggregatedHttpMessage res = service.serve(ctx, hcTurnOffReq).aggregate().get();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.headers().get(CONTENT_TYPE)).isEqualTo(PLAIN_TEXT_UTF_8.toString());
        assertThat(res.contentUtf8()).isEqualTo("Set unhealthy.");
        ctx = com.linecorp.armeria.server.ServiceRequestContextBuilder.of(hcReq).service(service).build();
        res = service.serve(ctx, hcReq).aggregate().get();
        assertThat(res.status()).isEqualTo(SERVICE_UNAVAILABLE);
        assertThat(res.headers().get(CONTENT_TYPE)).isEqualTo(PLAIN_TEXT_UTF_8.toString());
    }

    @Test
    public void turnOn() throws Exception {
        ServiceRequestContext ctx = ServiceRequestContext.of(hcTurnOnReq);
        AggregatedHttpMessage res = service.serve(ctx, hcTurnOnReq).aggregate().get();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.headers().get(CONTENT_TYPE)).isEqualTo(PLAIN_TEXT_UTF_8.toString());
        assertThat(res.contentUtf8()).isEqualTo("Set healthy.");
        ctx = com.linecorp.armeria.server.ServiceRequestContextBuilder.of(hcReq).service(service).build();
        res = service.serve(ctx, hcReq).aggregate().get();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.headers().get(CONTENT_TYPE)).isEqualTo(PLAIN_TEXT_UTF_8.toString());
    }

    @Test
    public void notSupported() throws Exception {
        HttpRequestWriter noopRequest = HttpRequest.streaming(PUT, "/");
        noopRequest.write(() -> HttpData.ofAscii("noop"));
        noopRequest.close();
        ServiceRequestContext ctx = ServiceRequestContext.of(noopRequest);
        AggregatedHttpMessage res = service.serve(ctx, noopRequest).aggregate().get();
        assertThat(res.status()).isEqualTo(BAD_REQUEST);
        assertThat(res.headers().get(CONTENT_TYPE)).isEqualTo(PLAIN_TEXT_UTF_8.toString());
        assertThat(res.contentUtf8()).isEqualTo("Not supported.");
        service.serverHealth.setHealthy(true);
        noopRequest = HttpRequest.streaming(PUT, "/");
        noopRequest.write(() -> HttpData.ofAscii("noop"));
        noopRequest.close();
        ctx = com.linecorp.armeria.server.ServiceRequestContextBuilder.of(noopRequest).service(service).build();
        res = service.serve(ctx, noopRequest).aggregate().get();
        assertThat(res.status()).isEqualTo(BAD_REQUEST);
        assertThat(res.headers().get(CONTENT_TYPE)).isEqualTo(PLAIN_TEXT_UTF_8.toString());
        assertThat(res.contentUtf8()).isEqualTo("Not supported.");
    }
}

