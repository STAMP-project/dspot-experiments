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
import org.junit.Test;


public final class AbstractRescorerProviderTest extends OryxTest {
    @Test
    public void testDefault() {
        RescorerProvider noop = new NullProvider1();
        assertNull(noop.getMostActiveUsersRescorer(null));
        assertNull(noop.getMostPopularItemsRescorer(null));
        assertNull(noop.getMostSimilarItemsRescorer(null));
        assertNull(noop.getRecommendRescorer(null, null));
        assertNull(noop.getRecommendToAnonymousRescorer(null, null));
    }
}

