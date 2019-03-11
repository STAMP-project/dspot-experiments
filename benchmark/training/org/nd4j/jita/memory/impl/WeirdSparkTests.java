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
package org.nd4j.jita.memory.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * This set of tests targets special concurrent environments, like Spark.
 * In this kind of environments, data pointers might be travelling across different threads
 *
 * PLEASE NOTE: This set of tests worth running on multi-gpu systems only. Running them on single-gpu system, will just show "PASSED" for everything.
 *
 * @author raver119@gmail.com
 */
@Ignore
public class WeirdSparkTests {
    @Test
    public void testMultithreaded1() throws Exception {
        final INDArray array1 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        float sum = array1.sumNumber().floatValue();
        Assert.assertEquals(15.0F, sum, 0.001F);
        sum = array1.sumNumber().floatValue();
        Assert.assertEquals(15.0F, sum, 0.001F);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("--------------------------------------------");
                System.out.println("           External thread started");
                array1.putScalar(0, 0.0F);
                float sum = array1.sumNumber().floatValue();
                Assert.assertEquals(14.0F, sum, 0.001F);
            }
        });
        Nd4j.getAffinityManager().attachThreadToDevice(thread, 1);
        thread.start();
        thread.join();
        System.out.println("--------------------------------------------");
        System.out.println("            Back to main thread");
        sum = array1.sumNumber().floatValue();
        Assert.assertEquals(14.0F, sum, 0.001F);
    }

    @Test
    public void testMultithreadedDup1() throws Exception {
        final INDArray array1 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        float sum = array1.sumNumber().floatValue();
        Assert.assertEquals(15.0F, sum, 0.001F);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("--------------------------------------------");
                System.out.println("           External thread started");
                INDArray array = array1.dup();
                float sum = array.sumNumber().floatValue();
                Assert.assertEquals(15.0F, sum, 0.001F);
            }
        });
        Nd4j.getAffinityManager().attachThreadToDevice(thread, 1);
        thread.start();
        thread.join();
        sum = array1.sumNumber().floatValue();
        Assert.assertEquals(15.0F, sum, 0.001F);
    }

    @Test
    public void testMultithreadedDup2() throws Exception {
        final INDArray array1 = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("--------------------------------------------");
                System.out.println("           External thread started");
                INDArray array = array1.dup();
                float sum = array.sumNumber().floatValue();
                Assert.assertEquals(15.0F, sum, 0.001F);
            }
        });
        Nd4j.getAffinityManager().attachThreadToDevice(thread, 1);
        thread.start();
        thread.join();
        float sum = array1.sumNumber().floatValue();
        Assert.assertEquals(15.0F, sum, 0.001F);
    }

    @Test
    public void testMultithreadedViews1() throws Exception {
        final INDArray array = Nd4j.ones(10, 10);
        final INDArray view = array.getRow(1);
        Assert.assertEquals(1.0F, view.getFloat(0), 0.01F);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(1.0F, view.getFloat(0), 0.01F);
                view.subi(1.0F);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    // 
                }
                System.out.println(view);
            }
        });
        Nd4j.getAffinityManager().attachThreadToDevice(thread, 1);
        thread.start();
        thread.join();
        // System.out.println(view);
        Assert.assertEquals(0.0F, view.getFloat(0), 0.01F);
    }

    @Test
    public void testMultithreadedRandom1() throws Exception {
        // for (int i = 0; i < 5; i++) {
        // System.out.println("Starting iteration " + i);
        final List<INDArray> holder = new ArrayList<>();
        final AtomicLong failures = new AtomicLong(0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                holder.add(Nd4j.ones(10));
            }
        });
        Nd4j.getAffinityManager().attachThreadToDevice(thread, 1);
        thread.start();
        thread.join();
        Thread[] threads = new Thread[100];
        for (int x = 0; x < (threads.length); x++) {
            threads[x] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        INDArray array = holder.get(0).dup();
                        // ((CudaGridExecutioner) Nd4j.getExecutioner()).flushQueueBlocking();
                    } catch (Exception e) {
                        failures.incrementAndGet();
                        throw new RuntimeException(e);
                    }
                }
            });
            threads[x].start();
        }
        for (int x = 0; x < (threads.length); x++) {
            threads[x].join();
        }
        Assert.assertEquals(0, failures.get());
        // }
    }
}

