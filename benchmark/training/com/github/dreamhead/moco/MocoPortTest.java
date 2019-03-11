package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoPortTest {
    private MocoTestHelper helper;

    @Test
    public void should_create_http_server_without_specific_port() throws Exception {
        final HttpServer server = Moco.httpServer();
        server.response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.root(server.port())), CoreMatchers.is("foo"));
            }
        });
    }

    @Test(expected = IllegalStateException.class)
    public void should_not_get_port_without_binding() throws Exception {
        final HttpServer server = Moco.httpServer();
        server.port();
    }
}

