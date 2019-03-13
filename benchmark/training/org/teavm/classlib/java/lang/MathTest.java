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
package org.teavm.classlib.java.lang;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class MathTest {
    @Test
    public void sinComputed() {
        Assert.assertEquals(0.90929742682568, Math.sin(2), 1.0E-14);
    }

    @Test
    public void expComputed() {
        Assert.assertEquals(3.4212295362896734, Math.exp(1.23), 1.0E-14);
    }

    @Test
    public void cbrtComputed() {
        Assert.assertEquals(3.0, Math.cbrt(27.0), 1.0E-14);
        Assert.assertEquals((-3.0), Math.cbrt((-27.0)), 1.0E-14);
        Assert.assertEquals(0, Math.cbrt(0), 1.0E-14);
    }

    @Test
    public void ulpComputed() {
        Assert.assertEquals(1.4210854715202004E-14, Math.ulp(123.456), 1.0E-25);
    }

    @Test
    public void sinhComputed() {
        Assert.assertEquals(1.3097586593745313E53, Math.sinh(123), 1.0E40);
    }

    @Test
    public void getExponentComputed() {
        Assert.assertEquals(6, Math.getExponent(123.456));
    }

    @Test
    public void roundWorks() {
        Assert.assertEquals(1, Math.round(1.3));
        Assert.assertEquals(2, Math.round(1.8));
        Assert.assertEquals((-1), Math.round((-1.3)));
        Assert.assertEquals((-2), Math.round((-1.8)));
    }
}

