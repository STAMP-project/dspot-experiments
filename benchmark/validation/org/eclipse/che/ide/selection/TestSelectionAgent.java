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
package org.eclipse.che.ide.selection;


import SelectionChangedEvent.TYPE;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.eclipse.che.ide.api.parts.AbstractPartPresenter;
import org.eclipse.che.ide.api.parts.ActivePartChangedEvent;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionChangedEvent;
import org.eclipse.che.ide.api.selection.SelectionChangedHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.vectomatic.dom.svg.ui.SVGResource;


/**
 * Test covers {@link SelectionAgentImpl} functionality.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSelectionAgent {
    private EventBus eventBus = new SimpleEventBus();

    private SelectionAgentImpl agent = new SelectionAgentImpl(eventBus);

    @Mock
    private PartPresenter part;

    @Mock
    private Selection selection;

    /**
     * Check proper Selection returned when part changed
     */
    @Test
    public void shouldChangeSelectionAfterPartGetsActivated() {
        Mockito.when(part.getSelection()).thenReturn(selection);
        // fire event, for agent to get information about active part
        eventBus.fireEvent(new ActivePartChangedEvent(part));
        Assert.assertEquals("Agent should return proper Selection", selection, agent.getSelection());
    }

    /**
     * Event should be fired, when active part changed
     */
    @Test
    public void shouldFireEventWhenPartChanged() {
        Mockito.when(part.getSelection()).thenReturn(selection);
        SelectionChangedHandler handler = Mockito.mock(SelectionChangedHandler.class);
        eventBus.addHandler(TYPE, handler);
        // fire event, for agent to get information about active part
        eventBus.fireEvent(new ActivePartChangedEvent(part));
        Mockito.verify(handler).onSelectionChanged(((SelectionChangedEvent) (ArgumentMatchers.any())));
    }

    /**
     * If selection chang in active part, Selection Agent should fire an Event
     */
    @Test
    public void shouldFireEventWhenSelectionInActivePartChanged() {
        AbstractPartPresenter part = new AbstractPartPresenter() {
            @Override
            public void go(AcceptsOneWidget container) {
            }

            @Override
            public String getTitleToolTip() {
                return null;
            }

            @Override
            public SVGResource getTitleImage() {
                return null;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public IsWidget getView() {
                return null;
            }
        };
        // fire event, for agent to get information about active part
        eventBus.fireEvent(new ActivePartChangedEvent(part));
        SelectionChangedHandler handler = Mockito.mock(SelectionChangedHandler.class);
        eventBus.addHandler(TYPE, handler);
        part.setSelection(Mockito.mock(Selection.class));
        Mockito.verify(handler).onSelectionChanged(((SelectionChangedEvent) (ArgumentMatchers.any())));
    }

    /**
     * If selection chang in non-active part, no events should be fired by Selection Agent
     */
    @Test
    public void shouldNOTFireEventWhenSelectionInNONActivePartChanged() {
        AbstractPartPresenter firstPart = new AbstractPartPresenter() {
            @Override
            public void go(AcceptsOneWidget container) {
            }

            @Override
            public String getTitleToolTip() {
                return null;
            }

            @Override
            public SVGResource getTitleImage() {
                return null;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public IsWidget getView() {
                return null;
            }
        };
        // fire event, for agent to get information about active part
        eventBus.fireEvent(new ActivePartChangedEvent(firstPart));
        // change part
        eventBus.fireEvent(new ActivePartChangedEvent(Mockito.mock(PartPresenter.class)));
        SelectionChangedHandler handler = Mockito.mock(SelectionChangedHandler.class);
        eventBus.addHandler(TYPE, handler);
        // call setSelection on the first Part.
        firstPart.setSelection(Mockito.mock(Selection.class));
        Mockito.verifyZeroInteractions(handler);
    }
}

