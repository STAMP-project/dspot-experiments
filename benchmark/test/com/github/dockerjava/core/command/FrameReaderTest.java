package com.github.dockerjava.core.command;


import com.github.dockerjava.api.model.StreamType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FrameReaderTest {
    public static final int HEADER_SIZE = 8;

    private final List<Integer> bytes = new ArrayList<>();

    private final InputStream inputStream = new InputStream() {
        @Override
        public int read() throws IOException {
            return bytes.isEmpty() ? -1 : bytes.remove(0);
        }
    };

    private final FrameReader frameReader = new FrameReader(inputStream);

    @Test
    public void endOfStreamReturnsNull() throws Exception {
        Assert.assertNull(nextFrame());
    }

    @Test
    public void stdInBytesFrameReturnsFrame() throws Exception {
        Assert.assertEquals(nextFrame(0, 0, 0, 0, 0, 0, 0, 0), new com.github.dockerjava.api.model.Frame(StreamType.STDIN, new byte[0]));
    }

    @Test
    public void stdOutBytesFrameReturnsFrame() throws Exception {
        Assert.assertEquals(nextFrame(1, 0, 0, 0, 0, 0, 0, 0), new com.github.dockerjava.api.model.Frame(StreamType.STDOUT, new byte[0]));
    }

    @Test
    public void stdErrBytesFrameReturnsFrame() throws Exception {
        Assert.assertEquals(nextFrame(2, 0, 0, 0, 0, 0, 0, 0), new com.github.dockerjava.api.model.Frame(StreamType.STDERR, new byte[0]));
    }
}

