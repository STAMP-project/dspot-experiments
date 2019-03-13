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
package org.nd4j.jita.allocator.utils;


import DataType.DOUBLE;
import DataType.FLOAT;
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

