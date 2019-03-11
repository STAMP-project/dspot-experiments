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
package org.assertj.core.data;


import org.assertj.core.api.Assertions;
import org.assertj.core.test.EqualsHashCodeContractAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link Index#equals(Object)} and {@link Index#hashCode()}.
 *
 * @author Alex Ruiz
 */
public class Index_equals_hashCode_Test {
    private static Index index;

    @Test
    public void should_have_reflexive_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsReflexive(Index_equals_hashCode_Test.index);
    }

    @Test
    public void should_have_symmetric_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsSymmetric(Index_equals_hashCode_Test.index, Index.atIndex(8));
    }

    @Test
    public void should_have_transitive_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsTransitive(Index_equals_hashCode_Test.index, Index.atIndex(8), Index.atIndex(8));
    }

    @Test
    public void should_maintain_equals_and_hashCode_contract() {
        EqualsHashCodeContractAssert.assertMaintainsEqualsAndHashCodeContract(Index_equals_hashCode_Test.index, Index.atIndex(8));
    }

    @Test
    public void should_not_be_equal_to_Object_of_different_type() {
        Assertions.assertThat(Index_equals_hashCode_Test.index.equals("8")).isFalse();
    }

    @Test
    public void should_not_be_equal_to_null() {
        Assertions.assertThat(Index_equals_hashCode_Test.index.equals(null)).isFalse();
    }

    @Test
    public void should_not_be_equal_to_Index_with_different_value() {
        Assertions.assertThat(Index_equals_hashCode_Test.index.equals(Index.atIndex(6))).isFalse();
    }
}

