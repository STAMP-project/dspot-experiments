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
package org.axonframework.eventhandling.tokenstore.inmemory;


import java.util.Arrays;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.tokenstore.UnableToClaimTokenException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class InMemoryTokenStoreTest {
    private InMemoryTokenStore testSubject;

    @Test
    public void testInitializeTokens() {
        testSubject.initializeTokenSegments("test1", 7);
        int[] actual = testSubject.fetchSegments("test1");
        Arrays.sort(actual);
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4, 5, 6 }, actual);
    }

    @Test
    public void testInitializeTokensAtGivenPosition() {
        testSubject.initializeTokenSegments("test1", 7, new GlobalSequenceTrackingToken(10));
        int[] actual = testSubject.fetchSegments("test1");
        Arrays.sort(actual);
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4, 5, 6 }, actual);
        for (int segment : actual) {
            Assert.assertEquals(new GlobalSequenceTrackingToken(10), testSubject.fetchToken("test1", segment));
        }
    }

    @Test
    public void testUpdateToken() {
        testSubject.initializeTokenSegments("test1", 1);
        testSubject.storeToken(new GlobalSequenceTrackingToken(1), "test1", 0);
        Assert.assertEquals(new GlobalSequenceTrackingToken(1), testSubject.fetchToken("test1", 0));
    }

    @Test
    public void testInitializeAtGivenToken() {
        testSubject.initializeTokenSegments("test1", 2, new GlobalSequenceTrackingToken(1));
        Assert.assertEquals(new GlobalSequenceTrackingToken(1), testSubject.fetchToken("test1", 0));
        Assert.assertEquals(new GlobalSequenceTrackingToken(1), testSubject.fetchToken("test1", 1));
    }

    @Test(expected = UnableToClaimTokenException.class)
    public void testInitializeTokensWhileAlreadyPresent() {
        testSubject.fetchToken("test1", 1);
        testSubject.initializeTokenSegments("test1", 7);
    }

    @Test
    public void testQuerySegments() {
        testSubject.initializeTokenSegments("test", 1);
        Assert.assertNull(testSubject.fetchToken("test", 0));
        testSubject.storeToken(new GlobalSequenceTrackingToken(1L), "proc1", 0);
        testSubject.storeToken(new GlobalSequenceTrackingToken(2L), "proc1", 1);
        testSubject.storeToken(new GlobalSequenceTrackingToken(2L), "proc2", 1);
        {
            final int[] segments = testSubject.fetchSegments("proc1");
            Assert.assertThat(segments.length, CoreMatchers.is(2));
        }
        {
            final int[] segments = testSubject.fetchSegments("proc2");
            Assert.assertThat(segments.length, CoreMatchers.is(1));
        }
        {
            final int[] segments = testSubject.fetchSegments("proc3");
            Assert.assertThat(segments.length, CoreMatchers.is(0));
        }
    }
}

