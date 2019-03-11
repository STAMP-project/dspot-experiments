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


/**
 *
 *
 * @author Filip Hrisafov
 */
public class TypeComparators_Test {
    private TypeComparators typeComparators;

    @Test
    public void should_return_exact_match() {
        Comparator<TypeComparators_Test.Foo> fooComparator = TypeComparators_Test.newComparator();
        Comparator<TypeComparators_Test.Bar> barComparator = TypeComparators_Test.newComparator();
        typeComparators.put(TypeComparators_Test.Foo.class, fooComparator);
        typeComparators.put(TypeComparators_Test.Bar.class, barComparator);
        Comparator<?> foo = typeComparators.get(TypeComparators_Test.Foo.class);
        Comparator<?> bar = typeComparators.get(TypeComparators_Test.Bar.class);
        Assertions.assertThat(foo).isEqualTo(fooComparator);
        Assertions.assertThat(bar).isEqualTo(barComparator);
    }

    @Test
    public void should_return_parent_comparator() {
        Comparator<TypeComparators_Test.Bar> barComparator = TypeComparators_Test.newComparator();
        typeComparators.put(TypeComparators_Test.Bar.class, barComparator);
        Comparator<?> foo = typeComparators.get(TypeComparators_Test.Foo.class);
        Comparator<?> bar = typeComparators.get(TypeComparators_Test.Bar.class);
        Assertions.assertThat(foo).isEqualTo(bar);
        Assertions.assertThat(bar).isEqualTo(barComparator);
    }

    @Test
    public void should_return_most_relevant() {
        Comparator<TypeComparators_Test.I3> i3Comparator = TypeComparators_Test.newComparator();
        Comparator<TypeComparators_Test.I4> i4Comparator = TypeComparators_Test.newComparator();
        Comparator<TypeComparators_Test.I1> i1Comparator = TypeComparators_Test.newComparator();
        Comparator<TypeComparators_Test.I2> i2Comparator = TypeComparators_Test.newComparator();
        Comparator<TypeComparators_Test.Foo> fooComparator = TypeComparators_Test.newComparator();
        typeComparators.put(TypeComparators_Test.I3.class, i3Comparator);
        typeComparators.put(TypeComparators_Test.I4.class, i4Comparator);
        typeComparators.put(TypeComparators_Test.I1.class, i1Comparator);
        typeComparators.put(TypeComparators_Test.I2.class, i2Comparator);
        typeComparators.put(TypeComparators_Test.Foo.class, fooComparator);
        Comparator<?> foo = typeComparators.get(TypeComparators_Test.Foo.class);
        Comparator<?> bar = typeComparators.get(TypeComparators_Test.Bar.class);
        Comparator<?> i3 = typeComparators.get(TypeComparators_Test.I3.class);
        Comparator<?> i4 = typeComparators.get(TypeComparators_Test.I4.class);
        Comparator<?> i5 = typeComparators.get(TypeComparators_Test.I5.class);
        Assertions.assertThat(foo).isEqualTo(fooComparator);
        Assertions.assertThat(bar).isEqualTo(i3Comparator);
        Assertions.assertThat(i3).isEqualTo(i3Comparator);
        Assertions.assertThat(i4).isEqualTo(i4Comparator);
        Assertions.assertThat(i5).isEqualTo(i1Comparator);
    }

    @Test
    public void should_find_no_comparator() {
        Comparator<TypeComparators_Test.I3> i3Comparator = TypeComparators_Test.newComparator();
        Comparator<TypeComparators_Test.I4> i4Comparator = TypeComparators_Test.newComparator();
        Comparator<TypeComparators_Test.Foo> fooComparator = TypeComparators_Test.newComparator();
        typeComparators.put(TypeComparators_Test.I3.class, i3Comparator);
        typeComparators.put(TypeComparators_Test.I4.class, i4Comparator);
        typeComparators.put(TypeComparators_Test.Foo.class, fooComparator);
        Comparator<?> i5 = typeComparators.get(TypeComparators_Test.I5.class);
        Assertions.assertThat(i5).isNull();
    }

    @Test
    public void should_be_empty() {
        typeComparators.clear();
        Assertions.assertThat(typeComparators.isEmpty()).isTrue();
    }

    private interface I1 {}

    private interface I2 {}

    private interface I3 {}

    private interface I4 {}

    private interface I5 extends TypeComparators_Test.I1 , TypeComparators_Test.I2 {}

    private class Bar implements TypeComparators_Test.I3 {}

    private class Foo extends TypeComparators_Test.Bar implements TypeComparators_Test.I4 , TypeComparators_Test.I5 {}
}

