

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
    public void testRequiresAuthentication() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        org.junit.Assert.assertFalse(request.requiresAuthentication());
        request.setAuth("username", "password");
        org.junit.Assert.assertTrue(request.requiresAuthentication());
    }

    @org.junit.Test
    public void testGetUsername() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        // AssertGenerator add assertion
        java.util.HashMap map_426894328 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_426894328, ((com.twilio.http.Request)request).getQueryParams());;
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
        org.junit.Assert.assertTrue(((com.twilio.http.Request)request).requiresAuthentication());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)request).getUrl(), "/uri");
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)request).encodeFormBody(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)request).getPassword(), "password");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)request).encodeQueryParams(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)request).getUsername(), "username");
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
        java.util.HashMap map_377328607 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_377328607, ((com.twilio.http.Request)request).getPostParams());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isAnonymousClass());
        org.junit.Assert.assertEquals("username", request.getUsername());
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeClosed */
    @org.junit.Test
    public void testAddQueryDateRangeClosed_literalMutation30_failAssert27_literalMutation1118() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/fobar");
            // AssertGenerator add assertion
            java.util.HashMap map_1783601375 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1783601375, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_923765944 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_923765944, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateRange("baz", com.google.common.collect.Range.closed(new org.joda.time.LocalDate(2014, 1, 10), new org.joda.time.LocalDate(2014, 100, 1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeClosed_literalMutation30 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeClosed */
    @org.junit.Test
    public void testAddQueryDateRangeClosed_literalMutation12_failAssert9_literalMutation401() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "LW_#*uaYP&<w$N`nJW");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comLW_#*uaYP&<w$N`nJW");
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
            java.util.HashMap map_1417060353 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1417060353, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getTypeName(), "com.twilio.http.HttpMethod");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            java.util.HashMap map_1452032884 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1452032884, ((com.twilio.http.Request)r).getQueryParams());;
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
            r.addQueryDateRange("baz", com.google.common.collect.Range.closed(new org.joda.time.LocalDate(4028, 1, 10), new org.joda.time.LocalDate(2014, 6, 1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeClosed_literalMutation12 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeClosed */
    @org.junit.Test
    public void testAddQueryDateRangeClosed_literalMutation24_failAssert21_literalMutation878_literalMutation1572() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "");
            // AssertGenerator add assertion
            java.util.HashMap map_1473302948 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1473302948, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_255255028 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_255255028, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            java.util.HashMap map_1311279793 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1311279793, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_162383462 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_162383462, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateRange("baz", com.google.common.collect.Range.closed(new org.joda.time.LocalDate(2014, 1, 10), new org.joda.time.LocalDate(1006, 6, 1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeClosed_literalMutation24 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeLowerBound */
    @org.junit.Test
    public void testAddQueryDateRangeLowerBound_literalMutation2535_failAssert16_literalMutation2941() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-0i/foobar");
            // AssertGenerator add assertion
            java.util.HashMap map_884431426 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_884431426, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_1076091410 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1076091410, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateRange("baz", com.google.common.collect.Range.greaterThan(new org.joda.time.LocalDate(2014, 1, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeLowerBound_literalMutation2535 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeLowerBound */
    @org.junit.Test
    public void testAddQueryDateRangeLowerBound_literalMutation2530_failAssert11_literalMutation2817_literalMutation3486() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/fooFbar");
            // AssertGenerator add assertion
            java.util.HashMap map_1854871085 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1854871085, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_1604616749 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1604616749, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            java.util.HashMap map_998742210 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_998742210, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_549980535 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_549980535, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateRange("baz", com.google.common.collect.Range.greaterThan(new org.joda.time.LocalDate(100, 0, 1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeLowerBound_literalMutation2530 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeUpperBound */
    @org.junit.Test
    public void testAddQueryDateRangeUpperBound_literalMutation4051_failAssert13_literalMutation4384() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "");
            // AssertGenerator add assertion
            java.util.HashMap map_375158346 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_375158346, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_601809937 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_601809937, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.LocalDate(2014, 100, 1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeUpperBound_literalMutation4051 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateRangeUpperBound */
    @org.junit.Test
    public void testAddQueryDateRangeUpperBound_literalMutation4053_failAssert15_literalMutation4436_literalMutation4989() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/fRobar");
            // AssertGenerator add assertion
            java.util.HashMap map_725168260 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_725168260, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_586486802 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_586486802, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            java.util.HashMap map_110984725 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_110984725, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_125277810 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_125277810, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.LocalDate(2014, 1, 100)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateRangeUpperBound_literalMutation4053 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeClosed */
    @org.junit.Test
    public void testAddQueryDateTimeRangeClosed_literalMutation5202_failAssert11_literalMutation5887() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/oobar");
            // AssertGenerator add assertion
            java.util.HashMap map_2139540467 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_2139540467, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_1866544162 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1866544162, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.closed(new org.joda.time.DateTime(2014, 0, 10, 14, 0), new org.joda.time.DateTime(2014, 6, 1, 16, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeClosed_literalMutation5202 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeLowerBound */
    @org.junit.Test
    public void testAddQueryDateTimeRangeLowerBound_literalMutation9329_failAssert0_literalMutation9365() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "duw}>>7.0)M5qS!92Id");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comduw}>>7.0)M5qS!92Id");
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
            java.util.HashMap map_359789644 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_359789644, ((com.twilio.http.Request)r).getPostParams());;
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
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.greaterThan(new org.joda.time.DateTime(2014, 1, 1, 0, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeLowerBound_literalMutation9329 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeLowerBound */
    @org.junit.Test
    public void testAddQueryDateTimeRangeLowerBound_literalMutation9340_failAssert11_literalMutation9721() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "");
            // AssertGenerator add assertion
            java.util.HashMap map_2117299492 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_2117299492, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_581870534 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_581870534, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.greaterThan(new org.joda.time.DateTime(2014, 0, 1, 0, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeLowerBound_literalMutation9340 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeUpperBound */
    @org.junit.Test
    public void testAddQueryDateTimeRangeUpperBound_literalMutation11407_failAssert24_literalMutation12231() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/fYobar");
            // AssertGenerator add assertion
            java.util.HashMap map_1190886907 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1190886907, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_1211084881 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1211084881, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.DateTime(2014, 1, 1, 22, -1)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeUpperBound_literalMutation11407 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testAddQueryDateTimeRangeUpperBound */
    @org.junit.Test
    public void testAddQueryDateTimeRangeUpperBound_literalMutation11398_failAssert15_literalMutation11926_literalMutation13045() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-0;4-01/foobar");
            // AssertGenerator add assertion
            java.util.HashMap map_33866864 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_33866864, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_162359360 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_162359360, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            java.util.HashMap map_1961615257 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1961615257, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_178437284 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_178437284, ((com.twilio.http.Request)r).getPostParams());;
            r.addQueryDateTimeRange("baz", com.google.common.collect.Range.lessThan(new org.joda.time.DateTime(2014, 1, 0, 11, 0)));
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testAddQueryDateTimeRangeUpperBound_literalMutation11398 should have thrown IllegalFieldValueException");
        } catch (org.joda.time.IllegalFieldValueException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURL */
    @org.junit.Test
    public void testConstructURL_literalMutation13262_failAssert5_literalMutation13321() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "");
            // AssertGenerator add assertion
            java.util.HashMap map_1850761466 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1850761466, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_662079567 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_662079567, ((com.twilio.http.Request)r).getPostParams());;
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURL_literalMutation13262 should have thrown MalformedURLException");
        } catch (java.net.MalformedURLException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURL */
    @org.junit.Test
    public void testConstructURL_literalMutation13264_failAssert7_literalMutation13339_literalMutation13890() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "");
            // AssertGenerator add assertion
            java.util.HashMap map_1226328629 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1226328629, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_1239362944 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1239362944, ((com.twilio.http.Request)r).getPostParams());;
            // AssertGenerator add assertion
            java.util.HashMap map_392140782 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_392140782, ((com.twilio.http.Request)r).getQueryParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)r).getPassword());
            // AssertGenerator add assertion
            java.util.HashMap map_158024263 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_158024263, ((com.twilio.http.Request)r).getPostParams());;
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("^KzrQ=<I*i1!0J\\yAY}5{#6yCv#OujSeQjf#yrxt");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURL_literalMutation13264 should have thrown MalformedURLException");
        } catch (java.net.MalformedURLException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLURISyntaxException */
    @org.junit.Test(expected = com.twilio.exception.ApiException.class)
    public void testConstructURLURISyntaxException_literalMutation13938() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "htep://{");
        // AssertGenerator add assertion
        java.util.HashMap map_1896544100 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1896544100, ((com.twilio.http.Request)request).getQueryParams());;
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)request).getUrl(), "htep://{");
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)request).encodeFormBody(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.twilio.http.Request)request).getPassword());
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
        java.util.HashMap map_227719357 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_227719357, ((com.twilio.http.Request)request).getPostParams());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isAnonymousClass());
        request.constructURL();
        org.junit.Assert.fail("ApiException was expected");
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLURISyntaxException */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLURISyntaxException_cf14028_failAssert36_literalMutation20210() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "htt`p://{");
            // AssertGenerator add assertion
            java.util.HashMap map_26390172 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_26390172, ((com.twilio.http.Request)request).getQueryParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)request).getUrl(), "htt`p://{");
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)request).encodeFormBody(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.twilio.http.Request)request).getPassword());
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
            java.util.HashMap map_1555122937 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1555122937, ((com.twilio.http.Request)request).getPostParams());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isAnonymousClass());
            request.constructURL();
            // StatementAdderOnAssert create null value
            com.google.common.collect.Range<org.joda.time.DateTime> vc_40 = (com.google.common.collect.Range)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_39 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.twilio.http.Request vc_36 = (com.twilio.http.Request)null;
            // StatementAdderMethod cloned existing statement
            vc_36.addQueryDateTimeRange(vc_39, vc_40);
            org.junit.Assert.fail("ApiException was expected");
            org.junit.Assert.fail("testConstructURLURISyntaxException_cf14028 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLURISyntaxException */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLURISyntaxException_cf13947_failAssert2_literalMutation14743_failAssert0_literalMutation21230() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "");
                // AssertGenerator add assertion
                java.util.HashMap map_550347144 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_550347144, ((com.twilio.http.Request)request).getQueryParams());;
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
                org.junit.Assert.assertEquals(((com.twilio.http.Request)request).getUrl(), "");
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
                org.junit.Assert.assertEquals(((com.twilio.http.Request)request).encodeFormBody(), "");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((com.twilio.http.Request)request).getPassword());
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
                java.util.HashMap map_220353073 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_220353073, ((com.twilio.http.Request)request).getPostParams());;
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isMemberClass());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingConstructor());
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isSynthetic());
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isAnonymousClass());
                request.constructURL();
                // StatementAdderOnAssert create null value
                java.lang.Object vc_2 = (java.lang.Object)null;
                // StatementAdderOnAssert create null value
                com.twilio.http.Request vc_0 = (com.twilio.http.Request)null;
                // StatementAdderMethod cloned existing statement
                vc_0.equals(vc_2);
                org.junit.Assert.fail("ApiException was /expected");
                org.junit.Assert.fail("testConstructURLURISyntaxException_cf13947 should have thrown ApiException");
            } catch (com.twilio.exception.ApiException eee) {
            }
            org.junit.Assert.fail("testConstructURLURISyntaxException_cf13947_failAssert2_literalMutation14743 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithInequalityParam */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithInequalityParam_literalMutation22706_failAssert1_add22724() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "tEVf qB6+JI,?]5gxm");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comtEVf qB6+JI,?]5gxm");
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
            java.util.HashMap map_2133779584 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_2133779584, ((com.twilio.http.Request)r).getPostParams());;
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
            // MethodCallAdder
            r.addQueryParam("baz>", "3");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comtEVf qB6+JI,?]5gxm");
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
            java.util.HashMap map_403074812 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_403074812, ((com.twilio.http.Request)r).getPostParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz%3E=3");
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
            r.addQueryParam("baz>", "3");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithInequalityParam_literalMutation22706 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithInequalityParam */
    @org.junit.Test
    public void testConstructURLWithInequalityParam_literalMutation22706_failAssert1_literalMutation22730() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "tEVf qB6+Jz,?]5gxm");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comtEVf qB6+Jz,?]5gxm");
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
            java.util.HashMap map_555863799 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_555863799, ((com.twilio.http.Request)r).getPostParams());;
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
            r.addQueryParam("baz>", "3");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithInequalityParam_literalMutation22706 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithMultivaluedParam */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParam_add23087() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryParam("baz", "quux");
        // MethodCallAdder
        r.addQueryParam("baz", "xyzzy");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
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
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
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
        org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
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
        r.addQueryParam("baz", "xyzzy");
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithMultivaluedParam */
    @org.junit.Test
    public void testConstructURLWithMultivaluedParam_literalMutation23090_failAssert1_literalMutation23129() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "C u)_0.ne. (h0PDv.");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comC u)_0.ne. (h0PDv.");
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
            java.util.HashMap map_1491755791 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1491755791, ((com.twilio.http.Request)r).getPostParams());;
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
            r.addQueryParam("baz", "quux");
            r.addQueryParam("baz", "xyzzy");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithMultivaluedParam_literalMutation23090 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithMultivaluedParam */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParam_add23087_literalMutation23105_failAssert2_literalMutation23268() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), ">Qf#b&C|R)z@.}v8^h");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com>Qf#b&C|R)z@.}v8^h");
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
            java.util.HashMap map_1575648952 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1575648952, ((com.twilio.http.Request)r).getPostParams());;
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
            r.addQueryParam("baz", "quux");
            // MethodCallAdder
            r.addQueryParam("baz", "xyzzy");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com>Qf#b&C|R)z@.}v8^h");
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
            java.util.HashMap map_1230374177 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1230374177, ((com.twilio.http.Request)r).getPostParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz=quux&baz=xyzzy");
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
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
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
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
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
            org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).ordinal(), 0);
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
            r.addQueryParam("baz", "xyzzy");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithMultivaluedParam_add23087_literalMutation23105 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithParam */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithParam_literalMutation23534_failAssert1_add23552() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "n/IbEz*%GZ]rg>J)#N");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comn/IbEz*%GZ]rg>J)#N");
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
            java.util.HashMap map_894435741 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_894435741, ((com.twilio.http.Request)r).getPostParams());;
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
            // MethodCallAdder
            r.addQueryParam("baz", "quux");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comn/IbEz*%GZ]rg>J)#N");
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
            java.util.HashMap map_674159671 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_674159671, ((com.twilio.http.Request)r).getPostParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz=quux");
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
            r.addQueryParam("baz", "quux");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithParam_literalMutation23534 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithParam */
    @org.junit.Test
    public void testConstructURLWithParam_literalMutation23534_failAssert1_literalMutation23559() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "n/IbEz*%GZ]rg>J)#N");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comn/IbEz*%GZ]rg>J)#N");
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
            java.util.HashMap map_1444794316 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1444794316, ((com.twilio.http.Request)r).getPostParams());;
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
            r.addQueryParam("baz", "quux");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilioK.com/2010-04-01/foobar?baz=quux");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithParam_literalMutation23534 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithParam */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithParam_literalMutation23534_failAssert1_add23552_add23646() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "n/IbEz*%GZ]rg>J)#N");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comn/IbEz*%GZ]rg>J)#N");
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
            java.util.HashMap map_1248123713 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1248123713, ((com.twilio.http.Request)r).getPostParams());;
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
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comn/IbEz*%GZ]rg>J)#N");
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
            java.util.HashMap map_894435741 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_894435741, ((com.twilio.http.Request)r).getPostParams());;
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
            // MethodCallAdder
            r.addQueryParam("baz", "quux");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comn/IbEz*%GZ]rg>J)#N");
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
            java.util.HashMap map_662767247 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_662767247, ((com.twilio.http.Request)r).getPostParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz=quux");
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
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comn/IbEz*%GZ]rg>J)#N");
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
            java.util.HashMap map_674159671 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_674159671, ((com.twilio.http.Request)r).getPostParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz=quux");
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
            // MethodCallAdder
            r.addQueryParam("baz", "quux");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.comn/IbEz*%GZ]rg>J)#N");
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
            java.util.HashMap map_1694832627 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1694832627, ((com.twilio.http.Request)r).getPostParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz=quux&baz=quux");
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
            r.addQueryParam("baz", "quux");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithParam_literalMutation23534 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithParam */
    @org.junit.Test
    public void testConstructURLWithParam_literalMutation23534_failAssert1_literalMutation23555_literalMutation23688() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), ";RY.US!(h)$qg6>C3}");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com;RY.US!(h)$qg6>C3}");
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
            java.util.HashMap map_1949637675 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1949637675, ((com.twilio.http.Request)r).getPostParams());;
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
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com;RY.US!(h)$qg6>C3}");
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
            java.util.HashMap map_237351327 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_237351327, ((com.twilio.http.Request)r).getPostParams());;
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
            r.addQueryParam("baz", "quux");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/201004-01/foobar?baz=quux");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithParam_literalMutation23534 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithParams */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithParams_add23879() throws java.net.MalformedURLException {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "/2010-04-01/foobar");
        r.addQueryParam("baz", "quux");
        // MethodCallAdder
        r.addQueryParam("garply", "xyzzy");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
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
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
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
        r.addQueryParam("garply", "xyzzy");
        java.net.URL url = r.constructURL();
        java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
        com.twilio.Assert.assertUrlsEqual(expected, url);
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithParams */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithParams_add23879_literalMutation23899_failAssert4_add24084() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "<x?j(@r L&wAZpV<|T");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com<x?j(@r L&wAZpV<|T");
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
            java.util.HashMap map_1461674568 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1461674568, ((com.twilio.http.Request)r).getPostParams());;
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
            r.addQueryParam("baz", "quux");
            // MethodCallAdder
            r.addQueryParam("garply", "xyzzy");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com<x?j(@r L&wAZpV<|T");
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
            java.util.HashMap map_314041225 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_314041225, ((com.twilio.http.Request)r).getPostParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz=quux&garply=xyzzy");
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
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
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
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
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
            // MethodCallAdder
            r.addQueryParam("garply", "xyzzy");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com<x?j(@r L&wAZpV<|T");
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
            java.util.HashMap map_2076242401 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_2076242401, ((com.twilio.http.Request)r).getPostParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz=quux&garply=xyzzy&garply=xyzzy");
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
            r.addQueryParam("garply", "xyzzy");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithParams_add23879_literalMutation23899 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testConstructURLWithParams */
    @org.junit.Test(timeout = 10000)
    public void testConstructURLWithParams_add23879_literalMutation23899_failAssert4_literalMutation24094() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.GET, com.twilio.rest.Domains.API.toString(), "<x?j(@r L&wAZpV<|T");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com<x?j(@r L&wAZpV<|T");
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
            java.util.HashMap map_432361421 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_432361421, ((com.twilio.http.Request)r).getPostParams());;
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
            r.addQueryParam("baz", "quux");
            // MethodCallAdder
            r.addQueryParam("garply", "xyzzy");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "https://api.twilio.com<x?j(@r L&wAZpV<|T");
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
            java.util.HashMap map_1290563493 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1290563493, ((com.twilio.http.Request)r).getPostParams());;
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
            org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeQueryParams(), "baz=quux&garply=xyzzy");
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
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
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
            org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getModifiers(), 16401);
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
            r.addQueryParam("garply", "xyzzy");
            java.net.URL url = r.constructURL();
            java.net.URL expected = new java.net.URL("https://api.tilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
            com.twilio.Assert.assertUrlsEqual(expected, url);
            org.junit.Assert.fail("testConstructURLWithParams_add23879_literalMutation23899 should have thrown ApiException");
        } catch (com.twilio.exception.ApiException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testEncodeFormBody */
    @org.junit.Test
    public void testEncodeFormBody_literalMutation41065() {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.POST, "hKtp://example.com/foobar");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "hKtp://example.com/foobar");
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
        java.util.HashMap map_807115589 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_807115589, ((com.twilio.http.Request)r).getQueryParams());;
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
        java.lang.String encoded = r.encodeFormBody();
        com.twilio.Assert.assertQueryStringsEqual("baz=quux&garply=xyzzy", encoded);
    }

    /* amplification of com.twilio.http.RequestTest#testEncodeFormBody */
    @org.junit.Test(timeout = 10000)
    public void testEncodeFormBody_literalMutation41067_add41090_add41258() {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.POST, "http://example.co m/foobar");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "http://example.co m/foobar");
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
        java.util.HashMap map_130838595 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_130838595, ((com.twilio.http.Request)r).getQueryParams());;
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
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "http://example.co m/foobar");
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
        java.util.HashMap map_53976854 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_53976854, ((com.twilio.http.Request)r).getQueryParams());;
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
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "http://example.co m/foobar");
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
        java.util.HashMap map_586308106 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_586308106, ((com.twilio.http.Request)r).getQueryParams());;
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
        // MethodCallAdder
        r.addPostParam("baz", "quux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "http://example.co m/foobar");
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
        java.util.HashMap map_228830963 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_228830963, ((com.twilio.http.Request)r).getQueryParams());;
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "baz=quux");
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
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "http://example.co m/foobar");
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
        java.util.HashMap map_291721271 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_291721271, ((com.twilio.http.Request)r).getQueryParams());;
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "baz=quux");
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
        // MethodCallAdder
        r.addPostParam("baz", "quux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "http://example.co m/foobar");
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
        java.util.HashMap map_538996550 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_538996550, ((com.twilio.http.Request)r).getQueryParams());;
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "baz=quux&baz=quux");
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
        java.lang.String encoded = r.encodeFormBody();
        com.twilio.Assert.assertQueryStringsEqual("baz=quux&garply=xyzzy", encoded);
    }

    /* amplification of com.twilio.http.RequestTest#testEncodeFormBody */
    @org.junit.Test(timeout = 10000)
    public void testEncodeFormBody_literalMutation41064_add41070_add41107() {
        com.twilio.http.Request r = new com.twilio.http.Request(com.twilio.http.HttpMethod.POST, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "");
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
        java.util.HashMap map_584156298 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_584156298, ((com.twilio.http.Request)r).getQueryParams());;
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
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "");
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
        java.util.HashMap map_648663207 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_648663207, ((com.twilio.http.Request)r).getQueryParams());;
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
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "");
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
        java.util.HashMap map_1090317782 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1090317782, ((com.twilio.http.Request)r).getQueryParams());;
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
        // MethodCallAdder
        r.addPostParam("garply", "xyzzy");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "");
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
        java.util.HashMap map_1888418140 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1888418140, ((com.twilio.http.Request)r).getQueryParams());;
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "baz=quux&garply=xyzzy");
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
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)r).getMethod()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).getUrl(), "");
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
        java.util.HashMap map_2106400852 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_2106400852, ((com.twilio.http.Request)r).getQueryParams());;
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)r).encodeFormBody(), "baz=quux&garply=xyzzy");
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
        r.addPostParam("garply", "xyzzy");
        java.lang.String encoded = r.encodeFormBody();
        // MethodCallAdder
        com.twilio.Assert.assertQueryStringsEqual("baz=quux&garply=xyzzy", encoded);
        com.twilio.Assert.assertQueryStringsEqual("baz=quux&garply=xyzzy", encoded);
    }

    /* amplification of com.twilio.http.RequestTest#testGetPassword */
    @org.junit.Test(timeout = 10000)
    public void testGetPassword_cf41369_cf43997() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetPassword_cf41369__4 = // StatementAdderMethod cloned existing statement
request.getQueryParams();
        // AssertGenerator add assertion
        java.util.HashMap map_1648441389 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1648441389, o_testGetPassword_cf41369__4);;
        // AssertGenerator replace invocation
        java.lang.String o_testGetPassword_cf41369_cf43997__8 = // StatementAdderMethod cloned existing statement
request.getUsername();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetPassword_cf41369_cf43997__8, "username");
        org.junit.Assert.assertEquals("password", request.getPassword());
    }

    /* amplification of com.twilio.http.RequestTest#testGetPassword */
    @org.junit.Test(timeout = 10000)
    public void testGetPassword_cf41335_cf42562() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_8553 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testGetPassword_cf41335__6 = // StatementAdderMethod cloned existing statement
request.equals(vc_8553);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testGetPassword_cf41335__6);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_8952 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_8952);
        // AssertGenerator replace invocation
        boolean o_testGetPassword_cf41335_cf42562__12 = // StatementAdderMethod cloned existing statement
vc_8553.equals(vc_8952);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testGetPassword_cf41335_cf42562__12);
        org.junit.Assert.assertEquals("password", request.getPassword());
    }

    /* amplification of com.twilio.http.RequestTest#testGetPassword */
    @org.junit.Test(timeout = 10000)
    public void testGetPassword_cf41366_cf43893_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
            request.setAuth("username", "password");
            // AssertGenerator replace invocation
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetPassword_cf41366__4 = // StatementAdderMethod cloned existing statement
request.getPostParams();
            // AssertGenerator add assertion
            java.util.HashMap map_154087717 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_154087717, o_testGetPassword_cf41366__4);;
            // StatementAdderOnAssert create null value
            com.google.common.collect.Range<org.joda.time.LocalDate> vc_9384 = (com.google.common.collect.Range)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_9383 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            request.addQueryDateRange(vc_9383, vc_9384);
            // MethodAssertGenerator build local variable
            Object o_14_0 = request.getPassword();
            org.junit.Assert.fail("testGetPassword_cf41366_cf43893 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testGetPassword */
    @org.junit.Test(timeout = 10000)
    public void testGetPassword_cf41360_cf43701_literalMutation44206() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/ujri");
        // AssertGenerator add assertion
        java.util.HashMap map_343736530 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_343736530, ((com.twilio.http.Request)request).getQueryParams());;
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)request).getUrl(), "/ujri");
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
        org.junit.Assert.assertEquals(((com.twilio.http.Request)request).encodeFormBody(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.twilio.http.Request)request).getPassword());
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
        java.util.HashMap map_945487742 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_945487742, ((com.twilio.http.Request)request).getPostParams());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.http.HttpMethod)((com.twilio.http.Request)request).getMethod()).getDeclaringClass()).isAnonymousClass());
        request.setAuth("username", "password");
        // AssertGenerator replace invocation
        java.lang.String o_testGetPassword_cf41360__4 = // StatementAdderMethod cloned existing statement
request.getUsername();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetPassword_cf41360__4, "username");
        // AssertGenerator replace invocation
        java.lang.String o_testGetPassword_cf41360_cf43701__8 = // StatementAdderMethod cloned existing statement
request.getUsername();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetPassword_cf41360_cf43701__8, "username");
        org.junit.Assert.assertEquals("password", request.getPassword());
    }

    /* amplification of com.twilio.http.RequestTest#testGetPassword */
    @org.junit.Test(timeout = 10000)
    public void testGetPassword_cf41354_cf43393_cf44358() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        // AssertGenerator replace invocation
        java.lang.String o_testGetPassword_cf41354__4 = // StatementAdderMethod cloned existing statement
request.getPassword();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetPassword_cf41354__4, "password");
        // StatementAdderOnAssert create null value
        java.lang.Object vc_9202 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9202);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9202);
        // AssertGenerator replace invocation
        boolean o_testGetPassword_cf41354_cf43393__10 = // StatementAdderMethod cloned existing statement
request.equals(vc_9202);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testGetPassword_cf41354_cf43393__10);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_9502 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9502);
        // AssertGenerator replace invocation
        boolean o_testGetPassword_cf41354_cf43393_cf44358__18 = // StatementAdderMethod cloned existing statement
request.equals(vc_9502);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testGetPassword_cf41354_cf43393_cf44358__18);
        org.junit.Assert.assertEquals("password", request.getPassword());
    }

    /* amplification of com.twilio.http.RequestTest#testGetPassword */
    @org.junit.Test(timeout = 10000)
    public void testGetPassword_cf41366_cf43862_cf45972() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetPassword_cf41366__4 = // StatementAdderMethod cloned existing statement
request.getPostParams();
        // AssertGenerator add assertion
        java.util.HashMap map_154087717 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_154087717, o_testGetPassword_cf41366__4);;
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetPassword_cf41366_cf43862__8 = // StatementAdderMethod cloned existing statement
request.getPostParams();
        // AssertGenerator add assertion
        java.util.HashMap map_1358196253 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1358196253, o_testGetPassword_cf41366_cf43862__8);;
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetPassword_cf41366_cf43862_cf45972__12 = // StatementAdderMethod cloned existing statement
request.getPostParams();
        // AssertGenerator add assertion
        java.util.HashMap map_966047742 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_966047742, o_testGetPassword_cf41366_cf43862_cf45972__12);;
        org.junit.Assert.assertEquals("password", request.getPassword());
    }

    /* amplification of com.twilio.http.RequestTest#testGetUsername */
    @org.junit.Test(timeout = 10000)
    public void testGetUsername_cf47563_cf50178_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
            request.setAuth("username", "password");
            // AssertGenerator replace invocation
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetUsername_cf47563__4 = // StatementAdderMethod cloned existing statement
request.getQueryParams();
            // AssertGenerator add assertion
            java.util.HashMap map_84106679 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_84106679, o_testGetUsername_cf47563__4);;
            // StatementAdderOnAssert create null value
            com.twilio.http.Request vc_11110 = (com.twilio.http.Request)null;
            // StatementAdderMethod cloned existing statement
            vc_11110.encodeQueryParams();
            // MethodAssertGenerator build local variable
            Object o_12_0 = request.getUsername();
            org.junit.Assert.fail("testGetUsername_cf47563_cf50178 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testGetUsername */
    @org.junit.Test(timeout = 10000)
    public void testGetUsername_cf47528_cf48546_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
            request.setAuth("username", "password");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_10252 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_10252);
            // AssertGenerator replace invocation
            boolean o_testGetUsername_cf47528__6 = // StatementAdderMethod cloned existing statement
request.equals(vc_10252);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testGetUsername_cf47528__6);
            // StatementAdderOnAssert create null value
            com.twilio.http.Request vc_10618 = (com.twilio.http.Request)null;
            // StatementAdderMethod cloned existing statement
            vc_10618.getUsername();
            // MethodAssertGenerator build local variable
            Object o_16_0 = request.getUsername();
            org.junit.Assert.fail("testGetUsername_cf47528_cf48546 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testGetUsername */
    @org.junit.Test(timeout = 10000)
    public void testGetUsername_cf47560_cf50079_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
            request.setAuth("username", "password");
            // AssertGenerator replace invocation
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetUsername_cf47560__4 = // StatementAdderMethod cloned existing statement
request.getPostParams();
            // AssertGenerator add assertion
            java.util.HashMap map_202654086 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_202654086, o_testGetUsername_cf47560__4);;
            // StatementAdderOnAssert create null value
            com.google.common.collect.Range<org.joda.time.LocalDate> vc_11084 = (com.google.common.collect.Range)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_11083 = new java.lang.String();
            // StatementAdderOnAssert create null value
            com.twilio.http.Request vc_11080 = (com.twilio.http.Request)null;
            // StatementAdderMethod cloned existing statement
            vc_11080.addQueryDateRange(vc_11083, vc_11084);
            // MethodAssertGenerator build local variable
            Object o_16_0 = request.getUsername();
            org.junit.Assert.fail("testGetUsername_cf47560_cf50079 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.http.RequestTest#testGetUsername */
    @org.junit.Test(timeout = 10000)
    public void testGetUsername_cf47548_cf49619_cf53871() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        // AssertGenerator replace invocation
        java.lang.String o_testGetUsername_cf47548__4 = // StatementAdderMethod cloned existing statement
request.getPassword();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetUsername_cf47548__4, "password");
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetUsername_cf47548_cf49619__8 = // StatementAdderMethod cloned existing statement
request.getPostParams();
        // AssertGenerator add assertion
        java.util.HashMap map_570650491 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_570650491, o_testGetUsername_cf47548_cf49619__8);;
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_12053 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testGetUsername_cf47548_cf49619_cf53871__14 = // StatementAdderMethod cloned existing statement
request.equals(vc_12053);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testGetUsername_cf47548_cf49619_cf53871__14);
        org.junit.Assert.assertEquals("username", request.getUsername());
    }

    /* amplification of com.twilio.http.RequestTest#testGetUsername */
    @org.junit.Test(timeout = 10000)
    public void testGetUsername_cf47548_cf49619_cf53896() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        // AssertGenerator replace invocation
        java.lang.String o_testGetUsername_cf47548__4 = // StatementAdderMethod cloned existing statement
request.getPassword();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetUsername_cf47548__4, "password");
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetUsername_cf47548_cf49619__8 = // StatementAdderMethod cloned existing statement
request.getPostParams();
        // AssertGenerator add assertion
        java.util.HashMap map_570650491 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_570650491, o_testGetUsername_cf47548_cf49619__8);;
        // AssertGenerator replace invocation
        java.lang.String o_testGetUsername_cf47548_cf49619_cf53896__12 = // StatementAdderMethod cloned existing statement
request.getUsername();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetUsername_cf47548_cf49619_cf53896__12, "username");
        org.junit.Assert.assertEquals("username", request.getUsername());
    }

    /* amplification of com.twilio.http.RequestTest#testGetUsername */
    @org.junit.Test(timeout = 10000)
    public void testGetUsername_cf47548_cf49622_cf50931() {
        com.twilio.http.Request request = new com.twilio.http.Request(com.twilio.http.HttpMethod.DELETE, "/uri");
        request.setAuth("username", "password");
        // AssertGenerator replace invocation
        java.lang.String o_testGetUsername_cf47548__4 = // StatementAdderMethod cloned existing statement
request.getPassword();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetUsername_cf47548__4, "password");
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> o_testGetUsername_cf47548_cf49622__8 = // StatementAdderMethod cloned existing statement
request.getQueryParams();
        // AssertGenerator add assertion
        java.util.HashMap map_1504105921 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1504105921, o_testGetUsername_cf47548_cf49622__8);;
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_11253 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testGetUsername_cf47548_cf49622_cf50931__14 = // StatementAdderMethod cloned existing statement
request.equals(vc_11253);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testGetUsername_cf47548_cf49622_cf50931__14);
        org.junit.Assert.assertEquals("username", request.getUsername());
    }
}

