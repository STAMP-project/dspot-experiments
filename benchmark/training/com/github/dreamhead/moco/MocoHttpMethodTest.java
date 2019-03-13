package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import java.io.IOException;
import org.apache.http.client.fluent.Request;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoHttpMethodTest extends AbstractMocoHttpTest {
    @Test
    public void should_match_get_method() throws Exception {
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_post_method() throws Exception {
        server.post(Moco.by("foo")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "foo"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_put_method() throws Exception {
        server.put(Moco.by("foo")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Put(RemoteTestUtils.root()).bodyByteArray("foo".getBytes());
                Assert.assertThat(helper.executeAsString(request), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_delete_method() throws Exception {
        server.delete(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Delete(RemoteTestUtils.remoteUrl("/foo"));
                String response = helper.executeAsString(request);
                Assert.assertThat(response, CoreMatchers.is("bar"));
            }
        });
    }
}

