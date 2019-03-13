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
package org.apache.mahout.cf.taste.hadoop;


import java.util.List;
import org.apache.mahout.cf.taste.impl.TasteTestCase;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


public class TopItemsQueueTest extends TasteTestCase {
    @Test
    public void topK() {
        float[] ratings = new float[]{ 0.5F, 0.6F, 0.7F, 2.0F, 0.0F };
        List<RecommendedItem> topItems = TopItemsQueueTest.findTop(ratings, 2);
        assertEquals(2, topItems.size());
        assertEquals(3L, topItems.get(0).getItemID());
        assertEquals(2.0F, topItems.get(0).getValue(), MahoutTestCase.EPSILON);
        assertEquals(2L, topItems.get(1).getItemID());
        assertEquals(0.7F, topItems.get(1).getValue(), MahoutTestCase.EPSILON);
    }

    @Test
    public void topKInputSmallerThanK() {
        float[] ratings = new float[]{ 0.7F, 2.0F };
        List<RecommendedItem> topItems = TopItemsQueueTest.findTop(ratings, 3);
        assertEquals(2, topItems.size());
        assertEquals(1L, topItems.get(0).getItemID());
        assertEquals(2.0F, topItems.get(0).getValue(), MahoutTestCase.EPSILON);
        assertEquals(0L, topItems.get(1).getItemID());
        assertEquals(0.7F, topItems.get(1).getValue(), MahoutTestCase.EPSILON);
    }
}

