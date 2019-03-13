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
package org.assertj.core.api.bytearray;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.ByteArrayAssertBaseTest;
import org.assertj.core.util.AbsValueComparator;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.api.ByteArrayAssert#containsExactly(int...)}</code>.
 */
public class ByteArrayAssert_containsExactly_with_Integer_Arguments_Test extends ByteArrayAssertBaseTest {
    @Test
    public void invoke_api_like_user() {
        Assertions.assertThat(new byte[]{ 1, 2, 3 }).containsExactly(1, 2, 3);
    }

    @Test
    public void should_honor_the_given_element_comparator() {
        byte[] actual = new byte[]{ 1, 2, 3, 4 };
        Assertions.assertThat(actual).usingElementComparator(new AbsValueComparator<Byte>()).containsExactly((-1), 2, 3, (-4));
    }
}

