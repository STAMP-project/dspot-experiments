package com.github.dreamhead.moco;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoJsonHttpRunnerTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response() throws Exception {
        final HttpServer server = MocoJsonRunner.jsonHttpServer(port(), Moco.file("src/test/resources/foo.json"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_from_path_resource() throws Exception {
        final HttpServer server = MocoJsonRunner.jsonHttpServer(port(), Moco.pathResource("foo.json"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_without_port() throws Exception {
        final HttpServer server = MocoJsonRunner.jsonHttpServer(Moco.file("src/test/resources/foo.json"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(root(server.port())), CoreMatchers.is("foo"));
            }
        });
    }
}

