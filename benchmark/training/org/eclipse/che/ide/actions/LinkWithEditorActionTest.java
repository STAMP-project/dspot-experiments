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
package org.eclipse.che.ide.actions;


import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(GwtMockitoTestRunner.class)
public class LinkWithEditorActionTest {
    private static final String TEXT = "to be or not to be";

    @Mock
    private CoreLocalizationConstant localizationConstant;

    @Mock
    private Provider<EditorAgent> editorAgentProvider;

    @Mock
    private EventBus eventBus;

    @Mock
    private PreferencesManager preferencesManager;

    @Mock
    private EditorAgent editorAgent;

    @Mock
    private EditorPartPresenter editorPartPresenter;

    @Mock
    private EditorInput editorInput;

    @Mock
    private VirtualFile virtualFile;

    @InjectMocks
    private LinkWithEditorAction action;

    @Test
    public void actionShouldBePerformed() throws Exception {
        Mockito.when(preferencesManager.getValue(ArgumentMatchers.eq(LinkWithEditorAction.LINK_WITH_EDITOR))).thenReturn(null);
        action.actionPerformed(new org.eclipse.che.ide.api.action.ActionEvent(new Presentation(), null, null));
        Mockito.verify(preferencesManager).setValue("linkWithEditor", Boolean.toString(true));
        Mockito.verify(eventBus).fireEvent(((Event<?>) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void actionShouldNotBePerformedIfActiveEditorIsNull() throws Exception {
        Mockito.when(preferencesManager.getValue(ArgumentMatchers.eq(LinkWithEditorAction.LINK_WITH_EDITOR))).thenReturn(null);
        Mockito.when(editorAgent.getActiveEditor()).thenReturn(null);
        action.actionPerformed(new org.eclipse.che.ide.api.action.ActionEvent(new Presentation(), null, null));
        Mockito.verify(eventBus, Mockito.never()).fireEvent(((Event<?>) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void actionShouldNotBePerformedIfEditorInputIsNull() throws Exception {
        Mockito.when(preferencesManager.getValue(ArgumentMatchers.eq(LinkWithEditorAction.LINK_WITH_EDITOR))).thenReturn(null);
        Mockito.when(editorPartPresenter.getEditorInput()).thenReturn(null);
        action.actionPerformed(new org.eclipse.che.ide.api.action.ActionEvent(new Presentation(), null, null));
        Mockito.verify(eventBus, Mockito.never()).fireEvent(((Event<?>) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void revealEventShouldNotBeFiredIfPreferenceValueIsFalse() throws Exception {
        Mockito.when(preferencesManager.getValue(ArgumentMatchers.eq(LinkWithEditorAction.LINK_WITH_EDITOR))).thenReturn(Boolean.toString(true));
        action.actionPerformed(new org.eclipse.che.ide.api.action.ActionEvent(new Presentation(), null, null));
        Mockito.verify(eventBus, Mockito.never()).fireEvent(((Event<?>) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void revealEventShouldBeFiredIfPreferenceValueIsTrue() throws Exception {
        Mockito.when(preferencesManager.getValue(ArgumentMatchers.eq(LinkWithEditorAction.LINK_WITH_EDITOR))).thenReturn(Boolean.toString(false));
        action.actionPerformed(new org.eclipse.che.ide.api.action.ActionEvent(new Presentation(), null, null));
        Mockito.verify(eventBus).fireEvent(((Event<?>) (ArgumentMatchers.anyObject())));
    }
}

