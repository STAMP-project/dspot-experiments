/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.classifier;


import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.Matrix;
import org.junit.Test;


public final class ConfusionMatrixTest extends MahoutTestCase {
    private static final int[][] VALUES = new int[][]{ new int[]{ 2, 3 }, new int[]{ 10, 20 } };

    private static final String[] LABELS = new String[]{ "Label1", "Label2" };

    private static final int[] OTHER = new int[]{ 3, 6 };

    private static final String DEFAULT_LABEL = "other";

    @Test
    public void testBuild() {
        ConfusionMatrix confusionMatrix = ConfusionMatrixTest.fillConfusionMatrix(ConfusionMatrixTest.VALUES, ConfusionMatrixTest.LABELS, ConfusionMatrixTest.DEFAULT_LABEL);
        ConfusionMatrixTest.checkValues(confusionMatrix);
        ConfusionMatrixTest.checkAccuracy(confusionMatrix);
    }

    @Test
    public void testGetMatrix() {
        ConfusionMatrix confusionMatrix = ConfusionMatrixTest.fillConfusionMatrix(ConfusionMatrixTest.VALUES, ConfusionMatrixTest.LABELS, ConfusionMatrixTest.DEFAULT_LABEL);
        Matrix m = confusionMatrix.getMatrix();
        Map<String, Integer> rowLabels = m.getRowLabelBindings();
        assertEquals(confusionMatrix.getLabels().size(), m.numCols());
        assertTrue(rowLabels.keySet().contains(ConfusionMatrixTest.LABELS[0]));
        assertTrue(rowLabels.keySet().contains(ConfusionMatrixTest.LABELS[1]));
        assertTrue(rowLabels.keySet().contains(ConfusionMatrixTest.DEFAULT_LABEL));
        assertEquals(2, confusionMatrix.getCorrect(ConfusionMatrixTest.LABELS[0]));
        assertEquals(20, confusionMatrix.getCorrect(ConfusionMatrixTest.LABELS[1]));
        assertEquals(0, confusionMatrix.getCorrect(ConfusionMatrixTest.DEFAULT_LABEL));
    }

    /**
     * Example taken from
     * http://scikit-learn.org/stable/modules/generated/sklearn.metrics.precision_recall_fscore_support.html
     */
    @Test
    public void testPrecisionRecallAndF1ScoreAsScikitLearn() {
        Collection<String> labelList = Arrays.asList("0", "1", "2");
        ConfusionMatrix confusionMatrix = new ConfusionMatrix(labelList, "DEFAULT");
        confusionMatrix.putCount("0", "0", 2);
        confusionMatrix.putCount("1", "0", 1);
        confusionMatrix.putCount("1", "2", 1);
        confusionMatrix.putCount("2", "1", 2);
        double delta = 0.001;
        assertEquals(0.222, confusionMatrix.getWeightedPrecision(), delta);
        assertEquals(0.333, confusionMatrix.getWeightedRecall(), delta);
        assertEquals(0.266, confusionMatrix.getWeightedF1score(), delta);
    }
}

