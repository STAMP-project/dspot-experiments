package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoHttpsTest {
    private static final HttpsCertificate DEFAULT_CERTIFICATE = HttpsCertificate.certificate(Moco.pathResource("cert.jks"), "mocohttps", "mocohttps");

    private MocoTestHelper helper;

    @Test
    public void should_return_expected_result() throws Exception {
        HttpsServer server = Moco.httpsServer(RemoteTestUtils.port(), MocoHttpsTest.DEFAULT_CERTIFICATE);
        server.response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.httpsRoot()), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_result_for_specified_request() throws Exception {
        HttpsServer server = Moco.httpsServer(RemoteTestUtils.port(), MocoHttpsTest.DEFAULT_CERTIFICATE);
        server.request(Moco.by("foo")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.httpsRoot(), "foo"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_result_with_monitor() throws Exception {
        RequestHit hit = MocoRequestHit.requestHit();
        HttpsServer server = Moco.httpsServer(RemoteTestUtils.port(), MocoHttpsTest.DEFAULT_CERTIFICATE, hit);
        server.request(Moco.by("foo")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.httpsRoot(), "foo"), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by("foo"), MocoRequestHit.once());
    }

    @Test
    public void should_return_expected_result_without_port() throws Exception {
        final HttpsServer server = Moco.httpsServer(MocoHttpsTest.DEFAULT_CERTIFICATE);
        server.request(Moco.by("foo")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.httpsRoot(server.port()), "foo"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_result_with_monitor_without_port() throws Exception {
        RequestHit hit = MocoRequestHit.requestHit();
        final HttpsServer server = Moco.httpsServer(MocoHttpsTest.DEFAULT_CERTIFICATE, hit);
        server.request(Moco.by("foo")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.httpsRoot(server.port()), "foo"), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by("foo"), MocoRequestHit.once());
    }

    @Test
    public void should_return_expected_result_with_global_config() throws Exception {
        HttpsServer server = Moco.httpsServer(RemoteTestUtils.port(), MocoHttpsTest.DEFAULT_CERTIFICATE, Moco.context("/foo"));
        server.request(Moco.by(Moco.uri("/bar"))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteHttpsUrl("/foo/bar")), CoreMatchers.is("foo"));
            }
        });
    }
}

