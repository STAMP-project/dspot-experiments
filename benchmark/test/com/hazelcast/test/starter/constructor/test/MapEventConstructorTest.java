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
package com.hazelcast.test.starter.constructor.test;


import EntryEventType.INVALIDATION;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.starter.constructor.MapEventConstructor;
import com.hazelcast.util.UuidUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapEventConstructorTest {
    @Test
    public void testConstructor() {
        String source = UuidUtil.newUnsecureUuidString();
        Member member = Mockito.mock(Member.class);
        int eventType = INVALIDATION.getType();
        MapEvent mapEvent = new MapEvent(source, member, eventType, 23);
        MapEventConstructor constructor = new MapEventConstructor(MapEvent.class);
        MapEvent clonedMapEvent = ((MapEvent) (constructor.createNew(mapEvent)));
        Assert.assertEquals(mapEvent.getName(), clonedMapEvent.getName());
        Assert.assertEquals(mapEvent.getMember(), clonedMapEvent.getMember());
        Assert.assertEquals(mapEvent.getEventType(), clonedMapEvent.getEventType());
        Assert.assertEquals(mapEvent.getSource(), clonedMapEvent.getSource());
        Assert.assertEquals(mapEvent.getNumberOfEntriesAffected(), clonedMapEvent.getNumberOfEntriesAffected());
    }
}

