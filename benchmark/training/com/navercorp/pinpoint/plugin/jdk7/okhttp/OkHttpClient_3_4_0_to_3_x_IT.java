/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.jdk7.okhttp;


import com.navercorp.pinpoint.bootstrap.plugin.test.Expectations;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.common.plugin.util.HostAndPort;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.plugin.WebServer;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author HyunGil Jeong
 */
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@Dependency({ "com.squareup.okhttp3:okhttp:[3.4.0,)", "org.nanohttpd:nanohttpd:2.3.1" })
public class OkHttpClient_3_4_0_to_3_x_IT {
    private static WebServer webServer;

    @Test
    public void execute() throws Exception {
        Request request = new Request.Builder().url(OkHttpClient_3_4_0_to_3_x_IT.webServer.getCallHttpUrl()).build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        Method executeMethod = Class.forName("okhttp3.RealCall").getDeclaredMethod("execute");
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), executeMethod));
        Method interceptMethod = Class.forName("okhttp3.internal.http.BridgeInterceptor").getDeclaredMethod("intercept", Class.forName("okhttp3.Interceptor$Chain"));
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT.getName(), interceptMethod, null, null, OkHttpClient_3_4_0_to_3_x_IT.webServer.getHostAndPort(), Expectations.annotation("http.url", request.url().toString()), Expectations.annotation("http.status.code", response.code())));
        String hostAndPort = HostAndPort.toHostAndPortString(request.url().host(), request.url().port());
        Method connectMethod = getConnectMethod(Class.forName("okhttp3.internal.connection.RealConnection"));
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), connectMethod, Expectations.annotation("http.internal.display", hostAndPort)));
        verifier.verifyTraceCount(0);
    }

    @Test
    public void enqueue() throws Exception {
        Request request = new Request.Builder().url(OkHttpClient_3_4_0_to_3_x_IT.webServer.getCallHttpUrl()).build();
        OkHttpClient client = new OkHttpClient();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Response> responseRef = new AtomicReference<Response>(null);
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                latch.countDown();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws java.io.IOException {
                responseRef.set(response);
                latch.countDown();
            }
        });
        latch.await(3, TimeUnit.SECONDS);
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.awaitTrace(Expectations.event(ASYNC.getName(), "Asynchronous Invocation"), 20, 3000);
        verifier.printCache();
        Method realCallEnqueueMethod = Class.forName("okhttp3.RealCall").getDeclaredMethod("enqueue", Class.forName("okhttp3.Callback"));
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), realCallEnqueueMethod));
        Method dispatcherEnqueueMethod = Class.forName("okhttp3.Dispatcher").getDeclaredMethod("enqueue", Class.forName("okhttp3.RealCall$AsyncCall"));
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), dispatcherEnqueueMethod));
        verifier.verifyTrace(Expectations.event(ASYNC.getName(), "Asynchronous Invocation"));
        Method executeMethod = Class.forName("okhttp3.RealCall$AsyncCall").getDeclaredMethod("execute");
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), executeMethod));
        Response response = responseRef.get();
        Method interceptMethod = Class.forName("okhttp3.internal.http.BridgeInterceptor").getDeclaredMethod("intercept", Class.forName("okhttp3.Interceptor$Chain"));
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT.getName(), interceptMethod, null, null, OkHttpClient_3_4_0_to_3_x_IT.webServer.getHostAndPort(), Expectations.annotation("http.url", request.url().toString()), Expectations.annotation("http.status.code", response.code())));
        String hostAndPort = HostAndPort.toHostAndPortString(request.url().host(), request.url().port());
        Method connectMethod = getConnectMethod(Class.forName("okhttp3.internal.connection.RealConnection"));
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), connectMethod, Expectations.annotation("http.internal.display", hostAndPort)));
        verifier.verifyTraceCount(0);
    }
}

