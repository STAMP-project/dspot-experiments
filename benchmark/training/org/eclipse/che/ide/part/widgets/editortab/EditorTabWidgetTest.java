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
package org.eclipse.che.ide.part.widgets.editortab;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.parts.EditorPartStack;
import org.eclipse.che.ide.api.parts.EditorTab.ActionDelegate;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.eclipse.che.ide.part.editor.EditorTabContextMenuFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;


/**
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class EditorTabWidgetTest {
    private static final String SOME_TEXT = "someText";

    // constructor mocks
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PartStackUIResources resources;

    @Mock
    private SVGResource icon;

    @Mock
    private SVGImage iconImage;

    @Mock
    private EditorPartPresenter editorPartPresenter;

    @Mock
    private EditorPartStack editorPartStack;

    // additional mocks
    @Mock
    private Element element;

    @Mock
    private OMSVGSVGElement svg;

    @Mock
    private ActionDelegate delegate;

    @Mock
    private ClickEvent event;

    @Mock
    private VirtualFile file;

    @Mock
    private EditorTabContextMenuFactory editorTabContextMenuFactory;

    @Mock
    private EventBus eventBus;

    @Mock
    private EditorAgent editorAgent;

    @Mock
    private EditorInput editorInput;

    private EditorTabWidget tab;

    @Test
    public void titleShouldBeReturned() {
        tab.getTitle();
        Mockito.verify(tab.title).getText();
    }

    @Test
    public void errorMarkShouldBeSet() {
        Mockito.when(resources.partStackCss().lineError()).thenReturn(EditorTabWidgetTest.SOME_TEXT);
        tab.setErrorMark(true);
        Mockito.verify(resources.partStackCss()).lineError();
        Mockito.verify(tab.title).addStyleName(EditorTabWidgetTest.SOME_TEXT);
    }

    @Test
    public void errorMarkShouldNotBeSet() {
        Mockito.when(resources.partStackCss().lineError()).thenReturn(EditorTabWidgetTest.SOME_TEXT);
        tab.setErrorMark(false);
        Mockito.verify(resources.partStackCss()).lineError();
        Mockito.verify(tab.title).removeStyleName(EditorTabWidgetTest.SOME_TEXT);
    }

    @Test
    public void warningMarkShouldBeSet() {
        Mockito.when(resources.partStackCss().lineWarning()).thenReturn(EditorTabWidgetTest.SOME_TEXT);
        tab.setWarningMark(true);
        Mockito.verify(resources.partStackCss()).lineWarning();
        Mockito.verify(tab.title).addStyleName(EditorTabWidgetTest.SOME_TEXT);
    }

    @Test
    public void warningMarkShouldNotBeSet() {
        Mockito.when(resources.partStackCss().lineWarning()).thenReturn(EditorTabWidgetTest.SOME_TEXT);
        tab.setWarningMark(false);
        Mockito.verify(resources.partStackCss()).lineWarning();
        Mockito.verify(tab.title).removeStyleName(EditorTabWidgetTest.SOME_TEXT);
    }

    @Test
    public void onTabShouldBeClicked() {
        tab.onClick(event);
        Mockito.verify(delegate).onTabClicked(tab);
    }

    @Test
    public void tabIconShouldBeUpdatedWhenMediaTypeChanged() {
        EditorInput editorInput = Mockito.mock(EditorInput.class);
        FileType fileType = Mockito.mock(FileType.class);
        Mockito.when(editorPartPresenter.getEditorInput()).thenReturn(editorInput);
        Mockito.when(fileType.getImage()).thenReturn(icon);
        Mockito.when(editorInput.getFile()).thenReturn(file);
        tab.update(editorPartPresenter);
        Mockito.verify(editorPartPresenter, Mockito.times(2)).getEditorInput();
        Mockito.verify(editorPartPresenter, Mockito.times(2)).getTitleImage();
        Mockito.verify(tab.iconPanel).setWidget(ArgumentMatchers.<SVGImage>anyObject());
    }

    @Test
    public void virtualFileShouldBeUpdated() throws Exception {
        EditorInput editorInput = Mockito.mock(EditorInput.class);
        FileType fileType = Mockito.mock(FileType.class);
        VirtualFile newFile = Mockito.mock(VirtualFile.class);
        Mockito.when(editorPartPresenter.getEditorInput()).thenReturn(editorInput);
        Mockito.when(fileType.getImage()).thenReturn(icon);
        Mockito.when(editorInput.getFile()).thenReturn(newFile);
        Assert.assertNotEquals(tab.getFile(), newFile);
        tab.update(editorPartPresenter);
        Assert.assertEquals(tab.getFile(), newFile);
    }

    @Test
    public void tabShouldBeReturned() throws Exception {
        tab.setFile(file);
        Assert.assertEquals(file, tab.getFile());
    }
}

