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
package org.optaplanner.core.api.score.buildin.bendablebigdecimal;


import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.score.buildin.bendablebigdecimal.BendableBigDecimalScoreDefinition;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;


public class BendableBigDecimalScoreTest extends AbstractScoreTest {
    private static final BigDecimal PLUS_4000 = BigDecimal.valueOf(4000);

    private static final BigDecimal PLUS_300 = BigDecimal.valueOf(300);

    private static final BigDecimal PLUS_280 = BigDecimal.valueOf(280);

    private static final BigDecimal PLUS_25 = BigDecimal.valueOf(25);

    private static final BigDecimal PLUS_24 = BigDecimal.valueOf(24);

    private static final BigDecimal PLUS_21 = BigDecimal.valueOf(21);

    private static final BigDecimal PLUS_20 = BigDecimal.valueOf(20);

    private static final BigDecimal PLUS_19 = BigDecimal.valueOf(19);

    private static final BigDecimal PLUS_16 = BigDecimal.valueOf(16);

    private static final BigDecimal NINE = BigDecimal.valueOf(9);

    private static final BigDecimal SIX = BigDecimal.valueOf(6);

    private static final BigDecimal FIVE = BigDecimal.valueOf(5);

    private static final BigDecimal FOUR = BigDecimal.valueOf(4);

    private static final BigDecimal THREE = BigDecimal.valueOf(3);

    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    private static final BigDecimal ONE = BigDecimal.ONE;

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final BigDecimal MINUS_ONE = BendableBigDecimalScoreTest.ONE.negate();

    private static final BigDecimal MINUS_THREE = BendableBigDecimalScoreTest.THREE.negate();

    private static final BigDecimal MINUS_FOUR = BendableBigDecimalScoreTest.FOUR.negate();

    private static final BigDecimal MINUS_FIVE = BendableBigDecimalScoreTest.FIVE.negate();

    private static final BigDecimal MINUS_TEN = BigDecimal.TEN.negate();

    private static final BigDecimal MINUS_20 = BendableBigDecimalScoreTest.PLUS_20.negate();

    private static final BigDecimal MINUS_21 = BendableBigDecimalScoreTest.PLUS_21.negate();

    private static final BigDecimal MINUS_24 = BendableBigDecimalScoreTest.PLUS_24.negate();

    private static final BigDecimal MINUS_25 = BendableBigDecimalScoreTest.PLUS_25.negate();

    private static final BigDecimal MINUS_30 = BigDecimal.valueOf((-30));

    private static final BigDecimal MINUS_300 = BendableBigDecimalScoreTest.PLUS_300.negate();

    private static final BigDecimal MINUS_320 = BigDecimal.valueOf((-320));

    private static final BigDecimal MINUS_4000 = BendableBigDecimalScoreTest.PLUS_4000.negate();

    private static final BigDecimal MINUS_5000 = BigDecimal.valueOf((-5000));

    private static final BigDecimal MINUS_8000 = BigDecimal.valueOf((-8000));

    private static final BigDecimal MIN_INTEGER = BigDecimal.valueOf(Integer.MIN_VALUE);

    private BendableBigDecimalScoreDefinition scoreDefinitionHSS = new BendableBigDecimalScoreDefinition(1, 2);

    @Test
    public void of() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(BigDecimal.valueOf((-147)), BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), BendableBigDecimalScore.ofHard(1, 2, 0, BigDecimal.valueOf((-147))));
        Assert.assertEquals(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.ZERO, BigDecimal.valueOf((-258)), BendableBigDecimalScoreTest.ZERO), BendableBigDecimalScore.ofSoft(1, 2, 0, BigDecimal.valueOf((-258))));
        Assert.assertEquals(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO, BigDecimal.valueOf((-369))), BendableBigDecimalScore.ofSoft(1, 2, 1, BigDecimal.valueOf((-369))));
    }

    @Test
    public void parseScore() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))), scoreDefinitionHSS.parseScore("[-147]hard/[-258/-369]soft"));
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-7), BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))), scoreDefinitionHSS.parseScore("-7init/[-147]hard/[-258/-369]soft"));
    }

    @Test
    public void toShortString() {
        Assert.assertEquals("[0/-369]soft", scoreDefinitionHSS.createScore(BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf((-369))).toShortString());
        Assert.assertEquals("[-258/-369]soft", scoreDefinitionHSS.createScore(BigDecimal.valueOf(0), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).toShortString());
        Assert.assertEquals("[-147]hard", scoreDefinitionHSS.createScore(BigDecimal.valueOf((-147)), BigDecimal.valueOf(0), BigDecimal.valueOf(0)).toShortString());
        Assert.assertEquals("[-147]hard/[-258/-369]soft", scoreDefinitionHSS.createScore(BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).toShortString());
        Assert.assertEquals("-7init/[-147]hard/[-258/-369]soft", scoreDefinitionHSS.createScoreUninitialized((-7), BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).toShortString());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("[0]hard/[-258/-369]soft", scoreDefinitionHSS.createScore(BigDecimal.valueOf(0), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).toString());
        Assert.assertEquals("[-147]hard/[-258/-369]soft", scoreDefinitionHSS.createScore(BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).toString());
        Assert.assertEquals("[-147/-258]hard/[-369]soft", new BendableBigDecimalScoreDefinition(2, 1).createScore(BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).toString());
        Assert.assertEquals("-7init/[-147]hard/[-258/-369]soft", scoreDefinitionHSS.createScoreUninitialized((-7), BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).toString());
        Assert.assertEquals("[]hard/[]soft", new BendableBigDecimalScoreDefinition(0, 0).createScore().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        scoreDefinitionHSS.parseScore("-147");
    }

    @Test
    public void getHardOrSoftScore() {
        BendableBigDecimalScore initializedScore = scoreDefinitionHSS.createScore(BigDecimal.valueOf((-5)), BigDecimal.valueOf((-10)), BigDecimal.valueOf((-200)));
        Assert.assertEquals(BigDecimal.valueOf((-5)), initializedScore.getHardOrSoftScore(0));
        Assert.assertEquals(BigDecimal.valueOf((-10)), initializedScore.getHardOrSoftScore(1));
        Assert.assertEquals(BigDecimal.valueOf((-200)), initializedScore.getHardOrSoftScore(2));
    }

    @Test
    public void toInitializedScoreHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))), scoreDefinitionHSS.createScore(BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).toInitializedScore());
        Assert.assertEquals(scoreDefinitionHSS.createScore(BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))), scoreDefinitionHSS.createScoreUninitialized((-7), BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-7), BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))), scoreDefinitionHSS.createScore(BigDecimal.valueOf((-147)), BigDecimal.valueOf((-258)), BigDecimal.valueOf((-369))).withInitScore((-7)));
    }

    @Test
    public void feasibleHSS() {
        AbstractScoreTest.assertScoreNotFeasible(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000), scoreDefinitionHSS.createScoreUninitialized((-7), BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000), scoreDefinitionHSS.createScoreUninitialized((-7), BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000));
        AbstractScoreTest.assertScoreFeasible(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.TWO, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000), scoreDefinitionHSS.createScoreUninitialized(0, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000));
    }

    @Test
    public void addHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.PLUS_19, BendableBigDecimalScoreTest.MINUS_320, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.PLUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_4000).add(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.PLUS_4000)));
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-77), BendableBigDecimalScoreTest.PLUS_19, BendableBigDecimalScoreTest.MINUS_320, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHSS.createScoreUninitialized((-70), BendableBigDecimalScoreTest.PLUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_4000).add(scoreDefinitionHSS.createScoreUninitialized((-7), BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.PLUS_4000)));
    }

    @Test
    public void subtractHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.PLUS_21, BendableBigDecimalScoreTest.PLUS_280, BendableBigDecimalScoreTest.MINUS_8000), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.PLUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_4000).subtract(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.PLUS_4000)));
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-63), BendableBigDecimalScoreTest.PLUS_21, BendableBigDecimalScoreTest.PLUS_280, BendableBigDecimalScoreTest.MINUS_8000), scoreDefinitionHSS.createScoreUninitialized((-70), BendableBigDecimalScoreTest.PLUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_4000).subtract(scoreDefinitionHSS.createScoreUninitialized((-7), BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.PLUS_4000)));
    }

    @Test
    public void multiplyHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-14), new BigDecimal("8.6"), new BigDecimal("-10.4"), new BigDecimal("-12.2")), scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("4.3"), new BigDecimal("-5.2"), new BigDecimal("-6.1")).multiply(2.0));
    }

    @Test
    public void divideHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.FIVE, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.FIVE), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.PLUS_25, BendableBigDecimalScoreTest.MINUS_25, BendableBigDecimalScoreTest.PLUS_25).divide(5.0));
        Assert.assertEquals(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.FOUR), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.PLUS_21, BendableBigDecimalScoreTest.MINUS_21, BendableBigDecimalScoreTest.PLUS_21).divide(5.0));
        Assert.assertEquals(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.FOUR), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.PLUS_24, BendableBigDecimalScoreTest.MINUS_24, BendableBigDecimalScoreTest.PLUS_24).divide(5.0));
        Assert.assertEquals(scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("4.3"), new BigDecimal("-5.2"), new BigDecimal("-6.1")), scoreDefinitionHSS.createScoreUninitialized((-14), new BigDecimal("8.6"), new BigDecimal("-10.4"), new BigDecimal("-12.2")).divide(2.0));
    }

    @Test
    public void negateHSS() {
        Assert.assertEquals(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_THREE, BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.MINUS_FIVE), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.THREE, BendableBigDecimalScoreTest.MINUS_FOUR, BendableBigDecimalScoreTest.FIVE).negate());
        Assert.assertEquals(scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.THREE, BendableBigDecimalScoreTest.MINUS_FOUR, BendableBigDecimalScoreTest.FIVE), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_THREE, BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.MINUS_FIVE).negate());
    }

    @Test
    public void equalsAndHashCodeHSS() {
        PlannerAssert.assertObjectsAreEqual(scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")), scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")), scoreDefinitionHSS.createScore(new BigDecimal("-10.000"), new BigDecimal("-200.000"), new BigDecimal("-3000.000")), scoreDefinitionHSS.createScoreUninitialized(0, new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")));
        PlannerAssert.assertObjectsAreEqual(scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")), scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")));
        PlannerAssert.assertObjectsAreNotEqual(scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")), scoreDefinitionHSS.createScore(new BigDecimal("-30"), new BigDecimal("-200"), new BigDecimal("-3000")), scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-400"), new BigDecimal("-3000")), scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-400"), new BigDecimal("-5000")), scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")));
    }

    @Test
    public void compareToHSS() {
        PlannerAssert.assertCompareToOrder(scoreDefinitionHSS.createScoreUninitialized((-8), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")), scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("-20"), new BigDecimal("-20"), new BigDecimal("-20")), scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("-1"), new BigDecimal("-300"), new BigDecimal("-4000")), scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")), scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("1")), scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("0")), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.MIN_INTEGER), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.MINUS_20), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.ONE), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_300), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_20), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.PLUS_300), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.ONE), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_20), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.ONE, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.MINUS_20), scoreDefinitionHSS.createScore(BendableBigDecimalScoreTest.ONE, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MIN_INTEGER));
    }

    private BendableBigDecimalScoreDefinition scoreDefinitionHHSSS = new BendableBigDecimalScoreDefinition(2, 3);

    @Test
    public void feasibleHHSSS() {
        AbstractScoreTest.assertScoreNotFeasible(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.MINUS_5000), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.MINUS_5000), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.ONE, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.MINUS_5000));
        AbstractScoreTest.assertScoreFeasible(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.MINUS_5000), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.TWO, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.MINUS_5000), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.TWO, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.MINUS_5000), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.ONE, BendableBigDecimalScoreTest.TWO, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.MINUS_5000));
    }

    @Test
    public void addHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.PLUS_19, BendableBigDecimalScoreTest.MINUS_320, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.PLUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO).add(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.PLUS_4000, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO)));
    }

    @Test
    public void subtractHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.PLUS_21, BendableBigDecimalScoreTest.PLUS_280, BendableBigDecimalScoreTest.MINUS_8000, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.PLUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO).subtract(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.PLUS_4000, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO)));
    }

    @Test
    public void divideHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.FIVE, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.FIVE, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.PLUS_25, BendableBigDecimalScoreTest.MINUS_25, BendableBigDecimalScoreTest.PLUS_25, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO).divide(5.0));
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.PLUS_21, BendableBigDecimalScoreTest.MINUS_21, BendableBigDecimalScoreTest.PLUS_21, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO).divide(5.0));
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.PLUS_24, BendableBigDecimalScoreTest.MINUS_24, BendableBigDecimalScoreTest.PLUS_24, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO).divide(5.0));
    }

    @Test
    public void negateHHSSS() {
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_THREE, BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.THREE, BendableBigDecimalScoreTest.MINUS_FOUR, BendableBigDecimalScoreTest.FIVE, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO).negate());
        Assert.assertEquals(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.THREE, BendableBigDecimalScoreTest.MINUS_FOUR, BendableBigDecimalScoreTest.FIVE, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_THREE, BendableBigDecimalScoreTest.FOUR, BendableBigDecimalScoreTest.MINUS_FIVE, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO).negate());
    }

    @Test
    public void equalsAndHashCodeHHSSS() {
        PlannerAssert.assertObjectsAreEqual(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_TEN, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_30, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_TEN, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_30, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO));
    }

    @Test
    public void compareToHHSSS() {
        PlannerAssert.assertCompareToOrder(scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.ONE, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.PLUS_300, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.ONE, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_4000, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.MINUS_ONE, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MINUS_300, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.ONE, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO), scoreDefinitionHHSSS.createScore(BendableBigDecimalScoreTest.ONE, BendableBigDecimalScoreTest.MINUS_20, BendableBigDecimalScoreTest.MIN_INTEGER, BendableBigDecimalScoreTest.ZERO, BendableBigDecimalScoreTest.ZERO));
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(scoreDefinitionHSS.createScore(new BigDecimal("-12"), new BigDecimal("3400"), new BigDecimal("-56")), ( output) -> {
            assertEquals(0, output.getInitScore());
            assertEquals(new BigDecimal("-12"), output.getHardScore(0));
            assertEquals(new BigDecimal("3400"), output.getSoftScore(0));
            assertEquals(new BigDecimal("-56"), output.getSoftScore(1));
        });
        PlannerTestUtils.serializeAndDeserializeWithAll(scoreDefinitionHSS.createScoreUninitialized((-7), new BigDecimal("-12"), new BigDecimal("3400"), new BigDecimal("-56")), ( output) -> {
            assertEquals((-7), output.getInitScore());
            assertEquals(new BigDecimal("-12"), output.getHardScore(0));
            assertEquals(new BigDecimal("3400"), output.getSoftScore(0));
            assertEquals(new BigDecimal("-56"), output.getSoftScore(1));
        });
    }
}

