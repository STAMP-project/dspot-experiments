/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja;


import Result.SC_200_OK;
import Result.SC_304_NOT_MODIFIED;
import java.io.ByteArrayOutputStream;
import ninja.utils.HttpCacheToolkit;
import ninja.utils.MimeTypes;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AssetsControllerTest {
    @Mock
    MimeTypes mimeTypes;

    @Mock
    HttpCacheToolkit httpCacheToolkit;

    @Mock
    Context contextRenderable;

    @Captor
    ArgumentCaptor<Result> resultCaptor;

    @Mock
    ResponseStreams responseStreams;

    @Mock
    NinjaProperties ninjaProperties;

    AssetsController assetsController;

    @Test
    public void testServeStatic404() throws Exception {
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("notAvailable");
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        Result result = Results.ok();
        renderable.render(contextRenderable, result);
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        Assert.assertEquals(Results.notFound().getStatusCode(), resultCaptor.getValue().getStatusCode());
    }

    @Test
    public void testServeStaticSecurityClassesWithoutSlash() throws Exception {
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("ninja/Ninja.class");
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        Result result = Results.ok();
        renderable.render(contextRenderable, result);
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        Assert.assertEquals(Results.notFound().getStatusCode(), resultCaptor.getValue().getStatusCode());
    }

    @Test
    public void testServeStaticSecurityClassesAbsolute() throws Exception {
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/ninja/Ninja.class");
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        Result result = Results.ok();
        renderable.render(contextRenderable, result);
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        Assert.assertEquals(Results.notFound().getStatusCode(), resultCaptor.getValue().getStatusCode());
    }

    @Test
    public void testServeStaticSecurityNoRelativPathWorks() throws Exception {
        // This theoretically could work as robots.txt is there..
        // But it should
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/assets/../../conf/heroku.conf");
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        Result result = Results.ok();
        renderable.render(contextRenderable, result);
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        Assert.assertEquals(Results.notFound().getStatusCode(), resultCaptor.getValue().getStatusCode());
    }

    @Test
    public void testServeStaticDirectory() throws Exception {
        AssetsControllerHelper assetsControllerHelper = Mockito.mock(AssetsControllerHelper.class, Mockito.CALLS_REAL_METHODS);
        assetsController = new AssetsController(assetsControllerHelper, httpCacheToolkit, mimeTypes, ninjaProperties);
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/");
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        Result result = Results.ok();
        renderable.render(contextRenderable, result);
        Mockito.verify(assetsControllerHelper).isDirectoryURL(this.getClass().getResource("/assets/"));
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        Assert.assertEquals(Results.notFound().getStatusCode(), resultCaptor.getValue().getStatusCode());
    }

    @Test
    public void testServeStatic304NotModified() throws Exception {
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/assets/testasset.txt");
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        Result result = Results.ok();
        // manually set to not modified => asset controller should
        // only finalize, but not stream
        result.status(SC_304_NOT_MODIFIED);
        renderable.render(contextRenderable, result);
        // test streaming of resource:
        // => not modified:
        // check etag has been called
        Mockito.verify(httpCacheToolkit).addEtag(Mockito.eq(contextRenderable), Mockito.eq(result), Mockito.anyLong());
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        // make sure we get the correct result...
        Assert.assertEquals(SC_304_NOT_MODIFIED, resultCaptor.getValue().getStatusCode());
    }

    @Test
    public void testStaticDirectoryIsFileSystemInDevMode() throws Exception {
        // some more setup needed:
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        AssetsControllerHelper assetsControllerHelper = Mockito.mock(AssetsControllerHelper.class, Mockito.CALLS_REAL_METHODS);
        assetsController = new AssetsController(assetsControllerHelper, httpCacheToolkit, mimeTypes, ninjaProperties);
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/assets/testasset-not-existent.txt");
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        renderable.render(contextRenderable, Results.ok());
        Mockito.verify(assetsControllerHelper).normalizePathWithoutLeadingSlash("/assets/testasset-not-existent.txt", true);
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        Assert.assertEquals(404, resultCaptor.getValue().getStatusCode());
    }

    @Test
    public void testStaticDirectoryClassPathWhenFileNotInFileSystemInDevMode() throws Exception {
        // some more setup needed:
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        AssetsControllerHelper assetsControllerHelper = Mockito.mock(AssetsControllerHelper.class, Mockito.CALLS_REAL_METHODS);
        assetsController = new AssetsController(assetsControllerHelper, httpCacheToolkit, mimeTypes, ninjaProperties);
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/assets/testasset.txt");
        Mockito.when(contextRenderable.finalizeHeadersWithoutFlashAndSessionCookie(Mockito.any(Result.class))).thenReturn(responseStreams);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Mockito.when(responseStreams.getOutputStream()).thenReturn(byteArrayOutputStream);
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        renderable.render(contextRenderable, Results.ok());
        Mockito.verify(assetsControllerHelper).normalizePathWithoutLeadingSlash("/assets/testasset.txt", true);
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        Assert.assertEquals(200, resultCaptor.getValue().getStatusCode());
    }

    @Test
    public void testStaticDirectoryIsClassPathInProdMode() throws Exception {
        // some more setup needed:
        Mockito.when(ninjaProperties.isDev()).thenReturn(false);
        AssetsControllerHelper assetsControllerHelper = Mockito.mock(AssetsControllerHelper.class, Mockito.CALLS_REAL_METHODS);
        assetsController = new AssetsController(assetsControllerHelper, httpCacheToolkit, mimeTypes, ninjaProperties);
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/assets/testasset.txt");
        Mockito.when(contextRenderable.finalizeHeadersWithoutFlashAndSessionCookie(Mockito.any(Result.class))).thenReturn(responseStreams);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Mockito.when(responseStreams.getOutputStream()).thenReturn(byteArrayOutputStream);
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        renderable.render(contextRenderable, Results.ok());
        Mockito.verify(assetsControllerHelper).normalizePathWithoutLeadingSlash("/assets/testasset.txt", true);
    }

    @Test
    public void testServeStaticNormalOperationModifiedNoCaching() throws Exception {
        Result result = Results.ok();
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/assets/testasset.txt");
        Mockito.when(mimeTypes.getContentType(Mockito.eq(contextRenderable), Mockito.anyString())).thenReturn("mimetype");
        Mockito.when(contextRenderable.finalizeHeadersWithoutFlashAndSessionCookie(Mockito.eq(result))).thenReturn(responseStreams);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Mockito.when(responseStreams.getOutputStream()).thenReturn(byteArrayOutputStream);
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        renderable.render(contextRenderable, result);
        // test streaming of resource:
        // => not modified:
        // check etag has been called
        Mockito.verify(httpCacheToolkit).addEtag(Mockito.eq(contextRenderable), Mockito.eq(result), Mockito.anyLong());
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        // make sure we get the correct result...
        Assert.assertEquals(SC_200_OK, resultCaptor.getValue().getStatusCode());
        // we mocked this one:
        Assert.assertEquals("mimetype", result.getContentType());
        // make sure the content is okay...
        Assert.assertEquals("testasset", byteArrayOutputStream.toString());
    }

    @Test
    public void testServeStaticRobotsTxt() throws Exception {
        Result result = Results.ok();
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/robots.txt");
        Mockito.when(mimeTypes.getContentType(Mockito.eq(contextRenderable), Mockito.anyString())).thenReturn("mimetype");
        Mockito.when(contextRenderable.finalizeHeadersWithoutFlashAndSessionCookie(Mockito.eq(result))).thenReturn(responseStreams);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Mockito.when(responseStreams.getOutputStream()).thenReturn(byteArrayOutputStream);
        Result result2 = assetsController.serveStatic();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        renderable.render(contextRenderable, result);
        // test streaming of resource:
        // => not modified:
        // check etag has been called
        Mockito.verify(httpCacheToolkit).addEtag(Mockito.eq(contextRenderable), Mockito.eq(result), Mockito.anyLong());
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        // make sure we get the correct result...
        Assert.assertEquals(SC_200_OK, resultCaptor.getValue().getStatusCode());
        // we mocked this one:
        Assert.assertEquals("mimetype", result.getContentType());
        String content = byteArrayOutputStream.toString();
        Assert.assertThat(content, CoreMatchers.containsString("User-agent: *"));
        Assert.assertThat(content, CoreMatchers.containsString("Disallow: /"));
    }

    @Test
    public void testServeWebJars() throws Exception {
        AssetsControllerHelper assetsControllerHelper = Mockito.mock(AssetsControllerHelper.class, Mockito.CALLS_REAL_METHODS);
        assetsController = new AssetsController(assetsControllerHelper, httpCacheToolkit, mimeTypes, ninjaProperties);
        Result result = Results.ok();
        Mockito.when(contextRenderable.getRequestPath()).thenReturn("/webjar_asset.txt");
        Mockito.when(contextRenderable.finalizeHeadersWithoutFlashAndSessionCookie(Mockito.eq(result))).thenReturn(responseStreams);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Mockito.when(responseStreams.getOutputStream()).thenReturn(byteArrayOutputStream);
        Result result2 = assetsController.serveWebJars();
        Renderable renderable = ((Renderable) (result2.getRenderable()));
        renderable.render(contextRenderable, result);
        Mockito.verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        // make sure we get the correct result...
        Assert.assertEquals(SC_200_OK, resultCaptor.getValue().getStatusCode());
        Assert.assertEquals("webjar_asset", byteArrayOutputStream.toString());
        Mockito.verify(assetsControllerHelper).normalizePathWithoutLeadingSlash("/webjar_asset.txt", true);
    }
}

