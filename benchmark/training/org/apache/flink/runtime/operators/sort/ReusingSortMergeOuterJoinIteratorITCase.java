/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.operators.sort;


import OuterJoinType.FULL;
import OuterJoinType.LEFT;
import OuterJoinType.RIGHT;
import org.junit.Test;


public class ReusingSortMergeOuterJoinIteratorITCase extends AbstractSortMergeOuterJoinIteratorITCase {
    @Test
    public void testFullOuterWithSample() throws Exception {
        super.testFullOuterWithSample();
    }

    @Test
    public void testLeftOuterWithSample() throws Exception {
        super.testLeftOuterWithSample();
    }

    @Test
    public void testRightOuterWithSample() throws Exception {
        super.testRightOuterWithSample();
    }

    @Test
    public void testRightSideEmpty() throws Exception {
        super.testRightSideEmpty();
    }

    @Test
    public void testLeftSideEmpty() throws Exception {
        super.testLeftSideEmpty();
    }

    @Test
    public void testFullOuterJoinWithHighNumberOfCommonKeys() {
        testOuterJoinWithHighNumberOfCommonKeys(FULL, 200, 500, 2048, 0.02F, 200, 500, 2048, 0.02F);
    }

    @Test
    public void testLeftOuterJoinWithHighNumberOfCommonKeys() {
        testOuterJoinWithHighNumberOfCommonKeys(LEFT, 200, 10, 4096, 0.02F, 100, 4000, 2048, 0.02F);
    }

    @Test
    public void testRightOuterJoinWithHighNumberOfCommonKeys() {
        testOuterJoinWithHighNumberOfCommonKeys(RIGHT, 100, 10, 2048, 0.02F, 200, 4000, 4096, 0.02F);
    }
}

