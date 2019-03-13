package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import org.apache.http.client.HttpResponseException;
import org.junit.Test;


public class MocoDefenseTest extends AbstractMocoHttpTest {
    @Test(expected = HttpResponseException.class)
    public void should_work_well_without_response_setting() throws Exception {
        server = Moco.httpServer(12306, Moco.context("/foo"));
        server.request(Moco.by("bar"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.postContent(RemoteTestUtils.root(), "bar");
            }
        });
    }
}

