/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.ui.repository.repositoryexplorer.controllers;


import XulDialogCallback.Status;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.ui.repository.repositoryexplorer.RepositoryExplorer;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryDirectory;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryObject;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.components.XulPromptBox;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.swt.custom.MessageDialogBase;
import org.pentaho.ui.xul.util.XulDialogCallback;


public class BrowseControllerTest {
    private static Class<?> PKG = RepositoryExplorer.class;// for i18n purposes, needed by Translator2!!


    private static final String PROMPTBOX = "promptbox";

    private static final String FOLDER_NAME = "New Folder";

    private Document document;

    private Binding directoryBinding;

    private Binding selectedItemsBinding;

    private Map<ObjectId, UIRepositoryDirectory> directoryMap;

    private UIRepositoryDirectory selectedFolder;

    private BrowseController controller;

    /* Test for {@link BrowseController#createFolder()}.

    Given an opened folder creation dialog with the non-empty folder name field.

    When this prompt dialog is just simply closed by pressing 'x' button, then folder should not be created.
     */
    @Test
    public void shouldNotCreateFolderOnCloseCreationDialog() throws Exception {
        XulPromptBox prompt = new BrowseControllerTest.XulPromptBoxMock(Status.CANCEL);
        Mockito.when(document.createElement(BrowseControllerTest.PROMPTBOX)).thenReturn(prompt);
        controller.createFolder();
        Assert.assertTrue(directoryMap.isEmpty());
        Mockito.verify(selectedFolder, Mockito.never()).createFolder(ArgumentMatchers.anyString());
        Mockito.verify(directoryBinding, Mockito.never()).fireSourceChanged();
        Mockito.verify(selectedItemsBinding, Mockito.never()).fireSourceChanged();
    }

    /* Test for {@link BrowseController#createFolder()}.

    Given an opened folder creation dialog with the non-empty folder name field.

    When this prompt dialog is accepted, then a folder should be created.
     */
    @Test
    public void shouldCreateFolderOnAcceptCreationDialog() throws Exception {
        XulPromptBox prompt = new BrowseControllerTest.XulPromptBoxMock(Status.ACCEPT);
        Mockito.when(document.createElement(BrowseControllerTest.PROMPTBOX)).thenReturn(prompt);
        controller.createFolder();
        Assert.assertFalse(directoryMap.isEmpty());
        Mockito.verify(selectedFolder).createFolder(ArgumentMatchers.anyString());
        Mockito.verify(directoryBinding).fireSourceChanged();
        Mockito.verify(selectedItemsBinding).fireSourceChanged();
    }

    @Test
    public void folderWithSingleDotThrowsException() throws Exception {
        XulPromptBox prompt = new BrowseControllerTest.XulPromptBoxMock(Status.ACCEPT);
        Mockito.when(document.createElement(BrowseControllerTest.PROMPTBOX)).thenReturn(prompt);
        Mockito.doNothing().when(controller).confirm(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(prompt).when(controller).promptForName(ArgumentMatchers.any(UIRepositoryObject.class));
        prompt.setValue(".");
        controller.createFolder();
        Mockito.verify(controller, Mockito.times(1)).confirm(BaseMessages.getString(BrowseControllerTest.PKG, "Dialog.Error"), BaseMessages.getString(BrowseControllerTest.PKG, "BrowserController.InvalidFolderName"));
    }

    @Test
    public void folderWithDoubleDotThrowsException() throws Exception {
        XulPromptBox prompt = new BrowseControllerTest.XulPromptBoxMock(Status.ACCEPT);
        Mockito.when(document.createElement(BrowseControllerTest.PROMPTBOX)).thenReturn(prompt);
        Mockito.doNothing().when(controller).confirm(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(prompt).when(controller).promptForName(ArgumentMatchers.any(UIRepositoryObject.class));
        prompt.setValue("..");
        controller.createFolder();
        Mockito.verify(controller, Mockito.times(1)).confirm(BaseMessages.getString(BrowseControllerTest.PKG, "Dialog.Error"), BaseMessages.getString(BrowseControllerTest.PKG, "BrowserController.InvalidFolderName"));
    }

    private static class XulPromptBoxMock extends MessageDialogBase implements XulPromptBox {
        private final Status status;

        private String value = null;

        public XulPromptBoxMock(XulDialogCallback.Status status) {
            super(BrowseControllerTest.PROMPTBOX);
            this.status = status;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public int open() {
            for (XulDialogCallback<String> callback : callbacks) {
                callback.onClose(null, status, value);
            }
            return 0;
        }
    }
}

