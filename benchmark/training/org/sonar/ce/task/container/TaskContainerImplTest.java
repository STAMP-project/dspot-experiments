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
package org.sonar.ce.task.container;


import org.junit.Test;
import org.mockito.Mockito;
import org.picocontainer.Startable;
import org.sonar.core.platform.ComponentContainer;
import org.sonar.core.platform.ContainerPopulator;


public class TaskContainerImplTest {
    private ComponentContainer parent = new ComponentContainer();

    private ContainerPopulator<TaskContainer> populator = Mockito.spy(new TaskContainerImplTest.DummyContainerPopulator());

    @Test(expected = NullPointerException.class)
    public void constructor_fails_fast_on_null_container() {
        new TaskContainerImpl(null, populator);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_fails_fast_on_null_item() {
        new TaskContainerImpl(new ComponentContainer(), null);
    }

    @Test
    public void calls_method_populateContainer_of_passed_in_populator() {
        TaskContainerImpl ceContainer = new TaskContainerImpl(parent, populator);
        Mockito.verify(populator).populateContainer(ceContainer);
    }

    @Test
    public void ce_container_is_not_child_of_specified_container() {
        TaskContainerImpl ceContainer = new TaskContainerImpl(parent, populator);
        assertThat(parent.getChildren()).isEmpty();
        Mockito.verify(populator).populateContainer(ceContainer);
    }

    @Test
    public void bootup_starts_components_lazily_unless_they_are_annotated_with_EagerStart() {
        final TaskContainerImplTest.DefaultStartable defaultStartable = new TaskContainerImplTest.DefaultStartable();
        final TaskContainerImplTest.EagerStartable eagerStartable = new TaskContainerImplTest.EagerStartable();
        TaskContainerImpl ceContainer = new TaskContainerImpl(parent, ( container) -> {
            container.add(defaultStartable);
            container.add(eagerStartable);
        });
        ceContainer.bootup();
        assertThat(defaultStartable.startCalls).isEqualTo(0);
        assertThat(defaultStartable.stopCalls).isEqualTo(0);
        assertThat(eagerStartable.startCalls).isEqualTo(1);
        assertThat(eagerStartable.stopCalls).isEqualTo(0);
    }

    @Test
    public void close_stops_started_components() {
        final TaskContainerImplTest.DefaultStartable defaultStartable = new TaskContainerImplTest.DefaultStartable();
        final TaskContainerImplTest.EagerStartable eagerStartable = new TaskContainerImplTest.EagerStartable();
        TaskContainerImpl ceContainer = new TaskContainerImpl(parent, ( container) -> {
            container.add(defaultStartable);
            container.add(eagerStartable);
        });
        ceContainer.bootup();
        ceContainer.close();
        assertThat(defaultStartable.startCalls).isEqualTo(0);
        assertThat(defaultStartable.stopCalls).isEqualTo(0);
        assertThat(eagerStartable.startCalls).isEqualTo(1);
        assertThat(eagerStartable.stopCalls).isEqualTo(1);
    }

    public static class DefaultStartable implements Startable {
        protected int startCalls = 0;

        protected int stopCalls = 0;

        @Override
        public void start() {
            (startCalls)++;
        }

        @Override
        public void stop() {
            (stopCalls)++;
        }
    }

    @EagerStart
    public static class EagerStartable extends TaskContainerImplTest.DefaultStartable {}

    private static class DummyContainerPopulator implements ContainerPopulator<TaskContainer> {
        @Override
        public void populateContainer(TaskContainer container) {
        }
    }
}

