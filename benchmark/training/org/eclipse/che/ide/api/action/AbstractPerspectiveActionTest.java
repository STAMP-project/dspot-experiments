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
package org.eclipse.che.ide.api.action;


import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotSupportedException;
import org.eclipse.che.ide.api.parts.PerspectiveManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.vectomatic.dom.svg.ui.SVGResource;


/**
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class AbstractPerspectiveActionTest {
    private static final String SOME_TEXT = "someText";

    @Mock
    private PerspectiveManager manager;

    @Mock
    private ActionEvent event;

    private AbstractPerspectiveActionTest.DummyAction dummyAction;

    @Test
    public void actionShouldBePerformed() {
        Presentation presentation = new Presentation();
        Mockito.when(event.getPresentation()).thenReturn(presentation);
        Mockito.when(manager.getPerspectiveId()).thenReturn("123");
        dummyAction.update(event);
        Mockito.verify(event).getPresentation();
        Mockito.verify(manager).getPerspectiveId();
    }

    private class DummyAction extends AbstractPerspectiveAction {
        public DummyAction(@NotNull
        List<String> activePerspectives, @NotNull
        String tooltip, @NotNull
        String description, @NotNull
        SVGResource icon) {
            super(activePerspectives, tooltip, description, icon);
            perspectiveManager = () -> AbstractPerspectiveActionTest.this.manager;
            appContext = () -> null;
        }

        @Override
        public void updateInPerspective(@NotNull
        ActionEvent event) {
            throw new NotSupportedException("Method isn't supported in current mode...");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new NotSupportedException("Method isn't supported in current mode...");
        }
    }
}

