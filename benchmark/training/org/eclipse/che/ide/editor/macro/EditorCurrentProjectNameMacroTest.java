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
package org.eclipse.che.ide.editor.macro;


import EditorCurrentProjectNameMacro.KEY;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit tests for the {@link EditorCurrentProjectNameMacro}
 *
 * @author Vlad Zhukovskyi
 */
@RunWith(MockitoJUnitRunner.class)
public class EditorCurrentProjectNameMacroTest extends AbstractEditorMacroTest {
    private EditorCurrentProjectNameMacro provider;

    @Test
    public void testGetKey() throws Exception {
        Assert.assertSame(provider.getName(), KEY);
    }

    @Test
    public void getValue() throws Exception {
        initEditorWithTestFile();
        provider.expand();
        Mockito.verify(editorAgent).getActiveEditor();
        Mockito.verify(promiseProvider).resolve(ArgumentMatchers.eq(AbstractEditorMacroTest.PROJECT_NAME));
    }

    @Test
    public void getEmptyValue() throws Exception {
        provider.expand();
        Mockito.verify(editorAgent).getActiveEditor();
        Mockito.verify(promiseProvider).resolve(ArgumentMatchers.eq(""));
    }
}

