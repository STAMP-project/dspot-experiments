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


import java.util.List;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author sonali
 */
public class NGramTokenizerTest {
    @Test
    public void testNGramTokenizer() throws Exception {
        String toTokenize = "Mary had a little lamb.";
        TokenizerFactory factory = new org.deeplearning4j.text.tokenization.tokenizerfactory.NGramTokenizerFactory(new DefaultTokenizerFactory(), 1, 2);
        Tokenizer tokenizer = factory.create(toTokenize);
        Tokenizer tokenizer2 = factory.create(toTokenize);
        while (tokenizer.hasMoreTokens()) {
            Assert.assertEquals(tokenizer.nextToken(), tokenizer2.nextToken());
        } 
        int stringCount = factory.create(toTokenize).countTokens();
        List<String> tokens = factory.create(toTokenize).getTokens();
        Assert.assertEquals(9, stringCount);
        Assert.assertTrue(tokens.contains("Mary"));
        Assert.assertTrue(tokens.contains("had"));
        Assert.assertTrue(tokens.contains("a"));
        Assert.assertTrue(tokens.contains("little"));
        Assert.assertTrue(tokens.contains("lamb."));
        Assert.assertTrue(tokens.contains("Mary had"));
        Assert.assertTrue(tokens.contains("had a"));
        Assert.assertTrue(tokens.contains("a little"));
        Assert.assertTrue(tokens.contains("little lamb."));
        factory = new org.deeplearning4j.text.tokenization.tokenizerfactory.NGramTokenizerFactory(new DefaultTokenizerFactory(), 2, 2);
        tokens = factory.create(toTokenize).getTokens();
        Assert.assertEquals(4, tokens.size());
        Assert.assertTrue(tokens.contains("Mary had"));
        Assert.assertTrue(tokens.contains("had a"));
        Assert.assertTrue(tokens.contains("a little"));
        Assert.assertTrue(tokens.contains("little lamb."));
    }
}

