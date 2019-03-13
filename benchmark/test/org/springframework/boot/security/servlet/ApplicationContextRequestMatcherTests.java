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
package org.springframework.boot.security.servlet;


import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;


/**
 * Tests for {@link ApplicationContextRequestMatcher}.
 *
 * @author Phillip Webb
 */
public class ApplicationContextRequestMatcherTests {
    @Test
    public void createWhenContextClassIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TestApplicationContextRequestMatcher<>(null)).withMessageContaining("Context class must not be null");
    }

    @Test
    public void matchesWhenContextClassIsApplicationContextShouldProvideContext() {
        StaticWebApplicationContext context = createWebApplicationContext();
        assertThat(new ApplicationContextRequestMatcherTests.TestApplicationContextRequestMatcher<>(ApplicationContext.class).callMatchesAndReturnProvidedContext(context).get()).isEqualTo(context);
    }

    @Test
    public void matchesWhenContextClassIsExistingBeanShouldProvideBean() {
        StaticWebApplicationContext context = createWebApplicationContext();
        context.registerSingleton("existingBean", ApplicationContextRequestMatcherTests.ExistingBean.class);
        assertThat(new ApplicationContextRequestMatcherTests.TestApplicationContextRequestMatcher<>(ApplicationContextRequestMatcherTests.ExistingBean.class).callMatchesAndReturnProvidedContext(context).get()).isEqualTo(context.getBean(ApplicationContextRequestMatcherTests.ExistingBean.class));
    }

    @Test
    public void matchesWhenContextClassIsBeanThatDoesNotExistShouldSupplyException() {
        StaticWebApplicationContext context = createWebApplicationContext();
        Supplier<ApplicationContextRequestMatcherTests.ExistingBean> supplier = new ApplicationContextRequestMatcherTests.TestApplicationContextRequestMatcher<>(ApplicationContextRequestMatcherTests.ExistingBean.class).callMatchesAndReturnProvidedContext(context);
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(supplier::get);
    }

    static class ExistingBean {}

    static class NewBean {
        private final ApplicationContextRequestMatcherTests.ExistingBean bean;

        NewBean(ApplicationContextRequestMatcherTests.ExistingBean bean) {
            this.bean = bean;
        }

        public ApplicationContextRequestMatcherTests.ExistingBean getBean() {
            return this.bean;
        }
    }

    static class TestApplicationContextRequestMatcher<C> extends ApplicationContextRequestMatcher<C> {
        private Supplier<C> providedContext;

        TestApplicationContextRequestMatcher(Class<? extends C> context) {
            super(context);
        }

        public Supplier<C> callMatchesAndReturnProvidedContext(WebApplicationContext context) {
            return callMatchesAndReturnProvidedContext(new org.springframework.mock.web.MockHttpServletRequest(context.getServletContext()));
        }

        public Supplier<C> callMatchesAndReturnProvidedContext(HttpServletRequest request) {
            matches(request);
            return getProvidedContext();
        }

        @Override
        protected boolean matches(HttpServletRequest request, Supplier<C> context) {
            this.providedContext = context;
            return false;
        }

        public Supplier<C> getProvidedContext() {
            return this.providedContext;
        }
    }
}

