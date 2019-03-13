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
package org.eclipse.che.ide.processes;


import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.machine.MachineResources;
import org.eclipse.che.ide.processes.panel.ProcessesPanelPresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Roman Nikitenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class NewTerminalActionTest {
    @Mock
    private ProcessesPanelPresenter processesPanelPresenter;

    @Mock
    private CoreLocalizationConstant locale;

    @Mock
    private MachineResources resources;

    @Mock
    private EventBus eventBus;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ActionEvent actionEvent;

    @InjectMocks
    private NewTerminalAction action;

    @Test
    public void constructorShouldBeVerified() {
        Mockito.verify(locale).newTerminal();
        Mockito.verify(locale).newTerminalDescription();
    }

    @Test
    public void actionShouldBePerformed() throws Exception {
        action.actionPerformed(actionEvent);
        Mockito.verify(processesPanelPresenter).newTerminal(ArgumentMatchers.any(), true);
    }
}

