/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.jita.allocator.tad;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.jita.allocator.impl.AtomicAllocator;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cache.ArrayDescriptor;
import org.nd4j.linalg.cache.TADManager;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.jcublas.context.CudaContext;


/**
 * Created by raver on 11.05.2016.
 */
public class BasicTADManagerTest {
    TADManager tadManager;

    @Test
    public void testTADcreation1() throws Exception {
        INDArray array = Nd4j.create(10, 100);
        DataBuffer tad = tadManager.getTADOnlyShapeInfo(array, new int[]{ 0 }).getFirst();
        System.out.println(("TAD: " + tad));
        System.out.println(("Shape: " + (array.shapeInfoDataBuffer())));
        Assert.assertEquals(2, tad.getInt(0));
        Assert.assertEquals(1, tad.getInt(1));
        Assert.assertEquals(10, tad.getInt(2));
        Assert.assertEquals(1, tad.getInt(3));
        Assert.assertEquals(100, tad.getInt(4));
        Assert.assertEquals(0, tad.getInt(5));
        Assert.assertEquals(100, tad.getInt(6));
        Assert.assertEquals(99, tad.getInt(7));
    }

    @Test
    public void testTADcreation2() throws Exception {
        INDArray array = Nd4j.create(10, 100);
        TADManager tadManager = new DeviceTADManager();
        DataBuffer tad = tadManager.getTADOnlyShapeInfo(array, new int[]{ 0 }).getFirst();
        DataBuffer tad2 = tadManager.getTADOnlyShapeInfo(array, new int[]{ 0 }).getFirst();
        System.out.println(("TAD: " + tad));
        System.out.println(("Shape: " + (array.shapeInfoDataBuffer())));
        CudaContext context = ((CudaContext) (AtomicAllocator.getInstance().getDeviceContext().getContext()));
        Assert.assertEquals(2, tad.getInt(0));
        Assert.assertEquals(1, tad.getInt(1));
        Assert.assertEquals(10, tad.getInt(2));
        Assert.assertEquals(1, tad.getInt(3));
        Assert.assertEquals(100, tad.getInt(4));
        Assert.assertEquals(0, tad.getInt(5));
        Assert.assertEquals(100, tad.getInt(6));
        Assert.assertEquals(99, tad.getInt(7));
        Assert.assertFalse(AtomicAllocator.getInstance().getAllocationPoint(tad).isActualOnDeviceSide());
        long tadPointer1 = AtomicAllocator.getInstance().getPointer(tad, context).address();
        long tadPointer2 = AtomicAllocator.getInstance().getPointer(tad2, context).address();
        Assert.assertTrue(AtomicAllocator.getInstance().getAllocationPoint(tad).isActualOnDeviceSide());
        System.out.println(("tadPointer1: " + tadPointer1));
        System.out.println(("tadPointer2: " + tadPointer2));
        Assert.assertEquals(tadPointer1, tadPointer2);
        AtomicAllocator.getInstance().moveToConstant(tad);
        long tadPointer3 = AtomicAllocator.getInstance().getPointer(tad, context).address();
        long tadPointer4 = AtomicAllocator.getInstance().getPointer(tad2, context).address();
        Assert.assertEquals(tadPointer4, tadPointer3);
        Assert.assertNotEquals(tadPointer1, tadPointer3);
    }

    @Test
    public void testArrayDesriptor1() throws Exception {
        ArrayDescriptor descriptor1 = new ArrayDescriptor(new int[]{ 2, 3, 4 });
        ArrayDescriptor descriptor2 = new ArrayDescriptor(new int[]{ 2, 4, 3 });
        ArrayDescriptor descriptor3 = new ArrayDescriptor(new int[]{ 3, 2, 4 });
        ArrayDescriptor descriptor4 = new ArrayDescriptor(new int[]{ 4, 2, 3 });
        ArrayDescriptor descriptor5 = new ArrayDescriptor(new int[]{ 4, 3, 2 });
        Assert.assertNotEquals(descriptor1, descriptor2);
        Assert.assertNotEquals(descriptor2, descriptor3);
        Assert.assertNotEquals(descriptor3, descriptor4);
        Assert.assertNotEquals(descriptor4, descriptor5);
        Assert.assertNotEquals(descriptor1, descriptor3);
        Assert.assertNotEquals(descriptor1, descriptor4);
        Assert.assertNotEquals(descriptor1, descriptor5);
        Assert.assertNotEquals(descriptor2, descriptor4);
        Assert.assertNotEquals(descriptor2, descriptor5);
    }
}

