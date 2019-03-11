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
package org.assertj.core.api.list;


import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class ListAssert_raw_list_assertions_chained_after_superclass_method_Test {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_bug_485() {
        // https://github.com/joel-costigliola/assertj-core/issues/485
        List list = new ArrayList<>();
        list.add("Key1");
        list.add("Key2");
        Assertions.assertThat(list).as("").isSorted().contains("Key1", "Key2");
        Assertions.assertThat(list).as("").containsOnly("Key1", "Key2");
    }
}

