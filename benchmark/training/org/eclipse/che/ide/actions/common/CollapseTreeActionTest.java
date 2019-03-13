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
package org.eclipse.che.ide.actions.common;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.ui.smartTree.data.TreeExpander;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for the {@link CollapseTreeAction}.
 *
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class CollapseTreeActionTest {
    @Mock
    TreeExpander treeExpander;

    @Mock
    ActionEvent actionEvent;

    @Mock
    Presentation presentation;

    private CollapseTreeAction action;

    @Test
    public void testShouldNotFireTreeCollapse() throws Exception {
        Mockito.when(treeExpander.isCollapseEnabled()).thenReturn(false);
        action.actionPerformed(actionEvent);
        Mockito.verify(treeExpander, Mockito.never()).collapseTree();
    }

    @Test
    public void testShouldFireTreeCollapse() throws Exception {
        Mockito.when(treeExpander.isCollapseEnabled()).thenReturn(true);
        action.actionPerformed(actionEvent);
        Mockito.verify(treeExpander).collapseTree();
    }

    @Test
    public void testShouldUpdatePresentationBasedOnStatus() throws Exception {
        Mockito.when(treeExpander.isCollapseEnabled()).thenReturn(true);
        action.update(actionEvent);
        Mockito.verify(presentation).setEnabledAndVisible(ArgumentMatchers.eq(true));
    }
}

