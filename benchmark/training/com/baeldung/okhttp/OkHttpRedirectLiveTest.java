package com.baeldung.okhttp;


import java.io.IOException;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class OkHttpRedirectLiveTest {
    @Test
    public void whenSetFollowRedirects_thenNotRedirected() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();
        Request request = new Request.Builder().url("http://t.co/I5YYd9tddw").build();
        Call call = client.newCall(request);
        Response response = call.execute();
        Assert.assertThat(response.code(), Matchers.equalTo(301));
    }
}

