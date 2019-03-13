/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.validation.beanvalidation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncAnnotationAdvisor;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.validation.annotation.Validated;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class MethodValidationTests {
    @Test
    public void testMethodValidationInterceptor() {
        MethodValidationTests.MyValidBean bean = new MethodValidationTests.MyValidBean();
        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.addAdvice(new MethodValidationInterceptor());
        proxyFactory.addAdvisor(new AsyncAnnotationAdvisor());
        doTestProxyValidation(((MethodValidationTests.MyValidInterface) (proxyFactory.getProxy())));
    }

    @Test
    public void testMethodValidationPostProcessor() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("mvpp", MethodValidationPostProcessor.class);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("beforeExistingAdvisors", false);
        ac.registerSingleton("aapp", AsyncAnnotationBeanPostProcessor.class, pvs);
        ac.registerSingleton("bean", MethodValidationTests.MyValidBean.class);
        ac.refresh();
        doTestProxyValidation(ac.getBean("bean", MethodValidationTests.MyValidInterface.class));
        ac.close();
    }

    @Test
    public void testLazyValidatorForMethodValidation() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MethodValidationTests.LazyMethodValidationConfig.class, CustomValidatorBean.class, MethodValidationTests.MyValidBean.class, MethodValidationTests.MyValidFactoryBean.class);
        ctx.getBeansOfType(MethodValidationTests.MyValidInterface.class).values().forEach(( bean) -> bean.myValidMethod("value", 5));
    }

    @Test
    public void testLazyValidatorForMethodValidationWithProxyTargetClass() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MethodValidationTests.LazyMethodValidationConfigWithProxyTargetClass.class, CustomValidatorBean.class, MethodValidationTests.MyValidBean.class, MethodValidationTests.MyValidFactoryBean.class);
        ctx.getBeansOfType(MethodValidationTests.MyValidInterface.class).values().forEach(( bean) -> bean.myValidMethod("value", 5));
    }

    @MethodValidationTests.MyStereotype
    public static class MyValidBean implements MethodValidationTests.MyValidInterface<String> {
        @Override
        public Object myValidMethod(String arg1, int arg2) {
            return arg2 == 0 ? null : "value";
        }

        @Override
        public void myValidAsyncMethod(String arg1, int arg2) {
        }

        @Override
        public String myGenericMethod(String value) {
            return value;
        }
    }

    @MethodValidationTests.MyStereotype
    public static class MyValidFactoryBean implements FactoryBean<String> , MethodValidationTests.MyValidInterface<String> {
        @Override
        public String getObject() {
            return null;
        }

        @Override
        public Class<?> getObjectType() {
            return String.class;
        }

        @Override
        public Object myValidMethod(String arg1, int arg2) {
            return arg2 == 0 ? null : "value";
        }

        @Override
        public void myValidAsyncMethod(String arg1, int arg2) {
        }

        @Override
        public String myGenericMethod(String value) {
            return value;
        }
    }

    public interface MyValidInterface<T> {
        @NotNull
        Object myValidMethod(@NotNull(groups = MethodValidationTests.MyGroup.class)
        String arg1, @Max(10)
        int arg2);

        @MethodValidationTests.MyValid
        @Async
        void myValidAsyncMethod(@NotNull(groups = MethodValidationTests.OtherGroup.class)
        String arg1, @Max(10)
        int arg2);

        T myGenericMethod(@NotNull
        T value);
    }

    public interface MyGroup {}

    public interface OtherGroup {}

    @Validated({ MethodValidationTests.MyGroup.class, Default.class })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyStereotype {}

    @Validated({ MethodValidationTests.OtherGroup.class, Default.class })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyValid {}

    @Configuration
    public static class LazyMethodValidationConfig {
        @Bean
        public static MethodValidationPostProcessor methodValidationPostProcessor(@Lazy
        Validator validator) {
            MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
            postProcessor.setValidator(validator);
            return postProcessor;
        }
    }

    @Configuration
    public static class LazyMethodValidationConfigWithProxyTargetClass {
        @Bean
        public static MethodValidationPostProcessor methodValidationPostProcessor(@Lazy
        Validator validator) {
            MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
            postProcessor.setValidator(validator);
            postProcessor.setProxyTargetClass(true);
            return postProcessor;
        }
    }
}

