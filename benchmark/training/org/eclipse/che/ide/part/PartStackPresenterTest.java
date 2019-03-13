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


import EditorPartPresenter.PROP_DIRTY;
import PartPresenter.TITLE_PROPERTY;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import java.util.List;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.events.EditorDirtyStateChangedEvent;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.api.parts.PropertyListener;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.menu.PartMenu;
import org.eclipse.che.ide.part.PartStackPresenter.PartStackEventHandler;
import org.eclipse.che.ide.part.widgets.TabItemFactory;
import org.eclipse.che.ide.part.widgets.partbutton.PartButton;
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
 * @author Roman Nikitenko
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class PartStackPresenterTest {
    private static final String SOME_TEXT = "someText";

    private static final double PART_SIZE = 170;

    // constructor mocks
    @Mock
    private EventBus eventBus;

    @Mock
    private PartMenu partMenu;

    @Mock
    private WorkBenchPartController workBenchPartController;

    @Mock
    private PartsComparator partsComparator;

    @Mock
    private PartStackEventHandler partStackHandler;

    @Mock
    private TabItemFactory tabItemFactory;

    @Mock
    private PartStackView view;

    @Mock
    private PropertyListener propertyListener;

    // additional mocks
    @Mock
    private AcceptsOneWidget container;

    @Mock
    private PartPresenter partPresenter;

    @Mock
    private Constraints constraints;

    @Mock
    private BasePresenter basePresenter;

    @Mock
    private SVGResource resource;

    @Mock
    private PartButton partButton;

    @Mock
    private EditorPartPresenter editorPartPresenter;

    @Captor
    public ArgumentCaptor<PropertyListener> listenerCaptor;

    private PartStackPresenter presenter;

    @Test
    public void partShouldBeUpdated() {
        presenter.addPart(partPresenter);
        Mockito.verify(partPresenter).addPropertyListener(listenerCaptor.capture());
        listenerCaptor.getValue().propertyChanged(partPresenter, TITLE_PROPERTY);
        Mockito.verify(view).updateTabItem(partPresenter);
    }

    @Test
    public void dirtyStateChangedEventShouldBeFired() {
        presenter.addPart(partPresenter);
        Mockito.verify(partPresenter).addPropertyListener(listenerCaptor.capture());
        listenerCaptor.getValue().propertyChanged(editorPartPresenter, PROP_DIRTY);
        Mockito.verify(eventBus).fireEvent(ArgumentMatchers.<EditorDirtyStateChangedEvent>anyObject());
    }

    @Test
    public void goShouldBeActioned() {
        presenter.go(container);
        Mockito.verify(container).setWidget(view);
    }

    @Test
    public void partShouldBeAddedWithoutConstraints() {
        presenter.addPart(basePresenter);
        Mockito.verify(basePresenter).setPartStack(presenter);
        Mockito.verify(tabItemFactory).createPartButton(PartStackPresenterTest.SOME_TEXT);
        Mockito.verify(partButton).setTooltip(PartStackPresenterTest.SOME_TEXT);
        Mockito.verify(partButton).setIcon(resource);
        Mockito.verify(partButton).setDelegate(presenter);
        Mockito.verify(view).addTab(partButton, basePresenter);
        Mockito.verify(view).setTabPositions(ArgumentMatchers.<List<PartPresenter>>anyObject());
        Mockito.verify(partStackHandler).onRequestFocus(presenter);
    }

    @Test
    public void partShouldBeSelectedIfItIsAddedTwice() {
        presenter.addPart(partPresenter);
        Mockito.reset(view);
        presenter.addPart(partPresenter);
        Mockito.verify(view).selectTab(partPresenter);
    }

    @Test
    public void partShouldBeContained() {
        presenter.addPart(basePresenter);
        boolean isContained = presenter.containsPart(basePresenter);
        Assert.assertThat(isContained, CoreMatchers.is(true));
    }

    @Test
    public void activePartShouldBeReturned() {
        presenter.addPart(partPresenter);
        presenter.setActivePart(partPresenter);
        Assert.assertThat(presenter.getActivePart(), CoreMatchers.sameInstance(partPresenter));
    }

    @Test
    public void focusShouldBeSet() {
        presenter.setFocus(true);
        Mockito.verify(view).setFocus(true);
    }

    @Test
    public void activePartShouldNotBeSet() {
        presenter.setActivePart(partPresenter);
        Assert.assertThat(presenter.getActivePart(), CoreMatchers.nullValue());
    }

    @Test
    public void partShouldBeHidden() {
        presenter.addPart(partPresenter);
        presenter.setActivePart(partPresenter);
        presenter.hide();
        Mockito.verify(workBenchPartController).getSize();
        Mockito.verify(workBenchPartController).setSize(0);
        Assert.assertThat(presenter.getActivePart(), CoreMatchers.nullValue());
    }

    @Test
    public void partShouldBeRemoved() {
        presenter.addPart(partPresenter);
        presenter.removePart(partPresenter);
        Mockito.verify(view).removeTab(partPresenter);
    }

    @Test
    public void previousActivePartShouldNotBeDisplayedWhenActivePartIsNull() {
        presenter.openPreviousActivePart();
        Mockito.verify(view, Mockito.never()).selectTab(partPresenter);
    }

    @Test
    public void previousPartShouldBeOpened() {
        presenter.addPart(partPresenter);
        presenter.setActivePart(partPresenter);
        Mockito.reset(view);
        presenter.openPreviousActivePart();
        Mockito.verify(view).selectTab(partPresenter);
    }

    @Test
    public void requestShouldBeOnFocus() {
        presenter.onRequestFocus();
        Mockito.verify(partStackHandler).onRequestFocus(presenter);
    }

    @Test
    public void onTabShouldBeClicked() {
        Mockito.reset(workBenchPartController);
        presenter.addPart(partPresenter);
        presenter.onTabClicked(partButton);
        Mockito.verify(workBenchPartController).setHidden(false);
        Mockito.verify(view).selectTab(partPresenter);
    }

    @Test
    public void shouldSetCurrentSizeForPart() {
        Mockito.reset(workBenchPartController);
        presenter.addPart(partPresenter);
        Mockito.when(workBenchPartController.getSize()).thenReturn(0.0);
        presenter.onTabClicked(partButton);
        Mockito.verify(workBenchPartController).setHidden(false);
        Mockito.verify(view).selectTab(partPresenter);
    }

    @Test
    public void shouldSetInitialSizeForPart() {
        Mockito.reset(workBenchPartController);
        presenter.addPart(partPresenter);
        Mockito.when(workBenchPartController.getSize()).thenReturn(PartStackPresenterTest.PART_SIZE);
        presenter.onTabClicked(partButton);
        Mockito.verify(workBenchPartController).setHidden(false);
        Mockito.verify(view).selectTab(partPresenter);
    }
}

