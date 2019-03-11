package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoJsonSocketRunnerTest {
    private MocoSocketHelper helper;

    @Test
    public void should_return_expected_response() throws Exception {
        final SocketServer server = MocoJsonRunner.jsonSocketServer(port(), Moco.file("src/test/resources/base.json"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                Assert.assertThat(helper.send("foo", 3), CoreMatchers.is("bar"));
                Assert.assertThat(helper.send("anything", 4), CoreMatchers.is("blah"));
                helper.close();
            }
        });
    }

    @Test
    public void should_return_expected_response_without_port() throws Exception {
        final SocketServer server = MocoJsonRunner.jsonSocketServer(Moco.file("src/test/resources/base.json"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                MocoSocketHelper mocoSocketHelper = new MocoSocketHelper(local(), server.port());
                mocoSocketHelper.connect();
                Assert.assertThat(mocoSocketHelper.send("foo", 3), CoreMatchers.is("bar"));
                mocoSocketHelper.close();
            }
        });
    }
}

