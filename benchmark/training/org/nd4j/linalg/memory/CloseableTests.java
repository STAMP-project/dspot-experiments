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
package org.nd4j.linalg.memory;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.NDArrayIndex;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class CloseableTests extends BaseNd4jTest {
    public CloseableTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testSimpleRelease_1() {
        val array = Nd4j.createFromArray(new float[]{ 1, 2, 3, 4, 5 });
        Assert.assertTrue(array.closeable());
        array.close();
        Assert.assertFalse(array.closeable());
    }

    @Test
    public void testCyclicRelease_1() {
        for (int e = 0; e < 1000; e++) {
            try (val array = Nd4j.createFromArray(new float[]{ 1, 2, 3, 4, 5 })) {
                array.addi(1.0F);
            }
            System.gc();
        }
    }

    @Test
    public void testViewRelease_1() {
        val array = Nd4j.create(5, 5);
        Assert.assertTrue(array.closeable());
        val view = array.get(NDArrayIndex.point(1), NDArrayIndex.all());
        Assert.assertTrue(array.closeable());
        Assert.assertFalse(view.closeable());
    }

    @Test
    public void testAttachedRelease_1() {
        val wsconf = WorkspaceConfiguration.builder().build();
        try (val ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(wsconf, "haha72yjhfdfs")) {
            val array = Nd4j.create(5, 5);
            Assert.assertFalse(array.closeable());
        }
    }
}

