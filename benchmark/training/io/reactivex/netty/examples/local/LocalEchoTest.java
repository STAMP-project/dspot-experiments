package io.reactivex.netty.examples.local;


import io.reactivex.netty.examples.ExamplesTestUtil;
import java.util.Queue;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("travis doesn't like me")
public class LocalEchoTest {
    @Test(timeout = 60000)
    public void testEcho() throws Exception {
        Queue<String> output = ExamplesTestUtil.runClientInMockedEnvironment(LocalEcho.class);
        MatcherAssert.assertThat("Unexpected number of messages echoed", output, hasSize(1));
        MatcherAssert.assertThat("Unexpected number of messages echoed", output, contains("echo => Hello World!"));
    }
}

