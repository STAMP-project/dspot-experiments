package org.nd4j.jita.memory.impl;


import AllocationStatus.DEVICE;
import AllocationStatus.HOST;
import DataBuffer.Type;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.jita.allocator.impl.AllocationPoint;
import org.nd4j.jita.allocator.impl.AllocationShape;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class CudaDirectProviderTest {
    @Test
    public void mallocHost() throws Exception {
        CudaDirectProvider provider = new CudaDirectProvider();
        AllocationShape shape = new AllocationShape(100000, 4, Type.FLOAT);
        AllocationPoint point = new AllocationPoint();
        point.setShape(shape);
        point.setPointers(provider.malloc(shape, point, HOST));
        System.out.println("Allocated...");
        Thread.sleep(1000);
        provider.free(point);
        System.out.println("Deallocated...");
        Thread.sleep(1000);
    }

    @Test
    public void mallocDevice() throws Exception {
        CudaDirectProvider provider = new CudaDirectProvider();
        AllocationShape shape = new AllocationShape(300000, 4, Type.FLOAT);
        AllocationPoint point = new AllocationPoint();
        point.setShape(shape);
        point.setPointers(provider.malloc(shape, point, DEVICE));
        System.out.println("Allocated...");
        Thread.sleep(1000);
        point.setAllocationStatus(DEVICE);
        provider.free(point);
        System.out.println("Deallocated...");
        Thread.sleep(1000);
    }
}

