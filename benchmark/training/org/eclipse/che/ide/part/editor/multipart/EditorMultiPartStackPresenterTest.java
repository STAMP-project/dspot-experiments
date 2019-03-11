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
package org.eclipse.che.ide.part.editor.multipart;


import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.editor.AbstractEditorPresenter;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.parts.ActivePartChangedEvent;
import org.eclipse.che.ide.api.parts.EditorPartStack;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.workspace.model.WorkspaceImpl;
import org.eclipse.che.ide.part.editor.EditorPartStackPresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Roman Nikitenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class EditorMultiPartStackPresenterTest {
    private static final String RELATIVE_PART_ID = "partID";

    private Constraints constraints = new Constraints(HORIZONTALLY, EditorMultiPartStackPresenterTest.RELATIVE_PART_ID);

    // constructor mocks
    @Mock
    private EditorMultiPartStackView view;

    @Mock
    private EventBus eventBus;

    @Mock
    private AppContext appContext;

    @Mock
    private WorkspaceImpl workspace;

    @Mock
    private Provider<EditorPartStack> editorPartStackProvider;

    // additional mocks
    @Mock
    private EditorPartStackPresenter editorPartStack;

    @Mock
    private AbstractEditorPresenter partPresenter1;

    @Mock
    private AbstractEditorPresenter partPresenter2;

    @Mock
    private EditorPartPresenter editorPartPresenter;

    @Mock
    private HandlerRegistration handlerRegistration;

    private EditorMultiPartStackPresenter presenter;

    @Test
    public void constructorShouldBeVerified() {
        Mockito.verify(eventBus).addHandler(ArgumentMatchers.<ActivePartChangedEvent.Type>anyObject(), ArgumentMatchers.eq(presenter));
    }

    @Test
    public void shouldOpenPartInNewEditorPartStack() {
        presenter.addPart(partPresenter1, null);
        Mockito.verify(editorPartStackProvider).get();
        Mockito.verify(editorPartStack).addPart(partPresenter1);
        Mockito.verify(view).addPartStack(ArgumentMatchers.eq(editorPartStack), ArgumentMatchers.isNull(EditorPartStack.class), ArgumentMatchers.isNull(Constraints.class), ArgumentMatchers.eq((-1.0)));
    }

    @Test
    public void shouldOpenPartInActiveEditorPartStack() {
        presenter.addPart(partPresenter1);
        presenter.setActivePart(partPresenter1);
        Mockito.reset(view);
        Mockito.reset(editorPartStackProvider);
        presenter.addPart(partPresenter2, null);
        Mockito.verify(editorPartStackProvider, Mockito.never()).get();
        Mockito.verify(editorPartStack).addPart(partPresenter2);
        Mockito.verify(view, Mockito.never()).addPartStack(((EditorPartStack) (ArgumentMatchers.anyObject())), ((EditorPartStack) (ArgumentMatchers.anyObject())), ((Constraints) (ArgumentMatchers.anyObject())), ArgumentMatchers.eq((-1.0)));
    }

    @Test
    public void shouldSplitEditorPartStackAndOpenPart() {
        presenter.addPart(partPresenter1);
        Mockito.reset(editorPartStackProvider);
        Mockito.when(editorPartStackProvider.get()).thenReturn(editorPartStack);
        Mockito.when(editorPartStack.getPartByTabId(EditorMultiPartStackPresenterTest.RELATIVE_PART_ID)).thenReturn(partPresenter1);
        presenter.addPart(partPresenter2, constraints);
        Mockito.verify(editorPartStackProvider).get();
        Mockito.verify(editorPartStack).addPart(partPresenter2);
        Mockito.verify(view).addPartStack(editorPartStack, editorPartStack, constraints, (-1));
    }

    @Test
    public void focusShouldBeSet() {
        presenter.addPart(partPresenter1);
        presenter.setActivePart(partPresenter1);
        presenter.setFocus(true);
        Mockito.verify(editorPartStack).setFocus(true);
    }

    @Test
    public void shouldSetActivePart() {
        presenter.addPart(partPresenter1);
        presenter.setActivePart(partPresenter1);
        Mockito.verify(editorPartStack).containsPart(partPresenter1);
        Mockito.verify(editorPartStack).setActivePart(partPresenter1);
    }

    @Test
    public void shouldRemovePart() {
        Mockito.when(editorPartStack.getActivePart()).thenReturn(partPresenter2);
        presenter.addPart(partPresenter1);
        presenter.removePart(partPresenter1);
        Mockito.verify(editorPartStack).containsPart(partPresenter1);
        Mockito.verify(editorPartStack).removePart(partPresenter1);
    }

    @Test
    public void shouldRemovePartStackWhenPartStackIsEmpty() {
        Mockito.when(editorPartStack.getActivePart()).thenReturn(null);
        presenter.addPart(partPresenter1);
        presenter.removePart(partPresenter1);
        Mockito.verify(editorPartStack).containsPart(partPresenter1);
        Mockito.verify(editorPartStack).removePart(partPresenter1);
        Mockito.verify(view).removePartStack(editorPartStack);
    }

    @Test
    public void shouldNotRemovePartStackWhenPartStackIsNotEmpty() {
        Mockito.when(editorPartStack.getActivePart()).thenReturn(partPresenter2);
        presenter.addPart(partPresenter1);
        presenter.removePart(partPresenter1);
        Mockito.verify(editorPartStack).containsPart(partPresenter1);
        Mockito.verify(editorPartStack).removePart(partPresenter1);
        Mockito.verify(view, Mockito.never()).removePartStack(editorPartStack);
    }

    @Test
    public void shouldOpenPreviousActivePartStack() {
        Mockito.when(editorPartStack.containsPart(partPresenter1)).thenReturn(true);
        presenter.addPart(partPresenter1);
        presenter.addPart(partPresenter2);
        presenter.removePart(partPresenter1);
        Mockito.verify(editorPartStack).containsPart(((PartPresenter) (ArgumentMatchers.anyObject())));
        Mockito.verify(view).removePartStack(editorPartStack);
        Mockito.verify(editorPartStack).openPreviousActivePart();
    }

    @Test
    public void shouldOpenPreviousActivePart() {
        presenter.addPart(partPresenter1);
        presenter.setActivePart(partPresenter1);
        presenter.openPreviousActivePart();
        Mockito.verify(editorPartStack).openPreviousActivePart();
    }
}

