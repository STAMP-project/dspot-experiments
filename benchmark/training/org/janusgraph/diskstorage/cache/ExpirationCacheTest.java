/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.diskstorage.cache;


import java.time.Duration;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class ExpirationCacheTest extends KCVSCacheTest {
    public static final String METRICS_STRING = "metrics";

    public static final long CACHE_SIZE = (1024 * 1024) * 48;// 48 MB


    @Test
    public void testExpiration() throws Exception {
        testExpiration(Duration.ofMillis(200));
        testExpiration(Duration.ofSeconds(4));
        testExpiration(Duration.ofSeconds(1));
    }

    @Test
    public void testGracePeriod() throws Exception {
        testGracePeriod(Duration.ofMillis(200));
        testGracePeriod(Duration.ZERO);
        testGracePeriod(Duration.ofSeconds(1));
    }
}

