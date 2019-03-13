package com.github.dreamhead.moco.internal;


import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.Runner;
import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ActualSocketServerTest {
    private MocoSocketHelper helper;

    @Test
    public void should_merge_socket_servers() throws Exception {
        SocketServer server = Moco.socketServer(12306);
        SocketServer secondServer = Moco.socketServer(12306);
        server.request(Moco.by("foo")).response(line("bar"));
        secondServer.request(Moco.by("foo1")).response(line("bar1"));
        SocketServer newServer = mergeServer(((ActualSocketServer) (secondServer)));
        Runner.running(newServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                Assert.assertThat(helper.send("foo"), CoreMatchers.is("bar"));
                Assert.assertThat(helper.send("foo1"), CoreMatchers.is("bar1"));
                helper.close();
            }
        });
    }

    @Test
    public void should_merge_socket_servers_with_first_port() throws Exception {
        SocketServer server = Moco.socketServer(12306);
        SocketServer secondServer = Moco.socketServer();
        final SocketServer newServer = mergeServer(((ActualSocketServer) (secondServer)));
        Runner.running(newServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(newServer.port(), CoreMatchers.is(12306));
            }
        });
    }

    @Test
    public void should_merge_socket_servers_with_second_port() throws Exception {
        SocketServer server = Moco.socketServer();
        SocketServer secondServer = Moco.socketServer(12307);
        final SocketServer newServer = mergeServer(((ActualSocketServer) (secondServer)));
        Runner.running(newServer, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(newServer.port(), CoreMatchers.is(12307));
            }
        });
    }

    @Test
    public void should_merge_socket_servers_without_ports_for_both_server() throws Exception {
        SocketServer server = Moco.socketServer();
        SocketServer secondServer = Moco.socketServer();
        final ActualSocketServer newServer = ((ActualSocketServer) (server)).mergeServer(((ActualSocketServer) (secondServer)));
        Assert.assertThat(newServer.getPort().isPresent(), CoreMatchers.is(false));
    }
}

