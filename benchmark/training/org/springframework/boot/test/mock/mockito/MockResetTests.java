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
package org.springframework.boot.test.mock.mockito;


import MockReset.AFTER;
import MockReset.BEFORE;
import MockReset.NONE;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.example.ExampleService;


/**
 * Tests for {@link MockReset}.
 *
 * @author Phillip Webb
 */
public class MockResetTests {
    @Test
    public void noneAttachesReset() {
        ExampleService mock = Mockito.mock(ExampleService.class);
        assertThat(MockReset.get(mock)).isEqualTo(NONE);
    }

    @Test
    public void withSettingsOfNoneAttachesReset() {
        ExampleService mock = Mockito.mock(ExampleService.class, MockReset.withSettings(NONE));
        assertThat(MockReset.get(mock)).isEqualTo(NONE);
    }

    @Test
    public void beforeAttachesReset() {
        ExampleService mock = Mockito.mock(ExampleService.class, MockReset.before());
        assertThat(MockReset.get(mock)).isEqualTo(BEFORE);
    }

    @Test
    public void afterAttachesReset() {
        ExampleService mock = Mockito.mock(ExampleService.class, MockReset.after());
        assertThat(MockReset.get(mock)).isEqualTo(AFTER);
    }

    @Test
    public void withSettingsAttachesReset() {
        ExampleService mock = Mockito.mock(ExampleService.class, MockReset.withSettings(BEFORE));
        assertThat(MockReset.get(mock)).isEqualTo(BEFORE);
    }

    @Test
    public void apply() {
        ExampleService mock = Mockito.mock(ExampleService.class, MockReset.apply(AFTER, Mockito.withSettings()));
        assertThat(MockReset.get(mock)).isEqualTo(AFTER);
    }
}

