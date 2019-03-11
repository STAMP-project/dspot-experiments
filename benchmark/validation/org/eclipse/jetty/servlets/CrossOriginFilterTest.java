/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.servlets;


import CrossOriginFilter.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER;
import CrossOriginFilter.ACCESS_CONTROL_ALLOW_HEADERS_HEADER;
import CrossOriginFilter.ACCESS_CONTROL_ALLOW_METHODS_HEADER;
import CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER;
import CrossOriginFilter.ACCESS_CONTROL_EXPOSE_HEADERS_HEADER;
import CrossOriginFilter.ACCESS_CONTROL_MAX_AGE_HEADER;
import CrossOriginFilter.ALLOWED_HEADERS_PARAM;
import CrossOriginFilter.ALLOWED_METHODS_PARAM;
import CrossOriginFilter.ALLOWED_ORIGINS_PARAM;
import CrossOriginFilter.ALLOWED_TIMING_ORIGINS_PARAM;
import CrossOriginFilter.ALLOW_CREDENTIALS_PARAM;
import CrossOriginFilter.CHAIN_PREFLIGHT_PARAM;
import CrossOriginFilter.TIMING_ALLOW_ORIGIN_HEADER;
import DispatcherType.REQUEST;
import HttpStatus.OK_200;
import HttpTester.Response;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletTester;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static CrossOriginFilter.ACCESS_CONTROL_REQUEST_HEADERS_HEADER;
import static CrossOriginFilter.ACCESS_CONTROL_REQUEST_METHOD_HEADER;


public class CrossOriginFilterTest {
    private ServletTester tester;

    @Test
    public void testRequestWithNoOriginArrivesToApplication() throws Exception {
        tester.getContext().addFilter(CrossOriginFilter.class, "/*", EnumSet.of(REQUEST));
        final CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = "" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n");
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithNonMatchingOrigin() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        String origin = "http://localhost";
        filterHolder.setInitParameter(ALLOWED_ORIGINS_PARAM, origin);
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String otherOrigin = origin.replace("localhost", "127.0.0.1");
        String request = ((("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: ")) + otherOrigin) + "\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.not(Matchers.isIn(fieldNames)));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.not(Matchers.isIn(fieldNames)));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithWildcardOrigin() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String origin = "http://foo.example.com";
        String request = ((("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: ")) + origin) + "\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), "Vary", Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithMatchingWildcardOrigin() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        String origin = "http://subdomain.example.com";
        filterHolder.setInitParameter(ALLOWED_ORIGINS_PARAM, "http://*.example.com");
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = ((("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: ")) + origin) + "\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), "Vary", Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithMatchingWildcardOriginAndMultipleSubdomains() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        String origin = "http://subdomain.subdomain.example.com";
        filterHolder.setInitParameter(ALLOWED_ORIGINS_PARAM, "http://*.example.com");
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = ((("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: ")) + origin) + "\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), "Vary", Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithMatchingOriginAndWithoutTimingOrigin() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        String origin = "http://localhost";
        filterHolder.setInitParameter(ALLOWED_ORIGINS_PARAM, origin);
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = ((("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: ")) + origin) + "\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), TIMING_ALLOW_ORIGIN_HEADER, Matchers.not(Matchers.isIn(fieldNames)));
        MatcherAssert.assertThat(response.toString(), "Vary", Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithMatchingOriginAndNonMatchingTimingOrigin() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        String origin = "http://localhost";
        String timingOrigin = "http://127.0.0.1";
        filterHolder.setInitParameter(ALLOWED_ORIGINS_PARAM, origin);
        filterHolder.setInitParameter(ALLOWED_TIMING_ORIGINS_PARAM, timingOrigin);
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = ((("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: ")) + origin) + "\r\n") + "\r\n";
        String response = tester.getResponses(request);
        Assertions.assertTrue(response.contains("HTTP/1.1 200"));
        Assertions.assertTrue(response.contains(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER));
        Assertions.assertTrue(response.contains(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER));
        Assertions.assertFalse(response.contains(TIMING_ALLOW_ORIGIN_HEADER));
        Assertions.assertTrue(response.contains("Vary"));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithMatchingOriginAndMatchingTimingOrigin() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        String origin = "http://localhost";
        filterHolder.setInitParameter(ALLOWED_ORIGINS_PARAM, origin);
        filterHolder.setInitParameter(ALLOWED_TIMING_ORIGINS_PARAM, origin);
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = ((("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: ")) + origin) + "\r\n") + "\r\n";
        String response = tester.getResponses(request);
        Assertions.assertTrue(response.contains("HTTP/1.1 200"));
        Assertions.assertTrue(response.contains(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER));
        Assertions.assertTrue(response.contains(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER));
        Assertions.assertTrue(response.contains(TIMING_ALLOW_ORIGIN_HEADER));
        Assertions.assertTrue(response.contains("Vary"));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithMatchingMultipleOrigins() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        String origin = "http://localhost";
        String otherOrigin = origin.replace("localhost", "127.0.0.1");
        filterHolder.setInitParameter(ALLOWED_ORIGINS_PARAM, ((origin + ",") + otherOrigin));
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = (((((("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + // Use 2 spaces as separator to test that the implementation does not fail
        "Origin: ")) + otherOrigin) + " ") + " ") + origin) + "\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), "Vary", Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithoutCredentials() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        filterHolder.setInitParameter(ALLOW_CREDENTIALS_PARAM, "false");
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = "" + (((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: http://localhost\r\n") + "\r\n");
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.not(Matchers.isIn(fieldNames)));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testNonSimpleRequestWithoutPreflight() throws Exception {
        // We cannot know if an actual request has performed the preflight before:
        // we'll trust browsers to do it right, so responses to actual requests
        // will contain the CORS response headers.
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = "" + (((("PUT / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: http://localhost\r\n") + "\r\n");
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testOptionsRequestButNotPreflight() throws Exception {
        // We cannot know if an actual request has performed the preflight before:
        // we'll trust browsers to do it right, so responses to actual requests
        // will contain the CORS response headers.
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = "" + (((("OPTIONS / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: http://localhost\r\n") + "\r\n");
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testPreflightWithWildcardCustomHeaders() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        filterHolder.setInitParameter(ALLOWED_HEADERS_PARAM, "*");
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = (((((("" + (("OPTIONS / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n")) + (ACCESS_CONTROL_REQUEST_HEADERS_HEADER)) + ": X-Foo-Bar\r\n") + (ACCESS_CONTROL_REQUEST_METHOD_HEADER)) + ": GET\r\n") + "Origin: http://localhost\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_HEADERS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testPUTRequestWithPreflight() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        filterHolder.setInitParameter(ALLOWED_METHODS_PARAM, "PUT");
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        // Preflight request
        String request = (((("" + (("OPTIONS / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n")) + (ACCESS_CONTROL_REQUEST_METHOD_HEADER)) + ": PUT\r\n") + "Origin: http://localhost\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_MAX_AGE_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_METHODS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_HEADERS_HEADER, Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
        // Preflight request was ok, now make the actual request
        request = "" + (((("PUT / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: http://localhost\r\n") + "\r\n");
        rawResponse = tester.getResponses(request);
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
    }

    @Test
    public void testDELETERequestWithPreflightAndAllowedCustomHeaders() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        filterHolder.setInitParameter(ALLOWED_METHODS_PARAM, "GET,HEAD,POST,PUT,DELETE");
        filterHolder.setInitParameter(ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,X-Custom");
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        // Preflight request
        String request = (((((("" + (("OPTIONS / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n")) + (ACCESS_CONTROL_REQUEST_METHOD_HEADER)) + ": DELETE\r\n") + (ACCESS_CONTROL_REQUEST_HEADERS_HEADER)) + ": origin,x-custom,x-requested-with\r\n") + "Origin: http://localhost\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_MAX_AGE_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_METHODS_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_HEADERS_HEADER, Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
        // Preflight request was ok, now make the actual request
        request = "" + (((((("DELETE / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "X-Custom: value\r\n") + "X-Requested-With: local\r\n") + "Origin: http://localhost\r\n") + "\r\n");
        rawResponse = tester.getResponses(request);
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.isIn(fieldNames));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.isIn(fieldNames));
    }

    @Test
    public void testDELETERequestWithPreflightAndNotAllowedCustomHeaders() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        filterHolder.setInitParameter(ALLOWED_METHODS_PARAM, "GET,HEAD,POST,PUT,DELETE");
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        // Preflight request
        String request = (((((("" + (("OPTIONS / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n")) + (ACCESS_CONTROL_REQUEST_METHOD_HEADER)) + ": DELETE\r\n") + (ACCESS_CONTROL_REQUEST_HEADERS_HEADER)) + ": origin,x-custom,x-requested-with\r\n") + "Origin: http://localhost\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.not(Matchers.isIn(fieldNames)));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.not(Matchers.isIn(fieldNames)));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
        // The preflight request failed because header X-Custom is not allowed, actual request not issued
    }

    @Test
    public void testCrossOriginFilterDisabledForWebSocketUpgrade() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = "" + ((((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: Upgrade\r\n") + "Upgrade: WebSocket\r\n") + "Origin: http://localhost\r\n") + "\r\n");
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Matchers.not(Matchers.isIn(fieldNames)));
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, Matchers.not(Matchers.isIn(fieldNames)));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSimpleRequestWithExposedHeaders() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        filterHolder.setInitParameter("exposedHeaders", "Content-Length");
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        String request = "" + (((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "Origin: http://localhost\r\n") + "\r\n");
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, Matchers.isIn(fieldNames));
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testChainPreflightRequest() throws Exception {
        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        filterHolder.setInitParameter(ALLOWED_METHODS_PARAM, "PUT");
        filterHolder.setInitParameter(CHAIN_PREFLIGHT_PARAM, "false");
        tester.getContext().addFilter(filterHolder, "/*", EnumSet.of(REQUEST));
        CountDownLatch latch = new CountDownLatch(1);
        tester.getContext().addServlet(new ServletHolder(new CrossOriginFilterTest.ResourceServlet(latch)), "/*");
        // Preflight request
        String request = (((("" + (("OPTIONS / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n")) + (ACCESS_CONTROL_REQUEST_METHOD_HEADER)) + ": PUT\r\n") + "Origin: http://localhost\r\n") + "\r\n";
        String rawResponse = tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        Set<String> fieldNames = response.getFieldNamesCollection();
        MatcherAssert.assertThat(response.toString(), ACCESS_CONTROL_ALLOW_METHODS_HEADER, Matchers.isIn(fieldNames));
        Assertions.assertFalse(latch.await(1, TimeUnit.SECONDS));
    }

    public static class ResourceServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        private final CountDownLatch latch;

        public ResourceServlet(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            latch.countDown();
        }
    }
}

