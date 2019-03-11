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
package org.nd4j.linalg.mixed;


import DataType.UTF8;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;


@Slf4j
public class StringArrayTests {
    @Test
    public void testBasicStrings_1() {
        val array = Nd4j.scalar("alpha");
        Assert.assertNotNull(array);
        Assert.assertEquals(1, array.length());
        Assert.assertEquals(0, array.rank());
        Assert.assertEquals(UTF8, array.dataType());
        Assert.assertEquals("alpha", array.getStringUnsafe(0));
        String s = array.toString();
        Assert.assertTrue(s, s.contains("alpha"));
        System.out.println(s);
    }

    @Test
    public void testBasicStrings_2() {
        val array = Nd4j.create("alpha", "beta", "gamma");
        Assert.assertNotNull(array);
        Assert.assertEquals(3, array.length());
        Assert.assertEquals(1, array.rank());
        Assert.assertEquals(UTF8, array.dataType());
        Assert.assertEquals("alpha", array.getStringUnsafe(0));
        Assert.assertEquals("beta", array.getStringUnsafe(1));
        Assert.assertEquals("gamma", array.getStringUnsafe(2));
        String s = array.toString();
        Assert.assertTrue(s, s.contains("alpha"));
        Assert.assertTrue(s, s.contains("beta"));
        Assert.assertTrue(s, s.contains("gamma"));
        System.out.println(s);
    }

    @Test
    public void testBasicStrings_3() {
        val arrayX = Nd4j.create("alpha", "beta", "gamma");
        val arrayY = Nd4j.create("alpha", "beta", "gamma");
        val arrayZ = Nd4j.create("Alpha", "bEta", "gamma");
        Assert.assertEquals(arrayX, arrayX);
        Assert.assertEquals(arrayX, arrayY);
        Assert.assertNotEquals(arrayX, arrayZ);
    }
}

