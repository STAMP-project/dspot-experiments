/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.zuul;


import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.monitoring.CounterFactory;
import com.netflix.zuul.monitoring.TracerFactory;
import java.util.Map;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;


public class ZuulFilterInitializerTests {
    private Map<String, ZuulFilter> filters;

    private CounterFactory counterFactory;

    private TracerFactory tracerFactory;

    private FilterLoader filterLoader;

    private FilterRegistry filterRegistry;

    private ZuulFilterInitializer initializer;

    @Test
    public void shouldSetupOnContextInitializedEvent() {
        assertThat(TracerFactory.instance()).isEqualTo(tracerFactory);
        assertThat(CounterFactory.instance()).isEqualTo(counterFactory);
        assertThat(filterRegistry.getAllFilters()).containsAll(filters.values());
        initializer.contextDestroyed();
    }

    @Test
    public void shouldCleanupOnContextDestroyed() {
        initializer.contextDestroyed();
        assertThat(ReflectionTestUtils.getField(TracerFactory.class, "INSTANCE")).isNull();
        assertThat(ReflectionTestUtils.getField(CounterFactory.class, "INSTANCE")).isNull();
        assertThat(filterRegistry.getAllFilters()).isEmpty();
        assertThat(getHashFiltersByType().isEmpty()).isTrue();
    }
}

