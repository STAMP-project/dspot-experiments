/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.domain;


import com.thoughtworks.go.config.ResourceConfig;
import com.thoughtworks.go.config.ResourceConfigs;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ResourcesTest {
    @Test
    public void shouldConvertResourceConfigsToListOfResource() {
        final Resources resources = new Resources(new ResourceConfigs("foo,bar"));
        Assert.assertThat(resources, Matchers.hasSize(2));
        Assert.assertThat(resources, Matchers.contains(new Resource("foo"), new Resource("bar")));
    }

    @Test
    public void shouldConvertResourceListToResourceConfigs() {
        final Resources resources = new Resources(new Resource("foo"), new Resource("bar"));
        final ResourceConfigs resourceConfigs = resources.toResourceConfigs();
        Assert.assertThat(resourceConfigs, Matchers.hasSize(2));
        Assert.assertThat(resourceConfigs, Matchers.contains(new ResourceConfig("foo"), new ResourceConfig("bar")));
    }

    @Test
    public void shouldConvertCommaSeparatedResourcesStringToResources() {
        final Resources resources = new Resources("foo,bar,baz");
        Assert.assertThat(resources, Matchers.hasSize(3));
        Assert.assertThat(resources, Matchers.contains(new Resource("foo"), new Resource("bar"), new Resource("baz")));
    }
}

