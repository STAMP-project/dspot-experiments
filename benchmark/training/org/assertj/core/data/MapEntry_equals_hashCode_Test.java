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


import java.util.Collections;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.EqualsHashCodeContractAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link MapEntry#equals(Object)} and {@link MapEntry#hashCode()}.
 *
 * @author Alex Ruiz
 */
public class MapEntry_equals_hashCode_Test {
    private static MapEntry<String, String> entry;

    private static Map.Entry<String, String> javaEntry;

    @Test
    public void should_have_reflexive_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsReflexive(MapEntry_equals_hashCode_Test.entry);
    }

    @Test
    public void should_have_symmetric_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsSymmetric(MapEntry_equals_hashCode_Test.entry, MapEntry.entry("key", "value"));
    }

    @Test
    public void should_have_transitive_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsTransitive(MapEntry_equals_hashCode_Test.entry, MapEntry.entry("key", "value"), MapEntry.entry("key", "value"));
    }

    @Test
    public void should_maintain_equals_and_hashCode_contract() {
        EqualsHashCodeContractAssert.assertMaintainsEqualsAndHashCodeContract(MapEntry_equals_hashCode_Test.entry, MapEntry.entry("key", "value"));
    }

    @Test
    public void should_not_be_equal_to_Object_of_different_type() {
        Assertions.assertThat(MapEntry_equals_hashCode_Test.entry.equals("{'key', 'value'}")).isFalse();
    }

    @Test
    public void should_not_be_equal_to_null() {
        Assertions.assertThat(MapEntry_equals_hashCode_Test.entry.equals(null)).isFalse();
    }

    @Test
    public void should_not_be_equal_to_MapEntry_with_different_value() {
        Assertions.assertThat(MapEntry_equals_hashCode_Test.entry.equals(MapEntry.entry("key0", "value0"))).isFalse();
    }

    @Test
    public void should_have_symmetric_equals_with_java_MapEntry() {
        EqualsHashCodeContractAssert.assertEqualsIsSymmetric(MapEntry_equals_hashCode_Test.javaEntry, MapEntry_equals_hashCode_Test.entry);
    }

    @Test
    public void should_maintain_equals_and_hashCode_contract_with_java_MapEntry() {
        EqualsHashCodeContractAssert.assertMaintainsEqualsAndHashCodeContract(MapEntry_equals_hashCode_Test.javaEntry, MapEntry_equals_hashCode_Test.entry);
    }

    @Test
    public void should_have_transitive_equals_with_java_MapEntry() {
        Map.Entry<String, String> javaEntry2 = Collections.singletonMap("key", "value").entrySet().iterator().next();
        EqualsHashCodeContractAssert.assertEqualsIsTransitive(MapEntry_equals_hashCode_Test.entry, MapEntry_equals_hashCode_Test.javaEntry, javaEntry2);
    }
}

