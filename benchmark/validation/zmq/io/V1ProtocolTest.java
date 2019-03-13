package zmq.io;


import ZMQ.CHARSET;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Test;


public class V1ProtocolTest extends AbstractProtocolVersion {
    @Test
    public void testFixIssue524() throws IOException, InterruptedException {
        for (int idx = 0; idx < (AbstractProtocolVersion.REPETITIONS); ++idx) {
            if ((idx % 100) == 0) {
                System.out.print((idx + " "));
            }
            testProtocolVersion1short();
        }
        System.out.println();
    }

    @Test
    public void testProtocolVersion1short() throws IOException, InterruptedException {
        List<ByteBuffer> raws = raws(0);
        raws.add(identity());
        // payload
        ByteBuffer raw = ByteBuffer.allocate(9);
        raw.put(((byte) (8))).put(((byte) (0))).put("abcdefg".getBytes(CHARSET));
        raws.add(raw);
        assertProtocolVersion(1, raws, "abcdefg");
    }

    @Test
    public void testProtocolVersion1long() throws IOException, InterruptedException {
        List<ByteBuffer> raws = raws(0);
        raws.add(identity());
        // payload
        ByteBuffer raw = ByteBuffer.allocate(17);
        raw.put(((byte) (255))).put(new byte[7]).put(((byte) (8))).put(((byte) (0))).put("abcdefg".getBytes(CHARSET));
        raws.add(raw);
        assertProtocolVersion(1, raws, "abcdefg");
    }
}

