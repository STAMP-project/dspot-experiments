package com.twilio.http;


public class AmplRequestTest {
    @org.junit.Test
    public void testConstructorWithDomain() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.IPMESSAGING.toString(), "/v1/uri");
        org.junit.Assert.assertNotNull(request);
        org.junit.Assert.assertEquals(com.twilio.http.HttpMethod.GET, request.getMethod());
        org.junit.Assert.assertEquals("https://ip-messaging.twilio.com/v1/uri", request.getUrl());
    }

    @org.junit.Test
    public void testConstructURL() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test(expected = com.twilio.exception.ApiException.class)
    public void testConstructURLURISyntaxException() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "http://{");
        request.constructURL();
        org.junit.Assert.fail("ApiException was expected");
    }

    @org.junit.Test
    public void testConstructURLWithParam() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryParam("baz", "quux");
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testConstructURLWithParams() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryParam("baz", "quux");
        r.addQueryParam("garply", "xyzzy");
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testConstructURLWithMultivaluedParam() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryParam("baz", "quux");
        r.addQueryParam("baz", "xyzzy");
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testConstructURLWithInequalityParam() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryParam("baz>", "3");
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testAddQueryDateRangeLowerBound() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryDateRange("baz", com.google.common.collect.Range.greaterThan(new org.joda.time.LocalDate(2014, 1, 1)));
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testAddQueryDateRangeUpperBound() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryDateRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.LocalDate(2014, 1, 1)));
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testAddQueryDateRangeClosed() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryDateRange("baz", com.google.common.collect.Range.closed(new org.joda.time.LocalDate(2014, 1, 10), new org.joda.time.LocalDate(2014, 6, 1)));
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testAddQueryDateTimeRangeLowerBound() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryDateTimeRange("baz", com.google.common.collect.Range.greaterThan(new org.joda.time.DateTime(2014, 1, 1, 0, 0)));
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testAddQueryDateTimeRangeUpperBound() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryDateTimeRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.DateTime(2014, 1, 1, 22, 0)));
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testAddQueryDateTimeRangeClosed() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryDateTimeRange("baz", com.google.common.collect.Range.closed(new org.joda.time.DateTime(2014, 1, 10, 14, 0), new org.joda.time.DateTime(2014, 6, 1, 16, 0)));
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    @org.junit.Test
    public void testEncodeFormBody() {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.POST, "http://example.com/foobar");
        r.addPostParam("baz", "quux");
        r.addPostParam("garply", "xyzzy");
        java.lang.String encoded = r.encodeFormBody();
        com.twilio.Assert.assertQueryStringsEqual("baz=quux&garply=xyzzy", encoded);
    }

    @org.junit.Test
    public void testGetPassword() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        org.junit.Assert.assertEquals("password", request.getPassword());
    }

    @org.junit.Test
    public void testGetUsername() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        org.junit.Assert.assertEquals("username", request.getUsername());
    }

    @org.junit.Test
    public void testRequiresAuthentication() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        org.junit.Assert.assertFalse(request.requiresAuthentication());
        request.setAuth("username", "password");
        org.junit.Assert.assertTrue(request.requiresAuthentication());
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURL */
    @org.junit.Test(timeout = 10000)
    public void testConstructURL_literalMutationString15998_failAssert0() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "mas4n3:s3okxH*!TLD");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURL_literalMutationString15998 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithMultivaluedParam */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParam_literalMutationString21227_failAssert1() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "XEdDOMY@F-f:O85l6I");
            r.addQueryParam("baz", "quux");
            r.addQueryParam("baz", "xyzzy");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithMultivaluedParam_literalMutationString21227 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructorWithDomain */
    /* amplification of testConstructorWithDomain_sd24431 */
    @org.junit.Test(timeout = 10000)
    public void testConstructorWithDomain_sd24431_sd24913_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg1_3160 = -1961303435;
            int __DSPOT_arg0_3159 = -1075247314;
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.IPMESSAGING.toString(), "/v1/uri");
            request.getMethod();
            request.getUrl();
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_9 = // StatementAdd: add invocation of a method
            request.encodeFormBody();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_9.codePointCount(__DSPOT_arg0_3159, __DSPOT_arg1_3160);
            org.junit.Assert.fail("testConstructorWithDomain_sd24431_sd24913 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testEncodeFormBody */
    /* amplification of testEncodeFormBody_sd30141 */
    @org.junit.Test(timeout = 10000)
    public void testEncodeFormBody_sd30141_sd33007_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg3_7873 = 440017348;
            char[] __DSPOT_arg2_7872 = new char[]{ 'i', 'K', 'N', ' ' };
            int __DSPOT_arg1_7871 = 1514863940;
            int __DSPOT_arg0_7870 = -1441134406;
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.POST, "http://example.com/foobar");
            r.addPostParam("baz", "quux");
            r.addPostParam("garply", "xyzzy");
            java.lang.String encoded = r.encodeFormBody();
            com.twilio.Assert.assertQueryStringsEqual("baz=quux&garply=xyzzy", encoded);
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_8 = // StatementAdd: add invocation of a method
            r.getAuthString();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_8.getChars(__DSPOT_arg0_7870, __DSPOT_arg1_7871, __DSPOT_arg2_7872, __DSPOT_arg3_7873);
            org.junit.Assert.fail("testEncodeFormBody_sd30141_sd33007 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }
}

