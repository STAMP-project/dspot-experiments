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
package org.eclipse.che.ide.part.perspectives.project;


import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.command.explorer.CommandsExplorerPresenter;
import org.eclipse.che.ide.part.PartStackPresenter;
import org.eclipse.che.ide.part.PartStackPresenterFactory;
import org.eclipse.che.ide.part.PartStackViewFactory;
import org.eclipse.che.ide.part.WorkBenchControllerFactory;
import org.eclipse.che.ide.part.WorkBenchPartController;
import org.eclipse.che.ide.part.editor.multipart.EditorMultiPartStackPresenter;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;
import org.eclipse.che.ide.part.perspectives.general.PerspectiveViewImpl;
import org.eclipse.che.ide.processes.panel.ProcessesPanelPresenter;
import org.eclipse.che.providers.DynaProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class ProjectPerspectiveTest {
    // constructor mocks
    @Mock
    private PerspectiveViewImpl view;

    @Mock
    private PartStackViewFactory partViewFactory;

    @Mock
    private WorkBenchControllerFactory controllerFactory;

    @Mock
    private PartStackPresenterFactory stackPresenterFactory;

    @Mock
    private EventBus eventBus;

    @Mock
    private EditorMultiPartStackPresenter editorMultiPartStackPresenter;

    // additional mocks
    @Mock
    private FlowPanel panel;

    @Mock
    private SplitLayoutPanel layoutPanel;

    @Mock
    private SimplePanel simplePanel;

    @Mock
    private SimpleLayoutPanel simpleLayoutPanel;

    @Mock
    private WorkBenchPartController workBenchController;

    @Mock
    private PartStackView partStackView;

    @Mock
    private PartStackPresenter partStackPresenter;

    @Mock
    private AcceptsOneWidget container;

    @Mock
    private DynaProvider dynaProvider;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private ProjectExplorerPresenter projectExplorerPresenter;

    @Mock
    private CommandsExplorerPresenter commandsExplorerPresenter;

    @Mock
    private ProcessesPanelPresenter processesPanelPresenter;

    private ProjectPerspective perspective;

    @Test
    public void perspectiveShouldBeDisplayed() {
        perspective.go(container);
        Mockito.verify(view).getEditorPanel();
        Mockito.verify(view, Mockito.times(2)).getNavigationPanel();
        Mockito.verify(view, Mockito.times(2)).getToolPanel();
        Mockito.verify(view, Mockito.times(2)).getInformationPanel();
        Mockito.verify(partStackPresenter, Mockito.times(2)).go(simplePanel);
        Mockito.verify(partStackPresenter).go(simpleLayoutPanel);
        Mockito.verify(container).setWidget(view);
    }
}

