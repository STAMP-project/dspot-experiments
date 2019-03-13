package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import java.io.IOException;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoContextTest {
    private static final String MOUNT_DIR = "src/test/resources/test";

    private HttpServer server;

    private MocoTestHelper helper;

    @Test
    public void should_config_context() throws Exception {
        server.get(Moco.by(Moco.uri("/foo"))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/context/foo")), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_mount_correctly() throws Exception {
        server.mount(MocoContextTest.MOUNT_DIR, MocoMount.to("/dir"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/context/dir/dir.response")), CoreMatchers.is("response from dir"));
            }
        });
    }

    @Test
    public void should_have_context_even_if_there_is_no_context_configured() throws Exception {
        server.response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String content = helper.get(RemoteTestUtils.remoteUrl("/context"));
                Assert.assertThat(content, CoreMatchers.is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_context() throws Exception {
        server.request(Moco.by("foo")).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.postContent(RemoteTestUtils.root(), "foo");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_context_for_any_response_handler() throws Exception {
        server.response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(RemoteTestUtils.root());
            }
        });
    }
}

