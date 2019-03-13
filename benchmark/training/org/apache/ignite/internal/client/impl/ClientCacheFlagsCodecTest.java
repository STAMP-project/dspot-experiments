/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.client.impl;


import java.util.EnumSet;
import java.util.Set;
import org.apache.ignite.internal.client.GridClientCacheFlag;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests conversions between GridClientCacheFlag.
 */
public class ClientCacheFlagsCodecTest {
    /**
     * Tests that each client flag will be correctly converted to server flag.
     */
    @Test
    public void testEncodingDecodingFullness() {
        for (GridClientCacheFlag f : GridClientCacheFlag.values()) {
            int bits = GridClientCacheFlag.encodeCacheFlags(EnumSet.of(f));
            Assert.assertTrue((bits != 0));
            Set<GridClientCacheFlag> out = GridClientCacheFlag.parseCacheFlags(bits);
            Assert.assertTrue(out.contains(f));
        }
    }

    /**
     * Tests that groups of client flags can be correctly converted to corresponding server flag groups.
     */
    @Test
    public void testGroupEncodingDecoding() {
        // All.
        doTestGroup(GridClientCacheFlag.values());
        // None.
        doTestGroup();
    }
}

