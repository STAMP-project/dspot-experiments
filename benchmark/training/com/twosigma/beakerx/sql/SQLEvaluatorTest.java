/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.sql;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.sql.evaluator.SQLEvaluator;
import org.junit.Test;


public class SQLEvaluatorTest {
    private SQLEvaluator sqlEvaluator;

    private KernelTest kernelTest;

    @Test
    public void evaluateSql() throws Exception {
        // given
        SimpleEvaluationObject seo = new SimpleEvaluationObject(SQLForColorTable.CREATE_AND_SELECT_ALL);
        seo.setJupyterMessage(MessageFactorTest.commMsg());
        // when
        TryResult evaluate = sqlEvaluator.evaluate(seo, seo.getExpression());
        // then
        verifyResult(evaluate);
    }

    @Test
    public void insertsShouldReturnOutputCellHIDDEN() throws Exception {
        // given
        SimpleEvaluationObject seo = new SimpleEvaluationObject(SQLForColorTable.CREATE);
        // when
        TryResult evaluate = sqlEvaluator.evaluate(seo, seo.getExpression());
        // then
        verifyInsertResult(evaluate);
    }
}

