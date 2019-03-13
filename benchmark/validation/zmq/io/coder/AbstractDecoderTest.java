package zmq.io.coder;


import Step.Result;
import Step.Result.DECODED;
import Step.Result.MORE_DATA;
import ZMQ.CHARSET;
import java.nio.ByteBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Msg;
import zmq.io.coder.IDecoder.Step;
import zmq.util.ValueReference;


public abstract class AbstractDecoderTest {
    DecoderBase decoder;

    @Test
    public void testReader() {
        ByteBuffer in = decoder.getBuffer();
        int insize = readShortMessage(in);
        Assert.assertThat(insize, CoreMatchers.is(7));
        in.flip();
        ValueReference<Integer> process = new ValueReference(0);
        decoder.decode(in, insize, process);
        Assert.assertThat(process.get(), CoreMatchers.is(7));
        Msg msg = decoder.msg();
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.flags(), CoreMatchers.is(1));
    }

    @Test
    public void testReaderLong() {
        ByteBuffer in = decoder.getBuffer();
        int insize = readLongMessage1(in);
        Assert.assertThat(insize, CoreMatchers.is(64));
        in.flip();
        ValueReference<Integer> process = new ValueReference(0);
        decoder.decode(in, insize, process);
        Assert.assertThat(process.get(), CoreMatchers.is(64));
        in = decoder.getBuffer();
        Assert.assertThat(in.capacity(), CoreMatchers.is(200));
        Assert.assertThat(in.position(), CoreMatchers.is(62));
        in.put("23456789".getBytes(CHARSET));
        insize = readLongMessage2(in);
        Assert.assertThat(insize, CoreMatchers.is(200));
        decoder.decode(in, 138, process);
        Assert.assertThat(process.get(), CoreMatchers.is(138));
        Assert.assertThat(in.array()[199], CoreMatchers.is(((byte) ('x'))));
        Msg msg = decoder.msg();
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        byte last = msg.data()[199];
        Assert.assertThat(last, CoreMatchers.is(((byte) ('x'))));
        Assert.assertThat(msg.size(), CoreMatchers.is(200));
        Assert.assertThat(msg.flags(), CoreMatchers.is(1));
    }

    @Test
    public void testReaderExtraLong() {
        ByteBuffer in = decoder.getBuffer();
        int insize = readExtraLongMessage(in);
        // assertThat(insize, is(62));
        in.flip();
        ValueReference<Integer> process = new ValueReference(0);
        decoder.decode(in, insize, process);
        Assert.assertThat(process.get(), CoreMatchers.is(insize));
        in = decoder.getBuffer();
        Assert.assertThat(in.capacity(), CoreMatchers.is(330));
        Assert.assertThat(in.position(), CoreMatchers.is(52));
        in.put("23456789".getBytes(CHARSET));
        in.put("0123456789".getBytes(CHARSET));
        insize = readLongMessage2(in);
        Assert.assertThat(insize, CoreMatchers.is(200));
        insize = readLongMessage2(in);
        Assert.assertThat(insize, CoreMatchers.is(330));
        decoder.decode(in, 278, process);
        Assert.assertThat(process.get(), CoreMatchers.is(278));
        Assert.assertThat(in.array()[329], CoreMatchers.is(((byte) ('x'))));
        Msg msg = decoder.msg();
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        byte last = msg.data()[329];
        Assert.assertThat(last, CoreMatchers.is(((byte) ('x'))));
        Assert.assertThat(msg.size(), CoreMatchers.is(330));
    }

    @Test
    public void testReaderMultipleMsg() {
        ByteBuffer in = decoder.getBuffer();
        int insize = readShortMessage(in);
        Assert.assertThat(insize, CoreMatchers.is(7));
        readShortMessage(in);
        in.flip();
        ValueReference<Integer> processed = new ValueReference(0);
        decoder.decode(in, 14, processed);
        Assert.assertThat(processed.get(), CoreMatchers.is(7));
        Assert.assertThat(in.position(), CoreMatchers.is(7));
        Assert.assertThat(decoder.msg(), CoreMatchers.notNullValue());
        Step.Result result = decoder.decode(in, 6, processed);
        Assert.assertThat(processed.get(), CoreMatchers.is(6));
        Assert.assertThat(in.position(), CoreMatchers.is(13));
        Assert.assertThat(result, CoreMatchers.is(MORE_DATA));
        result = decoder.decode(in, 10, processed);
        Assert.assertThat(processed.get(), CoreMatchers.is(1));
        Assert.assertThat(in.position(), CoreMatchers.is(14));
        Assert.assertThat(result, CoreMatchers.is(DECODED));
        Assert.assertThat(decoder.msg(), CoreMatchers.notNullValue());
    }
}

