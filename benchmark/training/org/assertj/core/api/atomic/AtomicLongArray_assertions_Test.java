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
package org.assertj.core.api.atomic;


import java.util.concurrent.atomic.AtomicLongArray;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;


public class AtomicLongArray_assertions_Test {
    @Test
    public void should_accept_null_atomicLongArray() {
        AtomicLongArray actual = null;
        Assertions.assertThat(actual).isNull();
        BDDAssertions.then(((AtomicLongArray) (null))).isNull();
    }

    @Test
    public void should_be_able_to_use_any_long_array_assertions() {
        AtomicLongArray actual = new AtomicLongArray(new long[]{ 1, 2, 3, 4 });
        Assertions.assertThat(actual).startsWith(1, 2).contains(3, Assertions.atIndex(2)).endsWith(4);
        BDDAssertions.then(actual).containsExactly(1, 2, 3, 4);
    }
}

