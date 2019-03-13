/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.context.annotation;


import java.io.Closeable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


/**
 *
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 */
public class DestroyMethodInferenceTests {
    @Test
    public void beanMethods() {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(DestroyMethodInferenceTests.Config.class);
        DestroyMethodInferenceTests.WithExplicitDestroyMethod c0 = ctx.getBean(DestroyMethodInferenceTests.WithExplicitDestroyMethod.class);
        DestroyMethodInferenceTests.WithLocalCloseMethod c1 = ctx.getBean("c1", DestroyMethodInferenceTests.WithLocalCloseMethod.class);
        DestroyMethodInferenceTests.WithLocalCloseMethod c2 = ctx.getBean("c2", DestroyMethodInferenceTests.WithLocalCloseMethod.class);
        DestroyMethodInferenceTests.WithInheritedCloseMethod c3 = ctx.getBean("c3", DestroyMethodInferenceTests.WithInheritedCloseMethod.class);
        DestroyMethodInferenceTests.WithInheritedCloseMethod c4 = ctx.getBean("c4", DestroyMethodInferenceTests.WithInheritedCloseMethod.class);
        DestroyMethodInferenceTests.WithInheritedCloseMethod c5 = ctx.getBean("c5", DestroyMethodInferenceTests.WithInheritedCloseMethod.class);
        DestroyMethodInferenceTests.WithNoCloseMethod c6 = ctx.getBean("c6", DestroyMethodInferenceTests.WithNoCloseMethod.class);
        DestroyMethodInferenceTests.WithLocalShutdownMethod c7 = ctx.getBean("c7", DestroyMethodInferenceTests.WithLocalShutdownMethod.class);
        DestroyMethodInferenceTests.WithInheritedCloseMethod c8 = ctx.getBean("c8", DestroyMethodInferenceTests.WithInheritedCloseMethod.class);
        DestroyMethodInferenceTests.WithDisposableBean c9 = ctx.getBean("c9", DestroyMethodInferenceTests.WithDisposableBean.class);
        Assert.assertThat(c0.closed, CoreMatchers.is(false));
        Assert.assertThat(c1.closed, CoreMatchers.is(false));
        Assert.assertThat(c2.closed, CoreMatchers.is(false));
        Assert.assertThat(c3.closed, CoreMatchers.is(false));
        Assert.assertThat(c4.closed, CoreMatchers.is(false));
        Assert.assertThat(c5.closed, CoreMatchers.is(false));
        Assert.assertThat(c6.closed, CoreMatchers.is(false));
        Assert.assertThat(c7.closed, CoreMatchers.is(false));
        Assert.assertThat(c8.closed, CoreMatchers.is(false));
        Assert.assertThat(c9.closed, CoreMatchers.is(false));
        ctx.close();
        Assert.assertThat("c0", c0.closed, CoreMatchers.is(true));
        Assert.assertThat("c1", c1.closed, CoreMatchers.is(true));
        Assert.assertThat("c2", c2.closed, CoreMatchers.is(true));
        Assert.assertThat("c3", c3.closed, CoreMatchers.is(true));
        Assert.assertThat("c4", c4.closed, CoreMatchers.is(true));
        Assert.assertThat("c5", c5.closed, CoreMatchers.is(true));
        Assert.assertThat("c6", c6.closed, CoreMatchers.is(false));
        Assert.assertThat("c7", c7.closed, CoreMatchers.is(true));
        Assert.assertThat("c8", c8.closed, CoreMatchers.is(false));
        Assert.assertThat("c9", c9.closed, CoreMatchers.is(true));
    }

    @Test
    public void xml() {
        ConfigurableApplicationContext ctx = new GenericXmlApplicationContext(getClass(), "DestroyMethodInferenceTests-context.xml");
        DestroyMethodInferenceTests.WithLocalCloseMethod x1 = ctx.getBean("x1", DestroyMethodInferenceTests.WithLocalCloseMethod.class);
        DestroyMethodInferenceTests.WithLocalCloseMethod x2 = ctx.getBean("x2", DestroyMethodInferenceTests.WithLocalCloseMethod.class);
        DestroyMethodInferenceTests.WithLocalCloseMethod x3 = ctx.getBean("x3", DestroyMethodInferenceTests.WithLocalCloseMethod.class);
        DestroyMethodInferenceTests.WithNoCloseMethod x4 = ctx.getBean("x4", DestroyMethodInferenceTests.WithNoCloseMethod.class);
        DestroyMethodInferenceTests.WithInheritedCloseMethod x8 = ctx.getBean("x8", DestroyMethodInferenceTests.WithInheritedCloseMethod.class);
        Assert.assertThat(x1.closed, CoreMatchers.is(false));
        Assert.assertThat(x2.closed, CoreMatchers.is(false));
        Assert.assertThat(x3.closed, CoreMatchers.is(false));
        Assert.assertThat(x4.closed, CoreMatchers.is(false));
        ctx.close();
        Assert.assertThat(x1.closed, CoreMatchers.is(false));
        Assert.assertThat(x2.closed, CoreMatchers.is(true));
        Assert.assertThat(x3.closed, CoreMatchers.is(true));
        Assert.assertThat(x4.closed, CoreMatchers.is(false));
        Assert.assertThat(x8.closed, CoreMatchers.is(false));
    }

    @Configuration
    static class Config {
        @Bean(destroyMethod = "explicitClose")
        public DestroyMethodInferenceTests.WithExplicitDestroyMethod c0() {
            return new DestroyMethodInferenceTests.WithExplicitDestroyMethod();
        }

        @Bean
        public DestroyMethodInferenceTests.WithLocalCloseMethod c1() {
            return new DestroyMethodInferenceTests.WithLocalCloseMethod();
        }

        @Bean
        public Object c2() {
            return new DestroyMethodInferenceTests.WithLocalCloseMethod();
        }

        @Bean
        public DestroyMethodInferenceTests.WithInheritedCloseMethod c3() {
            return new DestroyMethodInferenceTests.WithInheritedCloseMethod();
        }

        @Bean
        public Closeable c4() {
            return new DestroyMethodInferenceTests.WithInheritedCloseMethod();
        }

        @Bean(destroyMethod = "other")
        public DestroyMethodInferenceTests.WithInheritedCloseMethod c5() {
            return new DestroyMethodInferenceTests.WithInheritedCloseMethod() {
                @Override
                public void close() {
                    throw new IllegalStateException("close() should not be called");
                }

                @SuppressWarnings("unused")
                public void other() {
                    this.closed = true;
                }
            };
        }

        @Bean
        public DestroyMethodInferenceTests.WithNoCloseMethod c6() {
            return new DestroyMethodInferenceTests.WithNoCloseMethod();
        }

        @Bean
        public DestroyMethodInferenceTests.WithLocalShutdownMethod c7() {
            return new DestroyMethodInferenceTests.WithLocalShutdownMethod();
        }

        @Bean(destroyMethod = "")
        public DestroyMethodInferenceTests.WithInheritedCloseMethod c8() {
            return new DestroyMethodInferenceTests.WithInheritedCloseMethod();
        }

        @Bean(destroyMethod = "")
        public DestroyMethodInferenceTests.WithDisposableBean c9() {
            return new DestroyMethodInferenceTests.WithDisposableBean();
        }
    }

    static class WithExplicitDestroyMethod {
        boolean closed = false;

        public void explicitClose() {
            closed = true;
        }
    }

    static class WithLocalCloseMethod {
        boolean closed = false;

        public void close() {
            closed = true;
        }
    }

    static class WithInheritedCloseMethod implements Closeable {
        boolean closed = false;

        @Override
        public void close() {
            closed = true;
        }
    }

    static class WithDisposableBean implements DisposableBean {
        boolean closed = false;

        @Override
        public void destroy() {
            closed = true;
        }
    }

    static class WithNoCloseMethod {
        boolean closed = false;
    }

    static class WithLocalShutdownMethod {
        boolean closed = false;

        public void shutdown() {
            closed = true;
        }
    }
}

