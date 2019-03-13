/**
 * Copyright 2018 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package com.netflix.zuul.init;


import com.netflix.zuul.init2.TestZuulFilter2;
import org.apache.commons.configuration.AbstractConfiguration;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ZuulFiltersModuleTest {
    @Mock
    AbstractConfiguration configuration;

    ZuulFiltersModule module = new ZuulFiltersModule();

    @Test
    public void testDefaultFilterLocations() {
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.locations"))).thenReturn("inbound,outbound,endpoint".split(","));
        String[] filterLocations = module.findFilterLocations(configuration);
        MatcherAssert.assertThat(filterLocations.length, CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(filterLocations[1], CoreMatchers.equalTo("outbound"));
    }

    @Test
    public void testEmptyFilterLocations() {
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.locations"))).thenReturn(new String[0]);
        String[] filterLocations = module.findFilterLocations(configuration);
        MatcherAssert.assertThat(filterLocations.length, CoreMatchers.equalTo(0));
    }

    @Test
    public void testEmptyClassNames() {
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.classes"))).thenReturn(new String[]{  });
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.packages"))).thenReturn(new String[]{  });
        String[] classNames = module.findClassNames(configuration);
        MatcherAssert.assertThat(classNames.length, CoreMatchers.equalTo(0));
    }

    @Test
    public void testClassNamesOnly() {
        Class expectedClass = TestZuulFilter.class;
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.classes"))).thenReturn(new String[]{ "com.netflix.zuul.init.TestZuulFilter" });
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.packages"))).thenReturn(new String[]{  });
        String[] classNames = module.findClassNames(configuration);
        MatcherAssert.assertThat(classNames.length, CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(classNames[0], CoreMatchers.equalTo(expectedClass.getCanonicalName()));
    }

    @Test
    public void testClassNamesPackagesOnly() {
        Class expectedClass = TestZuulFilter.class;
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.classes"))).thenReturn(new String[]{  });
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.packages"))).thenReturn(new String[]{ "com.netflix.zuul.init" });
        String[] classNames = module.findClassNames(configuration);
        MatcherAssert.assertThat(classNames.length, CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(classNames[0], CoreMatchers.equalTo(expectedClass.getCanonicalName()));
    }

    @Test
    public void testMultiClasses() {
        Class expectedClass1 = TestZuulFilter.class;
        Class expectedClass2 = TestZuulFilter2.class;
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.classes"))).thenReturn(new String[]{ "com.netflix.zuul.init.TestZuulFilter", "com.netflix.zuul.init2.TestZuulFilter2" });
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.packages"))).thenReturn(new String[0]);
        String[] classNames = module.findClassNames(configuration);
        MatcherAssert.assertThat(classNames.length, CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(classNames[0], CoreMatchers.equalTo(expectedClass1.getCanonicalName()));
        MatcherAssert.assertThat(classNames[1], CoreMatchers.equalTo(expectedClass2.getCanonicalName()));
    }

    @Test
    public void testMultiPackages() {
        Class expectedClass1 = TestZuulFilter.class;
        Class expectedClass2 = TestZuulFilter2.class;
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.classes"))).thenReturn(new String[0]);
        Mockito.when(configuration.getStringArray(ArgumentMatchers.eq("zuul.filters.packages"))).thenReturn(new String[]{ "com.netflix.zuul.init", "com.netflix.zuul.init2" });
        String[] classNames = module.findClassNames(configuration);
        MatcherAssert.assertThat(classNames.length, CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(classNames[0], CoreMatchers.equalTo(expectedClass1.getCanonicalName()));
        MatcherAssert.assertThat(classNames[1], CoreMatchers.equalTo(expectedClass2.getCanonicalName()));
    }
}

