/**
 * Copyright 2002-2011 the original author or authors.
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


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.configuration.a.BaseConfig;


/**
 * Reproduces SPR-8756, which has been marked as "won't fix" for reasons
 * described in the issue. Also demonstrates the suggested workaround.
 *
 * @author Chris Beams
 */
public class PackagePrivateBeanMethodInheritanceTests {
    @Test
    public void repro() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PackagePrivateBeanMethodInheritanceTests.ReproConfig.class);
        ctx.refresh();
        PackagePrivateBeanMethodInheritanceTests.Foo foo1 = ctx.getBean("foo1", PackagePrivateBeanMethodInheritanceTests.Foo.class);
        PackagePrivateBeanMethodInheritanceTests.Foo foo2 = ctx.getBean("foo2", PackagePrivateBeanMethodInheritanceTests.Foo.class);
        ctx.getBean("packagePrivateBar", PackagePrivateBeanMethodInheritanceTests.Bar.class);// <-- i.e. @Bean was registered

        Assert.assertThat(foo1.bar, CoreMatchers.not(CoreMatchers.is(foo2.bar)));// <-- i.e. @Bean *not* enhanced

    }

    @Test
    public void workaround() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PackagePrivateBeanMethodInheritanceTests.WorkaroundConfig.class);
        ctx.refresh();
        PackagePrivateBeanMethodInheritanceTests.Foo foo1 = ctx.getBean("foo1", PackagePrivateBeanMethodInheritanceTests.Foo.class);
        PackagePrivateBeanMethodInheritanceTests.Foo foo2 = ctx.getBean("foo2", PackagePrivateBeanMethodInheritanceTests.Foo.class);
        ctx.getBean("protectedBar", PackagePrivateBeanMethodInheritanceTests.Bar.class);// <-- i.e. @Bean was registered

        Assert.assertThat(foo1.bar, CoreMatchers.is(foo2.bar));// <-- i.e. @Bean *was* enhanced

    }

    public static class Foo {
        final PackagePrivateBeanMethodInheritanceTests.Bar bar;

        public Foo(PackagePrivateBeanMethodInheritanceTests.Bar bar) {
            this.bar = bar;
        }
    }

    public static class Bar {}

    @Configuration
    public static class ReproConfig extends BaseConfig {
        @Bean
        public PackagePrivateBeanMethodInheritanceTests.Foo foo1() {
            return new PackagePrivateBeanMethodInheritanceTests.Foo(reproBar());
        }

        @Bean
        public PackagePrivateBeanMethodInheritanceTests.Foo foo2() {
            return new PackagePrivateBeanMethodInheritanceTests.Foo(reproBar());
        }
    }

    @Configuration
    public static class WorkaroundConfig extends BaseConfig {
        @Bean
        public PackagePrivateBeanMethodInheritanceTests.Foo foo1() {
            return new PackagePrivateBeanMethodInheritanceTests.Foo(workaroundBar());
        }

        @Bean
        public PackagePrivateBeanMethodInheritanceTests.Foo foo2() {
            return new PackagePrivateBeanMethodInheritanceTests.Foo(workaroundBar());
        }
    }
}

