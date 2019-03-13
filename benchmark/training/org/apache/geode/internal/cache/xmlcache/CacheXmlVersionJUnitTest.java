/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.xmlcache;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since GemFire 8.1
 */
public class CacheXmlVersionJUnitTest {
    /**
     * Previous strings based version just check ordinal comparison of strings. It failed test for
     * "8_0".compareTo("8.1") < 0. It also would have failed for "9.0".compareTo("10.0") < 0. Testing
     * that ENUM based solution is ordinal correct for comparisons.
     *
     * @since GemFire 8.1
     */
    @Test
    public void testOrdinal() {
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_3_0.compareTo(CacheXmlVersion.GEMFIRE_4_0)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_4_0.compareTo(CacheXmlVersion.GEMFIRE_4_1)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_4_1.compareTo(CacheXmlVersion.GEMFIRE_5_0)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_5_0.compareTo(CacheXmlVersion.GEMFIRE_5_1)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_5_1.compareTo(CacheXmlVersion.GEMFIRE_5_5)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_5_5.compareTo(CacheXmlVersion.GEMFIRE_5_7)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_5_7.compareTo(CacheXmlVersion.GEMFIRE_5_8)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_5_8.compareTo(CacheXmlVersion.GEMFIRE_6_0)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_6_0.compareTo(CacheXmlVersion.GEMFIRE_6_1)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_6_1.compareTo(CacheXmlVersion.GEMFIRE_6_5)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_6_5.compareTo(CacheXmlVersion.GEMFIRE_6_6)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_6_6.compareTo(CacheXmlVersion.GEMFIRE_7_0)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_7_0.compareTo(CacheXmlVersion.GEMFIRE_8_0)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_8_0.compareTo(CacheXmlVersion.GEMFIRE_8_1)) < 0));
        Assert.assertTrue(((CacheXmlVersion.GEMFIRE_8_1.compareTo(CacheXmlVersion.GEODE_1_0)) < 0));
    }

    /**
     * Test that {@link CacheXmlVersion#valueForVersion(String)} matches the same
     * {@link CacheXmlVersion} via {@link CacheXmlVersion#getVersion()}.
     *
     * @since GemFire 8.1
     */
    @Test
    public void testValueForVersion() {
        for (final CacheXmlVersion cacheXmlVersion : CacheXmlVersion.values()) {
            Assert.assertSame(cacheXmlVersion, CacheXmlVersion.valueForVersion(cacheXmlVersion.getVersion()));
        }
    }
}

