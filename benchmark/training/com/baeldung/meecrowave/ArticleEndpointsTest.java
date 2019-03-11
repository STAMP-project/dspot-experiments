package com.baeldung.meecrowave;


import Meecrowave.Builder;
import MonoMeecrowave.Runner;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Runner.class)
public class ArticleEndpointsTest {
    @ConfigurationInject
    private Builder config;

    private static OkHttpClient client;

    @Test
    public void whenRetunedArticle_thenCorrect() throws IOException {
        final String base = "http://localhost:" + (config.getHttpPort());
        Request request = new Request.Builder().url((base + "/article")).build();
        Response response = ArticleEndpointsTest.client.newCall(request).execute();
        Assert.assertEquals(200, response.code());
    }
}

