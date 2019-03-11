/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.impl.map.immutable;


import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.test.Verify;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link ImmutableEmptyMap}.
 */
public class ImmutableEmptyMapTest extends ImmutableMemoryEfficientMapTestCase {
    @Override
    @Test
    public void testToString() {
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertEquals("{}", map.toString());
    }

    @Override
    @Test
    public void flipUniqueValues() {
        Verify.assertEmpty(this.classUnderTest().flipUniqueValues());
    }

    @Override
    @Test
    public void get() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertNull(classUnderTest.get(absentKey));
        Assert.assertFalse(classUnderTest.containsValue(absentValue));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsent_function() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertEquals(absentValue, classUnderTest.getIfAbsent(absentKey, new com.gs.collections.impl.block.function.PassThruFunction0(absentValue)));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsent() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertEquals(absentValue, classUnderTest.getIfAbsentValue(absentKey, absentValue));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsentWith() {
        Integer absentKey = (this.size()) + 1;
        String absentValue = String.valueOf(absentKey);
        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertEquals(absentValue, classUnderTest.getIfAbsentWith(absentKey, String::valueOf, absentValue));
        // Still unchanged
        Assert.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void ifPresentApply() {
        Integer absentKey = (this.size()) + 1;
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assert.assertNull(classUnderTest.ifPresentApply(absentKey, Functions.<String>getPassThru()));
    }

    @Override
    @Test
    public void notEmpty() {
        Assert.assertFalse(this.classUnderTest().notEmpty());
    }

    @Override
    @Test
    public void allSatisfy() {
        ImmutableMap<String, String> map = new ImmutableEmptyMap();
        Assert.assertTrue(map.allSatisfy(String.class::isInstance));
        Assert.assertTrue(map.allSatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void anySatisfy() {
        ImmutableMap<String, String> map = new ImmutableEmptyMap();
        Assert.assertFalse(map.anySatisfy(String.class::isInstance));
        Assert.assertFalse(map.anySatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void noneSatisfy() {
        ImmutableMap<String, String> map = new ImmutableEmptyMap();
        Assert.assertTrue(map.noneSatisfy(String.class::isInstance));
        Assert.assertTrue(map.noneSatisfy("Monkey"::equals));
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max() {
        ImmutableMap<String, String> map = new ImmutableEmptyMap();
        map.max();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void maxBy() {
        ImmutableMap<String, String> map = new ImmutableEmptyMap();
        map.maxBy(Functions.getStringPassThru());
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min() {
        ImmutableMap<String, String> map = new ImmutableEmptyMap();
        map.min();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void minBy() {
        ImmutableMap<String, String> map = new ImmutableEmptyMap();
        map.minBy(Functions.getStringPassThru());
    }
}

