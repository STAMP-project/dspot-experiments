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
package org.assertj.core.navigation;


import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests navigating a generated Assert class with a List property
 */
public class ClassBasedNavigableList_withDefault_Test {
    @Test
    public void should_use_ObjectAssert_by_default() {
        List<String> list = Lists.newArrayList("one", "two", "three");
        Assertions.assertThat(list).first().isEqualTo("one");
        Assertions.assertThat(list).last().isEqualTo("three");
        Assertions.assertThat(list).element(1).isEqualTo("two");
    }

    @Test
    public void should_honor_list_assertions() {
        List<String> list = Lists.newArrayList("one", "two", "three");
        Assertions.assertThat(list).contains("one", Assertions.atIndex(0)).first().isEqualTo("one");
    }
}

