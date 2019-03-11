package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoFileRootTest {
    private HttpServer server;

    private MocoTestHelper helper;

    @Test
    public void should_config_file_root() throws Exception {
        server.response(Moco.file("foo.response"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_header_from_file_root() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.log(), Moco.fileRoot("src/test/resources"));
        server.response(Moco.header("foo", Moco.file("foo.response")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Get(RemoteTestUtils.root());
                Header header = helper.execute(request).getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_template_header_from_file_root() throws Exception {
        server.response(Moco.header("foo", Moco.template(Moco.file("foo.response"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                Header header = response.getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_template_from_file_root() throws Exception {
        server.response(Moco.template(Moco.file("foo.response")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo.response"));
            }
        });
    }

    @Test
    public void should_mount_correctly() throws Exception {
        server.mount("test", MocoMount.to("/dir"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/dir/dir.response")), CoreMatchers.is("response from dir"));
            }
        });
    }
}

