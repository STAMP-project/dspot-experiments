/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.qualitygate.changeevent;


import Issue.RESOLUTION_FALSE_POSITIVE;
import Issue.RESOLUTION_FIXED;
import Issue.RESOLUTION_REMOVED;
import Issue.RESOLUTION_WONT_FIX;
import Issue.STATUS_CLOSED;
import Issue.STATUS_CONFIRMED;
import Issue.STATUS_OPEN;
import Issue.STATUS_REOPENED;
import Issue.STATUS_RESOLVED;
import LoggerLevel.TRACE;
import LoggerLevel.WARN;
import QGChangeEventListener.Status.CONFIRMED;
import QGChangeEventListener.Status.OPEN;
import QGChangeEventListener.Status.REOPENED;
import QGChangeEventListener.Status.RESOLVED_FIXED;
import QGChangeEventListener.Status.RESOLVED_FP;
import QGChangeEventListener.Status.RESOLVED_WF;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.qualitygate.changeevent.QGChangeEventListener.ChangedIssue;
import org.sonar.server.qualitygate.changeevent.QGChangeEventListenersImpl.ChangedIssueImpl;


public class QGChangeEventListenersImplTest {
    @Rule
    public LogTester logTester = new LogTester();

    private QGChangeEventListener listener1 = Mockito.mock(QGChangeEventListener.class);

    private QGChangeEventListener listener2 = Mockito.mock(QGChangeEventListener.class);

    private QGChangeEventListener listener3 = Mockito.mock(QGChangeEventListener.class);

    private List<QGChangeEventListener> listeners = Arrays.asList(listener1, listener2, listener3);

    private String component1Uuid = RandomStringUtils.randomAlphabetic(6);

    private ComponentDto component1 = QGChangeEventListenersImplTest.newComponentDto(component1Uuid);

    private DefaultIssue component1Issue = QGChangeEventListenersImplTest.newDefaultIssue(component1Uuid);

    private List<DefaultIssue> oneIssueOnComponent1 = Collections.singletonList(component1Issue);

    private QGChangeEvent component1QGChangeEvent = QGChangeEventListenersImplTest.newQGChangeEvent(component1);

    private InOrder inOrder = Mockito.inOrder(listener1, listener2, listener3);

    private QGChangeEventListenersImpl underTest = new QGChangeEventListenersImpl(new QGChangeEventListener[]{ listener1, listener2, listener3 });

    @Test
    public void broadcastOnIssueChange_has_no_effect_when_issues_are_empty() {
        underTest.broadcastOnIssueChange(Collections.emptyList(), Collections.singletonList(component1QGChangeEvent));
        Mockito.verifyZeroInteractions(listener1, listener2, listener3);
    }

    @Test
    public void broadcastOnIssueChange_has_no_effect_when_no_changeEvent() {
        underTest.broadcastOnIssueChange(oneIssueOnComponent1, Collections.emptySet());
        Mockito.verifyZeroInteractions(listener1, listener2, listener3);
    }

    @Test
    public void broadcastOnIssueChange_passes_same_arguments_to_all_listeners_in_order_of_addition_to_constructor() {
        underTest.broadcastOnIssueChange(oneIssueOnComponent1, Collections.singletonList(component1QGChangeEvent));
        ArgumentCaptor<Set<ChangedIssue>> changedIssuesCaptor = QGChangeEventListenersImplTest.newSetCaptor();
        inOrder.verify(listener1).onIssueChanges(ArgumentMatchers.same(component1QGChangeEvent), changedIssuesCaptor.capture());
        Set<ChangedIssue> changedIssues = changedIssuesCaptor.getValue();
        inOrder.verify(listener2).onIssueChanges(ArgumentMatchers.same(component1QGChangeEvent), ArgumentMatchers.same(changedIssues));
        inOrder.verify(listener3).onIssueChanges(ArgumentMatchers.same(component1QGChangeEvent), ArgumentMatchers.same(changedIssues));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void broadcastOnIssueChange_calls_all_listeners_even_if_one_throws_an_exception() {
        QGChangeEventListener failingListener = new QGChangeEventListener[]{ listener1, listener2, listener3 }[new Random().nextInt(3)];
        Mockito.doThrow(new RuntimeException("Faking an exception thrown by onChanges")).when(failingListener).onIssueChanges(ArgumentMatchers.any(), ArgumentMatchers.any());
        underTest.broadcastOnIssueChange(oneIssueOnComponent1, Collections.singletonList(component1QGChangeEvent));
        ArgumentCaptor<Set<ChangedIssue>> changedIssuesCaptor = QGChangeEventListenersImplTest.newSetCaptor();
        inOrder.verify(listener1).onIssueChanges(ArgumentMatchers.same(component1QGChangeEvent), changedIssuesCaptor.capture());
        Set<ChangedIssue> changedIssues = changedIssuesCaptor.getValue();
        inOrder.verify(listener2).onIssueChanges(ArgumentMatchers.same(component1QGChangeEvent), ArgumentMatchers.same(changedIssues));
        inOrder.verify(listener3).onIssueChanges(ArgumentMatchers.same(component1QGChangeEvent), ArgumentMatchers.same(changedIssues));
        inOrder.verifyNoMoreInteractions();
        assertThat(logTester.logs()).hasSize(4);
        assertThat(logTester.logs(WARN)).hasSize(1);
    }

    @Test
    public void broadcastOnIssueChange_stops_calling_listeners_when_one_throws_an_ERROR() {
        Mockito.doThrow(new Error("Faking an error thrown by a listener")).when(listener2).onIssueChanges(ArgumentMatchers.any(), ArgumentMatchers.any());
        underTest.broadcastOnIssueChange(oneIssueOnComponent1, Collections.singletonList(component1QGChangeEvent));
        ArgumentCaptor<Set<ChangedIssue>> changedIssuesCaptor = QGChangeEventListenersImplTest.newSetCaptor();
        inOrder.verify(listener1).onIssueChanges(ArgumentMatchers.same(component1QGChangeEvent), changedIssuesCaptor.capture());
        Set<ChangedIssue> changedIssues = changedIssuesCaptor.getValue();
        inOrder.verify(listener2).onIssueChanges(ArgumentMatchers.same(component1QGChangeEvent), ArgumentMatchers.same(changedIssues));
        inOrder.verifyNoMoreInteractions();
        assertThat(logTester.logs()).hasSize(3);
        assertThat(logTester.logs(WARN)).hasSize(1);
    }

    @Test
    public void broadcastOnIssueChange_logs_each_listener_call_at_TRACE_level() {
        underTest.broadcastOnIssueChange(oneIssueOnComponent1, Collections.singletonList(component1QGChangeEvent));
        assertThat(logTester.logs()).hasSize(3);
        List<String> traceLogs = logTester.logs(TRACE);
        assertThat(traceLogs).hasSize(3).containsOnly((((("calling onChange() on listener " + (listener1.getClass().getName())) + " for events ") + (component1QGChangeEvent.toString())) + "..."), (((("calling onChange() on listener " + (listener2.getClass().getName())) + " for events ") + (component1QGChangeEvent.toString())) + "..."), (((("calling onChange() on listener " + (listener3.getClass().getName())) + " for events ") + (component1QGChangeEvent.toString())) + "..."));
    }

    @Test
    public void broadcastOnIssueChange_passes_immutable_set_of_ChangedIssues() {
        QGChangeEventListenersImpl underTest = new QGChangeEventListenersImpl(new QGChangeEventListener[]{ listener1 });
        underTest.broadcastOnIssueChange(oneIssueOnComponent1, Collections.singletonList(component1QGChangeEvent));
        ArgumentCaptor<Set<ChangedIssue>> changedIssuesCaptor = QGChangeEventListenersImplTest.newSetCaptor();
        inOrder.verify(listener1).onIssueChanges(ArgumentMatchers.same(component1QGChangeEvent), changedIssuesCaptor.capture());
        assertThat(changedIssuesCaptor.getValue()).isInstanceOf(ImmutableSet.class);
    }

    @Test
    public void broadcastOnIssueChange_has_no_effect_when_no_listener() {
        QGChangeEventListenersImpl underTest = new QGChangeEventListenersImpl();
        underTest.broadcastOnIssueChange(oneIssueOnComponent1, Collections.singletonList(component1QGChangeEvent));
        Mockito.verifyZeroInteractions(listener1, listener2, listener3);
    }

    @Test
    public void broadcastOnIssueChange_calls_listener_for_each_component_uuid_with_at_least_one_QGChangeEvent() {
        // component2 has multiple issues
        ComponentDto component2 = QGChangeEventListenersImplTest.newComponentDto(((component1Uuid) + "2"));
        DefaultIssue[] component2Issues = new DefaultIssue[]{ QGChangeEventListenersImplTest.newDefaultIssue(component2.uuid()), QGChangeEventListenersImplTest.newDefaultIssue(component2.uuid()) };
        QGChangeEvent component2QGChangeEvent = QGChangeEventListenersImplTest.newQGChangeEvent(component2);
        // component 3 has multiple QGChangeEvent and only one issue
        ComponentDto component3 = QGChangeEventListenersImplTest.newComponentDto(((component1Uuid) + "3"));
        DefaultIssue component3Issue = QGChangeEventListenersImplTest.newDefaultIssue(component3.uuid());
        QGChangeEvent[] component3QGChangeEvents = new QGChangeEvent[]{ QGChangeEventListenersImplTest.newQGChangeEvent(component3), QGChangeEventListenersImplTest.newQGChangeEvent(component3) };
        // component 4 has multiple QGChangeEvent and multiples issues
        ComponentDto component4 = QGChangeEventListenersImplTest.newComponentDto(((component1Uuid) + "4"));
        DefaultIssue[] component4Issues = new DefaultIssue[]{ QGChangeEventListenersImplTest.newDefaultIssue(component4.uuid()), QGChangeEventListenersImplTest.newDefaultIssue(component4.uuid()) };
        QGChangeEvent[] component4QGChangeEvents = new QGChangeEvent[]{ QGChangeEventListenersImplTest.newQGChangeEvent(component4), QGChangeEventListenersImplTest.newQGChangeEvent(component4) };
        // component 5 has no QGChangeEvent but one issue
        ComponentDto component5 = QGChangeEventListenersImplTest.newComponentDto(((component1Uuid) + "5"));
        DefaultIssue component5Issue = QGChangeEventListenersImplTest.newDefaultIssue(component5.uuid());
        List<DefaultIssue> issues = Stream.of(Stream.of(component1Issue), Arrays.stream(component2Issues), Stream.of(component3Issue), Arrays.stream(component4Issues), Stream.of(component5Issue)).flatMap(( s) -> s).collect(Collectors.toList());
        List<DefaultIssue> changedIssues = QGChangeEventListenersImplTest.randomizedList(issues);
        List<QGChangeEvent> qgChangeEvents = Stream.of(Stream.of(component1QGChangeEvent), Stream.of(component2QGChangeEvent), Arrays.stream(component3QGChangeEvents), Arrays.stream(component4QGChangeEvents)).flatMap(( s) -> s).collect(Collectors.toList());
        underTest.broadcastOnIssueChange(changedIssues, QGChangeEventListenersImplTest.randomizedList(qgChangeEvents));
        listeners.forEach(( listener) -> {
            verifyListenerCalled(listener, component1QGChangeEvent, component1Issue);
            verifyListenerCalled(listener, component2QGChangeEvent, component2Issues);
            Arrays.stream(component3QGChangeEvents).forEach(( component3QGChangeEvent) -> verifyListenerCalled(listener, component3QGChangeEvent, component3Issue));
            Arrays.stream(component4QGChangeEvents).forEach(( component4QGChangeEvent) -> verifyListenerCalled(listener, component4QGChangeEvent, component4Issues));
        });
        Mockito.verifyNoMoreInteractions(listener1, listener2, listener3);
    }

    @Test
    public void test_status_mapping() {
        assertThat(ChangedIssueImpl.statusOf(new DefaultIssue().setStatus(STATUS_OPEN))).isEqualTo(OPEN);
        assertThat(ChangedIssueImpl.statusOf(new DefaultIssue().setStatus(STATUS_REOPENED))).isEqualTo(REOPENED);
        assertThat(ChangedIssueImpl.statusOf(new DefaultIssue().setStatus(STATUS_CONFIRMED))).isEqualTo(CONFIRMED);
        assertThat(ChangedIssueImpl.statusOf(new DefaultIssue().setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FALSE_POSITIVE))).isEqualTo(RESOLVED_FP);
        assertThat(ChangedIssueImpl.statusOf(new DefaultIssue().setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_WONT_FIX))).isEqualTo(RESOLVED_WF);
        assertThat(ChangedIssueImpl.statusOf(new DefaultIssue().setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FIXED))).isEqualTo(RESOLVED_FIXED);
        try {
            ChangedIssueImpl.statusOf(new DefaultIssue().setStatus(STATUS_CLOSED));
            Assert.fail("Expected exception");
        } catch (Exception e) {
            assertThat(e).hasMessage("Unexpected status: CLOSED");
        }
        try {
            ChangedIssueImpl.statusOf(new DefaultIssue().setStatus(STATUS_RESOLVED));
            Assert.fail("Expected exception");
        } catch (Exception e) {
            assertThat(e).hasMessage("A resolved issue should have a resolution");
        }
        try {
            ChangedIssueImpl.statusOf(new DefaultIssue().setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_REMOVED));
            Assert.fail("Expected exception");
        } catch (Exception e) {
            assertThat(e).hasMessage("Unexpected resolution for a resolved issue: REMOVED");
        }
    }

    private static final String[] POSSIBLE_STATUSES = Arrays.asList(STATUS_CONFIRMED, STATUS_REOPENED, STATUS_RESOLVED).stream().toArray(String[]::new);

    private static int issueIdCounter = 0;
}

