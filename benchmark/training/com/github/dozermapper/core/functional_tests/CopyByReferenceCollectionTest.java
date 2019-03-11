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


import com.github.dozermapper.core.vo.SimpleEnum;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class CopyByReferenceCollectionTest extends AbstractFunctionalTest {
    @Test
    public void testCollectionToSet() {
        // Property within Set
        Set<SimpleEnum> tempSet = new HashSet<>();
        tempSet.add(SimpleEnum.TWO);
        tempSet.add(SimpleEnum.ONE);
        CopyByReferenceCollectionTest.ObjectWithCollection1 obj1 = new CopyByReferenceCollectionTest.ObjectWithCollection1();
        obj1.setTestSetToSet(tempSet);
        CopyByReferenceCollectionTest.ObjectWithCollection2 obj2 = new CopyByReferenceCollectionTest.ObjectWithCollection2();
        mapper.map(obj1, obj2);
        Assert.assertTrue(obj2.getTestSetToSet().contains(SimpleEnum.ONE));
        Assert.assertTrue(obj2.getTestSetToSet().contains(SimpleEnum.TWO));
    }

    @Test
    public void testArrayToArray() {
        SimpleEnum[] srcEnumArray = new SimpleEnum[1];
        srcEnumArray[0] = SimpleEnum.TWO;
        CopyByReferenceCollectionTest.ObjectWithCollection1 obj1 = new CopyByReferenceCollectionTest.ObjectWithCollection1();
        obj1.setTestArrayToArray(srcEnumArray);
        CopyByReferenceCollectionTest.ObjectWithCollection2 obj2 = new CopyByReferenceCollectionTest.ObjectWithCollection2();
        mapper.map(obj1, obj2);
        Assert.assertEquals(SimpleEnum.TWO, obj2.getTestArrayToArray()[0]);
    }

    @Test
    public void testArrayToList() {
        SimpleEnum[] srcEnumArray = new SimpleEnum[1];
        srcEnumArray[0] = SimpleEnum.TWO;
        CopyByReferenceCollectionTest.ObjectWithCollection1 obj1 = new CopyByReferenceCollectionTest.ObjectWithCollection1();
        obj1.setTestArrayToList(srcEnumArray);
        CopyByReferenceCollectionTest.ObjectWithCollection2 obj2 = new CopyByReferenceCollectionTest.ObjectWithCollection2();
        mapper.map(obj1, obj2);
        Assert.assertEquals(SimpleEnum.TWO, obj2.getTestArrayToList().get(0));
    }

    @Test
    public void testListToList() {
        CopyByReferenceCollectionTest.ObjectWithCollection1 obj1 = new CopyByReferenceCollectionTest.ObjectWithCollection1();
        List<SimpleEnum> listToArrayTest = new ArrayList<>();
        listToArrayTest.add(SimpleEnum.ONE);
        obj1.setTestListToArray(listToArrayTest);
        CopyByReferenceCollectionTest.ObjectWithCollection2 obj2 = new CopyByReferenceCollectionTest.ObjectWithCollection2();
        mapper.map(obj1, obj2);
        Assert.assertEquals(SimpleEnum.ONE, obj2.getTestListToArray()[0]);
    }

    public class ObjectWithCollection1 {
        private Set<SimpleEnum> testSetToSet;

        private SimpleEnum[] testArrayToArray;

        private SimpleEnum[] testArrayToList;

        private List<SimpleEnum> testListToArray = new ArrayList<>();

        public Set<SimpleEnum> getTestSetToSet() {
            return testSetToSet;
        }

        public void setTestSetToSet(Set<SimpleEnum> testSetToSet) {
            this.testSetToSet = testSetToSet;
        }

        public SimpleEnum[] getTestArrayToArray() {
            return testArrayToArray;
        }

        public void setTestArrayToArray(SimpleEnum[] testArrayToArray) {
            this.testArrayToArray = testArrayToArray;
        }

        public SimpleEnum[] getTestArrayToList() {
            return testArrayToList;
        }

        public void setTestArrayToList(SimpleEnum[] testArrayToList) {
            this.testArrayToList = testArrayToList;
        }

        public List getTestListToArray() {
            return testListToArray;
        }

        public void setTestListToArray(List testListToArray) {
            this.testListToArray = testListToArray;
        }
    }

    public class ObjectWithCollection2 {
        private Set<SimpleEnum> testSetToSet;

        private SimpleEnum[] testArrayToArray;

        private List testArrayToList = new ArrayList<SimpleEnum>();

        private SimpleEnum[] testListToArray;

        public Set<SimpleEnum> getTestSetToSet() {
            return testSetToSet;
        }

        public void setTestSetToSet(Set<SimpleEnum> testSetToSet) {
            this.testSetToSet = testSetToSet;
        }

        public SimpleEnum[] getTestArrayToArray() {
            return testArrayToArray;
        }

        public void setTestArrayToArray(SimpleEnum[] testArrayToArray) {
            this.testArrayToArray = testArrayToArray;
        }

        public List getTestArrayToList() {
            return testArrayToList;
        }

        public void setTestArrayToList(List testArrayToList) {
            this.testArrayToList = testArrayToList;
        }

        public SimpleEnum[] getTestListToArray() {
            return testListToArray;
        }

        public void setTestListToArray(SimpleEnum[] testListToArray) {
            this.testListToArray = testListToArray;
        }
    }
}

