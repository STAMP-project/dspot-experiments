/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.http;


import HttpHeaders.ACCEPT_LANGUAGE;
import HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.CACHE_CONTROL;
import HttpHeaders.CONTENT_LANGUAGE;
import HttpHeaders.DATE;
import HttpHeaders.IF_MATCH;
import HttpHeaders.IF_MODIFIED_SINCE;
import HttpMethod.GET;
import HttpMethod.POST;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Locale.LanguageRange.parse;


/**
 * Unit tests for {@link org.springframework.http.HttpHeaders}.
 *
 * @author Arjen Poutsma
 * @author Sebastien Deleuze
 * @author Brian Clozel
 * @author Juergen Hoeller
 */
public class HttpHeadersTests {
    private final HttpHeaders headers = new HttpHeaders();

    @Test
    public void getFirst() {
        headers.add(CACHE_CONTROL, "max-age=1000, public");
        headers.add(CACHE_CONTROL, "s-maxage=1000");
        Assert.assertThat(headers.getFirst(CACHE_CONTROL), is("max-age=1000, public"));
    }

    @Test
    public void accept() {
        MediaType mediaType1 = new MediaType("text", "html");
        MediaType mediaType2 = new MediaType("text", "plain");
        List<MediaType> mediaTypes = new ArrayList<>(2);
        mediaTypes.add(mediaType1);
        mediaTypes.add(mediaType2);
        headers.setAccept(mediaTypes);
        Assert.assertEquals("Invalid Accept header", mediaTypes, headers.getAccept());
        Assert.assertEquals("Invalid Accept header", "text/html, text/plain", headers.getFirst("Accept"));
    }

    // SPR-9655
    @Test
    public void acceptWithMultipleHeaderValues() {
        headers.add("Accept", "text/html");
        headers.add("Accept", "text/plain");
        List<MediaType> expected = Arrays.asList(new MediaType("text", "html"), new MediaType("text", "plain"));
        Assert.assertEquals("Invalid Accept header", expected, headers.getAccept());
    }

    // SPR-14506
    @Test
    public void acceptWithMultipleCommaSeparatedHeaderValues() {
        headers.add("Accept", "text/html,text/pdf");
        headers.add("Accept", "text/plain,text/csv");
        List<MediaType> expected = Arrays.asList(new MediaType("text", "html"), new MediaType("text", "pdf"), new MediaType("text", "plain"), new MediaType("text", "csv"));
        Assert.assertEquals("Invalid Accept header", expected, headers.getAccept());
    }

    @Test
    public void acceptCharsets() {
        Charset charset1 = StandardCharsets.UTF_8;
        Charset charset2 = StandardCharsets.ISO_8859_1;
        List<Charset> charsets = new ArrayList<>(2);
        charsets.add(charset1);
        charsets.add(charset2);
        headers.setAcceptCharset(charsets);
        Assert.assertEquals("Invalid Accept header", charsets, headers.getAcceptCharset());
        Assert.assertEquals("Invalid Accept header", "utf-8, iso-8859-1", headers.getFirst("Accept-Charset"));
    }

    @Test
    public void acceptCharsetWildcard() {
        headers.set("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        Assert.assertEquals("Invalid Accept header", Arrays.asList(StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8), headers.getAcceptCharset());
    }

    @Test
    public void allow() {
        EnumSet<HttpMethod> methods = EnumSet.of(GET, POST);
        headers.setAllow(methods);
        Assert.assertEquals("Invalid Allow header", methods, headers.getAllow());
        Assert.assertEquals("Invalid Allow header", "GET,POST", headers.getFirst("Allow"));
    }

    @Test
    public void contentLength() {
        long length = 42L;
        headers.setContentLength(length);
        Assert.assertEquals("Invalid Content-Length header", length, headers.getContentLength());
        Assert.assertEquals("Invalid Content-Length header", "42", headers.getFirst("Content-Length"));
    }

    @Test
    public void contentType() {
        MediaType contentType = new MediaType("text", "html", StandardCharsets.UTF_8);
        headers.setContentType(contentType);
        Assert.assertEquals("Invalid Content-Type header", contentType, headers.getContentType());
        Assert.assertEquals("Invalid Content-Type header", "text/html;charset=UTF-8", headers.getFirst("Content-Type"));
    }

    @Test
    public void location() throws URISyntaxException {
        URI location = new URI("http://www.example.com/hotels");
        headers.setLocation(location);
        Assert.assertEquals("Invalid Location header", location, headers.getLocation());
        Assert.assertEquals("Invalid Location header", "http://www.example.com/hotels", headers.getFirst("Location"));
    }

    @Test
    public void eTag() {
        String eTag = "\"v2.6\"";
        headers.setETag(eTag);
        Assert.assertEquals("Invalid ETag header", eTag, headers.getETag());
        Assert.assertEquals("Invalid ETag header", "\"v2.6\"", headers.getFirst("ETag"));
    }

    @Test
    public void host() {
        InetSocketAddress host = InetSocketAddress.createUnresolved("localhost", 8080);
        headers.setHost(host);
        Assert.assertEquals("Invalid Host header", host, headers.getHost());
        Assert.assertEquals("Invalid Host header", "localhost:8080", headers.getFirst("Host"));
    }

    @Test
    public void hostNoPort() {
        InetSocketAddress host = InetSocketAddress.createUnresolved("localhost", 0);
        headers.setHost(host);
        Assert.assertEquals("Invalid Host header", host, headers.getHost());
        Assert.assertEquals("Invalid Host header", "localhost", headers.getFirst("Host"));
    }

    @Test
    public void ipv6Host() {
        InetSocketAddress host = InetSocketAddress.createUnresolved("[::1]", 0);
        headers.setHost(host);
        Assert.assertEquals("Invalid Host header", host, headers.getHost());
        Assert.assertEquals("Invalid Host header", "[::1]", headers.getFirst("Host"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalETag() {
        String eTag = "v2.6";
        headers.setETag(eTag);
        Assert.assertEquals("Invalid ETag header", eTag, headers.getETag());
        Assert.assertEquals("Invalid ETag header", "\"v2.6\"", headers.getFirst("ETag"));
    }

    @Test
    public void ifMatch() {
        String ifMatch = "\"v2.6\"";
        headers.setIfMatch(ifMatch);
        Assert.assertEquals("Invalid If-Match header", ifMatch, headers.getIfMatch().get(0));
        Assert.assertEquals("Invalid If-Match header", "\"v2.6\"", headers.getFirst("If-Match"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ifMatchIllegalHeader() {
        headers.setIfMatch("Illegal");
        headers.getIfMatch();
    }

    @Test
    public void ifMatchMultipleHeaders() {
        headers.add(IF_MATCH, "\"v2,0\"");
        headers.add(IF_MATCH, "W/\"v2,1\", \"v2,2\"");
        Assert.assertEquals("Invalid If-Match header", "\"v2,0\"", headers.get(IF_MATCH).get(0));
        Assert.assertEquals("Invalid If-Match header", "W/\"v2,1\", \"v2,2\"", headers.get(IF_MATCH).get(1));
        Assert.assertThat(headers.getIfMatch(), Matchers.contains("\"v2,0\"", "W/\"v2,1\"", "\"v2,2\""));
    }

    @Test
    public void ifNoneMatch() {
        String ifNoneMatch = "\"v2.6\"";
        headers.setIfNoneMatch(ifNoneMatch);
        Assert.assertEquals("Invalid If-None-Match header", ifNoneMatch, headers.getIfNoneMatch().get(0));
        Assert.assertEquals("Invalid If-None-Match header", "\"v2.6\"", headers.getFirst("If-None-Match"));
    }

    @Test
    public void ifNoneMatchWildCard() {
        String ifNoneMatch = "*";
        headers.setIfNoneMatch(ifNoneMatch);
        Assert.assertEquals("Invalid If-None-Match header", ifNoneMatch, headers.getIfNoneMatch().get(0));
        Assert.assertEquals("Invalid If-None-Match header", "*", headers.getFirst("If-None-Match"));
    }

    @Test
    public void ifNoneMatchList() {
        String ifNoneMatch1 = "\"v2.6\"";
        String ifNoneMatch2 = "\"v2.7\", \"v2.8\"";
        List<String> ifNoneMatchList = new ArrayList<>(2);
        ifNoneMatchList.add(ifNoneMatch1);
        ifNoneMatchList.add(ifNoneMatch2);
        headers.setIfNoneMatch(ifNoneMatchList);
        Assert.assertThat(headers.getIfNoneMatch(), Matchers.contains("\"v2.6\"", "\"v2.7\"", "\"v2.8\""));
        Assert.assertEquals("Invalid If-None-Match header", "\"v2.6\", \"v2.7\", \"v2.8\"", headers.getFirst("If-None-Match"));
    }

    @Test
    public void date() {
        Calendar calendar = new GregorianCalendar(2008, 11, 18, 11, 20);
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        long date = calendar.getTimeInMillis();
        headers.setDate(date);
        Assert.assertEquals("Invalid Date header", date, headers.getDate());
        Assert.assertEquals("Invalid Date header", "Thu, 18 Dec 2008 10:20:00 GMT", headers.getFirst("date"));
        // RFC 850
        headers.set("Date", "Thu, 18 Dec 2008 10:20:00 GMT");
        Assert.assertEquals("Invalid Date header", date, headers.getDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void dateInvalid() {
        headers.set("Date", "Foo Bar Baz");
        headers.getDate();
    }

    @Test
    public void dateOtherLocale() {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("nl", "nl"));
            Calendar calendar = new GregorianCalendar(2008, 11, 18, 11, 20);
            calendar.setTimeZone(TimeZone.getTimeZone("CET"));
            long date = calendar.getTimeInMillis();
            headers.setDate(date);
            Assert.assertEquals("Invalid Date header", "Thu, 18 Dec 2008 10:20:00 GMT", headers.getFirst("date"));
            Assert.assertEquals("Invalid Date header", date, headers.getDate());
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Test
    public void lastModified() {
        Calendar calendar = new GregorianCalendar(2008, 11, 18, 11, 20);
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        long date = calendar.getTimeInMillis();
        headers.setLastModified(date);
        Assert.assertEquals("Invalid Last-Modified header", date, headers.getLastModified());
        Assert.assertEquals("Invalid Last-Modified header", "Thu, 18 Dec 2008 10:20:00 GMT", headers.getFirst("last-modified"));
    }

    @Test
    public void expiresLong() {
        Calendar calendar = new GregorianCalendar(2008, 11, 18, 11, 20);
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        long date = calendar.getTimeInMillis();
        headers.setExpires(date);
        Assert.assertEquals("Invalid Expires header", date, headers.getExpires());
        Assert.assertEquals("Invalid Expires header", "Thu, 18 Dec 2008 10:20:00 GMT", headers.getFirst("expires"));
    }

    @Test
    public void expiresZonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2008, 12, 18, 10, 20, 0, 0, ZoneId.of("GMT"));
        headers.setExpires(zonedDateTime);
        Assert.assertEquals("Invalid Expires header", zonedDateTime.toInstant().toEpochMilli(), headers.getExpires());
        Assert.assertEquals("Invalid Expires header", "Thu, 18 Dec 2008 10:20:00 GMT", headers.getFirst("expires"));
    }

    // SPR-16560
    @Test(expected = DateTimeException.class)
    public void expiresLargeDate() {
        headers.setExpires(Long.MAX_VALUE);
    }

    // SPR-10648 (example is from INT-3063)
    @Test
    public void expiresInvalidDate() {
        headers.set("Expires", "-1");
        Assert.assertEquals((-1), headers.getExpires());
    }

    @Test
    public void ifModifiedSince() {
        Calendar calendar = new GregorianCalendar(2008, 11, 18, 11, 20);
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        long date = calendar.getTimeInMillis();
        headers.setIfModifiedSince(date);
        Assert.assertEquals("Invalid If-Modified-Since header", date, headers.getIfModifiedSince());
        Assert.assertEquals("Invalid If-Modified-Since header", "Thu, 18 Dec 2008 10:20:00 GMT", headers.getFirst("if-modified-since"));
    }

    // SPR-14144
    @Test
    public void invalidIfModifiedSinceHeader() {
        headers.set(IF_MODIFIED_SINCE, "0");
        Assert.assertEquals((-1), headers.getIfModifiedSince());
        headers.set(IF_MODIFIED_SINCE, "-1");
        Assert.assertEquals((-1), headers.getIfModifiedSince());
        headers.set(IF_MODIFIED_SINCE, "XXX");
        Assert.assertEquals((-1), headers.getIfModifiedSince());
    }

    @Test
    public void pragma() {
        String pragma = "no-cache";
        headers.setPragma(pragma);
        Assert.assertEquals("Invalid Pragma header", pragma, headers.getPragma());
        Assert.assertEquals("Invalid Pragma header", "no-cache", headers.getFirst("pragma"));
    }

    @Test
    public void cacheControl() {
        headers.setCacheControl("no-cache");
        Assert.assertEquals("Invalid Cache-Control header", "no-cache", headers.getCacheControl());
        Assert.assertEquals("Invalid Cache-Control header", "no-cache", headers.getFirst("cache-control"));
    }

    @Test
    public void cacheControlBuilder() {
        headers.setCacheControl(CacheControl.noCache());
        Assert.assertEquals("Invalid Cache-Control header", "no-cache", headers.getCacheControl());
        Assert.assertEquals("Invalid Cache-Control header", "no-cache", headers.getFirst("cache-control"));
    }

    @Test
    public void cacheControlEmpty() {
        headers.setCacheControl(CacheControl.empty());
        Assert.assertNull("Invalid Cache-Control header", headers.getCacheControl());
        Assert.assertNull("Invalid Cache-Control header", headers.getFirst("cache-control"));
    }

    @Test
    public void cacheControlAllValues() {
        headers.add(CACHE_CONTROL, "max-age=1000, public");
        headers.add(CACHE_CONTROL, "s-maxage=1000");
        Assert.assertEquals("max-age=1000, public, s-maxage=1000", headers.getCacheControl());
    }

    @Test
    public void contentDisposition() {
        ContentDisposition disposition = headers.getContentDisposition();
        Assert.assertNotNull(disposition);
        Assert.assertEquals("Invalid Content-Disposition header", ContentDisposition.empty(), headers.getContentDisposition());
        disposition = ContentDisposition.builder("attachment").name("foo").filename("foo.txt").size(123L).build();
        headers.setContentDisposition(disposition);
        Assert.assertEquals("Invalid Content-Disposition header", disposition, headers.getContentDisposition());
    }

    // SPR-11917
    @Test
    public void getAllowEmptySet() {
        headers.setAllow(Collections.emptySet());
        Assert.assertThat(headers.getAllow(), Matchers.emptyCollectionOf(HttpMethod.class));
    }

    @Test
    public void accessControlAllowCredentials() {
        Assert.assertFalse(headers.getAccessControlAllowCredentials());
        headers.setAccessControlAllowCredentials(false);
        Assert.assertFalse(headers.getAccessControlAllowCredentials());
        headers.setAccessControlAllowCredentials(true);
        Assert.assertTrue(headers.getAccessControlAllowCredentials());
    }

    @Test
    public void accessControlAllowHeaders() {
        List<String> allowedHeaders = headers.getAccessControlAllowHeaders();
        Assert.assertThat(allowedHeaders, Matchers.emptyCollectionOf(String.class));
        headers.setAccessControlAllowHeaders(Arrays.asList("header1", "header2"));
        allowedHeaders = headers.getAccessControlAllowHeaders();
        Assert.assertEquals(allowedHeaders, Arrays.asList("header1", "header2"));
    }

    @Test
    public void accessControlAllowHeadersMultipleValues() {
        List<String> allowedHeaders = headers.getAccessControlAllowHeaders();
        Assert.assertThat(allowedHeaders, Matchers.emptyCollectionOf(String.class));
        headers.add(ACCESS_CONTROL_ALLOW_HEADERS, "header1, header2");
        headers.add(ACCESS_CONTROL_ALLOW_HEADERS, "header3");
        allowedHeaders = headers.getAccessControlAllowHeaders();
        Assert.assertEquals(Arrays.asList("header1", "header2", "header3"), allowedHeaders);
    }

    @Test
    public void accessControlAllowMethods() {
        List<HttpMethod> allowedMethods = headers.getAccessControlAllowMethods();
        Assert.assertThat(allowedMethods, Matchers.emptyCollectionOf(HttpMethod.class));
        headers.setAccessControlAllowMethods(Arrays.asList(GET, POST));
        allowedMethods = headers.getAccessControlAllowMethods();
        Assert.assertEquals(allowedMethods, Arrays.asList(GET, POST));
    }

    @Test
    public void accessControlAllowOrigin() {
        Assert.assertNull(headers.getAccessControlAllowOrigin());
        headers.setAccessControlAllowOrigin("*");
        Assert.assertEquals("*", headers.getAccessControlAllowOrigin());
    }

    @Test
    public void accessControlExposeHeaders() {
        List<String> exposedHeaders = headers.getAccessControlExposeHeaders();
        Assert.assertThat(exposedHeaders, Matchers.emptyCollectionOf(String.class));
        headers.setAccessControlExposeHeaders(Arrays.asList("header1", "header2"));
        exposedHeaders = headers.getAccessControlExposeHeaders();
        Assert.assertEquals(exposedHeaders, Arrays.asList("header1", "header2"));
    }

    @Test
    public void accessControlMaxAge() {
        Assert.assertEquals((-1), headers.getAccessControlMaxAge());
        headers.setAccessControlMaxAge(3600);
        Assert.assertEquals(3600, headers.getAccessControlMaxAge());
    }

    @Test
    public void accessControlRequestHeaders() {
        List<String> requestHeaders = headers.getAccessControlRequestHeaders();
        Assert.assertThat(requestHeaders, Matchers.emptyCollectionOf(String.class));
        headers.setAccessControlRequestHeaders(Arrays.asList("header1", "header2"));
        requestHeaders = headers.getAccessControlRequestHeaders();
        Assert.assertEquals(requestHeaders, Arrays.asList("header1", "header2"));
    }

    @Test
    public void accessControlRequestMethod() {
        Assert.assertNull(headers.getAccessControlRequestMethod());
        headers.setAccessControlRequestMethod(POST);
        Assert.assertEquals(POST, headers.getAccessControlRequestMethod());
    }

    @Test
    public void acceptLanguage() {
        String headerValue = "fr-ch, fr;q=0.9, en-*;q=0.8, de;q=0.7, *;q=0.5";
        headers.setAcceptLanguage(parse(headerValue));
        Assert.assertEquals(headerValue, headers.getFirst(ACCEPT_LANGUAGE));
        List<Locale.LanguageRange> expectedRanges = Arrays.asList(new Locale.LanguageRange("fr-ch"), new Locale.LanguageRange("fr", 0.9), new Locale.LanguageRange("en-*", 0.8), new Locale.LanguageRange("de", 0.7), new Locale.LanguageRange("*", 0.5));
        Assert.assertEquals(expectedRanges, headers.getAcceptLanguage());
        Assert.assertEquals(Locale.forLanguageTag("fr-ch"), headers.getAcceptLanguageAsLocales().get(0));
        headers.setAcceptLanguageAsLocales(Collections.singletonList(Locale.FRANCE));
        Assert.assertEquals(Locale.FRANCE, headers.getAcceptLanguageAsLocales().get(0));
    }

    // SPR-15603
    @Test
    public void acceptLanguageWithEmptyValue() throws Exception {
        this.headers.set(ACCEPT_LANGUAGE, "");
        Assert.assertEquals(Collections.emptyList(), this.headers.getAcceptLanguageAsLocales());
    }

    @Test
    public void contentLanguage() {
        headers.setContentLanguage(Locale.FRANCE);
        Assert.assertEquals(Locale.FRANCE, headers.getContentLanguage());
        Assert.assertEquals("fr-FR", headers.getFirst(CONTENT_LANGUAGE));
    }

    @Test
    public void contentLanguageSerialized() {
        headers.set(CONTENT_LANGUAGE, "de, en_CA");
        Assert.assertEquals("Expected one (first) locale", Locale.GERMAN, headers.getContentLanguage());
    }

    @Test
    public void firstDate() {
        headers.setDate(DATE, 1229595600000L);
        Assert.assertThat(headers.getFirstDate(DATE), is(1229595600000L));
        headers.clear();
        headers.add(DATE, "Thu, 18 Dec 2008 10:20:00 GMT");
        headers.add(DATE, "Sat, 18 Dec 2010 10:20:00 GMT");
        Assert.assertThat(headers.getFirstDate(DATE), is(1229595600000L));
    }

    @Test
    public void firstZonedDateTime() {
        ZonedDateTime date = ZonedDateTime.of(2017, 6, 22, 22, 22, 0, 0, ZoneId.of("GMT"));
        headers.setZonedDateTime(DATE, date);
        Assert.assertThat(headers.getFirst(DATE), is("Thu, 22 Jun 2017 22:22:00 GMT"));
        Assert.assertTrue(headers.getFirstZonedDateTime(DATE).isEqual(date));
        headers.clear();
        ZonedDateTime otherDate = ZonedDateTime.of(2010, 12, 18, 10, 20, 0, 0, ZoneId.of("GMT"));
        headers.add(DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(date));
        headers.add(DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(otherDate));
        Assert.assertTrue(headers.getFirstZonedDateTime(DATE).isEqual(date));
        // obsolete RFC 850 format
        headers.clear();
        headers.set(DATE, "Thursday, 22-Jun-17 22:22:00 GMT");
        Assert.assertTrue(headers.getFirstZonedDateTime(DATE).isEqual(date));
        // ANSI C's asctime() format
        headers.clear();
        headers.set(DATE, "Thu Jun 22 22:22:00 2017");
        Assert.assertTrue(headers.getFirstZonedDateTime(DATE).isEqual(date));
    }

    @Test
    public void basicAuth() {
        String username = "foo";
        String password = "bar";
        headers.setBasicAuth(username, password);
        String authorization = headers.getFirst(AUTHORIZATION);
        Assert.assertNotNull(authorization);
        Assert.assertTrue(authorization.startsWith("Basic "));
        byte[] result = Base64.getDecoder().decode(authorization.substring(6).getBytes(StandardCharsets.ISO_8859_1));
        Assert.assertEquals("foo:bar", new String(result, StandardCharsets.ISO_8859_1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void basicAuthIllegalChar() {
        String username = "foo";
        String password = "\u03bb";
        headers.setBasicAuth(username, password);
    }

    @Test
    public void bearerAuth() {
        String token = "foo";
        headers.setBearerAuth(token);
        String authorization = headers.getFirst(AUTHORIZATION);
        Assert.assertEquals("Bearer foo", authorization);
    }
}

