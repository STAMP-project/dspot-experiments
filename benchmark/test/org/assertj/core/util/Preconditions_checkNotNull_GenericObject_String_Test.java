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
 * Tests for <code>{@link Preconditions#checkNotNull(Object, String)}</code>.
 *
 * @author Christian R?sch
 */
public class Preconditions_checkNotNull_GenericObject_String_Test {
    private static final String CUSTOM_MESSAGE = "Wow, that's an error dude ..";

    @Test
    public void should_throw_nullpointerexception_if_object_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Object object = null;
            Preconditions.checkNotNull(object, CUSTOM_MESSAGE);
        }).withMessage(Preconditions_checkNotNull_GenericObject_String_Test.CUSTOM_MESSAGE);
    }

    @Test
    public void should_return_object_if_it_is_not_null_nor_empty() {
        String object = "4711";
        String result = Preconditions.checkNotNull(object, Preconditions_checkNotNull_GenericObject_String_Test.CUSTOM_MESSAGE);
        Assertions.assertThat(result).isEqualTo(object);
    }
}

