/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api.recursive.comparison;


import org.assertj.core.api.Assertions;
import org.assertj.core.internal.objects.data.Person;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class RecursiveComparisonConfiguration_shouldIgnoreOverriddenEqualsOf_Test {
    private RecursiveComparisonConfiguration recursiveComparisonConfiguration;

    @Test
    public void should_ignore_all_overridden_equals_for_non_java_types() {
        // GIVEN
        DualValue dualKey = new DualValue(Lists.list("foo"), new Person(), new Person());
        recursiveComparisonConfiguration.ignoreAllOverriddenEquals();
        // WHEN
        boolean ignored = recursiveComparisonConfiguration.shouldIgnoreOverriddenEqualsOf(dualKey);
        // THEN
        Assertions.assertThat(ignored).as("All overridden equals should be ignored").isTrue();
    }
}

