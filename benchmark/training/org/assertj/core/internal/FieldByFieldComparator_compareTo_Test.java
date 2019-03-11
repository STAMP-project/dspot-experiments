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
import org.junit.jupiter.api.Test;


public class FieldByFieldComparator_compareTo_Test {
    private FieldByFieldComparator fieldByFieldComparator;

    @Test
    public void should_return_true_if_both_Objects_are_null() {
        Assertions.assertThat(fieldByFieldComparator.compare(null, null)).isZero();
    }

    @Test
    public void should_return_true_if_Objects_are_equal() {
        Assertions.assertThat(fieldByFieldComparator.compare(new FieldByFieldComparator_compareTo_Test.JarJar("Yoda"), new FieldByFieldComparator_compareTo_Test.JarJar("Yoda"))).isZero();
    }

    @Test
    public void should_return_false_if_Objects_are_not_equal() {
        Assertions.assertThat(fieldByFieldComparator.compare(new FieldByFieldComparator_compareTo_Test.JarJar("Yoda"), new FieldByFieldComparator_compareTo_Test.JarJar("HanSolo"))).isNotZero();
    }

    @Test
    public void should_return_are_not_equal_if_first_Object_is_null_and_second_is_not() {
        Assertions.assertThat(fieldByFieldComparator.compare(null, new FieldByFieldComparator_compareTo_Test.JarJar("Yoda"))).isNotZero();
    }

    @Test
    public void should_return_are_not_equal_if_second_Object_is_null_and_first_is_not() {
        Assertions.assertThat(fieldByFieldComparator.compare(new FieldByFieldComparator_compareTo_Test.JarJar("Yoda"), null)).isNotZero();
    }

    @Test
    public void should_return_are_not_equal_if_Objects_do_not_have_the_same_properties() {
        Assertions.assertThat(fieldByFieldComparator.compare(new FieldByFieldComparator_compareTo_Test.JarJar("Yoda"), 2)).isNotZero();
    }

    public static class JarJar {
        public final String field;

        public JarJar(String field) {
            this.field = field;
        }
    }
}

