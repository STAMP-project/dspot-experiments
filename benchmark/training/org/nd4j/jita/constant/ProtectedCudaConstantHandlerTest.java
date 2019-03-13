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

