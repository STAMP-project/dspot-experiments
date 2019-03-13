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
package org.deeplearning4j.text.tokenization.tokenizer;


import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.text.tokenization.tokenizerFactory.ChineseTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author wangfeng
 * @unknown June 3,2017
 * @unknown 
 */
@Slf4j
public class ChineseTokenizerTest {
    private final String toTokenize = "???????????????????";

    private final String[] expect = new String[]{ "????", "?", "??", "?", "???", "?", "??", "?", "??", "??" };

    @Test
    public void testChineseTokenizer() {
        TokenizerFactory tokenizerFactory = new ChineseTokenizerFactory();
        Tokenizer tokenizer = tokenizerFactory.create(toTokenize);
        Assert.assertEquals(expect.length, tokenizer.countTokens());
        for (int i = 0; i < (tokenizer.countTokens()); ++i) {
            Assert.assertEquals(tokenizer.nextToken(), expect[i]);
        }
    }
}

