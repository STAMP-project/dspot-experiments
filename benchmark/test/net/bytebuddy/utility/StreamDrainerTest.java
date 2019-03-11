package net.bytebuddy.utility;


import java.io.ByteArrayInputStream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class StreamDrainerTest {
    @Test
    public void testDrainage() throws Exception {
        byte[] input = new byte[]{ 1, 2, 3, 4 };
        MatcherAssert.assertThat(new StreamDrainer(1).drain(new ByteArrayInputStream(input)), CoreMatchers.is(input));
    }
}

