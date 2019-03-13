package org.mp4parser.tools.boxes.fragment;


import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Test;
import org.mp4parser.boxes.iso14496.part12.SampleFlags;


public class TrackRunBoxTest {
    @Test
    public void test() throws IOException {
        testAllFlagsWithDataOffset(new SampleFlags(ByteBuffer.wrap(new byte[]{ 32, 3, 65, 127 })));
        testAllFlagsWithDataOffset(null);
    }
}

