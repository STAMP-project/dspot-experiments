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
import org.deeplearning4j.text.tokenization.tokenizerfactory.JapaneseTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Test;


public class JapaneseTokenizerTest {
    private String toTokenize = "??????????";

    private String[] expect = new String[]{ "??", "?", "?", "??", "?", "???" };

    private String baseString = "??????????????";

    @Test
    public void testJapaneseTokenizer() throws Exception {
        TokenizerFactory t = new JapaneseTokenizerFactory();
        Tokenizer tokenizer = t.create(toTokenize);
        Assert.assertEquals(expect.length, tokenizer.countTokens());
        for (int i = 0; i < (tokenizer.countTokens()); ++i) {
            Assert.assertEquals(tokenizer.nextToken(), expect[i]);
        }
    }

    @Test
    public void testBaseForm() throws Exception {
        TokenizerFactory tf = new JapaneseTokenizerFactory(true);
        Tokenizer tokenizer1 = tf.create(toTokenize);
        Tokenizer tokenizer2 = tf.create(baseString);
        Assert.assertEquals("??", tokenizer1.nextToken());
        Assert.assertEquals("??", tokenizer2.nextToken());
    }

    @Test
    public void testGetTokens() throws Exception {
        TokenizerFactory tf = new JapaneseTokenizerFactory();
        Tokenizer tokenizer = tf.create(toTokenize);
        // Exhaust iterator.
        Assert.assertEquals(expect.length, tokenizer.countTokens());
        for (int i = 0; i < (tokenizer.countTokens()); ++i) {
            Assert.assertEquals(tokenizer.nextToken(), expect[i]);
        }
        // Ensure exhausted.
        Assert.assertEquals(false, tokenizer.hasMoreTokens());
        // Count doesn't change.
        Assert.assertEquals(expect.length, tokenizer.countTokens());
        // getTokens still returns everything.
        List<String> tokens = tokenizer.getTokens();
        Assert.assertEquals(expect.length, tokens.size());
    }

    @Test
    public void testKuromojiMultithreading() throws Exception {
        class Worker implements Runnable {
            private final JapaneseTokenizerFactory tf;

            private final String[] jobs;

            private int runs;

            private boolean passed = false;

            public Worker(JapaneseTokenizerFactory tf, String[] jobs, int runs) {
                this.tf = tf;
                this.jobs = jobs;
                this.runs = runs;
            }

            @Override
            public void run() {
                while ((runs) > 0) {
                    String s = jobs[(((runs)--) % (jobs.length))];
                    List<String> tokens = tf.create(s).getTokens();
                    StringBuilder sb = new StringBuilder();
                    for (String token : tokens) {
                        sb.append(token);
                    }
                    if ((sb.toString().length()) != (s.length())) {
                        return;
                    }
                } 
                passed = true;
            }
        }
        JapaneseTokenizerFactory tf = new JapaneseTokenizerFactory();
        String[] work = new String[]{ toTokenize, baseString, toTokenize, baseString };
        Worker[] workers = new Worker[10];
        for (int i = 0; i < (workers.length); i++) {
            workers[i] = new Worker(tf, work, 50);
        }
        Thread[] threads = new Thread[10];
        for (int i = 0; i < (threads.length); i++) {
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        for (int i = 0; i < (workers.length); i++) {
            Assert.assertTrue(workers[i].passed);
        }
    }
}

