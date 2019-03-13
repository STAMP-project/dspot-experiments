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
package org.springframework.boot.web.server;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.ListableBeanFactory;


/**
 * Tests for {@link WebServerFactoryCustomizerBeanPostProcessor}.
 *
 * @author Phillip Webb
 */
public class WebServerFactoryCustomizerBeanPostProcessorTests {
    private WebServerFactoryCustomizerBeanPostProcessor processor = new WebServerFactoryCustomizerBeanPostProcessor();

    @Mock
    private ListableBeanFactory beanFactory;

    @Test
    public void setBeanFactoryWhenNotListableShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.processor.setBeanFactory(mock(.class))).withMessageContaining(("WebServerCustomizerBeanPostProcessor can only " + "be used with a ListableBeanFactory"));
    }

    @Test
    public void postProcessBeforeShouldReturnBean() {
        addMockBeans(Collections.emptyMap());
        Object bean = new Object();
        Object result = this.processor.postProcessBeforeInitialization(bean, "foo");
        assertThat(result).isSameAs(bean);
    }

    @Test
    public void postProcessAfterShouldReturnBean() {
        addMockBeans(Collections.emptyMap());
        Object bean = new Object();
        Object result = this.processor.postProcessAfterInitialization(bean, "foo");
        assertThat(result).isSameAs(bean);
    }

    @Test
    public void postProcessAfterShouldCallInterfaceCustomizers() {
        Map<String, Object> beans = addInterfaceBeans();
        addMockBeans(beans);
        postProcessBeforeInitialization(WebServerFactory.class);
        assertThat(wasCalled(beans, "one")).isFalse();
        assertThat(wasCalled(beans, "two")).isFalse();
        assertThat(wasCalled(beans, "all")).isTrue();
    }

    @Test
    public void postProcessAfterWhenWebServerFactoryOneShouldCallInterfaceCustomizers() {
        Map<String, Object> beans = addInterfaceBeans();
        addMockBeans(beans);
        postProcessBeforeInitialization(WebServerFactoryCustomizerBeanPostProcessorTests.WebServerFactoryOne.class);
        assertThat(wasCalled(beans, "one")).isTrue();
        assertThat(wasCalled(beans, "two")).isFalse();
        assertThat(wasCalled(beans, "all")).isTrue();
    }

    @Test
    public void postProcessAfterWhenWebServerFactoryTwoShouldCallInterfaceCustomizers() {
        Map<String, Object> beans = addInterfaceBeans();
        addMockBeans(beans);
        postProcessBeforeInitialization(WebServerFactoryCustomizerBeanPostProcessorTests.WebServerFactoryTwo.class);
        assertThat(wasCalled(beans, "one")).isFalse();
        assertThat(wasCalled(beans, "two")).isTrue();
        assertThat(wasCalled(beans, "all")).isTrue();
    }

    @Test
    public void postProcessAfterShouldCallLambdaCustomizers() {
        List<String> called = new ArrayList<>();
        addLambdaBeans(called);
        postProcessBeforeInitialization(WebServerFactory.class);
        assertThat(called).containsExactly("all");
    }

    @Test
    public void postProcessAfterWhenWebServerFactoryOneShouldCallLambdaCustomizers() {
        List<String> called = new ArrayList<>();
        addLambdaBeans(called);
        postProcessBeforeInitialization(WebServerFactoryCustomizerBeanPostProcessorTests.WebServerFactoryOne.class);
        assertThat(called).containsExactly("one", "all");
    }

    @Test
    public void postProcessAfterWhenWebServerFactoryTwoShouldCallLambdaCustomizers() {
        List<String> called = new ArrayList<>();
        addLambdaBeans(called);
        postProcessBeforeInitialization(WebServerFactoryCustomizerBeanPostProcessorTests.WebServerFactoryTwo.class);
        assertThat(called).containsExactly("two", "all");
    }

    private interface WebServerFactoryOne extends WebServerFactory {}

    private interface WebServerFactoryTwo extends WebServerFactory {}

    private static class MockWebServerFactoryCustomizer<T extends WebServerFactory> implements WebServerFactoryCustomizer<T> {
        private boolean called;

        @Override
        public void customize(T factory) {
            this.called = true;
        }

        public boolean wasCalled() {
            return this.called;
        }
    }

    private static class WebServerFactoryOneCustomizer extends WebServerFactoryCustomizerBeanPostProcessorTests.MockWebServerFactoryCustomizer<WebServerFactoryCustomizerBeanPostProcessorTests.WebServerFactoryOne> {}

    private static class WebServerFactoryTwoCustomizer extends WebServerFactoryCustomizerBeanPostProcessorTests.MockWebServerFactoryCustomizer<WebServerFactoryCustomizerBeanPostProcessorTests.WebServerFactoryTwo> {}

    private static class WebServerFactoryAllCustomizer extends WebServerFactoryCustomizerBeanPostProcessorTests.MockWebServerFactoryCustomizer<WebServerFactory> {}
}

