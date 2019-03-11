/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.rest.filter;


import MediaType.APPLICATION_JSON_TYPE;
import MediaType.APPLICATION_XHTML_XML_TYPE;
import MediaType.TEXT_HTML_TYPE;
import MediaType.WILDCARD_TYPE;
import Response.Status.NOT_FOUND;
import Response.Status.OK;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;
import org.graylog2.web.IndexHtmlGenerator;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class WebAppNotFoundResponseFilterTest {
    private static final String CK_METHOD_GET = "GET";

    private static final String CK_METHOD_POST = "POST";

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private ContainerResponseContext responseContext;

    @Mock
    private IndexHtmlGenerator indexHtmlGenerator;

    private WebAppNotFoundResponseFilter filter;

    private MultivaluedHashMap<String, Object> responseHeaders;

    @Test
    public void filterDoesNotFilterApplicationJson() throws Exception {
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final List<MediaType> mediaTypes = Collections.singletonList(APPLICATION_JSON_TYPE);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("/web/search"));
        Mockito.when(requestContext.getMethod()).thenReturn(WebAppNotFoundResponseFilterTest.CK_METHOD_GET);
        Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
        Mockito.when(requestContext.getAcceptableMediaTypes()).thenReturn(mediaTypes);
        Mockito.when(responseContext.getStatusInfo()).thenReturn(NOT_FOUND);
        filter.filter(requestContext, responseContext);
        Mockito.verify(responseContext, Mockito.never()).setEntity("index.html", new Annotation[0], TEXT_HTML_TYPE);
    }

    @Test
    public void filterDoesFilterTextHtml() throws Exception {
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final List<MediaType> mediaTypes = Collections.singletonList(TEXT_HTML_TYPE);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("/web/search"));
        Mockito.when(requestContext.getMethod()).thenReturn(WebAppNotFoundResponseFilterTest.CK_METHOD_GET);
        Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
        Mockito.when(requestContext.getAcceptableMediaTypes()).thenReturn(mediaTypes);
        Mockito.when(responseContext.getStatusInfo()).thenReturn(NOT_FOUND);
        filter.filter(requestContext, responseContext);
        Mockito.verify(responseContext, Mockito.times(1)).setEntity("index.html", new Annotation[0], TEXT_HTML_TYPE);
    }

    @Test
    public void filterDoesFilterApplicationXhtml() throws Exception {
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final List<MediaType> mediaTypes = Collections.singletonList(APPLICATION_XHTML_XML_TYPE);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("/web/search"));
        Mockito.when(requestContext.getMethod()).thenReturn(WebAppNotFoundResponseFilterTest.CK_METHOD_GET);
        Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
        Mockito.when(requestContext.getAcceptableMediaTypes()).thenReturn(mediaTypes);
        Mockito.when(responseContext.getStatusInfo()).thenReturn(NOT_FOUND);
        filter.filter(requestContext, responseContext);
        Mockito.verify(responseContext, Mockito.times(1)).setEntity("index.html", new Annotation[0], TEXT_HTML_TYPE);
    }

    @Test
    public void filterDoesFilterCompatibleAcceptMimeTypes() throws Exception {
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final List<MediaType> mediaTypes = Collections.singletonList(WILDCARD_TYPE);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("/web/search"));
        Mockito.when(requestContext.getMethod()).thenReturn(WebAppNotFoundResponseFilterTest.CK_METHOD_GET);
        Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
        Mockito.when(requestContext.getAcceptableMediaTypes()).thenReturn(mediaTypes);
        Mockito.when(responseContext.getStatusInfo()).thenReturn(NOT_FOUND);
        filter.filter(requestContext, responseContext);
        Mockito.verify(responseContext, Mockito.times(1)).setEntity("index.html", new Annotation[0], TEXT_HTML_TYPE);
    }

    @Test
    public void filterDoesNotFilterRestApiPrefix() throws Exception {
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final List<MediaType> mediaTypes = Collections.singletonList(TEXT_HTML_TYPE);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("/api/search"));
        Mockito.when(requestContext.getMethod()).thenReturn(WebAppNotFoundResponseFilterTest.CK_METHOD_GET);
        Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
        Mockito.when(requestContext.getAcceptableMediaTypes()).thenReturn(mediaTypes);
        Mockito.when(responseContext.getStatusInfo()).thenReturn(NOT_FOUND);
        filter.filter(requestContext, responseContext);
        Mockito.verify(responseContext, Mockito.never()).setEntity("index.html", new Annotation[0], TEXT_HTML_TYPE);
    }

    @Test
    public void filterDoesNotFilterResponseStatusOk() throws Exception {
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final List<MediaType> mediaTypes = Collections.singletonList(TEXT_HTML_TYPE);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("/web/search"));
        Mockito.when(requestContext.getMethod()).thenReturn(WebAppNotFoundResponseFilterTest.CK_METHOD_GET);
        Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
        Mockito.when(requestContext.getAcceptableMediaTypes()).thenReturn(mediaTypes);
        Mockito.when(responseContext.getStatusInfo()).thenReturn(OK);
        filter.filter(requestContext, responseContext);
        Mockito.verify(responseContext, Mockito.never()).setEntity("index.html", new Annotation[0], TEXT_HTML_TYPE);
    }

    @Test
    public void filterAddsUserAgentResponseHeader() throws Exception {
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final List<MediaType> mediaTypes = Collections.singletonList(TEXT_HTML_TYPE);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("/web/search"));
        Mockito.when(requestContext.getMethod()).thenReturn(WebAppNotFoundResponseFilterTest.CK_METHOD_GET);
        Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
        Mockito.when(requestContext.getAcceptableMediaTypes()).thenReturn(mediaTypes);
        Mockito.when(responseContext.getStatusInfo()).thenReturn(NOT_FOUND);
        filter.filter(requestContext, responseContext);
        assertThat(responseHeaders).containsEntry("X-UA-Compatible", Collections.singletonList("IE=edge"));
    }

    @Test
    public void filterDoesNotFilterPostRequests() throws Exception {
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final List<MediaType> mediaTypes = Collections.singletonList(TEXT_HTML_TYPE);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("/web/nonexisting"));
        Mockito.when(requestContext.getMethod()).thenReturn(WebAppNotFoundResponseFilterTest.CK_METHOD_POST);
        Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
        Mockito.when(requestContext.getAcceptableMediaTypes()).thenReturn(mediaTypes);
        Mockito.when(responseContext.getStatusInfo()).thenReturn(NOT_FOUND);
        filter.filter(requestContext, responseContext);
        Mockito.verify(responseContext, Mockito.never()).setEntity("index.html", new Annotation[0], TEXT_HTML_TYPE);
    }
}

