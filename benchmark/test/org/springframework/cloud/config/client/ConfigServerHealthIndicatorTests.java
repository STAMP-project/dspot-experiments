/**
 * Copyright 2014-2019 the original author or authors.
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
package org.springframework.cloud.config.client;


import Status.DOWN;
import Status.UNKNOWN;
import Status.UP;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;


/**
 *
 *
 * @author Dave Syer
 * @author Marcos Barbero
 */
public class ConfigServerHealthIndicatorTests {
    private ConfigServicePropertySourceLocator locator = Mockito.mock(ConfigServicePropertySourceLocator.class);

    private Environment environment = Mockito.mock(Environment.class);

    private ConfigServerHealthIndicator indicator = new ConfigServerHealthIndicator(this.locator, this.environment, new ConfigClientHealthProperties());

    @Test
    public void testDefaultStatus() {
        // UNKNOWN is better than DOWN since it doesn't stop the app from working
        assertThat(this.indicator.health().getStatus()).isEqualTo(UNKNOWN);
    }

    @Test
    public void testExceptionStatus() {
        Mockito.doThrow(new IllegalStateException()).when(this.locator).locate(ArgumentMatchers.any(Environment.class));
        assertThat(this.indicator.health().getStatus()).isEqualTo(DOWN);
        Mockito.verify(this.locator, Mockito.times(1)).locate(ArgumentMatchers.any(Environment.class));
    }

    @Test
    public void testServerUp() {
        PropertySource<?> source = new MapPropertySource("foo", Collections.<String, Object>emptyMap());
        Mockito.doReturn(source).when(this.locator).locate(ArgumentMatchers.any(Environment.class));
        assertThat(this.indicator.health().getStatus()).isEqualTo(UP);
        Mockito.verify(this.locator, Mockito.times(1)).locate(ArgumentMatchers.any(Environment.class));
    }

    @Test
    public void healthIsCached() {
        PropertySource<?> source = new MapPropertySource("foo", Collections.<String, Object>emptyMap());
        Mockito.doReturn(source).when(this.locator).locate(ArgumentMatchers.any(Environment.class));
        // not cached
        assertThat(this.indicator.health().getStatus()).isEqualTo(UP);
        // cached
        assertThat(this.indicator.health().getStatus()).isEqualTo(UP);
        Mockito.verify(this.locator, Mockito.times(1)).locate(ArgumentMatchers.any(Environment.class));
    }
}

