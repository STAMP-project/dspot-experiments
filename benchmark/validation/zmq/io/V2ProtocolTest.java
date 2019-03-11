package zmq.io;


import ZMQ.CHARSET;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Test;
import zmq.util.Wire;


public class V2ProtocolTest extends AbstractProtocolVersion {
    @Test
    public void testFixIssue524() throws IOException, InterruptedException {
        for (int idx = 0; idx < (AbstractProtocolVersion.REPETITIONS); ++idx) {
            if ((idx % 100) == 0) {
                System.out.print((idx + " "));
            }
            testProtocolVersion2short();
        }
        System.out.println();
    }

    @Test
    public void testProtocolVersion2short() throws IOException, InterruptedException {
        List<ByteBuffer> raws = raws(1);
        raws.add(identity());
        ByteBuffer raw = // payload
        // length
        // flag
        ByteBuffer.allocate(9).put(((byte) (0))).put(((byte) (7))).put("abcdefg".getBytes(CHARSET));
        raws.add(raw);
        assertProtocolVersion(2, raws, "abcdefg");
    }

    @Test
    public void testProtocolVersion2long() throws IOException, InterruptedException {
        List<ByteBuffer> raws = raws(1);
        raws.add(identity());
        ByteBuffer raw = ByteBuffer.allocate(17);
        // flag
        raw.put(((byte) (2)));
        // length
        Wire.putUInt64(raw, 8);
        // payload
        raw.put("abcdefgh".getBytes(CHARSET));
        raws.add(raw);
        assertProtocolVersion(2, raws, "abcdefgh");
    }
}

