/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.als;


import com.cloudera.oryx.common.OryxTest;
import java.util.Collections;
import org.junit.Test;


/**
 * Tests {@link MultiRescorerProvider} (implementations).
 */
public final class MultiRescorerProviderTest extends OryxTest {
    @Test
    public void testMultiRecommendRescorer() {
        RescorerProvider multi = MultiRescorerProvider.of(new SimpleModRescorerProvider(2), new SimpleModRescorerProvider(3));
        Rescorer provider = multi.getRecommendRescorer(Collections.singletonList("ABCDE"), null);
        assertNull(provider);
        Rescorer provider2 = multi.getRecommendRescorer(Collections.singletonList("AB"), null);
        assertNotNull(provider2);
        assertFalse((provider2 instanceof MultiRescorer));
        MultiRescorerProviderTest.assertIsFiltered(provider2, "ABC");
        MultiRescorerProviderTest.assertNotFiltered(provider2, "AB");
        assertNaN(provider2.rescore("ABC", 1.0));
        assertEquals(1.0, provider2.rescore("AB", 1.0));
        Rescorer provider3 = multi.getRecommendRescorer(Collections.singletonList("ABCDEF"), null);
        assertNotNull(provider3);
        assertInstanceOf(provider3, MultiRescorer.class);
        MultiRescorerProviderTest.assertIsFiltered(provider3, "ABC");
        MultiRescorerProviderTest.assertIsFiltered(provider3, "AB");
        MultiRescorerProviderTest.assertNotFiltered(provider3, "ABCDEFABCDEF");
        assertNaN(provider3.rescore("ABC", 1.0));
        assertEquals(1.0, provider3.rescore("ABCDEFABCDEF", 1.0));
    }

    @Test
    public void testMultiRecommendToAnonymousRescorer() {
        RescorerProvider multi = MultiRescorerProvider.of(new SimpleModRescorerProvider(2), new SimpleModRescorerProvider(3));
        Rescorer provider = multi.getRecommendToAnonymousRescorer(Collections.singletonList("ABCDE"), null);
        assertNull(provider);
        Rescorer provider2 = multi.getRecommendToAnonymousRescorer(Collections.singletonList("AB"), null);
        assertNotNull(provider2);
        assertFalse((provider2 instanceof MultiRescorer));
        MultiRescorerProviderTest.assertIsFiltered(provider2, "ABC");
        MultiRescorerProviderTest.assertNotFiltered(provider2, "AB");
        Rescorer provider3 = multi.getRecommendToAnonymousRescorer(Collections.singletonList("ABCDEF"), null);
        assertNotNull(provider3);
        assertInstanceOf(provider3, MultiRescorer.class);
        MultiRescorerProviderTest.assertIsFiltered(provider3, "ABC");
        MultiRescorerProviderTest.assertIsFiltered(provider3, "AB");
        MultiRescorerProviderTest.assertNotFiltered(provider3, "ABCDEF");
    }

    @Test
    public void testMultiMostPopularItemsRescorer() {
        RescorerProvider multi = MultiRescorerProvider.of(new SimpleModRescorerProvider(2), new SimpleModRescorerProvider(3));
        Rescorer provider = multi.getMostPopularItemsRescorer(null);
        assertNotNull(provider);
        assertInstanceOf(provider, MultiRescorer.class);
        MultiRescorerProviderTest.assertIsFiltered(provider, "ABC");
        MultiRescorerProviderTest.assertIsFiltered(provider, "AB");
        MultiRescorerProviderTest.assertNotFiltered(provider, "ABCDEF");
    }

    @Test
    public void testMultiMostActiveUsersRescorer() {
        RescorerProvider multi = MultiRescorerProvider.of(new SimpleModRescorerProvider(2), new SimpleModRescorerProvider(3));
        Rescorer provider = multi.getMostActiveUsersRescorer(null);
        assertNotNull(provider);
        assertInstanceOf(provider, MultiRescorer.class);
        MultiRescorerProviderTest.assertIsFiltered(provider, "ABC");
        MultiRescorerProviderTest.assertIsFiltered(provider, "AB");
        MultiRescorerProviderTest.assertNotFiltered(provider, "ABCDEF");
    }

    @Test
    public void testMultiMostSimilarItemsRescorer() {
        RescorerProvider multi = MultiRescorerProvider.of(new SimpleModRescorerProvider(2), new SimpleModRescorerProvider(3));
        Rescorer provider = multi.getMostSimilarItemsRescorer(null);
        assertNotNull(provider);
        assertInstanceOf(provider, MultiRescorer.class);
        MultiRescorerProviderTest.assertIsFiltered(provider, "ABC");
        MultiRescorerProviderTest.assertIsFiltered(provider, "ABCDE");
        MultiRescorerProviderTest.assertNotFiltered(provider, "ABCDEFABCDEF");
    }
}

