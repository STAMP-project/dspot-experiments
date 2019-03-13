package com.baeldung.okhttp;


import HttpUrl.Builder;
import com.baeldung.client.Consts;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OkHttpGetLiveTest {
    private static final String BASE_URL = ("http://localhost:" + (Consts.APPLICATION_PORT)) + "/spring-rest";

    OkHttpClient client;

    @Test
    public void whenGetRequest_thenCorrect() throws IOException {
        final Request request = new Request.Builder().url(((OkHttpGetLiveTest.BASE_URL) + "/date")).build();
        final Call call = client.newCall(request);
        final Response response = call.execute();
        Assert.assertThat(response.code(), CoreMatchers.equalTo(200));
    }

    @Test
    public void whenGetRequestWithQueryParameter_thenCorrect() throws IOException {
        final HttpUrl.Builder urlBuilder = HttpUrl.parse(((OkHttpGetLiveTest.BASE_URL) + "/ex/bars")).newBuilder();
        urlBuilder.addQueryParameter("id", "1");
        final String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).build();
        final Call call = client.newCall(request);
        final Response response = call.execute();
        Assert.assertThat(response.code(), CoreMatchers.equalTo(200));
    }

    @Test
    public void whenAsynchronousGetRequest_thenCorrect() throws InterruptedException {
        final Request request = new Request.Builder().url(((OkHttpGetLiveTest.BASE_URL) + "/date")).build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("OK");
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Assert.fail();
            }
        });
        Thread.sleep(3000);
    }
}

