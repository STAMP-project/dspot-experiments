package com.github.dreamhead.moco.internal;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.google.common.base.Optional;
import org.junit.Test;


public class MocoHttpServerTest {
    @Test
    public void should_stop_stoped_server_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(Optional.of(RemoteTestUtils.port())));
        server.stop();
    }

    @Test
    public void should_stop_server_many_times_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(Optional.of(RemoteTestUtils.port())));
        server.start();
        server.stop();
        server.stop();
    }
}

