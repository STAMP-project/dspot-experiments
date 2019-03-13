package com.github.dreamhead.moco.internal;


import com.github.dreamhead.moco.RequestMatcher;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class InternalApisTest {
    @Test
    public void should_create_context_correctly() {
        RequestMatcher matcher = InternalApis.context("targets");
        Assert.assertThat(matcher.match(requestByUri("targets/hello")), CoreMatchers.is(true));
        Assert.assertThat(matcher.match(requestByUri("targets")), CoreMatchers.is(true));
    }

    @Test
    public void should_not_match_mismatch_uri() {
        RequestMatcher matcher = InternalApis.context("targets");
        Assert.assertThat(matcher.match(requestByUri("something")), CoreMatchers.is(false));
        Assert.assertThat(matcher.match(requestByUri("targetshello")), CoreMatchers.is(false));
    }
}

