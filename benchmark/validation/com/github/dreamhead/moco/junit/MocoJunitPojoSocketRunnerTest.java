package com.github.dreamhead.moco.junit;


import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.helper.MocoSocketHelper;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MocoJunitPojoSocketRunnerTest {
    private static SocketServer server;

    static {
        MocoJunitPojoSocketRunnerTest.server = Moco.socketServer(12306);
        MocoJunitPojoSocketRunnerTest.server.response("bar\n");
    }

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.socketRunner(MocoJunitPojoSocketRunnerTest.server);

    private MocoSocketHelper helper;

    @Test
    public void should_return_expected_message() throws IOException {
        helper.connect();
        Assert.assertThat(helper.send("foo"), CoreMatchers.is("bar"));
        helper.close();
    }
}

