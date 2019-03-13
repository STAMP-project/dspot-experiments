/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
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
 */
package com.github.ambry.store;


import TestUtils.RANDOM;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;


/**
 * Unit tests {@link CostBenefitInfo}
 */
public class CostBenefitInfoTest {
    /**
     * Tests {@link CostBenefitInfo} for construction and getters
     */
    @Test
    public void testCostBenefitInfo() {
        for (int i = 0; i < 5; i++) {
            List<String> randomSegments = CompactionPolicyTest.generateRandomLogSegmentName(3);
            long cost = getRandomCost();
            int benefit = 1 + (RANDOM.nextInt(Integer.MAX_VALUE));
            CostBenefitInfo actual = new CostBenefitInfo(randomSegments, cost, benefit);
            verifyCostBenfitInfo(actual, randomSegments, cost, benefit, (cost * (1.0 / benefit)));
        }
    }

    /**
     * Tests {@link CostBenefitInfo} for 0 benefit
     */
    @Test
    public void testCostBenefitInfoForZeroBenefit() {
        List<String> randomSegments = CompactionPolicyTest.generateRandomLogSegmentName(3);
        long cost = getRandomCost();
        int benefit = 0;
        CostBenefitInfo actual = new CostBenefitInfo(randomSegments, cost, benefit);
        verifyCostBenfitInfo(actual, randomSegments, cost, benefit, Double.MAX_VALUE);
    }

    /**
     * Tests {@link CostBenefitInfo} for comparisons
     */
    @Test
    public void testCostBenefitInfoComparison() {
        List<String> randomSegments = CompactionPolicyTest.generateRandomLogSegmentName(3);
        long cost = getRandomCost();
        int benefit = 1 + (RANDOM.nextInt(((Integer.MAX_VALUE) - 1)));
        CostBenefitInfo one = new CostBenefitInfo(randomSegments, cost, benefit);
        // generate a CostBenefitInfo with cost = 1 + one's cost
        compareAndTest(one, 1, 0, (-1));
        // generate a CostBenefitInfo with cost = 1 - one's cost
        compareAndTest(one, (-1), 0, 1);
        // generate a CostBenefitInfo with same cost as one
        compareAndTest(one, 0, 0, 0);
        // generate a CostBenefitInfo with benefit = 1 + one's benefit
        compareAndTest(one, 0, 1, 1);
        // generate a CostBenefitInfo with benefit = 1 - one's benefit
        compareAndTest(one, 0, (-1), (-1));
        // test a case where costBenefitRatio is same, but diff cost and benefit.
        cost = ThreadLocalRandom.current().nextLong(((Integer.MAX_VALUE) / 2));
        benefit = 2 + (RANDOM.nextInt((((Integer.MAX_VALUE) / 2) - 2)));
        if ((cost % 2) != 0) {
            cost++;
        }
        if ((benefit % 2) != 0) {
            benefit++;
        }
        one = new CostBenefitInfo(randomSegments, cost, benefit);
        // generate a CostBenefitInfo with cost = half of one and benefit = half of one. CostBenefit is same, but one'e
        // benefit is more
        compareAndTest(one, ((cost / 2) * (-1)), ((benefit / 2) * (-1)), (-1));
    }
}

