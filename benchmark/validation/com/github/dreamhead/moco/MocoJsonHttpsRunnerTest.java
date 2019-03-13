package com.github.dreamhead.moco;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoJsonHttpsRunnerTest extends AbstractMocoStandaloneTest {
    private final HttpsCertificate DEFAULT_CERTIFICATE = HttpsCertificate.certificate(Moco.pathResource("cert.jks"), "mocohttps", "mocohttps");

    @Test
    public void should_return_expected_response() throws Exception {
        final HttpServer server = MocoJsonRunner.jsonHttpsServer(port(), Moco.file("src/test/resources/foo.json"), DEFAULT_CERTIFICATE);
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(httpsRoot()), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_from_path_resource() throws Exception {
        final HttpServer server = MocoJsonRunner.jsonHttpsServer(port(), Moco.pathResource("foo.json"), DEFAULT_CERTIFICATE);
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(httpsRoot()), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_without_port() throws Exception {
        final HttpServer server = MocoJsonRunner.jsonHttpsServer(Moco.file("src/test/resources/foo.json"), DEFAULT_CERTIFICATE);
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(httpsRoot(server.port())), CoreMatchers.is("foo"));
            }
        });
    }
}

