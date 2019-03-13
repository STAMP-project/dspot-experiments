/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.vo.mapidsameinstance.One;
import com.github.dozermapper.core.vo.mapidsameinstance.Two;
import org.junit.Test;


/**
 * Regression-tests to ensure that mapid is respected even when
 * the same instance is mapped.
 *
 * @see <a href="https://github.com/DozerMapper/dozer/issues/238">MappedFieldsTracker does not consider map-id 238</a>
 */
public class MapIdSameInstanceTest extends AbstractFunctionalTest {
    private One one;

    private Two two;

    /**
     * Different instances of Two in the fields of One.
     * Worked correctly in Dozer 5.5.1.
     */
    @Test
    public void testMappingDifferentInstances() {
        testMapping(createTwo());
    }

    /**
     * Same instance of Two in the fields of One. Failed
     * in Dozer 5.5.1, fixed by pull request #257.
     */
    @Test
    public void testMappingSameInstance() {
        testMapping(two);
    }
}

