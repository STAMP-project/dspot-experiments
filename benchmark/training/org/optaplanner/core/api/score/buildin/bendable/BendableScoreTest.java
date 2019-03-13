/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.api.score.buildin.bendable;


import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;


public class BendableScoreTest extends AbstractScoreTest {
    private BendableScoreDefinition scoreDefinitionHSS = new BendableScoreDefinition(1, 2);

    private BendableScoreDefinition scoreDefinitionHHH = new BendableScoreDefinition(3, 0);

    private BendableScoreDefinition scoreDefinitionSSS = new BendableScoreDefinition(0, 3);

    @Test
    public void of() {
        Assert.assertEquals(scoreDefinitionHSS.createScore((-147), 0, 0), BendableScore.ofHard(1, 2, 0, (-147)));
        Assert.assertEquals(scoreDefinitionHSS.createScore(0, (-258), 0), BendableScore.ofSoft(1, 2, 0, (-258)));
        Assert.assertEquals(scoreDefinitionHSS.createScore(0, 0, (-369)), BendableScore.ofSoft(1, 2, 1, (-369)));
        Assert.assertEquals(scoreDefinitionHHH.createScore(0, 0, (-369)), BendableScore.ofHard(3, 0, 2, (-369)));
        Assert.assertEquals(scoreDefinitionSSS.createScore(0, 0, (-369)), BendableScore.ofSoft(0, 3, 2, (-369)));
    }

    @Test
    public void parseScore() {
        Assert.assertEquals(scoreDefinitionHSS.createScore((-147), (-258), (-369)), scoreDefinitionHSS.parseScore("[-147]hard/[-258/-369]soft"));
        Assert.assertEquals(scoreDefinitionHHH.createScore((-147), (-258), (-369)), scoreDefinitionHHH.parseScore("[-147/-258/-369]hard/[]soft"));
        Assert.assertEquals(scoreDefinitionSSS.createScore((-147), (-258), (-369)), scoreDefinitionSSS.parseScore("[]hard/[-147/-258/-369]soft"));
        Assert.assertEquals(scoreDefinitionSSS.createScoreUninitialized((-7), (-147), (-258), (-369)), scoreDefinitionSSS.parseScore("-7init/[]hard/[-147/-258/-369]soft"));
        Assert.assertEquals(scoreDefinitionHSS.createScore((-147), (-258), Integer.MIN_VALUE), scoreDefinitionHSS.parseScore("[-147]hard/[-258/*]soft"));
        Assert.assertEquals(scoreDefinitionHSS.createScore((-147), Integer.MIN_VALUE, (-369)), scoreDefinitionHSS.parseScore("[-147]hard/[*/-369]soft"));
    }

    @Test
    public void toShortString() {
        Assert.assertEquals("0", scoreDefinitionHSS.createScore(0, 0, 0).toShortString());
        Assert.assertEquals("[0/-369]soft", scoreDefinitionHSS.createScore(0, 0, (-369)).toShortString());
        Assert.assertEquals("[-258/-369]soft", scoreDefinitionHSS.createScore(0, (-258), (-369)).toShortString());
        Assert.assertEquals("[-147]hard", scoreDefinitionHSS.createScore((-147), 0, 0).toShortString());
        Assert.assertEquals("[-147]hard/[-258/-369]soft", scoreDefinitionHSS.createScore((-147), (-258), (-369)).toShortString());
        Assert.assertEquals("[-147/-258/-369]hard", scoreDefinitionHHH.createScore((-147), (-258), (-369)).toShortString());
        Assert.assertEquals("[-147/-258/-369]soft", scoreDefinitionSSS.createScore((-147), (-258), (-369)).toShortString());
        Assert.assertEquals("-7init/[-147/-258/-369]soft", scoreDefinitionSSS.createScoreUninitialized((-7), (-147), (-258), (-369)).toShortString());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("[0]hard/[-258/-369]soft", scoreDefinitionHSS.createScore(0, (-258), (-369)).toString());
        Assert.assertEquals("[-147]hard/[-258/-369]soft", scoreDefinitionHSS.createScore((-147), (-258), (-369)).toString());
        Assert.assertEquals("[-147/-258/-369]hard/[]soft", scoreDefinitionHHH.createScore((-147), (-258), (-369)).toString());
        Assert.assertEquals("[]hard/[-147/-258/-369]soft", scoreDefinitionSSS.createScore((-147), (-258), (-369)).toString());
        Assert.assertEquals("-7init/[]hard/[-147/-258/-369]soft", scoreDefinitionSSS.createScoreUninitialized((-7), (-147), (-258), (-369)).toString());
        Assert.assertEquals("[]hard/[]soft", new BendableScoreDefinition(0, 0).createScore().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        scoreDefinitionHSS.parseScore("-147");
    }

    @Test
    public void getHardOrSoftScore() {
        BendableScore initializedScore = scoreDefinitionHSS.createScore((-5), (-10), (-200));
        Assert.assertEquals((-5), initializedScore.getHardOrSoftScore(0));
        Assert.assertEquals((-10), initializedScore.getHardOrSoftScore(1));
        Assert.assertEquals((-200), initializedScore.getHardOrSoftScore(2));
    }

    @Test
    public void toInitializedScoreHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore((-147), (-258), (-369)), scoreDefinitionHSS.createScore((-147), (-258), (-369)).toInitializedScore());
        Assert.assertEquals(scoreDefinitionHSS.createScore((-147), (-258), (-369)), scoreDefinitionHSS.createScoreUninitialized((-7), (-147), (-258), (-369)).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-7), (-147), (-258), (-369)), scoreDefinitionHSS.createScore((-147), (-258), (-369)).withInitScore((-7)));
    }

    @Test
    public void feasibleHSS() {
        AbstractScoreTest.assertScoreNotFeasible(scoreDefinitionHSS.createScore((-20), (-300), (-4000)), scoreDefinitionHSS.createScoreUninitialized((-1), 20, (-300), (-4000)), scoreDefinitionHSS.createScoreUninitialized((-1), 0, (-300), (-4000)), scoreDefinitionHSS.createScoreUninitialized((-1), (-20), (-300), (-4000)));
        AbstractScoreTest.assertScoreFeasible(scoreDefinitionHSS.createScore(0, (-300), (-4000)), scoreDefinitionHSS.createScore(20, (-300), (-4000)), scoreDefinitionHSS.createScoreUninitialized(0, 0, (-300), (-4000)));
    }

    @Test
    public void addHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(19, (-320), 0), scoreDefinitionHSS.createScore(20, (-20), (-4000)).add(scoreDefinitionHSS.createScore((-1), (-300), 4000)));
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-77), 19, (-320), 0), scoreDefinitionHSS.createScoreUninitialized((-70), 20, (-20), (-4000)).add(scoreDefinitionHSS.createScoreUninitialized((-7), (-1), (-300), 4000)));
    }

    @Test
    public void subtractHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(21, 280, (-8000)), scoreDefinitionHSS.createScore(20, (-20), (-4000)).subtract(scoreDefinitionHSS.createScore((-1), (-300), 4000)));
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-63), 21, 280, (-8000)), scoreDefinitionHSS.createScoreUninitialized((-70), 20, (-20), (-4000)).subtract(scoreDefinitionHSS.createScoreUninitialized((-7), (-1), (-300), 4000)));
    }

    @Test
    public void multiplyHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(6, (-6), 6), scoreDefinitionHSS.createScore(5, (-5), 5).multiply(1.2));
        Assert.assertEquals(scoreDefinitionHSS.createScore(1, (-2), 1), scoreDefinitionHSS.createScore(1, (-1), 1).multiply(1.2));
        Assert.assertEquals(scoreDefinitionHSS.createScore(4, (-5), 4), scoreDefinitionHSS.createScore(4, (-4), 4).multiply(1.2));
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-14), 8, (-10), 12), scoreDefinitionHSS.createScoreUninitialized((-7), 4, (-5), 6).multiply(2.0));
    }

    @Test
    public void divideHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(5, (-5), 5), scoreDefinitionHSS.createScore(25, (-25), 25).divide(5.0));
        Assert.assertEquals(scoreDefinitionHSS.createScore(4, (-5), 4), scoreDefinitionHSS.createScore(21, (-21), 21).divide(5.0));
        Assert.assertEquals(scoreDefinitionHSS.createScore(4, (-5), 4), scoreDefinitionHSS.createScore(24, (-24), 24).divide(5.0));
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-7), 4, (-5), 6), scoreDefinitionHSS.createScoreUninitialized((-14), 8, (-10), 12).divide(2.0));
    }

    @Test
    public void powerHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(9, 16, 25), scoreDefinitionHSS.createScore(3, (-4), 5).power(2.0));
        Assert.assertEquals(scoreDefinitionHSS.createScore(3, 4, 5), scoreDefinitionHSS.createScore(9, 16, 25).power(0.5));
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-343), 27, (-64), 125), scoreDefinitionHSS.createScoreUninitialized((-7), 3, (-4), 5).power(3.0));
    }

    @Test
    public void negateHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore((-3), 4, (-5)), scoreDefinitionHSS.createScore(3, (-4), 5).negate());
        Assert.assertEquals(scoreDefinitionHSS.createScore(3, (-4), 5), scoreDefinitionHSS.createScore((-3), 4, (-5)).negate());
    }

    @Test
    public void equalsAndHashCodeHSS() {
        PlannerAssert.assertObjectsAreEqual(scoreDefinitionHSS.createScore((-10), (-200), (-3000)), scoreDefinitionHSS.createScore((-10), (-200), (-3000)), scoreDefinitionHSS.createScoreUninitialized(0, (-10), (-200), (-3000)));
        PlannerAssert.assertObjectsAreEqual(scoreDefinitionHSS.createScoreUninitialized((-7), (-10), (-200), (-3000)), scoreDefinitionHSS.createScoreUninitialized((-7), (-10), (-200), (-3000)));
        PlannerAssert.assertObjectsAreNotEqual(scoreDefinitionHSS.createScore((-10), (-200), (-3000)), scoreDefinitionHSS.createScore((-30), (-200), (-3000)), scoreDefinitionHSS.createScore((-10), (-400), (-3000)), scoreDefinitionHSS.createScore((-10), (-400), (-5000)), scoreDefinitionHSS.createScoreUninitialized((-7), (-10), (-200), (-3000)));
    }

    @Test
    public void compareToHSS() {
        PlannerAssert.assertCompareToOrder(scoreDefinitionHSS.createScoreUninitialized((-8), 0, 0, 0), scoreDefinitionHSS.createScoreUninitialized((-7), (-20), (-20), (-20)), scoreDefinitionHSS.createScoreUninitialized((-7), (-1), (-300), (-4000)), scoreDefinitionHSS.createScoreUninitialized((-7), 0, 0, 0), scoreDefinitionHSS.createScoreUninitialized((-7), 0, 0, 1), scoreDefinitionHSS.createScoreUninitialized((-7), 0, 1, 0), scoreDefinitionHSS.createScore((-20), Integer.MIN_VALUE, Integer.MIN_VALUE), scoreDefinitionHSS.createScore((-20), Integer.MIN_VALUE, (-20)), scoreDefinitionHSS.createScore((-20), Integer.MIN_VALUE, 1), scoreDefinitionHSS.createScore((-20), (-300), (-4000)), scoreDefinitionHSS.createScore((-20), (-300), (-300)), scoreDefinitionHSS.createScore((-20), (-300), (-20)), scoreDefinitionHSS.createScore((-20), (-300), 300), scoreDefinitionHSS.createScore((-20), (-20), (-300)), scoreDefinitionHSS.createScore((-20), (-20), 0), scoreDefinitionHSS.createScore((-20), (-20), 1), scoreDefinitionHSS.createScore((-1), (-300), (-4000)), scoreDefinitionHSS.createScore((-1), (-300), (-20)), scoreDefinitionHSS.createScore((-1), (-20), (-300)), scoreDefinitionHSS.createScore(1, Integer.MIN_VALUE, (-20)), scoreDefinitionHSS.createScore(1, (-20), Integer.MIN_VALUE));
    }

    private BendableScoreDefinition scoreDefinitionHHSSS = new BendableScoreDefinition(2, 3);

    @Test
    public void feasibleHHSSS() {
        AbstractScoreTest.assertScoreNotFeasible(scoreDefinitionHHSSS.createScore((-1), (-20), (-300), (-4000), (-5000)), scoreDefinitionHHSSS.createScore((-1), 0, (-300), (-4000), (-5000)), scoreDefinitionHHSSS.createScore((-1), 20, (-300), (-4000), (-5000)), scoreDefinitionHHSSS.createScore(0, (-20), (-300), (-4000), (-5000)), scoreDefinitionHHSSS.createScore(1, (-20), (-300), (-4000), (-5000)));
        AbstractScoreTest.assertScoreFeasible(scoreDefinitionHHSSS.createScore(0, 0, (-300), (-4000), (-5000)), scoreDefinitionHHSSS.createScore(0, 20, (-300), (-4000), (-5000)), scoreDefinitionHHSSS.createScore(1, 0, (-300), (-4000), (-5000)), scoreDefinitionHHSSS.createScore(1, 20, (-300), (-4000), (-5000)));
    }

    @Test
    public void addHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(19, (-320), 0, 0, 0), scoreDefinitionHHSSS.createScore(20, (-20), (-4000), 0, 0).add(scoreDefinitionHHSSS.createScore((-1), (-300), 4000, 0, 0)));
    }

    @Test
    public void subtractHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(21, 280, (-8000), 0, 0), scoreDefinitionHHSSS.createScore(20, (-20), (-4000), 0, 0).subtract(scoreDefinitionHHSSS.createScore((-1), (-300), 4000, 0, 0)));
    }

    @Test
    public void multiplyHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(6, (-6), 6, 0, 0), scoreDefinitionHHSSS.createScore(5, (-5), 5, 0, 0).multiply(1.2));
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(1, (-2), 1, 0, 0), scoreDefinitionHHSSS.createScore(1, (-1), 1, 0, 0).multiply(1.2));
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(4, (-5), 4, 0, 0), scoreDefinitionHHSSS.createScore(4, (-4), 4, 0, 0).multiply(1.2));
    }

    @Test
    public void divideHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(5, (-5), 5, 0, 0), scoreDefinitionHHSSS.createScore(25, (-25), 25, 0, 0).divide(5.0));
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(4, (-5), 4, 0, 0), scoreDefinitionHHSSS.createScore(21, (-21), 21, 0, 0).divide(5.0));
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(4, (-5), 4, 0, 0), scoreDefinitionHHSSS.createScore(24, (-24), 24, 0, 0).divide(5.0));
    }

    @Test
    public void powerHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(9, 16, 25, 0, 0), scoreDefinitionHHSSS.createScore(3, (-4), 5, 0, 0).power(2.0));
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(3, 4, 5, 0, 0), scoreDefinitionHHSSS.createScore(9, 16, 25, 0, 0).power(0.5));
    }

    @Test
    public void negateHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore((-3), 4, (-5), 0, 0), scoreDefinitionHHSSS.createScore(3, (-4), 5, 0, 0).negate());
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(3, (-4), 5, 0, 0), scoreDefinitionHHSSS.createScore((-3), 4, (-5), 0, 0).negate());
    }

    @Test
    public void equalsAndHashCodeHHSSS() {
        PlannerAssert.assertObjectsAreEqual(scoreDefinitionHHSSS.createScore((-10), (-20), (-30), 0, 0), scoreDefinitionHHSSS.createScore((-10), (-20), (-30), 0, 0));
    }

    @Test
    public void compareToHHSSS() {
        PlannerAssert.assertCompareToOrder(scoreDefinitionHHSSS.createScore((-20), Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0), scoreDefinitionHHSSS.createScore((-20), Integer.MIN_VALUE, (-20), 0, 0), scoreDefinitionHHSSS.createScore((-20), Integer.MIN_VALUE, 1, 0, 0), scoreDefinitionHHSSS.createScore((-20), (-300), (-4000), 0, 0), scoreDefinitionHHSSS.createScore((-20), (-300), (-300), 0, 0), scoreDefinitionHHSSS.createScore((-20), (-300), (-20), 0, 0), scoreDefinitionHHSSS.createScore((-20), (-300), 300, 0, 0), scoreDefinitionHHSSS.createScore((-20), (-20), (-300), 0, 0), scoreDefinitionHHSSS.createScore((-20), (-20), 0, 0, 0), scoreDefinitionHHSSS.createScore((-20), (-20), 1, 0, 0), scoreDefinitionHHSSS.createScore((-1), (-300), (-4000), 0, 0), scoreDefinitionHHSSS.createScore((-1), (-300), (-20), 0, 0), scoreDefinitionHHSSS.createScore((-1), (-20), (-300), 0, 0), scoreDefinitionHHSSS.createScore(1, Integer.MIN_VALUE, (-20), 0, 0), scoreDefinitionHHSSS.createScore(1, (-20), Integer.MIN_VALUE, 0, 0));
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(scoreDefinitionHSS.createScore((-12), 3400, (-56)), ( output) -> {
            assertEquals(0, output.getInitScore());
            assertEquals((-12), output.getHardScore(0));
            assertEquals(3400, output.getSoftScore(0));
            assertEquals((-56), output.getSoftScore(1));
        });
        PlannerTestUtils.serializeAndDeserializeWithAll(scoreDefinitionHSS.createScoreUninitialized((-7), (-12), 3400, (-56)), ( output) -> {
            assertEquals((-7), output.getInitScore());
            assertEquals((-12), output.getHardScore(0));
            assertEquals(3400, output.getSoftScore(0));
            assertEquals((-56), output.getSoftScore(1));
        });
    }
}

