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
package io.helidon.config.tests.meta;


import io.helidon.config.tests.module.meta1.MyConfigSource1;
import io.helidon.config.tests.module.meta2.MyConfigSource2;
import org.junit.jupiter.api.Test;


/**
 * Tests custom config source type registration.
 * <ul>
 * <li>Custom config source types are loaded from more then one module.</li>
 * </ul>
 */
public class CustomConfigSourceTypesTest {
    @Test
    public void testCustomTypeClass1() {
        testCustomType("meta1class", MyConfigSource1.class);
    }

    @Test
    public void testCustomTypeClass2() {
        testCustomType("meta2class", MyConfigSource2.class);
    }
}

