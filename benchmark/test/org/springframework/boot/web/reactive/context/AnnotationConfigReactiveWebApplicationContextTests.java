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
package org.springframework.boot.web.reactive.context;


import org.junit.Test;
import org.springframework.context.annotation.Lazy;


/**
 * Tests for {@link AnnotationConfigReactiveWebApplicationContext}
 *
 * @author Stephane Nicoll
 */
public class AnnotationConfigReactiveWebApplicationContextTests {
    private AnnotationConfigReactiveWebApplicationContext context;

    @Test
    public void registerBean() {
        this.context = new AnnotationConfigReactiveWebApplicationContext();
        this.context.registerBean(AnnotationConfigReactiveWebApplicationContextTests.TestBean.class);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigReactiveWebApplicationContextTests.TestBean")).isTrue();
        assertThat(this.context.getBean(AnnotationConfigReactiveWebApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Test
    public void registerBeanWithLazy() {
        this.context = new AnnotationConfigReactiveWebApplicationContext();
        this.context.registerBean(AnnotationConfigReactiveWebApplicationContextTests.TestBean.class, Lazy.class);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigReactiveWebApplicationContextTests.TestBean")).isFalse();
        assertThat(this.context.getBean(AnnotationConfigReactiveWebApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Test
    public void registerBeanWithSupplier() {
        this.context = new AnnotationConfigReactiveWebApplicationContext();
        this.context.registerBean(AnnotationConfigReactiveWebApplicationContextTests.TestBean.class, AnnotationConfigReactiveWebApplicationContextTests.TestBean::new);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigReactiveWebApplicationContextTests.TestBean")).isTrue();
        assertThat(this.context.getBean(AnnotationConfigReactiveWebApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Test
    public void registerBeanWithSupplierAndLazy() {
        this.context = new AnnotationConfigReactiveWebApplicationContext();
        this.context.registerBean(AnnotationConfigReactiveWebApplicationContextTests.TestBean.class, AnnotationConfigReactiveWebApplicationContextTests.TestBean::new, Lazy.class);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigReactiveWebApplicationContextTests.TestBean")).isFalse();
        assertThat(this.context.getBean(AnnotationConfigReactiveWebApplicationContextTests.TestBean.class)).isNotNull();
    }

    private static class TestBean {}
}

