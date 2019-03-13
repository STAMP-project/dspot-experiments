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
package org.assertj.core.util.diff;


import org.assertj.core.api.Assertions;
import org.assertj.core.test.EqualsHashCodeContractAssert;
import org.junit.jupiter.api.Test;


public class ChunkTest {
    private Chunk<String> chunk;

    @Test
    public void should_have_reflexive_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsReflexive(chunk);
    }

    @Test
    public void should_have_symmetric_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsSymmetric(chunk, new Chunk(1, ChunkTest.emptyList()));
    }

    @Test
    public void should_have_transitive_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsTransitive(chunk, new Chunk(1, ChunkTest.emptyList()), new Chunk(1, ChunkTest.emptyList()));
    }

    @Test
    public void should_maintain_equals_and_hashCode_contract() {
        EqualsHashCodeContractAssert.assertMaintainsEqualsAndHashCodeContract(chunk, new Chunk(1, ChunkTest.emptyList()));
    }

    @Test
    public void should_not_be_equal_to_Object_of_different_type() {
        Assertions.assertThat(chunk.equals("8")).isFalse();
    }

    @Test
    public void should_not_be_equal_to_null() {
        Assertions.assertThat(chunk.equals(null)).isFalse();
    }

    @Test
    public void should_not_be_equal_to_Chunk_with_different_value() {
        Assertions.assertThat(chunk.equals(new Chunk(2, ChunkTest.emptyList()))).isFalse();
    }

    @Test
    public void should_have_nice_toString_value() {
        Assertions.assertThat(chunk).hasToString("[position: 1, size: 0, lines: []]");
    }
}

