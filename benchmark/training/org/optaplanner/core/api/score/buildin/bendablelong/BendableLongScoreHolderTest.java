/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.api.score.buildin.bendablelong;


import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;


public class BendableLongScoreHolderTest extends AbstractScoreHolderTest {
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
        BendableLongScoreHolder scoreHolder = new BendableLongScoreHolder(false, 2, 5);
        RuleContext rule = mockRuleContext("rule");
        scoreHolder.addHardConstraintMatch(rule, 3, (-1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failFastSoftLevel() {
        BendableLongScoreHolder scoreHolder = new BendableLongScoreHolder(false, 5, 2);
        RuleContext rule = mockRuleContext("rule");
        scoreHolder.addSoftConstraintMatch(rule, 3, (-1L));
    }
}

