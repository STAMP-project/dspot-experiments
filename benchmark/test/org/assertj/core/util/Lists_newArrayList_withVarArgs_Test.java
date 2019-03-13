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
package org.assertj.core.util;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link Lists#newArrayList(Object...)}.
 *
 * @author Yvonne Wang
 * @author Alex Ruiz
 */
public class Lists_newArrayList_withVarArgs_Test {
    @Test
    public void should_return_List_containing_all_elements_in_array() {
        // GIVEN
        String[] expected = new String[]{ "One", "Two" };
        // THEN
        Assertions.assertThat(Lists.newArrayList(expected).toArray()).isEqualTo(expected);
        Assertions.assertThat(Lists.list(expected).toArray()).isEqualTo(expected);
    }

    @Test
    public void should_return_null_if_array_is_null() {
        // GIVEN
        Object[] elements = null;
        // THEN
        Assertions.assertThat(Lists.newArrayList(elements)).isNull();
        Assertions.assertThat(Lists.list(elements)).isNull();
    }

    @Test
    public void should_return_empty_List_if_array_is_empty() {
        // GIVEN
        Object[] elements = new Object[0];
        // THEN
        Assertions.assertThat(Lists.newArrayList(elements)).isEmpty();
        Assertions.assertThat(Lists.list(elements)).isEmpty();
    }
}

