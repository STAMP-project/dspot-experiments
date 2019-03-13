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
package org.springframework.boot.test.autoconfigure.filter;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;


/**
 * Tests for {@link TypeExcludeFiltersContextCustomizerFactory}.
 *
 * @author Phillip Webb
 */
public class TypeExcludeFiltersContextCustomizerFactoryTests {
    private TypeExcludeFiltersContextCustomizerFactory factory = new TypeExcludeFiltersContextCustomizerFactory();

    private MergedContextConfiguration mergedContextConfiguration = Mockito.mock(MergedContextConfiguration.class);

    private ConfigurableApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void getContextCustomizerWhenHasNoAnnotationShouldReturnNull() {
        ContextCustomizer customizer = this.factory.createContextCustomizer(TypeExcludeFiltersContextCustomizerFactoryTests.NoAnnotation.class, null);
        assertThat(customizer).isNull();
    }

    @Test
    public void getContextCustomizerWhenHasAnnotationShouldReturnCustomizer() {
        ContextCustomizer customizer = this.factory.createContextCustomizer(TypeExcludeFiltersContextCustomizerFactoryTests.WithExcludeFilters.class, null);
        assertThat(customizer).isNotNull();
    }

    @Test
    public void hashCodeAndEquals() {
        ContextCustomizer customizer1 = this.factory.createContextCustomizer(TypeExcludeFiltersContextCustomizerFactoryTests.WithExcludeFilters.class, null);
        ContextCustomizer customizer2 = this.factory.createContextCustomizer(TypeExcludeFiltersContextCustomizerFactoryTests.WithSameExcludeFilters.class, null);
        ContextCustomizer customizer3 = this.factory.createContextCustomizer(TypeExcludeFiltersContextCustomizerFactoryTests.WithDifferentExcludeFilters.class, null);
        assertThat(customizer1.hashCode()).isEqualTo(customizer2.hashCode());
        assertThat(customizer1).isEqualTo(customizer1).isEqualTo(customizer2).isNotEqualTo(customizer3);
    }

    @Test
    public void getContextCustomizerShouldAddExcludeFilters() throws Exception {
        ContextCustomizer customizer = this.factory.createContextCustomizer(TypeExcludeFiltersContextCustomizerFactoryTests.WithExcludeFilters.class, null);
        customizer.customizeContext(this.context, this.mergedContextConfiguration);
        this.context.refresh();
        TypeExcludeFilter filter = this.context.getBean(TypeExcludeFilter.class);
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(TypeExcludeFiltersContextCustomizerFactoryTests.NoAnnotation.class.getName());
        assertThat(filter.match(metadataReader, metadataReaderFactory)).isFalse();
        metadataReader = metadataReaderFactory.getMetadataReader(TypeExcludeFiltersContextCustomizerFactoryTests.SimpleExclude.class.getName());
        assertThat(filter.match(metadataReader, metadataReaderFactory)).isTrue();
        metadataReader = metadataReaderFactory.getMetadataReader(TypeExcludeFiltersContextCustomizerFactoryTests.TestClassAwareExclude.class.getName());
        assertThat(filter.match(metadataReader, metadataReaderFactory)).isTrue();
    }

    static class NoAnnotation {}

    @TypeExcludeFilters({ TypeExcludeFiltersContextCustomizerFactoryTests.SimpleExclude.class, TypeExcludeFiltersContextCustomizerFactoryTests.TestClassAwareExclude.class })
    static class WithExcludeFilters {}

    @TypeExcludeFilters({ TypeExcludeFiltersContextCustomizerFactoryTests.TestClassAwareExclude.class, TypeExcludeFiltersContextCustomizerFactoryTests.SimpleExclude.class })
    static class WithSameExcludeFilters {}

    @TypeExcludeFilters(TypeExcludeFiltersContextCustomizerFactoryTests.SimpleExclude.class)
    static class WithDifferentExcludeFilters {}

    static class SimpleExclude extends TypeExcludeFilter {
        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            return metadataReader.getClassMetadata().getClassName().equals(getClass().getName());
        }

        @Override
        public boolean equals(Object obj) {
            return (obj.getClass()) == (getClass());
        }

        @Override
        public int hashCode() {
            return TypeExcludeFiltersContextCustomizerFactoryTests.SimpleExclude.class.hashCode();
        }
    }

    static class TestClassAwareExclude extends TypeExcludeFiltersContextCustomizerFactoryTests.SimpleExclude {
        TestClassAwareExclude(Class<?> testClass) {
            assertThat(testClass).isNotNull();
        }
    }
}

