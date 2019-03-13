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
package org.optaplanner.core.api.score.buildin.bendablebigdecimal;


import java.math.BigDecimal;
import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;


public class BendableBigDecimalScoreHolderTest extends AbstractScoreHolderTest {
    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    @Test
    public void rewardPenalizeWithConstraintMatch() {
        rewardPenalize(true);
    }

    @Test
    public void rewardPenalizeWithoutConstraintMatch() {
        rewardPenalize(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failFastHardLevel() {
        BendableBigDecimalScoreHolder scoreHolder = new BendableBigDecimalScoreHolder(false, 2, 5);
        RuleContext rule = mockRuleContext("rule");
        scoreHolder.addHardConstraintMatch(rule, 3, new BigDecimal("-0.01"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failFastSoftLevel() {
        BendableBigDecimalScoreHolder scoreHolder = new BendableBigDecimalScoreHolder(false, 5, 2);
        RuleContext rule = mockRuleContext("rule");
        scoreHolder.addSoftConstraintMatch(rule, 3, new BigDecimal("-0.01"));
    }
}

