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


import example.scannable.FooDao;
import example.scannable.FooService;
import example.scannable.FooServiceImpl;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class EnableAspectJAutoProxyTests {
    @Test
    public void withJdkProxy() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(EnableAspectJAutoProxyTests.ConfigWithJdkProxy.class);
        aspectIsApplied(ctx);
        Assert.assertThat(AopUtils.isJdkDynamicProxy(ctx.getBean(FooService.class)), CoreMatchers.is(true));
    }

    @Test
    public void withCglibProxy() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(EnableAspectJAutoProxyTests.ConfigWithCglibProxy.class);
        aspectIsApplied(ctx);
        Assert.assertThat(AopUtils.isCglibProxy(ctx.getBean(FooService.class)), CoreMatchers.is(true));
    }

    @Test
    public void withExposedProxy() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(EnableAspectJAutoProxyTests.ConfigWithExposedProxy.class);
        aspectIsApplied(ctx);
        Assert.assertThat(AopUtils.isJdkDynamicProxy(ctx.getBean(FooService.class)), CoreMatchers.is(true));
    }

    @Test
    public void withAnnotationOnArgumentAndJdkProxy() {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(EnableAspectJAutoProxyTests.ConfigWithJdkProxy.class, EnableAspectJAutoProxyTests.SampleService.class, EnableAspectJAutoProxyTests.LoggingAspect.class);
        EnableAspectJAutoProxyTests.SampleService sampleService = ctx.getBean(EnableAspectJAutoProxyTests.SampleService.class);
        sampleService.execute(new EnableAspectJAutoProxyTests.SampleDto());
        sampleService.execute(new EnableAspectJAutoProxyTests.SampleInputBean());
        sampleService.execute(((EnableAspectJAutoProxyTests.SampleDto) (null)));
        sampleService.execute(((EnableAspectJAutoProxyTests.SampleInputBean) (null)));
    }

    @Test
    public void withAnnotationOnArgumentAndCglibProxy() {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(EnableAspectJAutoProxyTests.ConfigWithCglibProxy.class, EnableAspectJAutoProxyTests.SampleService.class, EnableAspectJAutoProxyTests.LoggingAspect.class);
        EnableAspectJAutoProxyTests.SampleService sampleService = ctx.getBean(EnableAspectJAutoProxyTests.SampleService.class);
        sampleService.execute(new EnableAspectJAutoProxyTests.SampleDto());
        sampleService.execute(new EnableAspectJAutoProxyTests.SampleInputBean());
        sampleService.execute(((EnableAspectJAutoProxyTests.SampleDto) (null)));
        sampleService.execute(((EnableAspectJAutoProxyTests.SampleInputBean) (null)));
    }

    @ComponentScan("example.scannable")
    @EnableAspectJAutoProxy
    static class ConfigWithJdkProxy {}

    @ComponentScan("example.scannable")
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class ConfigWithCglibProxy {}

    @ComponentScan("example.scannable")
    @EnableAspectJAutoProxy(exposeProxy = true)
    static class ConfigWithExposedProxy {
        @Bean
        public FooService fooServiceImpl(final ApplicationContext context) {
            return new FooServiceImpl() {
                @Override
                public String foo(int id) {
                    Assert.assertNotNull(AopContext.currentProxy());
                    return super.foo(id);
                }

                @Override
                protected FooDao fooDao() {
                    return context.getBean(FooDao.class);
                }
            };
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Loggable {}

    @EnableAspectJAutoProxyTests.Loggable
    public static class SampleDto {}

    public static class SampleInputBean {}

    public static class SampleService {
        // Not matched method on {@link LoggingAspect}.
        public void execute(EnableAspectJAutoProxyTests.SampleInputBean inputBean) {
        }

        // Matched method on {@link LoggingAspect}
        public void execute(EnableAspectJAutoProxyTests.SampleDto dto) {
        }
    }

    @Aspect
    public static class LoggingAspect {
        @Before("@args(org.springframework.context.annotation.EnableAspectJAutoProxyTests.Loggable))")
        public void loggingBeginByAtArgs() {
        }
    }
}

