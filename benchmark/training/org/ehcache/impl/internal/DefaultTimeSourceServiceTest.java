/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.internal;


import ServiceLocator.DependencySet;
import org.ehcache.core.spi.ServiceLocator;
import org.ehcache.core.spi.time.SystemTimeSource;
import org.ehcache.core.spi.time.TimeSource;
import org.ehcache.core.spi.time.TimeSourceService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * DefaultTimeSourceServiceTest
 */
public class DefaultTimeSourceServiceTest {
    @Test
    public void testResolvesDefaultTimeSource() {
        ServiceLocator.DependencySet dependencySet = ServiceLocator.dependencySet().with(TimeSourceService.class);
        ServiceLocator serviceLocator = dependencySet.build();
        Assert.assertThat(serviceLocator.getService(TimeSourceService.class).getTimeSource(), CoreMatchers.sameInstance(SystemTimeSource.INSTANCE));
    }

    @Test
    public void testCanConfigureAlternateTimeSource() {
        TimeSource timeSource = Mockito.mock(TimeSource.class);
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new TimeSourceConfiguration(timeSource)).build();
        TimeSourceService timeSourceService = serviceLocator.getService(TimeSourceService.class);
        Assert.assertThat(timeSourceService.getTimeSource(), CoreMatchers.sameInstance(timeSource));
    }
}

