package io.dropwizard.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;


public class ByteStreamsTest {
    @Test
    public void testToByteArray() throws IOException {
        byte[] input = new byte[4010];
        for (int i = 0; i < 4010; i++) {
            input[i] = 26;
        }
        assertThat(ByteStreams.toByteArray(new ByteArrayInputStream(input))).isEqualTo(input);
    }
}

