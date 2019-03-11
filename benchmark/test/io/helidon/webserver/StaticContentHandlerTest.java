/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.webserver;


import Http.Header.ETAG;
import Http.Header.IF_MATCH;
import Http.Header.IF_NONE_MATCH;
import Http.Header.LOCATION;
import Http.Method.GET;
import Http.Method.POST;
import Http.Status.MOVED_PERMANENTLY_301;
import Http.Status.NOT_MODIFIED_304;
import Http.Status.PRECONDITION_FAILED_412;
import MediaType.TEXT_HTML;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.http.Http;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests {@link StaticContentHandler}.
 */
public class StaticContentHandlerTest {
    @Test
    public void etag_InNonMatch_NotAccept() throws Exception {
        RequestHeaders req = Mockito.mock(RequestHeaders.class);
        Mockito.when(req.values(IF_NONE_MATCH)).thenReturn(CollectionsHelper.listOf("\"ccc\"", "\"ddd\""));
        Mockito.when(req.values(IF_MATCH)).thenReturn(Collections.emptyList());
        ResponseHeaders res = Mockito.mock(ResponseHeaders.class);
        StaticContentHandler.processEtag("aaa", req, res);
        Mockito.verify(res).put(ETAG, "\"aaa\"");
    }

    @Test
    public void etag_InNonMatch_Accept() throws Exception {
        RequestHeaders req = Mockito.mock(RequestHeaders.class);
        Mockito.when(req.values(IF_NONE_MATCH)).thenReturn(CollectionsHelper.listOf("\"ccc\"", "W/\"aaa\""));
        Mockito.when(req.values(IF_MATCH)).thenReturn(Collections.emptyList());
        ResponseHeaders res = Mockito.mock(ResponseHeaders.class);
        StaticContentHandlerTest.assertHttpException(() -> StaticContentHandler.processEtag("aaa", req, res), NOT_MODIFIED_304);
        Mockito.verify(res).put(ETAG, "\"aaa\"");
    }

    @Test
    public void etag_InMatch_NotAccept() throws Exception {
        RequestHeaders req = Mockito.mock(RequestHeaders.class);
        Mockito.when(req.values(IF_MATCH)).thenReturn(CollectionsHelper.listOf("\"ccc\"", "\"ddd\""));
        Mockito.when(req.values(IF_NONE_MATCH)).thenReturn(Collections.emptyList());
        ResponseHeaders res = Mockito.mock(ResponseHeaders.class);
        StaticContentHandlerTest.assertHttpException(() -> StaticContentHandler.processEtag("aaa", req, res), PRECONDITION_FAILED_412);
        Mockito.verify(res).put(ETAG, "\"aaa\"");
    }

    @Test
    public void etag_InMatch_Accept() throws Exception {
        RequestHeaders req = Mockito.mock(RequestHeaders.class);
        Mockito.when(req.values(IF_MATCH)).thenReturn(CollectionsHelper.listOf("\"ccc\"", "\"aaa\""));
        Mockito.when(req.values(IF_NONE_MATCH)).thenReturn(Collections.emptyList());
        ResponseHeaders res = Mockito.mock(ResponseHeaders.class);
        StaticContentHandler.processEtag("aaa", req, res);
        Mockito.verify(res).put(ETAG, "\"aaa\"");
    }

    @Test
    public void ifModifySince_Accept() throws Exception {
        ZonedDateTime modified = ZonedDateTime.now();
        RequestHeaders req = Mockito.mock(RequestHeaders.class);
        Mockito.doReturn(Optional.of(modified.minusSeconds(60))).when(req).ifModifiedSince();
        Mockito.doReturn(Optional.empty()).when(req).ifUnmodifiedSince();
        ResponseHeaders res = Mockito.mock(ResponseHeaders.class);
        StaticContentHandler.processModifyHeaders(modified.toInstant(), req, res);
    }

    @Test
    public void ifModifySince_NotAccept() throws Exception {
        ZonedDateTime modified = ZonedDateTime.now();
        RequestHeaders req = Mockito.mock(RequestHeaders.class);
        Mockito.doReturn(Optional.of(modified)).when(req).ifModifiedSince();
        Mockito.doReturn(Optional.empty()).when(req).ifUnmodifiedSince();
        ResponseHeaders res = Mockito.mock(ResponseHeaders.class);
        StaticContentHandlerTest.assertHttpException(() -> StaticContentHandler.processModifyHeaders(modified.toInstant(), req, res), NOT_MODIFIED_304);
    }

    @Test
    public void ifUnmodifySince_Accept() throws Exception {
        ZonedDateTime modified = ZonedDateTime.now();
        RequestHeaders req = Mockito.mock(RequestHeaders.class);
        Mockito.doReturn(Optional.of(modified)).when(req).ifUnmodifiedSince();
        Mockito.doReturn(Optional.empty()).when(req).ifModifiedSince();
        ResponseHeaders res = Mockito.mock(ResponseHeaders.class);
        StaticContentHandler.processModifyHeaders(modified.toInstant(), req, res);
    }

    @Test
    public void ifUnmodifySince_NotAccept() throws Exception {
        ZonedDateTime modified = ZonedDateTime.now();
        RequestHeaders req = Mockito.mock(RequestHeaders.class);
        Mockito.doReturn(Optional.of(modified.minusSeconds(60))).when(req).ifUnmodifiedSince();
        Mockito.doReturn(Optional.empty()).when(req).ifModifiedSince();
        ResponseHeaders res = Mockito.mock(ResponseHeaders.class);
        StaticContentHandlerTest.assertHttpException(() -> StaticContentHandler.processModifyHeaders(modified.toInstant(), req, res), PRECONDITION_FAILED_412);
    }

    @Test
    public void redirect() throws Exception {
        ResponseHeaders resh = Mockito.mock(ResponseHeaders.class);
        ServerResponse res = Mockito.mock(ServerResponse.class);
        Mockito.doReturn(resh).when(res).headers();
        StaticContentHandler.redirect(res, "/foo/");
        Mockito.verify(res).status(MOVED_PERMANENTLY_301);
        Mockito.verify(resh).put(LOCATION, "/foo/");
        Mockito.verify(res).send();
    }

    @Test
    public void processContentType() throws Exception {
        ContentTypeSelector selector = Mockito.mock(ContentTypeSelector.class);
        Mockito.when(selector.determine(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(TEXT_HTML);
        StaticContentHandlerTest.TestContentHandler handler = new StaticContentHandlerTest.TestContentHandler(null, selector, Paths.get("/root"), false);
        RequestHeaders req = Mockito.mock(RequestHeaders.class);
        ResponseHeaders res = Mockito.mock(ResponseHeaders.class);
        handler.processContentType(Paths.get("/root/index.html"), req, res);
        Mockito.verify(res).contentType(TEXT_HTML);
    }

    @Test
    public void handleRoot() throws Exception {
        ServerRequest request = mockRequestWithPath("/");
        ServerResponse response = Mockito.mock(ServerResponse.class);
        StaticContentHandlerTest.TestContentHandler handler = new StaticContentHandlerTest.TestContentHandler("/root", true);
        handler.handle(GET, request, response);
        Mockito.verify(request, Mockito.never()).next();
        MatcherAssert.assertThat(handler.path, CoreMatchers.is(Paths.get("/root")));
    }

    @Test
    public void handleIllegalMethod() throws Exception {
        ServerRequest request = mockRequestWithPath("/");
        ServerResponse response = Mockito.mock(ServerResponse.class);
        StaticContentHandlerTest.TestContentHandler handler = new StaticContentHandlerTest.TestContentHandler("/root", true);
        handler.handle(POST, request, response);
        Mockito.verify(request).next();
        MatcherAssert.assertThat(handler.counter.get(), CoreMatchers.is(0));
    }

    @Test
    public void handleValid() throws Exception {
        ServerRequest request = mockRequestWithPath("/foo/some.txt");
        ServerResponse response = Mockito.mock(ServerResponse.class);
        StaticContentHandlerTest.TestContentHandler handler = new StaticContentHandlerTest.TestContentHandler("/root", true);
        handler.handle(GET, request, response);
        Mockito.verify(request, Mockito.never()).next();
        MatcherAssert.assertThat(handler.path, CoreMatchers.is(Paths.get("/root/foo/some.txt")));
    }

    @Test
    public void handleOutside() throws Exception {
        ServerRequest request = mockRequestWithPath("/../foo/some.txt");
        ServerResponse response = Mockito.mock(ServerResponse.class);
        StaticContentHandlerTest.TestContentHandler handler = new StaticContentHandlerTest.TestContentHandler("/root", true);
        handler.handle(GET, request, response);
        Mockito.verify(request).next();
        MatcherAssert.assertThat(handler.counter.get(), CoreMatchers.is(0));
    }

    @Test
    public void handleNextOnFalse() throws Exception {
        ServerRequest request = mockRequestWithPath("/");
        ServerResponse response = Mockito.mock(ServerResponse.class);
        StaticContentHandlerTest.TestContentHandler handler = new StaticContentHandlerTest.TestContentHandler("/root", false);
        handler.handle(GET, request, response);
        Mockito.verify(request).next();
        MatcherAssert.assertThat(handler.counter.get(), CoreMatchers.is(1));
    }

    static class TestContentHandler extends StaticContentHandler {
        final AtomicInteger counter = new AtomicInteger(0);

        final boolean returnValue;

        Path path;

        TestContentHandler(String welcomeFilename, ContentTypeSelector contentTypeSelector, Path root, boolean returnValue) {
            super(welcomeFilename, contentTypeSelector, root);
            this.returnValue = returnValue;
        }

        TestContentHandler(String path, boolean returnValue) {
            this(null, Mockito.mock(ContentTypeSelector.class), Paths.get(path), returnValue);
        }

        @Override
        boolean doHandle(Http.RequestMethod method, Path path, ServerRequest request, ServerResponse response) throws IOException {
            this.counter.incrementAndGet();
            this.path = path;
            return returnValue;
        }
    }
}

