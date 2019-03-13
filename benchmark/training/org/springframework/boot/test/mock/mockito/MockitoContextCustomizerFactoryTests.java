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


import org.junit.Test;
import org.springframework.test.context.ContextCustomizer;


/**
 * Tests for {@link MockitoContextCustomizerFactory}.
 *
 * @author Phillip Webb
 */
public class MockitoContextCustomizerFactoryTests {
    private final MockitoContextCustomizerFactory factory = new MockitoContextCustomizerFactory();

    @Test
    public void getContextCustomizerWithoutAnnotationReturnsCustomizer() {
        ContextCustomizer customizer = this.factory.createContextCustomizer(MockitoContextCustomizerFactoryTests.NoMockBeanAnnotation.class, null);
        assertThat(customizer).isNotNull();
    }

    @Test
    public void getContextCustomizerWithAnnotationReturnsCustomizer() {
        ContextCustomizer customizer = this.factory.createContextCustomizer(MockitoContextCustomizerFactoryTests.WithMockBeanAnnotation.class, null);
        assertThat(customizer).isNotNull();
    }

    @Test
    public void getContextCustomizerUsesMocksAsCacheKey() {
        ContextCustomizer customizer = this.factory.createContextCustomizer(MockitoContextCustomizerFactoryTests.WithMockBeanAnnotation.class, null);
        assertThat(customizer).isNotNull();
        ContextCustomizer same = this.factory.createContextCustomizer(MockitoContextCustomizerFactoryTests.WithSameMockBeanAnnotation.class, null);
        assertThat(customizer).isNotNull();
        ContextCustomizer different = this.factory.createContextCustomizer(MockitoContextCustomizerFactoryTests.WithDifferentMockBeanAnnotation.class, null);
        assertThat(different).isNotNull();
        assertThat(customizer.hashCode()).isEqualTo(same.hashCode());
        assertThat(customizer.hashCode()).isNotEqualTo(different.hashCode());
        assertThat(customizer).isEqualTo(customizer);
        assertThat(customizer).isEqualTo(same);
        assertThat(customizer).isNotEqualTo(different);
    }

    static class NoMockBeanAnnotation {}

    @MockBean({ MockitoContextCustomizerFactoryTests.Service1.class, MockitoContextCustomizerFactoryTests.Service2.class })
    static class WithMockBeanAnnotation {}

    @MockBean({ MockitoContextCustomizerFactoryTests.Service2.class, MockitoContextCustomizerFactoryTests.Service1.class })
    static class WithSameMockBeanAnnotation {}

    @MockBean({ MockitoContextCustomizerFactoryTests.Service1.class })
    static class WithDifferentMockBeanAnnotation {}

    interface Service1 {}

    interface Service2 {}
}

