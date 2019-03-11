package com.github.dreamhead.moco;


import io.netty.handler.codec.http.cookie.Cookie;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoCookieStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_set_and_recognize_cookie() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie");
        Assert.assertThat(decodeCookie.name(), CoreMatchers.is("login"));
        Assert.assertThat(decodeCookie.value(), CoreMatchers.is("true"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_path() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-path");
        Assert.assertThat(decodeCookie.name(), CoreMatchers.is("login"));
        Assert.assertThat(decodeCookie.value(), CoreMatchers.is("true"));
        Assert.assertThat(decodeCookie.path(), CoreMatchers.is("/"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_domain() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-domain");
        Assert.assertThat(decodeCookie.name(), CoreMatchers.is("login"));
        Assert.assertThat(decodeCookie.value(), CoreMatchers.is("true"));
        Assert.assertThat(decodeCookie.domain(), CoreMatchers.is("github.com"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_secure() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-secure");
        Assert.assertThat(decodeCookie.name(), CoreMatchers.is("login"));
        Assert.assertThat(decodeCookie.value(), CoreMatchers.is("true"));
        Assert.assertThat(decodeCookie.isSecure(), CoreMatchers.is(true));
    }

    @Test
    public void should_set_and_recognize_cookie_with_http_only() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-http-only");
        Assert.assertThat(decodeCookie.name(), CoreMatchers.is("login"));
        Assert.assertThat(decodeCookie.value(), CoreMatchers.is("true"));
        Assert.assertThat(decodeCookie.isHttpOnly(), CoreMatchers.is(true));
    }

    @Test
    public void should_set_and_recognize_cookie_with_max_age() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-max-age");
        Assert.assertThat(decodeCookie.name(), CoreMatchers.is("login"));
        Assert.assertThat(decodeCookie.value(), CoreMatchers.is("true"));
        Assert.assertThat(decodeCookie.maxAge(), CoreMatchers.is(3600L));
    }
}

