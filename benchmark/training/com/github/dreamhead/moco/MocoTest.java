package com.github.dreamhead.moco;


import HttpMethod.GET;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoTest extends AbstractMocoHttpTest {
    @Test
    public void should_return_expected_response() throws Exception {
        server.response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_with_text_api() throws Exception {
        server.response(Moco.text("foo"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_with_content_api() throws Exception {
        server.response(Moco.with("foo"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_from_file() throws Exception {
        server.response(Moco.file("src/test/resources/foo.response"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_expected_response_from_file_with_charset() throws Exception {
        server.response(Moco.file("src/test/resources/gbk.response", Charset.forName("GBK")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getAsBytes(RemoteTestUtils.root()), CoreMatchers.is(Files.toByteArray(new File("src/test/resources/gbk.response"))));
            }
        });
    }

    @Test
    public void should_return_expected_response_from_path_resource() throws Exception {
        server.response(Moco.pathResource("foo.response"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_expected_response_from_path_resource_with_charset() throws Exception {
        server.response(Moco.pathResource("gbk.response", Charset.forName("GBK")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getAsBytes(RemoteTestUtils.root()), CoreMatchers.is(Files.toByteArray(new File("src/test/resources/gbk.response"))));
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_path_resource() throws Exception {
        server.request(Moco.by(Moco.pathResource("foo.request"))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                URL resource = Resources.getResource("foo.request");
                InputStream stream = resource.openStream();
                Assert.assertThat(helper.postStream(RemoteTestUtils.root(), stream), CoreMatchers.is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_for_unknown_request() throws Exception {
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_request() throws Exception {
        server.request(Moco.by("foo")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "foo"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_request_with_text_api() throws Exception {
        server.request(Moco.by(Moco.text("foo"))).response(Moco.text("bar"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "foo"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_request_with_charset_from_file() throws Exception {
        final Charset gbk = Charset.forName("GBK");
        server.request(Moco.by(Moco.file("src/test/resources/gbk.response", gbk))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postBytes(RemoteTestUtils.root(), Files.toByteArray(new File("src/test/resources/gbk.response")), gbk), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_uri() throws Exception {
        server.request(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_multiple_matchers() throws Exception {
        server.request(Moco.and(Moco.by("foo"), Moco.by(Moco.uri("/foo")))).response(Moco.text("bar"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/foo"), "foo"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_even_if_match_one_of_conditions() throws Exception {
        server.request(Moco.and(Moco.by("foo"), Moco.by(Moco.uri("/foo")))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(RemoteTestUtils.remoteUrl("/foo"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_either_matcher() throws Exception {
        server.request(Moco.or(Moco.by("foo"), Moco.by(Moco.uri("/foo")))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/foo"), "foo"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_not_matcher() throws Exception {
        server.request(Moco.not(Moco.by(Moco.uri("/foo")))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/bar")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_simplified_either_matcher() throws Exception {
        server.request(Moco.by("foo"), Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "foo"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_get_method_by_method_api_with_http_method() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/foo")), Moco.by(Moco.method(GET)))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_get_method_by_method_api() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/foo")), Moco.by(Moco.method("get")))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_response_for_get_while_http_method_is_not_get() throws Exception {
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(RemoteTestUtils.remoteUrl("/foo"), "");
            }
        });
    }

    @Test
    public void should_match_put_method_via_api() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/foo")), Moco.by(Moco.method("put")))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Put(RemoteTestUtils.remoteUrl("/foo"));
                Assert.assertThat(helper.executeAsString(request), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_delete_method_via_api() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/foo")), Moco.by(Moco.method("delete")))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Delete(RemoteTestUtils.remoteUrl("/foo"));
                String response = helper.executeAsString(request);
                Assert.assertThat(response, CoreMatchers.is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_response_for_post_while_http_method_is_not_post() throws Exception {
        server.post(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(RemoteTestUtils.remoteUrl("/foo"));
            }
        });
    }

    @Test
    public void should_return_content_one_by_one() throws Exception {
        server.request(Moco.by(Moco.uri("/foo"))).response(Moco.seq("bar", "blah"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("blah"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("blah"));
            }
        });
    }

    @Test
    public void should_return_content_one_by_one_with_text_api() throws Exception {
        server.request(Moco.by(Moco.uri("/foo"))).response(Moco.seq(Moco.text("bar"), Moco.text("blah")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("blah"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("blah"));
            }
        });
    }

    @Test
    public void should_return_response_one_by_one() throws Exception {
        server.request(Moco.by(Moco.uri("/foo"))).response(Moco.seq(Moco.status(302), Moco.status(302), Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(302));
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(302));
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_return_content_circularly_one_by_one() throws Exception {
        server.request(Moco.by(Moco.uri("/foo"))).response(Moco.cycle("bar", "blah"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("blah"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("blah"));
            }
        });
    }

    @Test
    public void should_return_content_one_by_one_with_text_api_circularly() throws Exception {
        server.request(Moco.by(Moco.uri("/foo"))).response(Moco.cycle(Moco.text("bar"), Moco.text("blah")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("blah"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("blah"));
            }
        });
    }

    @Test
    public void should_return_response_circularly_one_by_one() throws Exception {
        server.request(Moco.by(Moco.uri("/foo"))).response(Moco.cycle(Moco.status(302), Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(302));
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(200));
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(302));
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_match() throws Exception {
        server.request(Moco.match(Moco.uri("/\\w*/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/bar/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/blah/foo")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_method() throws Exception {
        server.request(Moco.match(Moco.method("get|post"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/bar/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/blah/foo"), "content"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_header() throws Exception {
        server.request(Moco.match(Moco.header("foo"), "bar|blah")).response("header");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "bar")), CoreMatchers.is("header"));
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "blah")), CoreMatchers.is("header"));
            }
        });
    }

    @Test
    public void should_exist_header() throws Exception {
        server.request(Moco.exist(Moco.header("foo"))).response("header");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "bar")), CoreMatchers.is("header"));
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "blah")), CoreMatchers.is("header"));
            }
        });
    }

    @Test
    public void should_exist_query() throws Exception {
        server.request(Moco.exist(Moco.query("foo"))).response("query");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/path?foo")), CoreMatchers.is("query"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/other?foo")), CoreMatchers.is("query"));
            }
        });
    }

    @Test
    public void should_starts_with() throws Exception {
        server.request(Moco.startsWith(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo/a")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo/b")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_starts_with_for_resource() throws Exception {
        server.request(Moco.startsWith(Moco.header("foo"), "bar")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "barA")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "barB")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_ends_with() throws Exception {
        server.request(Moco.endsWith(Moco.uri("foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/a/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/b/foo")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_ends_with_for_resource() throws Exception {
        server.request(Moco.endsWith(Moco.header("foo"), "bar")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "Abar")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "Bbar")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_contain() throws Exception {
        server.request(Moco.contain(Moco.uri("foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/a/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo/a")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_contain_for_resource() throws Exception {
        server.request(Moco.contain(Moco.header("foo"), "bar")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "Abar")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "barA")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_eq_header() throws Exception {
        server.request(Moco.eq(Moco.header("foo"), "bar")).response("blah");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "bar")), CoreMatchers.is("blah"));
            }
        });
    }

    @Test
    public void should_eq_multiple_header_with_same_name() throws Exception {
        server.request(Moco.and(Moco.eq(Moco.header("foo"), "bar")), Moco.eq(Moco.header("foo"), "bar2")).response("blah");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.root(), ImmutableMultimap.of("foo", "bar", "foo", "bar2")), CoreMatchers.is("blah"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_specified_header() throws Exception {
        server.request(Moco.eq(Moco.header("foo"), "bar")).response("blah");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(RemoteTestUtils.remoteUrl("/foo"));
            }
        });
    }

    @Test
    public void should_set_multiple_header_with_same_name() throws Exception {
        server.response(Moco.header("foo", "bar"), Moco.header("foo", "another"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                Header[] headers = response.getHeaders("foo");
                Assert.assertThat(headers.length, CoreMatchers.is(2));
                Assert.assertThat(headers[0].getValue(), CoreMatchers.is("bar"));
                Assert.assertThat(headers[1].getValue(), CoreMatchers.is("another"));
            }
        });
    }

    @Test
    public void should_return_expected_response_for_multiple_specified_query() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/foo")), Moco.eq(Moco.query("param"), "blah"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo?param=multiple&param=blah")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_response_for_specified_query() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/foo")), Moco.eq(Moco.query("param"), "blah"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo?param=blah")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_response_for_empty_query() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/foo")), Moco.eq(Moco.query("param"), ""))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo?param")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_version() throws Exception {
        server.request(Moco.by(Moco.version(HttpProtocolVersion.VERSION_1_0))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getWithVersion(RemoteTestUtils.root(), HttpVersion.HTTP_1_0), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_version() throws Exception {
        server.response(Moco.version(HttpProtocolVersion.VERSION_1_0));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                ProtocolVersion version = helper.getResponse(RemoteTestUtils.root()).getProtocolVersion();
                Assert.assertThat(version.getMajor(), CoreMatchers.is(1));
                Assert.assertThat(version.getMinor(), CoreMatchers.is(0));
            }
        });
    }

    @Test
    public void should_return_excepted_version_with_version_api() throws Exception {
        server.response(Moco.version(HttpProtocolVersion.VERSION_1_0));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                ProtocolVersion version = helper.getResponse(RemoteTestUtils.root()).getProtocolVersion();
                Assert.assertThat(version.getMajor(), CoreMatchers.is(1));
                Assert.assertThat(version.getMinor(), CoreMatchers.is(0));
            }
        });
    }

    @Test
    public void should_return_expected_status_code() throws Exception {
        server.response(Moco.status(200));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.root()), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_return_expected_header() throws Exception {
        server.response(Moco.header(HttpHeaders.CONTENT_TYPE, "application/json"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String value = helper.getResponse(RemoteTestUtils.root()).getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                Assert.assertThat(value, CoreMatchers.is("application/json"));
            }
        });
    }

    @Test
    public void should_return_multiple_expected_header() throws Exception {
        server.response(Moco.header(HttpHeaders.CONTENT_TYPE, "application/json"), Moco.header("foo", "bar"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String json = helper.getResponse(RemoteTestUtils.root()).getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                Assert.assertThat(json, CoreMatchers.is("application/json"));
                String bar = helper.getResponse(RemoteTestUtils.root()).getFirstHeader("foo").getValue();
                Assert.assertThat(bar, CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_multiple_expected_header_with_same_name() throws Exception {
        server.response(Moco.header("foo", "bar"), Moco.header("foo", "moco"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Header[] headers = helper.getResponse(RemoteTestUtils.root()).getHeaders("foo");
                Assert.assertThat(headers.length, CoreMatchers.is(2));
                Assert.assertThat(headers[0].getValue(), CoreMatchers.is("bar"));
                Assert.assertThat(headers[1].getValue(), CoreMatchers.is("moco"));
            }
        });
    }

    @Test
    public void should_wait_for_awhile() throws Exception {
        final long latency = 1000;
        final long delta = 200;
        server.response(Moco.latency(latency, TimeUnit.MILLISECONDS));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                long start = System.currentTimeMillis();
                helper.get(RemoteTestUtils.root());
                int code = helper.getForStatus(RemoteTestUtils.root());
                long stop = System.currentTimeMillis();
                long gap = (stop - start) + delta;
                Assert.assertThat(gap, Matchers.greaterThan(latency));
                Assert.assertThat(code, CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_wait_for_awhile_with_time_unit() throws Exception {
        final long delta = 200;
        server.response(Moco.latency(1, TimeUnit.SECONDS));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                long start = System.currentTimeMillis();
                helper.get(RemoteTestUtils.root());
                int code = helper.getForStatus(RemoteTestUtils.root());
                long stop = System.currentTimeMillis();
                long gap = (stop - start) + delta;
                Assert.assertThat(gap, Matchers.greaterThan(TimeUnit.SECONDS.toMillis(1)));
                Assert.assertThat(code, CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_return_same_http_version_without_specified_version() throws Exception {
        server.response("foobar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                ProtocolVersion version10 = helper.execute(Request.Get(RemoteTestUtils.root()).version(HttpVersion.HTTP_1_0)).getProtocolVersion();
                Assert.assertThat(version10.getMajor(), CoreMatchers.is(1));
                Assert.assertThat(version10.getMinor(), CoreMatchers.is(0));
                ProtocolVersion version11 = helper.execute(Request.Get(RemoteTestUtils.root()).version(HttpVersion.HTTP_1_1)).getProtocolVersion();
                Assert.assertThat(version11.getMajor(), CoreMatchers.is(1));
                Assert.assertThat(version11.getMinor(), CoreMatchers.is(1));
            }
        });
    }

    @Test
    public void should_return_same_http_version_without_specified_version_for_error_response() throws Exception {
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                ProtocolVersion version10 = helper.execute(Request.Get(RemoteTestUtils.root()).version(HttpVersion.HTTP_1_0)).getProtocolVersion();
                Assert.assertThat(version10.getMajor(), CoreMatchers.is(1));
                Assert.assertThat(version10.getMinor(), CoreMatchers.is(0));
                ProtocolVersion version11 = helper.execute(Request.Get(RemoteTestUtils.root()).version(HttpVersion.HTTP_1_1)).getProtocolVersion();
                Assert.assertThat(version11.getMajor(), CoreMatchers.is(1));
                Assert.assertThat(version11.getMinor(), CoreMatchers.is(1));
            }
        });
    }

    @Test
    public void should_return_default_content_type() throws Exception {
        server.response(Moco.with("foo"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = helper.getResponse(RemoteTestUtils.root()).getFirstHeader(HttpHeaders.CONTENT_TYPE);
                Assert.assertThat(header.getValue(), CoreMatchers.is("text/plain; charset=utf-8"));
            }
        });
    }

    @Test
    public void should_return_specified_content_type() throws Exception {
        server.response(Moco.with("foo"), Moco.header(HttpHeaders.CONTENT_TYPE, "text/html"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = helper.getResponse(RemoteTestUtils.root()).getFirstHeader(HttpHeaders.CONTENT_TYPE);
                Assert.assertThat(header.getValue(), CoreMatchers.is("text/html"));
            }
        });
    }

    @Test
    public void should_return_specified_content_type_no_matter_order() throws Exception {
        server.response(Moco.header(HttpHeaders.CONTENT_TYPE, "text/html"), Moco.with("foo"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = helper.getResponse(RemoteTestUtils.root()).getFirstHeader(HttpHeaders.CONTENT_TYPE);
                Assert.assertThat(header.getValue(), CoreMatchers.is("text/html"));
            }
        });
    }

    @Test
    public void should_return_specified_content_type_with_case_insensitive() throws Exception {
        server.response(Moco.header("content-type", "text/html"), Moco.with("foo"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header[] headers = helper.getResponse(RemoteTestUtils.root()).getHeaders(HttpHeaders.CONTENT_TYPE);
                Assert.assertThat(headers.length, CoreMatchers.is(1));
            }
        });
    }

    @Test
    public void should_return_response_with_and_handler() throws Exception {
        server.request(Moco.by(Moco.uri("/foo"))).response(Moco.seq(Moco.and(Moco.with(Moco.text("foo")), Moco.status(302)), Moco.and(Moco.with(Moco.text("bar")), Moco.status(302)), Moco.and(Moco.with(Moco.text("run")), Moco.status(200))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(302));
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(302));
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is(200));
            }
        });
    }
}

