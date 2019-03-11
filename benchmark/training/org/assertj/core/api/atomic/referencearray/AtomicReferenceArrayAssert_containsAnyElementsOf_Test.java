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


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AtomicReferenceArrayAssertBaseTest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.assertj.core.util.Arrays.array;


public class AtomicReferenceArrayAssert_containsAnyElementsOf_Test extends AtomicReferenceArrayAssertBaseTest {
    private final List<Object> iterable = Arrays.asList(new Object(), "bar");

    @Test
    public void should_allow_assertion_on_atomic_reference_array() {
        // GIVEN
        AtomicReferenceArray<Object> objectArray = new AtomicReferenceArray(array("foo", "bar"));
        AtomicReferenceArray<String> stringArray = new AtomicReferenceArray(array("foo", "bar"));
        // THEN
        Assertions.assertThat(objectArray).containsAnyElementsOf(Lists.list("foo", "baz"));
        Assertions.assertThat(stringArray).containsAnyElementsOf(Lists.list("foo", "baz"));
    }
}

