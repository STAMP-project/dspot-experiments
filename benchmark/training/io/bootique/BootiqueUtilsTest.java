/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.AtLeast;


public class BootiqueUtilsTest {
    @Test
    public void testToArray() {
        Assert.assertArrayEquals(new String[]{  }, BootiqueUtils.toArray(Arrays.asList()));
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, BootiqueUtils.toArray(Arrays.asList("a", "b", "c")));
    }

    @Test
    public void testMergeArrays() {
        Assert.assertArrayEquals(new String[]{  }, BootiqueUtils.mergeArrays(new String[0], new String[0]));
        Assert.assertArrayEquals(new String[]{ "a" }, BootiqueUtils.mergeArrays(new String[]{ "a" }, new String[0]));
        Assert.assertArrayEquals(new String[]{ "b" }, BootiqueUtils.mergeArrays(new String[0], new String[]{ "b" }));
        Assert.assertArrayEquals(new String[]{ "b", "c", "d" }, BootiqueUtils.mergeArrays(new String[]{ "b", "c" }, new String[]{ "d" }));
    }

    @Test
    public void moduleProviderDependencies() {
        final BQModuleProvider testModuleProvider1 = Mockito.mock(BQModuleProvider.class);
        final BQModuleProvider testModuleProvider2 = Mockito.mock(BQModuleProvider.class);
        final BQModuleProvider testModuleProvider3 = Mockito.mock(BQModuleProvider.class);
        Mockito.when(testModuleProvider1.dependencies()).thenReturn(Arrays.asList(testModuleProvider2, testModuleProvider3));
        final Collection<BQModuleProvider> bqModuleProviders = BootiqueUtils.moduleProviderDependencies(Collections.singletonList(testModuleProvider1));
        Assert.assertThat(bqModuleProviders, CoreMatchers.hasItems(testModuleProvider1, testModuleProvider2, testModuleProvider3));
        Assert.assertEquals(3, bqModuleProviders.size());
        Mockito.verify(testModuleProvider1, new AtLeast(1)).dependencies();
        Mockito.verify(testModuleProvider2, new AtLeast(1)).dependencies();
        Mockito.verify(testModuleProvider3, new AtLeast(1)).dependencies();
        Mockito.verifyNoMoreInteractions(testModuleProvider1, testModuleProvider2, testModuleProvider3);
    }

    @Test
    public void moduleProviderDependenciesTwoLevels() {
        final BQModuleProvider testModuleProvider1 = Mockito.mock(BQModuleProvider.class);
        final BQModuleProvider testModuleProvider2 = Mockito.mock(BQModuleProvider.class);
        final BQModuleProvider testModuleProvider3 = Mockito.mock(BQModuleProvider.class);
        Mockito.when(testModuleProvider1.dependencies()).thenReturn(Collections.singletonList(testModuleProvider2));
        Mockito.when(testModuleProvider2.dependencies()).thenReturn(Collections.singletonList(testModuleProvider3));
        final Collection<BQModuleProvider> bqModuleProviders = BootiqueUtils.moduleProviderDependencies(Collections.singletonList(testModuleProvider1));
        Assert.assertThat(bqModuleProviders, CoreMatchers.hasItems(testModuleProvider1, testModuleProvider2, testModuleProvider3));
        Assert.assertEquals(3, bqModuleProviders.size());
        Mockito.verify(testModuleProvider1, new AtLeast(1)).dependencies();
        Mockito.verify(testModuleProvider2, new AtLeast(1)).dependencies();
        Mockito.verify(testModuleProvider3, new AtLeast(1)).dependencies();
        Mockito.verifyNoMoreInteractions(testModuleProvider1, testModuleProvider2, testModuleProvider3);
    }

    @Test
    public void moduleProviderDependenciesCircular() {
        final BQModuleProvider testModuleProvider1 = Mockito.mock(BQModuleProvider.class);
        final BQModuleProvider testModuleProvider2 = Mockito.mock(BQModuleProvider.class);
        final BQModuleProvider testModuleProvider3 = Mockito.mock(BQModuleProvider.class);
        Mockito.when(testModuleProvider1.dependencies()).thenReturn(Collections.singletonList(testModuleProvider2));
        Mockito.when(testModuleProvider2.dependencies()).thenReturn(Collections.singletonList(testModuleProvider3));
        Mockito.when(testModuleProvider3.dependencies()).thenReturn(Collections.singletonList(testModuleProvider1));
        final Collection<BQModuleProvider> bqModuleProviders = BootiqueUtils.moduleProviderDependencies(Collections.singletonList(testModuleProvider1));
        Assert.assertThat(bqModuleProviders, CoreMatchers.hasItems(testModuleProvider1, testModuleProvider2, testModuleProvider3));
        Assert.assertEquals(3, bqModuleProviders.size());
        Mockito.verify(testModuleProvider1, new AtLeast(1)).dependencies();
        Mockito.verify(testModuleProvider2, new AtLeast(1)).dependencies();
        Mockito.verify(testModuleProvider3, new AtLeast(1)).dependencies();
        Mockito.verifyNoMoreInteractions(testModuleProvider1, testModuleProvider2, testModuleProvider3);
    }
}

