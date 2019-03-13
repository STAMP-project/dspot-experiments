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
package org.springframework.web.reactive.result.method.annotation;


import java.time.Duration;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import reactor.core.publisher.Mono;
import rx.RxReactiveStreams;
import rx.Single;


/**
 * Unit tests for {@link ModelAttributeMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class ModelAttributeMethodArgumentResolverTests {
    private BindingContext bindContext;

    private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    @Test
    public void supports() throws Exception {
        ModelAttributeMethodArgumentResolver resolver = new ModelAttributeMethodArgumentResolver(ReactiveAdapterRegistry.getSharedInstance(), false);
        MethodParameter param = this.testMethod.annotPresent(ModelAttribute.class).arg(ModelAttributeMethodArgumentResolverTests.Foo.class);
        Assert.assertTrue(resolver.supportsParameter(param));
        param = this.testMethod.annotPresent(ModelAttribute.class).arg(Mono.class, ModelAttributeMethodArgumentResolverTests.Foo.class);
        Assert.assertTrue(resolver.supportsParameter(param));
        param = this.testMethod.annotNotPresent(ModelAttribute.class).arg(ModelAttributeMethodArgumentResolverTests.Foo.class);
        Assert.assertFalse(resolver.supportsParameter(param));
        param = this.testMethod.annotNotPresent(ModelAttribute.class).arg(Mono.class, ModelAttributeMethodArgumentResolverTests.Foo.class);
        Assert.assertFalse(resolver.supportsParameter(param));
    }

    @Test
    public void supportsWithDefaultResolution() throws Exception {
        ModelAttributeMethodArgumentResolver resolver = new ModelAttributeMethodArgumentResolver(ReactiveAdapterRegistry.getSharedInstance(), true);
        MethodParameter param = this.testMethod.annotNotPresent(ModelAttribute.class).arg(ModelAttributeMethodArgumentResolverTests.Foo.class);
        Assert.assertTrue(resolver.supportsParameter(param));
        param = this.testMethod.annotNotPresent(ModelAttribute.class).arg(Mono.class, ModelAttributeMethodArgumentResolverTests.Foo.class);
        Assert.assertTrue(resolver.supportsParameter(param));
        param = this.testMethod.annotNotPresent(ModelAttribute.class).arg(String.class);
        Assert.assertFalse(resolver.supportsParameter(param));
        param = this.testMethod.annotNotPresent(ModelAttribute.class).arg(Mono.class, String.class);
        Assert.assertFalse(resolver.supportsParameter(param));
    }

    @Test
    public void createAndBind() throws Exception {
        testBindFoo("foo", this.testMethod.annotPresent(ModelAttribute.class).arg(ModelAttributeMethodArgumentResolverTests.Foo.class), ( value) -> {
            assertEquals(.class, value.getClass());
            return ((org.springframework.web.reactive.result.method.annotation.Foo) (value));
        });
    }

    @Test
    public void createAndBindToMono() throws Exception {
        MethodParameter parameter = this.testMethod.annotNotPresent(ModelAttribute.class).arg(Mono.class, ModelAttributeMethodArgumentResolverTests.Foo.class);
        testBindFoo("fooMono", parameter, ( mono) -> {
            assertTrue(mono.getClass().getName(), (mono instanceof Mono));
            Object value = ((Mono<?>) (mono)).block(Duration.ofSeconds(5));
            assertEquals(.class, value.getClass());
            return ((org.springframework.web.reactive.result.method.annotation.Foo) (value));
        });
    }

    @Test
    public void createAndBindToSingle() throws Exception {
        MethodParameter parameter = this.testMethod.annotPresent(ModelAttribute.class).arg(Single.class, ModelAttributeMethodArgumentResolverTests.Foo.class);
        testBindFoo("fooSingle", parameter, ( single) -> {
            assertTrue(single.getClass().getName(), (single instanceof Single));
            Object value = ((Single<?>) (single)).toBlocking().value();
            assertEquals(.class, value.getClass());
            return ((org.springframework.web.reactive.result.method.annotation.Foo) (value));
        });
    }

    @Test
    public void bindExisting() throws Exception {
        ModelAttributeMethodArgumentResolverTests.Foo foo = new ModelAttributeMethodArgumentResolverTests.Foo();
        foo.setName("Jim");
        this.bindContext.getModel().addAttribute(foo);
        MethodParameter parameter = this.testMethod.annotNotPresent(ModelAttribute.class).arg(ModelAttributeMethodArgumentResolverTests.Foo.class);
        testBindFoo("foo", parameter, ( value) -> {
            assertEquals(.class, value.getClass());
            return ((org.springframework.web.reactive.result.method.annotation.Foo) (value));
        });
        Assert.assertSame(foo, this.bindContext.getModel().asMap().get("foo"));
    }

    @Test
    public void bindExistingMono() throws Exception {
        ModelAttributeMethodArgumentResolverTests.Foo foo = new ModelAttributeMethodArgumentResolverTests.Foo();
        foo.setName("Jim");
        this.bindContext.getModel().addAttribute("fooMono", Mono.just(foo));
        MethodParameter parameter = this.testMethod.annotNotPresent(ModelAttribute.class).arg(ModelAttributeMethodArgumentResolverTests.Foo.class);
        testBindFoo("foo", parameter, ( value) -> {
            assertEquals(.class, value.getClass());
            return ((org.springframework.web.reactive.result.method.annotation.Foo) (value));
        });
        Assert.assertSame(foo, this.bindContext.getModel().asMap().get("foo"));
    }

    @Test
    public void bindExistingSingle() throws Exception {
        ModelAttributeMethodArgumentResolverTests.Foo foo = new ModelAttributeMethodArgumentResolverTests.Foo();
        foo.setName("Jim");
        this.bindContext.getModel().addAttribute("fooSingle", Single.just(foo));
        MethodParameter parameter = this.testMethod.annotNotPresent(ModelAttribute.class).arg(ModelAttributeMethodArgumentResolverTests.Foo.class);
        testBindFoo("foo", parameter, ( value) -> {
            assertEquals(.class, value.getClass());
            return ((org.springframework.web.reactive.result.method.annotation.Foo) (value));
        });
        Assert.assertSame(foo, this.bindContext.getModel().asMap().get("foo"));
    }

    @Test
    public void bindExistingMonoToMono() throws Exception {
        ModelAttributeMethodArgumentResolverTests.Foo foo = new ModelAttributeMethodArgumentResolverTests.Foo();
        foo.setName("Jim");
        String modelKey = "fooMono";
        this.bindContext.getModel().addAttribute(modelKey, Mono.just(foo));
        MethodParameter parameter = this.testMethod.annotNotPresent(ModelAttribute.class).arg(Mono.class, ModelAttributeMethodArgumentResolverTests.Foo.class);
        testBindFoo(modelKey, parameter, ( mono) -> {
            assertTrue(mono.getClass().getName(), (mono instanceof Mono));
            Object value = ((Mono<?>) (mono)).block(Duration.ofSeconds(5));
            assertEquals(.class, value.getClass());
            return ((org.springframework.web.reactive.result.method.annotation.Foo) (value));
        });
    }

    @Test
    public void validationError() throws Exception {
        MethodParameter parameter = this.testMethod.annotNotPresent(ModelAttribute.class).arg(ModelAttributeMethodArgumentResolverTests.Foo.class);
        testValidationError(parameter, Function.identity());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void validationErrorToMono() throws Exception {
        MethodParameter parameter = this.testMethod.annotNotPresent(ModelAttribute.class).arg(Mono.class, ModelAttributeMethodArgumentResolverTests.Foo.class);
        testValidationError(parameter, ( resolvedArgumentMono) -> {
            Object value = resolvedArgumentMono.block(Duration.ofSeconds(5));
            assertNotNull(value);
            assertTrue((value instanceof Mono));
            return ((Mono<?>) (value));
        });
    }

    @Test
    public void validationErrorToSingle() throws Exception {
        MethodParameter parameter = this.testMethod.annotPresent(ModelAttribute.class).arg(Single.class, ModelAttributeMethodArgumentResolverTests.Foo.class);
        testValidationError(parameter, ( resolvedArgumentMono) -> {
            Object value = resolvedArgumentMono.block(Duration.ofSeconds(5));
            assertNotNull(value);
            assertTrue((value instanceof Single));
            return Mono.from(RxReactiveStreams.toPublisher(((Single<?>) (value))));
        });
    }

    @Test
    public void bindDataClass() throws Exception {
        testBindBar(this.testMethod.annotNotPresent(ModelAttribute.class).arg(ModelAttributeMethodArgumentResolverTests.Bar.class));
    }

    @SuppressWarnings("unused")
    private static class Foo {
        private String name;

        private int age;

        public Foo() {
        }

        public Foo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @SuppressWarnings("unused")
    private static class Bar {
        private final String name;

        private final int age;

        private int count;

        public Bar(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return this.age;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}

