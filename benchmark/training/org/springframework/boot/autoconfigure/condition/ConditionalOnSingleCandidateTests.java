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
import org.springframework.context.annotation.Primary;

import static SearchStrategy.ANCESTORS;


/**
 * Tests for {@link ConditionalOnSingleCandidate}.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
public class ConditionalOnSingleCandidateTests {
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void singleCandidateNoCandidate() {
        load(ConditionalOnSingleCandidateTests.OnBeanSingleCandidateConfiguration.class);
        assertThat(this.context.containsBean("baz")).isFalse();
    }

    @Test
    public void singleCandidateOneCandidate() {
        load(ConditionalOnSingleCandidateTests.FooConfiguration.class, ConditionalOnSingleCandidateTests.OnBeanSingleCandidateConfiguration.class);
        assertThat(this.context.containsBean("baz")).isTrue();
        assertThat(this.context.getBean("baz")).isEqualTo("foo");
    }

    @Test
    public void singleCandidateInAncestorsOneCandidateInCurrent() {
        load();
        AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext();
        child.register(ConditionalOnSingleCandidateTests.FooConfiguration.class, ConditionalOnSingleCandidateTests.OnBeanSingleCandidateInAncestorsConfiguration.class);
        child.setParent(this.context);
        child.refresh();
        assertThat(child.containsBean("baz")).isFalse();
        child.close();
    }

    @Test
    public void singleCandidateInAncestorsOneCandidateInParent() {
        load(ConditionalOnSingleCandidateTests.FooConfiguration.class);
        AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext();
        child.register(ConditionalOnSingleCandidateTests.OnBeanSingleCandidateInAncestorsConfiguration.class);
        child.setParent(this.context);
        child.refresh();
        assertThat(child.containsBean("baz")).isTrue();
        assertThat(child.getBean("baz")).isEqualTo("foo");
        child.close();
    }

    @Test
    public void singleCandidateInAncestorsOneCandidateInGrandparent() {
        load(ConditionalOnSingleCandidateTests.FooConfiguration.class);
        AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext();
        parent.setParent(this.context);
        parent.refresh();
        AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext();
        child.register(ConditionalOnSingleCandidateTests.OnBeanSingleCandidateInAncestorsConfiguration.class);
        child.setParent(parent);
        child.refresh();
        assertThat(child.containsBean("baz")).isTrue();
        assertThat(child.getBean("baz")).isEqualTo("foo");
        child.close();
        parent.close();
    }

    @Test
    public void singleCandidateMultipleCandidates() {
        load(ConditionalOnSingleCandidateTests.FooConfiguration.class, ConditionalOnSingleCandidateTests.BarConfiguration.class, ConditionalOnSingleCandidateTests.OnBeanSingleCandidateConfiguration.class);
        assertThat(this.context.containsBean("baz")).isFalse();
    }

    @Test
    public void singleCandidateMultipleCandidatesOnePrimary() {
        load(ConditionalOnSingleCandidateTests.FooPrimaryConfiguration.class, ConditionalOnSingleCandidateTests.BarConfiguration.class, ConditionalOnSingleCandidateTests.OnBeanSingleCandidateConfiguration.class);
        assertThat(this.context.containsBean("baz")).isTrue();
        assertThat(this.context.getBean("baz")).isEqualTo("foo");
    }

    @Test
    public void singleCandidateMultipleCandidatesMultiplePrimary() {
        load(ConditionalOnSingleCandidateTests.FooPrimaryConfiguration.class, ConditionalOnSingleCandidateTests.BarPrimaryConfiguration.class, ConditionalOnSingleCandidateTests.OnBeanSingleCandidateConfiguration.class);
        assertThat(this.context.containsBean("baz")).isFalse();
    }

    @Test
    public void invalidAnnotationTwoTypes() {
        assertThatIllegalStateException().isThrownBy(() -> load(.class)).withCauseInstanceOf(IllegalArgumentException.class).withMessageContaining(ConditionalOnSingleCandidateTests.OnBeanSingleCandidateTwoTypesConfiguration.class.getName());
    }

    @Test
    public void invalidAnnotationNoType() {
        assertThatIllegalStateException().isThrownBy(() -> load(.class)).withCauseInstanceOf(IllegalArgumentException.class).withMessageContaining(ConditionalOnSingleCandidateTests.OnBeanSingleCandidateNoTypeConfiguration.class.getName());
    }

    @Test
    public void singleCandidateMultipleCandidatesInContextHierarchy() {
        load(ConditionalOnSingleCandidateTests.FooPrimaryConfiguration.class, ConditionalOnSingleCandidateTests.BarConfiguration.class);
        try (AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext()) {
            child.setParent(this.context);
            child.register(ConditionalOnSingleCandidateTests.OnBeanSingleCandidateConfiguration.class);
            child.refresh();
            assertThat(child.containsBean("baz")).isTrue();
            assertThat(child.getBean("baz")).isEqualTo("foo");
        }
    }

    @Configuration
    @ConditionalOnSingleCandidate(String.class)
    protected static class OnBeanSingleCandidateConfiguration {
        @Bean
        public String baz(String s) {
            return s;
        }
    }

    @Configuration
    @ConditionalOnSingleCandidate(value = String.class, search = ANCESTORS)
    protected static class OnBeanSingleCandidateInAncestorsConfiguration {
        @Bean
        public String baz(String s) {
            return s;
        }
    }

    @Configuration
    @ConditionalOnSingleCandidate(value = String.class, type = "java.lang.String")
    protected static class OnBeanSingleCandidateTwoTypesConfiguration {}

    @Configuration
    @ConditionalOnSingleCandidate
    protected static class OnBeanSingleCandidateNoTypeConfiguration {}

    @Configuration
    protected static class FooConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    protected static class FooPrimaryConfiguration {
        @Bean
        @Primary
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    protected static class BarConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    protected static class BarPrimaryConfiguration {
        @Bean
        @Primary
        public String bar() {
            return "bar";
        }
    }
}

