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
package org.assertj.core.api.atomic.referencearray;


import java.util.concurrent.atomic.AtomicReferenceArray;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.CartoonCharacter;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;


public class AtomicReferenceArrayAssert_flatExtracting_with_String_parameter_Test {
    private CartoonCharacter bart;

    private CartoonCharacter lisa;

    private CartoonCharacter maggie;

    private CartoonCharacter homer;

    private CartoonCharacter pebbles;

    private CartoonCharacter fred;

    @Test
    public void should_allow_assertions_on_joined_lists_when_extracting_children() {
        AtomicReferenceArray<CartoonCharacter> cartoonCharacters = new AtomicReferenceArray(Arrays.array(homer, fred));
        Assertions.assertThat(cartoonCharacters).flatExtracting("children").containsOnly(bart, lisa, maggie, pebbles);
    }

    @Test
    public void should_allow_assertions_on_joined_lists_when_extracting_children_array() {
        AtomicReferenceArray<CartoonCharacter> cartoonCharacters = new AtomicReferenceArray(Arrays.array(homer, fred));
        Assertions.assertThat(cartoonCharacters).flatExtracting("childrenArray").containsOnly(bart, lisa, maggie, pebbles);
    }

    @Test
    public void should_allow_assertions_on_empty_result_lists() {
        AtomicReferenceArray<CartoonCharacter> childCharacters = new AtomicReferenceArray(Arrays.array(bart, lisa, maggie));
        Assertions.assertThat(childCharacters).flatExtracting("children").isEmpty();
    }

    @Test
    public void should_throw_illegal_argument_exception_when_extracting_from_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(new AtomicReferenceArray<>(array(homer, null))).flatExtracting("children"));
    }

    @Test
    public void should_throw_exception_when_extracted_value_is_not_an_array_or_an_iterable() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(new CartoonCharacter[]{ homer, fred }).flatExtracting("name")).withMessage("Flat extracting expects extracted values to be Iterables or arrays but was a String");
    }
}

