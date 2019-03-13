package org.nd4j.jita.constant;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class ProtectedCudaConstantHandlerTest {
    @Test
    public void testPurge1() throws Exception {
        DataBuffer buffer = Nd4j.getConstantHandler().getConstantBuffer(new float[]{ 1, 2, 3, 4, 5 });
        ProtectedCudaConstantHandler handler = ((ProtectedCudaConstantHandler) (((CudaConstantHandler) (Nd4j.getConstantHandler())).wrappedHandler));
        Assert.assertEquals(1, handler.amountOfEntries(0));
        handler.purgeConstants();
        Assert.assertEquals(0, handler.amountOfEntries(0));
    }
}

