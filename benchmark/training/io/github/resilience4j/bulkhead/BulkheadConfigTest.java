/**
 * Copyright 2017 Robert Winkler, Lucas Lech
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.resilience4j.bulkhead;


import org.junit.Test;


public class BulkheadConfigTest {
    @Test
    public void testBuildCustom() {
        // given
        int maxConcurrent = 66;
        long maxWait = 555;
        // when
        BulkheadConfig config = BulkheadConfig.custom().maxConcurrentCalls(maxConcurrent).maxWaitTime(maxWait).build();
        // then
        assertThat(config).isNotNull();
        assertThat(config.getMaxConcurrentCalls()).isEqualTo(maxConcurrent);
        assertThat(config.getMaxWaitTime()).isEqualTo(maxWait);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithIllegalMaxConcurrent() {
        // when
        BulkheadConfig.custom().maxConcurrentCalls((-1)).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithIllegalMaxWait() {
        // when
        BulkheadConfig.custom().maxWaitTime((-1)).build();
    }
}

