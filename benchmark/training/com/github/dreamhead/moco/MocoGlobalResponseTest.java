package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.google.common.net.HttpHeaders;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoGlobalResponseTest {
    private HttpServer server;

    private MocoTestHelper helper = new MocoTestHelper();

    @Test
    public void should_return_all_response_for_version_with_header() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.response(Moco.header(HttpHeaders.CONTENT_TYPE, "text/plain")));
        server.response(Moco.version(HttpProtocolVersion.VERSION_1_0));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                Header header = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
                Assert.assertThat(header.getValue(), CoreMatchers.is("text/plain"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_content_with_header() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.response(Moco.header("foo", "bar")));
        server.response("hello");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                Header header = response.getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                response.getEntity().writeTo(outstream);
                Assert.assertThat(new String(outstream.toByteArray()), CoreMatchers.is("hello"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_header_with_header() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.response(Moco.header("foo", "bar")));
        server.response(Moco.header("blah", "param"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                Header header = response.getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_status_with_header() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.response(Moco.header("foo", "bar")));
        server.response(Moco.status(200));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                Header header = response.getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_and_response_handler_with_header() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.response(Moco.header("foo", "bar")));
        server.response(Moco.status(200), Moco.with(Moco.version(HttpProtocolVersion.VERSION_1_0)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                Header header = response.getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
            }
        });
    }

    // @Test
    // public void should_return_all_response_for_proxy_with_header() throws Exception {
    // server = httpServer(port(), response(header("foo", "bar")));
    // server.response(proxy("https://github.com/"));
    // 
    // running(server, new Runnable() {
    // @Override
    // public void run() throws Exception {
    // HttpResponse response = Request.Get(root()).execute().returnResponse();
    // Header header = response.getFirstHeader("foo");
    // assertThat(header.getValue(), is("bar"));
    // }
    // });
    // }
    @Test
    public void should_return_all_response_for_mount_with_header() throws Exception {
        String MOUNT_DIR = "src/test/resources/test";
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.response(Moco.header("foo", "bar")));
        server.mount(MOUNT_DIR, MocoMount.to("/dir"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(RemoteTestUtils.remoteUrl("/dir/dir.response"));
                Header header = response.getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_latency_with_header() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.response(Moco.header("foo", "bar")));
        server.response(Moco.latency(1, TimeUnit.SECONDS));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                Header header = response.getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_all_response_for_seq_with_header() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.response(Moco.header("foo", "bar")));
        server.response(Moco.seq("hello", "world"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                Header header = response.getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
                response = helper.getResponse(RemoteTestUtils.root());
                header = response.getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
            }
        });
    }
}

