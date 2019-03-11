/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling;


import junit.framework.TestCase;
import org.junit.Test;


public class GlobalSequenceTrackingTokenTest {
    @Test
    public void testUpperBound() {
        GlobalSequenceTrackingToken token1 = new GlobalSequenceTrackingToken(1L);
        GlobalSequenceTrackingToken token2 = new GlobalSequenceTrackingToken(2L);
        TestCase.assertSame(token2, token1.upperBound(token2));
        TestCase.assertSame(token2, token2.upperBound(token1));
        TestCase.assertSame(token2, token2.upperBound(token2));
    }

    @Test
    public void testLowerBound() {
        GlobalSequenceTrackingToken token1 = new GlobalSequenceTrackingToken(1L);
        GlobalSequenceTrackingToken token2 = new GlobalSequenceTrackingToken(2L);
        TestCase.assertSame(token1, token1.lowerBound(token2));
        TestCase.assertSame(token1, token2.lowerBound(token1));
        TestCase.assertSame(token2, token2.lowerBound(token2));
    }

    @Test
    public void testCovers() {
        GlobalSequenceTrackingToken token1 = new GlobalSequenceTrackingToken(1L);
        GlobalSequenceTrackingToken token2 = new GlobalSequenceTrackingToken(2L);
        TestCase.assertFalse(token1.covers(token2));
        TestCase.assertTrue(token2.covers(token1));
        TestCase.assertTrue(token2.covers(token2));
        TestCase.assertTrue(token2.covers(null));
    }
}

