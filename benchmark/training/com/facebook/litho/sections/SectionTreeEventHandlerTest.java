/**
 * Copyright 2018-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.sections;


import EventHandlersController.EventHandlersWrapper;
import SectionTree.Target;
import com.facebook.litho.EventHandler;
import com.facebook.litho.EventHandlersController;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(ComponentsTestRunner.class)
public class SectionTreeEventHandlerTest {
    private final Section mSection = Mockito.mock(Section.class);

    private SectionContext mContext;

    private Target mTestTarget;

    @Test
    public void testNoDuplicateWhenEventHandlerIsReplacedInEventHandlerWrapper() {
        SectionTree sectionTree = SectionTree.create(mContext, mTestTarget).build();
        EventHandlersController eventHandlersController = sectionTree.getEventHandlersController();
        sectionTree.setRoot(mSection);
        EventHandler eventHandler1 = mContext.newEventHandler(1, null);
        sectionTree.recordEventHandler(mSection, eventHandler1);
        eventHandlersController.bindEventHandlers(mContext, mSection, mSection.getGlobalKey());
        eventHandlersController.clearUnusedEventHandlers();
        assertThat(eventHandlersController.getEventHandlers().size()).isEqualTo(1);
        EventHandlersController.EventHandlersWrapper eventHandlersWrapper = eventHandlersController.getEventHandlers().values().iterator().next();
        assertThat(eventHandlersWrapper.getEventHandlers().size()).isEqualTo(1);
        EventHandler eventHandler2 = mContext.newEventHandler(1, null);
        sectionTree.recordEventHandler(mSection, eventHandler2);
        eventHandlersController.bindEventHandlers(mContext, mSection, mSection.getGlobalKey());
        assertThat(eventHandlersWrapper.getEventHandlers().size()).isEqualTo(1);
        eventHandlersWrapper = eventHandlersController.getEventHandlers().values().iterator().next();
        assertThat(eventHandlersWrapper.getEventHandlers().size()).isEqualTo(1);
    }

    @Test
    public void testClearUnusedEntries() {
        SectionTree sectionTree = SectionTree.create(mContext, mTestTarget).build();
        EventHandlersController eventHandlersController = sectionTree.getEventHandlersController();
        sectionTree.setRoot(mSection);
        EventHandler eventHandler1 = mContext.newEventHandler(1, null);
        sectionTree.recordEventHandler(mSection, eventHandler1);
        eventHandlersController.bindEventHandlers(mContext, mSection, mSection.getGlobalKey());
        eventHandlersController.clearUnusedEventHandlers();
        assertThat(eventHandlersController.getEventHandlers().size()).isEqualTo(1);
        Mockito.when(mSection.getGlobalKey()).thenReturn("section2");
        sectionTree.setRoot(mSection);
        sectionTree.recordEventHandler(mSection, eventHandler1);
        eventHandlersController.bindEventHandlers(mContext, mSection, mSection.getGlobalKey());
        eventHandlersController.clearUnusedEventHandlers();
        assertThat(eventHandlersController.getEventHandlers().size()).isEqualTo(1);
    }
}

