

package org.jsoup.helper;


public class AmplHttpConnectionTest {
    /* most actual network http connection tests are in integration */
    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void throwsExceptionOnParseWithoutExecute() throws java.io.IOException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
        con.response().parse();
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void throwsExceptionOnBodyWithoutExecute() throws java.io.IOException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
        con.response().body();
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void throwsExceptionOnBodyAsBytesWithoutExecute() throws java.io.IOException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
        con.response().bodyAsBytes();
    }

    @org.junit.Test
    public void sameHeadersCombineWithComma() {
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
        java.util.List<java.lang.String> values = new java.util.ArrayList<java.lang.String>();
        values.add("no-cache");
        values.add("no-store");
        headers.put("Cache-Control", values);
        org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
        res.processResponseHeaders(headers);
        org.junit.Assert.assertEquals("no-cache, no-store", res.header("Cache-Control"));
    }

    @org.junit.Test
    public void ignoresEmptySetCookies() {
        // prep http response header map
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
        headers.put("Set-Cookie", java.util.Collections.<java.lang.String>emptyList());
        org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
        res.processResponseHeaders(headers);
        org.junit.Assert.assertEquals(0, res.cookies().size());
    }

    @org.junit.Test
    public void ignoresEmptyCookieNameAndVals() {
        // prep http response header map
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
        java.util.List<java.lang.String> cookieStrings = new java.util.ArrayList<java.lang.String>();
        cookieStrings.add(null);
        cookieStrings.add("");
        cookieStrings.add("one");
        cookieStrings.add("two=");
        cookieStrings.add("three=;");
        cookieStrings.add("four=data; Domain=.example.com; Path=/");
        headers.put("Set-Cookie", cookieStrings);
        org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
        res.processResponseHeaders(headers);
        org.junit.Assert.assertEquals(4, res.cookies().size());
        org.junit.Assert.assertEquals("", res.cookie("one"));
        org.junit.Assert.assertEquals("", res.cookie("two"));
        org.junit.Assert.assertEquals("", res.cookie("three"));
        org.junit.Assert.assertEquals("data", res.cookie("four"));
    }

    @org.junit.Test
    public void connectWithUrl() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void throwsOnMalformedUrl() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("bzzt");
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void throwsOnOddData() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        con.data("Name", "val", "what");
    }

    @org.junit.Test
    public void inputStream() {
        org.jsoup.Connection.KeyVal kv = org.jsoup.helper.HttpConnection.KeyVal.create("file", "thumb.jpg", org.jsoup.integration.ParseTest.inputStreamFrom("Check"));
        org.junit.Assert.assertEquals("file", kv.key());
        org.junit.Assert.assertEquals("thumb.jpg", kv.value());
        org.junit.Assert.assertTrue(kv.hasInputStream());
        kv = org.jsoup.helper.HttpConnection.KeyVal.create("one", "two");
        org.junit.Assert.assertEquals("one", kv.key());
        org.junit.Assert.assertEquals("two", kv.value());
        org.junit.Assert.assertFalse(kv.hasInputStream());
    }

    @org.junit.Test
    public void headers() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
        java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
        // AssertGenerator replace invocation
        java.lang.String o_headers__5 = headers.put("content-type", "text/html");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_headers__5);
        // AssertGenerator replace invocation
        java.lang.String o_headers__6 = headers.put("Connection", "keep-alive");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_headers__6);
        // AssertGenerator replace invocation
        java.lang.String o_headers__7 = headers.put("Host", "http://example.com");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_headers__7);
        // AssertGenerator replace invocation
        org.jsoup.Connection o_headers__8 = con.headers(headers);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_headers__8.equals(con));
        org.junit.Assert.assertEquals("text/html", con.request().header("content-type"));
        org.junit.Assert.assertEquals("keep-alive", con.request().header("Connection"));
        org.junit.Assert.assertEquals("http://example.com", con.request().header("Host"));
    }

    @org.junit.Test
    public void cookie() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_cookie__3 = con.cookie("Name", "Val");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_cookie__3.equals(con));
        org.junit.Assert.assertEquals("Val", con.request().cookie("Name"));
    }

    @org.junit.Test
    public void data() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_data__3 = con.data("Name", "Val", "Foo", "bar");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_data__3.equals(con));
        java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
        java.lang.Object[] data = values.toArray();
        org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
        org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
        org.junit.Assert.assertEquals("Name", one.key());
        org.junit.Assert.assertEquals("Val", one.value());
        org.junit.Assert.assertEquals("Foo", two.key());
        org.junit.Assert.assertEquals("bar", two.value());
    }

    @org.junit.Test
    public void requestBody() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_requestBody__3 = con.requestBody("foo");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_requestBody__3.equals(con));
        org.junit.Assert.assertEquals("foo", con.request().requestBody());
    }

    @org.junit.Test
    public void method() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        org.junit.Assert.assertEquals(org.jsoup.Connection.Method.GET, con.request().method());
        // AssertGenerator replace invocation
        org.jsoup.Connection o_method__6 = con.method(org.jsoup.Connection.Method.POST);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_method__6.equals(con));
        org.junit.Assert.assertEquals(org.jsoup.Connection.Method.POST, con.request().method());
    }

    @org.junit.Test
    public void caseInsensitiveHeaders() {
        org.jsoup.Connection.Response res = new org.jsoup.helper.HttpConnection.Response();
        java.util.Map<java.lang.String, java.lang.String> headers = res.headers();
        // AssertGenerator replace invocation
        java.lang.String o_caseInsensitiveHeaders__5 = headers.put("Accept-Encoding", "gzip");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_caseInsensitiveHeaders__5);
        // AssertGenerator replace invocation
        java.lang.String o_caseInsensitiveHeaders__6 = headers.put("content-type", "text/html");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_caseInsensitiveHeaders__6);
        // AssertGenerator replace invocation
        java.lang.String o_caseInsensitiveHeaders__7 = headers.put("refErrer", "http://example.com");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_caseInsensitiveHeaders__7);
        org.junit.Assert.assertTrue(res.hasHeader("Accept-Encoding"));
        org.junit.Assert.assertTrue(res.hasHeader("accept-encoding"));
        org.junit.Assert.assertTrue(res.hasHeader("accept-Encoding"));
        org.junit.Assert.assertEquals("gzip", res.header("accept-Encoding"));
        org.junit.Assert.assertEquals("text/html", res.header("Content-Type"));
        org.junit.Assert.assertEquals("http://example.com", res.header("Referrer"));
        // AssertGenerator replace invocation
        org.jsoup.Connection.Response o_caseInsensitiveHeaders__20 = res.removeHeader("Content-Type");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_caseInsensitiveHeaders__20.equals(res));
        org.junit.Assert.assertFalse(res.hasHeader("content-type"));
        // AssertGenerator replace invocation
        org.jsoup.Connection.Response o_caseInsensitiveHeaders__23 = res.header("accept-encoding", "deflate");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_caseInsensitiveHeaders__23.equals(res));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_caseInsensitiveHeaders__23.equals(o_caseInsensitiveHeaders__20));
        org.junit.Assert.assertEquals("deflate", res.header("Accept-Encoding"));
        org.junit.Assert.assertEquals("deflate", res.header("accept-Encoding"));
    }

    @org.junit.Test
    public void userAgent() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        org.junit.Assert.assertEquals(org.jsoup.helper.HttpConnection.DEFAULT_UA, con.request().header("User-Agent"));
        // AssertGenerator replace invocation
        org.jsoup.Connection o_userAgent__6 = con.userAgent("Mozilla");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_userAgent__6.equals(con));
        org.junit.Assert.assertEquals("Mozilla", con.request().header("User-Agent"));
    }

    @org.junit.Test
    public void timeout() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        org.junit.Assert.assertEquals((30 * 1000), con.request().timeout());
        // AssertGenerator replace invocation
        org.jsoup.Connection o_timeout__6 = con.timeout(1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_timeout__6.equals(con));
        org.junit.Assert.assertEquals(1000, con.request().timeout());
    }

    @org.junit.Test
    public void referrer() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_referrer__3 = con.referrer("http://foo.com");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_referrer__3.equals(con));
        org.junit.Assert.assertEquals("http://foo.com", con.request().header("Referer"));
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#caseInsensitiveHeaders */
    @org.junit.Test
    public void caseInsensitiveHeaders_literalMutation66_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection.Response res = new org.jsoup.helper.HttpConnection.Response();
            java.util.Map<java.lang.String, java.lang.String> headers = res.headers();
            headers.put("Accept-Encoding", "gzip");
            headers.put("content-type", "text/html");
            headers.put("refErrer", "http://example.com");
            // MethodAssertGenerator build local variable
            Object o_8_0 = res.hasHeader("Accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_10_0 = res.hasHeader("accept-encoding");
            // MethodAssertGenerator build local variable
            Object o_12_0 = res.hasHeader("accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_14_0 = res.header("accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_16_0 = res.header("Content-Type");
            // MethodAssertGenerator build local variable
            Object o_18_0 = res.header("Referrer");
            res.removeHeader("");
            // MethodAssertGenerator build local variable
            Object o_21_0 = res.hasHeader("content-type");
            res.header("accept-encoding", "deflate");
            // MethodAssertGenerator build local variable
            Object o_24_0 = res.header("Accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_26_0 = res.header("accept-Encoding");
            org.junit.Assert.fail("caseInsensitiveHeaders_literalMutation66 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#caseInsensitiveHeaders */
    @org.junit.Test
    public void caseInsensitiveHeaders_literalMutation36_failAssert83() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection.Response res = new org.jsoup.helper.HttpConnection.Response();
            java.util.Map<java.lang.String, java.lang.String> headers = res.headers();
            headers.put("Accept-Encoding", "gzip");
            headers.put("content-type", "text/html");
            headers.put("refErrer", "http://example.com");
            // MethodAssertGenerator build local variable
            Object o_8_0 = res.hasHeader("");
            // MethodAssertGenerator build local variable
            Object o_10_0 = res.hasHeader("accept-encoding");
            // MethodAssertGenerator build local variable
            Object o_12_0 = res.hasHeader("accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_14_0 = res.header("accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_16_0 = res.header("Content-Type");
            // MethodAssertGenerator build local variable
            Object o_18_0 = res.header("Referrer");
            res.removeHeader("Content-Type");
            // MethodAssertGenerator build local variable
            Object o_21_0 = res.hasHeader("content-type");
            res.header("accept-encoding", "deflate");
            // MethodAssertGenerator build local variable
            Object o_24_0 = res.header("Accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_26_0 = res.header("accept-Encoding");
            org.junit.Assert.fail("caseInsensitiveHeaders_literalMutation36 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#caseInsensitiveHeaders */
    @org.junit.Test(timeout = 10000)
    public void caseInsensitiveHeaders_add4() {
        org.jsoup.Connection.Response res = new org.jsoup.helper.HttpConnection.Response();
        java.util.Map<java.lang.String, java.lang.String> headers = res.headers();
        headers.put("Accept-Encoding", "gzip");
        headers.put("content-type", "text/html");
        headers.put("refErrer", "http://example.com");
        org.junit.Assert.assertTrue(res.hasHeader("Accept-Encoding"));
        org.junit.Assert.assertTrue(res.hasHeader("accept-encoding"));
        org.junit.Assert.assertTrue(res.hasHeader("accept-Encoding"));
        org.junit.Assert.assertEquals("gzip", res.header("accept-Encoding"));
        org.junit.Assert.assertEquals("text/html", res.header("Content-Type"));
        org.junit.Assert.assertEquals("http://example.com", res.header("Referrer"));
        // AssertGenerator replace invocation
        org.jsoup.Connection.Response o_caseInsensitiveHeaders_add4__20 = // MethodCallAdder
res.removeHeader("Content-Type");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).statusCode(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_caseInsensitiveHeaders_add4__20.equals(res));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).url());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).charset());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).contentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).method());
        // AssertGenerator add assertion
        java.util.LinkedHashMap map_376101124 = new java.util.LinkedHashMap<Object, Object>();	org.junit.Assert.assertEquals(map_376101124, ((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).cookies());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).statusMessage());
        res.removeHeader("Content-Type");
        org.junit.Assert.assertFalse(res.hasHeader("content-type"));
        res.header("accept-encoding", "deflate");
        org.junit.Assert.assertEquals("deflate", res.header("Accept-Encoding"));
        org.junit.Assert.assertEquals("deflate", res.header("accept-Encoding"));
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#caseInsensitiveHeaders */
    @org.junit.Test(timeout = 10000)
    public void caseInsensitiveHeaders_add4_cf614_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_22_1 = 0;
            org.jsoup.Connection.Response res = new org.jsoup.helper.HttpConnection.Response();
            java.util.Map<java.lang.String, java.lang.String> headers = res.headers();
            headers.put("Accept-Encoding", "gzip");
            headers.put("content-type", "text/html");
            headers.put("refErrer", "http://example.com");
            // MethodAssertGenerator build local variable
            Object o_8_0 = res.hasHeader("Accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_10_0 = res.hasHeader("accept-encoding");
            // MethodAssertGenerator build local variable
            Object o_12_0 = res.hasHeader("accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_14_0 = res.header("accept-Encoding");
            // MethodAssertGenerator build local variable
            Object o_16_0 = res.header("Content-Type");
            // MethodAssertGenerator build local variable
            Object o_18_0 = res.header("Referrer");
            // AssertGenerator replace invocation
            org.jsoup.Connection.Response o_caseInsensitiveHeaders_add4__20 = // MethodCallAdder
res.removeHeader("Content-Type");
            // MethodAssertGenerator build local variable
            Object o_22_0 = ((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).statusCode();
            // MethodAssertGenerator build local variable
            Object o_24_0 = o_caseInsensitiveHeaders_add4__20.equals(res);
            // MethodAssertGenerator build local variable
            Object o_26_0 = ((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).url();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).charset();
            // MethodAssertGenerator build local variable
            Object o_30_0 = ((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).contentType();
            // MethodAssertGenerator build local variable
            Object o_32_0 = ((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).method();
            // AssertGenerator add assertion
            java.util.LinkedHashMap map_376101124 = new java.util.LinkedHashMap<Object, Object>();	org.junit.Assert.assertEquals(map_376101124, ((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).cookies());;
            // MethodAssertGenerator build local variable
            Object o_36_0 = ((org.jsoup.helper.HttpConnection.Response)o_caseInsensitiveHeaders_add4__20).statusMessage();
            res.removeHeader("Content-Type");
            // MethodAssertGenerator build local variable
            Object o_39_0 = res.hasHeader("content-type");
            res.header("accept-encoding", "deflate");
            // MethodAssertGenerator build local variable
            Object o_42_0 = res.header("Accept-Encoding");
            // StatementAdderOnAssert create null value
            java.net.URL vc_260 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_258 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_258.connect(vc_260);
            // MethodAssertGenerator build local variable
            Object o_50_0 = res.header("accept-Encoding");
            org.junit.Assert.fail("caseInsensitiveHeaders_add4_cf614 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#caseInsensitiveHeaders */
    @org.junit.Test(timeout = 10000)
    public void caseInsensitiveHeaders_cf229_failAssert74_literalMutation3999_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection.Response res = new org.jsoup.helper.HttpConnection.Response();
                java.util.Map<java.lang.String, java.lang.String> headers = res.headers();
                headers.put("Accept-Encoding", "gzip");
                headers.put("content-type", "text/html");
                headers.put("refErrer", "http://example.com");
                // MethodAssertGenerator build local variable
                Object o_8_0 = res.hasHeader("Accept-Encoding");
                // MethodAssertGenerator build local variable
                Object o_10_0 = res.hasHeader("accept-encoding");
                // MethodAssertGenerator build local variable
                Object o_12_0 = res.hasHeader("accept-Encoding");
                // MethodAssertGenerator build local variable
                Object o_14_0 = res.header("accept-Encoding");
                // MethodAssertGenerator build local variable
                Object o_16_0 = res.header("Content-Type");
                // MethodAssertGenerator build local variable
                Object o_18_0 = res.header("Referrer");
                res.removeHeader("");
                // MethodAssertGenerator build local variable
                Object o_21_0 = res.hasHeader("content-type");
                res.header("accept-encoding", "deflate");
                // MethodAssertGenerator build local variable
                Object o_24_0 = res.header("Accept-Encoding");
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_10 = "http://example.com";
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_106 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_106.userAgent(String_vc_10);
                // MethodAssertGenerator build local variable
                Object o_32_0 = res.header("accept-Encoding");
                org.junit.Assert.fail("caseInsensitiveHeaders_cf229 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("caseInsensitiveHeaders_cf229_failAssert74_literalMutation3999 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#caseInsensitiveHeaders */
    @org.junit.Test(timeout = 10000)
    public void caseInsensitiveHeaders_add3_cf387_failAssert21_literalMutation6362_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection.Response res = new org.jsoup.helper.HttpConnection.Response();
                java.util.Map<java.lang.String, java.lang.String> headers = res.headers();
                headers.put("Accept-Encoding", "gzip");
                headers.put("content-type", "text/html");
                // AssertGenerator replace invocation
                java.lang.String o_caseInsensitiveHeaders_add3__7 = // MethodCallAdder
headers.put("refErrer", "http://example.com");
                // MethodAssertGenerator build local variable
                Object o_9_0 = o_caseInsensitiveHeaders_add3__7;
                headers.put("refErrer", "http://example.com");
                // MethodAssertGenerator build local variable
                Object o_12_0 = res.hasHeader("");
                // MethodAssertGenerator build local variable
                Object o_14_0 = res.hasHeader("accept-encoding");
                // MethodAssertGenerator build local variable
                Object o_16_0 = res.hasHeader("accept-Encoding");
                // MethodAssertGenerator build local variable
                Object o_18_0 = res.header("accept-Encoding");
                // MethodAssertGenerator build local variable
                Object o_20_0 = res.header("Content-Type");
                // MethodAssertGenerator build local variable
                Object o_22_0 = res.header("Referrer");
                res.removeHeader("Content-Type");
                // MethodAssertGenerator build local variable
                Object o_25_0 = res.hasHeader("content-type");
                res.header("accept-encoding", "deflate");
                // MethodAssertGenerator build local variable
                Object o_28_0 = res.header("Accept-Encoding");
                // StatementAdderOnAssert create null value
                java.lang.String[] vc_155 = (java.lang.String[])null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_153 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_153.data(vc_155);
                // MethodAssertGenerator build local variable
                Object o_36_0 = res.header("accept-Encoding");
                org.junit.Assert.fail("caseInsensitiveHeaders_add3_cf387 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("caseInsensitiveHeaders_add3_cf387_failAssert21_literalMutation6362 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7902() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create null value
        java.net.Proxy vc_839 = (java.net.Proxy)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_839);
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7902__6 = // StatementAdderMethod cloned existing statement
con.proxy(vc_839);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7902__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7924() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create null value
        java.lang.String vc_851 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_851);
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7924__6 = // StatementAdderMethod cloned existing statement
con.requestBody(vc_851);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7924__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7958() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_91 = "http://example.com";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_91, "http://example.com");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7958__6 = // StatementAdderMethod cloned existing statement
con.userAgent(String_vc_91);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7958__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7862() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create random local variable
        boolean vc_813 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_813);
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7862__6 = // StatementAdderMethod cloned existing statement
con.ignoreContentType(vc_813);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7862__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7969_failAssert44() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create null value
            java.lang.String vc_877 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_877);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7969 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7832() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAddOnAssert local variable replacement
        java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
        // AssertGenerator add assertion
        java.util.ArrayList collection_372448810 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_372448810, values);;
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7832__8 = // StatementAdderMethod cloned existing statement
con.data(values);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7832__8.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7964() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create random local variable
        boolean vc_874 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_874);
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7964__6 = // StatementAdderMethod cloned existing statement
con.validateTLSCertificates(vc_874);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7964__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7950_failAssert33() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create null value
            java.net.URL vc_866 = (java.net.URL)null;
            // StatementAdderMethod cloned existing statement
            con.url(vc_866);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7950 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7844() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create random local variable
        boolean vc_802 = true;
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(vc_802);
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7844__6 = // StatementAdderMethod cloned existing statement
con.followRedirects(vc_802);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7844__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7910() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_88 = "http://example.com";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_88, "http://example.com");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7910__6 = // StatementAdderMethod cloned existing statement
con.referrer(String_vc_88);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7910__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7943() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_90 = "http://example.com";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_90, "http://example.com");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7943__6 = // StatementAdderMethod cloned existing statement
con.url(String_vc_90);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7943__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7878() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create null value
        org.jsoup.parser.Parser vc_826 = (org.jsoup.parser.Parser)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_826);
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7878__6 = // StatementAdderMethod cloned existing statement
con.parser(vc_826);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7878__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7868() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create random local variable
        int vc_819 = 2118753082;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_819, 2118753082);
        // AssertGenerator replace invocation
        org.jsoup.Connection o_connectWithUrl_cf7868__6 = // StatementAdderMethod cloned existing statement
con.maxBodySize(vc_819);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_connectWithUrl_cf7868__6.equals(con));
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7894_failAssert5() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create random local variable
            int vc_836 = 1039365417;
            // StatementAdderOnAssert create null value
            java.lang.String vc_834 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_834, vc_836);
            // MethodAssertGenerator build local variable
            Object o_10_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7894 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7970() throws java.net.MalformedURLException {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_92 = "http://example.com";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_92, "http://example.com");
        // AssertGenerator replace invocation
        org.jsoup.Connection.KeyVal o_connectWithUrl_cf7970__6 = // StatementAdderMethod cloned existing statement
con.data(String_vc_92);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_connectWithUrl_cf7970__6);
        org.junit.Assert.assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7839_failAssert34() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_798 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_798);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7839 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7885_failAssert56() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create null value
            java.lang.String vc_830 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_830);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7885 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7831_failAssert0() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create null value
            java.util.Collection<org.jsoup.Connection.KeyVal> vc_794 = (java.util.Collection)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_794);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7831 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7824_failAssert57() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create null value
            java.lang.String[] vc_790 = (java.lang.String[])null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_790);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7824 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7832_cf8339_failAssert33() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAddOnAssert local variable replacement
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            // AssertGenerator add assertion
            java.util.ArrayList collection_372448810 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_372448810, values);;
            // AssertGenerator replace invocation
            org.jsoup.Connection o_connectWithUrl_cf7832__8 = // StatementAdderMethod cloned existing statement
con.data(values);
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_connectWithUrl_cf7832__8.equals(con);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_1052 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_1052);
            // MethodAssertGenerator build local variable
            Object o_18_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7832_cf8339 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7868_cf12477_failAssert12() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_6_1 = 2118753082;
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create random local variable
            int vc_819 = 2118753082;
            // MethodAssertGenerator build local variable
            Object o_6_0 = vc_819;
            // AssertGenerator replace invocation
            org.jsoup.Connection o_connectWithUrl_cf7868__6 = // StatementAdderMethod cloned existing statement
con.maxBodySize(vc_819);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_connectWithUrl_cf7868__6.equals(con);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_2935 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_2935);
            // MethodAssertGenerator build local variable
            Object o_16_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7868_cf12477 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7959_cf11416_failAssert40() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_871 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_6_0 = vc_871;
            // AssertGenerator replace invocation
            org.jsoup.Connection o_connectWithUrl_cf7959__6 = // StatementAdderMethod cloned existing statement
con.userAgent(vc_871);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_connectWithUrl_cf7959__6.equals(con);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_2460 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.headers(vc_2460);
            // MethodAssertGenerator build local variable
            Object o_16_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7959_cf11416 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7896_failAssert15_literalMutation12813() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create random local variable
            int vc_836 = 519682708;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_836, 519682708);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_835 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_835, "");
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_835, vc_836);
            // MethodAssertGenerator build local variable
            Object o_10_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7896 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7943_cf10113_failAssert47() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_90 = "http://example.com";
            // MethodAssertGenerator build local variable
            Object o_6_0 = String_vc_90;
            // AssertGenerator replace invocation
            org.jsoup.Connection o_connectWithUrl_cf7943__6 = // StatementAdderMethod cloned existing statement
con.url(String_vc_90);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_connectWithUrl_cf7943__6.equals(con);
            // StatementAdderOnAssert create random local variable
            int vc_1875 = -302322764;
            // StatementAdderMethod cloned existing statement
            con.timeout(vc_1875);
            // MethodAssertGenerator build local variable
            Object o_16_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7943_cf10113 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7924_cf9613_failAssert48() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create null value
            java.lang.String vc_851 = (java.lang.String)null;
            // MethodAssertGenerator build local variable
            Object o_6_0 = vc_851;
            // AssertGenerator replace invocation
            org.jsoup.Connection o_connectWithUrl_cf7924__6 = // StatementAdderMethod cloned existing statement
con.requestBody(vc_851);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_connectWithUrl_cf7924__6.equals(con);
            // StatementAdderOnAssert create null value
            java.lang.String vc_1639 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_1639);
            // MethodAssertGenerator build local variable
            Object o_16_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7924_cf9613 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7911_cf8798_failAssert30() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_844 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_6_0 = vc_844;
            // AssertGenerator replace invocation
            org.jsoup.Connection o_connectWithUrl_cf7911__6 = // StatementAdderMethod cloned existing statement
con.referrer(vc_844);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_connectWithUrl_cf7911__6.equals(con);
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_1270 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_1270.connect(vc_844);
            // MethodAssertGenerator build local variable
            Object o_16_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7911_cf8798 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7926_cf8652_failAssert34() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_852 = new java.lang.String();
            // MethodAssertGenerator build local variable
            Object o_6_0 = vc_852;
            // AssertGenerator replace invocation
            org.jsoup.Connection o_connectWithUrl_cf7926__6 = // StatementAdderMethod cloned existing statement
con.requestBody(vc_852);
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_connectWithUrl_cf7926__6.equals(con);
            // StatementAdderOnAssert create null value
            java.lang.String vc_1211 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_1211);
            // MethodAssertGenerator build local variable
            Object o_16_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7926_cf8652 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7942_failAssert12_literalMutation12791() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://exYmple.com"));
            // StatementAdderOnAssert create null value
            java.lang.String vc_862 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_862);
            // StatementAdderMethod cloned existing statement
            con.url(vc_862);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().url().toExternalForm();
            org.junit.Assert.fail("connectWithUrl_cf7942 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#connectWithUrl */
    @org.junit.Test(timeout = 10000)
    public void connectWithUrl_cf7955_failAssert3_add12729_cf13379_failAssert1() throws java.net.MalformedURLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect(new java.net.URL("http://example.com"));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_91 = "http://example.com";
                // MethodAssertGenerator build local variable
                Object o_8_0 = String_vc_91;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_868 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_3163 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                con.data(vc_3163);
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_868;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_868.userAgent(String_vc_91);
                // StatementAdderMethod cloned existing statement
                vc_868.userAgent(String_vc_91);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().url().toExternalForm();
                org.junit.Assert.fail("connectWithUrl_cf7955 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("connectWithUrl_cf7955_failAssert3_add12729_cf13379 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15459_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_3824 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_3824);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15459 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15553_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.lang.String vc_3878 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_3878);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15553 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15637_failAssert30() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.lang.String vc_3925 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_3925);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15637 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test
    public void cookie_literalMutation15418_failAssert25() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("", "Val");
            // MethodAssertGenerator build local variable
            Object o_4_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_literalMutation15418 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15499_failAssert41() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.util.Collection<org.jsoup.Connection.KeyVal> vc_3842 = (java.util.Collection)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_3842);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15499 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15525_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_3857 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.headers(vc_3857);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15525 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15442_failAssert55() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.net.URL vc_3816 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_3814 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_3814.connect(vc_3816);
            // MethodAssertGenerator build local variable
            Object o_10_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15442 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15492_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.lang.String[] vc_3838 = (java.lang.String[])null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_3838);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15492 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15507_failAssert32() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_3846 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_3846);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15507 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test
    public void cookie_literalMutation15413_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
            con.cookie("Name", "Val");
            // MethodAssertGenerator build local variable
            Object o_4_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_literalMutation15413 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test
    public void cookie_literalMutation15428_failAssert62() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // MethodAssertGenerator build local variable
            Object o_4_0 = con.request().cookie("");
            org.junit.Assert.fail("cookie_literalMutation15428 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15536_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create random local variable
            int vc_3867 = -1481987470;
            // StatementAdderMethod cloned existing statement
            con.maxBodySize(vc_3867);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15536 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15605_failAssert46() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create random local variable
            int vc_3907 = -142264091;
            // StatementAdderMethod cloned existing statement
            con.timeout(vc_3907);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15605 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_add15412() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_cookie_add15412__3 = // MethodCallAdder
con.cookie("Name", "Val");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_cookie_add15412__3.equals(con));
        con.cookie("Name", "Val");
        org.junit.Assert.assertEquals("Val", con.request().cookie("Name"));
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15554_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_455 = "Name";
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(String_vc_455);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15554 should have thrown IllegalCharsetNameException");
        } catch (java.nio.charset.IllegalCharsetNameException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15562_failAssert51() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create random local variable
            int vc_3884 = -329762751;
            // StatementAdderOnAssert create null value
            java.lang.String vc_3882 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_3882, vc_3884);
            // MethodAssertGenerator build local variable
            Object o_10_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15562 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15490_failAssert48_literalMutation16820_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                con.cookie("", "Val");
                // StatementAdderOnAssert create null value
                java.lang.String[] vc_3838 = (java.lang.String[])null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_3836 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_3836.data(vc_3838);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().cookie("Name");
                org.junit.Assert.fail("cookie_cf15490 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cookie_cf15490_failAssert48_literalMutation16820 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15618_failAssert57_add16996() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_cookie_cf15618_failAssert57_add16996__5 = // MethodCallAdder
con.cookie("Name", "Val");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_cookie_cf15618_failAssert57_add16996__5.equals(con));
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.net.URL vc_3914 = (java.net.URL)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3914);
            // StatementAdderMethod cloned existing statement
            con.url(vc_3914);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15618 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15457_failAssert56_literalMutation16976_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
                con.cookie("Name", "Val");
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.String, java.lang.String> vc_3824 = (java.util.Map)null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_3822 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_3822.cookies(vc_3824);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().cookie("Name");
                org.junit.Assert.fail("cookie_cf15457 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cookie_cf15457_failAssert56_literalMutation16976 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15637_failAssert30_literalMutation16496() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_cookie_cf15637_failAssert30_literalMutation16496__5 = con.cookie("NMame", "Val");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_cookie_cf15637_failAssert30_literalMutation16496__5.equals(con));
            // StatementAdderOnAssert create null value
            java.lang.String vc_3925 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3925);
            // StatementAdderMethod cloned existing statement
            con.data(vc_3925);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15637 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15459_failAssert8_add16115() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_cookie_cf15459_failAssert8_add16115__5 = // MethodCallAdder
con.cookie("Name", "Val");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_cookie_cf15459_failAssert8_add16115__5.equals(con));
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_3824 = (java.util.Map)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3824);
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_3824);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_cf15459 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_add15412_cf15802_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_cookie_add15412__3 = // MethodCallAdder
con.cookie("Name", "Val");
            // MethodAssertGenerator build local variable
            Object o_5_0 = o_cookie_add15412__3.equals(con);
            con.cookie("Name", "Val");
            // StatementAdderOnAssert create null value
            java.lang.String vc_4005 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_4005);
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().cookie("Name");
            org.junit.Assert.fail("cookie_add15412_cf15802 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15591_failAssert26_literalMutation16406_cf40481_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                con.cookie("Name", "Val");
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_3900 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_8_0 = vc_3900;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_3897 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.lang.String[] vc_14379 = (java.lang.String[])null;
                // StatementAdderMethod cloned existing statement
                con.data(vc_14379);
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_3897;
                // StatementAdderMethod cloned existing statement
                vc_3897.requestBody(vc_3900);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().cookie("");
                org.junit.Assert.fail("cookie_cf15591 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cookie_cf15591_failAssert26_literalMutation16406_cf40481 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15643_failAssert2_literalMutation15984_literalMutation41905_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
                // AssertGenerator replace invocation
                org.jsoup.Connection o_cookie_cf15643_failAssert2_literalMutation15984__5 = con.cookie("Name", "");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_cookie_cf15643_failAssert2_literalMutation15984__5.equals(con);
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_3927 = (org.jsoup.helper.HttpConnection)null;
                // MethodAssertGenerator build local variable
                Object o_11_0 = vc_3927;
                // StatementAdderMethod cloned existing statement
                vc_3927.request();
                // MethodAssertGenerator build local variable
                Object o_8_0 = con.request().cookie("Name");
                org.junit.Assert.fail("cookie_cf15643 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cookie_cf15643_failAssert2_literalMutation15984_literalMutation41905 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15551_failAssert27_literalMutation16432_cf32155_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                con.cookie("Name", "Val");
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_455 = "Na?me";
                // MethodAssertGenerator build local variable
                Object o_8_0 = String_vc_455;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_3876 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.String, java.lang.String> vc_10682 = (java.util.Map)null;
                // StatementAdderMethod cloned existing statement
                con.cookies(vc_10682);
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_3876;
                // StatementAdderMethod cloned existing statement
                vc_3876.postDataCharset(String_vc_455);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().cookie("Name");
                org.junit.Assert.fail("cookie_cf15551 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cookie_cf15551_failAssert27_literalMutation16432_cf32155 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#cookie */
    @org.junit.Test(timeout = 10000)
    public void cookie_cf15589_failAssert35_literalMutation16603_cf30528_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                con.cookie("Name", "Val");
                // StatementAdderOnAssert create null value
                java.lang.String vc_3899 = (java.lang.String)null;
                // MethodAssertGenerator build local variable
                Object o_8_0 = vc_3899;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_3897 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create literal from method
                int int_vc_1183 = 10000;
                // StatementAdderMethod cloned existing statement
                con.proxy(vc_3899, int_vc_1183);
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_3897;
                // StatementAdderMethod cloned existing statement
                vc_3897.requestBody(vc_3899);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().cookie("Ncame");
                org.junit.Assert.fail("cookie_cf15589 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("cookie_cf15589_failAssert35_literalMutation16603_cf30528 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55188_failAssert34() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // StatementAdderOnAssert create null value
            java.util.Collection<org.jsoup.Connection.KeyVal> vc_20606 = (java.util.Collection)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_20606);
            // MethodAssertGenerator build local variable
            Object o_21_0 = two.value();
            org.junit.Assert.fail("data_cf55188 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test
    public void data_literalMutation55088_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // MethodAssertGenerator build local variable
            Object o_17_0 = two.value();
            org.junit.Assert.fail("data_literalMutation55088 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55128_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // StatementAdderOnAssert create null value
            java.net.URL vc_20580 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_20578 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_20578.connect(vc_20580);
            // MethodAssertGenerator build local variable
            Object o_23_0 = two.value();
            org.junit.Assert.fail("data_cf55128 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55214_failAssert52() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_20621 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.headers(vc_20621);
            // MethodAssertGenerator build local variable
            Object o_21_0 = two.value();
            org.junit.Assert.fail("data_cf55214 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_add55087() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_data_add55087__3 = // MethodCallAdder
con.data("Name", "Val", "Foo", "bar");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_data_add55087__3.equals(con));
        con.data("Name", "Val", "Foo", "bar");
        java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
        java.lang.Object[] data = values.toArray();
        org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
        org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
        org.junit.Assert.assertEquals("Name", one.key());
        org.junit.Assert.assertEquals("Val", one.value());
        org.junit.Assert.assertEquals("Foo", two.key());
        org.junit.Assert.assertEquals("bar", two.value());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55257_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // StatementAdderOnAssert create literal from method
            int int_vc_2464 = 1;
            // StatementAdderOnAssert create null value
            java.lang.String vc_20646 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_20646, int_vc_2464);
            // MethodAssertGenerator build local variable
            Object o_23_0 = two.value();
            org.junit.Assert.fail("data_cf55257 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55145_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_20588 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_20588);
            // MethodAssertGenerator build local variable
            Object o_21_0 = two.value();
            org.junit.Assert.fail("data_cf55145 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55189_failAssert63() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // StatementAdderMethod cloned existing statement
            con.data(values);
            // MethodAssertGenerator build local variable
            Object o_19_0 = two.value();
            org.junit.Assert.fail("data_cf55189 should have thrown ConcurrentModificationException");
        } catch (java.util.ConcurrentModificationException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55246_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_2462 = "Val";
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(String_vc_2462);
            // MethodAssertGenerator build local variable
            Object o_21_0 = two.value();
            org.junit.Assert.fail("data_cf55246 should have thrown IllegalCharsetNameException");
        } catch (java.nio.charset.IllegalCharsetNameException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55341_failAssert68() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // StatementAdderOnAssert create null value
            java.lang.String vc_20689 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_20689);
            // MethodAssertGenerator build local variable
            Object o_21_0 = two.value();
            org.junit.Assert.fail("data_cf55341 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55179_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_11_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_13_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_15_0 = two.key();
            // StatementAdderOnAssert create null value
            java.lang.String[] vc_20602 = (java.lang.String[])null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_20602);
            // MethodAssertGenerator build local variable
            Object o_21_0 = two.value();
            org.junit.Assert.fail("data_cf55179 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55242_failAssert9_literalMutation55908_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
                con.data("Name", "Val", "Foo", "bar");
                java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
                java.lang.Object[] data = values.toArray();
                org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
                org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
                // MethodAssertGenerator build local variable
                Object o_11_0 = one.key();
                // MethodAssertGenerator build local variable
                Object o_13_0 = one.value();
                // MethodAssertGenerator build local variable
                Object o_15_0 = two.key();
                // StatementAdderOnAssert create null value
                java.lang.String vc_20642 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_20640 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_20640.postDataCharset(vc_20642);
                // MethodAssertGenerator build local variable
                Object o_23_0 = two.value();
                org.junit.Assert.fail("data_cf55242 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("data_cf55242_failAssert9_literalMutation55908 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_add55087_cf55420_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_data_add55087__3 = // MethodCallAdder
con.data("Name", "Val", "Foo", "bar");
            // MethodAssertGenerator build local variable
            Object o_5_0 = o_data_add55087__3.equals(con);
            con.data("Name", "Val", "Foo", "bar");
            java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
            java.lang.Object[] data = values.toArray();
            org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[0]));
            org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
            // MethodAssertGenerator build local variable
            Object o_15_0 = one.key();
            // MethodAssertGenerator build local variable
            Object o_17_0 = one.value();
            // MethodAssertGenerator build local variable
            Object o_19_0 = two.key();
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_20715 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_20715);
            // MethodAssertGenerator build local variable
            Object o_25_0 = two.value();
            org.junit.Assert.fail("data_add55087_cf55420 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55145_failAssert5_literalMutation55796_failAssert25_literalMutation58535() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // AssertGenerator replace invocation
                org.jsoup.Connection o_data_cf55145_failAssert5_literalMutation55796_failAssert25_literalMutation58535__7 = con.data("Name", "Val", "Foo", "u4p");
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_data_cf55145_failAssert5_literalMutation55796_failAssert25_literalMutation58535__7.equals(con));
                java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
                java.lang.Object[] data = values.toArray();
                org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[-1]));
                org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
                // MethodAssertGenerator build local variable
                Object o_11_0 = one.key();
                // MethodAssertGenerator build local variable
                Object o_13_0 = one.value();
                // MethodAssertGenerator build local variable
                Object o_15_0 = two.key();
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.String, java.lang.String> vc_20588 = (java.util.Map)null;
                // StatementAdderMethod cloned existing statement
                con.cookies(vc_20588);
                // MethodAssertGenerator build local variable
                Object o_21_0 = two.value();
                org.junit.Assert.fail("data_cf55145 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("data_cf55145_failAssert5_literalMutation55796 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#data */
    @org.junit.Test(timeout = 10000)
    public void data_cf55200_failAssert29_literalMutation56478_failAssert19_literalMutation58324_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                    con.data("Name", "Val", "", "bar");
                    java.util.Collection<org.jsoup.Connection.KeyVal> values = con.request().data();
                    java.lang.Object[] data = values.toArray();
                    org.jsoup.Connection.KeyVal one = ((org.jsoup.Connection.KeyVal) (data[-1]));
                    org.jsoup.Connection.KeyVal two = ((org.jsoup.Connection.KeyVal) (data[1]));
                    // MethodAssertGenerator build local variable
                    Object o_11_0 = one.key();
                    // MethodAssertGenerator build local variable
                    Object o_13_0 = one.value();
                    // MethodAssertGenerator build local variable
                    Object o_15_0 = two.key();
                    // StatementAdderOnAssert create random local variable
                    boolean vc_20614 = true;
                    // StatementAdderOnAssert create null value
                    org.jsoup.helper.HttpConnection vc_20612 = (org.jsoup.helper.HttpConnection)null;
                    // StatementAdderMethod cloned existing statement
                    vc_20612.followRedirects(vc_20614);
                    // MethodAssertGenerator build local variable
                    Object o_23_0 = two.value();
                    org.junit.Assert.fail("data_cf55200 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("data_cf55200_failAssert29_literalMutation56478 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("data_cf55200_failAssert29_literalMutation56478_failAssert19_literalMutation58324 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58867_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // StatementAdderOnAssert create null value
            java.lang.String vc_20936 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.userAgent(vc_20936);
            // MethodAssertGenerator build local variable
            Object o_19_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58867 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58735_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // StatementAdderOnAssert create null value
            java.util.Collection<org.jsoup.Connection.KeyVal> vc_20860 = (java.util.Collection)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_20860);
            // MethodAssertGenerator build local variable
            Object o_19_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58735 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58778_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // StatementAdderOnAssert create random local variable
            int vc_20885 = -1940842383;
            // StatementAdderMethod cloned existing statement
            con.maxBodySize(vc_20885);
            // MethodAssertGenerator build local variable
            Object o_19_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58778 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58804_failAssert48() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // StatementAdderOnAssert create random local variable
            int vc_20902 = -418568101;
            // StatementAdderOnAssert create null value
            java.lang.String vc_20900 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_20900, vc_20902);
            // MethodAssertGenerator build local variable
            Object o_21_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58804 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test
    public void headers_literalMutation58617_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // MethodAssertGenerator build local variable
            Object o_15_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_literalMutation58617 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58675_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // StatementAdderOnAssert create null value
            java.net.URL vc_20834 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_20832 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_20832.connect(vc_20834);
            // MethodAssertGenerator build local variable
            Object o_21_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58675 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58795_failAssert43() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // StatementAdderOnAssert create null value
            java.lang.String vc_20896 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_20896);
            // MethodAssertGenerator build local variable
            Object o_19_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58795 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58796_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_2491 = "content-type";
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(String_vc_2491);
            // MethodAssertGenerator build local variable
            Object o_19_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58796 should have thrown IllegalCharsetNameException");
        } catch (java.nio.charset.IllegalCharsetNameException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_add58614_cf59182_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            // AssertGenerator replace invocation
            java.lang.String o_headers_add58614__7 = // MethodCallAdder
headers.put("Host", "http://example.com");
            // MethodAssertGenerator build local variable
            Object o_9_0 = o_headers_add58614__7;
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_13_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_16_0 = con.request().header("Connection");
            // StatementAdderOnAssert create null value
            java.lang.String vc_21070 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_21070);
            // MethodAssertGenerator build local variable
            Object o_23_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_add58614_cf59182 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58810_failAssert45_literalMutation61378_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
                java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
                headers.put("content-type", "text/html");
                headers.put("Connection", "keep-alive");
                headers.put("Host", "http://example.com");
                con.headers(headers);
                // MethodAssertGenerator build local variable
                Object o_9_0 = con.request().header("content-type");
                // MethodAssertGenerator build local variable
                Object o_12_0 = con.request().header("Connection");
                // StatementAdderOnAssert create null value
                java.net.Proxy vc_20905 = (java.net.Proxy)null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_20903 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_20903.proxy(vc_20905);
                // MethodAssertGenerator build local variable
                Object o_21_0 = con.request().header("Host");
                org.junit.Assert.fail("headers_cf58810 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("headers_cf58810_failAssert45_literalMutation61378 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58831_failAssert8_literalMutation60039() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            // AssertGenerator replace invocation
            java.lang.String o_headers_cf58831_failAssert8_literalMutation60039__7 = headers.put("content-type", "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_headers_cf58831_failAssert8_literalMutation60039__7);
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "keep-alive");
            // StatementAdderOnAssert create null value
            java.lang.String vc_20917 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_20917);
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_20915 = (org.jsoup.helper.HttpConnection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_20915);
            // StatementAdderMethod cloned existing statement
            vc_20915.requestBody(vc_20917);
            // MethodAssertGenerator build local variable
            Object o_21_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58831 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58806_failAssert17_literalMutation60410() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "text/html");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "keep-alive");
            // StatementAdderOnAssert create random local variable
            int vc_20902 = -418568101;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_20902, -418568101);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_20901 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_20901, "");
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_20901, vc_20902);
            // MethodAssertGenerator build local variable
            Object o_21_0 = con.request().header("H<st");
            org.junit.Assert.fail("headers_cf58806 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58819_failAssert71_literalMutation62442() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            // AssertGenerator replace invocation
            java.lang.String o_headers_cf58819_failAssert71_literalMutation62442__7 = headers.put("content-xype", "text/html");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_headers_cf58819_failAssert71_literalMutation62442__7);
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_9_0);
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "keep-alive");
            // StatementAdderOnAssert create null value
            java.lang.String vc_20909 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_20909);
            // StatementAdderMethod cloned existing statement
            con.referrer(vc_20909);
            // MethodAssertGenerator build local variable
            Object o_19_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58819 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_add58614_cf59016_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            // AssertGenerator replace invocation
            java.lang.String o_headers_add58614__7 = // MethodCallAdder
headers.put("Host", "http://example.com");
            // MethodAssertGenerator build local variable
            Object o_9_0 = o_headers_add58614__7;
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_13_0 = con.request().header("content-type");
            // MethodAssertGenerator build local variable
            Object o_16_0 = con.request().header("Connection");
            // StatementAdderOnAssert create null value
            java.lang.String[] vc_20983 = (java.lang.String[])null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_20983);
            // MethodAssertGenerator build local variable
            Object o_23_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_add58614_cf59016 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58778_failAssert16_literalMutation60319() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            // AssertGenerator replace invocation
            java.lang.String o_headers_cf58778_failAssert16_literalMutation60319__8 = headers.put("Connction", "keep-alive");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_headers_cf58778_failAssert16_literalMutation60319__8);
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "text/html");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_12_0);
            // StatementAdderOnAssert create random local variable
            int vc_20885 = -1940842383;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_20885, -1940842383);
            // StatementAdderMethod cloned existing statement
            con.maxBodySize(vc_20885);
            // MethodAssertGenerator build local variable
            Object o_19_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58778 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58860_failAssert65_literalMutation62195() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            // AssertGenerator replace invocation
            java.lang.String o_headers_cf58860_failAssert65_literalMutation62195__9 = headers.put("Host", "Y+UlRT_cL(&4KmIR_s");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_headers_cf58860_failAssert65_literalMutation62195__9);
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_9_0 = con.request().header("content-type");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, "text/html");
            // MethodAssertGenerator build local variable
            Object o_12_0 = con.request().header("Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "keep-alive");
            // StatementAdderOnAssert create null value
            java.net.URL vc_20932 = (java.net.URL)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_20932);
            // StatementAdderMethod cloned existing statement
            con.url(vc_20932);
            // MethodAssertGenerator build local variable
            Object o_19_0 = con.request().header("Host");
            org.junit.Assert.fail("headers_cf58860 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58796_failAssert35_literalMutation61019_cf89758_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
                java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
                headers.put("content-type", "text/html");
                headers.put("Connection", "keep-alive");
                headers.put("Host", "http://example.com");
                con.headers(headers);
                // MethodAssertGenerator build local variable
                Object o_9_0 = con.request().header("content-tpe");
                // MethodAssertGenerator build local variable
                Object o_15_0 = o_9_0;
                // MethodAssertGenerator build local variable
                Object o_12_0 = con.request().header("Connection");
                // MethodAssertGenerator build local variable
                Object o_21_0 = o_12_0;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_2491 = "content-type";
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_31978 = new java.lang.String();
                // StatementAdderMethod cloned existing statement
                con.url(vc_31978);
                // MethodAssertGenerator build local variable
                Object o_29_0 = String_vc_2491;
                // StatementAdderMethod cloned existing statement
                con.postDataCharset(String_vc_2491);
                // MethodAssertGenerator build local variable
                Object o_19_0 = con.request().header("Host");
                org.junit.Assert.fail("headers_cf58796 should have thrown IllegalCharsetNameException");
            } catch (java.nio.charset.IllegalCharsetNameException eee) {
            }
            org.junit.Assert.fail("headers_cf58796_failAssert35_literalMutation61019_cf89758 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_add58612_cf59406_failAssert9_literalMutation105039() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
            java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
            // AssertGenerator replace invocation
            java.lang.String o_headers_add58612__5 = // MethodCallAdder
headers.put("content-type", "text/html");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_headers_add58612__5;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_7_0);
            headers.put("content-type", "text/html");
            headers.put("Connection", "keep-alive");
            headers.put("Host", "http://example.com");
            con.headers(headers);
            // MethodAssertGenerator build local variable
            Object o_13_0 = con.request().header("content-type");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_0, "text/html");
            // MethodAssertGenerator build local variable
            Object o_16_0 = con.request().header("Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_16_0, "keep-alive");
            // StatementAdderOnAssert create random local variable
            int vc_21156 = -1442124701;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_21156, -1442124701);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_21155 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_21155, "");
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_21155, vc_21156);
            // MethodAssertGenerator build local variable
            Object o_25_0 = con.request().header("{NQZ");
            org.junit.Assert.fail("headers_add58612_cf59406 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#headers */
    @org.junit.Test(timeout = 10000)
    public void headers_cf58851_failAssert4_literalMutation59925_failAssert3_literalMutation104751() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com");
                java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>();
                // AssertGenerator replace invocation
                java.lang.String o_headers_cf58851_failAssert4_literalMutation59925_failAssert3_literalMutation104751__9 = headers.put("", "text/html");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_headers_cf58851_failAssert4_literalMutation59925_failAssert3_literalMutation104751__9);
                headers.put("Connection", "keep-alive");
                headers.put("Host", "http://example.com");
                con.headers(headers);
                // MethodAssertGenerator build local variable
                Object o_9_0 = con.request().header("content-type");
                // MethodAssertGenerator build local variable
                Object o_12_0 = con.request().header("");
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_20929 = new java.lang.String();
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_20926 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_20926.url(vc_20929);
                // MethodAssertGenerator build local variable
                Object o_21_0 = con.request().header("Host");
                org.junit.Assert.fail("headers_cf58851 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("headers_cf58851_failAssert4_literalMutation59925 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptyCookieNameAndVals */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptyCookieNameAndVals_cf106021_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            java.util.List<java.lang.String> cookieStrings = new java.util.ArrayList<java.lang.String>();
            cookieStrings.add(null);
            cookieStrings.add("");
            cookieStrings.add("one");
            cookieStrings.add("two=");
            cookieStrings.add("three=;");
            cookieStrings.add("four=data; Domain=.example.com; Path=/");
            headers.put("Set-Cookie", cookieStrings);
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // MethodAssertGenerator build local variable
            Object o_16_0 = res.cookies().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = res.cookie("one");
            // MethodAssertGenerator build local variable
            Object o_21_0 = res.cookie("two");
            // MethodAssertGenerator build local variable
            Object o_23_0 = res.cookie("three");
            // StatementAdderOnAssert create null value
            java.net.URL vc_37852 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_37850 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_37850.connect(vc_37852);
            // MethodAssertGenerator build local variable
            Object o_31_0 = res.cookie("four");
            org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_cf106021 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptyCookieNameAndVals */
    @org.junit.Test
    public void ignoresEmptyCookieNameAndVals_literalMutation105990_failAssert53() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            java.util.List<java.lang.String> cookieStrings = new java.util.ArrayList<java.lang.String>();
            cookieStrings.add(null);
            cookieStrings.add("");
            cookieStrings.add("one");
            cookieStrings.add("two=");
            cookieStrings.add("three=;");
            cookieStrings.add("four=data; Domain=.example.com; Path=/");
            headers.put("", cookieStrings);
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // MethodAssertGenerator build local variable
            Object o_16_0 = res.cookies().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = res.cookie("one");
            // MethodAssertGenerator build local variable
            Object o_21_0 = res.cookie("two");
            // MethodAssertGenerator build local variable
            Object o_23_0 = res.cookie("three");
            // MethodAssertGenerator build local variable
            Object o_25_0 = res.cookie("four");
            org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_literalMutation105990 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptyCookieNameAndVals */
    @org.junit.Test
    public void ignoresEmptyCookieNameAndVals_literalMutation106005_failAssert73() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            java.util.List<java.lang.String> cookieStrings = new java.util.ArrayList<java.lang.String>();
            cookieStrings.add(null);
            cookieStrings.add("");
            cookieStrings.add("one");
            cookieStrings.add("two=");
            cookieStrings.add("three=;");
            cookieStrings.add("four=data; Domain=.example.com; Path=/");
            headers.put("Set-Cookie", cookieStrings);
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // MethodAssertGenerator build local variable
            Object o_16_0 = res.cookies().size();
            // MethodAssertGenerator build local variable
            Object o_19_0 = res.cookie("one");
            // MethodAssertGenerator build local variable
            Object o_21_0 = res.cookie("two");
            // MethodAssertGenerator build local variable
            Object o_23_0 = res.cookie("");
            // MethodAssertGenerator build local variable
            Object o_25_0 = res.cookie("four");
            org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_literalMutation106005 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptyCookieNameAndVals */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptyCookieNameAndVals_cf106055_failAssert59_literalMutation110552_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // prep http response header map
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
                java.util.List<java.lang.String> cookieStrings = new java.util.ArrayList<java.lang.String>();
                cookieStrings.add(null);
                cookieStrings.add("");
                cookieStrings.add("one");
                cookieStrings.add("two=");
                cookieStrings.add("three=;");
                cookieStrings.add("four=data; Domain=.example.com; Path=/");
                headers.put("", cookieStrings);
                org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
                res.processResponseHeaders(headers);
                // MethodAssertGenerator build local variable
                Object o_16_0 = res.cookies().size();
                // MethodAssertGenerator build local variable
                Object o_19_0 = res.cookie("one");
                // MethodAssertGenerator build local variable
                Object o_21_0 = res.cookie("two");
                // MethodAssertGenerator build local variable
                Object o_23_0 = res.cookie("three");
                // StatementAdderOnAssert create null value
                java.lang.String[] vc_37874 = (java.lang.String[])null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_37872 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_37872.data(vc_37874);
                // MethodAssertGenerator build local variable
                Object o_31_0 = res.cookie("four");
                org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_cf106055 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_cf106055_failAssert59_literalMutation110552 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptyCookieNameAndVals */
    @org.junit.Test
    public void ignoresEmptyCookieNameAndVals_literalMutation105996_failAssert40_literalMutation109876() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            java.util.List<java.lang.String> cookieStrings = new java.util.ArrayList<java.lang.String>();
            cookieStrings.add(null);
            cookieStrings.add("");
            cookieStrings.add("one");
            cookieStrings.add("two=");
            // AssertGenerator replace invocation
            boolean o_ignoresEmptyCookieNameAndVals_literalMutation105996_failAssert40_literalMutation109876__12 = cookieStrings.add("thre=;");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_ignoresEmptyCookieNameAndVals_literalMutation105996_failAssert40_literalMutation109876__12);
            cookieStrings.add("four=data; Domain=.example.com; Path=/");
            headers.put("Set-Cookie", cookieStrings);
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // MethodAssertGenerator build local variable
            Object o_16_0 = res.cookies().size();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_16_0, 4);
            // MethodAssertGenerator build local variable
            Object o_19_0 = res.cookie("");
            // MethodAssertGenerator build local variable
            Object o_21_0 = res.cookie("two");
            // MethodAssertGenerator build local variable
            Object o_23_0 = res.cookie("three");
            // MethodAssertGenerator build local variable
            Object o_25_0 = res.cookie("four");
            org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_literalMutation105996 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptyCookieNameAndVals */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptyCookieNameAndVals_cf106105_failAssert8_literalMutation108615_failAssert26_literalMutation144770() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // prep http response header map
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
                java.util.List<java.lang.String> cookieStrings = new java.util.ArrayList<java.lang.String>();
                cookieStrings.add(null);
                cookieStrings.add("");
                cookieStrings.add("one");
                cookieStrings.add("two=");
                cookieStrings.add("three=;");
                cookieStrings.add("four=data; Domain=.example.com; Path=/");
                // AssertGenerator replace invocation
                java.util.List<java.lang.String> o_ignoresEmptyCookieNameAndVals_cf106105_failAssert8_literalMutation108615_failAssert26_literalMutation144770__16 = headers.put("", cookieStrings);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_ignoresEmptyCookieNameAndVals_cf106105_failAssert8_literalMutation108615_failAssert26_literalMutation144770__16);
                org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
                res.processResponseHeaders(headers);
                // MethodAssertGenerator build local variable
                Object o_16_0 = res.cookies().size();
                // MethodAssertGenerator build local variable
                Object o_19_0 = res.cookie("V##");
                // MethodAssertGenerator build local variable
                Object o_21_0 = res.cookie("two");
                // MethodAssertGenerator build local variable
                Object o_23_0 = res.cookie("three");
                // StatementAdderOnAssert create literal from method
                int int_vc_4553 = 4;
                // StatementAdderOnAssert create null value
                java.lang.String vc_37918 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_37916 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_37916.proxy(vc_37918, int_vc_4553);
                // MethodAssertGenerator build local variable
                Object o_33_0 = res.cookie("four");
                org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_cf106105 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_cf106105_failAssert8_literalMutation108615 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptyCookieNameAndVals */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptyCookieNameAndVals_add105964_cf106526_failAssert9_literalMutation144266_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // prep http response header map
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
                java.util.List<java.lang.String> cookieStrings = new java.util.ArrayList<java.lang.String>();
                cookieStrings.add(null);
                cookieStrings.add("");
                cookieStrings.add("one");
                cookieStrings.add("two=");
                // AssertGenerator replace invocation
                boolean o_ignoresEmptyCookieNameAndVals_add105964__10 = // MethodCallAdder
cookieStrings.add("three=;");
                // MethodAssertGenerator build local variable
                Object o_12_0 = o_ignoresEmptyCookieNameAndVals_add105964__10;
                cookieStrings.add("three=;");
                cookieStrings.add("four=data; Domain=.example.com; Path=/");
                headers.put("Set-Cookie", cookieStrings);
                org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
                res.processResponseHeaders(headers);
                // MethodAssertGenerator build local variable
                Object o_20_0 = res.cookies().size();
                // MethodAssertGenerator build local variable
                Object o_23_0 = res.cookie("");
                // MethodAssertGenerator build local variable
                Object o_25_0 = res.cookie("two");
                // MethodAssertGenerator build local variable
                Object o_27_0 = res.cookie("three");
                // StatementAdderOnAssert create random local variable
                int vc_38157 = -1468043400;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_38155 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_38155.maxBodySize(vc_38157);
                // MethodAssertGenerator build local variable
                Object o_35_0 = res.cookie("four");
                org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_add105964_cf106526 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("ignoresEmptyCookieNameAndVals_add105964_cf106526_failAssert9_literalMutation144266 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test
    public void ignoresEmptySetCookies_literalMutation145504() {
        // prep http response header map
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
        // AssertGenerator replace invocation
        java.util.List<java.lang.String> o_ignoresEmptySetCookies_literalMutation145504__4 = headers.put("", java.util.Collections.<java.lang.String>emptyList());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_ignoresEmptySetCookies_literalMutation145504__4);
        org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
        res.processResponseHeaders(headers);
        org.junit.Assert.assertEquals(0, res.cookies().size());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test
    public void ignoresEmptySetCookies_literalMutation145508() {
        // prep http response header map
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
        // AssertGenerator replace invocation
        java.util.List<java.lang.String> o_ignoresEmptySetCookies_literalMutation145508__4 = headers.put("Set-CIookie", java.util.Collections.<java.lang.String>emptyList());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_ignoresEmptySetCookies_literalMutation145508__4);
        org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
        res.processResponseHeaders(headers);
        org.junit.Assert.assertEquals(0, res.cookies().size());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptySetCookies_cf145515_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            headers.put("Set-Cookie", java.util.Collections.<java.lang.String>emptyList());
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // StatementAdderOnAssert create null value
            java.net.URL vc_52330 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_52328 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_52328.connect(vc_52330);
            // MethodAssertGenerator build local variable
            Object o_15_0 = res.cookies().size();
            org.junit.Assert.fail("ignoresEmptySetCookies_cf145515 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptySetCookies_cf145511_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            headers.put("Set-Cookie", java.util.Collections.<java.lang.String>emptyList());
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_52327 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_52324 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_52324.connect(vc_52327);
            // MethodAssertGenerator build local variable
            Object o_15_0 = res.cookies().size();
            org.junit.Assert.fail("ignoresEmptySetCookies_cf145511 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptySetCookies_literalMutation145507_cf146260_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            // AssertGenerator replace invocation
            java.util.List<java.lang.String> o_ignoresEmptySetCookies_literalMutation145507__4 = headers.put("Set-Cokie", java.util.Collections.<java.lang.String>emptyList());
            // MethodAssertGenerator build local variable
            Object o_6_0 = o_ignoresEmptySetCookies_literalMutation145507__4;
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // StatementAdderOnAssert create null value
            java.net.URL vc_52965 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_52963 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_52963.connect(vc_52965);
            // MethodAssertGenerator build local variable
            Object o_17_0 = res.cookies().size();
            org.junit.Assert.fail("ignoresEmptySetCookies_literalMutation145507_cf146260 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptySetCookies_cf145603_failAssert23_literalMutation146764() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            // AssertGenerator replace invocation
            java.util.List<java.lang.String> o_ignoresEmptySetCookies_cf145603_failAssert23_literalMutation146764__6 = headers.put("Set-C@okie", java.util.Collections.<java.lang.String>emptyList());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_ignoresEmptySetCookies_cf145603_failAssert23_literalMutation146764__6);
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // StatementAdderOnAssert create literal from method
            int int_vc_6254 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_6254, 0);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_52397 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_52397, "");
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_52394 = (org.jsoup.helper.HttpConnection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_52394);
            // StatementAdderMethod cloned existing statement
            vc_52394.proxy(vc_52397, int_vc_6254);
            // MethodAssertGenerator build local variable
            Object o_17_0 = res.cookies().size();
            org.junit.Assert.fail("ignoresEmptySetCookies_cf145603 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptySetCookies_literalMutation145505_cf145983_failAssert30() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            // AssertGenerator replace invocation
            java.util.List<java.lang.String> o_ignoresEmptySetCookies_literalMutation145505__4 = headers.put("2jbM%8)$7O", java.util.Collections.<java.lang.String>emptyList());
            // MethodAssertGenerator build local variable
            Object o_6_0 = o_ignoresEmptySetCookies_literalMutation145505__4;
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_52708 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_52705 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_52705.connect(vc_52708);
            // MethodAssertGenerator build local variable
            Object o_17_0 = res.cookies().size();
            org.junit.Assert.fail("ignoresEmptySetCookies_literalMutation145505_cf145983 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptySetCookies_cf145658_failAssert43_literalMutation146955() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // prep http response header map
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            // AssertGenerator replace invocation
            java.util.List<java.lang.String> o_ignoresEmptySetCookies_cf145658_failAssert43_literalMutation146955__6 = headers.put("", java.util.Collections.<java.lang.String>emptyList());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_ignoresEmptySetCookies_cf145658_failAssert43_literalMutation146955__6);
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_6260 = "Set-Cookie";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_6260, "Set-Cookie");
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_52437 = (org.jsoup.helper.HttpConnection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_52437);
            // StatementAdderMethod cloned existing statement
            vc_52437.data(String_vc_6260);
            // MethodAssertGenerator build local variable
            Object o_15_0 = res.cookies().size();
            org.junit.Assert.fail("ignoresEmptySetCookies_cf145658 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptySetCookies_cf145582_failAssert14_literalMutation146661_cf151213_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_14_1 = 2058993289;
                // prep http response header map
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
                headers.put("Set-Cookie", java.util.Collections.<java.lang.String>emptyList());
                org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
                res.processResponseHeaders(headers);
                // StatementAdderOnAssert create random local variable
                int vc_52381 = 2058993289;
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_52381;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_52379 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.net.URL vc_55124 = (java.net.URL)null;
                // StatementAdderMethod cloned existing statement
                vc_52379.connect(vc_55124);
                // MethodAssertGenerator build local variable
                Object o_22_0 = vc_52379;
                // StatementAdderMethod cloned existing statement
                vc_52379.maxBodySize(vc_52381);
                // MethodAssertGenerator build local variable
                Object o_15_0 = res.cookies().size();
                org.junit.Assert.fail("ignoresEmptySetCookies_cf145582 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("ignoresEmptySetCookies_cf145582_failAssert14_literalMutation146661_cf151213 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#ignoresEmptySetCookies */
    @org.junit.Test(timeout = 10000)
    public void ignoresEmptySetCookies_cf145603_failAssert23_literalMutation146764_cf162953_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_15_1 = 0;
                // prep http response header map
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
                // AssertGenerator replace invocation
                java.util.List<java.lang.String> o_ignoresEmptySetCookies_cf145603_failAssert23_literalMutation146764__6 = headers.put("Set-C@okie", java.util.Collections.<java.lang.String>emptyList());
                // MethodAssertGenerator build local variable
                Object o_8_0 = o_ignoresEmptySetCookies_cf145603_failAssert23_literalMutation146764__6;
                org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
                res.processResponseHeaders(headers);
                // StatementAdderOnAssert create literal from method
                int int_vc_6254 = 0;
                // MethodAssertGenerator build local variable
                Object o_15_0 = int_vc_6254;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_52397 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_52397;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_52394 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_60455 = new java.lang.String();
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_60452 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_60452.connect(vc_60455);
                // MethodAssertGenerator build local variable
                Object o_29_0 = vc_52394;
                // StatementAdderMethod cloned existing statement
                vc_52394.proxy(vc_52397, int_vc_6254);
                // MethodAssertGenerator build local variable
                Object o_17_0 = res.cookies().size();
                org.junit.Assert.fail("ignoresEmptySetCookies_cf145603 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("ignoresEmptySetCookies_cf145603_failAssert23_literalMutation146764_cf162953 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#inputStream */
    @org.junit.Test(timeout = 10000)
    public void inputStream_cf166015_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection.KeyVal kv = org.jsoup.helper.HttpConnection.KeyVal.create("file", "thumb.jpg", org.jsoup.integration.ParseTest.inputStreamFrom("Check"));
            // MethodAssertGenerator build local variable
            Object o_4_0 = kv.key();
            // MethodAssertGenerator build local variable
            Object o_6_0 = kv.value();
            // MethodAssertGenerator build local variable
            Object o_8_0 = kv.hasInputStream();
            kv = org.jsoup.helper.HttpConnection.KeyVal.create("one", "two");
            // MethodAssertGenerator build local variable
            Object o_12_0 = kv.key();
            // MethodAssertGenerator build local variable
            Object o_14_0 = kv.value();
            // StatementAdderOnAssert create null value
            java.net.URL vc_61728 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_61726 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_61726.connect(vc_61728);
            // MethodAssertGenerator build local variable
            Object o_22_0 = kv.hasInputStream();
            org.junit.Assert.fail("inputStream_cf166015 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#inputStream */
    @org.junit.Test
    public void inputStream_literalMutation165994() {
        org.jsoup.Connection.KeyVal kv = org.jsoup.helper.HttpConnection.KeyVal.create("file", "thumb.jpg", org.jsoup.integration.ParseTest.inputStreamFrom(""));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.helper.HttpConnection.KeyVal)kv).key(), "file");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsoup.helper.HttpConnection.KeyVal)kv).value(), "thumb.jpg");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.helper.HttpConnection.KeyVal)kv).hasInputStream());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.io.ByteArrayInputStream)((org.jsoup.helper.HttpConnection.KeyVal)kv).inputStream()).available(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.io.ByteArrayInputStream)((org.jsoup.helper.HttpConnection.KeyVal)kv).inputStream()).read(), -1);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.io.ByteArrayInputStream)((org.jsoup.helper.HttpConnection.KeyVal)kv).inputStream()).markSupported());
        org.junit.Assert.assertEquals("file", kv.key());
        org.junit.Assert.assertEquals("thumb.jpg", kv.value());
        org.junit.Assert.assertTrue(kv.hasInputStream());
        kv = org.jsoup.helper.HttpConnection.KeyVal.create("one", "two");
        org.junit.Assert.assertEquals("one", kv.key());
        org.junit.Assert.assertEquals("two", kv.value());
        org.junit.Assert.assertFalse(kv.hasInputStream());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#inputStream */
    @org.junit.Test
    public void inputStream_literalMutation165984_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection.KeyVal kv = org.jsoup.helper.HttpConnection.KeyVal.create("", "thumb.jpg", org.jsoup.integration.ParseTest.inputStreamFrom("Check"));
            // MethodAssertGenerator build local variable
            Object o_4_0 = kv.key();
            // MethodAssertGenerator build local variable
            Object o_6_0 = kv.value();
            // MethodAssertGenerator build local variable
            Object o_8_0 = kv.hasInputStream();
            kv = org.jsoup.helper.HttpConnection.KeyVal.create("one", "two");
            // MethodAssertGenerator build local variable
            Object o_12_0 = kv.key();
            // MethodAssertGenerator build local variable
            Object o_14_0 = kv.value();
            // MethodAssertGenerator build local variable
            Object o_16_0 = kv.hasInputStream();
            org.junit.Assert.fail("inputStream_literalMutation165984 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#inputStream */
    @org.junit.Test(timeout = 10000)
    public void inputStream_literalMutation165994_cf166185_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_12_1 = -1;
            // MethodAssertGenerator build local variable
            Object o_10_1 = 0;
            org.jsoup.Connection.KeyVal kv = org.jsoup.helper.HttpConnection.KeyVal.create("file", "thumb.jpg", org.jsoup.integration.ParseTest.inputStreamFrom(""));
            // MethodAssertGenerator build local variable
            Object o_4_0 = ((org.jsoup.helper.HttpConnection.KeyVal)kv).key();
            // MethodAssertGenerator build local variable
            Object o_6_0 = ((org.jsoup.helper.HttpConnection.KeyVal)kv).value();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((org.jsoup.helper.HttpConnection.KeyVal)kv).hasInputStream();
            // MethodAssertGenerator build local variable
            Object o_10_0 = ((java.io.ByteArrayInputStream)((org.jsoup.helper.HttpConnection.KeyVal)kv).inputStream()).available();
            // MethodAssertGenerator build local variable
            Object o_12_0 = ((java.io.ByteArrayInputStream)((org.jsoup.helper.HttpConnection.KeyVal)kv).inputStream()).read();
            // MethodAssertGenerator build local variable
            Object o_14_0 = ((java.io.ByteArrayInputStream)((org.jsoup.helper.HttpConnection.KeyVal)kv).inputStream()).markSupported();
            // MethodAssertGenerator build local variable
            Object o_16_0 = kv.key();
            // MethodAssertGenerator build local variable
            Object o_18_0 = kv.value();
            // MethodAssertGenerator build local variable
            Object o_20_0 = kv.hasInputStream();
            kv = org.jsoup.helper.HttpConnection.KeyVal.create("one", "two");
            // MethodAssertGenerator build local variable
            Object o_24_0 = kv.key();
            // MethodAssertGenerator build local variable
            Object o_26_0 = kv.value();
            // StatementAdderOnAssert create null value
            java.net.URL vc_61855 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_61853 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_61853.connect(vc_61855);
            // MethodAssertGenerator build local variable
            Object o_34_0 = kv.hasInputStream();
            org.junit.Assert.fail("inputStream_literalMutation165994_cf166185 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#inputStream */
    @org.junit.Test(timeout = 10000)
    public void inputStream_cf166073_failAssert32_literalMutation166691_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection.KeyVal kv = org.jsoup.helper.HttpConnection.KeyVal.create("", "thumb.jpg", org.jsoup.integration.ParseTest.inputStreamFrom("Check"));
                // MethodAssertGenerator build local variable
                Object o_4_0 = kv.key();
                // MethodAssertGenerator build local variable
                Object o_6_0 = kv.value();
                // MethodAssertGenerator build local variable
                Object o_8_0 = kv.hasInputStream();
                kv = org.jsoup.helper.HttpConnection.KeyVal.create("one", "two");
                // MethodAssertGenerator build local variable
                Object o_12_0 = kv.key();
                // MethodAssertGenerator build local variable
                Object o_14_0 = kv.value();
                // StatementAdderOnAssert create random local variable
                boolean vc_61776 = false;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_61774 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_61774.ignoreHttpErrors(vc_61776);
                // MethodAssertGenerator build local variable
                Object o_22_0 = kv.hasInputStream();
                org.junit.Assert.fail("inputStream_cf166073 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("inputStream_cf166073_failAssert32_literalMutation166691 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#inputStream */
    @org.junit.Test(timeout = 10000)
    public void inputStream_literalMutation165994_cf166281_failAssert11_literalMutation167733_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_12_1 = -1;
                // MethodAssertGenerator build local variable
                Object o_10_1 = 0;
                org.jsoup.Connection.KeyVal kv = org.jsoup.helper.HttpConnection.KeyVal.create("", "thumb.jpg", org.jsoup.integration.ParseTest.inputStreamFrom(""));
                // MethodAssertGenerator build local variable
                Object o_4_0 = ((org.jsoup.helper.HttpConnection.KeyVal)kv).key();
                // MethodAssertGenerator build local variable
                Object o_6_0 = ((org.jsoup.helper.HttpConnection.KeyVal)kv).value();
                // MethodAssertGenerator build local variable
                Object o_8_0 = ((org.jsoup.helper.HttpConnection.KeyVal)kv).hasInputStream();
                // MethodAssertGenerator build local variable
                Object o_10_0 = ((java.io.ByteArrayInputStream)((org.jsoup.helper.HttpConnection.KeyVal)kv).inputStream()).available();
                // MethodAssertGenerator build local variable
                Object o_12_0 = ((java.io.ByteArrayInputStream)((org.jsoup.helper.HttpConnection.KeyVal)kv).inputStream()).read();
                // MethodAssertGenerator build local variable
                Object o_14_0 = ((java.io.ByteArrayInputStream)((org.jsoup.helper.HttpConnection.KeyVal)kv).inputStream()).markSupported();
                // MethodAssertGenerator build local variable
                Object o_16_0 = kv.key();
                // MethodAssertGenerator build local variable
                Object o_18_0 = kv.value();
                // MethodAssertGenerator build local variable
                Object o_20_0 = kv.hasInputStream();
                kv = org.jsoup.helper.HttpConnection.KeyVal.create("one", "two");
                // MethodAssertGenerator build local variable
                Object o_24_0 = kv.key();
                // MethodAssertGenerator build local variable
                Object o_26_0 = kv.value();
                // StatementAdderOnAssert create null value
                java.lang.String vc_61938 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_61936 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_61936.requestBody(vc_61938);
                // MethodAssertGenerator build local variable
                Object o_34_0 = kv.hasInputStream();
                org.junit.Assert.fail("inputStream_literalMutation165994_cf166281 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("inputStream_literalMutation165994_cf166281_failAssert11_literalMutation167733 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168120_failAssert40() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.net.URL vc_62080 = (java.net.URL)null;
            // StatementAdderMethod cloned existing statement
            con.url(vc_62080);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf168120 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf167994_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.lang.String[] vc_62004 = (java.lang.String[])null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_62004);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf167994 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168057_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_62045 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_62045);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf168057 should have thrown IllegalCharsetNameException");
        } catch (java.nio.charset.IllegalCharsetNameException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168139_failAssert44() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.lang.String vc_62091 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_62091);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf168139 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168001_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.util.Collection<org.jsoup.Connection.KeyVal> vc_62008 = (java.util.Collection)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_62008);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf168001 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168065_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create random local variable
            int vc_62050 = 92195510;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_7166 = "http://example.com/";
            // StatementAdderMethod cloned existing statement
            con.proxy(String_vc_7166, vc_62050);
            // MethodAssertGenerator build local variable
            Object o_13_0 = con.request().method();
            org.junit.Assert.fail("method_cf168065 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf167961_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_61990 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_61990);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf167961 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168009_failAssert55() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_62012 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_62012);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf168009 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168027_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_62023 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.headers(vc_62023);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf168027 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168038_failAssert26() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create random local variable
            int vc_62033 = -1815101286;
            // StatementAdderMethod cloned existing statement
            con.maxBodySize(vc_62033);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf168038 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168112_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.lang.String vc_62076 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.url(vc_62076);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf168112 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf167994_failAssert8_add168226() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.lang.String[] vc_62004 = (java.lang.String[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_62004);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            con.data(vc_62004);
            // StatementAdderMethod cloned existing statement
            con.data(vc_62004);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf167994 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168110_failAssert33_literalMutation168439() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).toGenericString(), "public abstract class java.lang.Enum<E>");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getSimpleName(), "Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getCanonicalName(), "org.jsoup.Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsoup.Connection.Method)o_3_0).name(), "GET");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getCanonicalName(), "org.jsoup.Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsoup.Connection.Method)o_3_0).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getModifiers(), 1025);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getName(), "org.jsoup.Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getPackage()).getSpecificationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).toGenericString(), "public abstract interface org.jsoup.Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getPackage()).getImplementationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getModifiers(), 16409);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getSimpleName(), "Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getTypeName(), "org.jsoup.Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getPackage()).getImplementationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getPackage()).getSpecificationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getGenericSuperclass()).getTypeName(), "java.lang.Enum<org.jsoup.Connection$Method>");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).toGenericString(), "public abstract interface org.jsoup.Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getName(), "org.jsoup.Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getGenericSuperclass()).getOwnerType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((org.jsoup.Connection.Method)o_3_0).hasBody());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getCanonicalName(), "org.jsoup.Connection.Method");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).toGenericString(), "public static final enum org.jsoup.Connection$Method");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getTypeName(), "org.jsoup.Connection$Method");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getName(), "org.jsoup.Connection$Method");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getPackage()).getImplementationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getPackage()).getSpecificationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getPackage()).getName(), "org.jsoup");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getSimpleName(), "Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getCanonicalName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).getTypeName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getTypeName(), "org.jsoup.Connection");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSimpleName(), "Method");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getEnclosingClass()).getModifiers(), 1537);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.jsoup.Connection.Method)o_3_0).getDeclaringClass()).getDeclaringClass()).getModifiers(), 1537);
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_7169 = ":i_ZK;P`Y7p*!!ae!4R";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_7169, ":i_ZK;P`Y7p*!!ae!4R");
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_62074 = (org.jsoup.helper.HttpConnection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_62074);
            // StatementAdderMethod cloned existing statement
            vc_62074.url(String_vc_7169);
            // MethodAssertGenerator build local variable
            Object o_13_0 = con.request().method();
            org.junit.Assert.fail("method_cf168110 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168064_failAssert4_literalMutation168192() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://exaBple.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create random local variable
            int vc_62050 = 92195510;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_62050, 92195510);
            // StatementAdderOnAssert create null value
            java.lang.String vc_62048 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_62048);
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_62048, vc_62050);
            // MethodAssertGenerator build local variable
            Object o_13_0 = con.request().method();
            org.junit.Assert.fail("method_cf168064 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf167994_failAssert8_add168225() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            // AssertGenerator replace invocation
            org.jsoup.Connection o_method_cf167994_failAssert8_add168225__9 = // MethodCallAdder
con.method(org.jsoup.Connection.Method.POST);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_method_cf167994_failAssert8_add168225__9.equals(con));
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create null value
            java.lang.String[] vc_62004 = (java.lang.String[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_62004);
            // StatementAdderMethod cloned existing statement
            con.data(vc_62004);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf167994 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168141_failAssert23_add168341() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().method();
            con.method(org.jsoup.Connection.Method.POST);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_62092 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_62092, "");
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            con.data(vc_62092);
            // StatementAdderMethod cloned existing statement
            con.data(vc_62092);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().method();
            org.junit.Assert.fail("method_cf168141 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168046_failAssert51_literalMutation168579_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().method();
                con.method(org.jsoup.Connection.Method.POST);
                // StatementAdderOnAssert create null value
                org.jsoup.parser.Parser vc_62040 = (org.jsoup.parser.Parser)null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_62038 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_62038.parser(vc_62040);
                // MethodAssertGenerator build local variable
                Object o_13_0 = con.request().method();
                org.junit.Assert.fail("method_cf168046 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("method_cf168046_failAssert51_literalMutation168579 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168125_failAssert18_literalMutation168307_cf196374_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().method();
                con.method(org.jsoup.Connection.Method.POST);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_7170 = "U1kSzoB3BARP:Z[N$A<";
                // MethodAssertGenerator build local variable
                Object o_12_0 = String_vc_7170;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_62082 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.util.Collection<org.jsoup.Connection.KeyVal> vc_74454 = (java.util.Collection)null;
                // StatementAdderMethod cloned existing statement
                con.data(vc_74454);
                // MethodAssertGenerator build local variable
                Object o_20_0 = vc_62082;
                // StatementAdderMethod cloned existing statement
                vc_62082.userAgent(String_vc_7170);
                // MethodAssertGenerator build local variable
                Object o_13_0 = con.request().method();
                org.junit.Assert.fail("method_cf168125 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("method_cf168125_failAssert18_literalMutation168307_cf196374 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168063_failAssert52_add168583_cf179964_failAssert26() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_16_1 = 92195510;
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().method();
                // AssertGenerator replace invocation
                org.jsoup.Connection o_method_cf168063_failAssert52_add168583__9 = // MethodCallAdder
con.method(org.jsoup.Connection.Method.POST);
                // MethodAssertGenerator build local variable
                Object o_11_0 = o_method_cf168063_failAssert52_add168583__9.equals(con);
                con.method(org.jsoup.Connection.Method.POST);
                // StatementAdderOnAssert create random local variable
                int vc_62050 = 92195510;
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_62050;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_62049 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_20_0 = vc_62049;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_62046 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_67137 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                con.referrer(vc_67137);
                // MethodAssertGenerator build local variable
                Object o_28_0 = vc_62046;
                // StatementAdderMethod cloned existing statement
                vc_62046.proxy(vc_62049, vc_62050);
                // MethodAssertGenerator build local variable
                Object o_15_0 = con.request().method();
                org.junit.Assert.fail("method_cf168063 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("method_cf168063_failAssert52_add168583_cf179964 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168126_failAssert54_literalMutation168604_cf181383_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.co{m/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().method();
                con.method(org.jsoup.Connection.Method.POST);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_62085 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_12_0 = vc_62085;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_62082 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create random local variable
                int vc_67765 = -1890908531;
                // StatementAdderMethod cloned existing statement
                con.proxy(vc_62085, vc_67765);
                // MethodAssertGenerator build local variable
                Object o_20_0 = vc_62082;
                // StatementAdderMethod cloned existing statement
                vc_62082.userAgent(vc_62085);
                // MethodAssertGenerator build local variable
                Object o_13_0 = con.request().method();
                org.junit.Assert.fail("method_cf168126 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("method_cf168126_failAssert54_literalMutation168604_cf181383 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168124_failAssert50_add168570_cf175550_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().method();
                con.method(org.jsoup.Connection.Method.POST);
                // StatementAdderOnAssert create null value
                java.lang.String vc_62084 = (java.lang.String)null;
                // MethodAssertGenerator build local variable
                Object o_12_0 = vc_62084;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_62082 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create random local variable
                java.lang.String[] vc_65180 = new java.lang.String []{new java.lang.String(),new java.lang.String()};
                // StatementAdderMethod cloned existing statement
                con.data(vc_65180);
                // MethodAssertGenerator build local variable
                Object o_20_0 = vc_62082;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_62082.userAgent(vc_62084);
                // StatementAdderMethod cloned existing statement
                vc_62082.userAgent(vc_62084);
                // MethodAssertGenerator build local variable
                Object o_13_0 = con.request().method();
                org.junit.Assert.fail("method_cf168124 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("method_cf168124_failAssert50_add168570_cf175550 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168118_failAssert22_literalMutation168337_cf200132_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http:/example.com/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().method();
                con.method(org.jsoup.Connection.Method.POST);
                // StatementAdderOnAssert create null value
                java.net.URL vc_62080 = (java.net.URL)null;
                // MethodAssertGenerator build local variable
                Object o_12_0 = vc_62080;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_62078 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.net.URL vc_76177 = (java.net.URL)null;
                // StatementAdderMethod cloned existing statement
                con.url(vc_76177);
                // MethodAssertGenerator build local variable
                Object o_20_0 = vc_62078;
                // StatementAdderMethod cloned existing statement
                vc_62078.url(vc_62080);
                // MethodAssertGenerator build local variable
                Object o_13_0 = con.request().method();
                org.junit.Assert.fail("method_cf168118 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("method_cf168118_failAssert22_literalMutation168337_cf200132 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#method */
    @org.junit.Test(timeout = 10000)
    public void method_cf168145_failAssert16_literalMutation168287_cf177120_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://eample.com/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().method();
                con.method(org.jsoup.Connection.Method.POST);
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_62093 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_65867 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                con.referrer(vc_65867);
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_62093;
                // StatementAdderMethod cloned existing statement
                vc_62093.request();
                // MethodAssertGenerator build local variable
                Object o_11_0 = con.request().method();
                org.junit.Assert.fail("method_cf168145 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("method_cf168145_failAssert16_literalMutation168287_cf177120 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204480_failAssert59() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_78014 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_78014);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204480 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_add204390() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        // AssertGenerator replace invocation
        org.jsoup.Connection o_referrer_add204390__3 = // MethodCallAdder
con.referrer("http://foo.com");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_referrer_add204390__3.equals(con));
        con.referrer("http://foo.com");
        org.junit.Assert.assertEquals("http://foo.com", con.request().header("Referer"));
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204611() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        con.referrer("http://foo.com");
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_9086 = "http://foo.com";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_9086, "http://foo.com");
        // AssertGenerator replace invocation
        org.jsoup.Connection.KeyVal o_referrer_cf204611__6 = // StatementAdderMethod cloned existing statement
con.data(String_vc_9086);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_referrer_cf204611__6);
        org.junit.Assert.assertEquals("http://foo.com", con.request().header("Referer"));
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204526_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create null value
            java.lang.String vc_78046 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_78046);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204526 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204432_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_77992 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_77992);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204432 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204550_failAssert42() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create null value
            java.lang.String vc_78059 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.referrer(vc_78059);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204550 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204465_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create null value
            java.lang.String[] vc_78006 = (java.lang.String[])null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_78006);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204465 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204535_failAssert58() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create random local variable
            int vc_78052 = -1656813647;
            // StatementAdderOnAssert create null value
            java.lang.String vc_78050 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_78050, vc_78052);
            // MethodAssertGenerator build local variable
            Object o_10_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204535 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204610_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create null value
            java.lang.String vc_78093 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_78093);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204610 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204591_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create null value
            java.net.URL vc_78082 = (java.net.URL)null;
            // StatementAdderMethod cloned existing statement
            con.url(vc_78082);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204591 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test
    public void referrer_literalMutation204391_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
            con.referrer("http://foo.com");
            // MethodAssertGenerator build local variable
            Object o_4_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_literalMutation204391 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204578_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create random local variable
            int vc_78075 = -1098667023;
            // StatementAdderMethod cloned existing statement
            con.timeout(vc_78075);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204578 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204472_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create null value
            java.util.Collection<org.jsoup.Connection.KeyVal> vc_78010 = (java.util.Collection)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_78010);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204472 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204407_cf204941_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_9075 = "http://foo.com";
            // MethodAssertGenerator build local variable
            Object o_6_0 = String_vc_9075;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_77978 = (org.jsoup.helper.HttpConnection)null;
            // MethodAssertGenerator build local variable
            Object o_10_0 = vc_77978;
            // StatementAdderMethod cloned existing statement
            vc_77978.connect(String_vc_9075);
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_78246 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_78246);
            // MethodAssertGenerator build local variable
            Object o_18_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204407_cf204941 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204415_failAssert41_literalMutation206135() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_referrer_cf204415_failAssert41_literalMutation206135__5 = con.referrer("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_referrer_cf204415_failAssert41_literalMutation206135__5.equals(con));
            // StatementAdderOnAssert create null value
            java.net.URL vc_77984 = (java.net.URL)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_77984);
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_77982 = (org.jsoup.helper.HttpConnection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_77982);
            // StatementAdderMethod cloned existing statement
            vc_77982.connect(vc_77984);
            // MethodAssertGenerator build local variable
            Object o_10_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204415 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204536_failAssert48_literalMutation206264() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_referrer_cf204536_failAssert48_literalMutation206264__5 = con.referrer("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_referrer_cf204536_failAssert48_literalMutation206264__5.equals(con));
            // StatementAdderOnAssert create random local variable
            int vc_78052 = -1656813647;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_78052, -1656813647);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_9081 = "Referer";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_9081, "Referer");
            // StatementAdderMethod cloned existing statement
            con.proxy(String_vc_9081, vc_78052);
            // MethodAssertGenerator build local variable
            Object o_10_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204536 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204595_failAssert25_literalMutation205915_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
                con.referrer("http://foo.com");
                // StatementAdderOnAssert create null value
                java.lang.String vc_78086 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_78084 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_78084.userAgent(vc_78086);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().header("Referer");
                org.junit.Assert.fail("referrer_cf204595 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("referrer_cf204595_failAssert25_literalMutation205915 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204480_failAssert59_literalMutation206410() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http:/!example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_78014 = (java.util.Map)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_78014);
            // StatementAdderMethod cloned existing statement
            con.data(vc_78014);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Referer");
            org.junit.Assert.fail("referrer_cf204480 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204612_failAssert61_literalMutation206453() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.referrer("http://foo.com");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_78094 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_78094, "");
            // StatementAdderMethod cloned existing statement
            con.data(vc_78094);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().header("Reerer");
            org.junit.Assert.fail("referrer_cf204612 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204528_failAssert9_add205632_cf213770_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                con.referrer("http://foo.com");
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_78047 = new java.lang.String();
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.String, java.lang.String> vc_81697 = (java.util.Map)null;
                // StatementAdderMethod cloned existing statement
                con.data(vc_81697);
                // MethodAssertGenerator build local variable
                Object o_12_0 = vc_78047;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                con.postDataCharset(vc_78047);
                // StatementAdderMethod cloned existing statement
                con.postDataCharset(vc_78047);
                // MethodAssertGenerator build local variable
                Object o_8_0 = con.request().header("Referer");
                org.junit.Assert.fail("referrer_cf204528 should have thrown IllegalCharsetNameException");
            } catch (java.nio.charset.IllegalCharsetNameException eee) {
            }
            org.junit.Assert.fail("referrer_cf204528_failAssert9_add205632_cf213770 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204528_failAssert9_literalMutation205638_cf223851_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // AssertGenerator replace invocation
                org.jsoup.Connection o_referrer_cf204528_failAssert9_literalMutation205638__5 = con.referrer("");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_referrer_cf204528_failAssert9_literalMutation205638__5.equals(con);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_78047 = new java.lang.String();
                // StatementAdderOnAssert create random local variable
                int vc_86163 = -1544718090;
                // StatementAdderMethod cloned existing statement
                con.maxBodySize(vc_86163);
                // MethodAssertGenerator build local variable
                Object o_15_0 = vc_78047;
                // StatementAdderMethod cloned existing statement
                con.postDataCharset(vc_78047);
                // MethodAssertGenerator build local variable
                Object o_8_0 = con.request().header("Referer");
                org.junit.Assert.fail("referrer_cf204528 should have thrown IllegalCharsetNameException");
            } catch (java.nio.charset.IllegalCharsetNameException eee) {
            }
            org.junit.Assert.fail("referrer_cf204528_failAssert9_literalMutation205638_cf223851 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204616_failAssert27_literalMutation205953_cf217575_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://examgle.com/");
                con.referrer("http://foo.com");
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_78095 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.net.URL vc_83416 = (java.net.URL)null;
                // StatementAdderMethod cloned existing statement
                con.url(vc_83416);
                // MethodAssertGenerator build local variable
                Object o_12_0 = vc_78095;
                // StatementAdderMethod cloned existing statement
                vc_78095.request();
                // MethodAssertGenerator build local variable
                Object o_8_0 = con.request().header("Referer");
                org.junit.Assert.fail("referrer_cf204616 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("referrer_cf204616_failAssert27_literalMutation205953_cf217575 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#referrer */
    @org.junit.Test(timeout = 10000)
    public void referrer_cf204589_failAssert37_literalMutation206082_cf220470_failAssert23() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://eample.com/");
                con.referrer("http://foo.com");
                // StatementAdderOnAssert create null value
                java.net.URL vc_78082 = (java.net.URL)null;
                // MethodAssertGenerator build local variable
                Object o_8_0 = vc_78082;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_78080 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_84697 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                con.data(vc_84697);
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_78080;
                // StatementAdderMethod cloned existing statement
                vc_78080.url(vc_78082);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().header("Referer");
                org.junit.Assert.fail("referrer_cf204589 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("referrer_cf204589_failAssert37_literalMutation206082_cf220470 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231002_failAssert39() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_89041 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_89041);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231002 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231050_failAssert51() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_89063 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_89063);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231050 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test
    public void requestBody_literalMutation230966_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
            con.requestBody("foo");
            // MethodAssertGenerator build local variable
            Object o_4_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_literalMutation230966 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231079_failAssert34() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create random local variable
            int vc_89084 = -1374440923;
            // StatementAdderMethod cloned existing statement
            con.maxBodySize(vc_89084);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231079 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231161_failAssert35() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create null value
            java.net.URL vc_89131 = (java.net.URL)null;
            // StatementAdderMethod cloned existing statement
            con.url(vc_89131);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231161 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231180_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create null value
            java.lang.String vc_89142 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_89142);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231180 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231068_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_89074 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.headers(vc_89074);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231068 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231181() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        con.requestBody("foo");
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_10412 = "foo";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_10412, "foo");
        // AssertGenerator replace invocation
        org.jsoup.Connection.KeyVal o_requestBody_cf231181__6 = // StatementAdderMethod cloned existing statement
con.data(String_vc_10412);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_requestBody_cf231181__6);
        org.junit.Assert.assertEquals("foo", con.request().requestBody());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231096_failAssert57() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create null value
            java.lang.String vc_89095 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_89095);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231096 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231106_failAssert46() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create random local variable
            int vc_89101 = -1494537011;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_10407 = "http://example.com/";
            // StatementAdderMethod cloned existing statement
            con.proxy(String_vc_10407, vc_89101);
            // MethodAssertGenerator build local variable
            Object o_10_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231106 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231042_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create null value
            java.util.Collection<org.jsoup.Connection.KeyVal> vc_89059 = (java.util.Collection)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_89059);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231042 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231106_failAssert46_literalMutation232041() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_requestBody_cf231106_failAssert46_literalMutation232041__5 = con.requestBody("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_requestBody_cf231106_failAssert46_literalMutation232041__5.equals(con));
            // StatementAdderOnAssert create random local variable
            int vc_89101 = -1494537011;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_89101, -1494537011);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_10407 = "http://example.com/";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_10407, "http://example.com/");
            // StatementAdderMethod cloned existing statement
            con.proxy(String_vc_10407, vc_89101);
            // MethodAssertGenerator build local variable
            Object o_10_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231106 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231119_failAssert37_literalMutation231924_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
                con.requestBody("foo");
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_89109 = new java.lang.String();
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_89106 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_89106.referrer(vc_89109);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().requestBody();
                org.junit.Assert.fail("requestBody_cf231119 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("requestBody_cf231119_failAssert37_literalMutation231924 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231096_failAssert57_literalMutation232184() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://exCample.com/");
            con.requestBody("foo");
            // StatementAdderOnAssert create null value
            java.lang.String vc_89095 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_89095);
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_89095);
            // MethodAssertGenerator build local variable
            Object o_8_0 = con.request().requestBody();
            org.junit.Assert.fail("requestBody_cf231096 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231098_failAssert54_literalMutation232149_literalMutation248137_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
                // AssertGenerator replace invocation
                org.jsoup.Connection o_requestBody_cf231098_failAssert54_literalMutation232149__5 = con.requestBody("fo");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_requestBody_cf231098_failAssert54_literalMutation232149__5.equals(con);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_89096 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_11_0 = vc_89096;
                // StatementAdderMethod cloned existing statement
                con.postDataCharset(vc_89096);
                // MethodAssertGenerator build local variable
                Object o_8_0 = con.request().requestBody();
                org.junit.Assert.fail("requestBody_cf231098 should have thrown IllegalCharsetNameException");
            } catch (java.nio.charset.IllegalCharsetNameException eee) {
            }
            org.junit.Assert.fail("requestBody_cf231098_failAssert54_literalMutation232149_literalMutation248137 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231098_failAssert54_literalMutation232146_cf232453_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://eample.com/");
                con.requestBody("foo");
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_89096 = new java.lang.String();
                // StatementAdderOnAssert create null value
                java.net.URL vc_89385 = (java.net.URL)null;
                // StatementAdderMethod cloned existing statement
                con.url(vc_89385);
                // MethodAssertGenerator build local variable
                Object o_12_0 = vc_89096;
                // StatementAdderMethod cloned existing statement
                con.postDataCharset(vc_89096);
                // MethodAssertGenerator build local variable
                Object o_8_0 = con.request().requestBody();
                org.junit.Assert.fail("requestBody_cf231098 should have thrown IllegalCharsetNameException");
            } catch (java.nio.charset.IllegalCharsetNameException eee) {
            }
            org.junit.Assert.fail("requestBody_cf231098_failAssert54_literalMutation232146_cf232453 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231151_failAssert20_literalMutation231740_cf253072_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // AssertGenerator replace invocation
                org.jsoup.Connection o_requestBody_cf231151_failAssert20_literalMutation231740__5 = con.requestBody("eoo");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_requestBody_cf231151_failAssert20_literalMutation231740__5.equals(con);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_10410 = "foo";
                // MethodAssertGenerator build local variable
                Object o_11_0 = String_vc_10410;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_89125 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.util.Map<java.lang.String, java.lang.String> vc_98566 = (java.util.Map)null;
                // StatementAdderMethod cloned existing statement
                con.cookies(vc_98566);
                // MethodAssertGenerator build local variable
                Object o_19_0 = vc_89125;
                // StatementAdderMethod cloned existing statement
                vc_89125.url(String_vc_10410);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().requestBody();
                org.junit.Assert.fail("requestBody_cf231151 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("requestBody_cf231151_failAssert20_literalMutation231740_cf253072 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231098_failAssert54_literalMutation232149_cf248154_failAssert26() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // AssertGenerator replace invocation
                org.jsoup.Connection o_requestBody_cf231098_failAssert54_literalMutation232149__5 = con.requestBody("fo");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_requestBody_cf231098_failAssert54_literalMutation232149__5.equals(con);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_89096 = new java.lang.String();
                // StatementAdderOnAssert create null value
                java.net.URL vc_96399 = (java.net.URL)null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_96397 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_96397.connect(vc_96399);
                // MethodAssertGenerator build local variable
                Object o_17_0 = vc_89096;
                // StatementAdderMethod cloned existing statement
                con.postDataCharset(vc_89096);
                // MethodAssertGenerator build local variable
                Object o_8_0 = con.request().requestBody();
                org.junit.Assert.fail("requestBody_cf231098 should have thrown IllegalCharsetNameException");
            } catch (java.nio.charset.IllegalCharsetNameException eee) {
            }
            org.junit.Assert.fail("requestBody_cf231098_failAssert54_literalMutation232149_cf248154 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231152_failAssert53_add232129_cf249649_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                con.requestBody("foo");
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_89128 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_8_0 = vc_89128;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_89125 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.util.Collection<org.jsoup.Connection.KeyVal> vc_97060 = (java.util.Collection)null;
                // StatementAdderMethod cloned existing statement
                con.data(vc_97060);
                // MethodAssertGenerator build local variable
                Object o_16_0 = vc_89125;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_89125.url(vc_89128);
                // StatementAdderMethod cloned existing statement
                vc_89125.url(vc_89128);
                // MethodAssertGenerator build local variable
                Object o_10_0 = con.request().requestBody();
                org.junit.Assert.fail("requestBody_cf231152 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("requestBody_cf231152_failAssert53_add232129_cf249649 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#requestBody */
    @org.junit.Test(timeout = 10000)
    public void requestBody_cf231186_failAssert58_literalMutation232203_cf245526_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // AssertGenerator replace invocation
                org.jsoup.Connection o_requestBody_cf231186_failAssert58_literalMutation232203__5 = con.requestBody("fo");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_requestBody_cf231186_failAssert58_literalMutation232203__5.equals(con);
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_89144 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create random local variable
                int vc_95197 = -1058605759;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_95196 = new java.lang.String();
                // StatementAdderMethod cloned existing statement
                con.proxy(vc_95196, vc_95197);
                // MethodAssertGenerator build local variable
                Object o_17_0 = vc_89144;
                // StatementAdderMethod cloned existing statement
                vc_89144.request();
                // MethodAssertGenerator build local variable
                Object o_8_0 = con.request().requestBody();
                org.junit.Assert.fail("requestBody_cf231186 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("requestBody_cf231186_failAssert58_literalMutation232203_cf245526 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#sameHeadersCombineWithComma */
    @org.junit.Test(timeout = 10000)
    public void sameHeadersCombineWithComma_cf264193_failAssert25() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            java.util.List<java.lang.String> values = new java.util.ArrayList<java.lang.String>();
            values.add("no-cache");
            values.add("no-store");
            headers.put("Cache-Control", values);
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // StatementAdderOnAssert create null value
            java.net.URL vc_103384 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_103382 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_103382.connect(vc_103384);
            // MethodAssertGenerator build local variable
            Object o_17_0 = res.header("Cache-Control");
            org.junit.Assert.fail("sameHeadersCombineWithComma_cf264193 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#sameHeadersCombineWithComma */
    @org.junit.Test
    public void sameHeadersCombineWithComma_literalMutation264177_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
            java.util.List<java.lang.String> values = new java.util.ArrayList<java.lang.String>();
            values.add("no-cache");
            values.add("no-store");
            headers.put("", values);
            org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
            res.processResponseHeaders(headers);
            // MethodAssertGenerator build local variable
            Object o_11_0 = res.header("Cache-Control");
            org.junit.Assert.fail("sameHeadersCombineWithComma_literalMutation264177 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#sameHeadersCombineWithComma */
    @org.junit.Test(timeout = 10000)
    public void sameHeadersCombineWithComma_cf264287_failAssert46_literalMutation265204_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
                java.util.List<java.lang.String> values = new java.util.ArrayList<java.lang.String>();
                values.add("no-cache");
                values.add("no-store");
                headers.put("", values);
                org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
                res.processResponseHeaders(headers);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_103460 = new java.lang.String();
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_103457 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_103457.referrer(vc_103460);
                // MethodAssertGenerator build local variable
                Object o_17_0 = res.header("Cache-Control");
                org.junit.Assert.fail("sameHeadersCombineWithComma_cf264287 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("sameHeadersCombineWithComma_cf264287_failAssert46_literalMutation265204 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#sameHeadersCombineWithComma */
    @org.junit.Test(timeout = 10000)
    public void sameHeadersCombineWithComma_cf264323_failAssert58_literalMutation265525_cf266860_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = new java.util.HashMap<java.lang.String, java.util.List<java.lang.String>>();
                java.util.List<java.lang.String> values = new java.util.ArrayList<java.lang.String>();
                values.add("no-cache");
                values.add("no-store");
                // AssertGenerator replace invocation
                java.util.List<java.lang.String> o_sameHeadersCombineWithComma_cf264323_failAssert58_literalMutation265525__9 = headers.put("CachLe-Control", values);
                // MethodAssertGenerator build local variable
                Object o_11_0 = o_sameHeadersCombineWithComma_cf264323_failAssert58_literalMutation265525__9;
                org.jsoup.helper.HttpConnection.Response res = new org.jsoup.helper.HttpConnection.Response();
                res.processResponseHeaders(headers);
                // StatementAdderOnAssert create random local variable
                boolean vc_103490 = true;
                // MethodAssertGenerator build local variable
                Object o_18_0 = vc_103490;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_103488 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.net.URL vc_104146 = (java.net.URL)null;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_104144 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_104144.connect(vc_104146);
                // MethodAssertGenerator build local variable
                Object o_28_0 = vc_103488;
                // StatementAdderMethod cloned existing statement
                vc_103488.validateTLSCertificates(vc_103490);
                // MethodAssertGenerator build local variable
                Object o_17_0 = res.header("Cache-Control");
                org.junit.Assert.fail("sameHeadersCombineWithComma_cf264323 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("sameHeadersCombineWithComma_cf264323_failAssert58_literalMutation265525_cf266860 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#timeout */
    @org.junit.Test(timeout = 10000)
    public void timeout_cf316990() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        org.junit.Assert.assertEquals((30 * 1000), con.request().timeout());
        con.timeout(1000);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_14679 = "http://example.com/";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_14679, "http://example.com/");
        // AssertGenerator replace invocation
        org.jsoup.Connection.KeyVal o_timeout_cf316990__9 = // StatementAdderMethod cloned existing statement
con.data(String_vc_14679);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_timeout_cf316990__9);
        org.junit.Assert.assertEquals(1000, con.request().timeout());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#timeout */
    @org.junit.Test(timeout = 10000)
    public void timeout_add316760() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        org.junit.Assert.assertEquals((30 * 1000), con.request().timeout());
        // AssertGenerator replace invocation
        org.jsoup.Connection o_timeout_add316760__6 = // MethodCallAdder
con.timeout(1000);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_timeout_add316760__6.equals(con));
        con.timeout(1000);
        org.junit.Assert.assertEquals(1000, con.request().timeout());
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343764_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create null value
            java.net.URL vc_136277 = (java.net.URL)null;
            // StatementAdderOnAssert create null value
            org.jsoup.helper.HttpConnection vc_136275 = (org.jsoup.helper.HttpConnection)null;
            // StatementAdderMethod cloned existing statement
            vc_136275.connect(vc_136277);
            // MethodAssertGenerator build local variable
            Object o_13_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343764 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343875_failAssert30() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create null value
            java.lang.String vc_136339 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(vc_136339);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343875 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343960() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        org.junit.Assert.assertEquals(org.jsoup.helper.HttpConnection.DEFAULT_UA, con.request().header("User-Agent"));
        con.userAgent("Mozilla");
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_16084 = "Mozilla";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_16084, "Mozilla");
        // AssertGenerator replace invocation
        org.jsoup.Connection.KeyVal o_userAgent_cf343960__9 = // StatementAdderMethod cloned existing statement
con.data(String_vc_16084);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_userAgent_cf343960__9);
        org.junit.Assert.assertEquals("Mozilla", con.request().header("User-Agent"));
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test
    public void userAgent_literalMutation343735_failAssert39() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // MethodAssertGenerator build local variable
            Object o_7_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_literalMutation343735 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343899_failAssert60() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create null value
            java.lang.String vc_136352 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.referrer(vc_136352);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343899 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343959_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create null value
            java.lang.String vc_136386 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_136386);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343959 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343781_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_136285 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.cookies(vc_136285);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343781 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343886_failAssert57() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create random local variable
            int vc_136345 = 1052260855;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_136344 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            con.proxy(vc_136344, vc_136345);
            // MethodAssertGenerator build local variable
            Object o_13_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343886 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_add343734() {
        org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
        org.junit.Assert.assertEquals(org.jsoup.helper.HttpConnection.DEFAULT_UA, con.request().header("User-Agent"));
        // AssertGenerator replace invocation
        org.jsoup.Connection o_userAgent_add343734__6 = // MethodCallAdder
con.userAgent("Mozilla");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_userAgent_add343734__6.equals(con));
        con.userAgent("Mozilla");
        org.junit.Assert.assertEquals("Mozilla", con.request().header("User-Agent"));
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343876_failAssert45() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_16078 = "Mozilla";
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(String_vc_16078);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343876 should have thrown IllegalCharsetNameException");
        } catch (java.nio.charset.IllegalCharsetNameException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343829_failAssert52() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create null value
            java.util.Map<java.lang.String, java.lang.String> vc_136307 = (java.util.Map)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_136307);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343829 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343858_failAssert49() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create random local variable
            int vc_136328 = -879281292;
            // StatementAdderMethod cloned existing statement
            con.maxBodySize(vc_136328);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343858 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343876_failAssert45_literalMutation345393() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("Uiser-Agent");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_3_0);
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_16078 = "Mozilla";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_16078, "Mozilla");
            // StatementAdderMethod cloned existing statement
            con.postDataCharset(String_vc_16078);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343876 should have thrown IllegalCharsetNameException");
        } catch (java.nio.charset.IllegalCharsetNameException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343960_cf344080_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_16084 = "Mozilla";
            // MethodAssertGenerator build local variable
            Object o_9_0 = String_vc_16084;
            // AssertGenerator replace invocation
            org.jsoup.Connection.KeyVal o_userAgent_cf343960__9 = // StatementAdderMethod cloned existing statement
con.data(String_vc_16084);
            // MethodAssertGenerator build local variable
            Object o_13_0 = o_userAgent_cf343960__9;
            // StatementAdderOnAssert create null value
            java.lang.String[] vc_136426 = (java.lang.String[])null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_136426);
            // MethodAssertGenerator build local variable
            Object o_19_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343960_cf344080 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_add343734_cf344375_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_userAgent_add343734__6 = // MethodCallAdder
con.userAgent("Mozilla");
            // MethodAssertGenerator build local variable
            Object o_8_0 = o_userAgent_add343734__6.equals(con);
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create null value
            java.util.Collection<org.jsoup.Connection.KeyVal> vc_136557 = (java.util.Collection)null;
            // StatementAdderMethod cloned existing statement
            con.data(vc_136557);
            // MethodAssertGenerator build local variable
            Object o_15_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_add343734_cf344375 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343851_failAssert44_literalMutation345362_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().header("User-Agent");
                con.userAgent("Mozilla");
                // StatementAdderOnAssert create random local variable
                boolean vc_136322 = true;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_136320 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderMethod cloned existing statement
                vc_136320.ignoreContentType(vc_136322);
                // MethodAssertGenerator build local variable
                Object o_13_0 = con.request().header("User-Agent");
                org.junit.Assert.fail("userAgent_cf343851 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("userAgent_cf343851_failAssert44_literalMutation345362 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343940_failAssert0_literalMutation344558() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("+WtuTSPb>b");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_3_0);
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create null value
            java.net.URL vc_136375 = (java.net.URL)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_136375);
            // StatementAdderMethod cloned existing statement
            con.url(vc_136375);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343940 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343934_failAssert8_literalMutation344699() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("User-Agent");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
            // AssertGenerator replace invocation
            org.jsoup.Connection o_userAgent_cf343934_failAssert8_literalMutation344699__9 = con.userAgent("zozilla");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_userAgent_cf343934_failAssert8_literalMutation344699__9.equals(con));
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_136372 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_136372, "");
            // StatementAdderMethod cloned existing statement
            con.url(vc_136372);
            // MethodAssertGenerator build local variable
            Object o_11_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343934 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343885_failAssert53_literalMutation345552() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
            // MethodAssertGenerator build local variable
            Object o_3_0 = con.request().header("Ustr-Agent");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_3_0);
            con.userAgent("Mozilla");
            // StatementAdderOnAssert create random local variable
            int vc_136345 = 1052260855;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_136345, 1052260855);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_16079 = "http://example.com/";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_16079, "http://example.com/");
            // StatementAdderMethod cloned existing statement
            con.proxy(String_vc_16079, vc_136345);
            // MethodAssertGenerator build local variable
            Object o_13_0 = con.request().header("User-Agent");
            org.junit.Assert.fail("userAgent_cf343885 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343883_failAssert34_literalMutation345165_cf358571_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_17_1 = 1052260855;
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().header("User-Agent");
                // MethodAssertGenerator build local variable
                Object o_9_0 = o_3_0;
                // AssertGenerator replace invocation
                org.jsoup.Connection o_userAgent_cf343883_failAssert34_literalMutation345165__9 = con.userAgent("Aozilla");
                // MethodAssertGenerator build local variable
                Object o_13_0 = o_userAgent_cf343883_failAssert34_literalMutation345165__9.equals(con);
                // StatementAdderOnAssert create random local variable
                int vc_136345 = 1052260855;
                // MethodAssertGenerator build local variable
                Object o_17_0 = vc_136345;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_136344 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_21_0 = vc_136344;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_136341 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create random local variable
                int vc_142187 = 1651034392;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_16779 = "http://example.com/";
                // StatementAdderMethod cloned existing statement
                con.proxy(String_vc_16779, vc_142187);
                // MethodAssertGenerator build local variable
                Object o_31_0 = vc_136341;
                // StatementAdderMethod cloned existing statement
                vc_136341.proxy(vc_136344, vc_136345);
                // MethodAssertGenerator build local variable
                Object o_15_0 = con.request().header("User-Agent");
                org.junit.Assert.fail("userAgent_cf343883 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("userAgent_cf343883_failAssert34_literalMutation345165_cf358571 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343911_failAssert27_literalMutation345054_cf367571_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().header("User-Agent");
                // MethodAssertGenerator build local variable
                Object o_9_0 = o_3_0;
                con.userAgent("Mozilla");
                // StatementAdderOnAssert create null value
                java.lang.String vc_136360 = (java.lang.String)null;
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_136360;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_136358 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create random local variable
                int vc_146124 = -1208253456;
                // StatementAdderMethod cloned existing statement
                con.proxy(vc_136360, vc_146124);
                // MethodAssertGenerator build local variable
                Object o_22_0 = vc_136358;
                // StatementAdderMethod cloned existing statement
                vc_136358.requestBody(vc_136360);
                // MethodAssertGenerator build local variable
                Object o_13_0 = con.request().header("");
                org.junit.Assert.fail("userAgent_cf343911 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("userAgent_cf343911_failAssert27_literalMutation345054_cf367571 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343956_failAssert20_literalMutation344924_cf348424_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().header("");
                // MethodAssertGenerator build local variable
                Object o_9_0 = o_3_0;
                con.userAgent("Mozilla");
                // StatementAdderOnAssert create null value
                java.lang.String vc_136386 = (java.lang.String)null;
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_136386;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_136384 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_137783 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                con.data(vc_137783);
                // MethodAssertGenerator build local variable
                Object o_22_0 = vc_136384;
                // StatementAdderMethod cloned existing statement
                vc_136384.data(vc_136386);
                // MethodAssertGenerator build local variable
                Object o_13_0 = con.request().header("User-Agent");
                org.junit.Assert.fail("userAgent_cf343956 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("userAgent_cf343956_failAssert20_literalMutation344924_cf348424 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of org.jsoup.helper.HttpConnectionTest#userAgent */
    @org.junit.Test(timeout = 10000)
    public void userAgent_cf343946_failAssert2_literalMutation344604_cf364146_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                org.jsoup.Connection con = org.jsoup.helper.HttpConnection.connect("http://example.com/");
                // MethodAssertGenerator build local variable
                Object o_3_0 = con.request().header("Use}-Agent");
                // MethodAssertGenerator build local variable
                Object o_9_0 = o_3_0;
                con.userAgent("Mozilla");
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_136380 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_14_0 = vc_136380;
                // StatementAdderOnAssert create null value
                org.jsoup.helper.HttpConnection vc_136377 = (org.jsoup.helper.HttpConnection)null;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_144656 = new java.lang.String();
                // StatementAdderMethod cloned existing statement
                vc_136377.connect(vc_144656);
                // MethodAssertGenerator build local variable
                Object o_22_0 = vc_136377;
                // StatementAdderMethod cloned existing statement
                vc_136377.userAgent(vc_136380);
                // MethodAssertGenerator build local variable
                Object o_13_0 = con.request().header("User-Agent");
                org.junit.Assert.fail("userAgent_cf343946 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("userAgent_cf343946_failAssert2_literalMutation344604_cf364146 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

