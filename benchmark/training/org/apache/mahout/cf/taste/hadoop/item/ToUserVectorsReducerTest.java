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
package org.apache.mahout.cf.taste.hadoop.item;


import Reducer.Context;
import ToUserVectorsReducer.Counters.USERS;
import java.util.Arrays;
import java.util.Collections;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.mahout.cf.taste.hadoop.TasteHadoopUtils;
import org.apache.mahout.cf.taste.impl.TasteTestCase;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.VarLongWritable;
import org.apache.mahout.math.hadoop.MathHelper;
import org.easymock.EasyMock;
import org.junit.Test;


/**
 * tests {@link ToUserVectorsReducer}
 */
public class ToUserVectorsReducerTest extends TasteTestCase {
    @Test
    public void testToUsersReducerMinPreferencesUserIgnored() throws Exception {
        Context context = EasyMock.createMock(Context.class);
        ToUserVectorsReducer reducer = new ToUserVectorsReducer();
        MahoutTestCase.setField(reducer, "minPreferences", 2);
        EasyMock.replay(context);
        reducer.reduce(new VarLongWritable(123), Collections.singletonList(new VarLongWritable(456)), context);
        EasyMock.verify(context);
    }

    @Test
    public void testToUsersReducerMinPreferencesUserPasses() throws Exception {
        Context context = EasyMock.createMock(Context.class);
        Counter userCounters = EasyMock.createMock(Counter.class);
        ToUserVectorsReducer reducer = new ToUserVectorsReducer();
        MahoutTestCase.setField(reducer, "minPreferences", 2);
        EasyMock.expect(context.getCounter(USERS)).andReturn(userCounters);
        userCounters.increment(1);
        context.write(EasyMock.eq(new VarLongWritable(123)), MathHelper.vectorMatches(MathHelper.elem(TasteHadoopUtils.idToIndex(456L), 1.0), MathHelper.elem(TasteHadoopUtils.idToIndex(789L), 1.0)));
        EasyMock.replay(context, userCounters);
        reducer.reduce(new VarLongWritable(123), Arrays.asList(new VarLongWritable(456), new VarLongWritable(789)), context);
        EasyMock.verify(context, userCounters);
    }
}

