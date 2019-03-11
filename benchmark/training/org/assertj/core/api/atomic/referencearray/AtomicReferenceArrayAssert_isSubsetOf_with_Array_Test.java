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


import org.assertj.core.api.Assertions;
import org.assertj.core.api.AtomicReferenceArrayAssertBaseTest;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;


public class AtomicReferenceArrayAssert_isSubsetOf_with_Array_Test extends AtomicReferenceArrayAssertBaseTest {
    private final Object[] values = Arrays.array("Yoda", "Luke");

    @Test
    public void invoke_api_like_user() {
        Assertions.assertThat(new java.util.concurrent.atomic.AtomicReferenceArray(Arrays.array("Luke", "Yoda"))).isSubsetOf("Yoda", "Luke", "Chewbacca");
    }
}

