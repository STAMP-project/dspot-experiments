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


import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.IterableUtil;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link StandardComparisonStrategy#duplicatesFrom(java.util.Collection)}.<br>
 *
 * @author Joel Costigliola
 */
public class StandardComparisonStrategy_duplicatesFrom_Test extends AbstractTest_StandardComparisonStrategy {
    @Test
    public void should_return_existing_duplicates() {
        List<String> list = Lists.newArrayList("Merry", "Frodo", null, null, "Merry", "Sam", "Frodo");
        Iterable<?> duplicates = AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.duplicatesFrom(list);
        Assertions.assertThat(IterableUtil.sizeOf(duplicates)).isEqualTo(3);
        Assertions.assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(duplicates, "Frodo")).isTrue();
        Assertions.assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(duplicates, "Merry")).isTrue();
        Assertions.assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(duplicates, null)).isTrue();
    }

    @Test
    public void should_return_existing_duplicates_array() {
        List<String[]> list = Lists.newArrayList(Arrays.array("Merry"), Arrays.array("Frodo"), new String[]{ null }, new String[]{ null }, Arrays.array("Merry"), Arrays.array("Sam"), Arrays.array("Frodo"));
        Iterable<?> duplicates = AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.duplicatesFrom(list);
        Assertions.assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(duplicates, new String[]{ null })).as("must contains null").isTrue();
        Assertions.assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(duplicates, Arrays.array("Frodo"))).as("must contains Frodo").isTrue();
        Assertions.assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.iterableContains(duplicates, Arrays.array("Merry"))).as("must contains Merry").isTrue();
        Assertions.assertThat(IterableUtil.sizeOf(duplicates)).isEqualTo(3);
    }

    @Test
    public void should_not_return_any_duplicates() {
        Iterable<?> duplicates = AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.duplicatesFrom(Lists.newArrayList("Frodo", "Sam", "Gandalf"));
        Assertions.assertThat(IterableUtil.isNullOrEmpty(duplicates)).isTrue();
    }

    @Test
    public void should_not_return_any_duplicates_if_collection_is_empty() {
        Iterable<?> duplicates = AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.duplicatesFrom(new ArrayList());
        Assertions.assertThat(IterableUtil.isNullOrEmpty(duplicates)).isTrue();
    }

    @Test
    public void should_not_return_any_duplicates_if_collection_is_null() {
        Iterable<?> duplicates = AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.duplicatesFrom(null);
        Assertions.assertThat(IterableUtil.isNullOrEmpty(duplicates)).isTrue();
    }
}

