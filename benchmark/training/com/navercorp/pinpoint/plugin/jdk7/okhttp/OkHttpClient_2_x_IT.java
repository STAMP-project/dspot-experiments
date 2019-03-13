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
package com.navercorp.pinpoint.plugin.jdk7.okhttp;


import com.navercorp.pinpoint.bootstrap.plugin.test.Expectations;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.common.plugin.util.HostAndPort;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.plugin.WebServer;
import com.navercorp.pinpoint.plugin.okhttp.EndPointUtils;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.internal.http.HttpEngine;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author jaehong.kim
 */
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@Dependency({ "com.squareup.okhttp:okhttp:[2.0.0,3.0.0)", "org.nanohttpd:nanohttpd:2.3.1" })
public class OkHttpClient_2_x_IT {
    private static WebServer webServer;

    @Test
    public void execute() throws Exception {
        Request request = new Request.Builder().url(OkHttpClient_2_x_IT.webServer.getCallHttpUrl()).build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        Method executeMethod = Call.class.getDeclaredMethod("execute");
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), executeMethod));
        Method sendRequestMethod = HttpEngine.class.getDeclaredMethod("sendRequest");
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT.getName(), sendRequestMethod, null, null, OkHttpClient_2_x_IT.webServer.getHostAndPort(), Expectations.annotation("http.url", request.urlString())));
        URL url = request.url();
        int port = EndPointUtils.getPort(url.getPort(), url.getDefaultPort());
        String hostAndPort = HostAndPort.toHostAndPortString(url.getHost(), port);
        Method connectMethod = getConnectMethod();
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), connectMethod, Expectations.annotation("http.internal.display", hostAndPort)));
        Method readResponseMethod = HttpEngine.class.getDeclaredMethod("readResponse");
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), readResponseMethod, Expectations.annotation("http.status.code", response.code())));
        verifier.verifyTraceCount(0);
    }

    @Test
    public void enqueue() throws Exception {
        Request request = new Request.Builder().url(OkHttpClient_2_x_IT.webServer.getCallHttpUrl()).build();
        OkHttpClient client = new OkHttpClient();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Response> responseRef = new AtomicReference<Response>(null);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, java.io.IOException e) {
                latch.countDown();
            }

            @Override
            public void onResponse(Response response) throws java.io.IOException {
                responseRef.set(response);
                latch.countDown();
            }
        });
        latch.await(3, TimeUnit.SECONDS);
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.awaitTrace(Expectations.event(ASYNC.getName(), "Asynchronous Invocation"), 20, 3000);
        verifier.printCache();
        Method callEnqueueMethod = Call.class.getDeclaredMethod("enqueue", Callback.class);
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), callEnqueueMethod));
        Method dispatcherEnqueueMethod = Dispatcher.class.getDeclaredMethod("enqueue", Class.forName("com.squareup.okhttp.Call$AsyncCall"));
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), dispatcherEnqueueMethod));
        verifier.verifyTrace(Expectations.event(ASYNC.getName(), "Asynchronous Invocation"));
        Method executeMethod = Class.forName("com.squareup.okhttp.Call$AsyncCall").getDeclaredMethod("execute");
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), executeMethod));
        Method sendRequestMethod = HttpEngine.class.getDeclaredMethod("sendRequest");
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT.getName(), sendRequestMethod, null, null, OkHttpClient_2_x_IT.webServer.getHostAndPort(), Expectations.annotation("http.url", request.urlString())));
        URL url = request.url();
        int port = EndPointUtils.getPort(url.getPort(), url.getDefaultPort());
        String hostAndPort = HostAndPort.toHostAndPortString(url.getHost(), port);
        Method connectMethod = getConnectMethod();
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), connectMethod, Expectations.annotation("http.internal.display", hostAndPort)));
        Response response = responseRef.get();
        Assert.assertNotNull("response is null", response);
        Method readResponseMethod = HttpEngine.class.getDeclaredMethod("readResponse");
        verifier.verifyTrace(Expectations.event(OK_HTTP_CLIENT_INTERNAL.getName(), readResponseMethod, Expectations.annotation("http.status.code", response.code())));
        verifier.verifyTraceCount(0);
    }
}

