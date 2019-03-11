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
package org.nd4j.jita.flow.impl;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.jita.allocator.impl.AllocationPoint;
import org.nd4j.jita.allocator.impl.AtomicAllocator;
import org.nd4j.jita.conf.Configuration;
import org.nd4j.jita.conf.CudaEnvironment;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.jcublas.context.CudaContext;


/**
 * This set of tests validates async flow controller behavior on atomic level
 *
 * @author raver119@gmail.com
 */
public class AsynchronousFlowControllerTest {
    private AtomicAllocator allocator;

    private AsynchronousFlowController controller;

    @Test
    public void testDependencies1() throws Exception {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F });
        // we use synchronization to make sure it completes activeWrite caused by array creation
        String arrayContents = array.toString();
        AllocationPoint point = allocator.getAllocationPoint(array);
        assertPointHasNoDependencies(point);
    }

    @Test
    public void testDependencies2() throws Exception {
        INDArray arrayWrite = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F });
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F });
        // we use synchronization to make sure it completes activeWrite caused by array creation
        String arrayContents = array.toString();
        AllocationPoint point = allocator.getAllocationPoint(array);
        assertPointHasNoDependencies(point);
        CudaContext context = controller.prepareAction(arrayWrite, array);
        controller.registerAction(context, arrayWrite, array);
        Assert.assertTrue(controller.hasActiveReads(point));
        Assert.assertEquals((-1), controller.hasActiveWrite(point));
    }

    @Test
    public void testDependencies3() throws Exception {
        INDArray arrayWrite = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F });
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F });
        // we use synchronization to make sure it completes activeWrite caused by array creation
        String arrayContents = array.toString();
        AllocationPoint point = allocator.getAllocationPoint(array);
        AllocationPoint pointWrite = allocator.getAllocationPoint(arrayWrite);
        assertPointHasNoDependencies(point);
        CudaContext context = controller.prepareAction(arrayWrite, array);
        controller.registerAction(context, arrayWrite, array);
        Assert.assertTrue(controller.hasActiveReads(point));
        Assert.assertFalse(controller.hasActiveReads(pointWrite));
        Assert.assertNotEquals((-1), controller.hasActiveWrite(pointWrite));
        controller.synchronizeReadLanes(point);
        assertPointHasNoDependencies(point);
        Assert.assertEquals((-1), controller.hasActiveWrite(pointWrite));
    }

    @Test
    public void testDependencies4() throws Exception {
        INDArray arrayWrite = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F });
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F });
        // we use synchronization to make sure it completes activeWrite caused by array creation
        String arrayContents = array.toString();
        AllocationPoint point = allocator.getAllocationPoint(array);
        AllocationPoint pointWrite = allocator.getAllocationPoint(arrayWrite);
        assertPointHasNoDependencies(point);
        controller.cutTail();
        CudaContext context = controller.prepareAction(arrayWrite, array);
        controller.registerAction(context, arrayWrite, array);
        Assert.assertTrue(controller.hasActiveReads(point));
        Assert.assertFalse(controller.hasActiveReads(pointWrite));
        Assert.assertNotEquals((-1), controller.hasActiveWrite(pointWrite));
        Configuration configuration = CudaEnvironment.getInstance().getConfiguration();
        controller.sweepTail();
        Assert.assertTrue(controller.hasActiveReads(point));
        Assert.assertFalse(controller.hasActiveReads(pointWrite));
        Assert.assertNotEquals((-1), controller.hasActiveWrite(pointWrite));
        controller.sweepTail();
        Assert.assertTrue(controller.hasActiveReads(point));
        Assert.assertFalse(controller.hasActiveReads(pointWrite));
        Assert.assertNotEquals((-1), controller.hasActiveWrite(pointWrite));
        for (int i = 0; i < (configuration.getCommandQueueLength()); i++)
            controller.sweepTail();

        assertPointHasNoDependencies(point);
        assertPointHasNoDependencies(pointWrite);
    }
}

