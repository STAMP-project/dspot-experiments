/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.dataset.primitive;


import java.io.Serializable;
import org.apache.ignite.ml.dataset.Dataset;
import org.apache.ignite.ml.math.functions.IgniteBiFunction;
import org.apache.ignite.ml.math.functions.IgniteBinaryOperator;
import org.apache.ignite.ml.math.functions.IgniteFunction;
import org.apache.ignite.ml.math.functions.IgniteTriFunction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link DatasetWrapper}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DatasetWrapperTest {
    /**
     * Mocked dataset.
     */
    @Mock
    private Dataset<Serializable, AutoCloseable> dataset;

    /**
     * Dataset wrapper.
     */
    private DatasetWrapper<Serializable, AutoCloseable> wrapper;

    /**
     * Tests {@code computeWithCtx()} method.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testComputeWithCtx() {
        Mockito.doReturn(42).when(dataset).computeWithCtx(ArgumentMatchers.any(IgniteTriFunction.class), ArgumentMatchers.any(), ArgumentMatchers.any());
        Integer res = ((Integer) (wrapper.computeWithCtx(Mockito.mock(IgniteTriFunction.class), Mockito.mock(IgniteBinaryOperator.class), null)));
        Assert.assertEquals(42, res.intValue());
        Mockito.verify(dataset, Mockito.times(1)).computeWithCtx(ArgumentMatchers.any(IgniteTriFunction.class), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    /**
     * Tests {@code computeWithCtx()} method.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testComputeWithCtx2() {
        Mockito.doReturn(42).when(dataset).computeWithCtx(ArgumentMatchers.any(IgniteTriFunction.class), ArgumentMatchers.any(), ArgumentMatchers.any());
        Integer res = ((Integer) (wrapper.computeWithCtx(Mockito.mock(IgniteBiFunction.class), Mockito.mock(IgniteBinaryOperator.class), null)));
        Assert.assertEquals(42, res.intValue());
        Mockito.verify(dataset, Mockito.times(1)).computeWithCtx(ArgumentMatchers.any(IgniteTriFunction.class), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    /**
     * Tests {@code computeWithCtx()} method.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testComputeWithCtx3() {
        wrapper.computeWithCtx(( ctx, data) -> {
            assertNotNull(ctx);
            assertNotNull(data);
        });
        Mockito.verify(dataset, Mockito.times(1)).computeWithCtx(ArgumentMatchers.any(IgniteTriFunction.class), ArgumentMatchers.any(IgniteBinaryOperator.class), ArgumentMatchers.any());
    }

    /**
     * Tests {@code compute()} method.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCompute() {
        Mockito.doReturn(42).when(dataset).compute(ArgumentMatchers.any(IgniteBiFunction.class), ArgumentMatchers.any(), ArgumentMatchers.any());
        Integer res = ((Integer) (wrapper.compute(Mockito.mock(IgniteBiFunction.class), Mockito.mock(IgniteBinaryOperator.class), null)));
        Assert.assertEquals(42, res.intValue());
        Mockito.verify(dataset, Mockito.times(1)).compute(ArgumentMatchers.any(IgniteBiFunction.class), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    /**
     * Tests {@code compute()} method.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCompute2() {
        Mockito.doReturn(42).when(dataset).compute(ArgumentMatchers.any(IgniteBiFunction.class), ArgumentMatchers.any(IgniteBinaryOperator.class), ArgumentMatchers.any());
        Integer res = ((Integer) (wrapper.compute(Mockito.mock(IgniteFunction.class), Mockito.mock(IgniteBinaryOperator.class), null)));
        Assert.assertEquals(42, res.intValue());
        Mockito.verify(dataset, Mockito.times(1)).compute(ArgumentMatchers.any(IgniteBiFunction.class), ArgumentMatchers.any(IgniteBinaryOperator.class), ArgumentMatchers.any());
    }

    /**
     * Tests {@code close()} method.
     */
    @Test
    public void testClose() throws Exception {
        wrapper.close();
        Mockito.verify(dataset, Mockito.times(1)).close();
    }
}

