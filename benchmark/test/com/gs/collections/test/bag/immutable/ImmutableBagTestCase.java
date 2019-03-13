/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.test.bag.immutable;


import com.gs.collections.test.bag.UnsortedBagTestCase;
import com.gs.collections.test.collection.immutable.ImmutableCollectionTestCase;
import org.junit.Test;


public interface ImmutableBagTestCase extends UnsortedBagTestCase , ImmutableCollectionTestCase {
    @Override
    @Test
    default void Iterable_remove() {
        ImmutableCollectionTestCase.super.Iterable_remove();
    }
}

