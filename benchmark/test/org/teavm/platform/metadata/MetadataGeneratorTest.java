/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.platform.metadata;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@SkipJVM
public class MetadataGeneratorTest {
    @Test
    public void nullExposed() {
        Assert.assertNull(getNull());
    }

    @Test
    public void intExposed() {
        Assert.assertEquals(23, getInt().getValue());
    }

    @Test
    public void resourceObjectExposed() {
        TestResource res = getResource();
        Assert.assertEquals(23, res.getA());
        Assert.assertFalse(res.getB());
        Assert.assertEquals(24, res.getD());
        Assert.assertEquals(25, res.getE());
        Assert.assertEquals(3.14, res.getF(), 0.001);
        Assert.assertEquals(2.72, res.getG(), 0.001);
        Assert.assertEquals("qwe", res.getFoo());
        Assert.assertEquals(2, res.getArrayA().size());
        Assert.assertEquals(2, res.getArrayA().get(0).getValue());
        Assert.assertEquals(3, res.getArrayA().get(1).getValue());
        Assert.assertEquals(1, res.getArrayB().size());
        Assert.assertEquals("baz", res.getArrayB().get(0).getBar());
        Assert.assertNull(res.getArrayC());
    }

    @Test
    public void resourceDefaultsSet() {
        TestResource res = getEmptyResource();
        Assert.assertEquals(0, res.getA());
        Assert.assertFalse(res.getB());
        Assert.assertEquals(0, res.getD());
        Assert.assertEquals(0, res.getE());
        Assert.assertEquals(0, res.getF(), 1.0E-10);
        Assert.assertEquals(0, res.getG(), 1.0E-10);
        Assert.assertNull(res.getFoo());
        Assert.assertNull(res.getArrayA());
        Assert.assertNull(res.getArrayB());
        Assert.assertNull(res.getArrayC());
        Assert.assertNull(res.getMapA());
        Assert.assertNull(res.getMapB());
        Assert.assertNull(res.getMapC());
    }

    @Test
    public void resourceModifiedInRunTime() {
        TestResource res = getEmptyResource();
        res.setA(23);
        res.setB(true);
        res.setD(((byte) (24)));
        res.setE(((short) (25)));
        res.setF(3.14F);
        res.setG(2.72);
        Assert.assertEquals(23, res.getA());
        Assert.assertTrue(res.getB());
        Assert.assertEquals(24, res.getD());
        Assert.assertEquals(25, res.getE());
        Assert.assertEquals(3.14, res.getF(), 0.001);
        Assert.assertEquals(2.72, res.getG(), 0.001);
    }
}

