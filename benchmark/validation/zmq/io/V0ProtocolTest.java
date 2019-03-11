package zmq.io;


import ZMQ.CHARSET;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Test;


public class V0ProtocolTest extends AbstractProtocolVersion {
    @Test
    public void testFixIssue524() throws IOException, InterruptedException {
        for (int idx = 0; idx < (AbstractProtocolVersion.REPETITIONS); ++idx) {
            if ((idx % 100) == 0) {
                System.out.print((idx + " "));
            }
            testProtocolVersion0short();
        }
        System.out.println();
    }

    @Test(timeout = 2000)
    public void testProtocolVersion0short() throws IOException, InterruptedException {
        ByteBuffer raw = // flags
        // send unversioned identity message
        // size
        ByteBuffer.allocate(11).put(((byte) (1))).put(((byte) (0)));
        // and payload
        raw.put(((byte) (8))).put(((byte) (0))).put("abcdefg".getBytes(CHARSET));
        assertProtocolVersion(0, raw, "abcdefg");
    }

    @Test(timeout = 2000)
    public void testProtocolVersion0long() throws IOException, InterruptedException {
        ByteBuffer raw = // identity
        // flags
        // size
        // send unversioned identity message
        // large message indicator
        ByteBuffer.allocate(35).put(((byte) (255))).put(new byte[7]).put(((byte) (9))).put(((byte) (0))).put("identity".getBytes(CHARSET));
        // and payload
        raw.put(((byte) (255))).put(new byte[7]).put(((byte) (8))).put(((byte) (0))).put("abcdefg".getBytes(CHARSET));
        assertProtocolVersion(0, raw, "abcdefg");
    }
}

