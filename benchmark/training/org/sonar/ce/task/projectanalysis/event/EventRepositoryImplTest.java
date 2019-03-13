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
package org.sonar.ce.task.projectanalysis.event;


import Component.Type;
import java.util.Arrays;
import org.junit.Test;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.ViewsComponent;


public class EventRepositoryImplTest {
    private static final Component COMPONENT_1 = EventRepositoryImplTest.newComponent(1);

    private static final Component COMPONENT_2 = EventRepositoryImplTest.newComponent(2);

    private static final Event EVENT_1 = Event.createProfile("event_1", null, null);

    private static final Event EVENT_2 = Event.createProfile("event_2", null, null);

    private EventRepositoryImpl underTest = new EventRepositoryImpl();

    @Test
    public void getEvents_returns_empty_iterable_when_repository_is_empty() {
        assertThat(underTest.getEvents(EventRepositoryImplTest.COMPONENT_1)).isEmpty();
    }

    @Test
    public void getEvents_discriminates_per_component() {
        underTest.add(EventRepositoryImplTest.COMPONENT_1, EventRepositoryImplTest.EVENT_1);
        underTest.add(EventRepositoryImplTest.COMPONENT_2, EventRepositoryImplTest.EVENT_2);
        assertThat(underTest.getEvents(EventRepositoryImplTest.COMPONENT_1)).extracting("name").containsExactly(EventRepositoryImplTest.EVENT_1.getName());
        assertThat(underTest.getEvents(EventRepositoryImplTest.COMPONENT_2)).extracting("name").containsExactly(EventRepositoryImplTest.EVENT_2.getName());
    }

    @Test(expected = NullPointerException.class)
    public void add_throws_NPE_if_component_arg_is_null() {
        underTest.add(null, EventRepositoryImplTest.EVENT_1);
    }

    @Test(expected = NullPointerException.class)
    public void add_throws_NPE_if_even_arg_is_null() {
        underTest.add(EventRepositoryImplTest.COMPONENT_1, null);
    }

    @Test
    public void add_throws_IAE_for_any_component_type_but_PROJECT() {
        Arrays.stream(Type.values()).filter(( type) -> type != Component.Type.PROJECT).map(( type) -> {
            if (type.isReportType()) {
                return ReportComponent.builder(type, 1).build();
            } else {
                return ViewsComponent.builder(type, 1).build();
            }
        }).forEach(( component) -> {
            try {
                underTest.add(component, EVENT_1);
                fail("should have raised an IAE");
            } catch ( e) {
                assertThat(e).hasMessage("Component must be of type PROJECT");
            }
        });
    }

    @Test
    public void can_add_and_retrieve_many_events_per_component() {
        underTest.add(EventRepositoryImplTest.COMPONENT_1, EventRepositoryImplTest.EVENT_1);
        underTest.add(EventRepositoryImplTest.COMPONENT_1, EventRepositoryImplTest.EVENT_2);
        assertThat(underTest.getEvents(EventRepositoryImplTest.COMPONENT_1)).extracting("name").containsOnly(EventRepositoryImplTest.EVENT_1.getName(), EventRepositoryImplTest.EVENT_2.getName());
    }
}

