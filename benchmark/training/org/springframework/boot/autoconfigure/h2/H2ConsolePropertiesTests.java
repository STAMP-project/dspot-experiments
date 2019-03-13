/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.autoconfigure.h2;


import org.junit.Test;


/**
 * Tests for {@link H2ConsoleProperties}.
 *
 * @author Madhura Bhave
 */
public class H2ConsolePropertiesTests {
    private H2ConsoleProperties properties;

    @Test
    public void pathMustNotBeEmpty() {
        this.properties = new H2ConsoleProperties();
        assertThatIllegalArgumentException().isThrownBy(() -> this.properties.setPath("")).withMessageContaining("Path must have length greater than 1");
    }

    @Test
    public void pathMustHaveLengthGreaterThanOne() {
        this.properties = new H2ConsoleProperties();
        assertThatIllegalArgumentException().isThrownBy(() -> this.properties.setPath("/")).withMessageContaining("Path must have length greater than 1");
    }

    @Test
    public void customPathMustBeginWithASlash() {
        this.properties = new H2ConsoleProperties();
        assertThatIllegalArgumentException().isThrownBy(() -> this.properties.setPath("custom")).withMessageContaining("Path must start with '/'");
    }
}

