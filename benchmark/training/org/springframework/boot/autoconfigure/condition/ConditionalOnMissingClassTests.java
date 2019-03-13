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
package org.springframework.boot.autoconfigure.condition;


import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ConditionalOnMissingClass}.
 *
 * @author Dave Syer
 */
public class ConditionalOnMissingClassTests {
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void testVanillaOnClassCondition() {
        this.context.register(ConditionalOnMissingClassTests.BasicConfiguration.class, ConditionalOnMissingClassTests.FooConfiguration.class);
        this.context.refresh();
        assertThat(this.context.containsBean("bar")).isFalse();
        assertThat(this.context.getBean("foo")).isEqualTo("foo");
    }

    @Test
    public void testMissingOnClassCondition() {
        this.context.register(ConditionalOnMissingClassTests.MissingConfiguration.class, ConditionalOnMissingClassTests.FooConfiguration.class);
        this.context.refresh();
        assertThat(this.context.containsBean("bar")).isTrue();
        assertThat(this.context.getBean("foo")).isEqualTo("foo");
    }

    @Configuration
    @ConditionalOnMissingClass("org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClassTests")
    protected static class BasicConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnMissingClass("FOO")
    protected static class MissingConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    protected static class FooConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }
}

