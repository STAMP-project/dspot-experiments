package com.gitblit.tests;


import FilestoreManager.UNDEFINED_SIZE;
import FilestoreServlet.GIT_LFS_META_MIME;
import FilestoreServlet.REGEX_GROUP_BASE_URI;
import FilestoreServlet.REGEX_GROUP_ENDPOINT;
import FilestoreServlet.REGEX_GROUP_PREFIX;
import FilestoreServlet.REGEX_GROUP_REPOSITORY;
import FilestoreServlet.REGEX_PATH;
import Keys.filestore.maxUploadSize;
import Status.Available;
import com.gitblit.models.RepositoryModel;
import com.gitblit.models.UserModel;
import com.gitblit.utils.FileUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;


public class FilestoreServletTest extends GitblitUnitTest {
    private static final AtomicBoolean started = new AtomicBoolean(false);

    private static final String SHA256_EG = "9a712c5d4037503a2d5ee1d07ad191eb99d051e84cbb020c171a5ae19bbe3cbd";

    private static final String repoName = "helloworld.git";

    private static final String repoLfs = ("/r/" + (FilestoreServletTest.repoName)) + "/info/lfs/objects/";

    @Test
    public void testRegexGroups() throws Exception {
        Pattern p = Pattern.compile(REGEX_PATH);
        String basicUrl = "https://localhost:8080/r/test.git/info/lfs/objects/";
        String batchUrl = basicUrl + "batch";
        String oidUrl = basicUrl + (FilestoreServletTest.SHA256_EG);
        Matcher m = p.matcher(batchUrl);
        Assert.assertTrue(m.find());
        Assert.assertEquals("https://localhost:8080", m.group(REGEX_GROUP_BASE_URI));
        Assert.assertEquals("r", m.group(REGEX_GROUP_PREFIX));
        Assert.assertEquals("test.git", m.group(REGEX_GROUP_REPOSITORY));
        Assert.assertEquals("batch", m.group(REGEX_GROUP_ENDPOINT));
        m = p.matcher(oidUrl);
        Assert.assertTrue(m.find());
        Assert.assertEquals("https://localhost:8080", m.group(REGEX_GROUP_BASE_URI));
        Assert.assertEquals("r", m.group(REGEX_GROUP_PREFIX));
        Assert.assertEquals("test.git", m.group(REGEX_GROUP_REPOSITORY));
        Assert.assertEquals(FilestoreServletTest.SHA256_EG, m.group(REGEX_GROUP_ENDPOINT));
    }

    @Test
    public void testRegexGroupsNestedRepo() throws Exception {
        Pattern p = Pattern.compile(REGEX_PATH);
        String basicUrl = "https://localhost:8080/r/nested/test.git/info/lfs/objects/";
        String batchUrl = basicUrl + "batch";
        String oidUrl = basicUrl + (FilestoreServletTest.SHA256_EG);
        Matcher m = p.matcher(batchUrl);
        Assert.assertTrue(m.find());
        Assert.assertEquals("https://localhost:8080", m.group(REGEX_GROUP_BASE_URI));
        Assert.assertEquals("r", m.group(REGEX_GROUP_PREFIX));
        Assert.assertEquals("nested/test.git", m.group(REGEX_GROUP_REPOSITORY));
        Assert.assertEquals("batch", m.group(REGEX_GROUP_ENDPOINT));
        m = p.matcher(oidUrl);
        Assert.assertTrue(m.find());
        Assert.assertEquals("https://localhost:8080", m.group(REGEX_GROUP_BASE_URI));
        Assert.assertEquals("r", m.group(REGEX_GROUP_PREFIX));
        Assert.assertEquals("nested/test.git", m.group(REGEX_GROUP_REPOSITORY));
        Assert.assertEquals(FilestoreServletTest.SHA256_EG, m.group(REGEX_GROUP_ENDPOINT));
    }

    @Test
    public void testDownload() throws Exception {
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        RepositoryModel r = GitblitUnitTest.gitblit().getRepositoryModel(FilestoreServletTest.repoName);
        UserModel u = new UserModel("admin");
        u.canAdmin = true;
        // No upload limit
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, UNDEFINED_SIZE);
        final BlobInfo blob = new BlobInfo((512 * (FileUtils.KB)));
        // Emulate a pre-existing Git-LFS repository by using using internal pre-tested methods
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(blob.hash, blob.length, u, r, new ByteArrayInputStream(blob.blob)));
        final String downloadURL = ((GitBlitSuite.url) + (FilestoreServletTest.repoLfs)) + (blob.hash);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(downloadURL);
        // add request header
        request.addHeader(HttpHeaders.ACCEPT, GIT_LFS_META_MIME);
        HttpResponse response = client.execute(request);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        String expectedContent = String.format("{%s:%s,%s:%d,%s:{%s:{%s:%s}}}", "\"oid\"", (("\"" + (blob.hash)) + "\""), "\"size\"", blob.length, "\"actions\"", "\"download\"", "\"href\"", (("\"" + downloadURL) + "\""));
        Assert.assertEquals(expectedContent, content);
        // Now try the binary download
        request.removeHeaders(HttpHeaders.ACCEPT);
        response = client.execute(request);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        byte[] dlData = IOUtils.toByteArray(response.getEntity().getContent());
        Assert.assertArrayEquals(blob.blob, dlData);
    }

    @Test
    public void testDownloadMultiple() throws Exception {
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        RepositoryModel r = GitblitUnitTest.gitblit().getRepositoryModel(FilestoreServletTest.repoName);
        UserModel u = new UserModel("admin");
        u.canAdmin = true;
        // No upload limit
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, UNDEFINED_SIZE);
        final BlobInfo blob = new BlobInfo((512 * (FileUtils.KB)));
        // Emulate a pre-existing Git-LFS repository by using using internal pre-tested methods
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(blob.hash, blob.length, u, r, new ByteArrayInputStream(blob.blob)));
        final String batchURL = ((GitBlitSuite.url) + (FilestoreServletTest.repoLfs)) + "batch";
        final String downloadURL = ((GitBlitSuite.url) + (FilestoreServletTest.repoLfs)) + (blob.hash);
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(batchURL);
        // add request header
        request.addHeader(HttpHeaders.ACCEPT, GIT_LFS_META_MIME);
        request.addHeader(HttpHeaders.CONTENT_ENCODING, GIT_LFS_META_MIME);
        String content = String.format("{%s:%s,%s:[{%s:%s,%s:%d},{%s:%s,%s:%d}]}", "\"operation\"", "\"download\"", "\"objects\"", "\"oid\"", (("\"" + (blob.hash)) + "\""), "\"size\"", blob.length, "\"oid\"", (("\"" + (FilestoreServletTest.SHA256_EG)) + "\""), "\"size\"", 0);
        HttpEntity entity = new ByteArrayEntity(content.getBytes("UTF-8"));
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        String responseMessage = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        String expectedContent = String.format("{%s:[{%s:%s,%s:%d,%s:{%s:{%s:%s}}},{%s:%s,%s:%d,%s:{%s:%s,%s:%d}}]}", "\"objects\"", "\"oid\"", (("\"" + (blob.hash)) + "\""), "\"size\"", blob.length, "\"actions\"", "\"download\"", "\"href\"", (("\"" + downloadURL) + "\""), "\"oid\"", (("\"" + (FilestoreServletTest.SHA256_EG)) + "\""), "\"size\"", 0, "\"error\"", "\"message\"", "\"Object not available\"", "\"code\"", 404);
        Assert.assertEquals(expectedContent, responseMessage);
    }

    @Test
    public void testDownloadUnavailable() throws Exception {
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        // No upload limit
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, UNDEFINED_SIZE);
        final BlobInfo blob = new BlobInfo((512 * (FileUtils.KB)));
        final String downloadURL = ((GitBlitSuite.url) + (FilestoreServletTest.repoLfs)) + (blob.hash);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(downloadURL);
        // add request header
        request.addHeader(HttpHeaders.ACCEPT, GIT_LFS_META_MIME);
        HttpResponse response = client.execute(request);
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
        String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        String expectedError = String.format("{%s:%s,%s:%d}", "\"message\"", "\"Object not available\"", "\"code\"", 404);
        Assert.assertEquals(expectedError, content);
    }

    @Test
    public void testUpload() throws Exception {
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        RepositoryModel r = GitblitUnitTest.gitblit().getRepositoryModel(FilestoreServletTest.repoName);
        UserModel u = new UserModel("admin");
        u.canAdmin = true;
        // No upload limit
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, UNDEFINED_SIZE);
        final BlobInfo blob = new BlobInfo((512 * (FileUtils.KB)));
        final String expectedUploadURL = ((GitBlitSuite.url) + (FilestoreServletTest.repoLfs)) + (blob.hash);
        final String initialUploadURL = ((GitBlitSuite.url) + (FilestoreServletTest.repoLfs)) + "batch";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(initialUploadURL);
        // add request header
        request.addHeader(HttpHeaders.ACCEPT, GIT_LFS_META_MIME);
        request.addHeader(HttpHeaders.CONTENT_ENCODING, GIT_LFS_META_MIME);
        String content = String.format("{%s:%s,%s:[{%s:%s,%s:%d}]}", "\"operation\"", "\"upload\"", "\"objects\"", "\"oid\"", (("\"" + (blob.hash)) + "\""), "\"size\"", blob.length);
        HttpEntity entity = new ByteArrayEntity(content.getBytes("UTF-8"));
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        String responseMessage = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        String expectedContent = String.format("{%s:[{%s:%s,%s:%d,%s:{%s:{%s:%s}}}]}", "\"objects\"", "\"oid\"", (("\"" + (blob.hash)) + "\""), "\"size\"", blob.length, "\"actions\"", "\"upload\"", "\"href\"", (("\"" + expectedUploadURL) + "\""));
        Assert.assertEquals(expectedContent, responseMessage);
        // Now try to upload the binary download
        HttpPut putRequest = new HttpPut(expectedUploadURL);
        putRequest.setEntity(new ByteArrayEntity(blob.blob));
        response = client.execute(putRequest);
        responseMessage = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        // Confirm behind the scenes that it is available
        ByteArrayOutputStream savedBlob = new ByteArrayOutputStream();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(blob.hash, u, r, savedBlob));
        Assert.assertArrayEquals(blob.blob, savedBlob.toByteArray());
    }

    @Test
    public void testMalformedUpload() throws Exception {
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        // No upload limit
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, UNDEFINED_SIZE);
        final BlobInfo blob = new BlobInfo((512 * (FileUtils.KB)));
        final String initialUploadURL = ((GitBlitSuite.url) + (FilestoreServletTest.repoLfs)) + "batch";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(initialUploadURL);
        // add request header
        request.addHeader(HttpHeaders.ACCEPT, GIT_LFS_META_MIME);
        request.addHeader(HttpHeaders.CONTENT_ENCODING, GIT_LFS_META_MIME);
        // Malformed JSON, comma instead of colon and unquoted strings
        String content = String.format("{%s:%s,%s:[{%s:%s,%s,%d}]}", "operation", "upload", "objects", "oid", blob.hash, "size", blob.length);
        HttpEntity entity = new ByteArrayEntity(content.getBytes("UTF-8"));
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        String responseMessage = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        Assert.assertEquals(400, response.getStatusLine().getStatusCode());
        String expectedError = String.format("{%s:%s,%s:%d}", "\"message\"", "\"Malformed Git-LFS request\"", "\"code\"", 400);
        Assert.assertEquals(expectedError, responseMessage);
    }
}

