package org.traccar.protocol;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import org.junit.Assert;
import org.junit.Test;
import org.traccar.ProtocolTest;


public class AmplProgressProtocolDecoderTest extends ProtocolTest {
    @Test(timeout = 10000)
    public void testDecode() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ByteBuf o_testDecode__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode__4)).isDirect());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecode__4)).hasArray());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode__4)).hasMemoryAddress());
        Assert.assertEquals("UnpooledHeapByteBuf(ridx: 0, widx: 63, cap: 63/63)", ((UnpooledHeapByteBuf) (o_testDecode__4)).toString());
        Assert.assertEquals(-1557132967, ((int) (((UnpooledHeapByteBuf) (o_testDecode__4)).hashCode())));
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode__4)).isReadOnly());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecode__4)).isReadable());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode__4)).isWritable());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }

    @Test(timeout = 10000)
    public void testDecodelitString5() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ByteBuf o_testDecodelitString5__4 = binary("");
        Assert.assertFalse(((EmptyByteBuf) (o_testDecodelitString5__4)).isReadable());
        Assert.assertFalse(((EmptyByteBuf) (o_testDecodelitString5__4)).isWritable());
        Assert.assertTrue(((EmptyByteBuf) (o_testDecodelitString5__4)).hasMemoryAddress());
        Assert.assertEquals("EmptyByteBufBE", ((EmptyByteBuf) (o_testDecodelitString5__4)).toString());
        Assert.assertEquals(1, ((int) (((EmptyByteBuf) (o_testDecodelitString5__4)).hashCode())));
        Assert.assertTrue(((EmptyByteBuf) (o_testDecodelitString5__4)).isDirect());
        Assert.assertTrue(((EmptyByteBuf) (o_testDecodelitString5__4)).hasArray());
        Assert.assertFalse(((EmptyByteBuf) (o_testDecodelitString5__4)).isReadOnly());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }

    @Test(timeout = 10000)
    public void testDecodelitString6_failAssert4() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            binary("\n");
            org.junit.Assert.fail("testDecodelitString6 should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Odd number of characters.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodelitString5_add101() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ByteBuf o_testDecodelitString5__4 = binary("");
        ((EmptyByteBuf) (o_testDecodelitString5__4)).isReadOnly();
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }

    @Test(timeout = 10000)
    public void testDecode_add8litString38_failAssert8() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb7b6");
            ByteBuf o_testDecode_add8__5 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
            org.junit.Assert.fail("testDecode_add8litString38 should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Odd number of characters.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecode_add8_add145_add2508() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        ByteBuf o_testDecode_add8_add145_add2508__7 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).hasMemoryAddress());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).isDirect());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).hasArray());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).isWritable());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).isReadable());
        Assert.assertEquals("UnpooledHeapByteBuf(ridx: 0, widx: 63, cap: 63/63)", ((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).toString());
        Assert.assertEquals(-1557132967, ((int) (((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).hashCode())));
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).isReadOnly());
        ByteBuf o_testDecode_add8_add145__7 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        ByteBuf o_testDecode_add8__5 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).hasMemoryAddress());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).isDirect());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).hasArray());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).isWritable());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).isReadable());
        Assert.assertEquals("UnpooledHeapByteBuf(ridx: 0, widx: 63, cap: 63/63)", ((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).toString());
        Assert.assertEquals(-1557132967, ((int) (((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).hashCode())));
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode_add8_add145_add2508__7)).isReadOnly());
    }

    @Test(timeout = 10000)
    public void testDecodenull9_failAssert6_add124_add1656() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
            Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
            ((ProgressProtocolDecoder) (decoder)).isSharable();
            binary(null);
            binary(null);
            org.junit.Assert.fail("testDecodenull9 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDecode_add8_add145litString2462_failAssert31() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530[03136333832383531353535010000000100000000000000e6bb97b6");
            ByteBuf o_testDecode_add8_add145__7 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
            ByteBuf o_testDecode_add8__5 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
            org.junit.Assert.fail("testDecode_add8_add145litString2462 should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Illegal hexadecimal character [ at index 70", expected.getMessage());
        }
    }
}

