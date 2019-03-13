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
package org.assertj.core.groups;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Properties#extractProperty(String, Class)}</code>.
 *
 * @author Yvonne Wang
 * @author Mikhail Mazursky
 */
public class Properties_extractProperty_Test {
    @Test
    public void should_create_a_new_Properties() {
        Properties<Object> properties = Properties.extractProperty("id", Object.class);
        Assertions.assertThat(properties.propertyName).isEqualTo("id");
    }

    @Test
    public void should_throw_error_if_property_name_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> Properties.extractProperty(null, .class)).withMessage("The name of the property to read should not be null");
    }

    @Test
    public void should_throw_error_if_property_name_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> Properties.extractProperty("", .class)).withMessage("The name of the property to read should not be empty");
    }

    @Test
    public void extractProperty_string_Test() {
        Properties_extractProperty_Test.TestItem[] ITEMS = new Properties_extractProperty_Test.TestItem[]{ new Properties_extractProperty_Test.TestItem("n1", "v1"), new Properties_extractProperty_Test.TestItem("n2", "v2") };
        Assertions.assertThat(Properties.extractProperty("name").from(ITEMS).contains("n1")).isTrue();
        Assertions.assertThat(Properties.extractProperty("name", String.class).from(ITEMS).contains("n1")).isTrue();
    }

    private static final class TestItem {
        private final String name;

        private final String value;

        public TestItem(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public String getValue() {
            return value;
        }
    }
}

