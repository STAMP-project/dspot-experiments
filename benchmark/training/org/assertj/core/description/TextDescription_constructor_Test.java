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
package org.assertj.core.description;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link TextDescription#TextDescription(String)}</code>.
 *
 * @author Yvonne Wang
 * @author Alex Ruiz
 */
public class TextDescription_constructor_Test {
    @Test
    public void should_set_value() {
        String value = TextDescription_constructor_Test.randomText();
        TextDescription description = new TextDescription(value);
        Assertions.assertThat(description.value).isEqualTo(value);
    }

    @Test
    public void should_return_empty_description_if_value_is_null() {
        TextDescription description = new TextDescription(null);
        Assertions.assertThat(description.value).isEmpty();
    }
}

