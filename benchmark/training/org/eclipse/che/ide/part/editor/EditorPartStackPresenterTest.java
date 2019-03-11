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
package org.eclipse.che.ide.part.editor;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.BaseAction;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.AbstractEditorPresenter;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.EditorWithErrors;
import org.eclipse.che.ide.api.parts.EditorTab;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStackView.TabItem;
import org.eclipse.che.ide.api.parts.PropertyListener;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.eclipse.che.ide.menu.PartMenu;
import org.eclipse.che.ide.part.PartStackPresenter.PartStackEventHandler;
import org.eclipse.che.ide.part.PartsComparator;
import org.eclipse.che.ide.part.editor.actions.CloseAllTabsPaneAction;
import org.eclipse.che.ide.part.editor.actions.ClosePaneAction;
import org.eclipse.che.ide.part.editor.actions.SplitHorizontallyAction;
import org.eclipse.che.ide.part.editor.actions.SplitVerticallyAction;
import org.eclipse.che.ide.part.widgets.TabItemFactory;
import org.eclipse.che.ide.part.widgets.panemenu.EditorPaneMenu;
import org.eclipse.che.ide.part.widgets.panemenu.EditorPaneMenuItem;
import org.eclipse.che.ide.part.widgets.panemenu.EditorPaneMenuItemFactory;
import org.eclipse.che.ide.part.widgets.panemenu.PaneMenuActionItemWidget;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.ui.toolbar.PresentationFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.vectomatic.dom.svg.ui.SVGResource;


/**
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class EditorPartStackPresenterTest {
    private static final String SOME_TEXT = "someText";

    // constructor mocks
    @Mock
    private EditorPartStackView view;

    @Mock
    private AppContext appContext;

    @Mock
    private PartMenu partMenu;

    @Mock
    private PartsComparator partsComparator;

    @Mock
    private EventBus eventBus;

    @Mock
    private TabItemFactory tabItemFactory;

    @Mock
    private PartStackEventHandler partStackEventHandler;

    @Mock
    private EditorPaneMenu editorPaneMenu;

    @Mock
    private Provider<EditorAgent> editorAgentProvider;

    @Mock
    private PresentationFactory presentationFactory;

    @Mock
    private ActionManager actionManager;

    @Mock
    private ClosePaneAction closePaneAction;

    @Mock
    private CloseAllTabsPaneAction closeAllTabsPaneAction;

    @Mock
    private EditorPaneMenuItemFactory editorPaneMenuItemFactory;

    @Mock
    private EditorAgent editorAgent;

    @Mock
    private AddEditorTabMenuFactory addEditorTabMenuFactory;

    // additional mocks
    @Mock
    private SplitHorizontallyAction splitHorizontallyAction;

    @Mock
    private SplitVerticallyAction splitVerticallyAction;

    @Mock
    private Presentation presentation;

    @Mock
    private EditorTab editorTab1;

    @Mock
    private EditorTab editorTab2;

    @Mock
    private EditorTab editorTab3;

    @Mock
    private EditorWithErrors withErrorsPart;

    @Mock
    private AbstractEditorPresenter partPresenter1;

    @Mock
    private AbstractEditorPresenter partPresenter2;

    @Mock
    private AbstractEditorPresenter partPresenter3;

    @Mock
    private EditorPaneMenuItem<Action> editorPaneActionMenuItem;

    @Mock
    private EditorPaneMenuItem<TabItem> editorPaneTabMenuItem;

    @Mock
    private SVGResource resource1;

    @Mock
    private SVGResource resource2;

    @Mock
    private ProjectConfigDto descriptor;

    @Mock
    private EditorPartPresenter editorPartPresenter;

    @Mock
    private EditorInput editorInput1;

    @Mock
    private EditorInput editorInput2;

    @Mock
    private EditorInput editorInput3;

    @Mock
    private VirtualFile file1;

    @Mock
    private VirtualFile file2;

    @Mock
    private VirtualFile file3;

    @Mock
    private HandlerRegistration handlerRegistration;

    @Captor
    private ArgumentCaptor<EditorPaneMenuItem> itemCaptor;

    @Captor
    private ArgumentCaptor<AsyncCallback<Void>> argumentCaptor;

    private EditorPartStackPresenter presenter;

    @Test
    public void constructorShouldBeVerified() {
        Mockito.verify(view, Mockito.times(2)).setDelegate(presenter);
        Mockito.verify(view).addPaneMenuButton(editorPaneMenu);
        Mockito.verify(editorPaneMenuItemFactory, Mockito.times(4)).createMenuItem(ArgumentMatchers.<BaseAction>anyObject());
        Mockito.verify(editorPaneMenu).addItem(ArgumentMatchers.<PaneMenuActionItemWidget>anyObject(), ArgumentMatchers.eq(true));
        Mockito.verify(editorPaneMenu, Mockito.times(3)).addItem(ArgumentMatchers.<PaneMenuActionItemWidget>anyObject());
    }

    @Test
    public void focusShouldBeSet() {
        presenter.setFocus(true);
        Mockito.verify(view).setFocus(true);
    }

    @Test
    public void partShouldBeAdded() {
        presenter.addPart(partPresenter1);
        Mockito.verify(partPresenter1, Mockito.times(2)).addPropertyListener(ArgumentMatchers.<PropertyListener>anyObject());
        Mockito.verify(tabItemFactory).createEditorPartButton(partPresenter1, presenter);
        Mockito.verify(editorTab1).setDelegate(presenter);
        Mockito.verify(view).addTab(editorTab1, partPresenter1);
        Mockito.verify(view).selectTab(partPresenter1);
    }

    @Test
    public void partShouldNotBeAddedWhenItExist() {
        presenter.addPart(partPresenter1);
        Mockito.reset(view);
        presenter.addPart(partPresenter1);
        Mockito.verify(view, Mockito.never()).addTab(editorTab1, partPresenter1);
        Mockito.verify(view).selectTab(partPresenter1);
    }

    @Test
    public void activePartShouldBeReturned() {
        presenter.setActivePart(partPresenter1);
        Assert.assertEquals(presenter.getActivePart(), partPresenter1);
    }

    @Test
    public void onTabShouldBeClicked() {
        presenter.addPart(partPresenter1);
        Mockito.reset(view);
        presenter.onTabClicked(editorTab1);
        Mockito.verify(view).selectTab(partPresenter1);
    }

    @Test
    public void tabShouldBeClosed() {
        presenter.addPart(partPresenter1);
        presenter.removePart(partPresenter1);
        presenter.onTabClose(editorTab1);
        Mockito.verify(view).removeTab(partPresenter1);
    }

    @Test
    public void activePartShouldBeChangedWhenWeClickOnTab() {
        presenter.addPart(partPresenter1);
        presenter.addPart(partPresenter2);
        presenter.onTabClicked(editorTab1);
        Assert.assertEquals(presenter.getActivePart(), partPresenter1);
        presenter.onTabClicked(editorTab2);
        Assert.assertEquals(presenter.getActivePart(), partPresenter2);
    }

    @Test
    public void previousTabSelectedWhenWeRemovePart() {
        presenter.addPart(partPresenter1);
        presenter.addPart(partPresenter2);
        presenter.onTabClicked(editorTab2);
        presenter.removePart(partPresenter2);
        presenter.onTabClose(editorTab2);
        Assert.assertEquals(presenter.getActivePart(), partPresenter1);
    }

    @Test
    public void activePartShouldBeNullWhenWeCloseAllParts() {
        presenter.addPart(partPresenter1);
        presenter.onTabClose(editorTab1);
        Assert.assertThat(presenter.getActivePart(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldReturnNextPart() {
        presenter.addPart(partPresenter1);
        presenter.addPart(partPresenter2);
        presenter.addPart(partPresenter3);
        EditorPartPresenter result = presenter.getNextFor(partPresenter2);
        Assert.assertNotNull(result);
        Assert.assertEquals(partPresenter3, result);
    }

    @Test
    public void shouldReturnFirstPart() {
        presenter.addPart(partPresenter1);
        presenter.addPart(partPresenter2);
        presenter.addPart(partPresenter3);
        EditorPartPresenter result = presenter.getNextFor(partPresenter3);
        Assert.assertNotNull(result);
        Assert.assertEquals(partPresenter1, result);
    }

    @Test
    public void shouldReturnPreviousPart() {
        presenter.addPart(partPresenter1);
        presenter.addPart(partPresenter2);
        presenter.addPart(partPresenter3);
        EditorPartPresenter result = presenter.getPreviousFor(partPresenter2);
        Assert.assertNotNull(result);
        Assert.assertEquals(partPresenter1, result);
    }

    @Test
    public void shouldReturnLastPart() {
        presenter.addPart(partPresenter1);
        presenter.addPart(partPresenter2);
        presenter.addPart(partPresenter3);
        EditorPartPresenter result = presenter.getPreviousFor(partPresenter1);
        Assert.assertNotNull(result);
        Assert.assertEquals(partPresenter3, result);
    }

    @Test
    public void tabShouldBeReturnedByPath() throws Exception {
        Path path = new Path(EditorPartStackPresenterTest.SOME_TEXT);
        Mockito.when(editorTab1.getFile()).thenReturn(file1);
        Mockito.when(file1.getLocation()).thenReturn(path);
        presenter.addPart(partPresenter1);
        Assert.assertEquals(editorTab1, presenter.getTabByPath(path));
    }

    @Test
    public void shouldAddHandlers() throws Exception {
        presenter.addPart(partPresenter1);
        Mockito.verify(eventBus, Mockito.times(2)).addHandler(((Event.Type<EditorPartStackPresenter>) (ArgumentMatchers.anyObject())), ArgumentMatchers.eq(presenter));
    }

    @Test
    public void shouldNotAddHandlersWhenHandlersAlreadyExist() throws Exception {
        presenter.addPart(partPresenter1);
        Mockito.reset(eventBus);
        presenter.addPart(partPresenter2);
        Mockito.verify(eventBus, Mockito.never()).addHandler(((Event.Type<EditorPartStackPresenter>) (ArgumentMatchers.anyObject())), ArgumentMatchers.eq(presenter));
    }

    @Test
    public void shouldRemoveHandlersWhenPartsIsAbsent() throws Exception {
        presenter.addPart(partPresenter1);
        Mockito.reset(handlerRegistration);
        presenter.removePart(partPresenter1);
        Mockito.verify(handlerRegistration, Mockito.times(2)).removeHandler();
    }

    @Test
    public void shouldAvoidNPEWhenHandlersAlreadyRemoved() throws Exception {
        presenter.addPart(partPresenter1);
        presenter.removePart(partPresenter1);
        Mockito.reset(handlerRegistration);
        presenter.removePart(partPresenter2);
        Mockito.verify(handlerRegistration, Mockito.never()).removeHandler();
    }

    @Test
    public void openPreviousActivePartTest() {
        presenter.addPart(partPresenter1);
        presenter.openPreviousActivePart();
        Mockito.verify(view).selectTab(ArgumentMatchers.eq(partPresenter1));
    }

    @Test
    public void onTabItemClickedTest() {
        TabItem tabItem = Mockito.mock(TabItem.class);
        Mockito.when(editorPaneTabMenuItem.getData()).thenReturn(tabItem);
        presenter.paneMenuTabItemHandler.onItemClicked(editorPaneTabMenuItem);
        Mockito.verify(view).selectTab(((PartPresenter) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void onActionClickedTest() {
        Action action = Mockito.mock(BaseAction.class);
        Mockito.when(editorPaneActionMenuItem.getData()).thenReturn(action);
        presenter.addPart(partPresenter1);
        presenter.setActivePart(partPresenter1);
        presenter.paneMenuActionItemHandler.onItemClicked(editorPaneActionMenuItem);
        Mockito.verify(presentation).putClientProperty(ArgumentMatchers.eq(CURRENT_PANE_PROP), ArgumentMatchers.eq(presenter));
        Mockito.verify(presentation).putClientProperty(ArgumentMatchers.eq(CURRENT_TAB_PROP), ArgumentMatchers.eq(editorTab1));
        Mockito.verify(presentation).putClientProperty(ArgumentMatchers.eq(CURRENT_FILE_PROP), ArgumentMatchers.eq(file1));
    }

    @Test
    public void onItemCloseTest() {
        Mockito.when(editorPaneTabMenuItem.getData()).thenReturn(editorTab1);
        presenter.paneMenuTabItemHandler.onCloseButtonClicked(editorPaneTabMenuItem);
        Mockito.verify(editorAgent).closeEditor(ArgumentMatchers.nullable(EditorPartPresenter.class));
    }
}

