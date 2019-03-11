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


import com.github.dozermapper.core.vo.recursive.ClassAA;
import com.github.dozermapper.core.vo.recursive.ClassAAPrime;
import com.github.dozermapper.core.vo.recursive.ClassB;
import com.github.dozermapper.core.vo.recursive.ClassBPrime;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the dozer behaviour when confronted with structures similar to ours. As of now (dozer 3.0) the behaviour is not
 * optimal and still require special treatments in special cases.
 */
public class RecursiveTest extends AbstractFunctionalTest {
    /**
     * this test should validate dozerXX correct behaviour in front of recursive class references in a subclass. With
     * dozer3.0 the first reference is not used but the recursion is correct on the next levels.
     */
    @Test
    public void testConvertWithSubClass() {
        mapper = getMapper("mappings/recursivemappings.xml", "mappings/recursivemappings2.xml");
        ClassAA testAA = createTestClassAA();
        // the == is on purpose, we test that the referenced parent of the first item of the subs is the parent instance
        // itself
        ClassB testClassB = testAA.getSubs().iterator().next();
        Assert.assertTrue(((testClassB.getParent()) == testAA));
        ClassAAPrime testAAPrime = mapper.map(testAA, ClassAAPrime.class, null);
        // testing the new dozer3.0 bi-directionnal reference through a set
        Assert.assertEquals(testAA.getSubs().size(), testAAPrime.getSubs().size());
        // the equality is true at the data level
        ClassBPrime testClassBPrime = testAAPrime.getSubs().iterator().next();
        Assert.assertTrue(testClassBPrime.getParent().equals(testAAPrime));
        // we want the referenced parent of the first item of the subs to be the parent instance itself
        ClassBPrime testClassBPrime2 = testAAPrime.getSubs().iterator().next();
        Assert.assertTrue(((testClassBPrime2.getParent()) == testAAPrime));
    }

    @Test
    public void testMirroredSelfReferencingTypes() {
        RecursiveTest.TypeA src = new RecursiveTest.TypeA();
        src.setId("1");
        RecursiveTest.TypeA parent = new RecursiveTest.TypeA();
        parent.setId("2");
        src.setParent(parent);
        RecursiveTest.TypeB result = new RecursiveTest.TypeB();
        mapper.map(src, result);
        Assert.assertNotNull(result);
        Assert.assertEquals("1", result.getId());
        Assert.assertEquals("2", result.getParent().getId());
    }

    public static class TypeA {
        private RecursiveTest.TypeA parent;

        private String id;

        public RecursiveTest.TypeA getParent() {
            return parent;
        }

        public void setParent(RecursiveTest.TypeA parent) {
            this.parent = parent;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class TypeB {
        private RecursiveTest.TypeB parent;

        private String id;

        public RecursiveTest.TypeB getParent() {
            return parent;
        }

        public void setParent(RecursiveTest.TypeB parent) {
            this.parent = parent;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

