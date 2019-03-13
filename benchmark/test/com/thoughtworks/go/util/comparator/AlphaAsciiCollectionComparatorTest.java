/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.util.comparator;


import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AlphaAsciiCollectionComparatorTest {
    private class Foo implements Comparable<AlphaAsciiCollectionComparatorTest.Foo> {
        private final String value;

        Foo(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public int compareTo(AlphaAsciiCollectionComparatorTest.Foo other) {
            return value.compareTo(other.value);
        }
    }

    @Test
    public void shouldCompareSortedCollections() {
        AlphaAsciiCollectionComparator<AlphaAsciiCollectionComparatorTest.Foo> comparator = new AlphaAsciiCollectionComparator();
        Assert.assertThat(comparator.compare(Arrays.asList(new AlphaAsciiCollectionComparatorTest.Foo("foo"), new AlphaAsciiCollectionComparatorTest.Foo("quux")), Arrays.asList(new AlphaAsciiCollectionComparatorTest.Foo("foo"), new AlphaAsciiCollectionComparatorTest.Foo("bar"))), Matchers.greaterThan(0));
        Assert.assertThat(comparator.compare(Arrays.asList(new AlphaAsciiCollectionComparatorTest.Foo("foo"), new AlphaAsciiCollectionComparatorTest.Foo("abc")), Arrays.asList(new AlphaAsciiCollectionComparatorTest.Foo("foo"), new AlphaAsciiCollectionComparatorTest.Foo("bar"))), Matchers.lessThan(0));
        Assert.assertThat(comparator.compare(Arrays.asList(new AlphaAsciiCollectionComparatorTest.Foo("foo"), new AlphaAsciiCollectionComparatorTest.Foo("bar")), Arrays.asList(new AlphaAsciiCollectionComparatorTest.Foo("bar"), new AlphaAsciiCollectionComparatorTest.Foo("foo"))), Matchers.is(0));
    }
}

