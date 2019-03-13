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
package org.teavm.html4j.test;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@SkipJVM
public class JavaScriptBodyConversionTest {
    @Test
    public void convertsInteger() {
        Assert.assertEquals(23, returnAsInt(23));
        Assert.assertEquals(Integer.valueOf(23), returnAsInteger(23));
        Assert.assertEquals(23, returnAsObject(23));
        Assert.assertEquals(24, addOne(((Object) (23))));
        Assert.assertEquals(Integer.valueOf(24), addOne(Integer.valueOf(23)));
        Assert.assertEquals(24, addOne(23));
    }

    @Test
    public void convertsIntegerResult() {
        Assert.assertEquals(23, returnAsObject(23));
        Assert.assertEquals(Integer.valueOf(23), returnAsInteger(23));
        Assert.assertEquals(23, returnAsInt(23));
    }

    @Test
    public void convertsBoolean() {
        Assert.assertTrue(returnAsBoolean(true));
        Assert.assertTrue(returnAsBooleanWrapper(true));
        Assert.assertEquals(Boolean.TRUE, returnAsObject(true));
    }

    @Test
    public void convertsArray() {
        Assert.assertEquals(42, getArrayItem(new int[]{ 23, 42 }, 1));
        Assert.assertEquals(42, getArrayItem(new Integer[]{ 23, 42 }, 1));
    }

    @Test
    public void copiesArray() {
        Integer[] array = new Integer[]{ 23, 42 };
        Integer[] arrayCopy = ((Integer[]) (modifyIntegerArray(array)));
        Assert.assertEquals(Integer.valueOf(23), array[0]);
        Assert.assertEquals(Integer.valueOf(1), arrayCopy[0]);
    }

    @Test
    public void createsArrayOfProperType() {
        Assert.assertEquals(Object[].class, returnAsObject(new int[]{ 23, 42 }).getClass());
        Assert.assertEquals(Integer[].class, returnAsIntegerArray(new Integer[]{ 23, 42 }).getClass());
        Assert.assertEquals(int[].class, returnAsIntArray(new Integer[]{ 23, 42 }).getClass());
    }
}

