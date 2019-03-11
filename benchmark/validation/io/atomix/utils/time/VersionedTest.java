/**
 * Copyright 2015-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.utils.time;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Versioned unit tests.
 */
public class VersionedTest {
    private final Versioned<Integer> stats1 = new Versioned(1, 2, 3);

    private final Versioned<Integer> stats2 = new Versioned(1, 2);

    /**
     * Tests the creation of the MapEvent object.
     */
    @Test
    public void testConstruction() {
        MatcherAssert.assertThat(stats1.value(), Matchers.is(1));
        MatcherAssert.assertThat(stats1.version(), Matchers.is(2L));
        MatcherAssert.assertThat(stats1.creationTime(), Matchers.is(3L));
    }

    /**
     * Tests the map function.
     */
    @Test
    public void testMap() {
        Versioned<String> tempObj = stats1.map(VersionedTest::transform);
        MatcherAssert.assertThat(tempObj.value(), Matchers.is("1"));
    }

    /**
     * Tests the valueOrElse method.
     */
    @Test
    public void testOrElse() {
        Versioned<String> vv = new Versioned("foo", 1);
        Versioned<String> nullVV = null;
        MatcherAssert.assertThat(Versioned.valueOrElse(vv, "bar"), Matchers.is("foo"));
        MatcherAssert.assertThat(Versioned.valueOrElse(nullVV, "bar"), Matchers.is("bar"));
    }

    /**
     * Tests the equals, hashCode and toString methods using Guava EqualsTester.
     */
    @Test
    public void testEquals() {
        new com.google.common.testing.EqualsTester().addEqualityGroup(stats1, stats1).addEqualityGroup(stats2).testEquals();
    }
}

