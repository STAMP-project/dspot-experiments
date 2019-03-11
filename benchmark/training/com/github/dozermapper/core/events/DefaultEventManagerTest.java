/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.events;


import EventTypes.MAPPING_FINISHED;
import EventTypes.MAPPING_POST_WRITING_DEST_VALUE;
import EventTypes.MAPPING_PRE_WRITING_DEST_VALUE;
import EventTypes.MAPPING_STARTED;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultEventManagerTest {
    @Test
    public void handlesEmptyListeners() {
        Event mockedEvent = Mockito.mock(DefaultEvent.class);
        Mockito.when(mockedEvent.getType()).thenReturn(MAPPING_STARTED);
        EventManager manager = new DefaultEventManager(new ArrayList(0));
        manager.on(mockedEvent);
        Assert.assertTrue(true);
    }

    @Test
    public void handlesNullListeners() {
        Event mockedEvent = Mockito.mock(DefaultEvent.class);
        Mockito.when(mockedEvent.getType()).thenReturn(MAPPING_FINISHED);
        EventManager manager = new DefaultEventManager(null);
        manager.on(mockedEvent);
        Assert.assertTrue(true);
    }

    @Test
    public void handlesOnMappingStarted() {
        Event mockedEvent = Mockito.mock(DefaultEvent.class);
        Mockito.when(mockedEvent.getType()).thenReturn(MAPPING_STARTED);
        EventListener listener = Mockito.mock(EventListener.class);
        List<EventListener> listeners = new ArrayList<>(1);
        listeners.add(listener);
        EventManager manager = new DefaultEventManager(listeners);
        manager.on(mockedEvent);
        Mockito.verify(listener).onMappingStarted(mockedEvent);
        Assert.assertTrue(true);
    }

    @Test
    public void handlesOnMappingPreWrite() {
        Event mockedEvent = Mockito.mock(DefaultEvent.class);
        Mockito.when(mockedEvent.getType()).thenReturn(MAPPING_PRE_WRITING_DEST_VALUE);
        EventListener listener = Mockito.mock(EventListener.class);
        List<EventListener> listeners = new ArrayList<>(1);
        listeners.add(listener);
        EventManager manager = new DefaultEventManager(listeners);
        manager.on(mockedEvent);
        Mockito.verify(listener).onPreWritingDestinationValue(mockedEvent);
        Assert.assertTrue(true);
    }

    @Test
    public void handlesOnMappingPostWrite() {
        Event mockedEvent = Mockito.mock(DefaultEvent.class);
        Mockito.when(mockedEvent.getType()).thenReturn(MAPPING_POST_WRITING_DEST_VALUE);
        EventListener listener = Mockito.mock(EventListener.class);
        List<EventListener> listeners = new ArrayList<>(1);
        listeners.add(listener);
        EventManager manager = new DefaultEventManager(listeners);
        manager.on(mockedEvent);
        Mockito.verify(listener).onPostWritingDestinationValue(mockedEvent);
        Assert.assertTrue(true);
    }

    @Test
    public void handlesOnMappingFinished() {
        Event mockedEvent = Mockito.mock(DefaultEvent.class);
        Mockito.when(mockedEvent.getType()).thenReturn(MAPPING_FINISHED);
        EventListener listener = Mockito.mock(EventListener.class);
        List<EventListener> listeners = new ArrayList<>(1);
        listeners.add(listener);
        EventManager manager = new DefaultEventManager(listeners);
        manager.on(mockedEvent);
        Mockito.verify(listener).onMappingFinished(mockedEvent);
        Assert.assertTrue(true);
    }
}

