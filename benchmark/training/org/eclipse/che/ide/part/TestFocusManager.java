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


import PartStackPresenter.PartStackEventHandler;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.PartStackView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Testing {@link FocusManager} functionality.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
// @Test
// public void shoudFireEventOnChangePartStack() {
// // create Part Agent
// agent = new FocusManager(eventBus);
// 
// PartStack partStack = mock(PartStackPresenter.class);
// agent.setActivePartStack(partStack); // focus requested
// 
// // verify New Event generated
// verify(eventBus).fireEvent(any(ActivePartChangedEvent.class));
// }
@RunWith(MockitoJUnitRunner.class)
public class TestFocusManager {
    @Mock
    EventBus eventBus;

    @Mock
    PartStackUIResources resources;

    @InjectMocks
    FocusManager agent;

    @Mock
    PartStackEventHandler handler;

    @Mock
    PartStackView view;

    @InjectMocks
    PartStackPresenter stack;

    // @Test
    // public void shouldDropFocusFromPrevStack() {
    // PartStackPresenter partStack = mock(PartStackPresenter.class);
    // PartStackPresenter partStack2 = mock(PartStackPresenter.class);
    // 
    // agent.setActivePartStack(partStack);
    // reset(partStack);
    // 
    // agent.setActivePartStack(partStack2);
    // verify(partStack).setFocus(eq(false));
    // verify(partStack2).setFocus(eq(true));
    // }
    // @Test
    // public void shouldSetActivePartonStackAndSetFocus() {
    // PartStackPresenter partStack = mock(PartStackPresenter.class);
    // agent.setActivePartStack(partStack);
    // 
    // verify(partStack).setFocus(eq(true));
    // }
    @Test
    public void shoudFireEventOnChangePart() {
        // PartPresenter part = mock(PartPresenter.class);
        // create Part Agent
        // agent = new FocusManager(eventBus);
        // agent.activePartChanged(part);
        // verify Event fired
        // verify(eventBus).fireEvent(any(ActivePartChangedEvent.class));
    }
}

