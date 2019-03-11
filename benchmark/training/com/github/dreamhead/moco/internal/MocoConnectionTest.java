package com.github.dreamhead.moco.internal;


import com.github.dreamhead.moco.AbstractMocoHttpTest;
import com.github.dreamhead.moco.BaseMocoHttpTest;
import com.github.dreamhead.moco.Runner;
import com.github.dreamhead.moco.com.github.dreamhead.moco.Runnable;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoConnectionTest extends AbstractMocoHttpTest {
    @Test
    public void should_keep_alive_for_1_0_keep_alive_request() throws Exception {
        server.response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Request request = Request.Get(RemoteTestUtils.root()).version(HttpVersion.HTTP_1_0).addHeader("Connection", "keep-alive");
                HttpResponse response = helper.execute(request);
                String connection = response.getFirstHeader("Connection").getValue();
                Assert.assertThat(connection, CoreMatchers.is("keep-alive"));
            }
        });
    }

    @Test
    public void should_not_have_keep_alive_header_for_1_1_keep_alive_request() throws Exception {
        server.response("foo");
        Runner.running(server, new com.github.dreamhead.moco.Runnable() {
            @Override
            public void run() throws Exception {
                Request request = Request.Get(RemoteTestUtils.root()).version(HttpVersion.HTTP_1_1).addHeader("Connection", "keep-alive");
                HttpResponse response = helper.execute(request);
                Assert.assertThat(response.getFirstHeader("Connection"), CoreMatchers.nullValue());
            }
        });
    }

    @Test
    public void should_not_keep_alive_for_close_request() throws Exception {
        server.response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Request request = Request.Get(RemoteTestUtils.root()).addHeader("Connection", "close");
                HttpResponse response = helper.execute(request);
                Assert.assertThat(response.getFirstHeader("Connection"), CoreMatchers.nullValue());
            }
        });
    }
}

