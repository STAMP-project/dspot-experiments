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
package org.sonar.server.project;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(DataProviderRunner.class)
public class ProjectLifeCycleListenersImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ProjectLifeCycleListener listener1 = Mockito.mock(ProjectLifeCycleListener.class);

    private ProjectLifeCycleListener listener2 = Mockito.mock(ProjectLifeCycleListener.class);

    private ProjectLifeCycleListener listener3 = Mockito.mock(ProjectLifeCycleListener.class);

    private ProjectLifeCycleListenersImpl underTestNoListeners = new ProjectLifeCycleListenersImpl();

    private ProjectLifeCycleListenersImpl underTestWithListeners = new ProjectLifeCycleListenersImpl(new ProjectLifeCycleListener[]{ listener1, listener2, listener3 });

    @Test
    public void onProjectsDeleted_throws_NPE_if_set_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("projects can't be null");
        underTestWithListeners.onProjectsDeleted(null);
    }

    @Test
    public void onProjectsDeleted_throws_NPE_if_set_is_null_even_if_no_listeners() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("projects can't be null");
        underTestNoListeners.onProjectsDeleted(null);
    }

    @Test
    public void onProjectsDeleted_has_no_effect_if_set_is_empty() {
        underTestNoListeners.onProjectsDeleted(Collections.emptySet());
        underTestWithListeners.onProjectsDeleted(Collections.emptySet());
        Mockito.verifyZeroInteractions(listener1, listener2, listener3);
    }

    @Test
    public void onProjectBranchesDeleted_throws_NPE_if_set_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("projects can't be null");
        underTestWithListeners.onProjectBranchesDeleted(null);
    }

    @Test
    public void onProjectBranchesDeleted_throws_NPE_if_set_is_null_even_if_no_listeners() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("projects can't be null");
        underTestNoListeners.onProjectBranchesDeleted(null);
    }

    @Test
    public void onProjectBranchesDeleted_has_no_effect_if_set_is_empty() {
        underTestNoListeners.onProjectBranchesDeleted(Collections.emptySet());
        underTestWithListeners.onProjectBranchesDeleted(Collections.emptySet());
        Mockito.verifyZeroInteractions(listener1, listener2, listener3);
    }

    // SDSDS
    @Test
    public void onProjectsRekeyed_throws_NPE_if_set_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("rekeyedProjects can't be null");
        underTestWithListeners.onProjectsRekeyed(null);
    }

    @Test
    public void onProjectsRekeyed_throws_NPE_if_set_is_null_even_if_no_listeners() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("rekeyedProjects can't be null");
        underTestNoListeners.onProjectsRekeyed(null);
    }

    @Test
    public void onProjectsRekeyed_has_no_effect_if_set_is_empty() {
        underTestNoListeners.onProjectsRekeyed(Collections.emptySet());
        underTestWithListeners.onProjectsRekeyed(Collections.emptySet());
        Mockito.verifyZeroInteractions(listener1, listener2, listener3);
    }

    private static int counter = 3989;
}

