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


import org.assertj.core.api.Assertions;
import org.assertj.core.test.Jedi;
import org.junit.jupiter.api.Test;


public class ExtendedByTypesComparator_compareTo_Test {
    private static final TypeComparators COMPARATORS_BY_TYPE = new TypeComparators();

    private static final ExtendedByTypesComparator EXTENDED_STANDARD_COMPARATOR = new ExtendedByTypesComparator(ExtendedByTypesComparator_compareTo_Test.COMPARATORS_BY_TYPE);

    private static final ExtendedByTypesComparator EXTENDED_FIELD_BY_FIELD_COMPARATOR = new ExtendedByTypesComparator(new FieldByFieldComparator(), ExtendedByTypesComparator_compareTo_Test.COMPARATORS_BY_TYPE);

    @Test
    public void should_return_equal_if_objects_are_equal_by_default_comparator() {
        Assertions.assertThat(ExtendedByTypesComparator_compareTo_Test.EXTENDED_STANDARD_COMPARATOR.compare(new Jedi("Yoda", "Green"), new Jedi("Yoda", "Green"))).isZero();
        Assertions.assertThat(ExtendedByTypesComparator_compareTo_Test.EXTENDED_FIELD_BY_FIELD_COMPARATOR.compare(new Jedi("Yoda", "Green"), new Jedi("Yoda", "Green"))).isZero();
    }

    @Test
    public void should_return_are_not_equal_if_objects_are_not_equal_by_default_comparator() {
        Assertions.assertThat(ExtendedByTypesComparator_compareTo_Test.EXTENDED_STANDARD_COMPARATOR.compare(new Jedi("Yoda", "Green"), new Jedi("Luke", "Blue"))).isNotZero();
        Assertions.assertThat(ExtendedByTypesComparator_compareTo_Test.EXTENDED_FIELD_BY_FIELD_COMPARATOR.compare(new Jedi("Yoda", "Green"), new Jedi("Yoda", "Any"))).isNotZero();
    }
}

