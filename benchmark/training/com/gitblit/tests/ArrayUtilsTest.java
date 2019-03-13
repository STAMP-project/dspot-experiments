/**
 * Copyright 2012 gitblit.com.
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
 */
package com.gitblit.tests;


import com.gitblit.utils.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ArrayUtilsTest extends GitblitUnitTest {
    @Test
    public void testArrays() {
        Object[] nullArray = null;
        Assert.assertTrue(ArrayUtils.isEmpty(nullArray));
        Object[] emptyArray = new Object[0];
        Assert.assertTrue(ArrayUtils.isEmpty(emptyArray));
        Assert.assertFalse(ArrayUtils.isEmpty(new String[]{ "" }));
    }

    @Test
    public void testLists() {
        List<?> nullList = null;
        Assert.assertTrue(ArrayUtils.isEmpty(nullList));
        List<?> emptyList = new ArrayList<Object>();
        Assert.assertTrue(ArrayUtils.isEmpty(emptyList));
        List<?> list = Arrays.asList("");
        Assert.assertFalse(ArrayUtils.isEmpty(list));
    }

    @Test
    public void testSets() {
        Set<?> nullSet = null;
        Assert.assertTrue(ArrayUtils.isEmpty(nullSet));
        Set<?> emptySet = new HashSet<Object>();
        Assert.assertTrue(ArrayUtils.isEmpty(emptySet));
        Set<?> set = new HashSet<Object>(Arrays.asList(""));
        Assert.assertFalse(ArrayUtils.isEmpty(set));
    }
}

