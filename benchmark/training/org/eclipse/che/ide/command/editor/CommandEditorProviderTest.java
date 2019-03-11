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
package org.eclipse.che.ide.command.editor;


import com.google.inject.Provider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link CommandEditorProvider}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommandEditorProviderTest {
    @Mock
    private EditorMessages editorMessages;

    @Mock
    private Provider<CommandEditor> editorProvider;

    @InjectMocks
    private CommandEditorProvider provider;

    @Test
    public void shouldReturnId() throws Exception {
        assertThat(provider.getId()).isNotNull();
        assertThat(provider.getId()).isNotEmpty();
    }

    @Test
    public void shouldReturnDescriptions() throws Exception {
        provider.getDescription();
        Mockito.verify(editorMessages).editorDescription();
    }

    @Test
    public void shouldReturnEditor() throws Exception {
        provider.getEditor();
        Mockito.verify(editorProvider).get();
    }
}

