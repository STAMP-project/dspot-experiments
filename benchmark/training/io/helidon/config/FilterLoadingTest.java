/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config;


import io.helidon.common.CollectionsHelper;
import io.helidon.config.internal.AutoLoadedConfigFilter;
import io.helidon.config.internal.AutoLoadedConfigHighPriority;
import io.helidon.config.internal.AutoLoadedConfigPriority;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Test checking that auto-loaded filter service and filter provider service works.
 */
public class FilterLoadingTest {
    private static final String ORIGINAL_VALUE_SUBJECT_TO_AUTO_FILTERING = "originalValue";

    private static final String ORIGINAL_VALUE_SUBJECT_TO_AUTO_FILTERING_VIA_PROVIDER = "originalValueForProviderTest";

    private static final String UNAFFECTED_KEY = "key1";

    private static final String UNAFFECTED_VALUE = "value1";

    public FilterLoadingTest() {
    }

    @Test
    public void testAutoLoadedFilter() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf(FilterLoadingTest.UNAFFECTED_KEY, FilterLoadingTest.UNAFFECTED_VALUE, AutoLoadedConfigFilter.KEY_SUBJECT_TO_AUTO_FILTERING, FilterLoadingTest.ORIGINAL_VALUE_SUBJECT_TO_AUTO_FILTERING, "name", "Joachim"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get(FilterLoadingTest.UNAFFECTED_KEY).asString(), CoreMatchers.is(ConfigValues.simpleValue(FilterLoadingTest.UNAFFECTED_VALUE)));
        MatcherAssert.assertThat(config.get(AutoLoadedConfigFilter.KEY_SUBJECT_TO_AUTO_FILTERING).asString(), CoreMatchers.is(ConfigValues.simpleValue(AutoLoadedConfigFilter.EXPECTED_FILTERED_VALUE)));
    }

    @Test
    public void testSuppressedAutoLoadedFilter() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf(FilterLoadingTest.UNAFFECTED_KEY, "value1", AutoLoadedConfigFilter.KEY_SUBJECT_TO_AUTO_FILTERING, FilterLoadingTest.ORIGINAL_VALUE_SUBJECT_TO_AUTO_FILTERING, "name", "Joachim"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get(FilterLoadingTest.UNAFFECTED_KEY).asString(), CoreMatchers.is(ConfigValues.simpleValue(FilterLoadingTest.UNAFFECTED_VALUE)));
        MatcherAssert.assertThat(config.get(AutoLoadedConfigFilter.KEY_SUBJECT_TO_AUTO_FILTERING).asString(), CoreMatchers.is(ConfigValues.simpleValue(FilterLoadingTest.ORIGINAL_VALUE_SUBJECT_TO_AUTO_FILTERING)));
    }

    @Test
    public void testPrioritizedAutoLoadedConfigFilters() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf(FilterLoadingTest.UNAFFECTED_KEY, "value1", AutoLoadedConfigPriority.KEY_SUBJECT_TO_AUTO_FILTERING, FilterLoadingTest.ORIGINAL_VALUE_SUBJECT_TO_AUTO_FILTERING, "name", "Joachim"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get(FilterLoadingTest.UNAFFECTED_KEY).asString(), CoreMatchers.is(ConfigValues.simpleValue(FilterLoadingTest.UNAFFECTED_VALUE)));
        MatcherAssert.assertThat(config.get(AutoLoadedConfigPriority.KEY_SUBJECT_TO_AUTO_FILTERING).asString(), CoreMatchers.is(ConfigValues.simpleValue(AutoLoadedConfigHighPriority.EXPECTED_FILTERED_VALUE)));
    }

    @Test
    public void testSuppressedPrioritizedAutoLoadedConfigFilters() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf(FilterLoadingTest.UNAFFECTED_KEY, "value1", AutoLoadedConfigPriority.KEY_SUBJECT_TO_AUTO_FILTERING, FilterLoadingTest.ORIGINAL_VALUE_SUBJECT_TO_AUTO_FILTERING, "name", "Joachim"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        MatcherAssert.assertThat(config.get(FilterLoadingTest.UNAFFECTED_KEY).asString(), CoreMatchers.is(ConfigValues.simpleValue(FilterLoadingTest.UNAFFECTED_VALUE)));
        MatcherAssert.assertThat(config.get(AutoLoadedConfigPriority.KEY_SUBJECT_TO_AUTO_FILTERING).asString(), CoreMatchers.is(ConfigValues.simpleValue(FilterLoadingTest.ORIGINAL_VALUE_SUBJECT_TO_AUTO_FILTERING)));
    }
}

