/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.workspace;


import com.google.inject.Provider;
import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.util.ArrayOf;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.api.parts.Perspective;
import org.eclipse.che.ide.api.parts.PerspectiveManager;
import org.eclipse.che.ide.menu.MainMenuPresenter;
import org.eclipse.che.ide.menu.StatusPanelGroupPresenter;
import org.eclipse.che.ide.ui.toolbar.ToolbarPresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit tests for the {@link org.eclipse.che.ide.workspace.WorkspacePresenter}.
 *
 * @author Evgen Vidolob
 * @author Vlad Zhukovskyi
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkspacePresenterPersistenceTest {
    @Mock
    private WorkspaceView workspaceView;

    @Mock
    private Perspective perspective1;

    @Mock
    private Perspective perspective2;

    @Mock
    private MainMenuPresenter mainMenuPresenter;

    @Mock
    private StatusPanelGroupPresenter statusPanelGroupPresenter;

    @Mock
    private ToolbarPresenter toolbarPresenter;

    @Mock
    private PromiseProvider promiseProvider;

    private WorkspacePresenter presenter;

    @Mock
    private Provider<PerspectiveManager> perspectiveManagerProvider;

    private PerspectiveManager perspectiveManager;

    @Test
    public void shouldStorePerspectives() throws Exception {
        Mockito.when(perspective1.getState()).thenReturn(Json.createObject());
        Mockito.when(perspective2.getState()).thenReturn(Json.createObject());
        JsonObject state = presenter.getState();
        JsonObject perspectives = state.getObject("perspectives");
        assertThat(perspectives).isNotNull();
        assertThat(perspectives.getObject("perspective1")).isNotNull();
    }

    @Test
    public void shouldRestoreStorePerspectives() throws Exception {
        JsonObject state = Json.createObject();
        state.put("currentPerspectiveId", "perspective2");
        JsonObject perspectives = Json.createObject();
        state.put("perspectives", perspectives);
        JsonObject perspective1State = Json.createObject();
        perspectives.put("perspective1", perspective1State);
        Mockito.when(promiseProvider.all2(ArgumentMatchers.any(ArrayOf.class))).thenReturn(Mockito.mock(Promise.class));
        presenter.loadState(state);
        Mockito.verify(perspective1).loadState(perspective1State);
    }
}

