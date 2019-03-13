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
package org.eclipse.che.ide.newresource;


import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.Folder;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for the {@link NewFolderAction}.
 *
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class NewFolderActionTest {
    @Mock
    CoreLocalizationConstant coreLocalizationConstant;

    @Mock
    Resources resources;

    @Mock
    DialogFactory dialogFactory;

    @Mock
    EventBus eventBus;

    @Mock
    AppContext appContext;

    @Mock
    NotificationManager notificationManager;

    @Mock
    Provider<EditorAgent> editorAgentProvider;

    @Mock
    Resource file;

    @Mock
    Container parent;

    @Mock
    Promise<Folder> folderPromise;

    private NewFolderAction action;

    @Test
    public void testShouldCreateFolderIfSelectedFile() throws Exception {
        Mockito.when(file.getParent()).thenReturn(parent);
        Mockito.when(appContext.getResource()).thenReturn(file);
        Mockito.when(parent.newFolder(ArgumentMatchers.anyString())).thenReturn(folderPromise);
        Mockito.when(folderPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(folderPromise);
        Mockito.when(folderPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(folderPromise);
        action.createFolder("name");
        Mockito.verify(parent).newFolder(ArgumentMatchers.eq("name"));
    }

    @Test
    public void testShouldCreateFolderIfSelectedContainer() throws Exception {
        Mockito.when(appContext.getResource()).thenReturn(parent);
        Mockito.when(parent.newFolder(ArgumentMatchers.anyString())).thenReturn(folderPromise);
        Mockito.when(folderPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(folderPromise);
        Mockito.when(folderPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(folderPromise);
        action.createFolder("name");
        Mockito.verify(parent).newFolder(ArgumentMatchers.eq("name"));
    }

    @Test(expected = IllegalStateException.class)
    public void testShouldThrowExceptionIfFileDoesNotContainParent() throws Exception {
        Mockito.when(appContext.getResource()).thenReturn(file);
        Mockito.when(file.getParent()).thenReturn(null);
        action.createFolder("name");
    }
}

