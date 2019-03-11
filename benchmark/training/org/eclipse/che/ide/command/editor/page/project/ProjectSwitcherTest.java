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
package org.eclipse.che.ide.command.editor.page.project;


import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


/**
 * Tests for {@link ProjectSwitcher}.
 */
@RunWith(GwtMockitoTestRunner.class)
public class ProjectSwitcherTest {
    private static final String PROJECT_NAME = "p1";

    private ProjectSwitcher switcher;

    @Test
    public void shouldSetLabel() throws Exception {
        Mockito.verify(switcher.label).setText(ProjectSwitcherTest.PROJECT_NAME);
    }

    @Test
    public void shouldReturnValue() throws Exception {
        switcher.getValue();
        Mockito.verify(switcher.switcher).getValue();
    }

    @Test
    public void shouldSetValue() throws Exception {
        switcher.setValue(true);
        Mockito.verify(switcher.switcher).setValue(Boolean.TRUE);
    }

    @Test
    public void shouldSetValueAndFireEvents() throws Exception {
        switcher.setValue(true, true);
        Mockito.verify(switcher.switcher).setValue(Boolean.TRUE, true);
    }

    @Test
    public void shouldAddValueChangeHandler() throws Exception {
        ValueChangeHandler valueChangeHandler = Mockito.mock(ValueChangeHandler.class);
        switcher.addValueChangeHandler(valueChangeHandler);
        Mockito.verify(switcher.switcher).addValueChangeHandler(valueChangeHandler);
    }
}

