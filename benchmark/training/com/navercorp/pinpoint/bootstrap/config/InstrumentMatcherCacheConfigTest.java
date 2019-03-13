/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.bootstrap.config;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class InstrumentMatcherCacheConfigTest {
    @Test
    public void constructor() throws Exception {
        InstrumentMatcherCacheConfig config = new InstrumentMatcherCacheConfig();
        config.setInterfaceCacheSize(1);
        config.setInterfaceCacheEntrySize(2);
        config.setAnnotationCacheSize(3);
        config.setAnnotationCacheEntrySize(4);
        config.setSuperCacheSize(5);
        config.setSuperCacheEntrySize(6);
        Assert.assertEquals(1, config.getInterfaceCacheSize());
        Assert.assertEquals(2, config.getInterfaceCacheEntrySize());
        Assert.assertEquals(3, config.getAnnotationCacheSize());
        Assert.assertEquals(4, config.getAnnotationCacheEntrySize());
        Assert.assertEquals(5, config.getSuperCacheSize());
        Assert.assertEquals(6, config.getSuperCacheEntrySize());
    }
}

