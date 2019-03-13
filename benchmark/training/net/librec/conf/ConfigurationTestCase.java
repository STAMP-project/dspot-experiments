/**
 * Copyright (C) 2016 LibRec
 *
 * This file is part of LibRec.
 * LibRec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibRec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibRec. If not, see <http://www.gnu.org/licenses/>.
 */
package net.librec.conf;


import net.librec.BaseTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Configuration Test Case corresponds to Configuration
 * {@link net.librec.conf.Configuration}
 *
 * @author SunYatong
 */
public class ConfigurationTestCase extends BaseTestCase {
    /**
     * Test method setStrings.
     */
    @Test
    public void test1SetStrings() {
        conf.setStrings("test1", "str0", "str1", "str2");
        String[] actual = conf.getStrings("test1");
        Assert.assertEquals(3, actual.length);
        Assert.assertEquals("str0", actual[0]);
        Assert.assertEquals("str1", actual[1]);
        Assert.assertEquals("str2", actual[2]);
    }

    /**
     * Test method setFloat.
     */
    @Test
    public void test2SetFloat() {
        float expected = 1.2F;
        conf.setFloat("test2", expected);
        float actual = conf.getFloat("test2");
        Assert.assertEquals(expected, actual, 0);
    }

    /**
     * Test method setDouble.
     */
    @Test
    public void test3SetDouble() {
        double expected = 1.33;
        conf.setDouble("test3", expected);
        double actual = conf.getDouble("test3");
        Assert.assertEquals(expected, actual, 0);
    }

    /**
     * Test method setInt.
     */
    @Test
    public void test4SetInt() {
        int expected = 4;
        conf.setInt("test4", expected);
        int actual = conf.getInt("test4");
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test method setInts.
     */
    @Test
    public void test5SetInts() {
        int[] expected = new int[]{ 0, 1, 2, 3, 4 };
        conf.setInts("test5", expected);
        int[] actual = conf.getInts("test5");
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < (actual.length); i++) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }

    /**
     * Test method setBoolean.
     */
    @Test
    public void test6SetBoolean() {
        boolean expected = false;
        conf.setBoolean("test6", expected);
        boolean actual = conf.getBoolean("test6");
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test method getTrimmedStrings.
     */
    @Test
    public void test7GetTrimmedStrings() {
        String testStr = "  str0 , str1 , str2  ";
        conf.set("test7", testStr);
        String[] actual = conf.getTrimmedStrings("test7");
        Assert.assertEquals(3, actual.length);
        Assert.assertEquals("str0", actual[0]);
        Assert.assertEquals("str1", actual[1]);
        Assert.assertEquals("str2", actual[2]);
    }
}

