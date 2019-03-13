package com.github.dreamhead.moco.handler.failover;


import HttpMethod.POST;
import HttpProtocolVersion.VERSION_1_1;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.google.common.collect.ImmutableMap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpRequestMatcherTest {
    @Test
    public void should_be_match_if_request_is_same() {
        HttpRequest request = DefaultHttpRequest.builder().withUri("/uri").withVersion(VERSION_1_1).withMethod(POST).withContent("proxy").withHeaders(ImmutableMap.of("Cookie", "loggedIn=true", "Host", "localhost:12306")).build();
        Assert.assertThat(new com.github.dreamhead.moco.model.HttpRequestFailoverMatcher(request).match(request), CoreMatchers.is(true));
    }

    @Test
    public void should_not_be_match_if_request_is_different() {
        HttpRequest request = DefaultHttpRequest.builder().withUri("/uri").withVersion(VERSION_1_1).withMethod(POST).withContent("proxy").withHeaders(ImmutableMap.of("Cookie", "loggedIn=true", "Host", "localhost:12306")).build();
        HttpRequest another = DefaultHttpRequest.builder().withUri("/uri").withVersion(VERSION_1_1).withMethod(POST).withContent("different").withHeaders(ImmutableMap.of("Cookie", "loggedIn=true", "Host", "localhost:12306")).build();
        Assert.assertThat(new com.github.dreamhead.moco.model.HttpRequestFailoverMatcher(request).match(another), CoreMatchers.is(false));
    }

    @Test
    public void should_not_be_match_if_uri_is_different() {
        HttpRequest request = DefaultHttpRequest.builder().withUri("/uri").withVersion(VERSION_1_1).withMethod(POST).withContent("proxy").withUri("/foo").withHeaders(ImmutableMap.of("Cookie", "loggedIn=true", "Host", "localhost:12306")).build();
        HttpRequest another = DefaultHttpRequest.builder().withUri("/uri").withVersion(VERSION_1_1).withMethod(POST).withContent("proxy").withUri("/bar").withHeaders(ImmutableMap.of("Cookie", "loggedIn=true", "Host", "localhost:12306")).build();
        Assert.assertThat(new com.github.dreamhead.moco.model.HttpRequestFailoverMatcher(request).match(another), CoreMatchers.is(false));
    }

    @Test
    public void should_be_match_if_failover_field_is_null() {
        HttpRequest request = DefaultHttpRequest.builder().withUri("/uri").withVersion(VERSION_1_1).withMethod(POST).withContent("proxy").withHeaders(ImmutableMap.of("Cookie", "loggedIn=true", "Host", "localhost:12306")).build();
        HttpRequest failover = DefaultHttpRequest.builder().withUri("/uri").withMethod(POST).withContent("proxy").withHeaders(ImmutableMap.of("Cookie", "loggedIn=true", "Host", "localhost:12306")).build();
        Assert.assertThat(new com.github.dreamhead.moco.model.HttpRequestFailoverMatcher(failover).match(request), CoreMatchers.is(true));
    }

    @Test
    public void should_be_match_even_if_target_request_has_more_headers() {
        HttpRequest request = DefaultHttpRequest.builder().withUri("/uri").withVersion(VERSION_1_1).withMethod(POST).withContent("proxy").withHeaders(ImmutableMap.of("Cookie", "loggedIn=true", "Host", "localhost:12306")).build();
        HttpRequest failover = DefaultHttpRequest.builder().withUri("/uri").withVersion(VERSION_1_1).withMethod(POST).withContent("proxy").withHeaders(ImmutableMap.of("Host", "localhost:12306")).build();
        Assert.assertThat(new com.github.dreamhead.moco.model.HttpRequestFailoverMatcher(failover).match(request), CoreMatchers.is(true));
    }
}

