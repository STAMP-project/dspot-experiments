package com.github.dreamhead.moco.junit;


import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Moco;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MocoJunitPojoHttpRunnerTest extends AbstractMocoStandaloneTest {
    private static HttpServer server;

    static {
        MocoJunitPojoHttpRunnerTest.server = Moco.httpServer(12306);
        MocoJunitPojoHttpRunnerTest.server.response("foo");
    }

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.httpRunner(MocoJunitPojoHttpRunnerTest.server);

    @Test
    public void should_return_expected_message() throws IOException {
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
    }
}

