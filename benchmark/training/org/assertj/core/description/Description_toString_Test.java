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
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Description#toString()}</code>.
 *
 * @author Yvonne Wang
 */
public class Description_toString_Test {
    private Description_toString_Test.ValueSource valueSource;

    private Description description;

    @Test
    public void should_return_value_in_toString() {
        Mockito.when(valueSource.value()).thenReturn("Yoda");
        Assertions.assertThat(description).hasToString("Yoda");
    }

    private interface ValueSource {
        String value();
    }

    private static class TestDescription extends Description {
        private final Description_toString_Test.ValueSource source;

        TestDescription(Description_toString_Test.ValueSource source) {
            this.source = source;
        }

        @Override
        public String value() {
            return source.value();
        }
    }
}

