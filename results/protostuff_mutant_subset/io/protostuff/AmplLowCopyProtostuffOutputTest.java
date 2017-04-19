

package io.protostuff;


/**
 * @author Ryan Rawson
 */
public class AmplLowCopyProtostuffOutputTest extends junit.framework.TestCase {
    public void testCompareVsOther() throws java.lang.Exception {
        io.protostuff.Baz aBaz = new io.protostuff.Baz(1, "hello world", 1238372479L);
        java.nio.ByteBuffer serForm1 = testObj(aBaz, aBaz);
        deserTest(aBaz, aBaz, serForm1);
        io.protostuff.Bar testBar = new io.protostuff.Bar(22, "some String", aBaz, io.protostuff.Bar.Status.COMPLETED, io.protostuff.ByteString.wrap("fuck yo test".getBytes()), false, 3.14F, 2.7182818284, 599L);
        java.nio.ByteBuffer serForm2 = testObj(testBar, testBar);
        deserTest(testBar, testBar, serForm2);
    }

    private void deserTest(io.protostuff.Message origMsg, io.protostuff.Schema sch, java.nio.ByteBuffer buf) throws java.io.IOException {
        io.protostuff.ByteBufferInput input = new io.protostuff.ByteBufferInput(buf, true);
        java.lang.Object newM = sch.newMessage();
        sch.mergeFrom(input, newM);
        junit.framework.TestCase.assertEquals(origMsg, newM);
    }

    private java.nio.ByteBuffer testObj(io.protostuff.Message msg, io.protostuff.Schema sch) throws java.io.IOException {
        // do protostuff now:
        java.io.ByteArrayOutputStream controlStream = new java.io.ByteArrayOutputStream();
        io.protostuff.LinkedBuffer linkedBuffer = io.protostuff.LinkedBuffer.allocate(512);// meh
        
        io.protostuff.ProtostuffIOUtil.writeTo(controlStream, msg, sch, linkedBuffer);
        byte[] controlData = controlStream.toByteArray();
        // now my new serialization:
        io.protostuff.LowCopyProtostuffOutput lcpo = new io.protostuff.LowCopyProtostuffOutput();
        sch.writeTo(lcpo, msg);
        java.util.List<java.nio.ByteBuffer> testDatas = lcpo.buffer.finish();
        junit.framework.TestCase.assertEquals(1, testDatas.size());
        java.nio.ByteBuffer testData = testDatas.get(0);
        byte[] testByteArray = new byte[testData.remaining()];
        testData.get(testByteArray);
        junit.framework.TestCase.assertTrue(java.util.Arrays.equals(controlData, testByteArray));
        java.lang.System.out.println(("ctrl len = " + (controlData.length)));
        java.lang.System.out.println(("test len = " + (testByteArray.length)));
        java.lang.System.out.println(("test size() = " + (lcpo.buffer.size())));
        java.lang.System.out.println(("test buff count = " + (lcpo.buffer.buffers.size())));
        testData.rewind();
        return testData;
    }
}

