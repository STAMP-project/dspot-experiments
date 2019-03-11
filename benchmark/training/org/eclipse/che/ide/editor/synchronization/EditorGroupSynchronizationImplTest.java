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
package org.eclipse.che.ide.editor.synchronization;


import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.EditorWithAutoSave;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.document.DocumentEventBus;
import org.eclipse.che.ide.api.editor.document.DocumentHandle;
import org.eclipse.che.ide.api.editor.document.DocumentStorage;
import org.eclipse.che.ide.api.editor.document.DocumentStorage.DocumentCallback;
import org.eclipse.che.ide.api.editor.events.DocumentChangedEvent;
import org.eclipse.che.ide.api.editor.events.FileContentUpdateEvent;
import org.eclipse.che.ide.api.editor.texteditor.EditorWidget;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Roman Nikitenko
 */
@RunWith(MockitoJUnitRunner.class)
public class EditorGroupSynchronizationImplTest {
    private static final String FILE_CONTENT = "some content";

    private static final String FILE_NEW_CONTENT = "some content to update";

    private static final String FILE_LOCATION = "testProject/src/main/java/org/eclipse/che/examples/someFile";

    @Mock
    private EventBus eventBus;

    @Mock
    private EditorAgent editorAgent;

    @Mock
    private Document document;

    @Mock
    private DocumentHandle documentHandle;

    @Mock
    private DocumentEventBus documentEventBus;

    @Mock
    private DocumentStorage documentStorage;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private HandlerRegistration handlerRegistration;

    @Mock
    private DocumentChangedEvent documentChangeEvent;

    @Mock
    private FileContentUpdateEvent fileContentUpdateEvent;

    @Mock
    private EditorInput editorInput;

    @Mock
    private EditorWidget activeEditorWidget;

    @Mock
    private EditorWidget editor_1_Widget;

    @Mock
    private EditorWidget editor_2_Widget;

    @Captor
    private ArgumentCaptor<DocumentCallback> documentCallbackCaptor;

    private EditorPartPresenter activeEditor;

    private EditorPartPresenter openedEditor1;

    private EditorPartPresenter openedEditor2;

    private VirtualFile virtualFile;

    private EditorGroupSynchronizationImpl editorGroupSynchronization;

    @Test
    public void shouldUpdateContentOnFileContentUpdateEvent() {
        editorGroupSynchronization.addEditor(openedEditor1);
        Mockito.reset(documentEventBus);
        Mockito.when(fileContentUpdateEvent.getFilePath()).thenReturn(EditorGroupSynchronizationImplTest.FILE_LOCATION);
        editorGroupSynchronization.onFileContentUpdate(fileContentUpdateEvent);
        Mockito.verify(documentStorage).getDocument(ArgumentMatchers.anyObject(), documentCallbackCaptor.capture());
        documentCallbackCaptor.getValue().onDocumentReceived(EditorGroupSynchronizationImplTest.FILE_NEW_CONTENT);
        Mockito.verify(document).replace(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(EditorGroupSynchronizationImplTest.FILE_NEW_CONTENT));
        Mockito.verify(notificationManager, Mockito.never()).notify(ArgumentMatchers.anyString(), ((StatusNotification.Status) (ArgumentMatchers.anyObject())), ArgumentMatchers.anyObject());
    }

    @Test
    public void shouldSkipUpdateContentOnFileContentUpdateEventWhenContentTheSame() {
        editorGroupSynchronization.addEditor(openedEditor1);
        Mockito.reset(documentEventBus);
        Mockito.when(fileContentUpdateEvent.getFilePath()).thenReturn(EditorGroupSynchronizationImplTest.FILE_LOCATION);
        editorGroupSynchronization.onFileContentUpdate(fileContentUpdateEvent);
        Mockito.verify(documentStorage).getDocument(ArgumentMatchers.anyObject(), documentCallbackCaptor.capture());
        documentCallbackCaptor.getValue().onDocumentReceived(EditorGroupSynchronizationImplTest.FILE_CONTENT);
        Mockito.verify(document, Mockito.never()).replace(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
        Mockito.verify(notificationManager, Mockito.never()).notify(ArgumentMatchers.anyString(), ((StatusNotification.Status) (ArgumentMatchers.anyObject())), ArgumentMatchers.anyObject());
    }

    @Test
    public void shouldNotifyAboutExternalOperationAtUpdateContentWhenStampIsDifferent() {
        editorGroupSynchronization.addEditor(openedEditor1);
        Mockito.reset(documentEventBus);
        Mockito.when(fileContentUpdateEvent.getFilePath()).thenReturn(EditorGroupSynchronizationImplTest.FILE_LOCATION);
        Mockito.when(fileContentUpdateEvent.getModificationStamp()).thenReturn("some stamp");
        Mockito.when(getModificationStamp()).thenReturn("current modification stamp");
        editorGroupSynchronization.onFileContentUpdate(fileContentUpdateEvent);
        Mockito.verify(documentStorage).getDocument(ArgumentMatchers.anyObject(), documentCallbackCaptor.capture());
        documentCallbackCaptor.getValue().onDocumentReceived(EditorGroupSynchronizationImplTest.FILE_NEW_CONTENT);
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(SUCCESS), ArgumentMatchers.eq(NOT_EMERGE_MODE));
    }

    @Test
    public void shouldUpdateDirtyStateForEditors() {
        addEditorsToGroup();
        editorGroupSynchronization.onEditorDirtyStateChanged(activeEditor);
        Mockito.verify(editor_1_Widget).markClean();
        Mockito.verify(editor_2_Widget).markClean();
        Mockito.verify(((TextEditor) (openedEditor1))).updateDirtyState(false);
        Mockito.verify(((TextEditor) (openedEditor2))).updateDirtyState(false);
        // we should not update 'dirty' state for the ACTIVE editor
        Mockito.verify(activeEditorWidget, Mockito.never()).markClean();
        Mockito.verify(((TextEditor) (activeEditor)), Mockito.never()).updateDirtyState(false);
    }

    @Test
    public void shouldSkipUpdatingDirtyStateWhenNotActiveEditorWasSaved() {
        addEditorsToGroup();
        editorGroupSynchronization.onEditorDirtyStateChanged(openedEditor1);
        // we sync 'dirty' state of editors when content of an ACTIVE editor is saved
        Mockito.verify(editor_1_Widget, Mockito.never()).markClean();
        Mockito.verify(editor_2_Widget, Mockito.never()).markClean();
        Mockito.verify(activeEditorWidget, Mockito.never()).markClean();
        Mockito.verify(((TextEditor) (openedEditor1)), Mockito.never()).updateDirtyState(ArgumentMatchers.anyBoolean());
        Mockito.verify(((TextEditor) (openedEditor2)), Mockito.never()).updateDirtyState(ArgumentMatchers.anyBoolean());
        Mockito.verify(((TextEditor) (activeEditor)), Mockito.never()).updateDirtyState(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void shouldSkipUpdatingDirtyStateWhenHasNotEditorsToSync() {
        editorGroupSynchronization.addEditor(activeEditor);
        editorGroupSynchronization.onEditorDirtyStateChanged(activeEditor);
        Mockito.verify(editor_1_Widget, Mockito.never()).markClean();
        Mockito.verify(editor_2_Widget, Mockito.never()).markClean();
        Mockito.verify(activeEditorWidget, Mockito.never()).markClean();
        Mockito.verify(((TextEditor) (openedEditor1)), Mockito.never()).updateDirtyState(ArgumentMatchers.anyBoolean());
        Mockito.verify(((TextEditor) (openedEditor2)), Mockito.never()).updateDirtyState(ArgumentMatchers.anyBoolean());
        Mockito.verify(((TextEditor) (activeEditor)), Mockito.never()).updateDirtyState(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void shouldAddEditor() {
        Mockito.reset(documentEventBus);
        editorGroupSynchronization.addEditor(activeEditor);
        Mockito.verify(documentEventBus).addHandler(ArgumentMatchers.<DocumentChangedEvent.Type>anyObject(), ArgumentMatchers.eq(editorGroupSynchronization));
    }

    @Test
    public void shouldUpdateContentAtAddingEditorWhenGroupHasUnsavedData() {
        editorGroupSynchronization.addEditor(openedEditor1);
        Mockito.reset(documentEventBus);
        Mockito.when(isAutoSaveEnabled()).thenReturn(false);
        editorGroupSynchronization.addEditor(activeEditor);
        editorGroupSynchronization.onActiveEditorChanged(activeEditor);
        isAutoSaveEnabled();
        Mockito.verify(document, Mockito.times(2)).getContents();
        Mockito.verify(document).replace(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
        Mockito.verify(documentEventBus).addHandler(ArgumentMatchers.<DocumentChangedEvent.Type>anyObject(), ArgumentMatchers.eq(editorGroupSynchronization));
    }

    @Test
    public void shouldRemoveEditorFromGroup() {
        editorGroupSynchronization.addEditor(activeEditor);
        editorGroupSynchronization.removeEditor(activeEditor);
        Mockito.verify(handlerRegistration).removeHandler();
    }

    @Test
    public void shouldRemoveAllEditorsFromGroup() {
        addEditorsToGroup();
        editorGroupSynchronization.unInstall();
        Mockito.verify(handlerRegistration, Mockito.times(3)).removeHandler();
    }

    @Test
    public void shouldNotApplyChangesFromNotActiveEditor() {
        DocumentHandle documentHandle1 = Mockito.mock(DocumentHandle.class);
        Mockito.when(documentChangeEvent.getDocument()).thenReturn(documentHandle1);
        Mockito.when(documentHandle1.isSameAs(documentHandle)).thenReturn(false);
        editorGroupSynchronization.onDocumentChanged(documentChangeEvent);
        Mockito.verify(document, Mockito.never()).replace(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldApplyChangesFromActiveEditor() {
        int offset = 10;
        int removeCharCount = 100;
        String text = "someText";
        Mockito.when(documentChangeEvent.getOffset()).thenReturn(offset);
        Mockito.when(documentChangeEvent.getRemoveCharCount()).thenReturn(removeCharCount);
        Mockito.when(documentChangeEvent.getText()).thenReturn(text);
        DocumentHandle documentHandle1 = Mockito.mock(DocumentHandle.class);
        Mockito.when(documentChangeEvent.getDocument()).thenReturn(documentHandle1);
        Mockito.when(documentHandle1.isSameAs(documentHandle)).thenReturn(true);
        addEditorsToGroup();
        editorGroupSynchronization.onDocumentChanged(documentChangeEvent);
        Mockito.verify(document, Mockito.times(2)).replace(ArgumentMatchers.eq(offset), ArgumentMatchers.eq(removeCharCount), ArgumentMatchers.eq(text));
    }

    @Test
    public void shouldResolveAutoSave() {
        addEditorsToGroup();
        // AutoSave for active editor should always be enabled,
        // but AutoSave for other editors with the same path should be disabled
        Mockito.verify(((EditorWithAutoSave) (activeEditor))).enableAutoSave();
        Mockito.verify(((EditorWithAutoSave) (openedEditor1))).disableAutoSave();
        Mockito.verify(((EditorWithAutoSave) (openedEditor2))).disableAutoSave();
    }
}

