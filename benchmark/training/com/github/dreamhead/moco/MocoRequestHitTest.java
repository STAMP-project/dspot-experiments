package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import java.io.IOException;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MocoRequestHitTest {
    private static final HttpsCertificate DEFAULT_CERTIFICATE = HttpsCertificate.certificate(Moco.pathResource("cert.jks"), "mocohttps", "mocohttps");

    private MocoTestHelper helper;

    private RequestHit hit;

    @Test
    public void should_monitor_server_behavior() throws Exception {
        final MocoMonitor monitor = Mockito.mock(MocoMonitor.class);
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), monitor);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        Mockito.verify(monitor).onMessageArrived(ArgumentMatchers.any(HttpRequest.class));
        Mockito.verify(monitor).onMessageLeave(ArgumentMatchers.any(HttpResponse.class));
        Mockito.verify(monitor, Mockito.never()).onException(ArgumentMatchers.any(Exception.class));
    }

    @Test
    public void should_monitor_server_behavior_without_port() throws Exception {
        final MocoMonitor monitor = Mockito.mock(MocoMonitor.class);
        final HttpServer server = Moco.httpServer(monitor);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl(server.port(), "/foo")), CoreMatchers.is("bar"));
            }
        });
        Mockito.verify(monitor).onMessageArrived(ArgumentMatchers.any(HttpRequest.class));
        Mockito.verify(monitor).onMessageLeave(ArgumentMatchers.any(HttpResponse.class));
        Mockito.verify(monitor, Mockito.never()).onException(ArgumentMatchers.any(Exception.class));
    }

    @Test
    public void should_verify_expected_request() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.times(1));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_while_expectation_can_not_be_met() throws Exception {
        Moco.httpServer(RemoteTestUtils.port(), hit);
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.times(1));
    }

    @Test
    public void should_verify_expected_request_for_at_least() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.atLeast(1));
    }

    @Test
    public void should_verify_expected_request_for_between() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.between(1, 3));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_at_least_expected_request_while_expectation_can_not_be_met() throws Exception {
        Moco.httpServer(RemoteTestUtils.port(), hit);
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.atLeast(1));
    }

    @Test
    public void should_verify_expected_request_for_at_most() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.atMost(2));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_at_most_expected_request_while_expectation_can_not_be_met() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.atMost(1));
    }

    @Test
    public void should_verify_expected_request_for_once() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.once());
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_while_once_expectation_can_not_be_met() throws Exception {
        Moco.httpServer(RemoteTestUtils.port(), hit);
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.times(1));
    }

    @Test
    public void should_verify_unexpected_request_without_unexpected_request() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        hit.verify(MocoRequestHit.unexpected(), MocoRequestHit.never());
    }

    @Test
    public void should_verify_unexpected_request_with_unexpected_request() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                try {
                    helper.get(RemoteTestUtils.remoteUrl("/foo"));
                } catch (IOException ignored) {
                }
            }
        });
        hit.verify(MocoRequestHit.unexpected(), MocoRequestHit.times(1));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_while_unexpected_request_expectation_can_not_be_met() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                try {
                    helper.get(RemoteTestUtils.remoteUrl("/foo"));
                } catch (IOException ignored) {
                }
            }
        });
        hit.verify(MocoRequestHit.unexpected(), MocoRequestHit.never());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_negative_number_for_times() {
        MocoRequestHit.times((-1));
    }

    @Test
    public void should_verify_form_data() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.post(Moco.eq(Moco.form("name"), "dreamhead")).response("foobar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Request request = Request.Post(RemoteTestUtils.root()).bodyForm(new BasicNameValuePair("name", "dreamhead"));
                String content = helper.executeAsString(request);
                Assert.assertThat(content, CoreMatchers.is("foobar"));
            }
        });
        hit.verify(Moco.eq(Moco.form("name"), "dreamhead"), MocoRequestHit.once());
    }

    @Test
    public void should_verify_form_data_even_if_no_server_expectation() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.response("foobar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Request request = Request.Post(RemoteTestUtils.root()).bodyForm(new BasicNameValuePair("name", "dreamhead"));
                String content = helper.executeAsString(request);
                Assert.assertThat(content, CoreMatchers.is("foobar"));
            }
        });
        hit.verify(Moco.eq(Moco.form("name"), "dreamhead"), MocoRequestHit.once());
    }

    @Test
    public void should_verify_expected_request_and_log_at_same_time() throws Exception {
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit, Moco.log());
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.times(1));
    }

    @Test
    public void should_verify_expected_request_and_log_at_same_time_for_https() throws Exception {
        final HttpServer server = Moco.httpsServer(RemoteTestUtils.port(), MocoRequestHitTest.DEFAULT_CERTIFICATE, hit, Moco.log());
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteHttpsUrl("/foo")), CoreMatchers.is("bar"));
            }
        });
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.times(1));
    }
}

