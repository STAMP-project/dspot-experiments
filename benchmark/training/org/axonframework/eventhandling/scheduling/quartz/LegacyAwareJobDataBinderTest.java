/**
 * Copyright (c) 2010-2017. Axon Framework
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
package org.axonframework.eventhandling.scheduling.quartz;


import java.io.ObjectInputStream;
import org.axonframework.eventhandling.EventMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.JobDataMap;


public class LegacyAwareJobDataBinderTest {
    private LegacyAwareJobDataBinder testSubject;

    @Test
    public void testReadLegacyInstance() {
        JobDataMap legacyJobDataMap = Mockito.mock(JobDataMap.class);
        Mockito.when(legacyJobDataMap.get("org.axonframework.domain.EventMessage")).thenAnswer(( i) -> {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(getClass().getClassLoader().getResourceAsStream("serialized.object"))) {
                return objectInputStream.readObject();
            }
        });
        Object event = testSubject.fromJobData(legacyJobDataMap);
        Mockito.verify(legacyJobDataMap).get("org.axonframework.domain.EventMessage");
        Assert.assertTrue((event instanceof EventMessage));
        EventMessage<?> eventMessage = ((EventMessage<?>) (event));
        Assert.assertEquals("this is the payload", eventMessage.getPayload());
        Assert.assertEquals("value", eventMessage.getMetaData().get("key"));
        Assert.assertEquals(1, eventMessage.getMetaData().size());
    }

    @Test
    public void testReadRecentInstance() {
        JobDataMap legacyJobDataMap = Mockito.mock(JobDataMap.class);
        Mockito.when(legacyJobDataMap.get(EventMessage.class.getName())).thenReturn(new org.axonframework.eventhandling.GenericEventMessage("new"));
        Object event = testSubject.fromJobData(legacyJobDataMap);
        Mockito.verify(legacyJobDataMap, Mockito.never()).get("org.axonframework.domain.EventMessage");
        Assert.assertTrue((event instanceof EventMessage));
        EventMessage<?> eventMessage = ((EventMessage<?>) (event));
        Assert.assertEquals("new", eventMessage.getPayload());
        Assert.assertEquals(0, eventMessage.getMetaData().size());
    }
}

