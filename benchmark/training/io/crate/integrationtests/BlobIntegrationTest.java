package io.crate.integrationtests;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.crate.blob.v2.BlobShard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Test;


@ClusterScope(scope = Scope.SUITE, numDataNodes = 2)
public class BlobIntegrationTest extends BlobHttpIntegrationTest {
    @Test
    public void testUploadInvalidSha1() throws IOException {
        CloseableHttpResponse response = put("test/d937ea65641c23fadc83616309e5b0e11acc5806", "asdf");
        assertThat(response.getStatusLine().getStatusCode(), Matchers.is(400));
    }

    @Test
    public void testCorsHeadersAreSet() throws Exception {
        String digest = uploadTinyBlob();
        CloseableHttpResponse response = get(blobUri(digest));
        assertThat(response.containsHeader("Access-Control-Allow-Origin"), Matchers.is(true));
    }

    @Test
    public void testNonExistingFile() throws IOException {
        CloseableHttpResponse response = get("test/d937ea65641c23fadc83616309e5b0e11acc5806");
        assertThat(response.getStatusLine().getStatusCode(), Matchers.is(404));
    }

    @Test
    public void testErrorResponseResetsBlobHandlerStateCorrectly() throws IOException {
        String digest = uploadTinyBlob();
        CloseableHttpResponse response;
        response = get(blobUri("0000000000000000000000000000000000000000"));
        assertThat(response.getStatusLine().getStatusCode(), Matchers.is(404));
        response = get(blobUri(digest));
        assertThat(response.getStatusLine().getStatusCode(), Matchers.is(200));
    }

    @Test
    public void testUploadValidFile() throws IOException {
        String digest = "c520e6109835c876fd98636efec43dd61634b7d3";
        CloseableHttpResponse response = put(blobUri(digest), StringUtils.repeat("a", 1500));
        assertThat(response.getStatusLine().getStatusCode(), Matchers.is(201));
        /* Note that the content length is specified in the response in order to
        let keep alive clients know that they don't have to wait for data
        after the put and may close the connection if appropriate
         */
        assertThat(response.getFirstHeader("Content-Length").getValue(), Matchers.is("0"));
    }

    @Test
    public void testUploadChunkedWithConflict() throws IOException {
        String digest = uploadBigBlob();
        CloseableHttpResponse conflictRes = put(blobUri(digest), StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", (1024 * 600)));
        assertThat(conflictRes.getStatusLine().getStatusCode(), Matchers.is(409));
    }

    @Test
    public void testUploadToUnknownBlobTable() throws IOException {
        String digest = "c520e6109835c876fd98636efec43dd61634b7d3";
        CloseableHttpResponse response = put(blobUri("test_no_blobs", digest), StringUtils.repeat("a", 1500));
        assertThat(response.getStatusLine().getStatusCode(), Matchers.is(404));
    }

    @Test
    public void testGetFiles() throws IOException {
        String digest = uploadBigBlob();
        CloseableHttpResponse res = get(blobUri(digest));
        assertThat(res.getEntity().getContentLength(), Matchers.is(15974400L));
    }

    @Test
    public void testHeadRequest() throws IOException {
        String digest = uploadSmallBlob();
        CloseableHttpResponse res = head(blobUri(digest));
        assertThat(res.getFirstHeader("Content-Length").getValue(), Matchers.is("1500"));
        assertThat(res.getFirstHeader("Accept-Ranges").getValue(), Matchers.is("bytes"));
        assertThat(res.getFirstHeader("Expires").getValue(), Matchers.is("Thu, 31 Dec 2037 23:59:59 GMT"));
        assertThat(res.getFirstHeader("Cache-Control").getValue(), Matchers.is("max-age=315360000"));
    }

    @Test
    public void testNodeWhichHasTheBlobDoesntRedirect() throws IOException {
        // One of the head requests must be redirected:
        String digest = uploadSmallBlob();
        int numberOfRedirects1 = getNumberOfRedirects(blobUri(digest), dataNode1);
        assertThat(numberOfRedirects1, Matchers.greaterThanOrEqualTo(0));
        int numberOfRedirects2 = getNumberOfRedirects(blobUri(digest), dataNode2);
        assertThat(numberOfRedirects2, Matchers.greaterThanOrEqualTo(0));
        assertThat("The node where the blob resides should not issue a redirect", numberOfRedirects1, Matchers.is(Matchers.not(numberOfRedirects2)));
    }

    @Test
    public void testDeleteFile() throws IOException {
        String digest = uploadSmallBlob();
        String uri = blobUri(digest);
        CloseableHttpResponse res = delete(uri);
        assertThat(res.getStatusLine().getStatusCode(), Matchers.is(204));
        res = get(uri);
        assertThat(res.getStatusLine().getStatusCode(), Matchers.is(404));
    }

    @Test
    public void testByteRange() throws IOException {
        String digest = uploadTinyBlob();
        Header[] headers = new Header[]{ new BasicHeader("Range", "bytes=8-") };
        CloseableHttpResponse res = get(blobUri(digest), headers);
        assertThat(res.getFirstHeader("Content-Length").getValue(), Matchers.is("18"));
        assertThat(res.getFirstHeader("Content-Range").getValue(), Matchers.is("bytes 8-25/26"));
        assertThat(res.getFirstHeader("Accept-Ranges").getValue(), Matchers.is("bytes"));
        assertThat(res.getFirstHeader("Expires").getValue(), Matchers.is("Thu, 31 Dec 2037 23:59:59 GMT"));
        assertThat(res.getFirstHeader("Cache-Control").getValue(), Matchers.is("max-age=315360000"));
        assertThat(EntityUtils.toString(res.getEntity()), Matchers.is("ijklmnopqrstuvwxyz"));
        res = get(blobUri(digest), new Header[]{ new BasicHeader("Range", "bytes=0-1") });
        assertThat(EntityUtils.toString(res.getEntity()), Matchers.is("ab"));
        res = get(blobUri(digest), new Header[]{ new BasicHeader("Range", "bytes=25-") });
        assertThat(EntityUtils.toString(res.getEntity()), Matchers.is("z"));
    }

    @Test
    public void testInvalidByteRange() throws IOException {
        String digest = uploadTinyBlob();
        Header[] headers = new Header[]{ new BasicHeader("Range", "bytes=40-58") };
        CloseableHttpResponse res = get(blobUri(digest), headers);
        assertThat(res.getStatusLine().getStatusCode(), Matchers.is(416));
        assertThat(res.getStatusLine().getReasonPhrase(), Matchers.is("Requested Range Not Satisfiable"));
        assertThat(res.getFirstHeader("Content-Length").getValue(), Matchers.is("0"));
    }

    @Test
    public void testParallelAccess() throws Throwable {
        String digest = uploadBigBlob();
        String expectedContent = StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", (1024 * 600));
        Header[][] headers = new Header[40][];
        String[] uris = new String[40];
        String[] expected = new String[40];
        for (int i = 0; i < 40; i++) {
            headers[i] = new Header[]{  };
            uris[i] = blobUri(digest);
            expected[i] = expectedContent;
        }
        assertThat(mget(uris, headers, expected), Matchers.is(true));
    }

    @Test
    public void testParallelAccessWithRange() throws Throwable {
        String digest = uploadBigBlob();
        String expectedContent = StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", (1024 * 600));
        Header[][] headers = new Header[][]{ new Header[]{ new BasicHeader("Range", "bytes=0-") }, new Header[]{ new BasicHeader("Range", "bytes=10-100") }, new Header[]{ new BasicHeader("Range", "bytes=20-30") }, new Header[]{ new BasicHeader("Range", "bytes=40-50") }, new Header[]{ new BasicHeader("Range", "bytes=40-80") }, new Header[]{ new BasicHeader("Range", "bytes=10-80") }, new Header[]{ new BasicHeader("Range", "bytes=5-30") }, new Header[]{ new BasicHeader("Range", "bytes=15-3000") }, new Header[]{ new BasicHeader("Range", "bytes=2000-10800") }, new Header[]{ new BasicHeader("Range", "bytes=1500-20000") } };
        String[] expected = new String[]{ expectedContent, expectedContent.substring(10, 101), expectedContent.substring(20, 31), expectedContent.substring(40, 51), expectedContent.substring(40, 81), expectedContent.substring(10, 81), expectedContent.substring(5, 31), expectedContent.substring(15, 3001), expectedContent.substring(2000, 10801), expectedContent.substring(1500, 20001) };
        String[] uris = new String[10];
        for (int i = 0; i < 10; i++) {
            uris[i] = blobUri(digest);
        }
        assertThat(mget(uris, headers, expected), Matchers.is(true));
    }

    @Test
    public void testHeadRequestConnectionIsNotClosed() throws Exception {
        Socket socket = new Socket(randomNode.getAddress(), randomNode.getPort());
        socket.setKeepAlive(true);
        socket.setSoTimeout(3000);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("HEAD /_blobs/invalid/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa HTTP/1.1\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write("Host: localhost\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        int linesRead = 0;
        while (linesRead < 3) {
            String line = reader.readLine();
            System.out.println(line);
            linesRead++;
        } 
        assertSocketIsConnected(socket);
        outputStream.write("HEAD /_blobs/invalid/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa HTTP/1.1\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write("Host: localhost\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        int read = reader.read();
        assertThat(read, Matchers.greaterThan((-1)));
        assertSocketIsConnected(socket);
    }

    @Test
    public void testResponseContainsCloseHeaderOnHttp10() throws Exception {
        Socket socket = new Socket(randomNode.getAddress(), randomNode.getPort());
        socket.setKeepAlive(false);
        socket.setSoTimeout(3000);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("HEAD /_blobs/invalid/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa HTTP/1.0\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write("Host: localhost\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        String line;
        List<String> lines = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        } 
        assertThat(lines, Matchers.hasItem("connection: close"));
    }

    @Test
    public void testEmptyFile() throws IOException {
        CloseableHttpResponse res = put(blobUri("da39a3ee5e6b4b0d3255bfef95601890afd80709"), "");
        assertThat(res.getStatusLine().getStatusCode(), Matchers.is(201));
        assertThat(res.getStatusLine().getReasonPhrase(), Matchers.is("Created"));
        res = put(blobUri("da39a3ee5e6b4b0d3255bfef95601890afd80709"), "");
        assertThat(res.getStatusLine().getStatusCode(), Matchers.is(409));
        assertThat(res.getStatusLine().getReasonPhrase(), Matchers.is("Conflict"));
    }

    @Test
    public void testGetInvalidDigest() throws Exception {
        CloseableHttpResponse resp = get(blobUri("invlaid"));
        assertThat(resp.getStatusLine().getStatusCode(), Matchers.is(404));
    }

    @Test
    public void testBlobShardIncrementalStatsUpdate() throws IOException {
        String digest = uploadSmallBlob();
        BlobShard blobShard = getBlobShard(digest);
        if (blobShard == null) {
            fail("Unable to find blob shard");
        }
        assertThat(blobShard.getBlobsCount(), Matchers.is(1L));
        assertThat(blobShard.getTotalSize(), Matchers.greaterThan(0L));
        String uri = blobUri(digest);
        delete(uri);
        assertThat(blobShard.getBlobsCount(), Matchers.is(0L));
        assertThat(blobShard.getTotalSize(), Matchers.is(0L));
        // attempting to delete the same digest multiple times doesn't modify the stats
        delete(uri);
        assertThat(blobShard.getBlobsCount(), Matchers.is(0L));
        assertThat(blobShard.getTotalSize(), Matchers.is(0L));
    }

    @Test
    public void testBlobShardStatsWhenTheSameBlobIsConcurrentlyUploaded() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        List<CompletableFuture<String>> blobUploads = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            blobUploads.add(CompletableFuture.supplyAsync(() -> {
                try {
                    latch.countDown();
                    latch.await(10, TimeUnit.SECONDS);
                    return uploadBigBlob();
                } catch (Exception e) {
                    fail(("Expecting successful upload but got: " + (e.getMessage())));
                }
                return null;
            }, executorService));
        }
        try {
            String digest = null;
            for (CompletableFuture<String> blobUpload : blobUploads) {
                digest = blobUpload.join();
            }
            BlobShard blobShard = getBlobShard(digest);
            if (blobShard == null) {
                fail("Unable to find blob shard");
            }
            assertThat(blobShard.getBlobsCount(), Matchers.is(1L));
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}

