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
package org.assertj.core.internal;


import java.util.Comparator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class TypeComparators_hasComparator_Test {
    private TypeComparators typeComparators;

    @Test
    public void should_find_comparator() {
        typeComparators.put(TypeComparators_hasComparator_Test.Foo.class, TypeComparators_hasComparator_Test.newComparator());
        // WHEN
        boolean comparatorFound = typeComparators.hasComparatorForType(TypeComparators_hasComparator_Test.Foo.class);
        // THEN
        Assertions.assertThat(comparatorFound).isTrue();
    }

    @Test
    public void should_find_parent_comparator() {
        typeComparators.put(TypeComparators_hasComparator_Test.Bar.class, TypeComparators_hasComparator_Test.newComparator());
        // WHEN
        boolean comparatorFound = typeComparators.hasComparatorForType(TypeComparators_hasComparator_Test.Foo.class);
        // THEN
        Assertions.assertThat(comparatorFound).isTrue();
    }

    @Test
    public void should_not_find_any_comparator() {
        // GIVEN
        Comparator<TypeComparators_hasComparator_Test.I3> i3Comparator = TypeComparators_hasComparator_Test.newComparator();
        Comparator<TypeComparators_hasComparator_Test.I4> i4Comparator = TypeComparators_hasComparator_Test.newComparator();
        Comparator<TypeComparators_hasComparator_Test.Foo> fooComparator = TypeComparators_hasComparator_Test.newComparator();
        typeComparators.put(TypeComparators_hasComparator_Test.I3.class, i3Comparator);
        typeComparators.put(TypeComparators_hasComparator_Test.I4.class, i4Comparator);
        typeComparators.put(TypeComparators_hasComparator_Test.Foo.class, fooComparator);
        // WHEN
        boolean comparatorFound = typeComparators.hasComparatorForType(TypeComparators_hasComparator_Test.I5.class);
        // THEN
        Assertions.assertThat(comparatorFound).isFalse();
    }

    private interface I1 {}

    private interface I2 {}

    private interface I3 {}

    private interface I4 {}

    private interface I5 extends TypeComparators_hasComparator_Test.I1 , TypeComparators_hasComparator_Test.I2 {}

    private class Bar implements TypeComparators_hasComparator_Test.I3 {}

    private class Foo extends TypeComparators_hasComparator_Test.Bar implements TypeComparators_hasComparator_Test.I4 , TypeComparators_hasComparator_Test.I5 {}
}

