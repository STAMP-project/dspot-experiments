package com.github.dreamhead.moco;


import ClientCookieDecoder.STRICT;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoWebTest extends AbstractMocoHttpTest {
    @Test
    public void should_match_form_value() throws Exception {
        server.post(Moco.eq(Moco.form("name"), "dreamhead")).response("foobar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Request request = Request.Post(RemoteTestUtils.root()).bodyForm(new BasicNameValuePair("name", "dreamhead"));
                Assert.assertThat(helper.executeAsString(request), CoreMatchers.is("foobar"));
            }
        });
    }

    @Test
    public void should_match_form_value_with_charset() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.log());
        server.post(Moco.eq(Moco.form("name"), "??")).response("foobar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Request request = Request.Post(RemoteTestUtils.root()).bodyForm(ImmutableSet.of(new BasicNameValuePair("name", "??")), Charset.forName("GBK"));
                Assert.assertThat(helper.executeAsString(request), CoreMatchers.is("foobar"));
            }
        });
    }

    @Test
    public void should_no_exception_form_get_request() throws Exception {
        server.request(Moco.eq(Moco.form("password"), "hello")).response("foobar");
        server.response("foobar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foobar"));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie() throws Exception {
        server.request(Moco.eq(Moco.cookie("loggedIn"), "true")).response(Moco.status(200));
        server.response(Moco.cookie("loggedIn", "true"), Moco.status(302));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.root()), CoreMatchers.is(302));
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.root()), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_path() throws Exception {
        server.request(Moco.eq(Moco.cookie("loggedIn"), "true")).response(Moco.status(200));
        server.response(Moco.cookie("loggedIn", "true", CookieAttribute.path("/")), Moco.status(302));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = STRICT.decode(value);
                Assert.assertThat(decodeCookie.path(), CoreMatchers.is("/"));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_max_age() throws Exception {
        server.request(Moco.eq(Moco.cookie("loggedIn"), "true")).response(Moco.status(200));
        server.response(Moco.cookie("loggedIn", "true", CookieAttribute.maxAge(1, TimeUnit.HOURS)), Moco.status(302));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = STRICT.decode(value);
                Assert.assertThat(decodeCookie.maxAge(), CoreMatchers.is(3600L));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_secure() throws Exception {
        server.request(Moco.eq(Moco.cookie("loggedIn"), "true")).response(Moco.status(200));
        server.response(Moco.cookie("loggedIn", "true", CookieAttribute.secure()), Moco.status(302));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = STRICT.decode(value);
                Assert.assertThat(decodeCookie.isSecure(), CoreMatchers.is(true));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_httponly() throws Exception {
        server.request(Moco.eq(Moco.cookie("loggedIn"), "true")).response(Moco.status(200));
        server.response(Moco.cookie("loggedIn", "true", CookieAttribute.httpOnly()), Moco.status(302));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = STRICT.decode(value);
                Assert.assertThat(decodeCookie.isHttpOnly(), CoreMatchers.is(true));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_domain() throws Exception {
        server.request(Moco.eq(Moco.cookie("loggedIn"), "true")).response(Moco.status(200));
        server.response(Moco.cookie("loggedIn", "true", CookieAttribute.domain("localhost")), Moco.status(302));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(RemoteTestUtils.root());
                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = STRICT.decode(value);
                Assert.assertThat(decodeCookie.domain(), CoreMatchers.is("localhost"));
            }
        });
    }

    @Test
    public void should_redirect_to_expected_url() throws Exception {
        server.get(Moco.by(Moco.uri("/"))).response("foo");
        server.get(Moco.by(Moco.uri("/redirectTo"))).redirectTo(RemoteTestUtils.root());
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/redirectTo")), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_redirect_for_any_response() throws Exception {
        server.get(Moco.by(Moco.uri("/"))).response("foo");
        server.redirectTo(RemoteTestUtils.root());
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/redirectTo")), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_download_attachment() throws Exception {
        server.get(Moco.by(Moco.uri("/"))).response(Moco.attachment("foo.txt", Moco.file("src/test/resources/foo.response")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/")), CoreMatchers.is("foo.response"));
            }
        });
    }
}

