/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.util;


import java.util.List;
import org.deeplearning4j.text.movingwindow.ContextLabelRetriever;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.collection.MultiDimensionalMap;
import org.nd4j.linalg.primitives.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Basic test case for the context label test
 */
public class ContextLabelTest {
    private static final Logger log = LoggerFactory.getLogger(ContextLabelTest.class);

    private TokenizerFactory tokenizerFactory;

    @Test
    public void testBasicLabel() {
        String labeledSentence = "<NEGATIVE> This sucks really bad </NEGATIVE> .";
        Pair<String, MultiDimensionalMap<Integer, Integer, String>> ret = ContextLabelRetriever.stringWithLabels(labeledSentence, tokenizerFactory);
        // positive and none
        Assert.assertEquals(2, ret.getSecond().size());
        List<String> vals = new java.util.ArrayList(ret.getSecond().values());
        Assert.assertEquals(true, vals.contains("NEGATIVE"));
        Assert.assertEquals(true, vals.contains("none"));
        Assert.assertEquals("This sucks really bad .", ret.getFirst());
    }
}

