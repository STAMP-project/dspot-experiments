/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.management.providers.statistics;


import LatencyHistogramConfiguration.DEFAULT_BUCKET_COUNT;
import LatencyHistogramConfiguration.DEFAULT_PHI;
import LatencyHistogramConfiguration.DEFAULT_WINDOW;
import org.junit.Test;

import static LatencyHistogramConfiguration.DEFAULT;


public class LatencyHistogramConfigurationTest {
    @Test
    public void test() {
        LatencyHistogramConfiguration conf = DEFAULT;
        assertThat(conf.getPhi()).isEqualTo(DEFAULT_PHI);
        assertThat(conf.getBucketCount()).isEqualTo(DEFAULT_BUCKET_COUNT);
        assertThat(conf.getWindow()).isEqualTo(DEFAULT_WINDOW);
    }
}

