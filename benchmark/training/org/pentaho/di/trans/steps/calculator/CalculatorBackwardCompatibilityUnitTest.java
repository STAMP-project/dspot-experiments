/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.calculator;


import java.math.BigDecimal;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Unit tests for calculator step
 *
 * @author Pavel Sakun
 * @see Calculator
 */
public class CalculatorBackwardCompatibilityUnitTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private StepMockHelper<CalculatorMeta, CalculatorData> smh;

    private static final String SYS_PROPERTY_ROUND_2_MODE = "ROUND_2_MODE";

    private static final int OBSOLETE_ROUND_2_MODE = BigDecimal.ROUND_HALF_EVEN;

    private static final int DEFAULT_ROUND_2_MODE = Const.ROUND_HALF_CEILING;

    @Test
    public void testRound() throws KettleException {
        assertRound(1.0, 1.2);
        assertRound(2.0, 1.5);
        assertRound(2.0, 1.7);
        assertRound(2.0, 2.2);
        assertRound(3.0, 2.5);
        assertRound(3.0, 2.7);
        assertRound((-1.0), (-1.2));
        assertRound((-1.0), (-1.5));
        assertRound((-2.0), (-1.7));
        assertRound((-2.0), (-2.2));
        assertRound((-2.0), (-2.5));
        assertRound((-3.0), (-2.7));
    }

    @Test
    public void testRound2() throws KettleException {
        assertRound2(1.0, 1.2, 0);
        assertRound2(2.0, 1.5, 0);
        assertRound2(2.0, 1.7, 0);
        assertRound2(2.0, 2.2, 0);
        assertRound2(2.0, 2.5, 0);
        assertRound2(3.0, 2.7, 0);
        assertRound2((-1.0), (-1.2), 0);
        assertRound2((-2.0), (-1.5), 0);
        assertRound2((-2.0), (-1.7), 0);
        assertRound2((-2.0), (-2.2), 0);
        assertRound2((-2.0), (-2.5), 0);
        assertRound2((-3.0), (-2.7), 0);
    }

    @Test
    public void testRoundStd() throws KettleException {
        assertRoundStd(1.0, 1.2);
        assertRoundStd(2.0, 1.5);
        assertRoundStd(2.0, 1.7);
        assertRoundStd(2.0, 2.2);
        assertRoundStd(3.0, 2.5);
        assertRoundStd(3.0, 2.7);
        assertRoundStd((-1.0), (-1.2));
        assertRoundStd((-2.0), (-1.5));
        assertRoundStd((-2.0), (-1.7));
        assertRoundStd((-2.0), (-2.2));
        assertRoundStd((-3.0), (-2.5));
        assertRoundStd((-3.0), (-2.7));
    }

    @Test
    public void testRoundStd2() throws KettleException {
        assertRoundStd2(1.0, 1.2, 0);
        assertRoundStd2(2.0, 1.5, 0);
        assertRoundStd2(2.0, 1.7, 0);
        assertRoundStd2(2.0, 2.2, 0);
        assertRoundStd2(3.0, 2.5, 0);
        assertRoundStd2(3.0, 2.7, 0);
        assertRoundStd2((-1.0), (-1.2), 0);
        assertRoundStd2((-2.0), (-1.5), 0);
        assertRoundStd2((-2.0), (-1.7), 0);
        assertRoundStd2((-2.0), (-2.2), 0);
        assertRoundStd2((-3.0), (-2.5), 0);
        assertRoundStd2((-3.0), (-2.7), 0);
    }
}

