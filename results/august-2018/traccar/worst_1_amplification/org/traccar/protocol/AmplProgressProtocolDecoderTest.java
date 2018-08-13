package org.traccar.protocol;


import io.netty.buffer.ByteBuf;
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
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode__4)).hasMemoryAddress());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode__4)).isDirect());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecode__4)).hasArray());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecode__4)).isReadable());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode__4)).isWritable());
        Assert.assertEquals("UnpooledHeapByteBuf(ridx: 0, widx: 63, cap: 63/63)", ((UnpooledHeapByteBuf) (o_testDecode__4)).toString());
        Assert.assertEquals(-1557132967, ((int) (((UnpooledHeapByteBuf) (o_testDecode__4)).hashCode())));
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecode__4)).isReadOnly());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }

    @Test(timeout = 10000)
    public void testDecodelitString7_failAssert5() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            binary(":");
            org.junit.Assert.fail("testDecodelitString7 should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Odd number of characters.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodenull11() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ByteBuf o_testDecodenull11__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecodenull11__4)).hasMemoryAddress());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecodenull11__4)).isDirect());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecodenull11__4)).hasArray());
        Assert.assertTrue(((UnpooledHeapByteBuf) (o_testDecodenull11__4)).isReadable());
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecodenull11__4)).isWritable());
        Assert.assertEquals("UnpooledHeapByteBuf(ridx: 0, widx: 63, cap: 63/63)", ((UnpooledHeapByteBuf) (o_testDecodenull11__4)).toString());
        Assert.assertEquals(-1557132967, ((int) (((UnpooledHeapByteBuf) (o_testDecodenull11__4)).hashCode())));
        Assert.assertFalse(((UnpooledHeapByteBuf) (o_testDecodenull11__4)).isReadOnly());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }

    @Test(timeout = 10000)
    public void testDecode_add8null306() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        ByteBuf o_testDecode_add8__5 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }

    @Test(timeout = 10000)
    public void testDecode_add8litString72_failAssert13() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
            ByteBuf o_testDecode_add8__5 = binary("020037000100000003003131310f003335343836383035313339303036320f0323530303136333832383531353535010000000100000000000000e6bb97b6");
            org.junit.Assert.fail("testDecode_add8litString72 should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Odd number of characters.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecode_add8_add194null6631() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        ByteBuf o_testDecode_add8_add194__7 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        ByteBuf o_testDecode_add8__5 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }

    @Test(timeout = 10000)
    public void testDecode_add8_add194litString6333_failAssert45() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            ByteBuf o_testDecode_add8__4 = binary("\n");
            ByteBuf o_testDecode_add8_add194__7 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
            ByteBuf o_testDecode_add8__5 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
            org.junit.Assert.fail("testDecode_add8_add194litString6333 should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Odd number of characters.", expected.getMessage());
        }
    }
}

