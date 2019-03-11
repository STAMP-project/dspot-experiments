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
package org.assertj.core.error;


import org.assertj.core.api.Assertions;
import org.assertj.core.test.EqualsHashCodeContractAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link BasicErrorMessageFactory#equals(Object)}</code> and
 * <code>{@link BasicErrorMessageFactory#hashCode()}</code>.
 *
 * @author Yvonne Wang
 */
public class BasicErrorMessageFactory_equals_hashCode_Test {
    private static BasicErrorMessageFactory factory;

    @Test
    public void should_have_reflexive_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsReflexive(BasicErrorMessageFactory_equals_hashCode_Test.factory);
    }

    @Test
    public void should_have_symmetric_equals() {
        EqualsHashCodeContractAssert.assertEqualsIsSymmetric(BasicErrorMessageFactory_equals_hashCode_Test.factory, new BasicErrorMessageFactory("Hello %s", "Yoda"));
    }

    @Test
    public void should_have_transitive_equals() {
        BasicErrorMessageFactory obj2 = new BasicErrorMessageFactory("Hello %s", "Yoda");
        BasicErrorMessageFactory obj3 = new BasicErrorMessageFactory("Hello %s", "Yoda");
        EqualsHashCodeContractAssert.assertEqualsIsTransitive(BasicErrorMessageFactory_equals_hashCode_Test.factory, obj2, obj3);
    }

    @Test
    public void should_maintain_equals_and_hashCode_contract() {
        EqualsHashCodeContractAssert.assertMaintainsEqualsAndHashCodeContract(BasicErrorMessageFactory_equals_hashCode_Test.factory, new BasicErrorMessageFactory("Hello %s", "Yoda"));
    }

    @Test
    public void should_not_be_equal_to_Object_of_different_type() {
        Assertions.assertThat(BasicErrorMessageFactory_equals_hashCode_Test.factory.equals("Yoda")).isFalse();
    }

    @Test
    public void should_not_be_equal_to_null() {
        Assertions.assertThat(BasicErrorMessageFactory_equals_hashCode_Test.factory.equals(null)).isFalse();
    }

    @Test
    public void should_not_be_equal_to_BasicErrorMessage_with_different_format() {
        Assertions.assertThat(BasicErrorMessageFactory_equals_hashCode_Test.factory.equals(new BasicErrorMessageFactory("How are you, %s?", "Yoda"))).isFalse();
    }

    @Test
    public void should_not_be_equal_to_BasicErrorMessage_with_different_arguments() {
        Assertions.assertThat(BasicErrorMessageFactory_equals_hashCode_Test.factory.equals(new BasicErrorMessageFactory("Hello %s", "Luke"))).isFalse();
    }
}

