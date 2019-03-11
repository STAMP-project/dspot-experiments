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
public class RatpackHystrixAppLiveTest {
    static MainClassApplicationUnderTest appUnderTest;

    @Test
    public void whenFetchReactive_thenGotEugenProfile() {
        Assert.assertThat(RatpackHystrixAppLiveTest.appUnderTest.getHttpClient().getText("rx"), CoreMatchers.containsString("www.baeldung.com"));
    }

    @Test
    public void whenFetchAsync_thenGotEugenProfile() {
        Assert.assertThat(RatpackHystrixAppLiveTest.appUnderTest.getHttpClient().getText("async"), CoreMatchers.containsString("www.baeldung.com"));
    }

    @Test
    public void whenFetchSync_thenGotEugenProfile() {
        Assert.assertThat(RatpackHystrixAppLiveTest.appUnderTest.getHttpClient().getText("sync"), CoreMatchers.containsString("www.baeldung.com"));
    }
}

