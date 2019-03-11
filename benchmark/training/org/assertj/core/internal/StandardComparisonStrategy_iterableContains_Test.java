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
package org.assertj.core.internal;


import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link StandardComparisonStrategy#iterableContains(java.util.Collection, Object)}.
 *
 * @author Joel Costigliola
 */
public class StandardComparisonStrategy_iterableContains_Test extends AbstractTest_StandardComparisonStrategy {
    @Test
    public void should_pass() {
        List<?> list = Lists.newArrayList("Sam", "Merry", null, "Frodo");
        assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(list, "Frodo")).isTrue();
        assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(list, null)).isTrue();
        assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(list, "Sauron")).isFalse();
    }

    @Test
    public void should_return_false_if_iterable_is_null() {
        assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(null, "Sauron")).isFalse();
    }
}

