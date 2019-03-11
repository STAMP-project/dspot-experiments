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
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MultipleHintsTest extends AbstractFunctionalTest {
    @Test
    public void testMultipleHints() {
        MultipleHintsTest.SourceClass sc = new MultipleHintsTest.SourceClass();
        List<MultipleHintsTest.SrcA> listOfSrcA = sc.getListOfSrcA();
        listOfSrcA.add(new MultipleHintsTest.SrcA.SrcB());
        listOfSrcA.add(new MultipleHintsTest.SrcA.SrcC());
        sc.setListOfSrcA(listOfSrcA);
        MultipleHintsTest.DestinationClass dc = mapper.map(sc, MultipleHintsTest.DestinationClass.class);
        Assert.assertThat(dc.getListOfA().size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void testMultipleHintsWithAlreadyMappedObject() {
        MultipleHintsTest.SourceClass sc = new MultipleHintsTest.SourceClass();
        List<MultipleHintsTest.SrcA> listOfSrcA = sc.getListOfSrcA();
        MultipleHintsTest.SrcA.SrcB testObj = new MultipleHintsTest.SrcA.SrcB();
        listOfSrcA.add(testObj);
        listOfSrcA.add(new MultipleHintsTest.SrcA.SrcC());
        listOfSrcA.add(testObj);
        sc.setListOfSrcA(listOfSrcA);
        MultipleHintsTest.DestinationClass dc = mapper.map(sc, MultipleHintsTest.DestinationClass.class);
        Assert.assertThat(dc.getListOfA().size(), CoreMatchers.equalTo(3));
        Assert.assertThat(dc.getListOfA().get(0), CoreMatchers.equalTo(dc.getListOfA().get(2)));
    }

    public static class SourceClass {
        List<MultipleHintsTest.SrcA> listOfSrcA = new ArrayList<>();

        public void setListOfSrcA(List<MultipleHintsTest.SrcA> listOfSrcA) {
            this.listOfSrcA = listOfSrcA;
        }

        public List<MultipleHintsTest.SrcA> getListOfSrcA() {
            return listOfSrcA;
        }
    }

    public static class DestinationClass {
        private List<MultipleHintsTest.A> listOfA = new ArrayList<>();

        public List<MultipleHintsTest.A> getListOfA() {
            return Collections.unmodifiableList(listOfA);
        }

        public void addA(MultipleHintsTest.A a) {
            listOfA.add(a);
        }
    }

    public abstract static class A {
        public static class B extends MultipleHintsTest.A {}

        public static class C extends MultipleHintsTest.A {}
    }

    public abstract static class SrcA {
        public static class SrcB extends MultipleHintsTest.SrcA {}

        public static class SrcC extends MultipleHintsTest.SrcA {}
    }
}

