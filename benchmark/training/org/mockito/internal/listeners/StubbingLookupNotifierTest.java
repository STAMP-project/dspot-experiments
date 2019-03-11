/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.listeners;


import StubbingLookupNotifier.Event;
import java.util.Collection;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.creation.settings.CreationSettings;
import org.mockito.invocation.Invocation;
import org.mockito.stubbing.Stubbing;
import org.mockitoutil.TestBase;


public class StubbingLookupNotifierTest extends TestBase {
    Invocation invocation = Mockito.mock(Invocation.class);

    Stubbing stubbingFound = Mockito.mock(Stubbing.class);

    Collection<Stubbing> allStubbings = Mockito.mock(Collection.class);

    CreationSettings creationSettings = Mockito.mock(CreationSettings.class);

    @Test
    public void does_not_do_anything_when_list_is_empty() {
        // given
        Mockito.doReturn(emptyList()).when(creationSettings).getStubbingLookupListeners();
        // when
        StubbingLookupNotifier.notifyStubbedAnswerLookup(invocation, stubbingFound, allStubbings, creationSettings);
        // then expect nothing to happen
    }

    @Test
    public void call_on_stubbing_lookup_method_of_listeners_with_correct_event() {
        // given
        StubbingLookupListener listener1 = Mockito.mock(StubbingLookupListener.class);
        StubbingLookupListener listener2 = Mockito.mock(StubbingLookupListener.class);
        List<StubbingLookupListener> listeners = Lists.newArrayList(listener1, listener2);
        Mockito.doReturn(listeners).when(creationSettings).getStubbingLookupListeners();
        // when
        StubbingLookupNotifier.notifyStubbedAnswerLookup(invocation, stubbingFound, allStubbings, creationSettings);
        // then
        Mockito.verify(listener1).onStubbingLookup(ArgumentMatchers.argThat(new StubbingLookupNotifierTest.EventArgumentMatcher()));
        Mockito.verify(listener2).onStubbingLookup(ArgumentMatchers.argThat(new StubbingLookupNotifierTest.EventArgumentMatcher()));
    }

    class EventArgumentMatcher implements ArgumentMatcher<StubbingLookupNotifier.Event> {
        @Override
        public boolean matches(StubbingLookupNotifier.Event argument) {
            return ((((invocation) == (argument.getInvocation())) && ((stubbingFound) == (argument.getStubbingFound()))) && ((allStubbings) == (argument.getAllStubbings()))) && ((creationSettings) == (argument.getMockSettings()));
        }
    }
}

