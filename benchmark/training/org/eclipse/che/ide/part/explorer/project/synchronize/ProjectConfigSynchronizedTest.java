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
package org.eclipse.che.ide.part.explorer.project.synchronize;


import Project.ProblemProjectMarker;
import Project.ProjectRequest;
import com.google.common.base.Optional;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.Map;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.workspace.shared.dto.SourceStorageDto;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.marker.Marker;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;
import org.eclipse.che.ide.resources.tree.ResourceNode;
import org.eclipse.che.ide.ui.dialogs.CancelCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.eclipse.che.ide.ui.smartTree.NodeLoader;
import org.eclipse.che.ide.ui.smartTree.Tree;
import org.eclipse.che.ide.ui.smartTree.event.BeforeLoadEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(GwtMockitoTestRunner.class)
public class ProjectConfigSynchronizedTest {
    private static final String PROJECT_NAME = "project name";

    private static final String PROJECT_LOCATION = "/project/location";

    private static final String SYNCH_DIALOG_TITLE = "Synchronize dialog title";

    private static final String SYNCH_DIALOG_CONTENT = "Synchronize dialog content";

    private static final String IMPORT_BUTTON = "Import";

    private static final String REMOVE_BUTTON = "Remove";

    @Mock
    private AppContext appContext;

    @Mock
    private ProjectExplorerPresenter projectExplorerPresenter;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private CoreLocalizationConstant locale;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private ChangeLocationWidget changeLocationWidget;

    @Mock
    private Optional<Marker> problemMarker;

    @Mock
    private ProblemProjectMarker problemProjectMarker;

    @Mock
    private ConfirmDialog confirmDialog;

    @Mock
    private ConfirmDialog changeConfirmDialog;

    @Mock
    private Promise<Void> deleteProjectPromise;

    @Mock
    private Promise<Project> projectPromise;

    @Mock
    private SourceStorageDto sourceStorage;

    @Mock
    private BeforeLoadEvent beforeLoadEvent;

    @Mock
    private Container wsRoot;

    @Mock
    private ProjectRequest projectRequest;

    @Mock
    private Tree tree;

    @Mock
    private NodeLoader nodeLoader;

    @Mock
    private ResourceNode requestedNode;

    @Mock
    private Project resource;

    @Captor
    private ArgumentCaptor<ConfirmCallback> confirmCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<CancelCallback> cancelCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Operation<Void>> projectDeleted;

    @Captor
    private ArgumentCaptor<BeforeLoadEvent.BeforeLoadHandler> beforeLoadHandlerArgumentCaptor;

    private Map<Integer, String> problems;

    @Test
    public void dialogIsNotShownIfProjectHasNotMarkers() throws Exception {
        Mockito.when(problemMarker.isPresent()).thenReturn(false);
        subscribeToOnBeforeLoadNodeEvent();
        Mockito.verify(confirmDialog, Mockito.never()).show();
    }

    @Test
    public void dialogIsNotShownIfNoProjectProblem() throws Exception {
        problems.clear();
        subscribeToOnBeforeLoadNodeEvent();
        Mockito.verify(confirmDialog, Mockito.never()).show();
    }

    @Test
    public void removeButtonIsClicked() throws Exception {
        String projectRemoved = "project removed";
        Mockito.when(resource.delete()).thenReturn(deleteProjectPromise);
        Mockito.when(deleteProjectPromise.then(ArgumentMatchers.<Operation<Void>>any())).thenReturn(deleteProjectPromise);
        Mockito.when(locale.projectRemoved(ProjectConfigSynchronizedTest.PROJECT_NAME)).thenReturn(projectRemoved);
        subscribeToOnBeforeLoadNodeEvent();
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.eq(ProjectConfigSynchronizedTest.SYNCH_DIALOG_TITLE), ArgumentMatchers.eq(ProjectConfigSynchronizedTest.SYNCH_DIALOG_CONTENT), ArgumentMatchers.eq(ProjectConfigSynchronizedTest.IMPORT_BUTTON), ArgumentMatchers.eq(ProjectConfigSynchronizedTest.REMOVE_BUTTON), confirmCallbackArgumentCaptor.capture(), cancelCallbackArgumentCaptor.capture());
        Mockito.verify(confirmDialog).show();
        cancelCallbackArgumentCaptor.getValue().cancelled();
        Mockito.verify(resource).delete();
        Mockito.verify(deleteProjectPromise).then(projectDeleted.capture());
        projectDeleted.getValue().apply(null);
        Mockito.verify(notificationManager).notify(projectRemoved, SUCCESS, EMERGE_MODE);
    }

    @Test
    public void importButtonIsClicked() throws Exception {
        subscribeToOnBeforeLoadNodeEvent();
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.eq(ProjectConfigSynchronizedTest.SYNCH_DIALOG_TITLE), ArgumentMatchers.eq(ProjectConfigSynchronizedTest.SYNCH_DIALOG_CONTENT), ArgumentMatchers.eq(ProjectConfigSynchronizedTest.IMPORT_BUTTON), ArgumentMatchers.eq(ProjectConfigSynchronizedTest.REMOVE_BUTTON), confirmCallbackArgumentCaptor.capture(), cancelCallbackArgumentCaptor.capture());
        Mockito.verify(confirmDialog).show();
        confirmCallbackArgumentCaptor.getValue().accepted();
        Mockito.verify(wsRoot).importProject();
        Mockito.verify(projectRequest).withBody(resource);
        Mockito.verify(projectRequest).send();
    }

    @Test
    public void changeLocationWindowShouldBeShown() throws Exception {
        String newLocation = "new/location";
        Mockito.when(sourceStorage.getLocation()).thenReturn(null);
        Mockito.when(changeLocationWidget.getText()).thenReturn(newLocation);
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.anyString(), ArgumentMatchers.<IsWidget>any(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(changeConfirmDialog);
        subscribeToOnBeforeLoadNodeEvent();
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.eq(ProjectConfigSynchronizedTest.SYNCH_DIALOG_TITLE), ArgumentMatchers.eq(ProjectConfigSynchronizedTest.SYNCH_DIALOG_CONTENT), ArgumentMatchers.eq(ProjectConfigSynchronizedTest.IMPORT_BUTTON), ArgumentMatchers.eq(ProjectConfigSynchronizedTest.REMOVE_BUTTON), confirmCallbackArgumentCaptor.capture(), cancelCallbackArgumentCaptor.capture());
        Mockito.verify(confirmDialog).show();
        confirmCallbackArgumentCaptor.getValue().accepted();
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.eq(ProjectConfigSynchronizedTest.SYNCH_DIALOG_TITLE), ArgumentMatchers.eq(changeLocationWidget), confirmCallbackArgumentCaptor.capture(), ArgumentMatchers.eq(null));
        Mockito.verify(changeConfirmDialog).show();
        confirmCallbackArgumentCaptor.getValue().accepted();
        Mockito.verify(sourceStorage).setLocation(newLocation);
        Mockito.verify(sourceStorage).setType("github");
        Mockito.verify(wsRoot).importProject();
        Mockito.verify(projectRequest).withBody(resource);
        Mockito.verify(projectRequest).send();
    }
}

