/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.map.mutable;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


/**
 * JUnit test for {@link MapAdapter}.
 */
public class MapAdapterTest extends MutableMapTestCase {
    @Test
    public void adaptNull() {
        Verify.assertThrows(NullPointerException.class, () -> new MapAdapter<>(null));
        Verify.assertThrows(NullPointerException.class, () -> MapAdapter.adapt(null));
    }
}

