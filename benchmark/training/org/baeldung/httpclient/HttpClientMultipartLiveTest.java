package org.baeldung.httpclient;


import ContentType.DEFAULT_BINARY;
import ContentType.TEXT_PLAIN;
import HttpMultipartMode.BROWSER_COMPATIBLE;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientMultipartLiveTest {
    // No longer available
    // private static final String SERVER = "http://echo.200please.com";
    private static final String SERVER = "http://posttestserver.com/post.php";

    private static final String TEXTFILENAME = "temp.txt";

    private static final String IMAGEFILENAME = "image.jpg";

    private static final String ZIPFILENAME = "zipFile.zip";

    private static final Logger LOGGER = Logger.getLogger("org.baeldung.httpclient.HttpClientMultipartLiveTest");

    private CloseableHttpClient client;

    private HttpPost post;

    private BufferedReader rd;

    private CloseableHttpResponse response;

    // tests
    @Test
    public final void givenFileandMultipleTextParts_whenUploadwithAddPart_thenNoExceptions() throws IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(("uploads/" + (HttpClientMultipartLiveTest.TEXTFILENAME)));
        final File file = new File(url.getPath());
        final FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
        final StringBody stringBody1 = new StringBody("This is message 1", ContentType.MULTIPART_FORM_DATA);
        final StringBody stringBody2 = new StringBody("This is message 2", ContentType.MULTIPART_FORM_DATA);
        // 
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        builder.addPart("text1", stringBody1);
        builder.addPart("text2", stringBody2);
        final HttpEntity entity = builder.build();
        // 
        post.setEntity(entity);
        response = client.execute(post);
        final int statusCode = response.getStatusLine().getStatusCode();
        final String responseString = getContent();
        final String contentTypeInHeader = getContentTypeHeader();
        Assert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
        // assertTrue(responseString.contains("Content-Type: multipart/form-data;"));
        Assert.assertTrue(contentTypeInHeader.contains("Content-Type: multipart/form-data;"));
        System.out.println(responseString);
        System.out.println(("POST Content Type: " + contentTypeInHeader));
    }

    @Test
    public final void givenFileandTextPart_whenUploadwithAddBinaryBodyandAddTextBody_ThenNoExeption() throws IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(("uploads/" + (HttpClientMultipartLiveTest.TEXTFILENAME)));
        final File file = new File(url.getPath());
        final String message = "This is a multipart post";
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addBinaryBody("upfile", file, DEFAULT_BINARY, HttpClientMultipartLiveTest.TEXTFILENAME);
        builder.addTextBody("text", message, DEFAULT_BINARY);
        final HttpEntity entity = builder.build();
        post.setEntity(entity);
        response = client.execute(post);
        final int statusCode = response.getStatusLine().getStatusCode();
        final String responseString = getContent();
        final String contentTypeInHeader = getContentTypeHeader();
        Assert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
        // assertTrue(responseString.contains("Content-Type: multipart/form-data;"));
        Assert.assertTrue(contentTypeInHeader.contains("Content-Type: multipart/form-data;"));
        System.out.println(responseString);
        System.out.println(("POST Content Type: " + contentTypeInHeader));
    }

    @Test
    public final void givenFileAndInputStreamandText_whenUploadwithAddBinaryBodyandAddTextBody_ThenNoException() throws IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(("uploads/" + (HttpClientMultipartLiveTest.ZIPFILENAME)));
        final URL url2 = Thread.currentThread().getContextClassLoader().getResource(("uploads/" + (HttpClientMultipartLiveTest.IMAGEFILENAME)));
        final InputStream inputStream = new FileInputStream(url.getPath());
        final File file = new File(url2.getPath());
        final String message = "This is a multipart post";
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addBinaryBody("upfile", file, DEFAULT_BINARY, HttpClientMultipartLiveTest.IMAGEFILENAME);
        builder.addBinaryBody("upstream", inputStream, ContentType.create("application/zip"), HttpClientMultipartLiveTest.ZIPFILENAME);
        builder.addTextBody("text", message, TEXT_PLAIN);
        final HttpEntity entity = builder.build();
        post.setEntity(entity);
        response = client.execute(post);
        final int statusCode = response.getStatusLine().getStatusCode();
        final String responseString = getContent();
        final String contentTypeInHeader = getContentTypeHeader();
        Assert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
        // assertTrue(responseString.contains("Content-Type: multipart/form-data;"));
        Assert.assertTrue(contentTypeInHeader.contains("Content-Type: multipart/form-data;"));
        System.out.println(responseString);
        System.out.println(("POST Content Type: " + contentTypeInHeader));
        inputStream.close();
    }

    @Test
    public final void givenCharArrayandText_whenUploadwithAddBinaryBodyandAddTextBody_ThenNoException() throws IOException {
        final String message = "This is a multipart post";
        final byte[] bytes = "binary code".getBytes();
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(BROWSER_COMPATIBLE);
        builder.addBinaryBody("upfile", bytes, DEFAULT_BINARY, HttpClientMultipartLiveTest.TEXTFILENAME);
        builder.addTextBody("text", message, TEXT_PLAIN);
        final HttpEntity entity = builder.build();
        post.setEntity(entity);
        response = client.execute(post);
        final int statusCode = response.getStatusLine().getStatusCode();
        final String responseString = getContent();
        final String contentTypeInHeader = getContentTypeHeader();
        Assert.assertThat(statusCode, Matchers.equalTo(HttpStatus.SC_OK));
        // assertTrue(responseString.contains("Content-Type: multipart/form-data;"));
        Assert.assertTrue(contentTypeInHeader.contains("Content-Type: multipart/form-data;"));
        System.out.println(responseString);
        System.out.println(("POST Content Type: " + contentTypeInHeader));
    }
}

