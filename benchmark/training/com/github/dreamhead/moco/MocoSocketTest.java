package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoSocketHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.Charset;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class MocoSocketTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MocoSocketHelper helper;

    private SocketServer server;

    @Test
    public void should_return_expected_response() throws Exception {
        server.request(Moco.by("foo")).response(line("bar"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                Assert.assertThat(helper.send("foo"), CoreMatchers.is("bar"));
                helper.close();
            }
        });
    }

    @Test
    public void should_return_many_expected_responses() throws Exception {
        server.request(Moco.by("foo")).response(line("bar"));
        server.request(Moco.by("bar")).response(line("blah"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                Assert.assertThat(helper.send("foo"), CoreMatchers.is("bar"));
                Assert.assertThat(helper.send("bar"), CoreMatchers.is("blah"));
                helper.close();
            }
        });
    }

    @Test
    public void should_match_extreme_big_request() throws Exception {
        server.request(Moco.by(times("a", 1025))).response(line("long_a"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                Assert.assertThat(helper.send(times("a", 1025)), CoreMatchers.is("long_a"));
                helper.close();
            }
        });
    }

    @Test
    public void should_log_request_and_response_into_file() throws Exception {
        File file = folder.newFile();
        SocketServer socketServer = Moco.socketServer(RemoteTestUtils.port(), Moco.log(file.getAbsolutePath()));
        socketServer.request(Moco.by("0XCAFE")).response(line("0XBABE"));
        Runner.running(socketServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                Assert.assertThat(helper.send("0XCAFE"), CoreMatchers.is("0XBABE"));
                helper.close();
            }
        });
        String actual = Files.toString(file, Charset.defaultCharset());
        Assert.assertThat(actual, CoreMatchers.containsString("0XBABE"));
        Assert.assertThat(actual, CoreMatchers.containsString("0XCAFE"));
    }

    @Test
    public void should_monitor_socket_server_behavior() throws Exception {
        RequestHit hit = MocoRequestHit.requestHit();
        SocketServer socketServer = Moco.socketServer(RemoteTestUtils.port(), hit);
        socketServer.request(Moco.by("0XCAFE")).response(line("0XBABE"));
        Runner.running(socketServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                Assert.assertThat(helper.send("0XCAFE"), CoreMatchers.is("0XBABE"));
                helper.close();
            }
        });
        hit.verify(Moco.by("0XCAFE"), MocoRequestHit.once());
    }

    @Test
    public void should_create_socket_server_without_specific_port() throws Exception {
        final SocketServer socketServer = Moco.socketServer();
        socketServer.request(Moco.by("foo")).response(line("bar"));
        Runner.running(socketServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper = new MocoSocketHelper(RemoteTestUtils.local(), socketServer.port());
                helper.connect();
                Assert.assertThat(helper.send("foo"), CoreMatchers.is("bar"));
                helper.close();
            }
        });
    }

    @Test
    public void should_verify_expected_request_and_log_at_same_time() throws Exception {
        RequestHit hit = MocoRequestHit.requestHit();
        final SocketServer socketServer = Moco.socketServer(RemoteTestUtils.port(), hit, Moco.log());
        socketServer.request(Moco.by("foo")).response(line("bar"));
        Runner.running(socketServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper = new MocoSocketHelper(RemoteTestUtils.local(), socketServer.port());
                helper.connect();
                Assert.assertThat(helper.send("foo"), CoreMatchers.is("bar"));
                helper.close();
            }
        });
        hit.verify(Moco.by("foo"), MocoRequestHit.once());
    }
}

