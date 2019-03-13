package org.stagemonitor.tracing.profiler;


import org.junit.Assert;
import org.junit.Test;


public class CallStackElementTest {
    @Test
    public void printPercentAsBar() throws Exception {
        Assert.assertEquals("|||||-----", CallStackElement.printPercentAsBar(0.54, 10, false));
        // ??????????
        char[] chars = new char[]{ 9608, 9608, 9608, 9608, 9608, 9619, 9617, 9617, 9617, 9617 };
        Assert.assertEquals(new String(chars), CallStackElement.printPercentAsBar(0.56, 10, true));
    }

    @Test
    public void testGetShortSignature() {
        CallStackElement callStackElement = CallStackElement.createRoot("public void org.stagemonitor.tracing.profiler.CallStackElementTest.testGetShortSignature()");
        Assert.assertEquals("CallStackElementTest#testGetShortSignature", callStackElement.getShortSignature());
    }

    @Test
    public void testGetShortSignatureTotal() {
        CallStackElement callStackElement = CallStackElement.createRoot("total");
        Assert.assertNull(callStackElement.getShortSignature());
    }
}

