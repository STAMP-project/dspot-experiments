/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.comparator;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;


@RunWith(Parameterized.class)
public class FlatteningHardSoftScoreComparatorTest {
    private int expectedResult;

    private int modifier;

    private String firstScore;

    private String secondScore;

    public FlatteningHardSoftScoreComparatorTest(int expectedResult, int modifier, String firstScore, String secondScore) {
        this.expectedResult = expectedResult;
        this.modifier = modifier;
        this.firstScore = firstScore;
        this.secondScore = secondScore;
    }

    @Test
    public void compare() {
        Assert.assertEquals(expectedResult, new FlatteningHardSoftScoreComparator(modifier).compare(new HardSoftScoreDefinition().parseScore(firstScore), new HardSoftScoreDefinition().parseScore(secondScore)));
    }
}

