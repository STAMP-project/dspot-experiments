package com.navercorp.pinpoint.bootstrap.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Naver on 2015-11-17.
 */
public class InterceptorUtilsTest {
    @Test
    public void getHttpUrl() {
        Assert.assertEquals("/", InterceptorUtils.getHttpUrl("/", true));
        Assert.assertEquals("/", InterceptorUtils.getHttpUrl("/", false));
        Assert.assertEquals("/pinpoint.get?foo=bar", InterceptorUtils.getHttpUrl("/pinpoint.get?foo=bar", true));
        Assert.assertEquals("/pinpoint.get", InterceptorUtils.getHttpUrl("/pinpoint.get?foo=bar", false));
        Assert.assertEquals("http://google.com?foo=bar", InterceptorUtils.getHttpUrl("http://google.com?foo=bar", true));
        Assert.assertEquals("http://google.com", InterceptorUtils.getHttpUrl("http://google.com?foo=bar", false));
        Assert.assertEquals("http://google.com?foo=bar", InterceptorUtils.getHttpUrl("http://google.com?foo=bar", true));
        Assert.assertEquals("http://google.com", InterceptorUtils.getHttpUrl("http://google.com?foo=bar", false));
        Assert.assertEquals("https://google.com#foo", InterceptorUtils.getHttpUrl("https://google.com#foo", true));
        Assert.assertEquals("https://google.com#foo", InterceptorUtils.getHttpUrl("https://google.com#foo", false));
    }
}

