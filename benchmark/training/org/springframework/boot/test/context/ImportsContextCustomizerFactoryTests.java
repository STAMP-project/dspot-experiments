/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.test.context;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;


/**
 * Tests for {@link ImportsContextCustomizerFactory} and {@link ImportsContextCustomizer}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ImportsContextCustomizerFactoryTests {
    private ImportsContextCustomizerFactory factory = new ImportsContextCustomizerFactory();

    @Test
    public void getContextCustomizerWhenHasNoImportAnnotationShouldReturnNull() {
        ContextCustomizer customizer = this.factory.createContextCustomizer(ImportsContextCustomizerFactoryTests.TestWithNoImport.class, null);
        assertThat(customizer).isNull();
    }

    @Test
    public void getContextCustomizerWhenHasImportAnnotationShouldReturnCustomizer() {
        ContextCustomizer customizer = this.factory.createContextCustomizer(ImportsContextCustomizerFactoryTests.TestWithImport.class, null);
        assertThat(customizer).isNotNull();
    }

    @Test
    public void getContextCustomizerWhenHasMetaImportAnnotationShouldReturnCustomizer() {
        ContextCustomizer customizer = this.factory.createContextCustomizer(ImportsContextCustomizerFactoryTests.TestWithMetaImport.class, null);
        assertThat(customizer).isNotNull();
    }

    @Test
    public void contextCustomizerEqualsAndHashCode() {
        ContextCustomizer customizer1 = this.factory.createContextCustomizer(ImportsContextCustomizerFactoryTests.TestWithImport.class, null);
        ContextCustomizer customizer2 = this.factory.createContextCustomizer(ImportsContextCustomizerFactoryTests.TestWithImport.class, null);
        ContextCustomizer customizer3 = this.factory.createContextCustomizer(ImportsContextCustomizerFactoryTests.TestWithImportAndMetaImport.class, null);
        ContextCustomizer customizer4 = this.factory.createContextCustomizer(ImportsContextCustomizerFactoryTests.TestWithSameImportAndMetaImport.class, null);
        assertThat(customizer1.hashCode()).isEqualTo(customizer1.hashCode());
        assertThat(customizer1.hashCode()).isEqualTo(customizer2.hashCode());
        assertThat(customizer1).isEqualTo(customizer1).isEqualTo(customizer2).isNotEqualTo(customizer3);
        assertThat(customizer3).isEqualTo(customizer4);
    }

    @Test
    public void getContextCustomizerWhenClassHasBeanMethodsShouldThrowException() {
        assertThatIllegalStateException().isThrownBy(() -> this.factory.createContextCustomizer(.class, null)).withMessageContaining("Test classes cannot include @Bean methods");
    }

    @Test
    public void contextCustomizerImportsBeans() {
        ContextCustomizer customizer = this.factory.createContextCustomizer(ImportsContextCustomizerFactoryTests.TestWithImport.class, null);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        customizer.customizeContext(context, Mockito.mock(MergedContextConfiguration.class));
        context.refresh();
        assertThat(context.getBean(ImportsContextCustomizerFactoryTests.ImportedBean.class)).isNotNull();
    }

    @Test
    public void selfAnnotatingAnnotationDoesNotCauseStackOverflow() {
        assertThat(this.factory.createContextCustomizer(ImportsContextCustomizerFactoryTests.TestWithImportAndSelfAnnotatingAnnotation.class, null)).isNotNull();
    }

    static class TestWithNoImport {}

    @Import(ImportsContextCustomizerFactoryTests.ImportedBean.class)
    static class TestWithImport {}

    @ImportsContextCustomizerFactoryTests.MetaImport
    static class TestWithMetaImport {}

    @ImportsContextCustomizerFactoryTests.MetaImport
    @Import(ImportsContextCustomizerFactoryTests.AnotherImportedBean.class)
    static class TestWithImportAndMetaImport {}

    @ImportsContextCustomizerFactoryTests.MetaImport
    @Import(ImportsContextCustomizerFactoryTests.AnotherImportedBean.class)
    static class TestWithSameImportAndMetaImport {}

    @Configuration
    @Import(ImportsContextCustomizerFactoryTests.ImportedBean.class)
    static class TestWithImportAndBeanMethod {
        @Bean
        public String bean() {
            return "bean";
        }
    }

    @ImportsContextCustomizerFactoryTests.SelfAnnotating
    @Import(ImportsContextCustomizerFactoryTests.ImportedBean.class)
    static class TestWithImportAndSelfAnnotatingAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    @Import(ImportsContextCustomizerFactoryTests.ImportedBean.class)
    @interface MetaImport {}

    @Component
    static class ImportedBean {}

    @Component
    static class AnotherImportedBean {}

    @Retention(RetentionPolicy.RUNTIME)
    @ImportsContextCustomizerFactoryTests.SelfAnnotating
    @interface SelfAnnotating {}
}

