package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.google.common.collect.ImmutableMultimap;
import java.io.IOException;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoGlobalRequestTest extends AbstractMocoHttpTest {
    @Test
    public void should_match_global_header() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.request(Moco.by(Moco.uri("/global-request"))).response("blah");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String result = helper.getWithHeader(RemoteTestUtils.remoteUrl("/global-request"), ImmutableMultimap.of("foo", "bar"));
                Assert.assertThat(result, CoreMatchers.is("blah"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_global_matcher() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.request(Moco.by(Moco.uri("/global-request"))).response("blah");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String result = helper.get(RemoteTestUtils.remoteUrl("/global-request"));
                Assert.assertThat(result, CoreMatchers.is("blah"));
            }
        });
    }

    @Test
    public void should_match_global_header_with_any_response() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.response("blah");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String result = helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "bar"));
                Assert.assertThat(result, CoreMatchers.is("blah"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_global_matcher_for_any_response() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.response("blah");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(RemoteTestUtils.root());
            }
        });
    }

    @Test
    public void should_match_with_exist_header() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.request(Moco.exist(Moco.header("blah"))).response("header");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "bar", "blah", "any")), CoreMatchers.is("header"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_global_matcher_for_exist() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.request(Moco.exist(Moco.header("blah"))).response("header");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("blah", "any"));
            }
        });
    }

    @Test
    public void should_match_with_json() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.by(Moco.uri("/path"))));
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(Moco.by(Moco.json(jsonContent))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/path"), jsonContent), CoreMatchers.is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_match_json() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.by(Moco.uri("/path"))));
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(Moco.by(Moco.json(jsonContent))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(RemoteTestUtils.root(), jsonContent);
            }
        });
    }

    @Test
    public void should_match_with_xml() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.by(Moco.uri("/path"))));
        server.request(Moco.xml("<request><parameters><id>1</id></parameters></request>")).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postFile(RemoteTestUtils.remoteUrl("/path"), "foo.xml"), CoreMatchers.is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_match_xml() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.by(Moco.uri("/path"))));
        server.request(Moco.xml("<request><parameters><id>1</id></parameters></request>")).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postFile(RemoteTestUtils.root(), "foo.xml");
            }
        });
    }

    @Test
    public void should_match_mount() throws Exception {
        final String MOUNT_DIR = "src/test/resources/test";
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.mount(MOUNT_DIR, MocoMount.to("/dir"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.remoteUrl("/dir/dir.response"), ImmutableMultimap.of("foo", "bar")), CoreMatchers.is("response from dir"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_match_mount() throws Exception {
        final String MOUNT_DIR = "src/test/resources/test";
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.mount(MOUNT_DIR, MocoMount.to("/dir"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/dir/dir.response")), CoreMatchers.is("response from dir"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_not_matcher() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.request(Moco.not(Moco.by(Moco.uri("/foo")))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.remoteUrl("/bar"), ImmutableMultimap.of("foo", "bar")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_match_not() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.request(Moco.not(Moco.by(Moco.uri("/foo")))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(RemoteTestUtils.remoteUrl("/bar"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_and_matcher() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.request(Moco.and(Moco.by(Moco.uri("/foo")), Moco.eq(Moco.header("header"), "blah"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.remoteUrl("/foo"), ImmutableMultimap.of("foo", "bar", "header", "blah")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_match_and_matcher() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.request(Moco.eq(Moco.header("foo"), "bar")));
        server.request(Moco.and(Moco.by(Moco.uri("/foo")), Moco.eq(Moco.header("header"), "blah"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.getWithHeader(RemoteTestUtils.remoteUrl("/foo"), ImmutableMultimap.of("header", "blah"));
            }
        });
    }
}

