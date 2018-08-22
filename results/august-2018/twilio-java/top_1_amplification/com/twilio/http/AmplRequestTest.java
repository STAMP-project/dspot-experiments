package com.twilio.http;


import com.google.common.collect.Range;
import com.twilio.exception.ApiException;
import com.twilio.rest.Domains;
import java.net.MalformedURLException;
import java.net.URL;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;


public class AmplRequestTest {
    @Test(timeout = 10000)
    public void testConstructorWithDomainlitString209215() throws Exception {
        Request request = new Request(HttpMethod.GET, Domains.IPMESSAGING.toString(), "/vY/uri");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("https://chat.twilio.com/vY/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.getMethod();
        request.getUrl();
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("https://chat.twilio.com/vY/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructorWithDomain_mg209227() throws Exception {
        Request request = new Request(HttpMethod.GET, Domains.IPMESSAGING.toString(), "/v1/uri");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("https://chat.twilio.com/v1/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.getMethod();
        request.getUrl();
        URL o_testConstructorWithDomain_mg209227__6 = request.constructURL();
        Assert.assertEquals("https://chat.twilio.com/v1/uri", ((URL) (o_testConstructorWithDomain_mg209227__6)).toString());
        Assert.assertEquals("/v1/uri", ((URL) (o_testConstructorWithDomain_mg209227__6)).getPath());
        Assert.assertEquals("chat.twilio.com", ((URL) (o_testConstructorWithDomain_mg209227__6)).getAuthority());
        Assert.assertNull(((URL) (o_testConstructorWithDomain_mg209227__6)).getQuery());
        Assert.assertEquals("https", ((URL) (o_testConstructorWithDomain_mg209227__6)).getProtocol());
        Assert.assertEquals("/v1/uri", ((URL) (o_testConstructorWithDomain_mg209227__6)).getFile());
        Assert.assertEquals("chat.twilio.com", ((URL) (o_testConstructorWithDomain_mg209227__6)).getHost());
        Assert.assertNull(((URL) (o_testConstructorWithDomain_mg209227__6)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testConstructorWithDomain_mg209227__6)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (o_testConstructorWithDomain_mg209227__6)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testConstructorWithDomain_mg209227__6)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("https://chat.twilio.com/v1/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructorWithDomain_mg209225_add209763() throws Exception {
        String __DSPOT_value_22564 = "{uf22vXU].=)v!M2Hkc3";
        String __DSPOT_name_22563 = "R ;J2NORjgJUzp1:GT9`";
        Request request = new Request(HttpMethod.GET, Domains.IPMESSAGING.toString(), "/v1/uri");
        Assert.assertEquals("https://chat.twilio.com/v1/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.getMethod();
        request.getUrl();
        request.addPostParam(__DSPOT_name_22563, __DSPOT_value_22564);
        ((HttpMethod) (((Request) (request)).getMethod())).toString();
        Assert.assertEquals("https://chat.twilio.com/v1/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertFalse(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructorWithDomain_mg209227litString209290_failAssert800() throws Exception {
        try {
            Request request = new Request(HttpMethod.GET, Domains.IPMESSAGING.toString(), "https://api.twilio.com/2010-04-01/foobar?baz>=3");
            request.getMethod();
            request.getUrl();
            URL o_testConstructorWithDomain_mg209227__6 = request.constructURL();
            org.junit.Assert.fail("testConstructorWithDomain_mg209227litString209290 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://chat.twilio.comhttps://api.twilio.com/2010-04-01/foobar?baz>=3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructorWithDomain_mg209227litString209294_failAssert802() throws Exception {
        try {
            Request request = new Request(HttpMethod.GET, Domains.IPMESSAGING.toString(), "E1o:94e");
            request.getMethod();
            request.getUrl();
            URL o_testConstructorWithDomain_mg209227__6 = request.constructURL();
            org.junit.Assert.fail("testConstructorWithDomain_mg209227litString209294 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URL: https://chat.twilio.comE1o:94e", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructorWithDomain_mg209225_remove209914_mg217524() throws Exception {
        String __DSPOT_value_22564 = "{uf22vXU].=)v!M2Hkc3";
        String __DSPOT_name_22563 = "R ;J2NORjgJUzp1:GT9`";
        Request request = new Request(HttpMethod.GET, Domains.IPMESSAGING.toString(), "/v1/uri");
        Assert.assertEquals("https://chat.twilio.com/v1/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.getMethod();
        request.getUrl();
        URL o_testConstructorWithDomain_mg209225_remove209914_mg217524__8 = request.constructURL();
        Assert.assertEquals("https://chat.twilio.com/v1/uri", ((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).toString());
        Assert.assertEquals("/v1/uri", ((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getPath());
        Assert.assertEquals("chat.twilio.com", ((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getAuthority());
        Assert.assertNull(((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getQuery());
        Assert.assertEquals("https", ((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getProtocol());
        Assert.assertEquals("/v1/uri", ((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getFile());
        Assert.assertEquals("chat.twilio.com", ((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getHost());
        Assert.assertNull(((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testConstructorWithDomain_mg209225_remove209914_mg217524__8)).getRef());
        Assert.assertEquals("https://chat.twilio.com/v1/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructorWithDomain_add209224_mg210079litString211598() throws Exception {
        Request request = new Request(HttpMethod.GET, Domains.IPMESSAGING.toString(), "/v1/auri");
        Assert.assertEquals("https://chat.twilio.com/v1/auri", ((Request) (request)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.getMethod();
        request.getUrl();
        request.getUrl();
        boolean o_testConstructorWithDomain_add209224_mg210079__7 = request.requiresAuthentication();
        Assert.assertEquals("https://chat.twilio.com/v1/auri", ((Request) (request)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURL() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertNull(((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLlitString126242_failAssert472() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "S#&<kP;!N&D+j]+8o.");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar");
            org.junit.Assert.fail("testConstructURLlitString126242 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comS#&<kP;!N&D+j]+8o.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLlitString126238() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertNull(((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLlitString126243_add126951() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "");
        Assert.assertEquals("https://api.twilio.com", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        ((URL) (url)).getQuery();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURL_mg126260litString126466_failAssert476() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "J0^[fQyj$x OVj1k-q");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar");
            String o_testConstructURL_mg126260__8 = r.encodeQueryParams();
            org.junit.Assert.fail("testConstructURL_mg126260litString126466 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comJ0^[fQyj$x OVj1k-q", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURL_mg126263null127974_remove134192() throws Exception, MalformedURLException {
        String __DSPOT_password_11574 = "IV_)zdMC0P)(nw&PRU4D";
        String __DSPOT_username_11573 = ",8@+hG6dJNA:W)X1Iok[";
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLlitString126240_add126723_mg135239() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04A-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04A-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        ((Request) (r)).getPassword();
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar");
        URL o_testConstructURLlitString126240_add126723_mg135239__9 = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04A-01/foobar", ((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).toString());
        Assert.assertEquals("/2010-04A-01/foobar", ((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getAuthority());
        Assert.assertNull(((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getQuery());
        Assert.assertEquals("https", ((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getProtocol());
        Assert.assertEquals("/2010-04A-01/foobar", ((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getHost());
        Assert.assertNull(((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testConstructURLlitString126240_add126723_mg135239__9)).getRef());
        Assert.assertEquals("https://api.twilio.com/2010-04A-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURL_mg126256_add127225litString129860_failAssert500() throws Exception, MalformedURLException {
        try {
            String __DSPOT_value_11569 = "]P1Y@`ifn:]jv-aDXkY=";
            String __DSPOT_name_11568 = "{Q#yVYgw^9OR @g/V.<}";
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "q0x[<XXL][>DOjf*>J");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar");
            r.addPostParam(__DSPOT_name_11568, __DSPOT_value_11569);
            r.addPostParam(__DSPOT_name_11568, __DSPOT_value_11569);
            org.junit.Assert.fail("testConstructURL_mg126256_add127225litString129860 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comq0x[<XXL][>DOjf*>J", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLURISyntaxException_failAssert0() throws Exception {
        try {
            Request request = new Request(HttpMethod.DELETE, "http://{");
            request.constructURL();
            org.junit.Assert.fail("testConstructURLURISyntaxException should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: http://{", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLURISyntaxException_remove136760() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "http://{");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("http://{", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLURISyntaxException_rv136771_failAssert512() throws Exception {
        try {
            Request request = new Request(HttpMethod.DELETE, "http://{");
            URL __DSPOT_invoc_3 = request.constructURL();
            __DSPOT_invoc_3.getAuthority();
            org.junit.Assert.fail("testConstructURLURISyntaxException_rv136771 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: http://{", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLURISyntaxExceptionlitString136756_failAssert527_mg137494() throws Exception {
        try {
            Object __DSPOT_o_13336 = new Object();
            Request request = new Request(HttpMethod.DELETE, "");
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
            Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
            Assert.assertEquals("", ((Request) (request)).getUrl());
            Assert.assertNull(((Request) (request)).getUsername());
            Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
            Assert.assertNull(((Request) (request)).getPassword());
            Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
            request.constructURL();
            org.junit.Assert.fail("testConstructURLURISyntaxExceptionlitString136756 should have thrown IllegalArgumentException");
            request.equals(__DSPOT_o_13336);
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLURISyntaxException_mg136761_failAssert537null138133null146689() throws Exception {
        try {
            String __DSPOT_value_13166 = "Pv1w<ol_D}V{#;*^q!H.";
            String __DSPOT_name_13165 = "&H|obzezf@/ZC,7 S HG";
            Request request = new Request(HttpMethod.DELETE, "http://{");
            Assert.assertEquals("http://{", ((Request) (request)).getUrl());
            Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
            Assert.assertNull(((Request) (request)).getPassword());
            Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
            Assert.assertNull(((Request) (request)).getUsername());
            Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
            request.constructURL();
            request.addPostParam(null, null);
            org.junit.Assert.fail("testConstructURLURISyntaxException_mg136761 should have thrown ApiException");
        } catch (ApiException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParam_add178730() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", "quux");
        r.addQueryParam("baz", "quux");
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=quux", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=quux&baz=quux", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&baz=quux", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz=quux", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=quux", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=quux&baz=quux", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&baz=quux", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamlitString178701_failAssert679() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "X5xZGqLL%fc}G?]<a^");
            r.addQueryParam("baz", "quux");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
            org.junit.Assert.fail("testConstructURLWithParamlitString178701 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comX5xZGqLL%fc}G?]<a^?baz=quux", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParam_remove178732() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz=quux", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamnull178742() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", null);
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz=quux", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParam_mg178737_add181121() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", "quux");
        r.addQueryParam("baz", "quux");
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        String o_testConstructURLWithParam_mg178737__9 = r.encodeQueryParams();
        Assert.assertEquals("baz=quux&baz=quux", o_testConstructURLWithParam_mg178737__9);
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamlitString178702null182438() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "");
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", null);
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamlitString178705_remove181971() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParam_remove178732litString178876_failAssert688() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "Gf</omPD9?tS59yf^G");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
            org.junit.Assert.fail("testConstructURLWithParam_remove178732litString178876 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comGf</omPD9?tS59yf^G", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamlitString178716_remove181974_add187644() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        ((HttpMethod) (((Request) (r)).getMethod())).toString();
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParam_mg178739_remove181982litString184384_failAssert715() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "w%622R(6+T%+N*;5an");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
            boolean o_testConstructURLWithParam_mg178739__9 = r.requiresAuthentication();
            org.junit.Assert.fail("testConstructURLWithParam_mg178739_remove181982litString184384 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comw%622R(6+T%+N*;5an", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParam_mg178737_add181121_mg191052() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", "quux");
        r.addQueryParam("baz", "quux");
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        String o_testConstructURLWithParam_mg178737__9 = r.encodeQueryParams();
        Assert.assertEquals("baz=quux&baz=quux", o_testConstructURLWithParam_mg178737__9);
        boolean o_testConstructURLWithParam_mg178737_add181121_mg191052__13 = r.requiresAuthentication();
        Assert.assertFalse(o_testConstructURLWithParam_mg178737_add181121_mg191052__13);
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("baz=quux&baz=quux", o_testConstructURLWithParam_mg178737__9);
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamlitString178716_remove181974_mg190424() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        URL o_testConstructURLWithParamlitString178716_remove181974_mg190424__8 = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getAuthority());
        Assert.assertNull(((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getQuery());
        Assert.assertEquals("https", ((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getHost());
        Assert.assertNull(((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testConstructURLWithParamlitString178716_remove181974_mg190424__8)).getRef());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamnull178742_mg182208_remove189629() throws Exception, MalformedURLException {
        String __DSPOT_value_19100 = "G:-CJCdRe(a]}Lg|G4T>";
        String __DSPOT_name_19099 = "XETaV1w qJJ*mA-DsmQQ";
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", null);
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamsnull192228() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", null);
        r.addQueryParam("garply", "xyzzy");
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?garply=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("garply=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?garply=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz=quux&garply=xyzzy", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&garply=xyzzy", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?garply=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("garply=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?garply=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamslitString192171_failAssert743() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "\n");
            r.addQueryParam("baz", "quux");
            r.addQueryParam("garply", "xyzzy");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
            org.junit.Assert.fail("testConstructURLWithParamslitString192171 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com\n?baz=quux&garply=xyzzy", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParams_add192215() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", "quux");
        r.addQueryParam("garply", "xyzzy");
        r.addQueryParam("garply", "xyzzy");
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy&garply=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=quux&garply=xyzzy&garply=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&garply=xyzzy&garply=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz=quux&garply=xyzzy", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&garply=xyzzy", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy&garply=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=quux&garply=xyzzy&garply=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&garply=xyzzy&garply=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamslitString192205() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", "quux");
        r.addQueryParam("garply", "xyzzy");
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=quux&garply=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&garply=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-01", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-01", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=quux&garply=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&garply=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamsnull192228_remove197664() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", null);
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamslitString192183_remove197611_remove206343() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamslitString192183_remove197610litString200819_failAssert778() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "\n");
            r.addQueryParam("garply", "xyzzy");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
            org.junit.Assert.fail("testConstructURLWithParamslitString192183_remove197610litString200819 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com\n?garply=xyzzy", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithParamslitString192186null198401_remove206583() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("garply", null);
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&garply=xyzzy");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParam() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", "quux");
        r.addQueryParam("baz", "xyzzy");
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=quux&baz=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz=quux&baz=xyzzy", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=quux&baz=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParamnull160873() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", null);
        r.addQueryParam("baz", "xyzzy");
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz=quux&baz=xyzzy", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParamlitString160823() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("", "quux");
        r.addQueryParam("baz", "xyzzy");
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?=quux&baz=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("=quux&baz=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?=quux&baz=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz=quux&baz=xyzzy", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?=quux&baz=xyzzy", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("=quux&baz=xyzzy", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?=quux&baz=xyzzy", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParamlitString160814_failAssert636() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "DG1;|srND5tW|UAW3O");
            r.addQueryParam("baz", "quux");
            r.addQueryParam("baz", "xyzzy");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
            org.junit.Assert.fail("testConstructURLWithMultivaluedParamlitString160814 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comDG1;|srND5tW|UAW3O?baz=quux&baz=xyzzy", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParamnull160872_failAssert631_add166229() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
            Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
            Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
            Assert.assertNull(((Request) (r)).getUsername());
            Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
            Assert.assertNull(((Request) (r)).getPassword());
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
            Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
            r.addQueryParam(null, "quux");
            r.addQueryParam(null, "quux");
            r.addQueryParam("baz", "xyzzy");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
            org.junit.Assert.fail("testConstructURLWithMultivaluedParamnull160872 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParamlitString160853_mg166458() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", "quux");
        r.addQueryParam("baz", "xyzzy");
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobarbaz=quux&baz=xyzzy");
        URL o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10 = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getAuthority());
        Assert.assertEquals("baz=quux&baz=xyzzy", ((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getQuery());
        Assert.assertEquals("https", ((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getHost());
        Assert.assertNull(((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testConstructURLWithMultivaluedParamlitString160853_mg166458__10)).getRef());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParamnull160873null166995() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", null);
        r.addQueryParam("baz", null);
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", "quux");
        r.addQueryParam("baz", "xyzzy");
        URL url = r.constructURL();
        ((URL) (url)).toString();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
        URL o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11 = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getAuthority());
        Assert.assertEquals("baz=quux&baz=xyzzy", ((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getQuery());
        Assert.assertEquals("https", ((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz=quux&baz=xyzzy", ((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getHost());
        Assert.assertNull(((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testConstructURLWithMultivaluedParamlitString160818_add165372_mg177356__11)).getRef());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParamlitString160812_remove166286null177556() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/201H0-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/201H0-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz", null);
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
        Assert.assertEquals("https://api.twilio.com/201H0-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithMultivaluedParamlitString160822_remove166275_remove175832() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz=quux&baz=xyzzy");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParamlitString147340_failAssert574() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "\n");
            r.addQueryParam("baz>", "3");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
            org.junit.Assert.fail("testConstructURLWithInequalityParamlitString147340 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com\n?baz%3E=3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParam_add147364() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz>", "3");
        r.addQueryParam("baz>", "3");
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=3&baz%3E=3", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=3&baz%3E=3", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=3&baz%3E=3", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=3", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=3", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=3", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=3&baz%3E=3", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=3&baz%3E=3", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=3&baz%3E=3", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParam_remove147366() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=3", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=3", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=3", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParamnull147376() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam("baz>", null);
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=3", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=3", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=3", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParam_remove147366litString147505_failAssert592() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "\n");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
            org.junit.Assert.fail("testConstructURLWithInequalityParam_remove147366litString147505 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com\n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParamlitString147349null150790() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryParam(":", null);
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParamlitString147350_remove150442() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParamlitString147344_remove150406litString151619_failAssert604() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
            org.junit.Assert.fail("testConstructURLWithInequalityParamlitString147344_remove150406litString151619 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comhttps://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParamlitString147346null150801litString152410_failAssert607() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
            r.addQueryParam("{$!R", null);
            URL url = r.constructURL();
            URL expected = new URL("\n");
            org.junit.Assert.fail("testConstructURLWithInequalityParamlitString147346null150801litString152410 should have thrown MalformedURLException");
        } catch (MalformedURLException expected) {
            Assert.assertEquals("no protocol: \n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParamlitString147356_remove150410_mg159050() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com72010-04-01/foobar?baz>=3");
        URL o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8 = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getAuthority());
        Assert.assertNull(((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getQuery());
        Assert.assertEquals("https", ((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getHost());
        Assert.assertNull(((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testConstructURLWithInequalityParamlitString147356_remove150410_mg159050__8)).getRef());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testConstructURLWithInequalityParamlitString147345_remove150413litString151635() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "xyzzy");
        Assert.assertEquals("https://api.twilio.comxyzzy", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=3");
        Assert.assertEquals("https://api.twilio.comxyzzy", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeLowerBound_remove22521() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-01", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-01", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeLowerBound_add22518() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryDateRange("baz", Range.greaterThan(new LocalDate(2014, 1, 1)));
        r.addQueryDateRange("baz", Range.greaterThan(new LocalDate(2014, 1, 1)));
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-01&baz%3E=2014-01-01", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-01&baz%3E=2014-01-01", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-01&baz%3E=2014-01-01", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-01", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-01", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-01&baz%3E=2014-01-01", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-01&baz%3E=2014-01-01", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-01&baz%3E=2014-01-01", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeLowerBoundlitString22481_failAssert85() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "]U2bBv[ pfV Y7n4$Z");
            r.addQueryDateRange("baz", Range.greaterThan(new LocalDate(2014, 1, 1)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
            org.junit.Assert.fail("testAddQueryDateRangeLowerBoundlitString22481 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com]U2bBv[ pfV Y7n4$Z?baz%3E=2014-01-01", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeLowerBoundlitString22487_remove26517() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeLowerBound_mg22528litString23025_failAssert108() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "\n");
            r.addQueryDateRange("baz", Range.greaterThan(new LocalDate(2014, 1, 1)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
            boolean o_testAddQueryDateRangeLowerBound_mg22528__11 = r.requiresAuthentication();
            org.junit.Assert.fail("testAddQueryDateRangeLowerBound_mg22528litString23025 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com\n?baz%3E=2014-01-01", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeLowerBoundlitNum22515_failAssert92litString23378litNum30772() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
            Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
            Assert.assertNull(((Request) (r)).getPassword());
            Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
            Assert.assertNull(((Request) (r)).getUsername());
            Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
            Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
            r.addQueryDateRange("baz", Range.greaterThan(new LocalDate(Integer.MIN_VALUE, 1, Integer.MIN_VALUE)));
            URL url = r.constructURL();
            URL expected = new URL(":");
            org.junit.Assert.fail("testAddQueryDateRangeLowerBoundlitNum22515 should have thrown IllegalFieldValueException");
        } catch (IllegalFieldValueException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01");
        boolean o_testAddQueryDateRangeLowerBound_mg22528__11 = r.requiresAuthentication();
        URL o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11 = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getAuthority());
        Assert.assertNull(((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getQuery());
        Assert.assertEquals("https", ((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getHost());
        Assert.assertNull(((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testAddQueryDateRangeLowerBound_mg22528_remove26538_mg37010__11)).getRef());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeUpperBound_add39461() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryDateRange("baz", Range.lessThan(new LocalDate(2014, 1, 1)));
        r.addQueryDateRange("baz", Range.lessThan(new LocalDate(2014, 1, 1)));
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3C=2014-01-01&baz%3C=2014-01-01", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3C=2014-01-01&baz%3C=2014-01-01", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3C=2014-01-01&baz%3C=2014-01-01", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz<=2014-01-01", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz<=2014-01-01", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3C=2014-01-01&baz%3C=2014-01-01", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3C=2014-01-01&baz%3C=2014-01-01", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3C=2014-01-01&baz%3C=2014-01-01", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeUpperBoundlitString39424_failAssert164() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "ah.i8^-d`08jN_fi[V");
            r.addQueryDateRange("baz", Range.lessThan(new LocalDate(2014, 1, 1)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
            org.junit.Assert.fail("testAddQueryDateRangeUpperBoundlitString39424 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comah.i8^-d`08jN_fi[V?baz%3C=2014-01-01", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeUpperBound_remove39464() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz<=2014-01-01", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz<=2014-01-01", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeUpperBound_remove39464_add41281() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
        ((URL) (expected)).getRef();
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeUpperBoundlitNum39447_failAssert176_add43446_rv55487() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
            Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
            Assert.assertNull(((Request) (r)).getUsername());
            Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
            Assert.assertNull(((Request) (r)).getPassword());
            Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
            Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
            Range<LocalDate> __DSPOT_invoc_30 = Range.lessThan(new LocalDate(Integer.MIN_VALUE, 1, 1));
            r.addQueryDateRange("baz", Range.lessThan(new LocalDate(Integer.MIN_VALUE, 1, 1)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01");
            org.junit.Assert.fail("testAddQueryDateRangeUpperBoundlitNum39447 should have thrown IllegalFieldValueException");
            __DSPOT_invoc_30.hashCode();
        } catch (IllegalFieldValueException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeClosedlitString7_failAssert5() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "\n");
            r.addQueryDateRange("baz", Range.closed(new LocalDate(2014, 1, 10), new LocalDate(2014, 6, 1)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
            org.junit.Assert.fail("testAddQueryDateRangeClosedlitString7 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com\n?baz%3E=2014-01-10&baz%3C=2014-06-01", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeClosed_add60() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryDateRange("baz", Range.closed(new LocalDate(2014, 1, 10), new LocalDate(2014, 6, 1)));
        r.addQueryDateRange("baz", Range.closed(new LocalDate(2014, 1, 10), new LocalDate(2014, 6, 1)));
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-10&baz%3E=2014-01-10&baz%3C=2014-06-01&baz%3C=2014-06-01", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-10&baz%3E=2014-01-10&baz%3C=2014-06-01&baz%3C=2014-06-01", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-10&baz%3E=2014-01-10&baz%3C=2014-06-01&baz%3C=2014-06-01", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-10&baz<=2014-06-01", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-10&baz%3E=2014-01-10&baz%3C=2014-06-01&baz%3C=2014-06-01", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-10&baz%3E=2014-01-10&baz%3C=2014-06-01&baz%3C=2014-06-01", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-10&baz%3E=2014-01-10&baz%3C=2014-06-01&baz%3C=2014-06-01", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeClosed_remove63() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-10&baz<=2014-06-01", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeClosednull72_remove6237() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeClosed_mg64_remove6222litString9473_failAssert61() throws Exception, MalformedURLException {
        try {
            String __DSPOT_value_1 = "xHdm7#=ToX)D7x>[Bob5";
            String __DSPOT_name_0 = " V!3a(!.#b{[Iz>YSe|%";
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/]foobar");
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
            r.addPostParam(__DSPOT_name_0, __DSPOT_value_1);
            org.junit.Assert.fail("testAddQueryDateRangeClosed_mg64_remove6222litString9473 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com/2010-04-01/]foobar", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateRangeClosed_mg67litNum2203_failAssert47litString10812() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "");
            Assert.assertEquals("https://api.twilio.com", ((Request) (r)).getUrl());
            Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
            Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
            Assert.assertNull(((Request) (r)).getPassword());
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
            Assert.assertNull(((Request) (r)).getUsername());
            Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
            r.addQueryDateRange("baz", Range.closed(new LocalDate(2014, 1, 10), new LocalDate(2013, 6, 1)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10&baz<=2014-06-01");
            String o_testAddQueryDateRangeClosed_mg67__12 = r.encodeFormBody();
            org.junit.Assert.fail("testAddQueryDateRangeClosed_mg67litNum2203 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeLowerBound_add87003() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryDateTimeRange("baz", Range.greaterThan(new DateTime(2014, 1, 1, 0, 0)));
        r.addQueryDateTimeRange("baz", Range.greaterThan(new DateTime(2014, 1, 1, 0, 0)));
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-01T00%3A00%3A00&baz%3E=2014-01-01T00%3A00%3A00", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-01T00%3A00%3A00&baz%3E=2014-01-01T00%3A00%3A00", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-01T00%3A00%3A00&baz%3E=2014-01-01T00%3A00%3A00", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-01T00:00:00", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-01T00:00:00", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-01T00%3A00%3A00&baz%3E=2014-01-01T00%3A00%3A00", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-01T00%3A00%3A00&baz%3E=2014-01-01T00%3A00%3A00", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-01T00%3A00%3A00&baz%3E=2014-01-01T00%3A00%3A00", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeLowerBoundlitString86954_failAssert340() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "BLEQzyg1%}kWq0Lsgl");
            r.addQueryDateTimeRange("baz", Range.greaterThan(new DateTime(2014, 1, 1, 0, 0)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeLowerBoundlitString86954 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comBLEQzyg1%}kWq0Lsgl?baz%3E=2014-01-01T00%3A00%3A00", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeLowerBound_remove87006() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-01T00:00:00", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-01T00:00:00", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeLowerBoundlitNum86979_failAssert356_add92373() throws Exception, MalformedURLException {
        try {
            Domains.API.toString();
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
            Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
            Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
            Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
            Assert.assertNull(((Request) (r)).getPassword());
            Assert.assertNull(((Request) (r)).getUsername());
            Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
            r.addQueryDateTimeRange("baz", Range.greaterThan(new DateTime(-416104748, 1, 1, 0, 0)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeLowerBoundlitNum86979 should have thrown IllegalFieldValueException");
        } catch (IllegalFieldValueException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeLowerBoundlitNum86974litString87665_failAssert379() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), ">tJ`_K#Kms.fXCw!h<");
            r.addQueryDateTimeRange("baz", Range.greaterThan(new DateTime(2015, 1, 1, 0, 0)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeLowerBoundlitNum86974litString87665 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com>tJ`_K#Kms.fXCw!h<?baz%3E=2015-01-01T00%3A00%3A00", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeLowerBoundlitNum86979_failAssert356_mg92927null105480() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
            Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
            Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
            Assert.assertNull(((Request) (r)).getPassword());
            Assert.assertNull(((Request) (r)).getUsername());
            Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
            Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
            r.addQueryDateTimeRange(null, Range.greaterThan(new DateTime(-416104748, 1, 1, 0, 0)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-01T00:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeLowerBoundlitNum86979 should have thrown IllegalFieldValueException");
            r.encodeFormBody();
        } catch (IllegalFieldValueException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeUpperBoundlitString106145_failAssert398() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "@-D-9Fvi`=(`F)`E5;");
            r.addQueryDateTimeRange("baz", Range.lessThan(new DateTime(2014, 1, 1, 22, 0)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeUpperBoundlitString106145 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com@-D-9Fvi`=(`F)`E5;?baz%3C=2014-01-01T22%3A00%3A00", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeUpperBound_remove106197() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz<=2014-01-01T22:00:00", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz<=2014-01-01T22:00:00", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeUpperBound_add106194() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryDateTimeRange("baz", Range.lessThan(new DateTime(2014, 1, 1, 22, 0)));
        r.addQueryDateTimeRange("baz", Range.lessThan(new DateTime(2014, 1, 1, 22, 0)));
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3C=2014-01-01T22%3A00%3A00&baz%3C=2014-01-01T22%3A00%3A00", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3C=2014-01-01T22%3A00%3A00&baz%3C=2014-01-01T22%3A00%3A00", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3C=2014-01-01T22%3A00%3A00&baz%3C=2014-01-01T22%3A00%3A00", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz<=2014-01-01T22:00:00", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz<=2014-01-01T22:00:00", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3C=2014-01-01T22%3A00%3A00&baz%3C=2014-01-01T22%3A00%3A00", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3C=2014-01-01T22%3A00%3A00&baz%3C=2014-01-01T22%3A00%3A00", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3C=2014-01-01T22%3A00%3A00&baz%3C=2014-01-01T22%3A00%3A00", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeUpperBoundlitString106149_remove111606() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeUpperBound_add106194_mg111930() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryDateTimeRange("baz", Range.lessThan(new DateTime(2014, 1, 1, 22, 0)));
        r.addQueryDateTimeRange("baz", Range.lessThan(new DateTime(2014, 1, 1, 22, 0)));
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
        String o_testAddQueryDateTimeRangeUpperBound_add106194_mg111930__14 = r.encodeQueryParams();
        Assert.assertEquals("baz%3C=2014-01-01T22%3A00%3A00&baz%3C=2014-01-01T22%3A00%3A00", o_testAddQueryDateTimeRangeUpperBound_add106194_mg111930__14);
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeUpperBound_mg106203litString106704_failAssert442() throws Exception, MalformedURLException {
        try {
            Request __DSPOT_o_9636 = null;
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "n}$T3%|w6(L3?`w8On");
            r.addQueryDateTimeRange("baz", Range.lessThan(new DateTime(2014, 1, 1, 22, 0)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
            boolean o_testAddQueryDateTimeRangeUpperBound_mg106203__12 = r.equals(__DSPOT_o_9636);
            org.junit.Assert.fail("testAddQueryDateTimeRangeUpperBound_mg106203litString106704 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.comn}$T3%|w6(L3?`w8On?baz%3C=2014-01-01T22%3A00%3A00", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeUpperBoundlitNum106168_failAssert407_add111522_rv125401() throws Exception, MalformedURLException {
        try {
            Object __DSPOT_arg0_11565 = new Object();
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
            Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
            Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
            Assert.assertNull(((Request) (r)).getUsername());
            Assert.assertNull(((Request) (r)).getPassword());
            Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
            Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
            r.addQueryDateTimeRange("baz", Range.lessThan(new DateTime(Integer.MIN_VALUE, 1, 1, 22, 0)));
            URL __DSPOT_invoc_33 = r.constructURL();
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz<=2014-01-01T22:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeUpperBoundlitNum106168 should have thrown IllegalFieldValueException");
            __DSPOT_invoc_33.equals(__DSPOT_arg0_11565);
        } catch (IllegalFieldValueException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeClosed_add56415() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryDateTimeRange("baz", Range.closed(new DateTime(2014, 1, 10, 14, 0), new DateTime(2014, 6, 1, 16, 0)));
        r.addQueryDateTimeRange("baz", Range.closed(new DateTime(2014, 1, 10, 14, 0), new DateTime(2014, 6, 1, 16, 0)));
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-10T14%3A00%3A00&baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-10T14%3A00%3A00&baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeClosedlitString56338_failAssert243() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "\n");
            r.addQueryDateTimeRange("baz", Range.closed(new DateTime(2014, 1, 10, 14, 0), new DateTime(2014, 6, 1, 16, 0)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeClosedlitString56338 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com\n?baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeClosedlitString56349() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryDateTimeRange("baz", Range.closed(new DateTime(2014, 1, 10, 14, 0), new DateTime(2014, 6, 1, 16, 0)));
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api2twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
        Assert.assertEquals("https://api2twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api2twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", ((URL) (expected)).getFile());
        Assert.assertEquals("api2twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeClosed_remove56418() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        URL url = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", ((URL) (expected)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (expected)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getAuthority());
        Assert.assertEquals("baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", ((URL) (expected)).getQuery());
        Assert.assertEquals("https", ((URL) (expected)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00", ((URL) (expected)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (expected)).getHost());
        Assert.assertNull(((URL) (expected)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (expected)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (expected)).getDefaultPort())));
        Assert.assertNull(((URL) (expected)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((URL) (url)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getAuthority());
        Assert.assertNull(((URL) (url)).getQuery());
        Assert.assertEquals("https", ((URL) (url)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (url)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (url)).getHost());
        Assert.assertNull(((URL) (url)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (url)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (url)).getDefaultPort())));
        Assert.assertNull(((URL) (url)).getRef());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeClosed_add56417litString57386_failAssert287() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/9[*s,p86W8sIR2E$6");
            r.addQueryDateTimeRange("baz", Range.closed(new DateTime(2014, 1, 10, 14, 0), new DateTime(2014, 6, 1, 16, 0)));
            URL o_testAddQueryDateTimeRangeClosed_add56417__8 = r.constructURL();
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeClosed_add56417litString57386 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: https://api.twilio.com/9[*s,p86W8sIR2E$6?baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeClosed_add56415_mg66594() throws Exception, MalformedURLException {
        Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addQueryDateTimeRange("baz", Range.closed(new DateTime(2014, 1, 10, 14, 0), new DateTime(2014, 6, 1, 16, 0)));
        r.addQueryDateTimeRange("baz", Range.closed(new DateTime(2014, 1, 10, 14, 0), new DateTime(2014, 6, 1, 16, 0)));
        URL url = r.constructURL();
        URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
        URL o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16 = r.constructURL();
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).toString());
        Assert.assertEquals("/2010-04-01/foobar", ((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getPath());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getAuthority());
        Assert.assertEquals("baz%3E=2014-01-10T14%3A00%3A00&baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getQuery());
        Assert.assertEquals("https", ((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getProtocol());
        Assert.assertEquals("/2010-04-01/foobar?baz%3E=2014-01-10T14%3A00%3A00&baz%3E=2014-01-10T14%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00&baz%3C=2014-06-01T16%3A00%3A00", ((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getFile());
        Assert.assertEquals("api.twilio.com", ((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getHost());
        Assert.assertNull(((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getPort())));
        Assert.assertEquals(443, ((int) (((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testAddQueryDateTimeRangeClosed_add56415_mg66594__16)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertFalse(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeClosedlitNum56361_failAssert251_add66079() throws Exception, MalformedURLException {
        try {
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
            Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
            Assert.assertNull(((Request) (r)).getPassword());
            Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
            Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
            Assert.assertNull(((Request) (r)).getUsername());
            Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
            r.addQueryDateTimeRange("baz", Range.closed(new DateTime(-1447413440, 1, 10, 14, 0), new DateTime(2014, 6, 1, 16, 0)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeClosedlitNum56361 should have thrown IllegalFieldValueException");
        } catch (IllegalFieldValueException expected) {
            expected.getMessage();
        }
    }

    @Test(timeout = 10000)
    public void testAddQueryDateTimeRangeClosedlitNum56370_failAssert250_add66070litString70515() throws Exception, MalformedURLException {
        try {
            Domains.API.toString();
            Request r = new Request(HttpMethod.GET, Domains.API.toString(), "/2010-04-01/foobar");
            Assert.assertEquals("https://api.twilio.com/2010-04-01/foobar", ((Request) (r)).getUrl());
            Assert.assertNull(((Request) (r)).getPassword());
            Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
            Assert.assertNull(((Request) (r)).getUsername());
            Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
            Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
            Assert.assertEquals("GET", ((HttpMethod) (((Request) (r)).getMethod())).toString());
            r.addQueryDateTimeRange("bz", Range.closed(new DateTime(2014, 1, Integer.MIN_VALUE, 14, 0), new DateTime(2014, 6, 1, 16, 0)));
            URL url = r.constructURL();
            URL expected = new URL("https://api.twilio.com/2010-04-01/foobar?baz>=2014-01-10T14:00:00&baz<=2014-06-01T16:00:00");
            org.junit.Assert.fail("testAddQueryDateTimeRangeClosedlitNum56370 should have thrown IllegalFieldValueException");
        } catch (IllegalFieldValueException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEncodeFormBodynull220318() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("baz", null);
        r.addPostParam("garply", "xyzzy");
        String encoded = r.encodeFormBody();
        Assert.assertEquals("garply=xyzzy", encoded);
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testEncodeFormBody_add220305() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("baz", "quux");
        r.addPostParam("garply", "xyzzy");
        r.addPostParam("garply", "xyzzy");
        String encoded = r.encodeFormBody();
        Assert.assertEquals("baz=quux&garply=xyzzy&garply=xyzzy", encoded);
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testEncodeFormBody_mg220311() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("baz", "quux");
        r.addPostParam("garply", "xyzzy");
        String encoded = r.encodeFormBody();
        Assert.assertEquals("baz=quux&garply=xyzzy", encoded);
        URL o_testEncodeFormBody_mg220311__7 = r.constructURL();
        Assert.assertEquals("http://example.com/foobar", ((URL) (o_testEncodeFormBody_mg220311__7)).toString());
        Assert.assertEquals("/foobar", ((URL) (o_testEncodeFormBody_mg220311__7)).getPath());
        Assert.assertEquals("example.com", ((URL) (o_testEncodeFormBody_mg220311__7)).getAuthority());
        Assert.assertNull(((URL) (o_testEncodeFormBody_mg220311__7)).getQuery());
        Assert.assertEquals("http", ((URL) (o_testEncodeFormBody_mg220311__7)).getProtocol());
        Assert.assertEquals("/foobar", ((URL) (o_testEncodeFormBody_mg220311__7)).getFile());
        Assert.assertEquals("example.com", ((URL) (o_testEncodeFormBody_mg220311__7)).getHost());
        Assert.assertNull(((URL) (o_testEncodeFormBody_mg220311__7)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testEncodeFormBody_mg220311__7)).getPort())));
        Assert.assertEquals(80, ((int) (((URL) (o_testEncodeFormBody_mg220311__7)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testEncodeFormBody_mg220311__7)).getRef());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("baz=quux&garply=xyzzy", encoded);
    }

    @Test(timeout = 10000)
    public void testEncodeFormBodylitString220287() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("baz", ":");
        r.addPostParam("garply", "xyzzy");
        String encoded = r.encodeFormBody();
        Assert.assertEquals("baz=%3A&garply=xyzzy", encoded);
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testEncodeFormBodylitString220292_mg223578() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("baz", "quux");
        r.addPostParam("1:`zKE", "xyzzy");
        String encoded = r.encodeFormBody();
        Assert.assertEquals("1%3A%60zKE=xyzzy&baz=quux", encoded);
        URL o_testEncodeFormBodylitString220292_mg223578__7 = r.constructURL();
        Assert.assertEquals("http://example.com/foobar", ((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).toString());
        Assert.assertEquals("/foobar", ((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getPath());
        Assert.assertEquals("example.com", ((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getAuthority());
        Assert.assertNull(((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getQuery());
        Assert.assertEquals("http", ((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getProtocol());
        Assert.assertEquals("/foobar", ((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getFile());
        Assert.assertEquals("example.com", ((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getHost());
        Assert.assertNull(((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getPort())));
        Assert.assertEquals(80, ((int) (((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testEncodeFormBodylitString220292_mg223578__7)).getRef());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("1%3A%60zKE=xyzzy&baz=quux", encoded);
    }

    @Test(timeout = 10000)
    public void testEncodeFormBody_mg220315_mg223411() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("baz", "quux");
        r.addPostParam("garply", "xyzzy");
        String encoded = r.encodeFormBody();
        Assert.assertEquals("baz=quux&garply=xyzzy", encoded);
        boolean o_testEncodeFormBody_mg220315__7 = r.requiresAuthentication();
        String o_testEncodeFormBody_mg220315_mg223411__10 = r.encodeFormBody();
        Assert.assertEquals("baz=quux&garply=xyzzy", o_testEncodeFormBody_mg220315_mg223411__10);
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("baz=quux&garply=xyzzy", encoded);
    }

    @Test(timeout = 10000)
    public void testEncodeFormBodylitString220280null223627() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("baz", "https://api.twilio.com/2010-04-01/foobar?baz=quux");
        r.addPostParam("garply", null);
        String encoded = r.encodeFormBody();
        Assert.assertEquals("baz=https%3A%2F%2Fapi.twilio.com%2F2010-04-01%2Ffoobar%3Fbaz%3Dquux", encoded);
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testEncodeFormBodylitString220285_add221985() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("baz", "");
        r.addPostParam("garply", "xyzzy");
        r.addPostParam("garply", "xyzzy");
        String encoded = r.encodeFormBody();
        Assert.assertEquals("baz=&garply=xyzzy&garply=xyzzy", encoded);
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testEncodeFormBody_mg220311litString220953_failAssert816() throws Exception {
        try {
            Request r = new Request(HttpMethod.POST, ":");
            r.addPostParam("baz", "quux");
            r.addPostParam("garply", "xyzzy");
            String encoded = r.encodeFormBody();
            URL o_testEncodeFormBody_mg220311__7 = r.constructURL();
            org.junit.Assert.fail("testEncodeFormBody_mg220311litString220953 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: :", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEncodeFormBodylitString220281_remove223075_mg233301() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("garply", "xyzzy");
        String encoded = r.encodeFormBody();
        Assert.assertEquals("garply=xyzzy", encoded);
        URL o_testEncodeFormBodylitString220281_remove223075_mg233301__6 = r.constructURL();
        Assert.assertEquals("http://example.com/foobar", ((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).toString());
        Assert.assertEquals("/foobar", ((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getPath());
        Assert.assertEquals("example.com", ((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getAuthority());
        Assert.assertNull(((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getQuery());
        Assert.assertEquals("http", ((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getProtocol());
        Assert.assertEquals("/foobar", ((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getFile());
        Assert.assertEquals("example.com", ((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getHost());
        Assert.assertNull(((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getPort())));
        Assert.assertEquals(80, ((int) (((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testEncodeFormBodylitString220281_remove223075_mg233301__6)).getRef());
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        Assert.assertEquals("garply=xyzzy", encoded);
    }

    @Test(timeout = 10000)
    public void testEncodeFormBodylitString220280_remove223050_remove232256() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        String encoded = r.encodeFormBody();
        Assert.assertEquals("", encoded);
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testEncodeFormBodylitString220281_remove223075null234392() throws Exception {
        Request r = new Request(HttpMethod.POST, "http://example.com/foobar");
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertTrue(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
        r.addPostParam("garply", null);
        String encoded = r.encodeFormBody();
        Assert.assertEquals("", encoded);
        Assert.assertEquals("http://example.com/foobar", ((Request) (r)).getUrl());
        Assert.assertFalse(((Request) (r)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (r)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (r)).getPassword());
        Assert.assertNull(((Request) (r)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (r)).getAuthString());
        Assert.assertEquals("POST", ((HttpMethod) (((Request) (r)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testGetPasswordlitString247047() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.setAuth("password", "password");
        request.getPassword();
        Assert.assertEquals("Basic cGFzc3dvcmQ6cGFzc3dvcmQ=", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("password", ((Request) (request)).getPassword());
        Assert.assertEquals("password", ((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testGetPassword_remove247065_add247712() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.getPassword();
        ((Request) (request)).getPostParams().isEmpty();
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testGetPassword_mg247067_mg248790_add253476() throws Exception {
        String __DSPOT_value_27824 = "0 p)c&!TY}#s`LdANoIj";
        String __DSPOT_name_27823 = "lPA8s.a{`eLvlO!>6m!?";
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.setAuth("username", "password");
        request.getPassword();
        request.addQueryParam(__DSPOT_name_27823, __DSPOT_value_27824);
        request.addQueryParam(__DSPOT_name_27823, __DSPOT_value_27824);
        String o_testGetPassword_mg247067_mg248790__8 = request.encodeQueryParams();
        Assert.assertEquals("lPA8s.a%7B%60eLvlO%21%3E6m%21%3F=0+p%29c%26%21TY%7D%23s%60LdANoIj&lPA8s.a%7B%60eLvlO%21%3E6m%21%3F=0+p%29c%26%21TY%7D%23s%60LdANoIj", o_testGetPassword_mg247067_mg248790__8);
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("password", ((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic dXNlcm5hbWU6cGFzc3dvcmQ=", ((Request) (request)).getAuthString());
        Assert.assertFalse(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("username", ((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testGetPassword_mg247068_failAssert849_rv248900litString251225_failAssert859() throws Exception {
        try {
            try {
                Request request = new Request(HttpMethod.DELETE, "\n");
                request.setAuth("username", "password");
                request.getPassword();
                URL __DSPOT_invoc_7 = request.constructURL();
                org.junit.Assert.fail("testGetPassword_mg247068 should have thrown IllegalArgumentException");
                __DSPOT_invoc_7.toExternalForm();
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("testGetPassword_mg247068_failAssert849_rv248900litString251225 should have thrown ApiException");
        } catch (ApiException expected_1) {
            Assert.assertEquals("Bad URI: \n", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetPassword_mg247067litString247440_remove256959() throws Exception {
        String __DSPOT_value_27824 = "\n";
        String __DSPOT_name_27823 = "lPA8s.a{`eLvlO!>6m!?";
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.setAuth("username", "password");
        request.addQueryParam(__DSPOT_name_27823, __DSPOT_value_27824);
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("password", ((Request) (request)).getPassword());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic dXNlcm5hbWU6cGFzc3dvcmQ=", ((Request) (request)).getAuthString());
        Assert.assertFalse(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("username", ((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testGetUsername() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.setAuth("username", "password");
        request.getUsername();
        Assert.assertEquals("Basic dXNlcm5hbWU6cGFzc3dvcmQ=", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("password", ((Request) (request)).getPassword());
        Assert.assertEquals("username", ((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testGetUsernamelitString259977() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.setAuth("username", "baz>");
        request.getUsername();
        Assert.assertEquals("Basic dXNlcm5hbWU6YmF6Pg==", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("baz>", ((Request) (request)).getPassword());
        Assert.assertEquals("username", ((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testGetUsernamelitString259975_add260819() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        ((Request) (request)).getQueryParams();
        request.setAuth("\n", "password");
        request.getUsername();
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("password", ((Request) (request)).getPassword());
        Assert.assertEquals("\n", ((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic CjpwYXNzd29yZA==", ((Request) (request)).getAuthString());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testGetUsername_mg259990_failAssert865_rv261806litString263883_failAssert872() throws Exception {
        try {
            try {
                Request request = new Request(HttpMethod.DELETE, "\n");
                request.setAuth("username", "password");
                request.getUsername();
                URL __DSPOT_invoc_7 = request.constructURL();
                org.junit.Assert.fail("testGetUsername_mg259990 should have thrown IllegalArgumentException");
                __DSPOT_invoc_7.getAuthority();
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("testGetUsername_mg259990_failAssert865_rv261806litString263883 should have thrown ApiException");
        } catch (ApiException expected_1) {
            Assert.assertEquals("Bad URI: \n", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetUsername_mg259988null261859null271888() throws Exception {
        String __DSPOT_value_29634 = "Z!ymi2>6t)Sy@!9vcYya";
        String __DSPOT_name_29633 = null;
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.setAuth("username", "password");
        request.getUsername();
        request.addPostParam(null, __DSPOT_value_29634);
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("password", ((Request) (request)).getPassword());
        Assert.assertEquals("username", ((Request) (request)).getUsername());
        Assert.assertEquals("Basic dXNlcm5hbWU6cGFzc3dvcmQ=", ((Request) (request)).getAuthString());
        Assert.assertFalse(((Request) (request)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testRequiresAuthenticationlitString272547() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        boolean o_testRequiresAuthenticationlitString272547__3 = request.requiresAuthentication();
        Assert.assertFalse(o_testRequiresAuthenticationlitString272547__3);
        request.setAuth("username", "?|B5Xg{=");
        boolean o_testRequiresAuthenticationlitString272547__5 = request.requiresAuthentication();
        Assert.assertTrue(o_testRequiresAuthenticationlitString272547__5);
        Assert.assertEquals("Basic dXNlcm5hbWU6P3xCNVhnez0=", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("?|B5Xg{=", ((Request) (request)).getPassword());
        Assert.assertEquals("username", ((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        Assert.assertFalse(o_testRequiresAuthenticationlitString272547__3);
    }

    @Test(timeout = 10000)
    public void testRequiresAuthentication_remove272554_mg274124() throws Exception {
        Request __DSPOT_o_31423 = null;
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        boolean o_testRequiresAuthentication_remove272554__3 = request.requiresAuthentication();
        boolean o_testRequiresAuthentication_remove272554__4 = request.requiresAuthentication();
        boolean o_testRequiresAuthentication_remove272554_mg274124__10 = request.equals(__DSPOT_o_31423);
        Assert.assertFalse(o_testRequiresAuthentication_remove272554_mg274124__10);
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testRequiresAuthenticationlitString272533_mg274145_failAssert884() throws Exception {
        try {
            Request request = new Request(HttpMethod.DELETE, "\n");
            boolean o_testRequiresAuthenticationlitString272533__3 = request.requiresAuthentication();
            request.setAuth("username", "password");
            boolean o_testRequiresAuthenticationlitString272533__5 = request.requiresAuthentication();
            request.constructURL();
            org.junit.Assert.fail("testRequiresAuthenticationlitString272533_mg274145 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: \n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testRequiresAuthentication_remove272554_add273230_add279518() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        ((Request) (request)).getUsername();
        boolean o_testRequiresAuthentication_remove272554__3 = request.requiresAuthentication();
        boolean o_testRequiresAuthentication_remove272554__4 = request.requiresAuthentication();
        ((Request) (request)).getPostParams();
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testRequiresAuthenticationlitString272533_mg274149_mg282423_failAssert886() throws Exception {
        try {
            Request request = new Request(HttpMethod.DELETE, "\n");
            boolean o_testRequiresAuthenticationlitString272533__3 = request.requiresAuthentication();
            request.setAuth("username", "password");
            boolean o_testRequiresAuthenticationlitString272533__5 = request.requiresAuthentication();
            boolean o_testRequiresAuthenticationlitString272533_mg274149__10 = request.requiresAuthentication();
            request.constructURL();
            org.junit.Assert.fail("testRequiresAuthenticationlitString272533_mg274149_mg282423 should have thrown ApiException");
        } catch (ApiException expected) {
            Assert.assertEquals("Bad URI: \n", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEqualslitString235244() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.setAuth("usern2me", "password");
        boolean o_testEqualslitString235244__4 = request.equals(new Object());
        Assert.assertFalse(o_testEqualslitString235244__4);
        boolean o_testEqualslitString235244__6 = request.equals(null);
        Assert.assertFalse(o_testEqualslitString235244__6);
        Assert.assertEquals("Basic dXNlcm4ybWU6cGFzc3dvcmQ=", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("password", ((Request) (request)).getPassword());
        Assert.assertEquals("usern2me", ((Request) (request)).getUsername());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        Assert.assertFalse(o_testEqualslitString235244__4);
    }

    @Test(timeout = 10000)
    public void testEqualslitString235250litString235885() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        request.setAuth(":", "pssword");
        boolean o_testEqualslitString235250__4 = request.equals(new Object());
        boolean o_testEqualslitString235250__6 = request.equals(null);
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertEquals("pssword", ((Request) (request)).getPassword());
        Assert.assertEquals(":", ((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic Ojpwc3N3b3Jk", ((Request) (request)).getAuthString());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
    }

    @Test(timeout = 10000)
    public void testEquals_mg235266_remove236811_add242159() throws Exception {
        Request request = new Request(HttpMethod.DELETE, "/uri");
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        boolean o_testEquals_mg235266_remove236811_add242159__3 = request.equals(new Object());
        Assert.assertFalse(o_testEquals_mg235266_remove236811_add242159__3);
        boolean o_testEquals_mg235266__4 = request.equals(new Object());
        boolean o_testEquals_mg235266__6 = request.equals(null);
        String o_testEquals_mg235266__7 = request.encodeFormBody();
        Assert.assertEquals("", o_testEquals_mg235266__7);
        Assert.assertEquals("/uri", ((Request) (request)).getUrl());
        Assert.assertNull(((Request) (request)).getPassword());
        Assert.assertNull(((Request) (request)).getUsername());
        Assert.assertTrue(((Request) (request)).getPostParams().isEmpty());
        Assert.assertTrue(((Request) (request)).getQueryParams().isEmpty());
        Assert.assertEquals("Basic bnVsbDpudWxs", ((Request) (request)).getAuthString());
        Assert.assertEquals("DELETE", ((HttpMethod) (((Request) (request)).getMethod())).toString());
        Assert.assertFalse(o_testEquals_mg235266_remove236811_add242159__3);
    }

    @Test(timeout = 10000)
    public void testEquals_mg235265_failAssert842_rv237148litString238864_failAssert846() throws Exception {
        try {
            try {
                Request request = new Request(HttpMethod.DELETE, "\n");
                request.setAuth("username", "password");
                boolean o_testEquals_mg235265_failAssert842_rv237148__6 = request.equals(new Object());
                boolean o_testEquals_mg235265_failAssert842_rv237148__8 = request.equals(null);
                URL __DSPOT_invoc_9 = request.constructURL();
                org.junit.Assert.fail("testEquals_mg235265 should have thrown IllegalArgumentException");
                __DSPOT_invoc_9.getPort();
            } catch (IllegalArgumentException expected) {
            }
            org.junit.Assert.fail("testEquals_mg235265_failAssert842_rv237148litString238864 should have thrown ApiException");
        } catch (ApiException expected_1) {
            Assert.assertEquals("Bad URI: \n", expected_1.getMessage());
        }
    }
}

