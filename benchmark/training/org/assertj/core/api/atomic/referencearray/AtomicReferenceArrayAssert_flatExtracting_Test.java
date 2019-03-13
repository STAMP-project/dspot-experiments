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


import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.iterable.Extractor;
import org.assertj.core.test.CartoonCharacter;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;


@SuppressWarnings("deprecation")
public class AtomicReferenceArrayAssert_flatExtracting_Test {
    private CartoonCharacter bart;

    private CartoonCharacter lisa;

    private CartoonCharacter maggie;

    private CartoonCharacter homer;

    private CartoonCharacter pebbles;

    private CartoonCharacter fred;

    private final Function<CartoonCharacter, List<CartoonCharacter>> children = CartoonCharacter::getChildren;

    private final Extractor<CartoonCharacter, List<CartoonCharacter>> childrenExtractor = new Extractor<CartoonCharacter, List<CartoonCharacter>>() {
        @Override
        public List<CartoonCharacter> extract(CartoonCharacter input) {
            return input.getChildren();
        }
    };

    @Test
    public void should_allow_assertions_on_joined_lists_when_extracting_children_with_extractor() {
        AtomicReferenceArray<CartoonCharacter> cartoonCharacters = new AtomicReferenceArray(Arrays.array(homer, fred));
        Assertions.assertThat(cartoonCharacters).flatExtracting(childrenExtractor).containsOnly(bart, lisa, maggie, pebbles);
    }

    @Test
    public void should_allow_assertions_on_joined_lists_when_extracting_children() {
        AtomicReferenceArray<CartoonCharacter> cartoonCharacters = new AtomicReferenceArray(Arrays.array(homer, fred));
        Assertions.assertThat(cartoonCharacters).flatExtracting(children).containsOnly(bart, lisa, maggie, pebbles);
    }

    @Test
    public void should_allow_assertions_on_empty_result_lists_with_extractor() {
        AtomicReferenceArray<CartoonCharacter> childCharacters = new AtomicReferenceArray(Arrays.array(bart, lisa, maggie));
        Assertions.assertThat(childCharacters).flatExtracting(childrenExtractor).isEmpty();
    }

    @Test
    public void should_allow_assertions_on_empty_result_lists() {
        AtomicReferenceArray<CartoonCharacter> childCharacters = new AtomicReferenceArray(Arrays.array(bart, lisa, maggie));
        Assertions.assertThat(childCharacters).flatExtracting(children).isEmpty();
    }

    @Test
    public void should_throw_null_pointer_exception_when_extracting_from_null_with_extractor() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(new AtomicReferenceArray<>(array(homer, null))).flatExtracting(childrenExtractor));
    }

    @Test
    public void should_throw_null_pointer_exception_when_extracting_from_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(new AtomicReferenceArray<>(array(homer, null))).flatExtracting(children));
    }

    @Test
    public void should_rethrow_throwing_extractor_checked_exception_as_a_runtime_exception() {
        AtomicReferenceArray<CartoonCharacter> childCharacters = new AtomicReferenceArray(Arrays.array(bart, lisa, maggie));
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> assertThat(childCharacters).flatExtracting(( cartoonCharacter) -> {
            if (cartoonCharacter.getChildren().isEmpty())
                throw new Exception("no children");

            return cartoonCharacter.getChildren();
        })).withMessage("java.lang.Exception: no children");
    }

    @Test
    public void should_let_throwing_extractor_runtime_exception_bubble_up() {
        AtomicReferenceArray<CartoonCharacter> childCharacters = new AtomicReferenceArray(Arrays.array(bart, lisa, maggie));
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> assertThat(childCharacters).flatExtracting(( cartoonCharacter) -> {
            if (cartoonCharacter.getChildren().isEmpty())
                throw new RuntimeException("no children");

            return cartoonCharacter.getChildren();
        })).withMessage("no children");
    }

    @Test
    public void should_allow_assertions_on_joined_lists_when_extracting_children_with_throwing_extractor() {
        AtomicReferenceArray<CartoonCharacter> cartoonCharacters = new AtomicReferenceArray(Arrays.array(homer, fred));
        Assertions.assertThat(cartoonCharacters).flatExtracting(( cartoonCharacter) -> {
            if (cartoonCharacter.getChildren().isEmpty())
                throw new Exception("no children");

            return cartoonCharacter.getChildren();
        }).containsOnly(bart, lisa, maggie, pebbles);
    }

    @Test
    public void should_allow_assertions_on_joined_lists_when_extracting_children_with_anonymous_class_throwing_extractor() {
        AtomicReferenceArray<CartoonCharacter> cartoonCharacters = new AtomicReferenceArray(Arrays.array(homer, fred));
        Assertions.assertThat(cartoonCharacters).flatExtracting(new org.assertj.core.api.iterable.ThrowingExtractor<CartoonCharacter, List<CartoonCharacter>, Exception>() {
            @Override
            public List<CartoonCharacter> extractThrows(CartoonCharacter cartoonCharacter) throws Exception {
                if (cartoonCharacter.getChildren().isEmpty())
                    throw new Exception("no children");

                return cartoonCharacter.getChildren();
            }
        }).containsOnly(bart, lisa, maggie, pebbles);
    }
}

