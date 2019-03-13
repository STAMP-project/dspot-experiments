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
package org.eclipse.che.ide.ext.git.client.add;


import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.File;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.ext.git.client.outputconsole.GitOutputConsole;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link AddToIndexPresenter} functionality.
 *
 * @author Andrey Plotnikov
 * @author Vlad Zhukovskyi
 * @author Igor Vinokur
 */
public class AddToIndexPresenterTest extends BaseTest {
    private static final String MESSAGE = "message";

    private static final String FOLDER_NAME = "folder name";

    private static final String FILE_NAME = "file name";

    @Mock
    private AddToIndexView view;

    @Mock
    private GitOutputConsole console;

    private AddToIndexPresenter presenter;

    @Test
    public void shouldSetFolderMessageToViewAndShowDialog() throws Exception {
        Container folder = Mockito.mock(Container.class);
        Mockito.when(folder.getName()).thenReturn(AddToIndexPresenterTest.FOLDER_NAME);
        Mockito.when(appContext.getResource()).thenReturn(folder);
        Mockito.when(appContext.getResources()).thenReturn(new Resource[]{ folder });
        Mockito.when(constant.addToIndexFolder(AddToIndexPresenterTest.FOLDER_NAME)).thenReturn(AddToIndexPresenterTest.MESSAGE);
        presenter.showDialog();
        Mockito.verify(constant).addToIndexFolder(ArgumentMatchers.eq(AddToIndexPresenterTest.FOLDER_NAME));
        Mockito.verify(view).setMessage(ArgumentMatchers.eq(AddToIndexPresenterTest.MESSAGE));
        Mockito.verify(view).setUpdated(ArgumentMatchers.eq(false));
        Mockito.verify(view).showDialog();
    }

    @Test
    public void shouldSetFileMessageToViewAndShowDialog() throws Exception {
        File file = Mockito.mock(File.class);
        Mockito.when(file.getName()).thenReturn(AddToIndexPresenterTest.FILE_NAME);
        Mockito.when(appContext.getResource()).thenReturn(file);
        Mockito.when(appContext.getResources()).thenReturn(new Resource[]{ file });
        Mockito.when(constant.addToIndexFile(AddToIndexPresenterTest.FILE_NAME)).thenReturn(AddToIndexPresenterTest.MESSAGE);
        presenter.showDialog();
        Mockito.verify(constant).addToIndexFile(ArgumentMatchers.eq(AddToIndexPresenterTest.FILE_NAME));
        Mockito.verify(view).setMessage(ArgumentMatchers.eq(AddToIndexPresenterTest.MESSAGE));
        Mockito.verify(view).setUpdated(ArgumentMatchers.eq(false));
        Mockito.verify(view).showDialog();
    }

    @Test
    public void shouldSetMultiSelectionMessageToViewAndShowDialog() throws Exception {
        File file1 = Mockito.mock(File.class);
        File file2 = Mockito.mock(File.class);
        Mockito.when(file1.getName()).thenReturn(((AddToIndexPresenterTest.FILE_NAME) + "1"));
        Mockito.when(file2.getName()).thenReturn(((AddToIndexPresenterTest.FILE_NAME) + "2"));
        Mockito.when(appContext.getResource()).thenReturn(file2);
        Mockito.when(appContext.getResources()).thenReturn(new Resource[]{ file1, file2 });
        Mockito.when(constant.addToIndexMultiSelect()).thenReturn(AddToIndexPresenterTest.MESSAGE);
        presenter.showDialog();
        Mockito.verify(constant).addToIndexMultiSelect();
        Mockito.verify(view).setMessage(ArgumentMatchers.eq(AddToIndexPresenterTest.MESSAGE));
        Mockito.verify(view).setUpdated(ArgumentMatchers.eq(false));
        Mockito.verify(view).showDialog();
    }

    @Test
    public void shouldAddToIndexWhenAddButtonClicked() throws Exception {
        Mockito.when(constant.addSuccess()).thenReturn(AddToIndexPresenterTest.MESSAGE);
        presenter.onAddClicked();
        Mockito.verify(voidPromise).then(voidPromiseCaptor.capture());
        voidPromiseCaptor.getValue().apply(null);
        Mockito.verify(console).print(ArgumentMatchers.eq(AddToIndexPresenterTest.MESSAGE));
        Mockito.verify(notificationManager).notify(AddToIndexPresenterTest.MESSAGE);
    }

    @Test
    public void shouldPrintErrorWhenFailedAddToIndex() throws Exception {
        Mockito.when(constant.addFailed()).thenReturn(AddToIndexPresenterTest.MESSAGE);
        presenter.onAddClicked();
        Mockito.verify(voidPromise).catchError(promiseErrorCaptor.capture());
        promiseErrorCaptor.getValue().apply(null);
        Mockito.verify(console).printError(ArgumentMatchers.eq(AddToIndexPresenterTest.MESSAGE));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.eq(AddToIndexPresenterTest.MESSAGE), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();
        Mockito.verify(view).close();
    }
}

