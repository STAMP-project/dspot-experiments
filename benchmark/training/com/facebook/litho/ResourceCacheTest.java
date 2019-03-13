/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import RuntimeEnvironment.application;
import android.content.res.Configuration;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class ResourceCacheTest {
    @Test
    public void testSameConfigurationDoesNotUpdateResourceCache() {
        Configuration configuration = application.getResources().getConfiguration();
        ResourceCache cache = ResourceCache.getLatest(configuration);
        assertThat(cache).isEqualTo(ResourceCache.getLatest(configuration));
    }

    @Test
    public void testSameConfigurationNewInstanceDoesNotUpdateResourceCache() {
        Configuration configuration = application.getResources().getConfiguration();
        ResourceCache cache = ResourceCache.getLatest(configuration);
        assertThat(cache).isEqualTo(ResourceCache.getLatest(new Configuration(configuration)));
    }

    @Test
    public void testDifferentLocaleUpdatesResourceCache() {
        Configuration configuration = new Configuration(application.getResources().getConfiguration());
        ResourceCacheTest.setLocale(configuration, new Locale("en"));
        ResourceCache cache = ResourceCache.getLatest(configuration);
        ResourceCacheTest.setLocale(configuration, new Locale("it"));
        assertThat(cache).isNotEqualTo(ResourceCache.getLatest(configuration));
    }
}

