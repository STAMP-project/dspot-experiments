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
package org.eclipse.jetty.servlet;


import DispatcherType.REQUEST;
import HttpHeader.ETAG;
import HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import HttpServletResponse.SC_NOT_FOUND;
import HttpStatus.FORBIDDEN_403;
import HttpStatus.INTERNAL_SERVER_ERROR_500;
import HttpStatus.MOVED_TEMPORARILY_302;
import HttpStatus.NOT_FOUND_404;
import HttpStatus.NOT_MODIFIED_304;
import HttpStatus.OK_200;
import HttpTester.Response;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Consumer;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.ResourceContentFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AllowSymLinkAliasChecker;
import org.eclipse.jetty.toolchain.test.FS;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;


@ExtendWith(WorkDirExtension.class)
public class DefaultServletTest {
    public WorkDir workDir;

    public Path docRoot;

    private Server server;

    private LocalConnector connector;

    private ServletContextHandler context;

    @Test
    public void testListingWithSession() throws Exception {
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/*");
        defholder.setInitParameter("dirAllowed", "true");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("gzip", "false");
        /* create some content in the docroot */
        FS.ensureDirExists(docRoot.resolve("one"));
        FS.ensureDirExists(docRoot.resolve("two"));
        FS.ensureDirExists(docRoot.resolve("three"));
        String rawResponse = connector.getResponse("GET /context/;JSESSIONID=1234567890 HTTP/1.0\n\n");
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        String body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("/one/;JSESSIONID=1234567890"));
        MatcherAssert.assertThat(body, Matchers.containsString("/two/;JSESSIONID=1234567890"));
        MatcherAssert.assertThat(body, Matchers.containsString("/three/;JSESSIONID=1234567890"));
        MatcherAssert.assertThat(body, Matchers.not(Matchers.containsString("<script>")));
    }

    @Test
    public void testListingXSS() throws Exception {
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/*");
        defholder.setInitParameter("dirAllowed", "true");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("gzip", "false");
        /* create some content in the docroot */
        FS.ensureDirExists(docRoot.resolve("one"));
        FS.ensureDirExists(docRoot.resolve("two"));
        FS.ensureDirExists(docRoot.resolve("three"));
        /* Intentionally bad request URI. Sending a non-encoded URI with typically
        encoded characters '<', '>', and '"'.
         */
        String req1 = "GET /context/;<script>window.alert(\"hi\");</script> HTTP/1.0\r\n" + "\r\n";
        String rawResponse = connector.getResponse(req1);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        String body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.not(Matchers.containsString("<script>")));
    }

    @Test
    public void testListingWithQuestionMarks() throws Exception {
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/*");
        defholder.setInitParameter("dirAllowed", "true");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("gzip", "false");
        /* create some content in the docroot */
        FS.ensureDirExists(docRoot.resolve("one"));
        FS.ensureDirExists(docRoot.resolve("two"));
        FS.ensureDirExists(docRoot.resolve("three"));
        // Creating dir 'f??r' (Might not work in Windows)
        DefaultServletTest.assumeMkDirSupported(docRoot, "f??r");
        String rawResponse = connector.getResponse("GET /context/ HTTP/1.0\r\n\r\n");
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        String body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("f??r"));
    }

    @Test
    public void testListingProperUrlEncoding() throws Exception {
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/*");
        defholder.setInitParameter("dirAllowed", "true");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("gzip", "false");
        /* create some content in the docroot */
        Path wackyDir = docRoot.resolve("dir;");// this should not be double-encoded.

        FS.ensureDirExists(wackyDir);
        FS.ensureDirExists(wackyDir.resolve("four"));
        FS.ensureDirExists(wackyDir.resolve("five"));
        FS.ensureDirExists(wackyDir.resolve("six"));
        /* At this point we have the following
        testListingProperUrlEncoding/
        `-- docroot
            `-- dir;
                |-- five
                |-- four
                `-- six
         */
        // First send request in improper, unencoded way.
        String rawResponse = connector.getResponse("GET /context/dir;/ HTTP/1.0\r\n\r\n");
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_FOUND_404));
        // Now send request in proper, encoded format.
        rawResponse = connector.getResponse("GET /context/dir%3B/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        String body = response.getContent();
        // Should not see double-encoded ";"
        // First encoding: ";" -> "%3b"
        // Second encoding: "%3B" -> "%253B" (BAD!)
        MatcherAssert.assertThat(body, Matchers.not(Matchers.containsString("%253B")));
        MatcherAssert.assertThat(body, Matchers.containsString("/dir%3B/"));
        MatcherAssert.assertThat(body, Matchers.containsString("/dir%3B/four/"));
        MatcherAssert.assertThat(body, Matchers.containsString("/dir%3B/five/"));
        MatcherAssert.assertThat(body, Matchers.containsString("/dir%3B/six/"));
    }

    @Test
    public void testWelcomeMultipleBasesBase() throws Exception {
        Path dir = docRoot.resolve("dir");
        FS.ensureDirExists(dir);
        Path inde = dir.resolve("index.htm");
        Path index = dir.resolve("index.html");
        Path altRoot = workDir.getPath().resolve("altroot");
        Path altDir = altRoot.resolve("dir");
        FS.ensureDirExists(altDir);
        Path altInde = altDir.resolve("index.htm");
        Path altIndex = altDir.resolve("index.html");
        ServletHolder altholder = context.addServlet(DefaultServlet.class, "/alt/*");
        altholder.setInitParameter("resourceBase", altRoot.toUri().toASCIIString());
        altholder.setInitParameter("pathInfoOnly", "true");
        altholder.setInitParameter("dirAllowed", "false");
        altholder.setInitParameter("redirectWelcome", "false");
        altholder.setInitParameter("welcomeServlets", "false");
        altholder.setInitParameter("gzip", "false");
        ServletHolder otherholder = context.addServlet(DefaultServlet.class, "/other/*");
        otherholder.setInitParameter("resourceBase", altRoot.toUri().toASCIIString());
        otherholder.setInitParameter("pathInfoOnly", "true");
        otherholder.setInitParameter("dirAllowed", "true");
        otherholder.setInitParameter("redirectWelcome", "false");
        otherholder.setInitParameter("welcomeServlets", "false");
        otherholder.setInitParameter("gzip", "false");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("welcomeServlets", "false");
        defholder.setInitParameter("gzip", "false");
        @SuppressWarnings("unused")
        ServletHolder jspholder = context.addServlet(NoJspServlet.class, "*.jsp");
        String rawResponse;
        HttpTester.Response response;
        // Test other redirect
        rawResponse = connector.getResponse("GET /context/other HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(MOVED_TEMPORARILY_302));
        MatcherAssert.assertThat(response, containsHeaderValue("Location", "http://0.0.0.0/context/other/"));
        // Test alt default
        rawResponse = connector.getResponse("GET /context/alt/dir/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(FORBIDDEN_403));
        createFile(altIndex, "<h1>Alt Index</h1>");
        rawResponse = connector.getResponse("GET /context/alt/dir/index.html HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Alt Index</h1>"));
        rawResponse = connector.getResponse("GET /context/alt/dir/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Alt Index</h1>"));
        createFile(altInde, "<h1>Alt Inde</h1>");
        rawResponse = connector.getResponse("GET /context/alt/dir/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Alt Index</h1>"));
        if (deleteFile(altIndex)) {
            rawResponse = connector.getResponse("GET /context/alt/dir/ HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Alt Inde</h1>"));
            if (deleteFile(altInde)) {
                rawResponse = connector.getResponse("GET /context/alt/dir/ HTTP/1.0\r\n\r\n");
                response = HttpTester.parseResponse(rawResponse);
                MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(FORBIDDEN_403));
            }
        }
        // Test normal default
        rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(FORBIDDEN_403));
        createFile(index, "<h1>Hello Index</h1>");
        rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Hello Index</h1>"));
        createFile(inde, "<h1>Hello Inde</h1>");
        rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Hello Index</h1>"));
        if (deleteFile(index)) {
            rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Hello Inde</h1>"));
            if (deleteFile(inde)) {
                rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
                response = HttpTester.parseResponse(rawResponse);
                MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(FORBIDDEN_403));
            }
        }
    }

    @Test
    public void testWelcomeRedirect() throws Exception {
        Path dir = docRoot.resolve("dir");
        FS.ensureDirExists(dir);
        Path inde = dir.resolve("index.htm");
        Path index = dir.resolve("index.html");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "true");
        defholder.setInitParameter("welcomeServlets", "false");
        defholder.setInitParameter("gzip", "false");
        defholder.setInitParameter("maxCacheSize", "1024000");
        defholder.setInitParameter("maxCachedFileSize", "512000");
        defholder.setInitParameter("maxCachedFiles", "100");
        @SuppressWarnings("unused")
        ServletHolder jspholder = context.addServlet(NoJspServlet.class, "*.jsp");
        String rawResponse;
        HttpTester.Response response;
        rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(FORBIDDEN_403));
        createFile(index, "<h1>Hello Index</h1>");
        rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(MOVED_TEMPORARILY_302));
        MatcherAssert.assertThat(response, containsHeaderValue("Location", "http://0.0.0.0/context/dir/index.html"));
        createFile(inde, "<h1>Hello Inde</h1>");
        rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(MOVED_TEMPORARILY_302));
        MatcherAssert.assertThat(response, containsHeaderValue("Location", "http://0.0.0.0/context/dir/index.html"));
        if (deleteFile(index)) {
            rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(MOVED_TEMPORARILY_302));
            MatcherAssert.assertThat(response, containsHeaderValue("Location", "http://0.0.0.0/context/dir/index.htm"));
            if (deleteFile(inde)) {
                rawResponse = connector.getResponse("GET /context/dir/ HTTP/1.0\r\n\r\n");
                response = HttpTester.parseResponse(rawResponse);
                MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(FORBIDDEN_403));
            }
        }
    }

    /**
     * Ensure that oddball directory names are served with proper escaping
     */
    @Test
    public void testWelcomeRedirect_DirWithQuestion() throws Exception {
        FS.ensureDirExists(docRoot);
        context.setBaseResource(new PathResource(docRoot));
        Path dir = DefaultServletTest.assumeMkDirSupported(docRoot, "dir?");
        Path index = dir.resolve("index.html");
        createFile(index, "<h1>Hello Index</h1>");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "true");
        defholder.setInitParameter("welcomeServlets", "false");
        defholder.setInitParameter("gzip", "false");
        String rawResponse;
        HttpTester.Response response;
        rawResponse = connector.getResponse("GET /context/dir%3F HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(MOVED_TEMPORARILY_302));
        MatcherAssert.assertThat(response, containsHeaderValue("Location", "http://0.0.0.0/context/dir%3F/"));
        rawResponse = connector.getResponse("GET /context/dir%3F/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(MOVED_TEMPORARILY_302));
        MatcherAssert.assertThat(response, containsHeaderValue("Location", "http://0.0.0.0/context/dir%3F/index.html"));
    }

    /**
     * Ensure that oddball directory names are served with proper escaping
     */
    @Test
    public void testWelcomeRedirect_DirWithSemicolon() throws Exception {
        FS.ensureDirExists(docRoot);
        context.setBaseResource(new PathResource(docRoot));
        Path dir = DefaultServletTest.assumeMkDirSupported(docRoot, "dir;");
        Path index = dir.resolve("index.html");
        createFile(index, "<h1>Hello Index</h1>");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "true");
        defholder.setInitParameter("welcomeServlets", "false");
        defholder.setInitParameter("gzip", "false");
        String rawResponse;
        HttpTester.Response response;
        rawResponse = connector.getResponse("GET /context/dir%3B HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(MOVED_TEMPORARILY_302));
        MatcherAssert.assertThat(response, containsHeaderValue("Location", "http://0.0.0.0/context/dir%3B/"));
        rawResponse = connector.getResponse("GET /context/dir%3B/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(MOVED_TEMPORARILY_302));
        MatcherAssert.assertThat(response, containsHeaderValue("Location", "http://0.0.0.0/context/dir%3B/index.html"));
    }

    @Test
    public void testWelcomeServlet() throws Exception {
        Path inde = docRoot.resolve("index.htm");
        Path index = docRoot.resolve("index.html");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("welcomeServlets", "true");
        defholder.setInitParameter("gzip", "false");
        @SuppressWarnings("unused")
        ServletHolder jspholder = context.addServlet(NoJspServlet.class, "*.jsp");
        String rawResponse;
        HttpTester.Response response;
        rawResponse = connector.getResponse("GET /context/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(INTERNAL_SERVER_ERROR_500));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("JSP support not configured"));
        createFile(index, "<h1>Hello Index</h1>");
        rawResponse = connector.getResponse("GET /context/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Hello Index</h1>"));
        createFile(inde, "<h1>Hello Inde</h1>");
        rawResponse = connector.getResponse("GET /context/ HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Hello Index</h1>"));
        if (deleteFile(index)) {
            rawResponse = connector.getResponse("GET /context/ HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Hello Inde</h1>"));
            if (deleteFile(inde)) {
                rawResponse = connector.getResponse("GET /context/ HTTP/1.0\r\n\r\n");
                response = HttpTester.parseResponse(rawResponse);
                MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(INTERNAL_SERVER_ERROR_500));
                MatcherAssert.assertThat(response.getContent(), Matchers.containsString("JSP support not configured"));
            }
        }
    }

    @Test
    public void testSymLinks() throws Exception {
        FS.ensureDirExists(docRoot);
        Path dir = docRoot.resolve("dir");
        Path dirLink = docRoot.resolve("dirlink");
        Path dirRLink = docRoot.resolve("dirrlink");
        FS.ensureDirExists(dir);
        Path foobar = dir.resolve("foobar.txt");
        Path link = dir.resolve("link.txt");
        Path rLink = dir.resolve("rlink.txt");
        createFile(foobar, "Foo Bar");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("gzip", "false");
        String rawResponse;
        HttpTester.Response response;
        rawResponse = connector.getResponse("GET /context/dir/foobar.txt HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("Foo Bar"));
        if (!(OS.WINDOWS.isCurrentOs())) {
            context.clearAliasChecks();
            Files.createSymbolicLink(dirLink, dir);
            Files.createSymbolicLink(dirRLink, new File("dir").toPath());
            Files.createSymbolicLink(link, foobar);
            Files.createSymbolicLink(rLink, new File("foobar.txt").toPath());
            rawResponse = connector.getResponse("GET /context/dir/link.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_FOUND_404));
            rawResponse = connector.getResponse("GET /context/dir/rlink.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_FOUND_404));
            rawResponse = connector.getResponse("GET /context/dirlink/foobar.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_FOUND_404));
            rawResponse = connector.getResponse("GET /context/dirrlink/foobar.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_FOUND_404));
            rawResponse = connector.getResponse("GET /context/dirlink/link.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_FOUND_404));
            rawResponse = connector.getResponse("GET /context/dirrlink/rlink.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_FOUND_404));
            context.addAliasCheck(new AllowSymLinkAliasChecker());
            rawResponse = connector.getResponse("GET /context/dir/link.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat(response.getContent(), Matchers.containsString("Foo Bar"));
            rawResponse = connector.getResponse("GET /context/dir/rlink.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat(response.getContent(), Matchers.containsString("Foo Bar"));
            rawResponse = connector.getResponse("GET /context/dirlink/foobar.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat(response.getContent(), Matchers.containsString("Foo Bar"));
            rawResponse = connector.getResponse("GET /context/dirrlink/foobar.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat(response.getContent(), Matchers.containsString("Foo Bar"));
            rawResponse = connector.getResponse("GET /context/dirlink/link.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat(response.getContent(), Matchers.containsString("Foo Bar"));
            rawResponse = connector.getResponse("GET /context/dirrlink/link.txt HTTP/1.0\r\n\r\n");
            response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat(response.getContent(), Matchers.containsString("Foo Bar"));
        }
    }

    @Test
    public void testDirectFromResourceHttpContent() throws Exception {
        FS.ensureDirExists(docRoot);
        context.setBaseResource(Resource.newResource(docRoot));
        Path index = docRoot.resolve("index.html");
        createFile(index, "<h1>Hello World</h1>");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "true");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("useFileMappedBuffer", "true");
        defholder.setInitParameter("welcomeServlets", "exact");
        defholder.setInitParameter("gzip", "false");
        defholder.setInitParameter("resourceCache", "resourceCache");
        String rawResponse;
        HttpTester.Response response;
        rawResponse = connector.getResponse("GET /context/index.html HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString("<h1>Hello World</h1>"));
        ResourceContentFactory factory = ((ResourceContentFactory) (context.getServletContext().getAttribute("resourceCache")));
        HttpContent content = factory.getContent("/index.html", 200);
        ByteBuffer buffer = content.getDirectBuffer();
        MatcherAssert.assertThat("Buffer is direct", buffer.isDirect(), Matchers.is(true));
        content = factory.getContent("/index.html", 5);
        buffer = content.getDirectBuffer();
        MatcherAssert.assertThat("Direct buffer", buffer, Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testFiltered() throws Exception {
        FS.ensureDirExists(docRoot);
        Path file0 = docRoot.resolve("data0.txt");
        createFile(file0, "Hello Text 0");
        Path image = docRoot.resolve("image.jpg");
        createFile(image, "not an image");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("welcomeServlets", "false");
        defholder.setInitParameter("gzip", "false");
        String rawResponse;
        HttpTester.Response response;
        String body;
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "12"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeaderValue(HttpHeader.CONTENT_TYPE, "charset=")));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.not(Matchers.containsString("Extra Info")));
        server.stop();
        context.addFilter(DefaultServletTest.OutputFilter.class, "/*", EnumSet.of(REQUEST));
        server.start();
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        body = response.getContent();
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, ("" + (body.length()))));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "charset="));
        MatcherAssert.assertThat(body, Matchers.containsString("Extra Info"));
        rawResponse = connector.getResponse("GET /context/image.jpg HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        body = response.getContent();
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, ("" + (body.length()))));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "image/jpeg"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "charset=utf-8"));
        MatcherAssert.assertThat(body, Matchers.containsString("Extra Info"));
        server.stop();
        context.getServletHandler().setFilterMappings(new FilterMapping[]{  });
        context.getServletHandler().setFilters(new FilterHolder[]{  });
        context.addFilter(DefaultServletTest.WriterFilter.class, "/*", EnumSet.of(REQUEST));
        server.start();
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        body = response.getContent();
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeaderValue(HttpHeader.CONTENT_TYPE, "charset=")));
        MatcherAssert.assertThat(body, Matchers.containsString("Extra Info"));
    }

    @Test
    public void testGzip() throws Exception {
        FS.ensureDirExists(docRoot);
        Path file0 = docRoot.resolve("data0.txt");
        createFile(file0, "Hello Text 0");
        Path file0gz = docRoot.resolve("data0.txt.gz");
        createFile(file0gz, "fake gzip");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("welcomeServlets", "false");
        defholder.setInitParameter("gzip", "true");
        defholder.setInitParameter("etags", "true");
        String rawResponse;
        HttpTester.Response response;
        String body;
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "12"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, containsHeader(ETAG));
        MatcherAssert.assertThat(response, Matchers.not(containsHeaderValue(HttpHeader.CONTENT_ENCODING, "gzip")));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("Hello Text 0"));
        String etag = response.get(ETAG);
        String etag_gzip = etag.replaceFirst("([^\"]*)\"(.*)\"", "$1\"$2--gzip\"");
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "9"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_ENCODING, "gzip"));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_gzip));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake gzip"));
        rawResponse = connector.getResponse("GET /context/data0.txt.gz HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "9"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "application/gzip"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.VARY)));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.CONTENT_ENCODING)));
        MatcherAssert.assertThat("Should not contain gzip variant", response, Matchers.not(containsHeaderValue(ETAG, etag_gzip)));
        MatcherAssert.assertThat("Should have a different ETag", response, containsHeader(ETAG));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake gzip"));
        rawResponse = connector.getResponse("GET /context/data0.txt.gz HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: W/\"wobble\"\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "9"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "application/gzip"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.VARY)));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.CONTENT_ENCODING)));
        MatcherAssert.assertThat("Should not contain gzip variant", response, Matchers.not(containsHeaderValue(ETAG, etag_gzip)));
        MatcherAssert.assertThat("Should have a different ETag", response, containsHeader(ETAG));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake gzip"));
        String bad_etag_gzip = etag.replaceFirst("([^\"]*)\"(.*)\"", "$1\"$2X--gzip\"");
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: " + bad_etag_gzip) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(Matchers.not(NOT_MODIFIED_304)));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: " + etag_gzip) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_gzip));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: " + etag) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: W/\"foobar\"," + etag_gzip) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_gzip));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: W/\"foobar\"," + etag) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag));
    }

    @Test
    public void testCachedGzip() throws Exception {
        FS.ensureDirExists(docRoot);
        Path file0 = docRoot.resolve("data0.txt");
        createFile(file0, "Hello Text 0");
        Path file0gz = docRoot.resolve("data0.txt.gz");
        createFile(file0gz, "fake gzip");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("welcomeServlets", "false");
        defholder.setInitParameter("gzip", "true");
        defholder.setInitParameter("etags", "true");
        defholder.setInitParameter("maxCachedFiles", "1024");
        defholder.setInitParameter("maxCachedFileSize", "200000000");
        defholder.setInitParameter("maxCacheSize", "256000000");
        String rawResponse;
        HttpTester.Response response;
        String body;
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "12"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.CONTENT_ENCODING)));
        MatcherAssert.assertThat(response, containsHeader(ETAG));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("Hello Text 0"));
        String etag = response.get(ETAG);
        String etag_gzip = etag.replaceFirst("([^\"]*)\"(.*)\"", "$1\"$2--gzip\"");
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "9"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_ENCODING, "gzip"));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_gzip));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake gzip"));
        rawResponse = connector.getResponse("GET /context/data0.txt.gz HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "9"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "application/gzip"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.VARY)));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.CONTENT_ENCODING)));
        MatcherAssert.assertThat("Should not contain gzip variant", response, Matchers.not(containsHeaderValue(ETAG, etag_gzip)));
        MatcherAssert.assertThat("Should have a different ETag", response, containsHeader(ETAG));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake gzip"));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: " + etag_gzip) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_gzip));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: " + etag) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: W/\"foobar\"," + etag_gzip) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_gzip));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: W/\"foobar\"," + etag) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag));
    }

    @Test
    public void testBrotli() throws Exception {
        createFile(docRoot.resolve("data0.txt"), "Hello Text 0");
        createFile(docRoot.resolve("data0.txt.br"), "fake brotli");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("welcomeServlets", "false");
        defholder.setInitParameter("precompressed", "true");
        defholder.setInitParameter("etags", "true");
        String rawResponse;
        HttpTester.Response response;
        String body;
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "12"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.CONTENT_ENCODING)));
        MatcherAssert.assertThat(response, containsHeader(ETAG));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("Hello Text 0"));
        String etag = response.get(ETAG);
        String etag_br = etag.replaceFirst("([^\"]*)\"(.*)\"", "$1\"$2--br\"");
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip;q=0.9,br\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "11"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_ENCODING, "br"));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_br));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake br"));
        rawResponse = connector.getResponse("GET /context/data0.txt.br HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br,gzip\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "11"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "application/brotli"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.VARY)));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.CONTENT_ENCODING)));
        MatcherAssert.assertThat("Should not contain br variant", response, Matchers.not(containsHeaderValue(ETAG, etag_br)));
        MatcherAssert.assertThat("Should have a different ETag", response, containsHeader(ETAG));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake br"));
        rawResponse = connector.getResponse("GET /context/data0.txt.br HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip\r\nIf-None-Match: W/\"wobble\"\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "11"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "application/brotli"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.VARY)));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.CONTENT_ENCODING)));
        MatcherAssert.assertThat("Should not contain br variant", response, Matchers.not(containsHeaderValue(ETAG, etag_br)));
        MatcherAssert.assertThat("Should have a different ETag", response, containsHeader(ETAG));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake br"));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\nIf-None-Match: " + etag_br) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_br));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\nIf-None-Match: " + etag) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\nIf-None-Match: W/\"foobar\"," + etag_br) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_br));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\nIf-None-Match: W/\"foobar\"," + etag) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag));
    }

    @Test
    public void testCachedBrotli() throws Exception {
        createFile(docRoot.resolve("data0.txt"), "Hello Text 0");
        createFile(docRoot.resolve("data0.txt.br"), "fake brotli");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("dirAllowed", "false");
        defholder.setInitParameter("redirectWelcome", "false");
        defholder.setInitParameter("welcomeServlets", "false");
        defholder.setInitParameter("precompressed", "true");
        defholder.setInitParameter("etags", "true");
        defholder.setInitParameter("maxCachedFiles", "1024");
        defholder.setInitParameter("maxCachedFileSize", "200000000");
        defholder.setInitParameter("maxCacheSize", "256000000");
        String rawResponse;
        HttpTester.Response response;
        String body;
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "12"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.CONTENT_ENCODING)));
        MatcherAssert.assertThat(response, containsHeader(ETAG));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("Hello Text 0"));
        String etag = response.get(ETAG);
        String etag_br = etag.replaceFirst("([^\"]*)\"(.*)\"", "$1\"$2--br\"");
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "11"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_ENCODING, "br"));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_br));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake brotli"));
        rawResponse = connector.getResponse("GET /context/data0.txt.br HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "11"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "application/brotli"));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.VARY)));
        MatcherAssert.assertThat(response, Matchers.not(containsHeader(HttpHeader.CONTENT_ENCODING)));
        MatcherAssert.assertThat("Should not contain br variant", response, Matchers.not(containsHeaderValue(ETAG, etag_br)));
        MatcherAssert.assertThat("Should have a different ETag", response, containsHeader(ETAG));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake brotli"));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\nIf-None-Match: " + etag_br) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_br));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\nIf-None-Match: " + etag) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\nIf-None-Match: W/\"foobar\"," + etag_br) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag_br));
        rawResponse = connector.getResponse((("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br\r\nIf-None-Match: W/\"foobar\"," + etag) + "\r\n\r\n"));
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(NOT_MODIFIED_304));
        MatcherAssert.assertThat(response, containsHeaderValue(ETAG, etag));
    }

    @Test
    public void testDefaultBrotliOverGzip() throws Exception {
        createFile(docRoot.resolve("data0.txt"), "Hello Text 0");
        createFile(docRoot.resolve("data0.txt.br"), "fake brotli");
        createFile(docRoot.resolve("data0.txt.gz"), "fake gzip");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("precompressed", "true");
        defholder.setInitParameter("resourceBase", docRoot.toString());
        String rawResponse;
        HttpTester.Response response;
        String body;
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip, compress, br\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "11"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_ENCODING, "br"));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake brotli"));
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:gzip, compress, br;q=0.9\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "9"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_ENCODING, "gzip"));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake gzip"));
    }

    @Test
    public void testCustomCompressionFormats() throws Exception {
        createFile(docRoot.resolve("data0.txt"), "Hello Text 0");
        createFile(docRoot.resolve("data0.txt.br"), "fake brotli");
        createFile(docRoot.resolve("data0.txt.gz"), "fake gzip");
        createFile(docRoot.resolve("data0.txt.bz2"), "fake bzip2");
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("precompressed", "bzip2=.bz2,gzip=.gz,br=.br");
        defholder.setInitParameter("resourceBase", docRoot.toString());
        String rawResponse;
        HttpTester.Response response;
        String body;
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:bzip2, br, gzip\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "10"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_ENCODING, "bzip2"));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake bzip2"));
        // TODO: show accept-encoding search order issue (shouldn't this request return data0.txt.br?)
        rawResponse = connector.getResponse("GET /context/data0.txt HTTP/1.0\r\nHost:localhost:8080\r\nAccept-Encoding:br, gzip\r\n\r\n");
        response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_LENGTH, "9"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_TYPE, "text/plain"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.VARY, "Accept-Encoding"));
        MatcherAssert.assertThat(response, containsHeaderValue(HttpHeader.CONTENT_ENCODING, "gzip"));
        body = response.getContent();
        MatcherAssert.assertThat(body, Matchers.containsString("fake gzip"));
    }

    @Test
    public void testControlCharacter() throws Exception {
        FS.ensureDirExists(docRoot);
        ServletHolder defholder = context.addServlet(DefaultServlet.class, "/");
        defholder.setInitParameter("resourceBase", docRoot.toFile().getAbsolutePath());
        String rawResponse = connector.getResponse("GET /context/%0a HTTP/1.1\r\nHost: local\r\nConnection: close\r\n\r\n");
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        System.out.println(((response + "\n") + (response.getContent())));
        MatcherAssert.assertThat("Response.status", response.getStatus(), Matchers.anyOf(Matchers.is(SC_NOT_FOUND), Matchers.is(SC_INTERNAL_SERVER_ERROR)));
        MatcherAssert.assertThat("Response.content", response.getContent(), Matchers.is(Matchers.not(Matchers.containsString(docRoot.toString()))));
    }

    public static class OutputFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            response.getOutputStream().println("Extra Info");
            response.setCharacterEncoding("utf-8");
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
        }
    }

    public static class WriterFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            response.getWriter().println("Extra Info");
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
        }
    }

    public static class Scenarios extends ArrayList<Arguments> {
        public void addScenario(String description, String rawRequest, int expectedStatus) {
            add(Arguments.of(new DefaultServletTest.Scenario(description, rawRequest, expectedStatus)));
        }

        public void addScenario(String description, String rawRequest, int expectedStatus, Consumer<HttpTester.Response> extraAsserts) {
            add(Arguments.of(new DefaultServletTest.Scenario(description, rawRequest, expectedStatus, extraAsserts)));
        }
    }

    public static class Scenario {
        private final String description;

        public final String rawRequest;

        public final int expectedStatus;

        public Consumer<HttpTester.Response> extraAsserts;

        public Scenario(String description, String rawRequest, int expectedStatus) {
            this.description = description;
            this.rawRequest = rawRequest;
            this.expectedStatus = expectedStatus;
        }

        public Scenario(String description, String rawRequest, int expectedStatus, Consumer<HttpTester.Response> extraAsserts) {
            this(description, rawRequest, expectedStatus);
            this.extraAsserts = extraAsserts;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}

