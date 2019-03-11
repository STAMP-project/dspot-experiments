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
package smile.gap;


import BitString.Crossover;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class BitStringTest {
    public BitStringTest() {
    }

    /**
     * Test of newInstance method, of class BitString.
     */
    @Test
    public void testNewInstance() {
        System.out.println("newInstance");
        int[] father = new int[]{ 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0 };
        BitString instance = new BitString(father, null, Crossover.SINGLE_POINT, 1.0, 0.0);
        BitString result = instance.newInstance();
        Assert.assertEquals(father.length, result.length);
        Assert.assertEquals(father.length, result.bits().length);
        boolean same = true;
        for (int i = 0; i < (father.length); i++) {
            if ((father[i]) != (result.bits()[i])) {
                same = false;
            }
        }
        Assert.assertFalse(same);
    }

    /**
     * Test of crossover method, of class BitString.
     */
    @Test
    public void testCrossoverOne() {
        System.out.println("crossover one point");
        int[] father = new int[]{ 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0 };
        int[] mother = new int[]{ 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1 };
        BitString instance = new BitString(father, null, Crossover.SINGLE_POINT, 1.0, 0.0);
        BitString another = new BitString(mother, null, Crossover.SINGLE_POINT, 1.0, 0.0);
        int[] son = new int[]{ 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1 };
        int[] daughter = new int[]{ 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0 };
        BitString[] result = instance.crossover(another);
        Assert.assertEquals(son.length, result[0].bits().length);
        Assert.assertEquals(daughter.length, result[1].bits().length);
        for (int i = 0; i < (son.length); i++) {
            // assertEquals(son[i], result[0].bits()[i]);
            // assertEquals(daughter[i], result[1].bits()[i]);
            Assert.assertTrue(((((father[i]) == (result[0].bits()[i])) && ((mother[i]) == (result[1].bits()[i]))) || (((father[i]) == (result[1].bits()[i])) && ((mother[i]) == (result[0].bits()[i])))));
        }
    }

    /**
     * Test of crossover method, of class BitString.
     */
    @Test
    public void testCrossoverTwo() {
        System.out.println("crossover two point");
        int[] father = new int[]{ 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0 };
        int[] mother = new int[]{ 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1 };
        BitString instance = new BitString(father, null, Crossover.TWO_POINT, 1.0, 0.0);
        BitString another = new BitString(mother, null, Crossover.TWO_POINT, 1.0, 0.0);
        int[] son = new int[]{ 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0 };
        int[] daughter = new int[]{ 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1 };
        BitString[] result = instance.crossover(another);
        Assert.assertEquals(son.length, result[0].bits().length);
        Assert.assertEquals(daughter.length, result[1].bits().length);
        for (int i = 0; i < (son.length); i++) {
            // assertEquals(son[i], result[0].bits()[i]);
            // assertEquals(daughter[i], result[1].bits()[i]);
            Assert.assertTrue(((((father[i]) == (result[0].bits()[i])) && ((mother[i]) == (result[1].bits()[i]))) || (((father[i]) == (result[1].bits()[i])) && ((mother[i]) == (result[0].bits()[i])))));
        }
    }

    /**
     * Test of crossover method, of class BitString.
     */
    @Test
    public void testCrossoverUniform() {
        System.out.println("crossover uniform");
        int[] father = new int[]{ 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0 };
        int[] mother = new int[]{ 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1 };
        BitString instance = new BitString(father, null, Crossover.UNIFORM, 1.0, 0.0);
        BitString another = new BitString(mother, null, Crossover.UNIFORM, 1.0, 0.0);
        BitString[] result = instance.crossover(another);
        Assert.assertEquals(father.length, result[0].bits().length);
        Assert.assertEquals(mother.length, result[1].bits().length);
        boolean same = true;
        for (int i = 0; i < (father.length); i++) {
            Assert.assertTrue(((((father[i]) == (result[0].bits()[i])) && ((mother[i]) == (result[1].bits()[i]))) || (((father[i]) == (result[1].bits()[i])) && ((mother[i]) == (result[0].bits()[i])))));
            if ((father[i]) != (result[0].bits()[i])) {
                same = false;
            }
        }
        Assert.assertFalse(same);
    }

    /**
     * Test of mutate method, of class BitString.
     */
    @Test
    public void testMutate() {
        System.out.println("mutate");
        int[] father = new int[]{ 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0 };
        BitString instance = new BitString(father.clone(), null, Crossover.SINGLE_POINT, 1.0, 1.0);
        instance.mutate();
        Assert.assertEquals(father.length, instance.length);
        Assert.assertEquals(father.length, instance.bits().length);
        boolean same = true;
        for (int i = 0; i < (father.length); i++) {
            if ((father[i]) != (instance.bits()[i])) {
                same = false;
            }
        }
        Assert.assertFalse(same);
    }
}

