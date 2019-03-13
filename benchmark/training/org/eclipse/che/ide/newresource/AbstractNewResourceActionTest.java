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
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.File;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for the {@link AbstractNewResourceAction}.
 *
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class AbstractNewResourceActionTest {
    @Mock
    DialogFactory dialogFactory;

    @Mock
    CoreLocalizationConstant coreLocalizationConstant;

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
    Promise<File> filePromise;

    private AbstractNewResourceAction action;

    @Test
    public void testShouldCreateFileIfSelectedFile() throws Exception {
        Mockito.when(file.getParent()).thenReturn(parent);
        Mockito.when(appContext.getResource()).thenReturn(file);
        Mockito.when(parent.newFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(filePromise);
        Mockito.when(filePromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(filePromise);
        Mockito.when(filePromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(filePromise);
        action.createFile("name");
        Mockito.verify(parent).newFile(ArgumentMatchers.eq("name"), ArgumentMatchers.eq(""));
    }

    @Test
    public void testShouldCreateFileIfSelectedContainer() throws Exception {
        Mockito.when(appContext.getResource()).thenReturn(parent);
        Mockito.when(parent.newFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(filePromise);
        Mockito.when(filePromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(filePromise);
        Mockito.when(filePromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(filePromise);
        action.createFile("name");
        Mockito.verify(parent).newFile(ArgumentMatchers.eq("name"), ArgumentMatchers.eq(""));
    }

    @Test(expected = IllegalStateException.class)
    public void testShouldThrowExceptionIfFileDoesNotContainParent() throws Exception {
        Mockito.when(appContext.getResource()).thenReturn(file);
        Mockito.when(file.getParent()).thenReturn(null);
        action.createFile("name");
    }
}

