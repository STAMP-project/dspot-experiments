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
package org.assertj.core.api.objectarray;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectArrayAssertBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ObjectArrayAssert#doesNotContainSequence(List)}</code>.
 *
 * @author Chris Arnott
 */
public class ObjectArrayAssert_doesNotContainSequence_List_Test extends ObjectArrayAssertBaseTest {
    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            List<Object> nullList = null;
            assertions.doesNotContainSequence(nullList);
        }).withMessage(ErrorMessages.nullSequence());
    }
}

