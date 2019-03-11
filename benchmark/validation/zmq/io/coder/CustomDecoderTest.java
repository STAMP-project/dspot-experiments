package zmq.io.coder;


import Step.Result;
import Step.Result.DECODED;
import ZError.InstantiationException;
import ZMQ.ZMQ_DECODER;
import ZMQ.ZMQ_PAIR;
import java.nio.ByteBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.io.coder.IDecoder.Step;
import zmq.msg.MsgAllocatorThreshold;
import zmq.util.Errno;
import zmq.util.ValueReference;


public class CustomDecoderTest {
    static class CustomDecoder extends Decoder {
        private final Step readHeader = new Step() {
            @Override
            public Result apply() {
                return readHeader();
            }
        };

        private final Step readBody = new Step() {
            @Override
            public Result apply() {
                return readBody();
            }
        };

        ByteBuffer header = ByteBuffer.allocate(10);

        Msg msg;

        int size = -1;

        public CustomDecoder(int bufsize, long maxmsgsize) {
            super(new Errno(), bufsize, maxmsgsize, new MsgAllocatorThreshold());
            nextStep(header, readHeader);
        }

        private Result readHeader() {
            byte[] headerBuff = new byte[6];
            header.position(0);
            header.get(headerBuff);
            Assert.assertThat(new String(headerBuff, 0, 6, ZMQ.CHARSET), CoreMatchers.is("HEADER"));
            ByteBuffer b = header.duplicate();
            b.position(6);
            size = b.getInt();
            msg = allocate(size);
            nextStep(msg, readBody);
            return Result.MORE_DATA;
        }

        private Result readBody() {
            nextStep(header, readHeader);
            return Result.DECODED;
        }
    }

    @Test
    public void testCustomDecoder() {
        CustomDecoderTest.CustomDecoder cdecoder = new CustomDecoderTest.CustomDecoder(32, 64);
        ByteBuffer in = getBuffer();
        int insize = readHeader(in);
        Assert.assertThat(insize, CoreMatchers.is(10));
        readBody(in);
        in.flip();
        ValueReference<Integer> processed = new ValueReference(0);
        Step.Result result = cdecoder.decode(in, 30, processed);
        Assert.assertThat(processed.get(), CoreMatchers.is(30));
        Assert.assertThat(cdecoder.size, CoreMatchers.is(20));
        Assert.assertThat(result, CoreMatchers.is(DECODED));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testAssignCustomDecoder() {
        Ctx ctx = ZMQ.createContext();
        SocketBase socket = ctx.createSocket(ZMQ_PAIR);
        boolean rc = socket.setSocketOpt(ZMQ_DECODER, CustomDecoderTest.CustomDecoder.class);
        Assert.assertThat(rc, CoreMatchers.is(true));
        ZMQ.close(socket);
        ZMQ.term(ctx);
    }

    private static class WrongDecoder extends CustomDecoderTest.CustomDecoder {
        public WrongDecoder(int bufsize) {
            super(bufsize, 0);
        }
    }

    @SuppressWarnings("deprecation")
    @Test(expected = InstantiationException.class)
    public void testAssignWrongCustomDecoder() {
        Ctx ctx = ZMQ.createContext();
        SocketBase socket = ctx.createSocket(ZMQ_PAIR);
        try {
            socket.setSocketOpt(ZMQ_DECODER, CustomDecoderTest.WrongDecoder.class);
        } finally {
            ZMQ.close(socket);
            ZMQ.term(ctx);
        }
    }
}

