package com.github.dreamhead.moco.internal;


import com.github.dreamhead.moco.AbstractMocoHttpTest;
import com.github.dreamhead.moco.BaseMocoHttpTest;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.Runner;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import java.io.IOException;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ActualHttpServerTest extends AbstractMocoHttpTest {
    private HttpServer httpServer;

    private HttpServer anotherServer;

    @Test
    public void should_merge_http_server_with_any_handler_one_side() throws Exception {
        HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo/anything")), CoreMatchers.is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_for_merging_http_server_with_any_handler_one_side() throws Exception {
        HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(RemoteTestUtils.remoteUrl("/bar/anything"));
            }
        });
    }

    @Test
    public void should_merge_http_server_with_any_handler_other_side() throws Exception {
        HttpServer mergedServer = mergeServer(((ActualHttpServer) (anotherServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo/anything")), CoreMatchers.is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_for_merging_http_server_with_any_handler_other_side() throws Exception {
        HttpServer mergedServer = mergeServer(((ActualHttpServer) (anotherServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(RemoteTestUtils.remoteUrl("/bar/anything"));
            }
        });
    }

    @Test
    public void should_config_handler_correctly_while_merging() throws Exception {
        httpServer = Moco.httpServer(12306, Moco.fileRoot("src/test/resources"));
        httpServer.response(Moco.file("foo.response"));
        HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo.response"));
            }
        });
    }

    @Test
    public void should_config_handler_correctly_other_side_while_merging() throws Exception {
        httpServer = Moco.httpServer(12306, Moco.fileRoot("src/test/resources"));
        httpServer.response(Moco.file("foo.response"));
        HttpServer mergedServer = mergeServer(((ActualHttpServer) (anotherServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo.response"));
            }
        });
    }

    private final HttpsCertificate DEFAULT_CERTIFICATE = HttpsCertificate.certificate(Moco.pathResource("cert.jks"), "mocohttps", "mocohttps");

    @Test
    public void should_merge_https_server() throws Exception {
        anotherServer = Moco.httpsServer(12306, DEFAULT_CERTIFICATE, Moco.context("/bar"));
        HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteHttpsUrl("/foo/anything")), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_merge_two_https_servers() throws Exception {
        httpServer = Moco.httpsServer(12306, DEFAULT_CERTIFICATE, Moco.context("/foo"));
        httpServer.response("foo");
        anotherServer = Moco.httpsServer(12306, DEFAULT_CERTIFICATE, Moco.context("/bar"));
        anotherServer.request(Moco.by(Moco.uri("/bar"))).response("bar");
        HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteHttpsUrl("/foo/anything")), CoreMatchers.is("foo"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteHttpsUrl("/bar/bar")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_merge_https_server_into_http_server() throws Exception {
        httpServer = Moco.httpsServer(12306, DEFAULT_CERTIFICATE, Moco.context("/foo"));
        httpServer.response("foo");
        HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteHttpsUrl("/foo/anything")), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_merge_http_server_with_same_port() throws Exception {
        httpServer = Moco.httpServer(12306, Moco.context("/foo"));
        anotherServer = Moco.httpServer(12306, Moco.context("/bar"));
        final HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(mergedServer.port(), CoreMatchers.is(12306));
            }
        });
    }

    @Test
    public void should_merge_http_server_with_different_port() throws Exception {
        httpServer = Moco.httpServer(12306, Moco.context("/foo"));
        anotherServer = Moco.httpServer(12307, Moco.context("/bar"));
        final HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(mergedServer.port(), CoreMatchers.is(12307));
            }
        });
    }

    @Test
    public void should_merge_http_server_without_port_for_first_server() throws Exception {
        httpServer = Moco.httpServer(12306, Moco.context("/foo"));
        anotherServer = Moco.httpServer(Moco.context("/bar"));
        final HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(mergedServer.port(), CoreMatchers.is(12306));
            }
        });
    }

    @Test
    public void should_merge_http_server_without_port_for_second_server() throws Exception {
        httpServer = Moco.httpServer(Moco.context("/foo"));
        anotherServer = Moco.httpServer(12307, Moco.context("/bar"));
        final HttpServer mergedServer = mergeServer(((ActualHttpServer) (httpServer)));
        Runner.running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(mergedServer.port(), CoreMatchers.is(12307));
            }
        });
    }

    @Test
    public void should_merge_http_server_without_port_for_both_servers() throws Exception {
        httpServer = Moco.httpServer(Moco.context("/foo"));
        anotherServer = Moco.httpServer(Moco.context("/bar"));
        final ActualHttpServer mergedServer = ((ActualHttpServer) (anotherServer)).mergeServer(((ActualHttpServer) (httpServer)));
        Assert.assertThat(mergedServer.getPort().isPresent(), CoreMatchers.is(false));
    }
}

