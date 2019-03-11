/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.config.annotation;


import org.junit.Test;
import org.springframework.core.Ordered;


public class SecurityConfigurerAdapterTests {
    ConcereteSecurityConfigurerAdapter adapter;

    @Test
    public void postProcessObjectPostProcessorsAreSorted() {
        addObjectPostProcessor(new SecurityConfigurerAdapterTests.OrderedObjectPostProcessor(Ordered.LOWEST_PRECEDENCE));
        addObjectPostProcessor(new SecurityConfigurerAdapterTests.OrderedObjectPostProcessor(Ordered.HIGHEST_PRECEDENCE));
        assertThat(postProcess("hi")).isEqualTo(((("hi " + (Ordered.HIGHEST_PRECEDENCE)) + " ") + (Ordered.LOWEST_PRECEDENCE)));
    }

    static class OrderedObjectPostProcessor implements Ordered , ObjectPostProcessor<String> {
        private final int order;

        public OrderedObjectPostProcessor(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }

        @SuppressWarnings("unchecked")
        public String postProcess(String object) {
            return (object + " ") + (order);
        }
    }
}

