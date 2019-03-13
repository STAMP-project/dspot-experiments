/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api;


import org.junit.jupiter.api.Test;


public class ComparableAssertion_should_be_flexible_Test {
    @Test
    public void comparable_api_should_be_flexible() {
        ComparableAssertion_should_be_flexible_Test.TestClass testClass1 = new ComparableAssertion_should_be_flexible_Test.TestClass();
        ComparableAssertion_should_be_flexible_Test.TestClass testClass2 = new ComparableAssertion_should_be_flexible_Test.TestClass();
        isEqualByComparingTo(testClass2);
    }

    // The important thing here is that TestClass implements Comparable<Object> and not Comparable<TestClass>
    // even
    private static final class TestClass implements Comparable<Object> {
        @Override
        public int compareTo(Object other) {
            return 0;// always equals for the test

        }
    }

    // we'd like to get rid of the compile error here
    private static final class TestClassAssert extends AbstractComparableAssert<ComparableAssertion_should_be_flexible_Test.TestClassAssert, ComparableAssertion_should_be_flexible_Test.TestClass> {
        TestClassAssert(ComparableAssertion_should_be_flexible_Test.TestClass actual) {
            super(actual, ComparableAssertion_should_be_flexible_Test.TestClassAssert.class);
        }

        static ComparableAssertion_should_be_flexible_Test.TestClassAssert assertThat(ComparableAssertion_should_be_flexible_Test.TestClass actual) {
            return new ComparableAssertion_should_be_flexible_Test.TestClassAssert(actual);
        }
    }
}

