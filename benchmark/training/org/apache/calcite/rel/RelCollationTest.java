/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.rel;


import RelFieldCollation.Direction;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link RelCollation} and {@link RelFieldCollation}.
 */
public class RelCollationTest {
    /**
     * Unit test for {@link RelCollations#contains}.
     */
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @Test
    public void testCollationContains() {
        final RelCollation collation21 = RelCollations.of(new RelFieldCollation(2, Direction.ASCENDING), new RelFieldCollation(1, Direction.DESCENDING));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(2)), CoreMatchers.is(true));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(1)), CoreMatchers.is(false));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(0)), CoreMatchers.is(false));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(2, 1)), CoreMatchers.is(true));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(2, 0)), CoreMatchers.is(false));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(2, 1, 3)), CoreMatchers.is(false));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList()), CoreMatchers.is(true));
        // if there are duplicates in keys, later occurrences are ignored
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(2, 1, 2)), CoreMatchers.is(true));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(2, 1, 1)), CoreMatchers.is(true));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(1, 2, 1)), CoreMatchers.is(false));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(1, 1)), CoreMatchers.is(false));
        Assert.assertThat(RelCollations.contains(collation21, Arrays.asList(2, 2)), CoreMatchers.is(true));
        final RelCollation collation1 = RelCollations.of(new RelFieldCollation(1, Direction.DESCENDING));
        Assert.assertThat(RelCollations.contains(collation1, Arrays.asList(1, 1)), CoreMatchers.is(true));
        Assert.assertThat(RelCollations.contains(collation1, Arrays.asList(2, 2)), CoreMatchers.is(false));
        Assert.assertThat(RelCollations.contains(collation1, Arrays.asList(1, 2, 1)), CoreMatchers.is(false));
        Assert.assertThat(RelCollations.contains(collation1, Arrays.asList()), CoreMatchers.is(true));
    }

    /**
     * Unit test for
     *  {@link org.apache.calcite.rel.RelCollationImpl#compareTo}.
     */
    @Test
    public void testCollationCompare() {
        Assert.assertThat(RelCollationTest.collation(1, 2).compareTo(RelCollationTest.collation(1, 2)), CoreMatchers.equalTo(0));
        Assert.assertThat(RelCollationTest.collation(1, 2).compareTo(RelCollationTest.collation(1)), CoreMatchers.equalTo(1));
        Assert.assertThat(RelCollationTest.collation(1).compareTo(RelCollationTest.collation(1, 2)), CoreMatchers.equalTo((-1)));
        Assert.assertThat(RelCollationTest.collation(1, 3).compareTo(RelCollationTest.collation(1, 2)), CoreMatchers.equalTo(1));
        Assert.assertThat(RelCollationTest.collation(0, 3).compareTo(RelCollationTest.collation(1, 2)), CoreMatchers.equalTo((-1)));
        Assert.assertThat(RelCollationTest.collation().compareTo(RelCollationTest.collation(0)), CoreMatchers.equalTo((-1)));
        Assert.assertThat(RelCollationTest.collation(1).compareTo(RelCollationTest.collation()), CoreMatchers.equalTo(1));
    }
}

/**
 * End RelCollationTest.java
 */
