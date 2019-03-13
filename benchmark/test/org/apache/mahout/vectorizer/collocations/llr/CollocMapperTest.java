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
package org.apache.mahout.vectorizer.collocations.llr;


import CollocDriver.EMIT_UNIGRAMS;
import CollocMapper.Count.NGRAM_TOTAL;
import CollocMapper.MAX_SHINGLE_SIZE;
import Mapper.Context;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.StringTuple;
import org.apache.mahout.vectorizer.collocations.llr.Gram.Type;
import org.easymock.EasyMock;
import org.junit.Test;


/**
 * Test for CollocMapper
 */
public final class CollocMapperTest extends MahoutTestCase {
    private Context context;

    private Counter counter;

    @Test
    public void testCollectNgrams() throws Exception {
        Text key = new Text();
        key.set("dummy-key");
        String[] input = new String[]{ "the", "best", "of", "times", "the", "worst", "of", "times" };
        StringTuple inputTuple = new StringTuple();
        for (String i : input) {
            inputTuple.add(i);
        }
        String[][] values = new String[][]{ new String[]{ "h_the", "the best" }, new String[]{ "t_best", "the best" }, new String[]{ "h_of", "of times" }, new String[]{ "t_times", "of times" }, new String[]{ "h_best", "best of" }, new String[]{ "t_of", "best of" }, new String[]{ "h_the", "the worst" }, new String[]{ "t_worst", "the worst" }, new String[]{ "h_times", "times the" }, new String[]{ "t_the", "times the" }, new String[]{ "h_worst", "worst of" }, new String[]{ "t_of", "worst of" } };
        // set up expectations for mocks. ngram max size = 2
        Configuration conf = getConfiguration();
        conf.set(MAX_SHINGLE_SIZE, "2");
        EasyMock.expect(context.getConfiguration()).andReturn(conf);
        for (String[] v : values) {
            Type p = (v[0].startsWith("h")) ? Type.HEAD : Type.TAIL;
            int frequency = 1;
            if ("of times".equals(v[1])) {
                frequency = 2;
            }
            Gram subgram = new Gram(v[0].substring(2), frequency, p);
            Gram ngram = new Gram(v[1], frequency, Type.NGRAM);
            GramKey subgramKey = new GramKey(subgram, new byte[0]);
            GramKey subgramNgramKey = new GramKey(subgram, ngram.getBytes());
            context.write(subgramKey, subgram);
            context.write(subgramNgramKey, ngram);
        }
        EasyMock.expect(context.getCounter(NGRAM_TOTAL)).andReturn(counter);
        counter.increment(7);
        EasyMock.replay(context, counter);
        CollocMapper c = new CollocMapper();
        c.setup(context);
        c.map(key, inputTuple, context);
        EasyMock.verify(context);
    }

    @Test
    public void testCollectNgramsWithUnigrams() throws Exception {
        Text key = new Text();
        key.set("dummy-key");
        String[] input = new String[]{ "the", "best", "of", "times", "the", "worst", "of", "times" };
        StringTuple inputTuple = new StringTuple();
        for (String i : input) {
            inputTuple.add(i);
        }
        String[][] values = new String[][]{ new String[]{ "h_the", "the best" }, new String[]{ "t_best", "the best" }, new String[]{ "h_of", "of times" }, new String[]{ "t_times", "of times" }, new String[]{ "h_best", "best of" }, new String[]{ "t_of", "best of" }, new String[]{ "h_the", "the worst" }, new String[]{ "t_worst", "the worst" }, new String[]{ "h_times", "times the" }, new String[]{ "t_the", "times the" }, new String[]{ "h_worst", "worst of" }, new String[]{ "t_of", "worst of" }, new String[]{ "u_worst", "worst" }, new String[]{ "u_of", "of" }, new String[]{ "u_the", "the" }, new String[]{ "u_best", "best" }, new String[]{ "u_times", "times" } };
        // set up expectations for mocks. ngram max size = 2
        Configuration conf = getConfiguration();
        conf.set(MAX_SHINGLE_SIZE, "2");
        conf.setBoolean(EMIT_UNIGRAMS, true);
        EasyMock.expect(context.getConfiguration()).andReturn(conf);
        for (String[] v : values) {
            Type p = (v[0].startsWith("h")) ? Type.HEAD : Type.TAIL;
            p = (v[0].startsWith("u")) ? Type.UNIGRAM : p;
            int frequency = 1;
            if (((("of times".equals(v[1])) || ("of".equals(v[1]))) || ("times".equals(v[1]))) || ("the".equals(v[1]))) {
                frequency = 2;
            }
            if (p == (Type.UNIGRAM)) {
                Gram unigram = new Gram(v[1], frequency, Type.UNIGRAM);
                GramKey unigramKey = new GramKey(unigram, new byte[0]);
                context.write(unigramKey, unigram);
            } else {
                Gram subgram = new Gram(v[0].substring(2), frequency, p);
                Gram ngram = new Gram(v[1], frequency, Type.NGRAM);
                GramKey subgramKey = new GramKey(subgram, new byte[0]);
                GramKey subgramNgramKey = new GramKey(subgram, ngram.getBytes());
                context.write(subgramKey, subgram);
                context.write(subgramNgramKey, ngram);
            }
        }
        EasyMock.expect(context.getCounter(NGRAM_TOTAL)).andReturn(counter);
        counter.increment(7);
        EasyMock.replay(context, counter);
        CollocMapper c = new CollocMapper();
        c.setup(context);
        c.map(key, inputTuple, context);
        EasyMock.verify(context);
    }
}

