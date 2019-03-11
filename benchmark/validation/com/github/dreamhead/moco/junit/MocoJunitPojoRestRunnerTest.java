package com.github.dreamhead.moco.junit;


import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestServer;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MocoJunitPojoRestRunnerTest extends AbstractMocoStandaloneTest {
    private static RestServer server;

    static {
        MocoJunitPojoRestRunnerTest.server = MocoRest.restServer(12306);
        MocoJunitPojoRestRunnerTest.server.resource("targets", MocoRest.post().response(Moco.status(201), Moco.header("Location", "/targets/123")));
    }

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.restRunner(MocoJunitPojoRestRunnerTest.server);

    @Test
    public void should_return_expected_message() throws IOException {
        HttpResponse response = helper.postForResponse(remoteUrl("/targets"), "hello");
        Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(201));
        Assert.assertThat(response.getFirstHeader("Location").getValue(), CoreMatchers.is("/targets/123"));
    }
}

