package com.baeldung.hystrix;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import ratpack.test.MainClassApplicationUnderTest;


/**
 *
 *
 * @author aiet
 */
public class RatpackHystrixAppFallbackLiveTest {
    static MainClassApplicationUnderTest appUnderTest;

    @Test
    public void whenFetchReactive_thenGotFallbackProfile() {
        Assert.assertThat(RatpackHystrixAppFallbackLiveTest.appUnderTest.getHttpClient().getText("rx"), CoreMatchers.containsString("reactive fallback profile"));
    }

    @Test
    public void whenFetchAsync_thenGotFallbackProfile() {
        Assert.assertThat(RatpackHystrixAppFallbackLiveTest.appUnderTest.getHttpClient().getText("async"), CoreMatchers.containsString("async fallback profile"));
    }

    @Test
    public void whenFetchSync_thenGotFallbackProfile() {
        Assert.assertThat(RatpackHystrixAppFallbackLiveTest.appUnderTest.getHttpClient().getText("sync"), CoreMatchers.containsString("sync fallback profile"));
    }
}

