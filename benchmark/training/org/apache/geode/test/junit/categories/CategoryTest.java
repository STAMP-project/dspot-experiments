/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.test.junit.categories;


import org.apache.geode.test.junit.runners.TestRunner;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


public class CategoryTest {
    private static boolean executedClassOneMethodNone;// 1


    private static boolean executedClassOneMethodTwo;// 2


    private static boolean executedClassTwoMethodTwo;// 3


    private static boolean executedClassNoneMethodOne;// 4


    private static boolean executedClassNoneMethodTwo;// 5


    private static boolean executedClassTwoMethodOne;// 6


    private static boolean executedClassOneMethodOne;// 7


    private static boolean executedClassOneAndTwoMethodNone;// 8


    private static boolean executedClassNoneMethodOneAndTwo;// 9


    @Test
    public void allTestsWithCategoryOneShouldBeExecuted() {
        Result result = TestRunner.runTest(CategoryTest.CategoryTestSuite.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(CategoryTest.executedClassOneMethodNone).isTrue();
        assertThat(CategoryTest.executedClassOneMethodTwo).isTrue();
        assertThat(CategoryTest.executedClassTwoMethodTwo).isFalse();
        assertThat(CategoryTest.executedClassNoneMethodOne).isTrue();
        assertThat(CategoryTest.executedClassNoneMethodTwo).isFalse();
        assertThat(CategoryTest.executedClassTwoMethodOne).isTrue();
        assertThat(CategoryTest.executedClassOneMethodOne).isTrue();
        assertThat(CategoryTest.executedClassOneAndTwoMethodNone).isTrue();
        assertThat(CategoryTest.executedClassNoneMethodOneAndTwo).isTrue();
    }

    @Category(CategoryOne.class)
    public static class ClassOneMethodNone {
        // 1
        @Test
        public void test() {
            CategoryTest.executedClassOneMethodNone = true;
        }
    }

    @Category(CategoryOne.class)
    public static class ClassOneMethodTwo {
        // 2
        @Category(CategoryTwo.class)
        @Test
        public void test() {
            CategoryTest.executedClassOneMethodTwo = true;
        }
    }

    @Category(CategoryTwo.class)
    public static class ClassTwoMethodTwo {
        // 3
        @Category(CategoryTwo.class)
        @Test
        public void test() {
            CategoryTest.executedClassTwoMethodTwo = true;
        }
    }

    public static class ClassNoneMethodOne {
        // 4
        @Category(CategoryOne.class)
        @Test
        public void test() {
            CategoryTest.executedClassNoneMethodOne = true;
        }
    }

    public static class ClassNoneMethodTwo {
        // 5
        @Category(CategoryTwo.class)
        @Test
        public void test() {
            CategoryTest.executedClassNoneMethodTwo = true;
        }
    }

    @Category(CategoryTwo.class)
    public static class ClassTwoMethodOne {
        // 6
        @Category(CategoryOne.class)
        @Test
        public void test() {
            CategoryTest.executedClassTwoMethodOne = true;
        }
    }

    @Category(CategoryOne.class)
    public static class ClassOneMethodOne {
        // 7
        @Category(CategoryOne.class)
        @Test
        public void test() {
            CategoryTest.executedClassOneMethodOne = true;
        }
    }

    @Category({ CategoryOne.class, CategoryTwo.class })
    public static class ClassOneAndTwoMethodNone {
        // 8
        @Test
        public void test() {
            CategoryTest.executedClassOneAndTwoMethodNone = true;
        }
    }

    public static class ClassNoneMethodOneAndTwo {
        // 9
        @Category({ CategoryOne.class, CategoryTwo.class })
        @Test
        public void test() {
            CategoryTest.executedClassNoneMethodOneAndTwo = true;
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoryOne.class)
    @Suite.SuiteClasses({ CategoryTest.ClassOneMethodNone.class// 1
    , CategoryTest.ClassOneMethodTwo.class// 2
    , CategoryTest.ClassTwoMethodTwo.class// 3
    , CategoryTest.ClassNoneMethodOne.class// 4
    , CategoryTest.ClassNoneMethodTwo.class// 5
    , CategoryTest.ClassTwoMethodOne.class// 6
    , CategoryTest.ClassOneMethodOne.class// 7
    , CategoryTest.ClassOneAndTwoMethodNone.class// 8
    , CategoryTest.ClassNoneMethodOneAndTwo.class// 9
     })
    public static class CategoryTestSuite {}
}

