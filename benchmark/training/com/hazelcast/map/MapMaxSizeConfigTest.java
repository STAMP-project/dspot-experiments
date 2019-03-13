/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map;


import MaxSizeConfig.MaxSizePolicy;
import Warning.NONFINAL_FIELDS;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MaxSizeConfigReadOnly;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapMaxSizeConfigTest extends HazelcastTestSupport {
    @Test
    public void setMaxSize_withConstructor_toZero() throws Exception {
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig(0, MaxSizePolicy.PER_NODE);
        Assert.assertEquals(Integer.MAX_VALUE, maxSizeConfig.getSize());
    }

    @Test
    public void setMaxSize_withSetter_toZero() throws Exception {
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
        maxSizeConfig.setSize(0);
        Assert.assertEquals(Integer.MAX_VALUE, maxSizeConfig.getSize());
    }

    @Test
    public void setMaxSize_withConstructor_toNegative() throws Exception {
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig((-2131), MaxSizePolicy.PER_NODE);
        Assert.assertEquals(Integer.MAX_VALUE, maxSizeConfig.getSize());
    }

    @Test
    public void setMaxSize_withSetter_toNegative() throws Exception {
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
        maxSizeConfig.setSize((-2131));
        Assert.assertEquals(Integer.MAX_VALUE, maxSizeConfig.getSize());
    }

    @Test
    public void setMaxSize_withConstructor_toPositive() throws Exception {
        final int expectedMaxSize = 123456;
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig(expectedMaxSize, MaxSizePolicy.PER_NODE);
        Assert.assertEquals(expectedMaxSize, maxSizeConfig.getSize());
    }

    @Test
    public void setMaxSize_withSetter_toPositive() throws Exception {
        final int expectedMaxSize = 123456;
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
        maxSizeConfig.setSize(expectedMaxSize);
        Assert.assertEquals(expectedMaxSize, maxSizeConfig.getSize());
    }

    @Test
    public void testEquals() {
        HazelcastTestSupport.assumeDifferentHashCodes();
        EqualsVerifier.forClass(MaxSizeConfig.class).allFieldsShouldBeUsedExcept("readOnly").suppress(NONFINAL_FIELDS).withPrefabValues(MaxSizeConfigReadOnly.class, new MaxSizeConfigReadOnly(new MaxSizeConfig(100, MaxSizePolicy.PER_PARTITION)), new MaxSizeConfigReadOnly(new MaxSizeConfig(50, MaxSizePolicy.FREE_HEAP_PERCENTAGE))).verify();
    }
}

