package com.github.dreamhead.moco.junit;


import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import com.github.dreamhead.moco.Moco;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MocoJunitJsonRestRunnerWithPathTest extends AbstractMocoStandaloneTest {
    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.jsonRestRunner(12306, Moco.pathResource("rest.json"));

    @Test
    public void should_return_expected_message() throws IOException {
        HttpResponse response = helper.postForResponse(remoteUrl("/targets"), "hello");
        Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(201));
        Assert.assertThat(response.getFirstHeader("Location").getValue(), CoreMatchers.is("/targets/123"));
    }
}

