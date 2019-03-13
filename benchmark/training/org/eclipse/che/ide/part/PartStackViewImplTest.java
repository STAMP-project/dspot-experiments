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
package org.eclipse.che.ide.part;


import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.PartStackView.ActionDelegate;
import org.eclipse.che.ide.api.parts.PartStackView.TabItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class PartStackViewImplTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    PartStackUIResources resources;

    @Mock
    private CoreLocalizationConstant localizationConstant;

    @Mock
    FlowPanel tabsPanel;

    // additional mocks
    @Mock
    private MouseDownEvent event;

    @Mock
    private ContextMenuEvent contextMenuEvent;

    @Mock
    private ActionDelegate delegate;

    @Mock
    private TabItem tabItem;

    @Mock
    private TabItem tabItem2;

    @Mock
    private PartPresenter partPresenter;

    @Mock
    private PartPresenter partPresenter2;

    @Mock
    private IsWidget widget;

    @Mock
    private IsWidget widget2;

    @Mock
    private Widget focusedWidget;

    @Mock
    private Element element;

    @Captor
    private ArgumentCaptor<AcceptsOneWidget> contentCaptor;

    private PartStackViewImpl view;

    @Test
    public void onPartStackMouseShouldBeDown() {
        view.onMouseDown(event);
        Mockito.verify(delegate).onRequestFocus();
    }

    @Test
    public void onPartStackContextMenuShouldBeClicked() {
        view.onContextMenu(contextMenuEvent);
        Mockito.verify(delegate).onRequestFocus();
    }

    @Test
    public void tabShouldBeAdded() {
        view.addTab(tabItem, partPresenter);
        Mockito.verify(tabItem).getView();
        Mockito.verify(partPresenter).go(ArgumentMatchers.any());
    }

    @Test
    public void tabShouldBeSelected() {
        view.addTab(tabItem, partPresenter);
        view.selectTab(partPresenter);
        Mockito.verify(tabItem).select();
        Mockito.verify(delegate).onRequestFocus();
    }
}

