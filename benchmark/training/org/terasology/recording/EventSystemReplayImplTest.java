/**
 * Copyright 2018 MovingBlocks
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
package org.terasology.recording;


import RecordAndReplayStatus.REPLAYING;
import RecordAndReplayStatus.REPLAY_FINISHED;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.AbstractConsumableEvent;
import org.terasology.entitySystem.event.Event;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.event.internal.EventSystem;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.input.binds.interaction.AttackButton;


public class EventSystemReplayImplTest {
    private EntityRef entity;

    private EventSystem eventSystem;

    private EventSystemReplayImplTest.TestEventHandler handler;

    private RecordAndReplayCurrentStatus recordAndReplayCurrentStatus;

    @Test
    public void testReplayStatus() {
        Assert.assertEquals(REPLAYING, recordAndReplayCurrentStatus.getStatus());
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis()) - startTime) < 30) {
            eventSystem.process();
        } 
        Assert.assertEquals(REPLAY_FINISHED, recordAndReplayCurrentStatus.getStatus());
    }

    @Test
    public void testProcessingRecordedEvent() {
        Assert.assertEquals(0, handler.receivedAttackButtonList.size());
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis()) - startTime) < 10) {
            eventSystem.process();
        } 
        Assert.assertEquals(3, handler.receivedAttackButtonList.size());
    }

    @Test
    public void testBlockingEventDuringReplay() {
        Assert.assertEquals(0, handler.receivedAttackButtonList.size());
        eventSystem.send(entity, new AttackButton());
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis()) - startTime) < 10) {
            eventSystem.process();
        } 
        Assert.assertEquals(3, handler.receivedAttackButtonList.size());
    }

    @Test
    public void testSendingEventAfterReplay() {
        Assert.assertEquals(0, handler.receivedAttackButtonList.size());
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis()) - startTime) < 10) {
            eventSystem.process();
        } 
        eventSystem.send(entity, new AttackButton());
        Assert.assertEquals(4, handler.receivedAttackButtonList.size());
    }

    @Test
    public void testSendingAllowedEventDuringReplay() {
        eventSystem.send(entity, new EventSystemReplayImplTest.TestEvent());
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis()) - startTime) < 10) {
            eventSystem.process();
        } 
        Assert.assertEquals(3, handler.receivedAttackButtonList.size());
        Assert.assertEquals(1, handler.receivedTestEventList.size());
    }

    public static class TestEventHandler extends BaseComponentSystem {
        List<EventSystemReplayImplTest.Received> receivedAttackButtonList = Lists.newArrayList();

        List<EventSystemReplayImplTest.Received> receivedTestEventList = Lists.newArrayList();

        @ReceiveEvent
        public void handleAttackButtonEvent(AttackButton event, EntityRef entity) {
            receivedAttackButtonList.add(new EventSystemReplayImplTest.Received(event, entity));
        }

        @ReceiveEvent
        public void handleTestEvent(EventSystemReplayImplTest.TestEvent event, EntityRef entity) {
            receivedTestEventList.add(new EventSystemReplayImplTest.Received(event, entity));
        }
    }

    public static class Received {
        Event event;

        EntityRef entity;

        Received(Event event, EntityRef entity) {
            this.event = event;
            this.entity = entity;
        }
    }

    private static class TestEvent extends AbstractConsumableEvent {}
}

