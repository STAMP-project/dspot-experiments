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


import ResourceDelta.ADDED;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.events.EditorDirtyStateChangedEvent;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.api.resources.ResourceChangedEvent;
import org.eclipse.che.ide.api.resources.ResourceDelta;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.eclipse.che.ide.resource.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Roman Nikitenko
 */
@RunWith(MockitoJUnitRunner.class)
public class EditorContentSynchronizerImplTest {
    private static final String FOLDER_PATH = "testProject/src/main/java/org/eclipse/che/examples/";

    private static final String FILE_NAME = "someFile";

    private static final String FILE_PATH = (EditorContentSynchronizerImplTest.FOLDER_PATH) + (EditorContentSynchronizerImplTest.FILE_NAME);

    // constructor mocks
    @Mock
    private EventBus eventBus;

    @Mock
    private Provider<EditorGroupSynchronization> editorGroupSyncProvider;

    // additional mocks
    @Mock
    private EditorInput editorInput;

    @Mock
    private VirtualFile virtualFile;

    @Mock
    private EditorGroupSynchronization editorGroupSynchronization;

    @Mock
    private EditorDirtyStateChangedEvent editorDirtyStateChangedEvent;

    private EditorPartPresenter activeEditor;

    @InjectMocks
    EditorContentSynchronizerImpl editorContentSynchronizer;

    @Test
    public void constructorShouldBeVerified() {
        Mockito.verify(eventBus, Mockito.times(3)).addHandler(ArgumentMatchers.<Event.Type<Object>>anyObject(), ArgumentMatchers.anyObject());
    }

    @Test
    public void shouldCreateNewEditorGroup() {
        EditorPartPresenter openedEditor = Mockito.mock(EditorPartPresenter.class);
        Mockito.when(openedEditor.getEditorInput()).thenReturn(editorInput);
        editorContentSynchronizer.trackEditor(activeEditor);
        Mockito.verify(editorGroupSyncProvider).get();
    }

    @Test
    public void shouldAddEditorIntoExistGroup() {
        EditorPartPresenter openedEditor1 = Mockito.mock(EditorPartPresenter.class);
        EditorPartPresenter openedEditor2 = Mockito.mock(EditorPartPresenter.class);
        Mockito.when(openedEditor1.getEditorInput()).thenReturn(editorInput);
        Mockito.when(openedEditor2.getEditorInput()).thenReturn(editorInput);
        editorContentSynchronizer.trackEditor(openedEditor1);
        editorContentSynchronizer.trackEditor(openedEditor2);
        Mockito.reset(editorGroupSyncProvider);
        editorContentSynchronizer.trackEditor(activeEditor);
        Mockito.verify(editorGroupSyncProvider, Mockito.never()).get();
        Mockito.verify(editorGroupSynchronization).addEditor(activeEditor);
    }

    @Test
    public void shouldRemoveEditorFromGroup() {
        EditorPartPresenter openedEditor1 = Mockito.mock(EditorPartPresenter.class);
        EditorPartPresenter openedEditor2 = Mockito.mock(EditorPartPresenter.class);
        Mockito.when(openedEditor1.getEditorInput()).thenReturn(editorInput);
        Mockito.when(openedEditor2.getEditorInput()).thenReturn(editorInput);
        editorContentSynchronizer.trackEditor(openedEditor1);
        editorContentSynchronizer.trackEditor(openedEditor2);
        editorContentSynchronizer.trackEditor(activeEditor);
        editorContentSynchronizer.unTrackEditor(activeEditor);
        Mockito.verify(editorGroupSynchronization).removeEditor(activeEditor);
    }

    @Test
    public void shouldRemoveGroup() {
        EditorPartPresenter openedEditor1 = Mockito.mock(EditorPartPresenter.class);
        Mockito.when(openedEditor1.getEditorInput()).thenReturn(editorInput);
        editorContentSynchronizer.trackEditor(openedEditor1);
        editorContentSynchronizer.trackEditor(activeEditor);
        editorContentSynchronizer.unTrackEditor(activeEditor);
        Mockito.verify(editorGroupSynchronization).removeEditor(activeEditor);
        Mockito.verify(editorGroupSynchronization).unInstall();
    }

    @Test
    public void shouldUpdatePathForGroupWhenFileLocationIsChanged() {
        Resource resource = Mockito.mock(Resource.class);
        ResourceDelta delta = Mockito.mock(ResourceDelta.class);
        ResourceChangedEvent resourceChangedEvent = new ResourceChangedEvent(delta);
        Path fromPath = new Path(EditorContentSynchronizerImplTest.FILE_PATH);
        Path toPath = new Path("testProject/src/main/java/org/eclipse/che/examples/changedFile");
        EditorPartPresenter openedEditor1 = Mockito.mock(EditorPartPresenter.class);
        Mockito.when(openedEditor1.getEditorInput()).thenReturn(editorInput);
        Mockito.when(delta.getKind()).thenReturn(ADDED);
        Mockito.when(delta.getFlags()).thenReturn(5632);
        Mockito.when(delta.getFromPath()).thenReturn(fromPath);
        Mockito.when(delta.getToPath()).thenReturn(toPath);
        Mockito.when(delta.getResource()).thenReturn(resource);
        Mockito.when(resource.isFile()).thenReturn(true);
        editorContentSynchronizer.trackEditor(openedEditor1);
        editorContentSynchronizer.onResourceChanged(resourceChangedEvent);
        final EditorGroupSynchronization oldGroup = editorContentSynchronizer.editorGroups.get(fromPath);
        final EditorGroupSynchronization newGroup = editorContentSynchronizer.editorGroups.get(toPath);
        Assert.assertNull(oldGroup);
        Assert.assertNotNull(newGroup);
    }

    @Test
    public void shouldUpdatePathForGroupWhenFolderLocationIsChanged() {
        Resource resource = Mockito.mock(Resource.class);
        ResourceDelta delta = Mockito.mock(ResourceDelta.class);
        ResourceChangedEvent resourceChangedEvent = new ResourceChangedEvent(delta);
        Path fromPath = new Path(EditorContentSynchronizerImplTest.FOLDER_PATH);
        Path toPath = new Path("testProject/src/main/java/org/eclipse/che/samples/");
        Path oldFilePath = new Path(EditorContentSynchronizerImplTest.FILE_PATH);
        Path newFilePath = new Path(("testProject/src/main/java/org/eclipse/che/samples/" + (EditorContentSynchronizerImplTest.FILE_NAME)));
        EditorPartPresenter openedEditor1 = Mockito.mock(EditorPartPresenter.class);
        Mockito.when(openedEditor1.getEditorInput()).thenReturn(editorInput);
        Mockito.when(delta.getKind()).thenReturn(ADDED);
        Mockito.when(delta.getFlags()).thenReturn(5632);
        Mockito.when(delta.getFromPath()).thenReturn(fromPath);
        Mockito.when(delta.getToPath()).thenReturn(toPath);
        Mockito.when(delta.getResource()).thenReturn(resource);
        Mockito.when(resource.isFile()).thenReturn(false);
        editorContentSynchronizer.trackEditor(openedEditor1);
        editorContentSynchronizer.onResourceChanged(resourceChangedEvent);
        final EditorGroupSynchronization oldGroup = editorContentSynchronizer.editorGroups.get(oldFilePath);
        final EditorGroupSynchronization newGroup = editorContentSynchronizer.editorGroups.get(newFilePath);
        Assert.assertNull(oldGroup);
        Assert.assertNotNull(newGroup);
    }

    // we sync 'dirty' state of editors only for case when content of an active editor IS SAVED
    @Test
    public void shouldSkipEditorDirtyStateChangedEventWhenEditorIsDirty() {
        Mockito.when(activeEditor.isDirty()).thenReturn(true);
        EditorPartPresenter openedEditor1 = Mockito.mock(EditorPartPresenter.class);
        Mockito.when(openedEditor1.getEditorInput()).thenReturn(editorInput);
        editorContentSynchronizer.trackEditor(openedEditor1);
        editorContentSynchronizer.trackEditor(activeEditor);
        editorContentSynchronizer.onEditorDirtyStateChanged(editorDirtyStateChangedEvent);
        EditorGroupSynchronization group = editorContentSynchronizer.editorGroups.get(new Path(EditorContentSynchronizerImplTest.FILE_PATH));
        Mockito.verify(group, Mockito.never()).onEditorDirtyStateChanged(activeEditor);
    }

    @Test
    public void shouldSkipEditorDirtyStateChangedEventWhenEditorIsNull() {
        Mockito.when(editorDirtyStateChangedEvent.getEditor()).thenReturn(null);
        EditorPartPresenter openedEditor1 = Mockito.mock(EditorPartPresenter.class);
        Mockito.when(openedEditor1.getEditorInput()).thenReturn(editorInput);
        editorContentSynchronizer.trackEditor(openedEditor1);
        editorContentSynchronizer.trackEditor(activeEditor);
        editorContentSynchronizer.onEditorDirtyStateChanged(editorDirtyStateChangedEvent);
        EditorGroupSynchronization group = editorContentSynchronizer.editorGroups.get(new Path(EditorContentSynchronizerImplTest.FILE_PATH));
        Mockito.verify(group, Mockito.never()).onEditorDirtyStateChanged(activeEditor);
    }

    @Test
    public void shouldNotifyGroupWhenEditorContentHasSaved() {
        Mockito.when(activeEditor.isDirty()).thenReturn(false);
        EditorPartPresenter openedEditor1 = Mockito.mock(EditorPartPresenter.class);
        Mockito.when(openedEditor1.getEditorInput()).thenReturn(editorInput);
        editorContentSynchronizer.trackEditor(openedEditor1);
        editorContentSynchronizer.trackEditor(activeEditor);
        editorContentSynchronizer.onEditorDirtyStateChanged(editorDirtyStateChangedEvent);
        EditorGroupSynchronization group = editorContentSynchronizer.editorGroups.get(new Path(EditorContentSynchronizerImplTest.FILE_PATH));
        Mockito.verify(group).onEditorDirtyStateChanged(activeEditor);
    }
}

