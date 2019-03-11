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
package org.eclipse.che.ide.projecttype.wizard;


import Project.ProjectRequest;
import Resource.PROJECT;
import Wizard.CompleteCallback;
import com.google.common.base.Optional;
import java.util.Collections;
import org.eclipse.che.api.core.model.workspace.config.ProjectConfig;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.workspace.shared.dto.CommandDto;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandManager;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.Folder;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.resource.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Testing {@link ProjectWizard}.
 *
 * @author Artem Zatsarynnyi
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectWizardTest {
    private static final String PROJECT_NAME = "project1";

    @Mock
    private MutableProjectConfig dataObject;

    @Mock
    private CompleteCallback completeCallback;

    @Mock
    private AppContext appContext;

    @Mock
    private CommandManager commandManager;

    @Mock
    private Container workspaceRoot;

    @Mock
    private ProjectRequest createProjectRequest;

    @Mock
    private Promise<Project> createProjectPromise;

    @Mock
    private Project createdProject;

    @Mock
    private CommandDto command;

    @Mock
    private Promise<CommandImpl> createCommandPromise;

    @Mock
    private CommandImpl createdCommand;

    @Mock
    private Promise<Optional<Container>> optionalContainerPromise;

    @Mock
    private Project projectToUpdate;

    @Mock
    private Folder folderToUpdate;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private StatusNotification statusNotification;

    @Mock
    private CoreLocalizationConstant localizationConstant;

    @Mock
    private PromiseError promiseError;

    @Mock
    private Exception exception;

    @Captor
    private ArgumentCaptor<Operation<Project>> completeOperationCaptor;

    @Captor
    private ArgumentCaptor<Operation<CommandImpl>> completeAddCommandsOperationCaptor;

    @Captor
    private ArgumentCaptor<Operation<PromiseError>> failedOperationCaptor;

    @Captor
    private ArgumentCaptor<Operation<Optional<Container>>> optionalContainerCaptor;

    private ProjectWizard wizard;

    @Test
    public void shouldCreateProject() throws Exception {
        prepareWizard(CREATE);
        Mockito.when(workspaceRoot.newProject()).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.withBody(ArgumentMatchers.any(ProjectConfig.class))).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.send()).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        wizard.complete(completeCallback);
        Mockito.verify(createProjectPromise).then(completeOperationCaptor.capture());
        completeOperationCaptor.getValue().apply(createdProject);
        Mockito.verify(completeCallback).onCompleted();
    }

    @Test
    public void shouldInvokeCallbackWhenCreatingFailure() throws Exception {
        prepareWizard(CREATE);
        Mockito.when(workspaceRoot.newProject()).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.withBody(ArgumentMatchers.any(ProjectConfig.class))).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.send()).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(promiseError.getCause()).thenReturn(exception);
        wizard.complete(completeCallback);
        Mockito.verify(createProjectPromise).catchError(failedOperationCaptor.capture());
        failedOperationCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getCause();
        Mockito.verify(completeCallback).onFailure(ArgumentMatchers.eq(exception));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
    }

    @Test
    public void shouldImportProjectSuccessfully() throws Exception {
        prepareWizard(IMPORT);
        Mockito.when(workspaceRoot.newProject()).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.withBody(ArgumentMatchers.any(ProjectConfig.class))).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.send()).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.thenPromise(ArgumentMatchers.any(Function.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(promiseError.getCause()).thenReturn(exception);
        wizard.complete(completeCallback);
        Mockito.verify(createProjectPromise).then(completeOperationCaptor.capture());
        completeOperationCaptor.getValue().apply(createdProject);
        Mockito.verify(completeCallback).onCompleted();
    }

    @Test
    public void shouldImportProjectWithCommandSuccessfully() throws Exception {
        prepareWizard(IMPORT);
        Mockito.when(workspaceRoot.importProject()).thenReturn(createProjectRequest);
        Mockito.when(workspaceRoot.newProject()).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.withBody(ArgumentMatchers.any(ProjectConfig.class))).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.send()).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.thenPromise(ArgumentMatchers.any(Function.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(promiseError.getCause()).thenReturn(exception);
        Mockito.when(dataObject.getCommands()).thenReturn(Collections.singletonList(command));
        Mockito.when(command.getCommandLine()).thenReturn("echo 'Hello'");
        Mockito.when(commandManager.createCommand(ArgumentMatchers.any(CommandImpl.class))).thenReturn(createCommandPromise);
        Mockito.when(createCommandPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(createCommandPromise);
        Mockito.when(createCommandPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(createCommandPromise);
        wizard.complete(completeCallback);
        Mockito.verify(createProjectPromise).then(completeOperationCaptor.capture());
        completeOperationCaptor.getValue().apply(createdProject);
        Mockito.verify(createCommandPromise).then(completeAddCommandsOperationCaptor.capture());
        completeAddCommandsOperationCaptor.getValue().apply(createdCommand);
        Mockito.verify(completeCallback).onCompleted();
    }

    @Test
    public void shouldFailOnImportProject() throws Exception {
        prepareWizard(IMPORT);
        Mockito.when(workspaceRoot.newProject()).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.withBody(ArgumentMatchers.any(ProjectConfig.class))).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.send()).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.thenPromise(ArgumentMatchers.any(Function.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(promiseError.getCause()).thenReturn(exception);
        wizard.complete(completeCallback);
        Mockito.verify(createProjectPromise).catchError(failedOperationCaptor.capture());
        failedOperationCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getCause();
        Mockito.verify(completeCallback).onFailure(ArgumentMatchers.eq(exception));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
    }

    @Test
    public void shouldUpdateProjectConfig() throws Exception {
        prepareWizard(UPDATE);
        Mockito.when(workspaceRoot.getContainer(ArgumentMatchers.any(Path.class))).thenReturn(optionalContainerPromise);
        Mockito.when(projectToUpdate.getResourceType()).thenReturn(PROJECT);
        Mockito.when(projectToUpdate.update()).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.withBody(ArgumentMatchers.any(ProjectConfig.class))).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.send()).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        wizard.complete(completeCallback);
        Mockito.verify(optionalContainerPromise).then(optionalContainerCaptor.capture());
        optionalContainerCaptor.getValue().apply(Optional.of(((Container) (projectToUpdate))));
        Mockito.verify(createProjectPromise).then(completeOperationCaptor.capture());
        completeOperationCaptor.getValue().apply(createdProject);
        Mockito.verify(completeCallback).onCompleted();
        Mockito.verify(notificationManager).notify(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(PROGRESS), ArgumentMatchers.eq(FLOAT_MODE));
        Mockito.verify(statusNotification).setStatus(ArgumentMatchers.eq(SUCCESS));
    }

    @Test
    public void shouldFailUpdateProjectConfig() throws Exception {
        prepareWizard(UPDATE);
        Mockito.when(workspaceRoot.getContainer(ArgumentMatchers.any(Path.class))).thenReturn(optionalContainerPromise);
        Mockito.when(projectToUpdate.getResourceType()).thenReturn(PROJECT);
        Mockito.when(projectToUpdate.update()).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.withBody(ArgumentMatchers.any(ProjectConfig.class))).thenReturn(createProjectRequest);
        Mockito.when(createProjectRequest.send()).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(createProjectPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(createProjectPromise);
        Mockito.when(promiseError.getCause()).thenReturn(exception);
        wizard.complete(completeCallback);
        Mockito.verify(optionalContainerPromise).then(optionalContainerCaptor.capture());
        optionalContainerCaptor.getValue().apply(Optional.of(((Container) (projectToUpdate))));
        Mockito.verify(createProjectPromise).catchError(failedOperationCaptor.capture());
        failedOperationCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getCause();
        Mockito.verify(completeCallback).onFailure(ArgumentMatchers.eq(exception));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(PROGRESS), ArgumentMatchers.eq(FLOAT_MODE));
        Mockito.verify(statusNotification).setStatus(ArgumentMatchers.eq(FAIL));
    }

    @Test
    public void shouldCreateConfigForFolder() throws Exception {
        // prepareWizard(UPDATE);
        // 
        // 
        // when(workspaceRoot.getContainer(any(Path.class))).thenReturn(optionalContainerPromise);
        // when(folderToUpdate.getResourceType()).thenReturn(Resource.FOLDER);
        // when(folderToUpdate.toProject()).thenReturn(createProjectRequest);
        // 
        // when(createProjectRequest.withBody(any(ProjectConfig.class))).thenReturn(createProjectRequest);
        // when(createProjectRequest.send()).thenReturn(createProjectPromise);
        // 
        // when(createProjectPromise.then(any(Operation.class))).thenReturn(createProjectPromise);
        // 
        // when(createProjectPromise.catchError(any(Operation.class))).thenReturn(createProjectPromise);
        // 
        // wizard.complete(completeCallback);
        // 
        // verify(optionalContainerPromise).then(optionalContainerCaptor.capture());
        // optionalContainerCaptor.getValue().apply(Optional.of((Container)folderToUpdate));
        // 
        // verify(createProjectPromise).then(completeOperationCaptor.capture());
        // completeOperationCaptor.getValue().apply(createdProject);
        // 
        // verify(completeCallback).onCompleted();
    }

    @Test
    public void shouldFailCreateConfigForFolder() throws Exception {
        // prepareWizard(UPDATE);
        // 
        // 
        // when(workspaceRoot.getContainer(any(Path.class))).thenReturn(optionalContainerPromise);
        // when(folderToUpdate.getResourceType()).thenReturn(Resource.FOLDER);
        // when(folderToUpdate.toProject()).thenReturn(createProjectRequest);
        // 
        // when(createProjectRequest.withBody(any(ProjectConfig.class))).thenReturn(createProjectRequest);
        // when(createProjectRequest.send()).thenReturn(createProjectPromise);
        // 
        // when(createProjectPromise.then(any(Operation.class))).thenReturn(createProjectPromise);
        // 
        // when(createProjectPromise.catchError(any(Operation.class))).thenReturn(createProjectPromise);
        // when(promiseError.getCause()).thenReturn(exception);
        // 
        // wizard.complete(completeCallback);
        // 
        // verify(optionalContainerPromise).then(optionalContainerCaptor.capture());
        // optionalContainerCaptor.getValue().apply(Optional.of((Container)folderToUpdate));
        // 
        // verify(createProjectPromise).catchError(failedOperationCaptor.capture());
        // failedOperationCaptor.getValue().apply(promiseError);
        // 
        // verify(promiseError).getCause();
        // verify(completeCallback).onFailure(eq(exception));
    }
}

