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
package org.eclipse.che.ide.ext.java.client.navigation.action;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.action.FileStructureAction;
import org.eclipse.che.ide.ext.java.client.navigation.filestructure.FileStructurePresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class FileStructureActionTest {
    @Mock
    private FileStructurePresenter fileStructurePresenter;

    @Mock
    private EditorAgent editorAgent;

    @Mock
    private JavaLocalizationConstant locale;

    @Mock
    private EditorPartPresenter editorPartPresenter;

    @Mock
    private JavaResources resources;

    @InjectMocks
    private FileStructureAction action;

    @Test
    public void constructorShouldBePerformed() throws Exception {
        Mockito.verify(locale).fileStructureActionName();
        Mockito.verify(locale).fileStructureActionDescription();
        Mockito.verify(resources).fileNavigation();
    }

    @Test
    public void actionShouldBePerformed() throws Exception {
        ActionEvent e = Mockito.mock(ActionEvent.class);
        action.actionPerformed(e);
        Mockito.verify(editorAgent).getActiveEditor();
        Mockito.verify(fileStructurePresenter).show(editorPartPresenter);
    }
}

