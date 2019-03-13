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
package org.deeplearning4j.parallelism;


import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.parallel.MultiBoolean;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class MultiBooleanTest extends BaseDL4JTest {
    @Test
    public void testBoolean1() throws Exception {
        MultiBoolean bool = new MultiBoolean(5);
        Assert.assertTrue(bool.allFalse());
        Assert.assertFalse(bool.allTrue());
    }

    @Test
    public void testBoolean2() throws Exception {
        MultiBoolean bool = new MultiBoolean(5);
        bool.set(true, 2);
        Assert.assertFalse(bool.allFalse());
        Assert.assertFalse(bool.allTrue());
    }

    @Test
    public void testBoolean3() throws Exception {
        MultiBoolean bool = new MultiBoolean(5);
        bool.set(true, 0);
        bool.set(true, 1);
        bool.set(true, 2);
        bool.set(true, 3);
        Assert.assertFalse(bool.allTrue());
        bool.set(true, 4);
        Assert.assertFalse(bool.allFalse());
        Assert.assertTrue(bool.allTrue());
        bool.set(false, 2);
        Assert.assertFalse(bool.allTrue());
        bool.set(true, 2);
        Assert.assertTrue(bool.allTrue());
    }

    @Test
    public void testBoolean4() throws Exception {
        MultiBoolean bool = new MultiBoolean(5, true);
        Assert.assertTrue(bool.get(1));
        bool.set(false, 1);
        Assert.assertFalse(bool.get(1));
    }

    @Test
    public void testBoolean5() throws Exception {
        MultiBoolean bool = new MultiBoolean(5, true, true);
        for (int i = 0; i < 5; i++) {
            bool.set(false, i);
        }
        for (int i = 0; i < 5; i++) {
            bool.set(true, i);
        }
        Assert.assertTrue(bool.allFalse());
    }
}

