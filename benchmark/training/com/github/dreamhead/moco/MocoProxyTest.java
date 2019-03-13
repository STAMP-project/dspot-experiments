package com.github.dreamhead.moco;


import ContentType.DEFAULT_TEXT;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class MocoProxyTest extends AbstractMocoHttpTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    // @Test
    // public void should_fetch_remote_url() throws Exception {
    // server.response(proxy("http://github.com/"));
    // 
    // running(server, new Runnable() {
    // @Override
    // public void run() throws IOException {
    // assertThat(helper.getForStatus(root()), is(200));
    // }
    // });
    // }
    @Test
    public void should_proxy_with_request_method() throws Exception {
        server.get(Moco.by(Moco.uri("/target"))).response("get_proxy");
        server.post(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("proxy"))).response("post_proxy");
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.method("put")), Moco.by("proxy"))).response("put_proxy");
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.method("delete")))).response("delete_proxy");
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.method("head")))).response(Moco.status(200));
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.method("options")))).response("options_proxy");
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.method("trace")))).response("trace_proxy");
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy")), CoreMatchers.is("get_proxy"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("post_proxy"));
                Request putRequest = Request.Put(RemoteTestUtils.remoteUrl("/proxy")).bodyString("proxy", DEFAULT_TEXT);
                Assert.assertThat(helper.executeAsString(putRequest), CoreMatchers.is("put_proxy"));
                Request deleteRequest = Request.Delete(RemoteTestUtils.remoteUrl("/proxy"));
                Assert.assertThat(helper.executeAsString(deleteRequest), CoreMatchers.is("delete_proxy"));
                Request headRequest = Request.Head(RemoteTestUtils.remoteUrl("/proxy"));
                StatusLine headStatusLine = helper.execute(headRequest).getStatusLine();
                Assert.assertThat(headStatusLine.getStatusCode(), CoreMatchers.is(200));
                Request optionsRequest = Request.Options(RemoteTestUtils.remoteUrl("/proxy"));
                Assert.assertThat(helper.executeAsString(optionsRequest), CoreMatchers.is("options_proxy"));
                Request traceRequest = Request.Trace(RemoteTestUtils.remoteUrl("/proxy"));
                Assert.assertThat(helper.executeAsString(traceRequest), CoreMatchers.is("trace_proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_request_header() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.eq(Moco.header("foo"), "foo"))).response("foo_proxy");
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.eq(Moco.header("bar"), "bar"))).response("bar_proxy");
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.remoteUrl("/proxy"), ImmutableMultimap.of("foo", "foo")), CoreMatchers.is("foo_proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_request_query_parameters() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.eq(Moco.query("foo"), "foo"))).response("foo_proxy");
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.eq(Moco.query("bar"), "bar"))).response("bar_proxy");
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy?foo=foo")), CoreMatchers.is("foo_proxy"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy?bar=bar")), CoreMatchers.is("bar_proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_response_headers() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.eq(Moco.header("foo"), "foo"))).response(Moco.header("foo", "foo_header"));
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.eq(Moco.header("bar"), "bar"))).response(Moco.header("bar", "bar_header"));
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Get(RemoteTestUtils.remoteUrl("/proxy")).addHeader("foo", "foo");
                String fooHeader = helper.execute(request).getFirstHeader("foo").getValue();
                Assert.assertThat(fooHeader, CoreMatchers.is("foo_header"));
            }
        });
    }

    @Test
    public void should_proxy_with_request_version() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.version(HttpProtocolVersion.VERSION_1_0)))).response("1.0");
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.version(HttpProtocolVersion.VERSION_1_1)))).response("1.1");
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getWithVersion(RemoteTestUtils.remoteUrl("/proxy"), HttpVersion.HTTP_1_0), CoreMatchers.is("1.0"));
                Assert.assertThat(helper.getWithVersion(RemoteTestUtils.remoteUrl("/proxy"), HttpVersion.HTTP_1_1), CoreMatchers.is("1.1"));
            }
        });
    }

    @Test
    public void should_proxy_with_response_version() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.version(HttpProtocolVersion.VERSION_1_0)))).response(Moco.version(HttpProtocolVersion.VERSION_1_0));
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.version(HttpProtocolVersion.VERSION_1_1)))).response(Moco.version(HttpProtocolVersion.VERSION_1_1));
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.version(HttpProtocolVersion.VERSION_0_9)))).response(Moco.version(HttpProtocolVersion.VERSION_1_0));
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response10 = helper.execute(Request.Get(RemoteTestUtils.remoteUrl("/proxy")).version(HttpVersion.HTTP_1_0));
                Assert.assertThat(response10.getProtocolVersion().toString(), CoreMatchers.is(HttpVersion.HTTP_1_0.toString()));
                HttpResponse response11 = helper.execute(Request.Get(RemoteTestUtils.remoteUrl("/proxy")).version(HttpVersion.HTTP_1_1));
                Assert.assertThat(response11.getProtocolVersion().toString(), CoreMatchers.is(HttpVersion.HTTP_1_1.toString()));
                HttpResponse response09 = helper.execute(Request.Get(RemoteTestUtils.remoteUrl("/proxy")).version(HttpVersion.HTTP_0_9));
                Assert.assertThat(response09.getProtocolVersion().toString(), CoreMatchers.is(HttpVersion.HTTP_1_0.toString()));
            }
        });
    }

    @Test
    public void should_failover_with_response_content() throws Exception {
        server.post(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("proxy"))).response("proxy");
        final File tempFile = tempFolder.newFile();
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target"), Moco.failover(tempFile.getAbsolutePath())));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("proxy"));
                Assert.assertThat(Files.toString(tempFile, Charset.defaultCharset()), CoreMatchers.containsString("proxy"));
            }
        });
    }

    @Test
    public void should_failover_with_many_response_content() throws Exception {
        server.get(Moco.by(Moco.uri("/target"))).response("get_proxy");
        server.post(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("proxy"))).response("post_proxy");
        final File tempFile = tempFolder.newFile();
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target"), Moco.failover(tempFile.getAbsolutePath())));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy")), CoreMatchers.is("get_proxy"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("post_proxy"));
                String failoverContent = Files.toString(tempFile, Charset.defaultCharset());
                Assert.assertThat(failoverContent, CoreMatchers.containsString("get_proxy"));
                Assert.assertThat(failoverContent, CoreMatchers.containsString("post_proxy"));
            }
        });
    }

    @Test
    public void should_failover_with_same_response_once() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port());
        server.post(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("proxy"))).response("0XCAFEBABE");
        final File tempFile = tempFolder.newFile();
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target"), Moco.failover(tempFile.getAbsolutePath())));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("0XCAFEBABE"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("0XCAFEBABE"));
                Assert.assertThat(Files.toString(tempFile, Charset.defaultCharset()), countString("/proxy", 1));
            }
        });
    }

    @Test
    public void should_failover_for_unreachable_remote_server() throws Exception {
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target"), Moco.failover("src/test/resources/failover.response")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("proxy"));
            }
        });
    }

    @Test
    public void should_failover_for_specified_status() throws Exception {
        server.request(Moco.by(Moco.uri("/target"))).response(Moco.seq(Moco.status(500), Moco.status(400)));
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target"), Moco.failover("src/test/resources/failover.response", 500, 400)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("proxy"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("proxy"));
            }
        });
    }

    @Test
    public void should_failover_for_specified_status_with_resource_proxy() throws Exception {
        server.request(Moco.by(Moco.uri("/target"))).response(Moco.seq(Moco.status(500), Moco.status(400)));
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(Moco.text(RemoteTestUtils.remoteUrl("/target")), Moco.failover("src/test/resources/failover.response", 500, 400)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("proxy"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("proxy"));
            }
        });
    }

    @Test
    public void should_failover_for_unreachable_remote_server_with_many_content() throws Exception {
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target"), Moco.failover("src/test/resources/many_content_failover.response")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy")), CoreMatchers.is("get_proxy"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy"), "proxy"), CoreMatchers.is("post_proxy"));
            }
        });
    }

    // @Test
    // public void should_be_able_to_connect_to_external_website_successfully() throws Exception {
    // server.response(proxy("http://www.baidu.com/"));
    // 
    // running(server, new Runnable() {
    // @Override
    // public void run() throws IOException {
    // assertThat(helper.getForStatus(root()), is(200));
    // }
    // });
    // }
    @Test
    public void should_proxy_a_batch_of_urls() throws Exception {
        server.get(Moco.by(Moco.uri("/target/1"))).response("target_1");
        server.get(Moco.by(Moco.uri("/target/2"))).response("target_2");
        server.get(Moco.match(Moco.uri("/proxy/.*"))).response(Moco.proxy(Moco.from("/proxy").to(RemoteTestUtils.remoteUrl("/target"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy/1")), CoreMatchers.is("target_1"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy/2")), CoreMatchers.is("target_2"));
            }
        });
    }

    @Test
    public void should_proxy_a_batch_of_urls_with_failover() throws Exception {
        server.request(Moco.match(Moco.uri("/proxy/.*"))).response(Moco.proxy(Moco.from("/proxy").to(RemoteTestUtils.remoteUrl("/target")), Moco.failover("src/test/resources/failover.response")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy/1"), "proxy"), CoreMatchers.is("proxy"));
            }
        });
    }

    @Test
    public void should_failover_for_batch_api_with_specified_status() throws Exception {
        server.request(Moco.by(Moco.uri("/target"))).response(Moco.seq(Moco.status(500), Moco.status(400)));
        server.request(Moco.match(Moco.uri("/proxy/.*"))).response(Moco.proxy(Moco.from("/proxy").to(RemoteTestUtils.remoteUrl("/target")), Moco.failover("src/test/resources/failover.response", 500, 400)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy/1"), "proxy"), CoreMatchers.is("proxy"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy/2"), "proxy"), CoreMatchers.is("proxy"));
            }
        });
    }

    @Test
    public void should_batch_proxy_from_server() throws Exception {
        server.get(Moco.by(Moco.uri("/target/1"))).response("target_1");
        server.get(Moco.by(Moco.uri("/target/2"))).response("target_2");
        server.proxy(Moco.from("/proxy").to(RemoteTestUtils.remoteUrl("/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy/1")), CoreMatchers.is("target_1"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy/2")), CoreMatchers.is("target_2"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_proxy_url_for_unmatching_url_for_batch_proxy_from_server() throws Exception {
        server.proxy(Moco.from("/proxy").to(RemoteTestUtils.remoteUrl("/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(RemoteTestUtils.remoteUrl("/proxy1/1"));
            }
        });
    }

    @Test
    public void should_batch_proxy_from_server_with_context_server() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.context("/proxy"));
        server.get(Moco.by(Moco.uri("/target/1"))).response("target_1");
        server.get(Moco.by(Moco.uri("/target/2"))).response("target_2");
        server.proxy(Moco.from("/proxy").to(RemoteTestUtils.remoteUrl("/proxy/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy/proxy/1")), CoreMatchers.is("target_1"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy/proxy/2")), CoreMatchers.is("target_2"));
            }
        });
    }

    @Test
    public void should_proxy_a_batch_of_urls_with_failover_from_server() throws Exception {
        server.proxy(Moco.from("/proxy").to(RemoteTestUtils.remoteUrl("/target")), Moco.failover("src/test/resources/failover.response"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/proxy/1"), "proxy"), CoreMatchers.is("proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_playback() throws Exception {
        server.request(Moco.by(Moco.uri("/target"))).response("proxy");
        final File file = tempFolder.newFile();
        server.request(Moco.by(Moco.uri("/proxy_playback"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target"), Moco.playback(file.getAbsolutePath())));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy_playback")), CoreMatchers.is("proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_playback_to_access_remote_only_once() throws Exception {
        RequestHit hit = MocoRequestHit.requestHit();
        server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.request(Moco.by(Moco.uri("/target"))).response("proxy");
        final File file = tempFolder.newFile();
        server.request(Moco.by(Moco.uri("/proxy_playback"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target"), Moco.playback(file.getAbsolutePath())));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy_playback")), CoreMatchers.is("proxy"));
                System.out.println("First request");
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy_playback")), CoreMatchers.is("proxy"));
                System.out.println("Second request");
            }
        });
        hit.verify(Moco.by(Moco.uri("/target")), MocoRequestHit.once());
    }

    @Test
    public void should_ignore_some_header_from_remote_server() throws Exception {
        server.request(Moco.by(Moco.uri("/target"))).response(Moco.with("proxy"), Moco.header("Date", "2014-5-1"), Moco.header("Server", "moco"));
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(RemoteTestUtils.remoteUrl("/target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.execute(Request.Get(RemoteTestUtils.remoteUrl("/proxy")));
                Assert.assertThat(response.getFirstHeader("Date"), CoreMatchers.nullValue());
                Assert.assertThat(response.getFirstHeader("Server"), CoreMatchers.nullValue());
            }
        });
    }

    // @Test
    // public void should_work_well_for_chunk_response() throws Exception {
    // final File file = tempFolder.newFile();
    // HttpServer server = httpServer(12306, context("/"));
    // server.get(match(uri("/repos/.*")))
    // .response(proxy(from("/repos").to("https://api.github.com/repos"), playback(file.getAbsolutePath())));
    // running(server, new Runnable() {
    // @Override
    // public void run() throws Exception {
    // String result = helper.get("http://localhost:12306/repos/HipByte/RubyMotion/contributors");
    // assertThat(result.isEmpty(), is(false));
    // }
    // });
    // }
    @Test
    public void should_work_with_file_resource_url() throws Exception {
        server.get(Moco.by(Moco.uri("/target"))).response("get_proxy");
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(Moco.file("src/test/resources/remote_url.resource")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy")), CoreMatchers.is("get_proxy"));
            }
        });
    }

    @Test
    public void should_work_with_template() throws Exception {
        server.get(Moco.by(Moco.uri("/target"))).response("get_proxy");
        server.request(Moco.by(Moco.uri("/proxy"))).response(Moco.proxy(Moco.template("http://localhost:12306/${var}", "var", "target")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/proxy")), CoreMatchers.is("get_proxy"));
            }
        });
    }

    @Test
    public void should_forward_gbk_request() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.log());
        final Charset gbk = Charset.forName("GBK");
        server.request(Moco.and(Moco.by(Moco.uri("/proxy")), Moco.by(Moco.json(Moco.pathResource("gbk.json", gbk))))).response("response");
        server.response(Moco.proxy(RemoteTestUtils.remoteUrl("/proxy")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                URL resource = Resources.getResource("gbk.json");
                byte[] bytes = ByteStreams.toByteArray(resource.openStream());
                String result = helper.postBytes(RemoteTestUtils.root(), bytes, gbk);
                Assert.assertThat(result, CoreMatchers.is("response"));
            }
        });
    }
}

