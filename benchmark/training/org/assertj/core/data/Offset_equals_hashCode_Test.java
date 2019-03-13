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
 * Tests for {@link Offset#equals(Object)} and {@link Offset#hashCode()}.
 *
 * @author Alex Ruiz
 */
public class Offset_equals_hashCode_Test {
    private static Offset<Integer> offset;

    private static Offset<Integer> strictOffset;

    @Test
    public void should_have_reflexive_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsReflexive(Offset_equals_hashCode_Test.offset);
        EqualsHashCodeContractAssert.assertEqualsIsReflexive(Offset_equals_hashCode_Test.strictOffset);
    }

    @Test
    public void should_have_symmetric_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsSymmetric(Offset_equals_hashCode_Test.offset, Offset.offset(8));
        EqualsHashCodeContractAssert.assertEqualsIsSymmetric(Offset_equals_hashCode_Test.strictOffset, Offset.strictOffset(8));
    }

    @Test
    public void should_have_transitive_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsTransitive(Offset_equals_hashCode_Test.offset, Offset.offset(8), Offset.offset(8));
        EqualsHashCodeContractAssert.assertEqualsIsTransitive(Offset_equals_hashCode_Test.strictOffset, Offset.strictOffset(8), Offset.strictOffset(8));
    }

    @Test
    public void should_maintain_equals_and_hashCode_contract() {
        EqualsHashCodeContractAssert.assertMaintainsEqualsAndHashCodeContract(Offset_equals_hashCode_Test.offset, Offset.offset(8));
        EqualsHashCodeContractAssert.assertMaintainsEqualsAndHashCodeContract(Offset_equals_hashCode_Test.strictOffset, Offset.strictOffset(8));
    }

    @Test
    public void should_not_be_equal_to_Object_of_different_type() {
        Assertions.assertThat(Offset_equals_hashCode_Test.offset.equals("8")).isFalse();
        Assertions.assertThat(Offset_equals_hashCode_Test.strictOffset.equals("8")).isFalse();
    }

    @Test
    public void should_not_be_equal_to_null() {
        Assertions.assertThat(Offset_equals_hashCode_Test.offset.equals(null)).isFalse();
        Assertions.assertThat(Offset_equals_hashCode_Test.strictOffset.equals(null)).isFalse();
    }

    @Test
    public void should_not_be_equal_to_Offset_with_different_value() {
        Assertions.assertThat(Offset_equals_hashCode_Test.offset.equals(Offset.offset(6))).isFalse();
        Assertions.assertThat(Offset_equals_hashCode_Test.strictOffset.equals(Offset.strictOffset(6))).isFalse();
    }
}

