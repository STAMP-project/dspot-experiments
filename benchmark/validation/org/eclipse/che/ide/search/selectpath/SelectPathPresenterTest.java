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
package org.eclipse.che.ide.search.selectpath;


import FullTextSearchView.ActionDelegate;
import ResourceNode.NodeFactory;
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.List;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.ui.smartTree.data.Node;
import org.eclipse.che.ide.ui.smartTree.data.settings.SettingsProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link FindResultPresenter}.
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class SelectPathPresenterTest {
    @Mock
    private SelectPathView view;

    @Mock
    private ActionDelegate searcher;

    @Mock
    private AppContext appContext;

    @Mock
    private NodeFactory nodeFactory;

    @Mock
    private SettingsProvider settingsProvider;

    @InjectMocks
    SelectPathPresenter selectPathPresenter;

    @Test
    public void windowShouldBeShown() throws Exception {
        Mockito.when(appContext.getProjects()).thenReturn(new Project[0]);
        selectPathPresenter.show(searcher);
        Mockito.verify(view).setStructure(ArgumentMatchers.<List<Node>>any());
        Mockito.verify(view).showDialog();
    }

    @Test
    public void pathShouldBeSelected() throws Exception {
        Mockito.when(appContext.getProjects()).thenReturn(new Project[0]);
        selectPathPresenter.show(searcher);
        selectPathPresenter.setSelectedPath("path");
        Mockito.verify(searcher).setPathDirectory("path");
    }
}

