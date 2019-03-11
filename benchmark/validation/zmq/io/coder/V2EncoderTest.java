package zmq.io.coder;


import java.nio.ByteBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Msg;
import zmq.util.Errno;
import zmq.util.ValueReference;


public class V2EncoderTest {
    private EncoderBase encoder = new zmq.io.coder.v2.V2Encoder(new Errno(), 64);

    @Test
    public void testReader() {
        Msg msg = readShortMessage();
        encoder.loadMsg(msg);
        ValueReference<ByteBuffer> ref = new ValueReference();
        int outsize = encoder.encode(ref, 0);
        ByteBuffer out = ref.get();
        Assert.assertThat(out, CoreMatchers.notNullValue());
        Assert.assertThat(outsize, CoreMatchers.is(7));
        Assert.assertThat(out.position(), CoreMatchers.is(7));
    }

    @Test
    public void testReaderLong() {
        Msg msg = readLongMessage1();
        ValueReference<ByteBuffer> ref = new ValueReference();
        int outsize = encoder.encode(ref, 0);
        Assert.assertThat(outsize, CoreMatchers.is(0));
        ByteBuffer out = ref.get();
        Assert.assertThat(out, CoreMatchers.nullValue());
        encoder.loadMsg(msg);
        outsize = encoder.encode(ref, 64);
        Assert.assertThat(outsize, CoreMatchers.is(64));
        out = ref.get();
        int position = out.position();
        int limit = out.limit();
        Assert.assertThat(limit, CoreMatchers.is(64));
        Assert.assertThat(position, CoreMatchers.is(64));
        ref.set(null);
        outsize = encoder.encode(ref, 64);
        Assert.assertThat(outsize, CoreMatchers.is(138));
        out = ref.get();
        position = out.position();
        limit = out.limit();
        Assert.assertThat(position, CoreMatchers.is(62));
        Assert.assertThat(limit, CoreMatchers.is(200));
    }
}

