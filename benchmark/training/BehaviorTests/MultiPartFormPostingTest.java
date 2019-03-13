/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package BehaviorTests;


import ContentType.APPLICATION_OCTET_STREAM;
import ContentType.IMAGE_JPEG;
import ContentType.MULTIPART_FORM_DATA;
import ContentType.WILDCARD;
import MultipartMode.STRICT;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import kong.unirest.MockCallback;
import kong.unirest.TestUtil;
import org.junit.Assert;
import org.junit.Test;


public class MultiPartFormPostingTest extends BddTest {
    @Test
    public void testMultipart() throws Exception {
        Unirest.post(MockServer.POST).field("name", "Mark").field("funky", "bunch").field("file", TestUtil.rezFile("/test")).asObject(RequestCapture.class).getBody().assertParam("name", "Mark").assertMultiPartContentType().getFile("test").assertBody("This is a test file").assertFileType("application/octet-stream");
    }

    @Test
    public void testMultipartContentType() throws Exception {
        Unirest.post(MockServer.POST).field("name", "Mark").field("file", TestUtil.rezFile("/image.jpg"), "image/jpeg").asObject(RequestCapture.class).getBody().assertMultiPartContentType().assertParam("name", "Mark").getFile("image.jpg").assertFileType("image/jpeg");
    }

    @Test
    public void testMultipartInputStreamContentType() throws Exception {
        FileInputStream stream = new FileInputStream(TestUtil.rezFile("/image.jpg"));
        Unirest.post(MockServer.POST).header("accept", MULTIPART_FORM_DATA.toString()).field("name", "Mark").field("file", stream, APPLICATION_OCTET_STREAM, "image.jpg").asObject(RequestCapture.class).getBody().assertMultiPartContentType().assertHeader("Accept", MULTIPART_FORM_DATA.toString()).assertParam("name", "Mark").getFile("image.jpg").assertFileType("application/octet-stream");
    }

    @Test
    public void testMultipartInputStreamContentTypeAsync() throws Exception {
        Unirest.post(MockServer.POST).field("name", "Mark").field("file", new FileInputStream(TestUtil.rezFile("/test")), APPLICATION_OCTET_STREAM, "test").asJsonAsync(new MockCallback(this, ( r) -> parse(r).assertParam("name", "Mark").assertMultiPartContentType().getFile("test").assertFileType("application/octet-stream")));
        assertAsync();
    }

    @Test
    public void testMultipartByteContentType() throws Exception {
        final byte[] bytes = TestUtil.getFileBytes("/image.jpg");
        Unirest.post(MockServer.POST).field("boot", "boot").field("file", bytes, "image.jpg").asObject(RequestCapture.class).getBody().assertMultiPartContentType().getFile("image.jpg").assertFileType("application/octet-stream");
    }

    @Test
    public void testMultipartByteContentTypeAsync() throws Exception {
        final byte[] bytes = TestUtil.getFileBytes("/test");
        Unirest.post(MockServer.POST).field("name", "Mark").field("file", bytes, "test").asJsonAsync(new MockCallback(this, ( r) -> parse(r).assertParam("name", "Mark").assertMultiPartContentType().getFile("test").assertFileType("application/octet-stream")));
        assertAsync();
    }

    @Test
    public void testMultipartAsync() throws Exception {
        Unirest.post(MockServer.POST).field("name", "Mark").field("file", TestUtil.rezFile("/test")).asJsonAsync(new MockCallback(this, ( r) -> parse(r).assertParam("name", "Mark").assertMultiPartContentType().getFile("test").assertFileType("application/octet-stream").assertBody("This is a test file")));
        assertAsync();
    }

    @Test
    public void utf8FileNames() {
        InputStream fileData = new ByteArrayInputStream(new byte[]{ 't', 'e', 's', 't' });
        final String filename = "file???.p?f";
        Unirest.post(MockServer.POST).field("file", fileData, filename).asObject(RequestCapture.class).getBody().assertMultiPartContentType().getFile(filename).assertFileName(filename);
    }

    @Test
    public void canSetModeToStrictForLegacySupport() {
        InputStream fileData = new ByteArrayInputStream(new byte[]{ 't', 'e', 's', 't' });
        final String filename = "file???.p?f";
        Unirest.post(MockServer.POST).field("file", fileData, filename).mode(STRICT).asObject(RequestCapture.class).getBody().assertMultiPartContentType().getFile("file???.p?f").assertFileName("file???.p?f");
    }

    @Test
    public void canPostInputStreamWithContentType() throws Exception {
        File file = TestUtil.getImageFile();
        Unirest.post(MockServer.POST).field("testfile", new FileInputStream(file), IMAGE_JPEG, "image.jpg").asObject(RequestCapture.class).getBody().assertMultiPartContentType().getFileByInput("testfile").assertFileName("image.jpg").assertFileType("image/jpeg");
    }

    @Test
    public void canPostInputStream() throws Exception {
        File file = TestUtil.getImageFile();
        Unirest.post(MockServer.POST).field("testfile", new FileInputStream(file), "image.jpg").asObject(RequestCapture.class).getBody().assertMultiPartContentType().getFileByInput("testfile").assertFileName("image.jpg").assertFileType("application/octet-stream");
    }

    @Test
    public void postFieldsAsMap() throws URISyntaxException {
        File file = TestUtil.getImageFile();
        Unirest.post(MockServer.POST).fields(TestUtil.mapOf("big", "bird", "charlie", 42, "testfile", file, "gonzo", null)).asObject(RequestCapture.class).getBody().assertMultiPartContentType().assertParam("big", "bird").assertParam("charlie", "42").assertParam("gonzo", "").getFile("image.jpg").assertFileType("application/octet-stream");
    }

    @Test
    public void postFileWithoutContentType() {
        File file = TestUtil.getImageFile();
        Unirest.post(MockServer.POST).field("testfile", file).asObject(RequestCapture.class).getBody().assertMultiPartContentType().getFile("image.jpg").assertFileType("application/octet-stream");
    }

    @Test
    public void postFileWithContentType() {
        File file = TestUtil.getImageFile();
        Unirest.post(MockServer.POST).field("testfile", file, IMAGE_JPEG.getMimeType()).asObject(RequestCapture.class).getBody().assertMultiPartContentType().getFile("image.jpg").assertFileType(IMAGE_JPEG);
    }

    @Test
    public void multiPartInputStreamAsFile() throws FileNotFoundException {
        Unirest.post(MockServer.POST).field("foo", "bar").field("filecontents", new FileInputStream(TestUtil.rezFile("/image.jpg")), "image.jpg").asObject(RequestCapture.class).getBody().assertMultiPartContentType().assertParam("foo", "bar").getFileByInput("filecontents").assertFileType(APPLICATION_OCTET_STREAM).assertFileName("image.jpg");
    }

    @Test
    public void testPostMulipleFIles() {
        RequestCapture cap = Unirest.post(MockServer.POST).field("name", Arrays.asList(TestUtil.rezFile("/test"), TestUtil.rezFile("/test2"))).asObject(RequestCapture.class).getBody().assertMultiPartContentType();
        cap.getFile("test").assertBody("This is a test file");
        cap.getFile("test2").assertBody("this is another test");
    }

    @Test
    public void testPostMultipleFiles() throws Exception {
        Unirest.post(MockServer.POST).field("param3", "wot").field("file1", TestUtil.rezFile("/test")).field("file2", TestUtil.rezFile("/test")).asObject(RequestCapture.class).getBody().assertMultiPartContentType().assertParam("param3", "wot").assertFileContent("file1", "This is a test file").assertFileContent("file2", "This is a test file");
    }

    @Test
    public void testPostBinaryUTF8() throws Exception {
        Unirest.post(MockServer.POST).header("Accept", MULTIPART_FORM_DATA.getMimeType()).field("param3", "?????").field("file", TestUtil.rezFile("/test")).asObject(RequestCapture.class).getBody().assertMultiPartContentType().assertParam("param3", "?????").assertFileContent("file", "This is a test file");
    }

    @Test
    public void testMultipeInputStreams() throws FileNotFoundException {
        Unirest.post(MockServer.POST).field("name", Arrays.asList(new FileInputStream(TestUtil.rezFile("/test")), new FileInputStream(TestUtil.rezFile("/test2")))).asObject(RequestCapture.class).getBody().assertMultiPartContentType().assertParam("name", "This is a test file").assertParam("name", "this is another test");
    }

    @Test
    public void multiPartInputStream() throws FileNotFoundException {
        Unirest.post(MockServer.POST).field("foo", "bar").field("filecontents", new FileInputStream(TestUtil.rezFile("/test")), WILDCARD).asObject(RequestCapture.class).getBody().assertMultiPartContentType().assertParam("foo", "bar").assertParam("filecontents", "This is a test file");
    }

    @Test
    public void passFileAsByteArray() {
        Unirest.post(MockServer.POST).field("foo", "bar").field("filecontents", TestUtil.getFileBytes("/image.jpg"), IMAGE_JPEG, "image.jpg").asObject(RequestCapture.class).getBody().assertMultiPartContentType().assertParam("foo", "bar").getFileByInput("filecontents").assertFileType(IMAGE_JPEG).assertFileName("image.jpg");
    }

    @Test
    public void nullFileResultsInEmptyPost() {
        Unirest.post(MockServer.POST).field("testfile", ((Object) (null)), IMAGE_JPEG.getMimeType()).asObject(RequestCapture.class).getBody().assertContentType("application/x-www-form-urlencoded; charset=UTF-8").assertParam("testfile", "");
    }

    @Test
    public void rawInspection() {
        String body = Unirest.post(MockServer.ECHO_RAW).field("marky", "mark").field("funky", "bunch").field("file", TestUtil.rezFile("/test")).asString().getBody();
        String expected = TestUtil.getResource("rawPost.txt").replaceAll("\r", "").trim();
        String id = body.substring(2, ((body.indexOf("\n")) - 1));
        body = body.replaceAll(id, "IDENTIFIER").replaceAll("\r", "").trim();
        Assert.assertEquals(expected, body);
    }
}

