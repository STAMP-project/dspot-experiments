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
    public void testDecodelitString4_failAssert1() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            binary("GdhscbCS@!x*zH_,y(q2 5[gpbL[{$QV5:Wz2[|+mr6#-VtX(r!Fs2l>UgIvC=TU&zgYc TM1`_8;0L`A=SO/woO!OKS@Rl&{ha!&Bcvg[?i!rb0/|]6^FT)-ef&bk");
            org.junit.Assert.fail("testDecodelitString4 should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Illegal hexadecimal character G at index 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodelitString5() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ByteBuf o_testDecodelitString5__4 = binary("");
        Assert.assertTrue(((EmptyByteBuf) (o_testDecodelitString5__4)).hasMemoryAddress());
        Assert.assertFalse(((EmptyByteBuf) (o_testDecodelitString5__4)).isReadable());
        Assert.assertFalse(((EmptyByteBuf) (o_testDecodelitString5__4)).isWritable());
        Assert.assertEquals("EmptyByteBufBE", ((EmptyByteBuf) (o_testDecodelitString5__4)).toString());
        Assert.assertEquals(1, ((int) (((EmptyByteBuf) (o_testDecodelitString5__4)).hashCode())));
        Assert.assertTrue(((EmptyByteBuf) (o_testDecodelitString5__4)).isDirect());
        Assert.assertTrue(((EmptyByteBuf) (o_testDecodelitString5__4)).hasArray());
        Assert.assertFalse(((EmptyByteBuf) (o_testDecodelitString5__4)).isReadOnly());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }

    @Test(timeout = 10000)
    public void testDecode_add8litString41() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        ByteBuf o_testDecode_add8__5 = binary("");
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }

    @Test(timeout = 10000)
    public void testDecode_add8litString40_failAssert16() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
            ByteBuf o_testDecode_add8__5 = binary("+{5@T5!^MYU(dM7KJ&><6ycw,-c^.vZ(8(U^r,Jp9Flz5*yC=M]:bMoV#NG^1yAAF?5P&+YTN/#yO[*WW4JN-$nw<}7EGpwmm(EQndBdj-qEHp!#I]LDWP=,y4JV)d");
            org.junit.Assert.fail("testDecode_add8litString40 should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Illegal hexadecimal character + at index 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecode_add8_add77litString564_failAssert27() throws Exception {
        try {
            ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
            ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
            ByteBuf o_testDecode_add8__5 = binary("\n");
            ((ProgressProtocolDecoder) (decoder)).getProtocolName();
            org.junit.Assert.fail("testDecode_add8_add77litString564 should have thrown RuntimeException");
        } catch (RuntimeException expected) {
            Assert.assertEquals("org.apache.commons.codec.DecoderException: Odd number of characters.", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecode_add8_add77_add1821() throws Exception {
        ProgressProtocolDecoder decoder = new ProgressProtocolDecoder(new ProgressProtocol());
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
        ((ProgressProtocolDecoder) (decoder)).getProtocolName();
        ByteBuf o_testDecode_add8__4 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        ByteBuf o_testDecode_add8__5 = binary("020037000100000003003131310f003335343836383035313339303036320f00323530303136333832383531353535010000000100000000000000e6bb97b6");
        ((ProgressProtocolDecoder) (decoder)).getProtocolName();
        Assert.assertEquals("progress", ((ProgressProtocolDecoder) (decoder)).getProtocolName());
        Assert.assertFalse(((ProgressProtocolDecoder) (decoder)).isSharable());
    }
}

