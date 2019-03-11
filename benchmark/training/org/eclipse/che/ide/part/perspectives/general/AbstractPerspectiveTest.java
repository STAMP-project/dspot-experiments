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
package org.eclipse.che.ide.part.perspectives.general;


import ActivePartChangedEvent.TYPE;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import java.util.Arrays;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotSupportedException;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.editor.AbstractEditorPresenter;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStack;
import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.part.PartStackPresenter;
import org.eclipse.che.ide.part.PartStackPresenterFactory;
import org.eclipse.che.ide.part.PartStackViewFactory;
import org.eclipse.che.ide.part.WorkBenchControllerFactory;
import org.eclipse.che.ide.part.WorkBenchPartController;
import org.eclipse.che.providers.DynaProvider;
import org.junit.Assert;
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
public class AbstractPerspectiveTest {
    private static final String SOME_TEXT = "someText";

    // constructor mocks
    @Mock
    private PerspectiveViewImpl view;

    @Mock
    private PartStackPresenterFactory stackPresenterFactory;

    @Mock
    private PartStackViewFactory partStackViewFactory;

    @Mock
    private WorkBenchControllerFactory controllerFactory;

    @Mock
    private EventBus eventBus;

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
    private PartStackView partStackView;

    @Mock
    private PartStackPresenter extraPartStackPresenter;

    @Mock
    private PartStackPresenter partStackPresenter;

    @Mock
    private WorkBenchPartController workBenchController;

    @Mock
    private PartPresenter partPresenter;

    @Mock
    private Constraints constraints;

    @Mock
    private PartPresenter navigationPart;

    @Mock
    private PartPresenter activePart;

    @Mock
    private AbstractEditorPresenter editorPart;

    @Mock
    private DynaProvider dynaProvider;

    private AbstractPerspective perspective;

    @Test
    public void activePartShouldBeOpened() {
        perspective.openActivePart(INFORMATION);
        Mockito.verify(partStackPresenter).openPreviousActivePart();
    }

    @Test
    public void constructorShouldBeVerified() {
        Mockito.verify(view).getLeftPanel();
        Mockito.verify(view).getBottomPanel();
        Mockito.verify(view).getRightPanel();
        Mockito.verify(partStackViewFactory, Mockito.times(3)).create(panel);
        Mockito.verify(view, Mockito.times(3)).getSplitPanel();
        Mockito.verify(view).getNavigationPanel();
        Mockito.verify(controllerFactory, Mockito.times(2)).createController(layoutPanel, simplePanel);
        Mockito.verify(controllerFactory).createController(layoutPanel, simpleLayoutPanel);
        Mockito.verify(stackPresenterFactory, Mockito.times(3)).create(partStackView, workBenchController);
        Mockito.verify(eventBus).addHandler(TYPE, perspective);
    }

    @Test
    public void partShouldBeRemoved() {
        Mockito.when(partStackPresenter.containsPart(partPresenter)).thenReturn(true);
        perspective.removePart(partPresenter);
        Mockito.verify(partStackPresenter).removePart(partPresenter);
    }

    @Test
    public void perspectiveStateShouldBeStored() {
        perspective.onActivePartChanged(new org.eclipse.che.ide.api.parts.ActivePartChangedEvent(editorPart));
        perspective.storeState();
        Mockito.verify(editorPart).storeState();
    }

    @Test
    public void perspectiveStateShouldBeRestored() {
        perspective.onActivePartChanged(new org.eclipse.che.ide.api.parts.ActivePartChangedEvent(editorPart));
        perspective.storeState();
        perspective.restoreState();
        Mockito.verify(editorPart).restoreState();
    }

    @Test
    public void partShouldBeHided() {
        Mockito.when(partStackPresenter.containsPart(partPresenter)).thenReturn(true);
        perspective.hidePart(partPresenter);
        Mockito.verify(partStackPresenter).hide();
    }

    @Test
    public void partShouldBeMaximized() {
        perspective.onMaximize(partStackPresenter);
        Mockito.verify(partStackPresenter).maximize();
    }

    @Test
    public void partShouldBeMinimized() {
        perspective.onMaximize(extraPartStackPresenter);
        Mockito.verify(partStackPresenter, Mockito.times(3)).minimize();
        Mockito.verify(extraPartStackPresenter).maximize();
    }

    @Test
    public void activePartShouldBeSet() {
        Mockito.when(partStackPresenter.containsPart(partPresenter)).thenReturn(true);
        perspective.setActivePart(partPresenter);
        Mockito.verify(partStackPresenter).setActivePart(partPresenter);
    }

    @Test
    public void activePartShouldBeSetWithType() {
        perspective.setActivePart(partPresenter, INFORMATION);
        Mockito.verify(partStackPresenter).setActivePart(partPresenter);
    }

    @Test
    public void nullShouldBeReturnedWhenPartIsNotFound() {
        PartStack partStack = perspective.findPartStackByPart(partPresenter);
        Assert.assertSame(partStack, null);
    }

    @Test
    public void nullShouldBeFound() {
        Mockito.when(partStackPresenter.containsPart(partPresenter)).thenReturn(true);
        PartStack partStack = perspective.findPartStackByPart(partPresenter);
        Assert.assertSame(partStack, partStackPresenter);
    }

    @Test
    public void partShouldBeAddedWithoutConstraints() {
        perspective.addPart(partPresenter, INFORMATION);
        Mockito.verify(partStackPresenter).addPart(partPresenter, null);
    }

    @Test
    public void partShouldBeAddedWithConstraints() {
        perspective.addPart(partPresenter, INFORMATION, constraints);
        Mockito.verify(partStackPresenter).addPart(partPresenter, constraints);
    }

    @Test
    public void partStackShouldBeReturned() {
        perspective.addPart(partPresenter, INFORMATION);
        PartStack partStack = perspective.getPartStack(INFORMATION);
        Assert.assertSame(partStack, partStackPresenter);
    }

    @Test
    public void partShouldBeAddedWithRules() {
        Mockito.when(partPresenter.getRules()).thenReturn(Arrays.asList(AbstractPerspectiveTest.SOME_TEXT));
        perspective.addPart(partPresenter, INFORMATION);
        Mockito.verify(partStackPresenter).addPart(partPresenter, null);
    }

    public static class DummyPerspective extends AbstractPerspective {
        public DummyPerspective(@NotNull
        PerspectiveViewImpl view, @NotNull
        PartStackPresenterFactory stackPresenterFactory, @NotNull
        PartStackViewFactory partViewFactory, @NotNull
        WorkBenchControllerFactory controllerFactory, @NotNull
        EventBus eventBus, PartStackPresenter extraPartStackPresenter, PartStackPresenter editingPartStackPresenter, DynaProvider dynaProvider) {
            super(AbstractPerspectiveTest.SOME_TEXT, view, stackPresenterFactory, partViewFactory, controllerFactory, eventBus, dynaProvider);
            if (extraPartStackPresenter != null) {
                partStacks.put(NAVIGATION, extraPartStackPresenter);
            }
            if (editingPartStackPresenter != null) {
                partStacks.put(EDITING, editingPartStackPresenter);
            }
        }

        @Override
        public String getPerspectiveId() {
            return AbstractPerspectiveTest.SOME_TEXT;
        }

        @Override
        public String getPerspectiveName() {
            return "Dummy";
        }

        @Override
        public void go(@NotNull
        AcceptsOneWidget container) {
            throw new NotSupportedException("This method will be tested in the class which extends AbstractPerspective");
        }
    }
}

