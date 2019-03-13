package org.nd4j.jita.allocator.utils;


import DataBuffer.Type.DOUBLE;
import DataBuffer.Type.FLOAT;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.jita.allocator.impl.AllocationShape;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class AllocationUtilsTest {
    @Test
    public void testGetRequiredMemory1() throws Exception {
        AllocationShape shape = new AllocationShape();
        shape.setOffset(0);
        shape.setLength(10);
        shape.setStride(1);
        shape.setDataType(DOUBLE);
        Assert.assertEquals(80, AllocationUtils.getRequiredMemory(shape));
    }

    @Test
    public void testGetRequiredMemory2() throws Exception {
        AllocationShape shape = new AllocationShape();
        shape.setOffset(0);
        shape.setLength(10);
        shape.setStride(1);
        shape.setDataType(FLOAT);
        Assert.assertEquals(40, AllocationUtils.getRequiredMemory(shape));
    }

    @Test
    public void testGetRequiredMemory3() throws Exception {
        AllocationShape shape = new AllocationShape();
        shape.setOffset(0);
        shape.setLength(10);
        shape.setStride(2);
        shape.setDataType(FLOAT);
        Assert.assertEquals(80, AllocationUtils.getRequiredMemory(shape));
    }
}

