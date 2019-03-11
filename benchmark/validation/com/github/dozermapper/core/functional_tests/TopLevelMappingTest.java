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


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class TopLevelMappingTest extends AbstractFunctionalTest {
    @Test
    public void testListToListMapping_Explicit() {
        mapper = getMapper("mappings/topLevelMapping.xml");
        TopLevelMappingTest.MyList source = new TopLevelMappingTest.MyList();
        source.add("100");
        ArrayList result = mapper.map(source, ArrayList.class);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(100, result.get(0));
    }

    @Test
    public void testListToListMapping_Implicit() {
        mapper = getMapper();
        TopLevelMappingTest.MyList source = new TopLevelMappingTest.MyList();
        source.add("100");
        ArrayList result = mapper.map(source, ArrayList.class);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("100", result.get(0));
    }

    @Test
    public void testListToListMapping_ImplicitItems() {
        mapper = getMapper();
        ArrayList source = new ArrayList();
        TopLevelMappingTest.ItemA itemA = new TopLevelMappingTest.ItemA();
        itemA.setA("test");
        source.add(itemA);
        ArrayList result = mapper.map(source, ArrayList.class);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(itemA.getA(), ((TopLevelMappingTest.ItemA) (result.get(0))).getA());
    }

    @Test
    public void testListToListMapping_ExplicitItems() {
        mapper = getMapper("mappings/topLevelMapping.xml");
        TopLevelMappingTest.MyList source = new TopLevelMappingTest.MyList();
        TopLevelMappingTest.ItemA itemA = new TopLevelMappingTest.ItemA();
        itemA.setA("test");
        source.add(itemA);
        ArrayList result = mapper.map(source, ArrayList.class, "2");
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(((result.get(0)) instanceof TopLevelMappingTest.ItemB));
        Assert.assertEquals(itemA.getA(), ((TopLevelMappingTest.ItemB) (result.get(0))).getA());
    }

    public static class MyList extends ArrayList {}

    public static class ItemA {
        private String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }

    public static class ItemB {
        private String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }
}

