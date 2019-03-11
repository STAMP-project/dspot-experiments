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
package org.sonar.process;


import java.util.Objects;
import org.junit.Test;

import static State.OPERATIONAL;
import static State.STARTED;
import static State.STARTING;
import static State.values;


public class LifecycleTest {
    @Test
    public void equals_and_hashcode() {
        Lifecycle init = new Lifecycle();
        assertThat(init.equals(init)).isTrue();
        assertThat(init.equals(new Lifecycle())).isTrue();
        assertThat(init.equals("INIT")).isFalse();
        assertThat(init.equals(null)).isFalse();
        assertThat(init.hashCode()).isEqualTo(new Lifecycle().hashCode());
        // different state
        Lifecycle stopping = new Lifecycle();
        stopping.tryToMoveTo(State.STARTING);
        assertThat(stopping).isNotEqualTo(init);
    }

    @Test
    public void try_to_move_does_not_support_jumping_states() {
        Lifecycle lifecycle = new Lifecycle();
        assertThat(lifecycle.getState()).isEqualTo(State.INIT);
        assertThat(lifecycle.tryToMoveTo(State.STARTED)).isFalse();
        assertThat(lifecycle.getState()).isEqualTo(State.INIT);
        assertThat(lifecycle.tryToMoveTo(State.STARTING)).isTrue();
        assertThat(lifecycle.getState()).isEqualTo(State.STARTING);
    }

    @Test
    public void no_state_can_not_move_to_itself() {
        for (Lifecycle.State state : values()) {
            assertThat(LifecycleTest.newLifeCycle(state).tryToMoveTo(state)).isFalse();
        }
    }

    @Test
    public void can_move_to_STOPPING_from_STARTING_STARTED_OPERATIONAL_only() {
        for (Lifecycle.State state : values()) {
            boolean tryToMoveTo = LifecycleTest.newLifeCycle(state).tryToMoveTo(State.STOPPING);
            if (((state == (STARTING)) || (state == (STARTED))) || (state == (OPERATIONAL))) {
                assertThat(tryToMoveTo).describedAs(("from state " + state)).isTrue();
            } else {
                assertThat(tryToMoveTo).describedAs(("from state " + state)).isFalse();
            }
        }
    }

    @Test
    public void can_move_to_OPERATIONAL_from_STARTED_only() {
        for (Lifecycle.State state : values()) {
            boolean tryToMoveTo = LifecycleTest.newLifeCycle(state).tryToMoveTo(State.OPERATIONAL);
            if (state == (STARTED)) {
                assertThat(tryToMoveTo).describedAs(("from state " + state)).isTrue();
            } else {
                assertThat(tryToMoveTo).describedAs(("from state " + state)).isFalse();
            }
        }
    }

    @Test
    public void can_move_to_STARTING_from_RESTARTING() {
        assertThat(LifecycleTest.newLifeCycle(State.RESTARTING).tryToMoveTo(State.STARTING)).isTrue();
    }

    private static final class Transition {
        private final Lifecycle.State from;

        private final Lifecycle.State to;

        private Transition(Lifecycle.State from, Lifecycle.State to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            LifecycleTest.Transition that = ((LifecycleTest.Transition) (o));
            return ((from) == (that.from)) && ((to) == (that.to));
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public String toString() {
            return ((("Transition{" + (from)) + " => ") + (to)) + '}';
        }
    }
}

