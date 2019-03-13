package com.github.dreamhead.moco.junit;


import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.HttpsServer;
import com.github.dreamhead.moco.Moco;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MocoJunitPojoHttpsRunnerTest extends AbstractMocoStandaloneTest {
    private static final HttpsCertificate DEFAULT_CERTIFICATE = HttpsCertificate.certificate(Moco.pathResource("cert.jks"), "mocohttps", "mocohttps");

    private static HttpsServer server;

    static {
        MocoJunitPojoHttpsRunnerTest.server = Moco.httpsServer(12306, MocoJunitPojoHttpsRunnerTest.DEFAULT_CERTIFICATE);
        MocoJunitPojoHttpsRunnerTest.server.response("foo");
    }

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.httpsRunner(MocoJunitPojoHttpsRunnerTest.server);

    @Test
    public void should_return_expected_message() throws IOException {
        Assert.assertThat(helper.get(httpsRoot()), CoreMatchers.is("foo"));
    }
}

