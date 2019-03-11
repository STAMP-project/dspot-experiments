package com.baeldung.okhttp;


import MultipartBody.FORM;
import com.baeldung.client.Consts;
import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class OkHttpFileUploadingLiveTest {
    private static final String BASE_URL = ("http://localhost:" + (Consts.APPLICATION_PORT)) + "/spring-rest";

    OkHttpClient client;

    @Test
    public void whenUploadFile_thenCorrect() throws IOException {
        final RequestBody requestBody = new MultipartBody.Builder().setType(FORM).addFormDataPart("file", "file.txt", RequestBody.create(MediaType.parse("application/octet-stream"), new File("src/test/resources/test.txt"))).build();
        final Request request = new Request.Builder().url(((OkHttpFileUploadingLiveTest.BASE_URL) + "/users/upload")).post(requestBody).build();
        final Call call = client.newCall(request);
        final Response response = call.execute();
        Assert.assertThat(response.code(), Matchers.equalTo(200));
    }

    @Test
    public void whenGetUploadFileProgress_thenCorrect() throws IOException {
        final RequestBody requestBody = new MultipartBody.Builder().setType(FORM).addFormDataPart("file", "file.txt", RequestBody.create(MediaType.parse("application/octet-stream"), new File("src/test/resources/test.txt"))).build();
        final ProgressRequestWrapper countingBody = new ProgressRequestWrapper(requestBody, (long bytesWritten,long contentLength) -> {
            final float percentage = (100.0F * bytesWritten) / contentLength;
            assertFalse(((Float.compare(percentage, 100)) > 0));
        });
        final Request request = new Request.Builder().url(((OkHttpFileUploadingLiveTest.BASE_URL) + "/users/upload")).post(countingBody).build();
        final Call call = client.newCall(request);
        final Response response = call.execute();
        Assert.assertThat(response.code(), Matchers.equalTo(200));
    }
}

