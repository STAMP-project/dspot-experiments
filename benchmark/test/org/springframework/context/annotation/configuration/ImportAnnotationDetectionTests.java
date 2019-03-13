/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.context.annotation.configuration;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Tests that @Import may be used both as a locally declared and meta-declared
 * annotation, that all declarations are processed, and that any local declaration
 * is processed last.
 *
 * @author Chris Beams
 * @since 3.1
 */
@SuppressWarnings("resource")
public class ImportAnnotationDetectionTests {
    @Test
    public void multipleMetaImportsAreProcessed() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ImportAnnotationDetectionTests.MultiMetaImportConfig.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("testBean1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("testBean2"), CoreMatchers.is(true));
    }

    @Test
    public void localAndMetaImportsAreProcessed() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ImportAnnotationDetectionTests.MultiMetaImportConfigWithLocalImport.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("testBean1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("testBean2"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("testBean3"), CoreMatchers.is(true));
    }

    @Test
    public void localImportIsProcessedLast() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ImportAnnotationDetectionTests.MultiMetaImportConfigWithLocalImportWithBeanOverride.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("testBean1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("testBean2"), CoreMatchers.is(true));
        Assert.assertThat(ctx.getBean("testBean2", TestBean.class).getName(), CoreMatchers.is("2a"));
    }

    @Test
    public void importFromBean() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ImportAnnotationDetectionTests.ImportFromBean.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("importAnnotationDetectionTests.ImportFromBean"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("testBean1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.getBean("testBean1", TestBean.class).getName(), CoreMatchers.is("1"));
    }

    @Configuration
    @ImportAnnotationDetectionTests.MetaImport1
    @ImportAnnotationDetectionTests.MetaImport2
    static class MultiMetaImportConfig {}

    @Configuration
    @ImportAnnotationDetectionTests.MetaImport1
    @ImportAnnotationDetectionTests.MetaImport2
    @Import(ImportAnnotationDetectionTests.Config3.class)
    static class MultiMetaImportConfigWithLocalImport {}

    @Configuration
    @ImportAnnotationDetectionTests.MetaImport1
    @ImportAnnotationDetectionTests.MetaImport2
    @Import(ImportAnnotationDetectionTests.Config2a.class)
    static class MultiMetaImportConfigWithLocalImportWithBeanOverride {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(ImportAnnotationDetectionTests.Config1.class)
    @interface MetaImport1 {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(ImportAnnotationDetectionTests.Config2.class)
    @interface MetaImport2 {}

    @Configuration
    static class Config1 {
        @Bean
        TestBean testBean1() {
            return new TestBean("1");
        }
    }

    @Configuration
    static class Config2 {
        @Bean
        TestBean testBean2() {
            return new TestBean("2");
        }
    }

    @Configuration
    static class Config2a {
        @Bean
        TestBean testBean2() {
            return new TestBean("2a");
        }
    }

    @Configuration
    static class Config3 {
        @Bean
        TestBean testBean3() {
            return new TestBean("3");
        }
    }

    @ImportAnnotationDetectionTests.MetaImport1
    static class ImportFromBean {}
}

