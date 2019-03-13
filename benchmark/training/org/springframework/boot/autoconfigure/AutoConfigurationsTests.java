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
package org.springframework.boot.autoconfigure;


import org.junit.Test;
import org.springframework.boot.context.annotation.Configurations;


/**
 * Tests for {@link AutoConfigurations}.
 *
 * @author Phillip Webb
 */
public class AutoConfigurationsTests {
    @Test
    public void ofShouldCreateOrderedConfigurations() {
        Configurations configurations = AutoConfigurations.of(AutoConfigurationsTests.AutoConfigureA.class, AutoConfigurationsTests.AutoConfigureB.class);
        assertThat(Configurations.getClasses(configurations)).containsExactly(AutoConfigurationsTests.AutoConfigureB.class, AutoConfigurationsTests.AutoConfigureA.class);
    }

    @AutoConfigureAfter(AutoConfigurationsTests.AutoConfigureB.class)
    public static class AutoConfigureA {}

    public static class AutoConfigureB {}
}

