/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.vo.copybyreference.Reference;
import com.github.dozermapper.core.vo.copybyreference.TestA;
import com.github.dozermapper.core.vo.copybyreference.TestB;
import org.junit.Assert;
import org.junit.Test;


public class SubclassReferenceTest extends AbstractFunctionalTest {
    private Mapper mapper;

    private TestA testA;

    private TestB testB;

    @Test
    public void testBase() {
        testB = mapper.map(testA, TestB.class);
        Assert.assertEquals(testA.getOne(), testB.getOne());
        Assert.assertEquals(testA.getOneA(), testB.getOneB());
    }

    @Test
    public void testSubclassSource() {
        TestA testA = new TestA() {};// anonymous subclass

        testA.setOne("one");
        testA.setOneA("oneA");
        testB = mapper.map(testA, TestB.class);
        Assert.assertEquals(testA.getOne(), testB.getOne());
        Assert.assertEquals(testA.getOneA(), testB.getOneB());
    }

    @Test
    public void testReference() {
        testA.setTestReference(Reference.FOO);
        testB = mapper.map(testA, TestB.class);
        Assert.assertEquals(testA.getOne(), testB.getOne());
        Assert.assertEquals(testA.getOneA(), testB.getOneB());
        Assert.assertEquals(testA.getTestReference(), testB.getTestReference());
    }

    @Test
    public void testReferenceSubclassSource() {
        TestA testASubclass = new TestA() {};// anonymous subclass

        testASubclass.setTestReference(Reference.FOO);
        testB = mapper.map(testASubclass, TestB.class);
        Assert.assertEquals(testASubclass.getOne(), testB.getOne());
        Assert.assertEquals(testASubclass.getOneA(), testB.getOneB());
        Assert.assertEquals(testASubclass.getTestReference(), testB.getTestReference());
    }
}

