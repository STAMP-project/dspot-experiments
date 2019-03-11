package zmq.io.coder;


import ZError.InstantiationException;
import ZMQ.CHARSET;
import ZMQ.ZMQ_ENCODER;
import ZMQ.ZMQ_PAIR;
import java.nio.ByteBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Errno;
import zmq.util.ValueReference;


public class CustomEncoderTest {
    private Helper.DummySocketChannel sock = new Helper.DummySocketChannel();

    static class CustomEncoder extends EncoderBase {
        public static final boolean RAW_ENCODER = true;

        private final Runnable readHeader = new Runnable() {
            @Override
            public void run() {
                readHeader();
            }
        };

        private final Runnable readBody = new Runnable() {
            @Override
            public void run() {
                readBody();
            }
        };

        ByteBuffer header = ByteBuffer.allocate(10);

        public CustomEncoder(int bufsize, long maxmsgsize) {
            super(new Errno(), bufsize);
            initStep(readBody, true);
        }

        private void readHeader() {
            nextStep(inProgress, readBody, (!(inProgress.hasMore())));
        }

        private void readBody() {
            if ((inProgress) == null) {
                return;
            }
            header.clear();
            header.put("HEADER".getBytes(CHARSET));
            header.putInt(inProgress.size());
            header.flip();
            nextStep(header, header.limit(), readHeader, false);
        }
    }

    @Test
    public void testCustomEncoder() {
        CustomEncoderTest.CustomEncoder cencoder = new CustomEncoderTest.CustomEncoder(32, ((Integer.MAX_VALUE) / 2));
        Msg msg = new Msg("12345678901234567890".getBytes(CHARSET));
        cencoder.loadMsg(msg);
        ValueReference<ByteBuffer> ref = new ValueReference();
        int outsize = cencoder.encode(ref, 0);
        Assert.assertThat(outsize, CoreMatchers.is(30));
        ByteBuffer out = ref.get();
        out.flip();
        write(out);
        byte[] data = sock.data();
        Assert.assertThat(new String(data, 0, 6, ZMQ.CHARSET), CoreMatchers.is("HEADER"));
        Assert.assertThat(((int) (data[9])), CoreMatchers.is(20));
        Assert.assertThat(new String(data, 10, 20, ZMQ.CHARSET), CoreMatchers.is("12345678901234567890"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testAssignCustomEncoder() {
        Ctx ctx = ZMQ.createContext();
        SocketBase socket = ctx.createSocket(ZMQ_PAIR);
        boolean rc = socket.setSocketOpt(ZMQ_ENCODER, CustomEncoderTest.CustomEncoder.class);
        Assert.assertThat(rc, CoreMatchers.is(true));
        ZMQ.close(socket);
        ZMQ.term(ctx);
    }

    private static class WrongEncoder extends CustomEncoderTest.CustomEncoder {
        public WrongEncoder(int bufsize) {
            super(bufsize, 0);
        }
    }

    @SuppressWarnings("deprecation")
    @Test(expected = InstantiationException.class)
    public void testAssignWrongCustomEncoder() {
        Ctx ctx = ZMQ.createContext();
        SocketBase socket = ctx.createSocket(ZMQ_PAIR);
        try {
            socket.setSocketOpt(ZMQ_ENCODER, CustomEncoderTest.WrongEncoder.class);
        } finally {
            ZMQ.close(socket);
            ZMQ.term(ctx);
        }
    }
}

