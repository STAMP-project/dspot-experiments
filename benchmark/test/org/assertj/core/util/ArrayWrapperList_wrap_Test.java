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
 * Tests for <code>{@link ArrayWrapperList#wrap(Object)}</code>.
 *
 * @author Alex Ruiz
 */
public class ArrayWrapperList_wrap_Test {
    @Test
    public void should_create_ArrayWrapperList_if_array_is_not_null() {
        int[] array = new int[]{ 6, 8 };
        ArrayWrapperList list = ArrayWrapperList.wrap(array);
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.array).isSameAs(array);
    }

    @Test
    public void should_return_null_if_array_is_null() {
        Assertions.assertThat(ArrayWrapperList.wrap(null)).isNull();
    }

    @Test
    public void should_throw_error_if_parameter_is_not_array() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> ArrayWrapperList.wrap("Yoda")).withMessage("The object to wrap should be an array");
    }
}

