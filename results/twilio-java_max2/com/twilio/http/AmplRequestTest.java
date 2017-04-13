

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

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeClosed */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeClosed_literalMutation4 */
    @org.junit.Test
    public void testAddQueryDateRangeClosed_literalMutation4_failAssert1_literalMutation43() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "!&Bcvg[?i!rb0/|]6^F");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com!&Bcvg[?i!rb0/|]6^F");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).name(), "GET");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            java.util.HashMap map_1448583354 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1448583354, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isSynthetic());
            r.addQueryDateRange("baz", com.google.common.collect.Range.closed(new org.joda.time.LocalDate(2014, 1, 10), new org.joda.time.LocalDate(2014, 6, 1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeClosed_literalMutation4 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeClosed */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeClosed_literalMutation14 */
    @org.junit.Test
    public void testAddQueryDateRangeClosed_literalMutation14_failAssert11_literalMutation164() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/}oobar");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com/2010-04-01/}oobar");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).name(), "GET");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            java.util.HashMap map_1611335330 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1611335330, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            java.util.HashMap map_840600929 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_840600929, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isSynthetic());
            r.addQueryDateRange("baz", com.google.common.collect.Range.closed(new org.joda.time.LocalDate(2014, 0, 10), new org.joda.time.LocalDate(2014, 6, 1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeClosed_literalMutation14 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeLowerBound */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeLowerBound_literalMutation2385 */
    @org.junit.Test
    public void testAddQueryDateRangeLowerBound_literalMutation2385_failAssert13_literalMutation2475() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04T01/foobar");
            // AssertGenerator add assertion
            java.util.HashMap map_802409889 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_802409889, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com/2010-04T01/foobar");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_966787627 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_966787627, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateRange("baz", com.google.common.collect.Range.greaterThan(new org.joda.time.LocalDate(2014, 100, 1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeLowerBound_literalMutation2385 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeUpperBound */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeUpperBound_literalMutation4909 */
    @org.junit.Test
    public void testAddQueryDateRangeUpperBound_literalMutation4909_failAssert16_literalMutation5095() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/!oobar");
            // AssertGenerator add assertion
            java.util.HashMap map_869136251 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_869136251, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com/2010-04-01/!oobar");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_1238780506 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1238780506, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.LocalDate(2014, 1, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeUpperBound_literalMutation4909 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeUpperBound */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeUpperBound_literalMutation4895 */
    @org.junit.Test
    public void testAddQueryDateRangeUpperBound_literalMutation4895_failAssert2_literalMutation4921() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2[10-04-1/foobar");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com/2[10-04-1/foobar");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).name(), "GET");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            java.util.HashMap map_524289893 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_524289893, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isSynthetic());
            r.addQueryDateRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.LocalDate(2014, 1, 1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeUpperBound_literalMutation4895 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeClosed */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeClosed_literalMutation8957 */
    @org.junit.Test
    public void testAddQueryDateTimeRangeClosed_literalMutation8957_failAssert19_literalMutation9461() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), ">t1-EZ;[WIgM>D#s%<");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com>t1-EZ;[WIgM>D#s%<");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).name(), "GET");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            java.util.HashMap map_42239719 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_42239719, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            java.util.HashMap map_950798695 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_950798695, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isSynthetic());
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.closed(new org.joda.time.DateTime(2014, 1, 10, 100, 0), new org.joda.time.DateTime(2014, 6, 1, 16, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeClosed_literalMutation8957 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeClosed */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeClosed_literalMutation8940 */
    @org.junit.Test
    public void testAddQueryDateTimeRangeClosed_literalMutation8940_failAssert2_literalMutation9000() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "(:w@>Pz(hTy2&:796qC");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com(:w@>Pz(hTy2&:796qC");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).name(), "GET");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            java.util.HashMap map_1301579252 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1301579252, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isSynthetic());
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.closed(new org.joda.time.DateTime(2014, 1, 10, 14, 0), new org.joda.time.DateTime(2014, 6, 1, 16, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeClosed_literalMutation8940 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeLowerBound */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeLowerBound_literalMutation12328 */
    @org.junit.Test
    public void testAddQueryDateTimeRangeLowerBound_literalMutation12328_failAssert20_literalMutation12608() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "Cm_=iczR#qiJLGgjw#");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comCm_=iczR#qiJLGgjw#");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).name(), "GET");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_1524050564 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1524050564, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            java.util.HashMap map_1871546243 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1871546243, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isSynthetic());
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.greaterThan(new org.joda.time.DateTime(2014, 1, 1, 100, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeLowerBound_literalMutation12328 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeUpperBound */
    @org.junit.Test
    public void testAddQueryDateTimeRangeUpperBound_literalMutation14783_failAssert1() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "1=!mas4n3:s3okxH*!");
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.DateTime(2014, 1, 1, 22, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeUpperBound_literalMutation14783 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeUpperBound */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeUpperBound_literalMutation14798 */
    @org.junit.Test
    public void testAddQueryDateTimeRangeUpperBound_literalMutation14798_failAssert16_literalMutation15022() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), ":=+T|uV?z(q6]n^[p:");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com:=+T|uV?z(q6]n^[p:");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).name(), "GET");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_702859757 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_702859757, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            java.util.HashMap map_603737271 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_603737271, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isSynthetic());
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.DateTime(2014, 1, 0, 22, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeUpperBound_literalMutation14798 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeUpperBound */
    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeUpperBound_literalMutation14783 */
    @org.junit.Test
    public void testAddQueryDateTimeRangeUpperBound_literalMutation14783_failAssert1_literalMutation14817() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "1=!mas4n3:3okxH*!");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com1=!mas4n3:3okxH*!");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).name(), "GET");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            java.util.HashMap map_1396914389 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1396914389, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isSynthetic());
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.DateTime(2014, 1, 1, 22, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeUpperBound_literalMutation14783 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLURISyntaxException */
    /* amplification of com.twilio.http.RequestTest#testConstructURLURISyntaxException_cf17974 */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLURISyntaxException_cf17974_failAssert27_literalMutation18282() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "htp://{");
            // AssertGenerator add assertion
            java.util.HashMap map_545696258 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_545696258, ((com.twilio.http.Request)request).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)request).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)request).getUrl(), "htp://{");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).ordinal(), 3);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).name(), "DELETE");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)request).getPassword());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)request).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)request).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)request).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            java.util.HashMap map_1514791125 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1514791125, ((com.twilio.http.Request)request).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isAnonymousClass());
            request.constructURL();
            // StatementAdderMethod cloned existing statement
            request.getQueryParams();
            org.junit.Assert.fail("testConstructURLURISyntaxException_cf17974 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithParam */
    /* amplification of com.twilio.http.RequestTest#testConstructURLWithParam_literalMutation37310 */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithParam_literalMutation37310_failAssert7_add37337() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
            // MethodCallAdder
            r.addQueryParam("baz", "quux");
            r.addQueryParam("baz", "quux");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com/2010-04-01/foobar");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz=quux&baz=quux");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_110171678 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_110171678, ((com.twilio.http.Request)r).getPostParams());;
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithParam_literalMutation37310 should have thrown MalformedURLException");
        } catch (java.net.MalformedURLException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructorWithDomain */
    /* amplification of com.twilio.http.RequestTest#testConstructorWithDomain_cf38577 */
    @org.junit.Test(timeout = 10000)
    public void testConstructorWithDomain_cf38577_cf40672_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.IPMESSAGING.toString(), "/v1/uri");
            // MethodAssertGenerator build local variable
            Object o_5_0 = request.getMethod();
            // AssertGenerator replace invocation
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testConstructorWithDomain_cf38577__7 = // StatementAdderMethod cloned existing statement
request.getQueryParams();
            // AssertGenerator add assertion
            java.util.HashMap map_2077362660 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_2077362660, o_testConstructorWithDomain_cf38577__7);;
            // StatementAdderOnAssert create null value
            java.lang.String vc_8214 = (java.lang.String)null;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_946 = "https://ip-messaging.twilio.com/v1/uri";
            // StatementAdderOnAssert create null value
            com.twilio.http.Request vc_8210 = (com.twilio.http.Request)null;
            // StatementAdderMethod cloned existing statement
            vc_8210.setAuth(String_vc_946, vc_8214);
            // MethodAssertGenerator build local variable
            Object o_19_0 = request.getUrl();
            org.junit.Assert.fail("testConstructorWithDomain_cf38577_cf40672 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructorWithDomain */
    /* amplification of com.twilio.http.RequestTest#testConstructorWithDomain_cf38574 */
    @org.junit.Test(timeout = 10000)
    public void testConstructorWithDomain_cf38574_cf40507_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.IPMESSAGING.toString(), "/v1/uri");
            // MethodAssertGenerator build local variable
            Object o_5_0 = request.getMethod();
            // AssertGenerator replace invocation
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testConstructorWithDomain_cf38574__7 = // StatementAdderMethod cloned existing statement
request.getPostParams();
            // AssertGenerator add assertion
            java.util.HashMap map_608565383 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_608565383, o_testConstructorWithDomain_cf38574__7);;
            // StatementAdderOnAssert create null value
            java.lang.String vc_8162 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_8161 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.twilio.http.Request vc_8158 = (com.twilio.http.Request)null;
            // StatementAdderMethod cloned existing statement
            vc_8158.setAuth(vc_8161, vc_8162);
            // MethodAssertGenerator build local variable
            Object o_19_0 = request.getUrl();
            org.junit.Assert.fail("testConstructorWithDomain_cf38574_cf40507 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructorWithDomain */
    /* amplification of com.twilio.http.RequestTest#testConstructorWithDomain_cf38577 */
    @org.junit.Test(timeout = 10000)
    public void testConstructorWithDomain_cf38577_cf40690_cf42848_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.IPMESSAGING.toString(), "/v1/uri");
            // MethodAssertGenerator build local variable
            Object o_5_0 = request.getMethod();
            // AssertGenerator replace invocation
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testConstructorWithDomain_cf38577__7 = // StatementAdderMethod cloned existing statement
request.getQueryParams();
            // AssertGenerator add assertion
            java.util.HashMap map_2077362660 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_2077362660, o_testConstructorWithDomain_cf38577__7);;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_947 = "https://ip-messaging.twilio.com/v1/uri";
            // MethodAssertGenerator build local variable
            Object o_13_0 = String_vc_947;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_946 = "https://ip-messaging.twilio.com/v1/uri";
            // MethodAssertGenerator build local variable
            Object o_17_0 = String_vc_946;
            // StatementAdderMethod cloned existing statement
            request.setAuth(String_vc_946, String_vc_947);
            // StatementAdderOnAssert create null value
            com.google.common.collect.Range<org.joda.time.LocalDate> vc_8666 = (com.google.common.collect.Range)null;
            // StatementAdderOnAssert create null value
            java.lang.String vc_8664 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            com.twilio.http.Request vc_8662 = (com.twilio.http.Request)null;
            // StatementAdderMethod cloned existing statement
            vc_8662.addQueryDateRange(vc_8664, vc_8666);
            // MethodAssertGenerator build local variable
            Object o_29_0 = request.getUrl();
            org.junit.Assert.fail("testConstructorWithDomain_cf38577_cf40690_cf42848 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testEncodeFormBody */
    /* amplification of com.twilio.http.RequestTest#testEncodeFormBody_literalMutation43755 */
    @org.junit.Test(timeout = 10000)
    public void testEncodeFormBody_literalMutation43755_cf44082_failAssert22_literalMutation44249() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.POST, "O-X>xN}3L{tBy4&DoW_]$Z=l");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "O-X>xN}3L{tBy4&DoW_]$Z=l");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).name(), "POST");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            java.util.HashMap map_764661422 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_764661422, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.twilio.http.Request)r).requiresAuthentication());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getUsername());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSimpleName(), "HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).toGenericString(), "public final enum com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getCanonicalName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isSynthetic());
            r.addPostParam("baz", "quux");
            r.addPostParam("garply", "xyzzy");
            // StatementAdderOnAssert create null value
            java.lang.String vc_8890 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8890);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_8889 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_8889, "");
            // StatementAdderOnAssert create null value
            com.twilio.http.Request vc_8886 = (com.twilio.http.Request)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8886);
            // StatementAdderMethod cloned existing statement
            vc_8886.setAuth(vc_8889, vc_8890);
            // MethodAssertGenerator build local variable
            Object o_13_0 = ((com.twilio.http.Request)r).requiresAuthentication();
            java.lang.String encoded = r.encodeFormBody();
            com.twilio.Assert.assertQueryStringsEqual("baz=quux&garply=xyzzy", encoded);
            org.junit.Assert.fail("testEncodeFormBody_literalMutation43755_cf44082 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testRequiresAuthentication */
    /* amplification of com.twilio.http.RequestTest#testRequiresAuthentication_cf62984 */
    @org.junit.Test(timeout = 10000)
    public void testRequiresAuthentication_cf62984_cf65170_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_42_1 = 16401;
            // MethodAssertGenerator build local variable
            Object o_28_1 = 3;
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
            // MethodAssertGenerator build local variable
            Object o_3_0 = request.requiresAuthentication();
            request.setAuth("username", "password");
            // AssertGenerator add assertion
            java.util.HashMap map_494889131 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_494889131, ((com.twilio.http.Request)request).getQueryParams());;
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isEnum();
            // MethodAssertGenerator build local variable
            Object o_10_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingMethod();
            // MethodAssertGenerator build local variable
            Object o_12_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).toGenericString();
            // MethodAssertGenerator build local variable
            Object o_14_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isInterface();
            // MethodAssertGenerator build local variable
            Object o_16_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_18_0 = ((com.twilio.http.Request)request).requiresAuthentication();
            // MethodAssertGenerator build local variable
            Object o_20_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isLocalClass();
            // MethodAssertGenerator build local variable
            Object o_22_0 = ((com.twilio.http.Request)request).getUrl();
            // MethodAssertGenerator build local variable
            Object o_24_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isArray();
            // MethodAssertGenerator build local variable
            Object o_26_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getCanonicalName();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).ordinal();
            // MethodAssertGenerator build local variable
            Object o_30_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getSimpleName();
            // MethodAssertGenerator build local variable
            Object o_32_0 = ((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).name();
            // MethodAssertGenerator build local variable
            Object o_34_0 = ((com.twilio.http.Request)request).getPassword();
            // MethodAssertGenerator build local variable
            Object o_36_0 = ((com.twilio.http.Request)request).encodeFormBody();
            // MethodAssertGenerator build local variable
            Object o_38_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getSigners();
            // MethodAssertGenerator build local variable
            Object o_40_0 = ((com.twilio.http.Request)request).encodeQueryParams();
            // MethodAssertGenerator build local variable
            Object o_42_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getModifiers();
            // MethodAssertGenerator build local variable
            Object o_44_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getComponentType();
            // MethodAssertGenerator build local variable
            Object o_46_0 = ((com.twilio.http.Request)request).getUsername();
            // MethodAssertGenerator build local variable
            Object o_48_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getName();
            // MethodAssertGenerator build local variable
            Object o_50_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isAnnotation();
            // MethodAssertGenerator build local variable
            Object o_52_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isPrimitive();
            // MethodAssertGenerator build local variable
            Object o_54_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingClass();
            // MethodAssertGenerator build local variable
            Object o_56_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getDeclaringClass();
            // MethodAssertGenerator build local variable
            Object o_58_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).desiredAssertionStatus();
            // AssertGenerator add assertion
            java.util.HashMap map_862388393 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_862388393, ((com.twilio.http.Request)request).getPostParams());;
            // MethodAssertGenerator build local variable
            Object o_62_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isMemberClass();
            // MethodAssertGenerator build local variable
            Object o_64_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingConstructor();
            // MethodAssertGenerator build local variable
            Object o_66_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isSynthetic();
            // MethodAssertGenerator build local variable
            Object o_68_0 = ((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isAnonymousClass();
            // AssertGenerator replace invocation
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testRequiresAuthentication_cf62984__6 = // StatementAdderMethod cloned existing statement
request.getPostParams();
            // AssertGenerator add assertion
            java.util.HashMap map_577498540 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_577498540, o_testRequiresAuthentication_cf62984__6);;
            // StatementAdderOnAssert create null value
            com.twilio.http.Request vc_13486 = (com.twilio.http.Request)null;
            // StatementAdderMethod cloned existing statement
            vc_13486.getUsername();
            // MethodAssertGenerator build local variable
            Object o_78_0 = request.requiresAuthentication();
            org.junit.Assert.fail("testRequiresAuthentication_cf62984_cf65170 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

