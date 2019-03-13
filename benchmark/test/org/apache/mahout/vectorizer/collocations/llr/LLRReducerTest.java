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


import Gram.Type;
import LLRReducer.NGRAM_TOTAL;
import Reducer.Context;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.vectorizer.collocations.llr.LLRReducer.LLCallback;
import org.easymock.EasyMock;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the LLRReducer
 *  TODO Add negative test cases.
 */
public final class LLRReducerTest extends MahoutTestCase {
    private static final Logger log = LoggerFactory.getLogger(LLRReducerTest.class);

    private Context context;

    private LLCallback ll;

    private LLCallback cl;

    @Test
    public void testReduce() throws Exception {
        LLRReducer reducer = new LLRReducer(ll);
        // test input, input[*][0] is the key,
        // input[*][1..n] are the values passed in via
        // the iterator.
        Gram[][] input = new Gram[][]{ new Gram[]{ new Gram("the best", 1, Type.NGRAM), new Gram("the", 2, Type.HEAD), new Gram("best", 1, Type.TAIL) }, new Gram[]{ new Gram("best of", 1, Type.NGRAM), new Gram("best", 1, Type.HEAD), new Gram("of", 2, Type.TAIL) }, new Gram[]{ new Gram("of times", 2, Type.NGRAM), new Gram("of", 2, Type.HEAD), new Gram("times", 2, Type.TAIL) }, new Gram[]{ new Gram("times the", 1, Type.NGRAM), new Gram("times", 1, Type.HEAD), new Gram("the", 1, Type.TAIL) }, new Gram[]{ new Gram("the worst", 1, Type.NGRAM), new Gram("the", 2, Type.HEAD), new Gram("worst", 1, Type.TAIL) }, new Gram[]{ new Gram("worst of", 1, Type.NGRAM), new Gram("worst", 1, Type.HEAD), new Gram("of", 2, Type.TAIL) } };
        int[][] expectations = new int[][]{ // A+B, A+!B, !A+B, !A+!B
        new int[]{ 1, 1, 0, 5 }// the best
        // the best
        // the best
        , new int[]{ 1, 0, 1, 5 }// best of
        // best of
        // best of
        , new int[]{ 2, 0, 0, 5 }// of times
        // of times
        // of times
        , new int[]{ 1, 0, 0, 6 }// times the
        // times the
        // times the
        , new int[]{ 1, 1, 0, 5 }// the worst
        // the worst
        // the worst
        , new int[]{ 1, 0, 1, 5 }// worst of
        // worst of
        // worst of
         };
        Configuration config = getConfiguration();
        config.set(NGRAM_TOTAL, "7");
        EasyMock.expect(context.getConfiguration()).andReturn(config);
        for (int i = 0; i < (expectations.length); i++) {
            int[] ee = expectations[i];
            context.write(EasyMock.eq(new org.apache.hadoop.io.Text(input[i][0].getString())), ((DoubleWritable) (EasyMock.anyObject())));
            EasyMock.expect(ll.logLikelihoodRatio(ee[0], ee[1], ee[2], ee[3])).andDelegateTo(cl);
        }
        EasyMock.replay(context, ll);
        reducer.setup(context);
        for (Gram[] ii : input) {
            Collection<Gram> vv = Lists.newLinkedList();
            vv.addAll(Arrays.asList(ii).subList(1, ii.length));
            reducer.reduce(ii[0], vv, context);
        }
        EasyMock.verify(ll);
    }
}

