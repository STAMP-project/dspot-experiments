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
package org.springframework.boot.test.context.assertj;


import java.util.function.Supplier;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * Tests for {@link ApplicationContextAssertProvider} and
 * {@link AssertProviderApplicationContextInvocationHandler}.
 *
 * @author Phillip Webb
 */
public class ApplicationContextAssertProviderTests {
    @Mock
    private ConfigurableApplicationContext mockContext;

    private RuntimeException startupFailure;

    private Supplier<ApplicationContext> mockContextSupplier;

    private Supplier<ApplicationContext> startupFailureSupplier;

    @Test
    public void getWhenTypeIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> ApplicationContextAssertProvider.get(null, .class, this.mockContextSupplier)).withMessageContaining("Type must not be null");
    }

    @Test
    public void getWhenTypeIsClassShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> ApplicationContextAssertProvider.get(null, .class, this.mockContextSupplier)).withMessageContaining("Type must not be null");
    }

    @Test
    public void getWhenContextTypeIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> ApplicationContextAssertProvider.get(.class, .class, this.mockContextSupplier)).withMessageContaining("Type must be an interface");
    }

    @Test
    public void getWhenContextTypeIsClassShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> ApplicationContextAssertProvider.get(.class, null, this.mockContextSupplier)).withMessageContaining("ContextType must not be null");
    }

    @Test
    public void getWhenSupplierIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> ApplicationContextAssertProvider.get(.class, .class, this.mockContextSupplier)).withMessageContaining("ContextType must be an interface");
    }

    @Test
    public void getWhenContextStartsShouldReturnProxyThatCallsRealMethods() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.mockContextSupplier);
        assertThat(((Object) (context))).isNotNull();
        context.getBean("foo");
        Mockito.verify(this.mockContext).getBean("foo");
    }

    @Test
    public void getWhenContextFailsShouldReturnProxyThatThrowsExceptions() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.startupFailureSupplier);
        assertThat(((Object) (context))).isNotNull();
        assertThatIllegalStateException().isThrownBy(() -> context.getBean("foo")).withCause(this.startupFailure).withMessageContaining("failed to start");
    }

    @Test
    public void getSourceContextWhenContextStartsShouldReturnSourceContext() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.mockContextSupplier);
        assertThat(context.getSourceApplicationContext()).isSameAs(this.mockContext);
    }

    @Test
    public void getSourceContextWhenContextFailsShouldThrowException() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.startupFailureSupplier);
        assertThatIllegalStateException().isThrownBy(context::getSourceApplicationContext).withCause(this.startupFailure).withMessageContaining("failed to start");
    }

    @Test
    public void getSourceContextOfTypeWhenContextStartsShouldReturnSourceContext() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.mockContextSupplier);
        assertThat(context.getSourceApplicationContext(ApplicationContext.class)).isSameAs(this.mockContext);
    }

    @Test
    public void getSourceContextOfTypeWhenContextFailsToStartShouldThrowException() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.startupFailureSupplier);
        assertThatIllegalStateException().isThrownBy(() -> context.getSourceApplicationContext(.class)).withCause(this.startupFailure).withMessageContaining("failed to start");
    }

    @Test
    public void getStartupFailureWhenContextStartsShouldReturnNull() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.mockContextSupplier);
        assertThat(context.getStartupFailure()).isNull();
    }

    @Test
    public void getStartupFailureWhenContextFailsToStartShouldReturnException() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.startupFailureSupplier);
        assertThat(context.getStartupFailure()).isEqualTo(this.startupFailure);
    }

    @Test
    public void assertThatWhenContextStartsShouldReturnAssertions() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.mockContextSupplier);
        ApplicationContextAssert<ApplicationContext> contextAssert = assertThat(context);
        assertThat(contextAssert.getApplicationContext()).isSameAs(context);
        assertThat(contextAssert.getStartupFailure()).isNull();
    }

    @Test
    public void assertThatWhenContextFailsShouldReturnAssertions() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.startupFailureSupplier);
        ApplicationContextAssert<ApplicationContext> contextAssert = assertThat(context);
        assertThat(contextAssert.getApplicationContext()).isSameAs(context);
        assertThat(contextAssert.getStartupFailure()).isSameAs(this.startupFailure);
    }

    @Test
    public void toStringWhenContextStartsShouldReturnSimpleString() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.mockContextSupplier);
        assertThat(context.toString()).startsWith("Started application [ConfigurableApplicationContext.MockitoMock").endsWith("id = [null], applicationName = [null], beanDefinitionCount = 0]");
    }

    @Test
    public void toStringWhenContextFailsToStartShouldReturnSimpleString() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.startupFailureSupplier);
        assertThat(context.toString()).isEqualTo(("Unstarted application context " + ("org.springframework.context.ApplicationContext" + "[startupFailure=java.lang.RuntimeException]")));
    }

    @Test
    public void closeShouldCloseContext() {
        ApplicationContextAssertProvider<ApplicationContext> context = get(this.mockContextSupplier);
        context.close();
        Mockito.verify(this.mockContext).close();
    }

    private interface TestAssertProviderApplicationContext extends ApplicationContextAssertProvider<ApplicationContext> {}

    private abstract static class TestAssertProviderApplicationContextClass implements ApplicationContextAssertProviderTests.TestAssertProviderApplicationContext {}
}

