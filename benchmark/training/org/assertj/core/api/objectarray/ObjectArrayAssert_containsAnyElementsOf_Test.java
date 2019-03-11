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
package org.assertj.core.api.objectarray;


import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectArrayAssertBaseTest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.assertj.core.util.Arrays.array;


public class ObjectArrayAssert_containsAnyElementsOf_Test extends ObjectArrayAssertBaseTest {
    private final List<Object> iterable = Arrays.asList(new Object(), "bar");

    @Test
    public void should_allow_assertion_on_object_array() {
        // GIVEN
        Object[] objectArray = array("foo", "bar");
        String[] stringArray = org.assertj.core.util.Arrays.array("foo", "bar");
        // THEN
        Assertions.assertThat(objectArray).containsAnyElementsOf(Lists.list("foo", "baz"));
        Assertions.assertThat(stringArray).containsAnyElementsOf(Lists.list("foo", "baz"));
    }
}

