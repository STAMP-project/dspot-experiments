/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.math;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class ComplexTest {
    Complex a = new Complex(5.0, 6.0);

    Complex b = new Complex((-3.0), 4.0);

    public ComplexTest() {
    }

    /**
     * Test of toString method, of class Complex.
     */
    @Test
    public void testAll() {
        System.out.println("Complex");
        System.out.println(("a = " + (a)));
        System.out.println(("b = " + (b)));
        Assert.assertEquals(a.re(), 5.0, 1.0E-15);
        Assert.assertEquals(a.im(), 6.0, 1.0E-15);
        Assert.assertEquals(a.abs(), 7.810249675906654, 1.0E-15);
        Assert.assertTrue(a.plus(b).equals(new Complex(2.0, 10.0)));
        Assert.assertTrue(a.minus(b).equals(new Complex(8.0, 2.0)));
        Assert.assertTrue(a.times(b).equals(new Complex((-39.0), 2.0)));
        Assert.assertTrue(a.div(b).equals(new Complex(0.36, (-1.52))));
        System.out.println(("a / b = " + (a.div(b))));
        Assert.assertTrue(a.div(b).times(b).equals(new Complex(5.0, 6.0)));
        Assert.assertTrue(a.conjugate().equals(new Complex(5.0, (-6.0))));
        Assert.assertTrue(a.tan().equals(new Complex((-6.685231390243073E-6), 1.00001031089812)));
    }
}

