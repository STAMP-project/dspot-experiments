/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config.spi;


import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link OverrideSource}.
 */
public class OverrideSourceTest {
    @Test
    public void testOrderedPropertiesNotLoadedIsEmpty() {
        OrderedProperties props = new OrderedProperties();
        MatcherAssert.assertThat(props.orderedMap().entrySet().isEmpty(), Is.is(true));
    }

    @Test
    public void testOrderedPropertiesUseInsertionOrderedMap() {
        OrderedProperties props = new OrderedProperties();
        MatcherAssert.assertThat(props.orderedMap(), IsInstanceOf.instanceOf(LinkedHashMap.class));
    }

    @Test
    public void testOrderedPropertiesLoadKeepsOrdering() throws IOException {
        OrderedProperties props = new OrderedProperties();
        props.load(new StringReader(("" + (("aaa=1\n" + "bbb=2\n") + "ccc=3\n"))));
        MatcherAssert.assertThat(props.orderedMap().keySet(), Matchers.contains("aaa", "bbb", "ccc"));
    }
}

