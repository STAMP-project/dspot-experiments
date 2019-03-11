/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.file;


import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.linecorp.armeria.internal.PathAndQuery;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.logging.LoggingService;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class HttpFileServiceTest {
    private static final ZoneId UTC = ZoneId.of("UTC");

    private static final Pattern ETAG_PATTERN = Pattern.compile("^\"[^\"]+\"$");

    private static final String baseResourceDir = (HttpFileServiceTest.class.getPackage().getName().replace('.', '/')) + '/';

    private static final File tmpDir;

    private static final Server server;

    private static int httpPort;

    static {
        try {
            tmpDir = Files.createTempDirectory("armeria-test.").toFile();
        } catch (Exception e) {
            throw new Error(e);
        }
        final ServerBuilder sb = new ServerBuilder();
        try {
            sb.serviceUnder("/cached/fs/", HttpFileServiceBuilder.forFileSystem(HttpFileServiceTest.tmpDir.toPath()).autoIndex(true).build());
            sb.serviceUnder("/uncached/fs/", HttpFileServiceBuilder.forFileSystem(HttpFileServiceTest.tmpDir.toPath()).maxCacheEntries(0).autoIndex(true).build());
            sb.serviceUnder("/cached/compressed/", HttpFileServiceBuilder.forClassPath(((HttpFileServiceTest.baseResourceDir) + "foo")).serveCompressedFiles(true).build());
            sb.serviceUnder("/uncached/compressed/", HttpFileServiceBuilder.forClassPath(((HttpFileServiceTest.baseResourceDir) + "foo")).serveCompressedFiles(true).maxCacheEntries(0).build());
            sb.serviceUnder("/cached/classes/", HttpFileService.forClassPath("/"));
            sb.serviceUnder("/uncached/classes/", HttpFileServiceBuilder.forClassPath("/").maxCacheEntries(0).build());
            sb.serviceUnder("/cached/", HttpFileService.forClassPath(((HttpFileServiceTest.baseResourceDir) + "foo")).orElse(HttpFileService.forClassPath(((HttpFileServiceTest.baseResourceDir) + "bar"))));
            sb.serviceUnder("/uncached/", HttpFileServiceBuilder.forClassPath(((HttpFileServiceTest.baseResourceDir) + "foo")).maxCacheEntries(0).build().orElse(HttpFileServiceBuilder.forClassPath(((HttpFileServiceTest.baseResourceDir) + "bar")).maxCacheEntries(0).build()));
            sb.decorator(LoggingService.newDecorator());
        } catch (Exception e) {
            throw new Error(e);
        }
        server = sb.build();
    }

    private final boolean cached;

    public HttpFileServiceTest(boolean cached) {
        this.cached = cached;
    }

    @Test
    public void testClassPathGet() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final String lastModified;
            final String etag;
            try (CloseableHttpResponse res = hc.execute(new HttpGet(newUri("/foo.txt")))) {
                HttpFileServiceTest.assert200Ok(res, "text/plain", "foo");
                lastModified = HttpFileServiceTest.header(res, HttpHeaders.LAST_MODIFIED);
                etag = HttpFileServiceTest.header(res, HttpHeaders.ETAG);
            }
            assert304NotModified(hc, "/foo.txt", etag, lastModified);
            // Confirm file service paths are cached when cache is enabled.
            if (cached) {
                assertThat(PathAndQuery.cachedPaths()).contains("/cached/foo.txt");
            }
        }
    }

    @Test
    public void testClassPathGetUtf8() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(newUri("/%C2%A2.txt")))) {
                HttpFileServiceTest.assert200Ok(res, "text/plain", "?");
            }
        }
    }

    @Test
    public void testClassPathGetFromModule() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            // Read a class from a JDK module (java.base).
            try (CloseableHttpResponse res = hc.execute(new HttpGet(newUri("/classes/java/lang/Object.class")))) {
                HttpFileServiceTest.assert200Ok(res, null, ( content) -> assertThat(content).isNotEmpty());
            }
        }
    }

    @Test
    public void testClassPathGetFromJar() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            // Read a class from a third-party library JAR.
            try (CloseableHttpResponse res = hc.execute(new HttpGet(newUri("/classes/io/netty/util/NetUtil.class")))) {
                HttpFileServiceTest.assert200Ok(res, null, ( content) -> assertThat(content).isNotEmpty());
            }
        }
    }

    @Test
    public void testClassPathOrElseGet() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal();CloseableHttpResponse res = hc.execute(new HttpGet(newUri("/bar.txt")))) {
            HttpFileServiceTest.assert200Ok(res, "text/plain", "bar");
        }
    }

    @Test
    public void testIndexHtml() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(newUri("/")))) {
                HttpFileServiceTest.assert200Ok(res, "text/html", "<html><body></body></html>");
            }
        }
    }

    @Test
    public void testAutoIndex() throws Exception {
        final File rootDir = new File(HttpFileServiceTest.tmpDir, "auto_index");
        final File childFile = new File(rootDir, "child_file");
        final File childDir = new File(rootDir, "child_dir");
        final File grandchildFile = new File(childDir, "grandchild_file");
        final File emptyChildDir = new File(rootDir, "empty_child_dir");
        final File childDirWithCustomIndex = new File(rootDir, "child_dir_with_custom_index");
        final File customIndexFile = new File(childDirWithCustomIndex, "index.html");
        childDir.mkdirs();
        emptyChildDir.mkdirs();
        childDirWithCustomIndex.mkdirs();
        Files.write(childFile.toPath(), "child_file".getBytes(StandardCharsets.UTF_8));
        Files.write(grandchildFile.toPath(), "grandchild_file".getBytes(StandardCharsets.UTF_8));
        Files.write(customIndexFile.toPath(), "custom_index_file".getBytes(StandardCharsets.UTF_8));
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            // Ensure auto-redirect works as expected.
            HttpUriRequest req = new HttpGet(newUri("/fs/auto_index"));
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assertStatusLine(res, "HTTP/1.1 307 Temporary Redirect");
                assertThat(HttpFileServiceTest.header(res, "location")).isEqualTo(newPath("/fs/auto_index/"));
            }
            // Ensure directory listing works as expected.
            req = new HttpGet(newUri("/fs/auto_index/"));
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assertStatusLine(res, "HTTP/1.1 200 OK");
                final String content = HttpFileServiceTest.contentString(res);
                assertThat(content).contains(("Directory listing: " + (newPath("/fs/auto_index/")))).contains("4 file(s) total").contains("<a href=\"../\">../</a>").contains("<a href=\"child_dir/\">child_dir/</a>").contains("<a href=\"child_file\">child_file</a>").contains("<a href=\"child_dir_with_custom_index/\">child_dir_with_custom_index/</a>").contains("<a href=\"empty_child_dir/\">empty_child_dir/</a>");
            }
            // Ensure directory listing on an empty directory works as expected.
            req = new HttpGet(newUri("/fs/auto_index/empty_child_dir/"));
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assertStatusLine(res, "HTTP/1.1 200 OK");
                final String content = HttpFileServiceTest.contentString(res);
                assertThat(content).contains(("Directory listing: " + (newPath("/fs/auto_index/empty_child_dir/")))).contains("0 file(s) total").contains("<a href=\"../\">../</a>");
            }
            // Ensure custom index.html takes precedence over auto-generated directory listing.
            req = new HttpGet(newUri("/fs/auto_index/child_dir_with_custom_index/"));
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assertStatusLine(res, "HTTP/1.1 200 OK");
                assertThat(HttpFileServiceTest.contentString(res)).isEqualTo("custom_index_file");
            }
        }
    }

    @Test
    public void testUnknownMediaType() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal();CloseableHttpResponse res = hc.execute(new HttpGet(newUri("/bar.unknown")))) {
            HttpFileServiceTest.assert200Ok(res, null, "Unknown Media Type");
            final String lastModified = HttpFileServiceTest.header(res, HttpHeaders.LAST_MODIFIED);
            final String etag = HttpFileServiceTest.header(res, HttpHeaders.ETAG);
            assert304NotModified(hc, "/bar.unknown", etag, lastModified);
        }
    }

    @Test
    public void testGetPreCompressedSupportsNone() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpGet request = new HttpGet(newUri("/compressed/foo.txt"));
            try (CloseableHttpResponse res = hc.execute(request)) {
                assertThat(res.getFirstHeader("Content-Encoding")).isNull();
                assertThat(HttpFileServiceTest.headerOrNull(res, "Content-Type")).isEqualTo("text/plain; charset=utf-8");
                final byte[] content = HttpFileServiceTest.content(res);
                assertThat(new String(content, StandardCharsets.UTF_8)).isEqualTo("foo");
                // Confirm path not cached when cache disabled.
                assertThat(PathAndQuery.cachedPaths()).doesNotContain("/compressed/foo.txt");
            }
        }
    }

    @Test
    public void testGetPreCompressedSupportsGzip() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpGet request = new HttpGet(newUri("/compressed/foo.txt"));
            request.setHeader("Accept-Encoding", "gzip");
            try (CloseableHttpResponse res = hc.execute(request)) {
                assertThat(HttpFileServiceTest.headerOrNull(res, "Content-Encoding")).isEqualTo("gzip");
                assertThat(HttpFileServiceTest.headerOrNull(res, "Content-Type")).isEqualTo("text/plain; charset=utf-8");
                final byte[] content;
                try (GZIPInputStream unzipper = new GZIPInputStream(res.getEntity().getContent())) {
                    content = ByteStreams.toByteArray(unzipper);
                }
                assertThat(new String(content, StandardCharsets.UTF_8)).isEqualTo("foo");
            }
        }
    }

    @Test
    public void testGetPreCompressedSupportsBrotli() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpGet request = new HttpGet(newUri("/compressed/foo.txt"));
            request.setHeader("Accept-Encoding", "br");
            try (CloseableHttpResponse res = hc.execute(request)) {
                assertThat(HttpFileServiceTest.headerOrNull(res, "Content-Encoding")).isEqualTo("br");
                assertThat(HttpFileServiceTest.headerOrNull(res, "Content-Type")).isEqualTo("text/plain; charset=utf-8");
                // Test would be more readable and fun by decompressing like the gzip one, but since JDK doesn't
                // support brotli yet, just compare the compressed content to avoid adding a complex dependency.
                final byte[] content = HttpFileServiceTest.content(res);
                assertThat(content).containsExactly(Resources.toByteArray(Resources.getResource(((HttpFileServiceTest.baseResourceDir) + "foo/foo.txt.br"))));
            }
        }
    }

    @Test
    public void testGetPreCompressedSupportsBothPrefersBrotli() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpGet request = new HttpGet(newUri("/compressed/foo.txt"));
            request.setHeader("Accept-Encoding", "gzip, br");
            try (CloseableHttpResponse res = hc.execute(request)) {
                assertThat(HttpFileServiceTest.headerOrNull(res, "Content-Encoding")).isEqualTo("br");
                assertThat(HttpFileServiceTest.headerOrNull(res, "Content-Type")).isEqualTo("text/plain; charset=utf-8");
                // Test would be more readable and fun by decompressing like the gzip one, but since JDK doesn't
                // support brotli yet, just compare the compressed content to avoid adding a complex dependency.
                final byte[] content = HttpFileServiceTest.content(res);
                assertThat(content).containsExactly(Resources.toByteArray(Resources.getResource(((HttpFileServiceTest.baseResourceDir) + "foo/foo.txt.br"))));
            }
        }
    }

    @Test
    public void testFileSystemGet() throws Exception {
        final File barFile = new File(HttpFileServiceTest.tmpDir, "bar.html");
        final String expectedContentA = "<html/>";
        final String expectedContentB = "<html><body/></html>";
        Files.write(barFile.toPath(), expectedContentA.getBytes(StandardCharsets.UTF_8));
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final String lastModified;
            final String etag;
            HttpUriRequest req = new HttpGet(newUri("/fs/bar.html"));
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assert200Ok(res, "text/html", expectedContentA);
                lastModified = HttpFileServiceTest.header(res, HttpHeaders.LAST_MODIFIED);
                etag = HttpFileServiceTest.header(res, HttpHeaders.ETAG);
            }
            assert304NotModified(hc, "/fs/bar.html", etag, lastModified);
            // Test if the 'If-Modified-Since' header works as expected after the file is modified.
            req = new HttpGet(newUri("/fs/bar.html"));
            final Instant now = Instant.now();
            req.setHeader(HttpHeaders.IF_MODIFIED_SINCE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(now, HttpFileServiceTest.UTC)));
            // HTTP-date has no sub-second precision; just add a few seconds to the time.
            Files.write(barFile.toPath(), expectedContentB.getBytes(StandardCharsets.UTF_8));
            assertThat(barFile.setLastModified(((now.toEpochMilli()) + 5000))).isTrue();
            final String newLastModified;
            final String newETag;
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assert200Ok(res, "text/html", expectedContentB);
                newLastModified = HttpFileServiceTest.header(res, HttpHeaders.LAST_MODIFIED);
                newETag = HttpFileServiceTest.header(res, HttpHeaders.ETAG);
                // Ensure that both 'Last-Modified' and 'ETag' changed.
                assertThat(newLastModified).isNotEqualTo(lastModified);
                assertThat(newETag).isNotEqualTo(etag);
            }
            // Test if the 'If-None-Match' header works as expected after the file is modified.
            req = new HttpGet(newUri("/fs/bar.html"));
            req.setHeader(HttpHeaders.IF_NONE_MATCH, etag);
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assert200Ok(res, "text/html", expectedContentB);
                // Ensure that both 'Last-Modified' and 'ETag' changed.
                assertThat(HttpFileServiceTest.header(res, HttpHeaders.LAST_MODIFIED)).isEqualTo(newLastModified);
                assertThat(HttpFileServiceTest.header(res, HttpHeaders.ETAG)).isEqualTo(newETag);
            }
            // Test if the cache detects the file removal correctly.
            final boolean deleted = barFile.delete();
            assertThat(deleted).isTrue();
            req = new HttpGet(newUri("/fs/bar.html"));
            req.setHeader(HttpHeaders.IF_MODIFIED_SINCE, HttpFileServiceTest.currentHttpDate());
            req.setHeader(HttpHeaders.CONNECTION, "close");
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assert404NotFound(res);
            }
        }
    }

    @Test
    public void testFileSystemGet_modifiedFile() throws Exception {
        final File barFile = new File(HttpFileServiceTest.tmpDir, "modifiedFile.html");
        final String expectedContentA = "<html/>";
        final String expectedContentB = "<html><body/></html>";
        Files.write(barFile.toPath(), expectedContentA.getBytes(StandardCharsets.UTF_8));
        final long barFileLastModified = barFile.lastModified();
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpUriRequest req = new HttpGet(newUri("/fs/modifiedFile.html"));
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assert200Ok(res, "text/html", expectedContentA);
            }
            // Modify the file cached by the service. Update last modification time explicitly
            // so that it differs from the old value.
            Files.write(barFile.toPath(), expectedContentB.getBytes(StandardCharsets.UTF_8));
            assertThat(barFile.setLastModified((barFileLastModified + 5000))).isTrue();
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assert200Ok(res, "text/html", expectedContentB);
            }
        }
    }

    @Test
    public void testFileSystemGet_newFile() throws Exception {
        final File barFile = new File(HttpFileServiceTest.tmpDir, "newFile.html");
        final String expectedContentA = "<html/>";
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpUriRequest req = new HttpGet(newUri("/fs/newFile.html"));
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assert404NotFound(res);
            }
            Files.write(barFile.toPath(), expectedContentA.getBytes(StandardCharsets.UTF_8));
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assert200Ok(res, "text/html", expectedContentA);
            }
        } finally {
            barFile.delete();
        }
    }

    @Test
    public void testFileSystemGetUtf8() throws Exception {
        final File barFile = new File(HttpFileServiceTest.tmpDir, "?.txt");
        final String expectedContentA = "?";
        Files.write(barFile.toPath(), expectedContentA.getBytes(StandardCharsets.UTF_8));
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpUriRequest req = new HttpGet(newUri("/fs/%C2%A2.txt"));
            try (CloseableHttpResponse res = hc.execute(req)) {
                HttpFileServiceTest.assert200Ok(res, "text/plain", expectedContentA);
            }
        }
    }
}

