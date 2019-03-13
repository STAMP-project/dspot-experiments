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
package org.deeplearning4j.text.tokenization.tokenizer.preprocessor;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class StemmingPreprocessorTest {
    @Test
    public void testPreProcess() throws Exception {
        StemmingPreprocessor preprocessor = new StemmingPreprocessor();
        String test = "TESTING.";
        String output = preprocessor.preProcess(test);
        System.out.println(("Output: " + output));
        Assert.assertEquals("test", output);
    }
}

